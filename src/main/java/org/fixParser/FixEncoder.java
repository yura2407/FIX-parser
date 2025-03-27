package org.fixParser;
public class FixEncoder {

    public static byte[] encodeBinary(String message) {
        return populateBuffer(message);
    }

    private static ByteArrayProperties getByteArrayProperties(String message){
        int numTags = 0;
        int numDataChars = 0;
        int c = 0;
        while (c < message.length()){
            if (message.charAt(c) == '=') {
                c++;
                while (message.charAt(c) != '\u0001') {
                    numDataChars++;
                    c++;
                }
            }
            if (message.charAt(c) == '\u0001') {
                numTags++;
                numDataChars++;
            }
            c++;
        }
        return new ByteArrayProperties(numTags, 4 + numTags * 8 + numDataChars);
    }

    private static byte[] populateBuffer(String message) {
        ByteArrayProperties byteArrayProperties = getByteArrayProperties(message);
        byte[] buffer = new byte[byteArrayProperties.totalSize];
        final StringBuilder field = new StringBuilder();
        int offsetTags = 0;
        ByteUtils.putInt(buffer, offsetTags, byteArrayProperties.numTags * 8);
        offsetTags+=4;
        int offsetData = byteArrayProperties.numTags * 8 + offsetTags;
        for (char c : message.toCharArray()) {
            switch (c) {
                case '=' -> {
                    ByteUtils.putInt(buffer, offsetTags, Integer.parseInt(field, 0, field.length(), 10));
                    offsetTags+=4;
                    field.setLength(0);
                    ByteUtils.putInt(buffer, offsetTags, offsetData);
                    offsetTags+=4;
                }
                case '\u0001' -> {
                    field.append(c);
                    for (int i = 0; i < field.length(); i++) {
                        buffer[offsetData++] = (byte) field.charAt(i);
                    }
                    field.setLength(0);
                }
                default -> field.append(c);
            }
        }
        return buffer;
    }
}
