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
        byte[] actual = FixEncoder.encodeBinary(message);
        System.out.println(Arrays.toString(actual));
        assertArrayEquals(expected, actual);
    }

    @Test
    void verifyBufferReturnsToOriginalStateAfterEncoding() {
        ByteBuffer buffer = FixEncoder.getBuffer();
        assertEquals(1024*1024, buffer.capacity());
        assertEquals(0, buffer.position());
        assertEquals(1024*1024, buffer.limit());
        assertEquals(1024*1024, buffer.remaining());
        FixEncoder.encodeBinary(message);
        assertEquals(1024*1024, buffer.capacity());
        assertEquals(0, buffer.position());
        assertEquals(1024*1024, buffer.limit());
        assertEquals(1024*1024, buffer.remaining());
    }
}