package org.fixParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestUtils {

    static byte[] concatenate(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }
        byte[] result = new byte[length];
        int destPos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, destPos, array.length);
            destPos += array.length;
        }
        return result;
    }

    static HashMap<Integer, String> parseFixMessageNonRepetitive(String message) {
        HashMap<Integer, String> result = new HashMap<>();
        String[] pairs = message.split("\u0001");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            result.put(Integer.parseInt(keyValue[0]), keyValue[1]);        }
        return result;
    }


    static HashMap<Integer, List<String>> parseFixMessageRepetitive(String message) {
        HashMap<Integer, List<String>> result = new HashMap<>();
        String[] pairs = message.split("\u0001");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            result.computeIfAbsent(
                    Integer.parseInt(keyValue[0]), k -> new ArrayList<>()
            ).add(keyValue[1]);
        }
        return result;
    }
}
