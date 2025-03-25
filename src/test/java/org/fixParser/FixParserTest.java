package org.fixParser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FixParserTest {

    String message = "8=FIX.4.4\u00019=148";
    byte[] expected = new byte[] {0, 0, 0, 8, 70, 73, 88, 46, 52, 46, 52, 0, 0, 0, 9, 49, 52, 56};
    @Test
    void encode() {
        byte[] actual = FixParser.encode(message);
        assertArrayEquals(expected, actual);
    }
}