package org.fixParser;

import java.util.*;

@FunctionalInterface
interface MapUpdater<K, V> {
    void updateMap(Map<K, V> map, K key, String value);
}

public class FixParser {

    private static final MapUpdater<Integer, String> nonRepetitiveMapUpdater = Map::put;
    private static final MapUpdater<Integer, List<String>> repetitiveMapUpdater = (map, key, value) ->
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);

    public static HashMap<Integer, String> parseBinaryNonRepetitive(byte[] message, int... tags) {
        HashMap<Integer, String> map = new HashMap<>();
        if (tags.length == 0) {
            readBinaryArray(message, true, map, nonRepetitiveMapUpdater);
            return map;
        }
        for (int tag: tags) {
            map.put(tag, "");
        }
        readBinaryArray(message, false, map, nonRepetitiveMapUpdater);
        return map;
    }

    public static HashMap<Integer, List<String>> parseBinaryRepetitive(byte[] message, int... tags) {
        HashMap<Integer, List<String>> map = new HashMap<>();
        if (tags.length == 0){
            readBinaryArray(message, true, map, repetitiveMapUpdater);
            return map;
        }
        for (int tag: tags) {
            map.put(tag, new ArrayList<>());
        }
        readBinaryArray(message, false, map, repetitiveMapUpdater);
        return map;
    }

    private static <T> void readBinaryArray(
            byte[] message,
            boolean getAll,
            HashMap<Integer, T> map,
            MapUpdater mapUpdater
    ) {
        int offset = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while (offset < message.length) {
            int tag = ByteUtils.getInt(message, offset);
            offset += 4;
            int length = ByteUtils.getInt(message, offset);
            offset += 4;
            if (getAll || map.containsKey(tag)) {
                for (int i = 0; i < length; i++) {
                    stringBuilder.append((char) message[offset + i]);
                }
                mapUpdater.updateMap(map, tag, stringBuilder.toString());
                stringBuilder.setLength(0);
            }
            offset += length;
        }
    }
}
