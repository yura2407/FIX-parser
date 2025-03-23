package org.fixParser.message;

public class Message {
    private final String name;
    private final Field[] fields;
    private final int id;

    public Message(String name, int id, Field[] fields) {
        this.name = name;
        this.id = id;
        this.fields = fields;
    }
}
