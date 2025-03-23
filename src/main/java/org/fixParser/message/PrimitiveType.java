package org.fixParser.message;

public enum PrimitiveType {
    CHAR("char", 2),
    INT("int", 4),
    BYTE("byte", 1),
    SHORT("short", 2)
    ;
    private final String name;
    private final int size;
    PrimitiveType(final String name, final int size) {
        this.name = name;
        this.size = size;
    }
}
