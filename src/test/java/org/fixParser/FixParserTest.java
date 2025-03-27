package org.fixParser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.fixParser.TestUtils.parseFixMessageNonRepetitive;
import static org.fixParser.TestUtils.parseFixMessageRepetitive;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FixParserTest {

    @ParameterizedTest
    @MethodSource("provideByteArrayAndEncodingMethodNonRepetitive")
    void encodeDecodeNonRepetitiveTags(byte[] messageBinary, String message, Encoding encoding) {
        HashMap<Integer, String> inputMap = new HashMap<>(
                Map.of(8, "random_val",
                        35, "",
                        10, "random_val"
                )
        );
        Map<Integer, String> expected = parseFixMessageNonRepetitive(message);
        assertEquals(
                expected,
                FixParser.parseBinaryNonRepetitive(messageBinary, encoding)
        );
        assertEquals(
                "FIX.4.4",
                FixParser.parseBinaryNonRepetitive(messageBinary, encoding, 8).get(8)
        );
        assertEquals(
                Map.of(
                        8, "FIX.4.4",
                        35, "A"
                ),
                FixParser.parseBinaryNonRepetitive(messageBinary, encoding, 8,35)
        );
        assertEquals(
                Map.of(
                        8, "FIX.4.4",
                        35, "A",
                        10, ""
                ),
                FixParser.parseBinaryNonRepetitive(messageBinary, encoding, 8,35,10)
        );
        FixParser.parseBinaryNonRepetitive(messageBinary, encoding, inputMap);
        assertEquals(
                Map.of(
                        8, "FIX.4.4",
                        35, "A",
                        10, ""
                ),
                inputMap
        );
        assertEquals(
                Map.of(
                        8, "FIX.4.4",
                        35, "A",
                        10, ""
                ),
                FixParser.parseBinaryNonRepetitive(messageBinary, encoding, 8,35,10)
        );
    }

    @ParameterizedTest
    @MethodSource("provideByteArrayAndEncodingMethodRepetitive")
    void encodeDecodeRepetitiveTags(byte[] messageBinary, String message, Encoding encoding) {
        Map<Integer, List<String>> expected = parseFixMessageRepetitive(message);
        HashMap<Integer, List<String>> inputMap = new HashMap<>(
                Map.of(8, new ArrayList<>(List.of("random_val", "random_val2")),
                        35, new ArrayList<>(),
                        9, new ArrayList<>(List.of("random_val", "random_val2")),
                        10, new ArrayList<>(List.of("random_val"))
                )
        );
        assertEquals(expected, FixParser.parseBinaryRepetitive(messageBinary, encoding));
        assertEquals(
                List.of("148", "158"),
                FixParser.parseBinaryRepetitive(messageBinary, encoding, 9).get(9)
        );
        assertEquals(
                Map.of(
                        8, List.of("FIX.4.4"),
                        9, List.of("148", "158")
                ),
                FixParser.parseBinaryRepetitive(messageBinary, encoding, 8,9)
        );
        assertEquals(
                Map.of(
                        8, List.of("FIX.4.4"),
                        35,List.of( "A"),
                        10, List.of()
                ),
                FixParser.parseBinaryRepetitive(messageBinary, encoding, 8,35,10)
        );
        FixParser.parseBinaryRepetitive(messageBinary, encoding, inputMap);
        assertEquals(
                Map.of(
                        8, List.of("FIX.4.4"),
                        35, List.of("A"),
                        9, List.of("148", "158"),
                        10, List.of()
                ),
                inputMap
        );
    }

    private static Stream<Arguments> provideByteArrayAndEncodingMethodNonRepetitive() {
        String message = "8=FIX.4.4\u00019=148\u000135=A\u0001";
        return getTestArguments(message);
    }

    private static Stream<Arguments> provideByteArrayAndEncodingMethodRepetitive() {
        String message = "8=FIX.4.4\u00019=148\u00019=158\u000135=A\u0001";
        return getTestArguments(message);
    }

    private static Stream<Arguments> getTestArguments(String message) {
        return Stream.of(
                Arguments.of(
                        FixEncoder.encodeBinary(message),
                        message,
                        Encoding.CUSTOM
                ),
                Arguments.of(
                        message.getBytes(StandardCharsets.US_ASCII),
                        message,
                        Encoding.ASCII
                )
        );
    }
}