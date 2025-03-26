package org.fixParser;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FixParserTest {

    @Test
    void encodeDecodeNonRepetitiveTags() {
        String message = "8=FIX.4.4\u00019=148\u0001";
        byte[] customEncoded = FixEncoder.encodeBinary(message);
        byte[] asciiEncoded = message.getBytes(StandardCharsets.US_ASCII);
        Map<Integer, String> expected = Map.of(
                8, "FIX.4.4",
                9, "148"
        );
        Map<Integer, String> expectedWithDummyTag = Map.of(
                8, "FIX.4.4",
                9, "148",
                10, ""
        );
        assertEquals(expected, FixParser.parseBinaryNonRepetitive(customEncoded, Encoding.CUSTOM));
        assertEquals(expected, FixParser.parseBinaryNonRepetitive(asciiEncoded, Encoding.ASCII));
        assertEquals(expected, FixParser.parseBinaryNonRepetitive(customEncoded, Encoding.CUSTOM, 8,9));
        assertEquals(expected, FixParser.parseBinaryNonRepetitive(asciiEncoded, Encoding.ASCII, 8,9));
        assertEquals(expectedWithDummyTag, FixParser.parseBinaryNonRepetitive(customEncoded, Encoding.CUSTOM, 8,9,10));
        assertEquals(expectedWithDummyTag, FixParser.parseBinaryNonRepetitive(asciiEncoded, Encoding.ASCII, 8,9,10));
        assertEquals(
                "FIX.4.4",
                FixParser.parseBinaryNonRepetitive(customEncoded, Encoding.CUSTOM, 8).get(8)
        );
        assertEquals(
                "FIX.4.4",
                FixParser.parseBinaryNonRepetitive(asciiEncoded, Encoding.ASCII, 8).get(8)
        );
    }

    @Test
    void encodeDecodeRepetitiveTags() {
        String message = "8=FIX.4.4\u00019=148\u00019=158\u0001";
        byte[] customEncoded = FixEncoder.encodeBinary(message);
        byte[] asciiEncoded = message.getBytes(StandardCharsets.US_ASCII);
        Map<Integer, List<String>> expected = Map.of(
                8, List.of("FIX.4.4"),
                9, List.of("148", "158")
        );
        Map<Integer, List<String>> expectedWithDummyTag = Map.of(
                8, List.of("FIX.4.4"),
                9, List.of("148", "158"),
                10, List.of()
        );
        assertEquals(expected, FixParser.parseBinaryRepetitive(customEncoded, Encoding.CUSTOM));
        assertEquals(expected, FixParser.parseBinaryRepetitive(asciiEncoded, Encoding.ASCII));
        assertEquals(expected, FixParser.parseBinaryRepetitive(customEncoded, Encoding.CUSTOM, 8,9));
        assertEquals(expected, FixParser.parseBinaryRepetitive(asciiEncoded, Encoding.ASCII, 8,9));
        assertEquals(expectedWithDummyTag, FixParser.parseBinaryRepetitive(customEncoded, Encoding.CUSTOM, 8,9,10));
        assertEquals(expectedWithDummyTag, FixParser.parseBinaryRepetitive(asciiEncoded, Encoding.ASCII, 8,9,10));
        assertEquals(
                List.of("148", "158"),
                FixParser.parseBinaryRepetitive(customEncoded, Encoding.CUSTOM, 9).get(9)
        );
        assertEquals(
                List.of("148", "158"),
                FixParser.parseBinaryRepetitive(asciiEncoded, Encoding.ASCII, 9).get(9)
        );
    }
}