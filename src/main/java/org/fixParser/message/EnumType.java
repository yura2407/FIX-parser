package org.fixParser.message;

import java.util.Map;
import java.util.Set;

public class EnumType implements Type {
    //TODO: Overwrite hashCode and equals for tests
    //TODO: Enable validation of bytes
    private final String name;
    private final Set<Character> validValues;
    public EnumType(String name, Set<Character> validValues) {
        this.name = name;
        this.validValues = validValues;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
