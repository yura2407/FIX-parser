package org.fixParser;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.fixParser.TestUtils.parseFixMessageNonRepetitive;
import static org.fixParser.TestUtils.parseFixMessageRepetitive;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FixParserTest {

    @Test
    void encodeDecodeNonRepetitiveTags() {
        String message = "8=FIX.4.4\u00019=148\u000135=A\u0001";
        byte[] customEncoded = FixEncoder.encodeBinary(message);
        byte[] asciiEncoded = message.getBytes(StandardCharsets.US_ASCII);
        Map<Integer, String> expected = parseFixMessageNonRepetitive(message);
        assertEquals(expected, FixParser.parseBinaryNonRepetitive(customEncoded, Encoding.CUSTOM));
        assertEquals(expected, FixParser.parseBinaryNonRepetitive(asciiEncoded, Encoding.ASCII));
        assertEquals(
                "FIX.4.4",
                FixParser.parseBinaryNonRepetitive(customEncoded, Encoding.CUSTOM, 8).get(8)
        );
        assertEquals(
                Map.of(
                        8, "FIX.4.4",
                        35, "A"
                ),
                FixParser.parseBinaryNonRepetitive(customEncoded, Encoding.CUSTOM, 8,35)
        );
        assertEquals(
                Map.of(
                        8, "FIX.4.4",
                        35, "A"
                ),
                FixParser.parseBinaryNonRepetitive(asciiEncoded, Encoding.ASCII, 8,35)
        );
        assertEquals(
                Map.of(
                        8, "FIX.4.4",
                        35, "A",
                        10, ""
                ),
                FixParser.parseBinaryNonRepetitive(customEncoded, Encoding.CUSTOM, 8,35,10)
        );
        assertEquals(
                Map.of(
                        8, "FIX.4.4",
                        35, "A",
                        10, ""
                ),
                FixParser.parseBinaryNonRepetitive(asciiEncoded, Encoding.ASCII, 8,35,10)
        );
    }

    @Test
    void encodeDecodeRepetitiveTags() {
        String message = "8=FIX.4.4\u00019=148\u00019=158\u000135=A\u0001";
        byte[] customEncoded = FixEncoder.encodeBinary(message);
        byte[] asciiEncoded = message.getBytes(StandardCharsets.US_ASCII);
        Map<Integer, List<String>> expected = parseFixMessageRepetitive(message);
        assertEquals(expected, FixParser.parseBinaryRepetitive(customEncoded, Encoding.CUSTOM));
        assertEquals(expected, FixParser.parseBinaryRepetitive(asciiEncoded, Encoding.ASCII));
        assertEquals(
                List.of("148", "158"),
                FixParser.parseBinaryRepetitive(customEncoded, Encoding.CUSTOM, 9).get(9)
        );
        assertEquals(
                List.of("148", "158"),
                FixParser.parseBinaryRepetitive(asciiEncoded, Encoding.ASCII, 9).get(9)
        );
        assertEquals(
                Map.of(
                        8, List.of("FIX.4.4"),
                        9, List.of("148", "158")
                ),
                FixParser.parseBinaryRepetitive(customEncoded, Encoding.CUSTOM, 8,9)
        );
        assertEquals(
                Map.of(
                        8, List.of("FIX.4.4"),
                        9, List.of("148", "158")
                ),
                FixParser.parseBinaryRepetitive(asciiEncoded, Encoding.ASCII, 8,9)
        );
        assertEquals(
                Map.of(
                        8, List.of("FIX.4.4"),
                        35,List.of( "A"),
                        10, List.of()
                ),
                FixParser.parseBinaryRepetitive(customEncoded, Encoding.CUSTOM, 8,35,10)
        );
        assertEquals(
                Map.of(
                        8, List.of("FIX.4.4"),
                        35,List.of( "A"),
                        10, List.of()
                ),
                FixParser.parseBinaryRepetitive(asciiEncoded, Encoding.ASCII, 8,35,10)
        );
    }
}