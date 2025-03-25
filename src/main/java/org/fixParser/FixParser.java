package org.fixParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FunctionalInterface
interface MapUpdater<K, V> {
    int processMapUpdateAndReturnNumberOfTagsLeft(
            Map<K, V> map, K key, String value, int tagsLeft
    );
}

public class FixParser {

    //TODO can we figure out repetitive tags from the message?
    private static final MapUpdater<Integer, String> NON_REPETITIVE_MAP_UPDATER = (map, key, value, tagsLeft) -> {
        map.put(key, value);
        return tagsLeft - 1;
    };
    private static final MapUpdater<Integer, List<String>> REPETITIVE_MAP_UPDATER = (map, key, value, tagsLeft) -> {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        return tagsLeft;
    };

    public static HashMap<Integer, String> parseBinaryNonRepetitive(byte[] message, int... tags) {
        HashMap<Integer, String> map = new HashMap<>();
        if (tags.length == 0) {
            readCustomBinaryArray(message, true, map, NON_REPETITIVE_MAP_UPDATER);
            return map;
        }
        for (int tag: tags) {
            map.put(tag, "");
        }
        readCustomBinaryArray(message, false, map, NON_REPETITIVE_MAP_UPDATER);
        return map;
    }

    public static HashMap<Integer, List<String>> parseBinaryRepetitive(byte[] message, int... tags) {
        HashMap<Integer, List<String>> map = new HashMap<>();
        if (tags.length == 0){
            readCustomBinaryArray(message, true, map, REPETITIVE_MAP_UPDATER);
            return map;
        }
        for (int tag: tags) {
            map.put(tag, new ArrayList<>());
        }
        readCustomBinaryArray(message, false, map, REPETITIVE_MAP_UPDATER);
        return map;
    }

    private static <T> void readCustomBinaryArray(
            byte[] message,
            boolean getAll,
            HashMap<Integer, T> map,
            MapUpdater<Integer, T> mapUpdater
    ) {
        int tagOffset = 0;
        int fieldOffset;
        int tag;
        StringBuilder stringBuilder = new StringBuilder();
        int tagBytes = ByteUtils.getInt(message, tagOffset);
        int numTags = getAll ? tagBytes / 8 : map.size();
        tagOffset += 4;
        while (tagOffset < tagBytes) {
            tag = ByteUtils.getInt(message, tagOffset);
            tagOffset += 4;
            if (getAll || map.containsKey(tag)) {
                fieldOffset = ByteUtils.getInt(message, tagOffset);
                while (message[fieldOffset] != '\u0001') {
                    stringBuilder.append((char) message[fieldOffset++]);
                }
                numTags = mapUpdater.processMapUpdateAndReturnNumberOfTagsLeft(
                        map, tag, stringBuilder.toString(), numTags
                );
                if (numTags == 0) {
                    return;
                }
                stringBuilder.setLength(0);
            }
            tagOffset += 4;
        }
    }
}
