package org.fixParser;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FixParserTest {

    @Test
    void encodeDecodeNonRepetitiveTags() {
        String message = "8=FIX.4.4\u00019=148\u0001";
        byte[] encoded = FixEncoder.encodeBinary(message);
        Map<Integer, String> decoded = FixParser.parseBinaryNonRepetitive(encoded);
        Map<Integer, String> expected = Map.of(
                8, "FIX.4.4",
                9, "148"
        );
        Map<Integer, String> expectedWithDummyTag = Map.of(
                8, "FIX.4.4",
                9, "148",
                10, ""
        );
        assertEquals(expected, decoded);
        assertEquals(expected, FixParser.parseBinaryNonRepetitive(encoded, 8,9));
        assertEquals(expectedWithDummyTag, FixParser.parseBinaryNonRepetitive(encoded, 8,9,10));
        assertEquals(
                "FIX.4.4",
                FixParser.parseBinaryNonRepetitive(encoded, 8).get(8)
        );
    }

    @Test
    void encodeDecodeRepetitiveTags() {
        String message = "8=FIX.4.4\u00019=148\u00019=158\u0001";
        byte[] encoded = FixEncoder.encodeBinary(message);
        Map<Integer, List<String>> decoded = FixParser.parseBinaryRepetitive(encoded);
        Map<Integer, List<String>> expected = Map.of(
                8, List.of("FIX.4.4"),
                9, List.of("148", "158")
        );
        Map<Integer, List<String>> expectedWithDummyTag = Map.of(
                8, List.of("FIX.4.4"),
                9, List.of("148", "158"),
                10, List.of()
        );
        assertEquals(expected, decoded);
        assertEquals(expected, FixParser.parseBinaryRepetitive(encoded, 8,9));
        assertEquals(expectedWithDummyTag, FixParser.parseBinaryRepetitive(encoded, 8,9,10));
        assertEquals(
                List.of("148", "158"),
                FixParser.parseBinaryRepetitive(encoded, 9).get(9)
        );
    }
}