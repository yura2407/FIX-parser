package org.fixParser.message;

public class Field {
    private final String name;
    private final Type type;
    private final short id;
    private final int offset;

    public Field(String name, SimpleType type, short id, int offset) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.offset = offset;
    }
}
