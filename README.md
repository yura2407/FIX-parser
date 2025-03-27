# FIX-parser
Binary FIX parser for Java
## Overview
This is a binary FIX parser for Java. It also provides a custom message encoder for better efficiency
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
The encoder
|Benchmark                                                             | Mode    Cnt        Score         Error   Units
-----------------------------------------------------------------------------------------------------------------------
| FixEncoderBenchmark.customBinaryEncodingMessage                      | thrpt  |   5   319974.582 �  279839.226   ops/s
| FixEncoderBenchmark.customBinaryEncodingMessage:gc.alloc.rate        | thrpt  |   5      221.812 �     193.988  MB/sec
| FixEncoderBenchmark.customBinaryEncodingMessage:gc.alloc.rate.norm   | thrpt  |   5      726.997 �       0.857    B/op
| FixEncoderBenchmark.customBinaryEncodingMessage:gc.count             | thrpt  |   5      148.000                counts
| FixEncoderBenchmark.customBinaryEncodingMessage:gc.time              | thrpt  |   5       87.000                    ms
| FixEncoderBenchmark.defaultBinaryEncodingMessage                     | thrpt  |   5  9382197.213 � 3106011.643   ops/s
| FixEncoderBenchmark.defaultBinaryEncodingMessage:gc.alloc.rate       | thrpt  |   5     1646.204 �     544.793  MB/sec
| FixEncoderBenchmark.defaultBinaryEncodingMessage:gc.alloc.rate.norm  | thrpt  |   5      183.999 �       0.019    B/op
| FixEncoderBenchmark.defaultBinaryEncodingMessage:gc.count            | thrpt  |   5      599.000                counts
| FixEncoderBenchmark.defaultBinaryEncodingMessage:gc.time             | thrpt  | 5      555.000                    ms
## Parser
Parser is more flexible than the encoder. It can parse both customly (using the encoder above)
and ASCII encoded binary FIX messages.


