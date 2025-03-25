package org.fixParser;

import java.util.*;

@FunctionalInterface
interface MapUpdater<K, V> {
    void updateMap(Map<K, V> map, K key, String value);
}

public class FixParser {

    //TODO can we figure out repetitive tags from the message?
    private static final MapUpdater<Integer, String> nonRepetitiveMapUpdater = Map::put;
    private static final MapUpdater<Integer, List<String>> repetitiveMapUpdater = (map, key, value) ->
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);

    public static HashMap<Integer, String> parseBinaryNonRepetitive(byte[] message, int... tags) {
        HashMap<Integer, String> map = new HashMap<>();
        if (tags.length == 0) {
            readCustomBinaryArray(message, true, map, nonRepetitiveMapUpdater);
            return map;
        }
        for (int tag: tags) {
            map.put(tag, "");
        }
        readCustomBinaryArray(message, false, map, nonRepetitiveMapUpdater);
        return map;
    }

    public static HashMap<Integer, List<String>> parseBinaryRepetitive(byte[] message, int... tags) {
        HashMap<Integer, List<String>> map = new HashMap<>();
        if (tags.length == 0){
            readCustomBinaryArray(message, true, map, repetitiveMapUpdater);
            return map;
        }
        for (int tag: tags) {
            map.put(tag, new ArrayList<>());
        }
        readCustomBinaryArray(message, false, map, repetitiveMapUpdater);
        return map;
    }

    private static <T> void readCustomBinaryArray(
            byte[] message,
            boolean getAll,
            HashMap<Integer, T> map,
            MapUpdater mapUpdater
    ) {
        int tagOffset = 0;
        int fieldOffset;
        int tag;
        StringBuilder stringBuilder = new StringBuilder();
        int tagBytes = ByteUtils.getInt(message, tagOffset);
        tagOffset += 4;
        while (tagOffset < tagBytes) {
            tag = ByteUtils.getInt(message, tagOffset);
            tagOffset += 4;
            if (getAll || map.containsKey(tag)) {
                fieldOffset = ByteUtils.getInt(message, tagOffset);
                while (message[fieldOffset] != '\u0001') {
                    stringBuilder.append((char) message[fieldOffset++]);
                }
                mapUpdater.updateMap(map, tag, stringBuilder.toString());
                stringBuilder.setLength(0);
            }
            tagOffset += 4;
        }
    }
}
