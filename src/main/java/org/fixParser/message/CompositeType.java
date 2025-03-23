package org.fixParser.message;

public class CompositeType {
    private final String name;
    private final Type[] types;

    public CompositeType(String name, Type[] types) {
        this.name = name;
        this.types = types;
    }
}
