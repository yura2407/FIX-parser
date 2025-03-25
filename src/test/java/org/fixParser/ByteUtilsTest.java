package org.fixParser;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ByteUtilsTest {

    ByteBuffer buffer = ByteBuffer.allocate(1024);
    Random random = new Random();
    @RepeatedTest(100)
    void convertSingleIntToBytes() {
        int val = random.nextInt();
        byte[] encoded = buffer.putInt(val).array();
        int res = ByteUtils.getInt(encoded, 0);
        assertEquals(val, res);
    }

    @RepeatedTest(100)
    void convertMultipleIntToBytes() {
        int[] vals = new int[random.nextInt(200)];
        for (int i = 0; i < vals.length; i++) {
            vals[i] = random.nextInt();
            buffer.putInt(vals[i]);
        }
        byte[] encoded = buffer.array();
        for (int i = 0; i < vals.length; i++) {
            int res = ByteUtils.getInt(encoded, i * 4);
            assertEquals(vals[i], res);
        }
    }

}