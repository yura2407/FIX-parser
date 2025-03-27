package org.fixParser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String[] MESSAGES = new String[]{
            "8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000149=TESTBUY1\u000152=20180920-18:14:19.508\u000156=TESTSELL1\u000111=636730640278898634\u000115=USD\u000121=2\u000138=7000\u000140=1\u000154=1\u000155=MSFT\u000160=20180920-18:14:19.492\u000110=092\u0001",
            "8=FIX.4.49=28935=834=109049=TESTSELL152=20180920-18:23:53.67156=TESTBUY16=113.3511=63673064027889863414=3500.000000000015=USD17=2063673064633531000021=231=113.3532=350037=2063673064633531000038=700039=140=154=155=MSFT60=20180920-18:23:53.531150=F151=3500453=1448=BRK2447=D452=110=151",
            "8=FIX.4.4\u00019=75\u000135=A\u000134=1092\u000149=TESTBUY1\u000152=20180920-18:24:59.643\u000156=TESTSELL1\u000198=0\u0001108=60\u000110=178\u0001",
            "8=FIX.4.4\u00019=63\u000135=5\u000134=1091\u000149=TESTBUY1\u000152=20180920-18:24:58.675\u000156=TESTSELL1\u000110=138\u0001",
            "8=FIX.4.2\u00019=163\u000135=D\u000134=972\u000149=TESTBUY3\u000152=20190206-16:25:10.403\u000156=TESTSELL3\u000111=141636850670842269979\u000121=2\u000138=100\u000140=1\u000154=1\u000155=AAPL\u000160=20190206-16:25:08.968\u0001207=TO\u00016000=TEST1234\u000110=106\u0001",
            "8=FIX.4.2\u00019=271\u000135=8\u000134=974\u000149=TESTSELL3\u000152=20190206-16:26:09.059\u000156=TESTBUY3\u00016=174.51\u000111=141636850670842269979\u000114=100.0000000000\u000117=3636850671684357979\u000120=0\u000121=2\u000131=174.51\u000132=100.0000000000\u000137=1005448\u000138=100\u000139=2\u000140=1\u000154=1\u000155=AAPL\u000160=20190206-16:26:08.435\u0001150=2\u0001151=0.0000000000\u000110=194\u0001",
            "8=FIX.4.2\u00019=74\u000135=A\u000134=978\u000149=TESTSELL3\u000152=20190206-16:29:19.208\u000156=TESTBUY3\u000198=0\u0001108=60\u000110=137\u0001",
            "8=FIX.4.2\u00019=62\u000135=5\u000134=977\u000149=TESTSELL3\u000152=20190206-16:28:51.518\u000156=TESTBUY3\u000110=092\u0001"
    };
    public static void main(String[] args) {
        //Encode a FIX message to binary using Custom encoding provided by API
        byte[] encodedMessage = FixEncoder.encodeBinary(MESSAGES[3]);
        //Encode a FIX message to binary using ASCII encoding
        byte[] encodedMessageAscii = MESSAGES[3].getBytes(StandardCharsets.US_ASCII);
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
    }

}