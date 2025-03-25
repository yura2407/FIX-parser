package org.fixParser;

import java.nio.ByteBuffer;

public class FixEncoder {

    //TODO make thread safe
    private static final ByteBuffer buffer = ByteBuffer.allocateDirect(1024*1024);
    public static byte[] encodeBinary(String message) {
        populateBuffer(message);
        return drainBuffer();
    }

    static ByteBuffer getBuffer() {
        return buffer;
    }

    private static void populateBuffer(String message) {
        final StringBuilder field = new StringBuilder();
        for (char c : message.toCharArray()) {
            switch (c) {
                case '=' -> {
                    //TODO int conversion without toString
                    buffer.putInt(Integer.parseInt(field.toString()));
                    field.setLength(0);
                }
                case '\u0001' -> {
                    buffer.putInt(field.length());
                    for (int i = 0; i < field.length(); i++) {
                        buffer.put((byte) field.charAt(i));
                    }
                    field.setLength(0);
                }
                default -> field.append(c);
            }
        }
    }

    private static byte[] drainBuffer() {
        byte[] bytes = new byte[buffer.position()];
        buffer.flip();
        buffer.get(bytes);
        buffer.clear();
        return bytes;
    }
}
