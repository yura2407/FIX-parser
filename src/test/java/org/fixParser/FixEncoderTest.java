package org.fixParser;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FixEncoderTest {

    String message = "8=FIX.4.4\u00019=148\u0001";
    @Test
    void encodeSimpleMessage() {
        byte[] firstFieldNum = new byte[]{0, 0, 0, 8};
        byte[] firstFieldLen = new byte[]{0, 0, 0, 7};
        byte[] firstFieldData = new byte[]{70, 73, 88, 46, 52, 46, 52};
        byte[] secondFieldNum = new byte[]{0, 0, 0, 9};
        byte[] secondFieldLen = new byte[]{0, 0, 0, 3};
        byte[] secondFieldData = new byte[]{49, 52, 56};
        byte[] expected = TestUtils.concatenate(
                firstFieldNum,
                firstFieldLen,
                firstFieldData,
                secondFieldNum,
                secondFieldLen,
                secondFieldData
        );
        byte[] actual = FixEncoder.encode(message);
        System.out.println(Arrays.toString(actual));
        assertArrayEquals(expected, actual);
    }

    @Test
    void verifyBufferBehavior() {
        ByteBuffer buffer = FixEncoder.getBuffer();
        assertEquals(1024*1024, buffer.capacity());
        assertEquals(0, buffer.position());
        assertEquals(1024*1024, buffer.limit());
        FixEncoder.encode(message);
        byte[] bytes = new byte[buffer.position()];
        buffer.get(bytes);
        buffer.clear();
        byte[] expected = new byte[]{0, 0, 0, 7, 0, 0, 0, 8};
        assertArrayEquals(expected, bytes);
    }

    @Test
    void convertIntToBytes() {
        int val = 7;
        byte[] expected = new byte[]{0, 0, 0, 7};
        byte[] actual = new byte[4];
        // Extract each byte using bitwise operations
        actual[0] = (byte) (val >> 24); // Most significant byte
        actual[1] = (byte) (val >> 16);
        actual[2] = (byte) (val >> 8);
        actual[3] = (byte) val; // Least significant byte

        assertArrayEquals(expected, actual);
    }
}