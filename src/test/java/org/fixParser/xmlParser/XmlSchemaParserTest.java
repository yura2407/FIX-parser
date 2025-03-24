package org.fixParser.xmlParser;

import org.fixParser.message.SimpleType;
import org.fixParser.message.Type;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.List;
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
        List<String> expectedTypes = List.of(
                "date", "enumEncoding", "idString", "intEnumEncoding", "currency",
                "length", "varData", "blockLength", "numInGroup", "numGroups", "numVarDataFields"
        );
        expectedTypes.forEach(type -> assertTrue(types.containsKey(type)));
    }

    @Test
    void parseCompositeTypes(){
        parser.parseSimpleTypes(doc, types);
        parser.parseCompositeTypes(doc, types);
        assertEquals(13, types.size());
        List<String> expectedTypes = List.of("DATA", "groupSizeEncoding");
        expectedTypes.forEach(type -> assertTrue(types.containsKey(type)));
    }

    @Test
    void parseEnumTypes(){
        parser.parseSimpleTypes(doc, types);
        parser.parseCompositeTypes(doc, types);
        parser.parseEnumTypes(doc, types);
        assertEquals(15, types.size());
        List<String> expectedTypes = List.of("TimeUnit", "execTypeEnum");
        expectedTypes.forEach(type -> assertTrue(types.containsKey(type)));
    }
}