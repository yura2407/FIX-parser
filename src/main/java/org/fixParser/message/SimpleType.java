package org.fixParser.message;

public class SimpleType implements Type {
    //TODO: Overwrite hashCode and equals for tests
    private final String name;
    private final PrimitiveType primitiveType;
    private final boolean optional;
    private final int length;

    public SimpleType(String name, String primitiveType, boolean optional, String length) {
        this.name = name;
        this.primitiveType = PrimitiveType.valueOf(primitiveType.toUpperCase());
        this.optional = optional;
        this.length = length.isEmpty() ? 1 : Integer.parseInt(length);    }

    @Override
    public String getName() {
        return this.name;
    }
}
