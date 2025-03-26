package org.fixParser;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.fixParser.TestUtils.parseFixMessageNonRepetitive;
import static org.fixParser.TestUtils.parseFixMessageRepetitive;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FixParserTest {

    private final FixParser fixParser = new FixParser();
    @Test
    void encodeDecodeNonRepetitiveTags() {
        String message = "8=FIX.4.4\u00019=148\u000135=A\u0001";
        byte[] customEncoded = FixEncoder.encodeBinary(message);
        byte[] asciiEncoded = message.getBytes(StandardCharsets.US_ASCII);
        Map<Integer, String> expected = parseFixMessageNonRepetitive(message);
        assertEquals(expected, fixParser.parseBinaryNonRepetitive(customEncoded, Encoding.CUSTOM));
        assertEquals(expected, fixParser.parseBinaryNonRepetitive(asciiEncoded, Encoding.ASCII));
        assertEquals(
                "FIX.4.4",
                fixParser.parseBinaryNonRepetitive(customEncoded, Encoding.CUSTOM, 8).get(8)
        );
        assertEquals(
                Map.of(
                        8, "FIX.4.4",
                        35, "A"
                ),
                fixParser.parseBinaryNonRepetitive(customEncoded, Encoding.CUSTOM, 8,35)
        );
        assertEquals(
                Map.of(
                        8, "FIX.4.4",
                        35, "A"
                ),
                fixParser.parseBinaryNonRepetitive(asciiEncoded, Encoding.ASCII, 8,35)
        );
        assertEquals(
                Map.of(
                        8, "FIX.4.4",
                        35, "A",
                        10, ""
                ),
                fixParser.parseBinaryNonRepetitive(customEncoded, Encoding.CUSTOM, 8,35,10)
        );
        assertEquals(
                Map.of(
                        8, "FIX.4.4",
                        35, "A",
                        10, ""
                ),
                fixParser.parseBinaryNonRepetitive(asciiEncoded, Encoding.ASCII, 8,35,10)
        );
    }

    @Test
    void encodeDecodeRepetitiveTags() {
        String message = "8=FIX.4.4\u00019=148\u00019=158\u000135=A\u0001";
        byte[] customEncoded = FixEncoder.encodeBinary(message);
        byte[] asciiEncoded = message.getBytes(StandardCharsets.US_ASCII);
        Map<Integer, List<String>> expected = parseFixMessageRepetitive(message);
        assertEquals(expected, fixParser.parseBinaryRepetitive(customEncoded, Encoding.CUSTOM));
        assertEquals(expected, fixParser.parseBinaryRepetitive(asciiEncoded, Encoding.ASCII));
        assertEquals(
                List.of("148", "158"),
                fixParser.parseBinaryRepetitive(customEncoded, Encoding.CUSTOM, 9).get(9)
        );
        assertEquals(
                List.of("148", "158"),
                fixParser.parseBinaryRepetitive(asciiEncoded, Encoding.ASCII, 9).get(9)
        );
        assertEquals(
                Map.of(
                        8, List.of("FIX.4.4"),
                        9, List.of("148", "158")
                ),
                fixParser.parseBinaryRepetitive(customEncoded, Encoding.CUSTOM, 8,9)
        );
        assertEquals(
                Map.of(
                        8, List.of("FIX.4.4"),
                        9, List.of("148", "158")
                ),
                fixParser.parseBinaryRepetitive(asciiEncoded, Encoding.ASCII, 8,9)
        );
        assertEquals(
                Map.of(
                        8, List.of("FIX.4.4"),
                        35,List.of( "A"),
                        10, List.of()
                ),
                fixParser.parseBinaryRepetitive(customEncoded, Encoding.CUSTOM, 8,35,10)
        );
        assertEquals(
                Map.of(
                        8, List.of("FIX.4.4"),
                        35,List.of( "A"),
                        10, List.of()
                ),
                fixParser.parseBinaryRepetitive(asciiEncoded, Encoding.ASCII, 8,35,10)
        );
    }
}