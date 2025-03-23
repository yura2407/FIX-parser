package org.fixParser.message;

import java.util.Map;

public class EnumType implements Type {
    private final String name;
    private final Map<Character, String> values;
    public EnumType(String name, Map<Character, String> values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
