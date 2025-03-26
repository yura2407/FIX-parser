package org.fixParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private final StringBuilder stringBuilder = new StringBuilder();
    private final AtomicBoolean isParsing = new AtomicBoolean(false);

    public HashMap<Integer, String> parseBinaryNonRepetitive(byte[] message, Encoding encoding, int... tags) {
        HashMap<Integer, String> map = new HashMap<>();
        if (tags.length == 0) {
            readBinaryArray(message, true, map, NON_REPETITIVE_MAP_UPDATER, encoding);
            return map;
        }
        for (int tag: tags) {
            map.put(tag, "");
        }
        readBinaryArray(message, false, map, NON_REPETITIVE_MAP_UPDATER, encoding);
        return map;
    }

    public HashMap<Integer, List<String>> parseBinaryRepetitive(byte[] message, Encoding encoding, int... tags) {
        HashMap<Integer, List<String>> map = new HashMap<>();
        if (tags.length == 0){
            readBinaryArray(message, true, map, REPETITIVE_MAP_UPDATER, encoding);
            return map;
        }
        for (int tag: tags) {
            map.put(tag, new ArrayList<>());
        }
        readBinaryArray(message, false, map, REPETITIVE_MAP_UPDATER, encoding);
        return map;
    }

    private <T> void readBinaryArray(
            byte[] message,
            boolean getAll,
            HashMap<Integer, T> map,
            MapUpdater<Integer, T> mapUpdater,
            Encoding encoding
    ) {
        switch (encoding) {
            case CUSTOM -> {
                while(readCustomBinaryArray(message, getAll, map, mapUpdater)!=1){}
            }
            case ASCII -> {
                while(readDefaultBinaryArray(message, getAll, map, mapUpdater)!=1){};
            }
        }
    }

    private <T> int readCustomBinaryArray(
            byte[] message,
            boolean getAll,
            HashMap<Integer, T> map,
            MapUpdater<Integer, T> mapUpdater
    ) {
        int tagOffset = 0;
        int fieldOffset;
        int tag;
        int tagBytes = ByteUtils.getInt(message, tagOffset);
        int numTags = getAll ? tagBytes / 8 : map.size();
        tagOffset += 4;
        if (isParsing.compareAndSet(false, true)) {
            try {
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
                        stringBuilder.setLength(0);
                        if (numTags == 0) {
                            return 1;
                        }
                    }
                    tagOffset += 4;
                }
            } catch (Exception e) {
                System.err.println("Error parsing message: " + e.getMessage());
            } finally {
                isParsing.set(false);
                stringBuilder.setLength(0);
            }
            return 1;
        } else {
            return 0;
        }
    }

    private <T> int readDefaultBinaryArray(
            byte[] message,
            boolean getAll,
            HashMap<Integer, T> map,
            MapUpdater<Integer, T> mapUpdater
    ) {
        int offset = 0;
        int tag;
        int numTags = getAll ? Integer.MAX_VALUE : map.size();
        if (isParsing.compareAndSet(false, true)) {
            try {
                while (offset < message.length) {
                    while (message[offset] != '=') {
                        stringBuilder.append((char) message[offset++]);
                    }
                    tag = Integer.parseInt(stringBuilder, 0, stringBuilder.length(), 10);
                    stringBuilder.setLength(0);
                    offset += 1; //Skip '='
                    if (getAll || map.containsKey(tag)) {
                        while (message[offset] != '\u0001') {
                            stringBuilder.append((char) message[offset++]);
                        }
                        numTags = mapUpdater.processMapUpdateAndReturnNumberOfTagsLeft(
                                map, tag, stringBuilder.toString(), numTags
                        );
                        stringBuilder.setLength(0);
                        if (numTags == 0) {
                            return 1;
                        }
                    } else {
                        while (message[offset] != '\u0001') {
                            offset++;
                        }
                    }
                    offset++;
                }
            } catch (Exception e) {
                System.err.println("Error parsing message: " + e.getMessage());
            } finally {
                isParsing.set(false);
                stringBuilder.setLength(0);
            }
            return 1;
        } else {
            return 0;
        }
    }
}
