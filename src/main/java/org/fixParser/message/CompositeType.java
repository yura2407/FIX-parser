package org.fixParser.message;

public class CompositeType implements Type {

    //TODO: Overwrite hashCode and equals for tests
    private final String name;
    private final Type[] nestedTypes;

    public CompositeType(String name, Type[] nestedTypes) {
        this.name = name;
        this.nestedTypes = nestedTypes;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
