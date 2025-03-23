package org.fixParser.message;

public class Type {
    private final String name;
    private final PrimitiveType primitiveType;
    private final boolean optional;

    private Type(Builder builder) {
        this.name = builder.name;
        this.primitiveType = builder.primitiveType;
        this.optional = builder.optional;
    }

    public static class Builder {
        private String name;
        private PrimitiveType primitiveType;
        private boolean optional;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder primitiveType(String primitiveType) {
            this.primitiveType = PrimitiveType.valueOf(primitiveType.toUpperCase());
            return this;
        }

        public Builder isOptional(boolean optional) {
            this.optional = optional;
            return this;
        }

        public Type build() {
            return new Type(this);
        }
    }
}
