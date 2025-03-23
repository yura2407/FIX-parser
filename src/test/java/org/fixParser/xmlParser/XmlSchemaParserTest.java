package org.fixParser.xmlParser;

import org.fixParser.message.Type;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XmlSchemaParserTest {

    XmlSchemaParser parser = new XmlSchemaParser();
    Document doc = parser.parse("test-schema.xml");
    Map<String, Type> types = new HashMap<>();

    @Test
    void parseTypes(){
        parser.parseSimpleTypes(doc, types);
        assertEquals(11, types.size());
    }

}