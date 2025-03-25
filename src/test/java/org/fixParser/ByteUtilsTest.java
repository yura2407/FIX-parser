package org.fixParser;

import org.junit.jupiter.api.RepeatedTest;

import java.nio.ByteBuffer;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ByteUtilsTest {

    ByteBuffer buffer = ByteBuffer.allocate(4);
    Random random = new Random();
    @RepeatedTest(100)
    void getIntFromByteArray() {
        int val = random.nextInt();
        byte[] encoded = buffer.putInt(val).array();
        int res = ByteUtils.getInt(encoded, 0);
        assertEquals(val, res);
    }

    @RepeatedTest(100)
    void putIntToByteArray() {
        int val = random.nextInt();
        byte[] encoded = buffer.putInt(val).array();
        byte[] res = new byte[4];
        ByteUtils.putInt(res, 0, val);
        assertArrayEquals(encoded, res);
    }
}