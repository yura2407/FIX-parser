package org.fixParser;

//TODO make thread safe
class ByteArrayProperties {
    final int numTags;
    final int totalSize;

    ByteArrayProperties(int numTags, int totalSize) {
        this.numTags = numTags;
        this.totalSize = totalSize;
    }
}
