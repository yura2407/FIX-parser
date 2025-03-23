package org.fixParser.xmlParser;

import org.fixParser.message.CompositeType;
import org.fixParser.message.EnumType;
import org.fixParser.message.Message;
import org.fixParser.message.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XmlSchemaParser {
    private Type[] types;
    private EnumType[] enums;
    private CompositeType[] composites;
    private Message[] messages;

    public void parse(String filePath){
        try{
            File file = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            parseTypes(doc);
        } catch (ParserConfigurationException | SAXException e) {
            System.out.println("Error parsing XML file");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Error reading XML file");
            throw new RuntimeException(e);
        }
    }

    private void parseTypes(Document doc){
        NodeList typeList = doc.getElementsByTagName("type");
        types = new Type[typeList.getLength()];
        for (int i = 0; i < typeList.getLength(); i++) {
            Node node = typeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Type type = new Type.Builder()
                        .name(element.getAttribute("name"))
                        .primitiveType(element.getAttribute("primitiveType"))
                        .isOptional(!element.getAttribute("presence").isEmpty())
                        .build();
                types[i] = type;
            }
        }
    }
}
