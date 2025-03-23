package org.fixParser.message;

public enum PrimitiveType implements Type {
    CHAR("char", 2),
    INT64("long", 8),
    INT32("int", 4),
    INT8("byte", 1),
    UINT8("unsigned byte", 1),
    UINT16("unsigned short", 2),
    UINT64("unsigned long", 8),
    ;
    private final String name;
    private final int size;
    PrimitiveType(final String name, final int size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
