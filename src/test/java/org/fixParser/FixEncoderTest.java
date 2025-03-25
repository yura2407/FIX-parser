package org.fixParser;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FixEncoderTest {

    String message = "8=FIX.4.4\u00019=148\u0001";

    @Test
    void sizeIsObtainedCorrectly() {
        assertEquals(32, FixEncoder.getByteArrayProperties(message).totalSize);
        assertEquals(2, FixEncoder.getByteArrayProperties(message).numTags);
    }
    @Test
    void encodeSimpleMessage() {
        byte[] numberOfTagBytes = new byte[]{0, 0, 0, 16};
        byte[] firstFieldNum = new byte[]{0, 0, 0, 8};
        byte[] firstFieldOffset = new byte[]{0, 0, 0, 20};
        byte[] secondFieldNum = new byte[]{0, 0, 0, 9};
        byte[] secondFieldOffset = new byte[]{0, 0, 0, 28};
        byte[] firstFieldData = new byte[]{70, 73, 88, 46, 52, 46, 52, 1};
        byte[] secondFieldData = new byte[]{49, 52, 56, 1};
        byte[] expected = TestUtils.concatenate(
                numberOfTagBytes,
                firstFieldNum,
                firstFieldOffset,
                secondFieldNum,
                secondFieldOffset,
                firstFieldData,
                secondFieldData
        );
        byte[] actual = FixEncoder.encodeBinary(message);
        assertArrayEquals(expected, actual);
    }
}