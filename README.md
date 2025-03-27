# FIX-parser
Binary FIX parser and encoder (optionally) for Java
## Overview
This is a binary FIX parser for Java. It also provides a custom message encoder for better efficiency. It is up to you if you want to use the provided encoder (goes under CUSTOM) or the default ASCII one. But you need to ensure that the binary message was encoded by the same method as you supply when decoding (e.g.use Encoding.CUSTOM for messages encoded using FixEncoder from this API and Encoding.ASCII for messages encoded using String.getBytes(StandardCharsets.US_ASCII). **Verify your encoding before using parser**. This API supports any valid FIX message which uses only ASCII characters, custom tags with any integer tag number are allowed.
### Prerequisites
- Java 17 or higher
- Gradle 7.3 or higher
### Installation
Clone the repository:
```sh
git clone https://github.com/yura2407/FIX-parser.git
cd FIX-parser
./gradlew build
```
Or just open as a Project in your IDE and build using gradle
To use the FIX parser in your project, include the following dependency in your build.gradle.kts file:
```kotlin
dependencies {
    implementation(project(":FIX-parser"))
}
```
To run benchmarks, run src/jmh/benchmarks/BenchmarkRunner.main()
## Encoder
The encoder is a custom implementation of the FIX message encoder. 
It uses internal schema to encode the message in order to be able to parse it 
more efficiently later. Encoder is designed to be able to encode any valid FIX message
which follows the FIX protocol standard.
### Encoder usage
FixEncoder is static class with a single method encodeBinary(String message) which returns byte array
It is thread-safe and handles no state
```java
byte[] encodedMessage = 
        FixEncoder.encodeBinary("8=FIX.4.4\u00019=63\u000135=5\u000134=1091\u000149=TESTBUY1\u0001")
```
### Encoder message schema (reference only, not necessary for a user)
The encoder uses the following schema to encode the message:
1. The first four bytes is the number of tags in the message
2. Then each tag will occupy 8 bytes (4 for tag number and 4 for tag`s data offset)
3. Then all the data will be stored in the byte array separated by the SOH byte (1)
Example:
```java
String message = "8=FIX.4.4\u00019=148\u0001";
byte[] numberOfTagBytes = new byte[]{0, 0, 0, 16}; // 2 * 8
byte[] firstFieldNum = new byte[]{0, 0, 0, 8}; // 8
byte[] firstFieldOffset = new byte[]{0, 0, 0, 20}; // 20
byte[] secondFieldNum = new byte[]{0, 0, 0, 9}; // 9
byte[] secondFieldOffset = new byte[]{0, 0, 0, 28}; // 28
byte[] firstFieldData = new byte[]{70, 73, 88, 46, 52, 46, 52, 1}; // FIX.4.4SOH
byte[] secondFieldData = new byte[]{49, 52, 56, 1}; // 148SOH
```
### Encoder overhead
The encoder has some overhead compared to the standard string encoding (ASCII) which is
(8-1-length(tag in chars)) bytes for each tag and 4 bytes for the number of tags
E.g. for the message "8=FIX.4.4\u00019=148\u0001" the overhead is 4 + (8-1-1) + (8-1-1) = 10 bytes
Where does 8-1-length(tag in chars) come from?
We need 4 bytes for the tag number and 4 bytes for the tag`s data offset, but we do not
store '=' and you deduct length of tag in characters as it does not need to be stored
### Encoder performance
Encoder (customBinaryEncodingMessage) was benchmarked against default ASCII encoding (String.getBytes(StandardCharsets.US_ASCII)).
It has lower performance comparing to ASCII, but you get benefits of it on decoding stage. It is recommended to use Custom encoder when you encode message once but need to parse it many times (e.g internal communication within the system)
![image](https://github.com/user-attachments/assets/4e3a7134-0384-47d1-a4d6-43682eebaef5)
## Parser
Parser is very flexible. It can parse both customly (using the encoder above)
and ASCII encoded binary FIX messages. Encoding enum specifies mode and you must pass it to every call. Parser can handle messages with and without repetitive tags and you choose the function call depending on the existence of repetitive tags in your FIX message (if you are not sure, just use repetitive mode, but non-repetitve mode is more performant). You can specify tags you are interested in (recommended), or you will receive all the tags (not recommended in production mode, but can be helpful for testing/exploration). FixParser returns you a Map (more in usage) with all or interested tags and their values from a particular message. There is a possibility to supply your own HashMap with tags as keys in a function call to avoid extra allocation and reuse the existing map. If tag of interest is not present in a message, it`s value in result map will be empty.
### Parser usage
FixParser is a static class with no internal persistent state, it is thread-safe. **!!! If you supply your own HashMap, it's on you to ensure map's thread-safety !!!**
```java
//Encode a FIX message to binary using Custom encoding provided by API
byte[] encodedMessage = FixEncoder.encodeBinary("8=FIX.4.4\u00019=63\u000135=5\u000134=1091\u0001");
//Encode a FIX message to binary using ASCII encoding
byte[] encodedMessageAscii = "8=FIX.4.4\u00019=63\u000135=5\u000134=1091\u0001".getBytes(StandardCharsets.US_ASCII);
//Decode a binary FIX message without repetitive tags using Custom encoding provided by API and get a map with all fields
Map<Integer, String> allFieldsNonRepetitive = FixParser.parseBinaryNonRepetitive(encodedMessage, Encoding.CUSTOM);
//Decode a binary FIX message with repetitive tags using Custom encoding provided by API and get a map with all fields
Map<Integer, List<String>> allFieldsRepetitive = FixParser.parseBinaryRepetitive(encodedMessage, Encoding.CUSTOM);
//Decode a binary FIX message with repetitive tags using Custom encoding provided by API and get a map with specific tags
Map<Integer, List<String>> specificTagsRepetitive = FixParser.parseBinaryRepetitive(encodedMessage, Encoding.CUSTOM, 9, 35);
//Decode a binary FIX message without repetitive tags using ASCII encoding provided by API and get a map with specific tags
Map<Integer, String> specificTagsNonRepetitive = FixParser.parseBinaryNonRepetitive(encodedMessageAscii, Encoding.ASCII, 9, 35);
//Decode a binary FIX message with repetitive tags using Custom encoding provided by API with provided map of interesting tags
HashMap<Integer, List<String>> inputMap = new HashMap<>(
        Map.of(8, new ArrayList<>(List.of("random_val", "random_val2")),
                35, new ArrayList<>()
        )
);
//It will populate your map with the values of the tags you are interested in, old values will be erased
FixParser.parseBinaryRepetitive(encodedMessage, Encoding.CUSTOM, inputMap);
```
### Parser performance
![image](https://github.com/user-attachments/assets/f6029b08-2489-4f07-b1b6-5a9148fa3f69)
![image](https://github.com/user-attachments/assets/b90fdd09-27c0-4a07-b4cc-6d4412808e04)
Brief summary of this data:
1. Custom encoding gives higher throughput than ASCII for all operations. This difference is much more notable in desired use cases of getting only fields of interest (either repetitive or non-repetitive).
2. Providing HashMap reduces allocation twices with no significant effect on throughput, also it automatically limits tags of interest by the provided keys in a Map. This methos is strongly encourage and is perhaps the most efficient out of provided
Getting tags using String manipulation is inefficient and leads to massive allocations and lower throughput (just for reference, not part of API)
![image](https://github.com/user-attachments/assets/8a942f74-ce99-475c-81b6-00dcb25d6650)
## Important considerations
1. FixEncoder and FixParser do not currently provide any input validation on either binary or string
2. You need to take care of using supplying correct encoding to FixParser, otherwise, unexpected results/exceptions will happen, currently there is no validation of binary message matching encoding







