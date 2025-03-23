package org.fixParser.message;

public class CompositeType implements Type {
    private final String name;
    private final SimpleType[] types;

    public CompositeType(String name, SimpleType[] types) {
        this.name = name;
        this.types = types;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
