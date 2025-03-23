package org.fixParser.xmlParser;

import org.fixParser.message.EnumType;
import org.fixParser.message.Message;

import java.util.Map;

public class XmlSchemaHolder {
    private Map<String, Message> messages;
    private Map<String, EnumType> enumTypes;
}
