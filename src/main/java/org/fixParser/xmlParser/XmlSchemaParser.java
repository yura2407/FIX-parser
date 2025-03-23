package org.fixParser.xmlParser;

import org.fixParser.message.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class XmlSchemaParser {
    private EnumType[] enums;
    private CompositeType[] composites;
    private Message[] messages;

    public Document parse(String schemaPath){
        try (InputStream fileInput = this.getClass().getClassLoader().getResourceAsStream(schemaPath)){
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fileInput);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (ParserConfigurationException | SAXException e) {
            System.out.println("Error parsing XML file");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Error reading XML file");
            throw new RuntimeException(e);
        }
    }

    public void parseSimpleTypes(Document doc, Map<String, Type> types){
        NodeList typeList = doc.getElementsByTagName("type");
        for (int i = 0; i < typeList.getLength(); i++) {
            Node node = typeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                SimpleType type = new SimpleType(
                        element.getAttribute("name"),
                        element.getAttribute("primitiveType"),
                        !element.getAttribute("presence").isEmpty(),
                        element.getAttribute("length")
                );
                types.put(type.getName(), type);
            }
        }
    }

//    public CompositeType[] parseCompositeTypes(Document doc){
//        NodeList typeList = doc.getElementsByTagName("composite");
//        CompositeType[] types = new CompositeType[typeList.getLength()];
//        for (int i = 0; i < typeList.getLength(); i++) {
//            Node node = typeList.item(i);
//            if (node.getNodeType() == Node.ELEMENT_NODE) {
//                Element element = (Element) node;
//                String name = element.getAttribute("name");
//                NodeList fieldList = element.getElementsByTagName("field");
//                SimpleType[] fields = new SimpleType[fieldList.getLength()];
//                for (int j = 0; j < fieldList.getLength(); j++) {
//                    Node fieldNode = fieldList.item(j);
//                    if (fieldNode.getNodeType() == Node.ELEMENT_NODE) {
//                        Element fieldElement = (Element) fieldNode;
//                        SimpleType field = new SimpleType.Builder()
//                                .name(fieldElement.getAttribute("name"))
//                                .primitiveType(fieldElement.getAttribute("primitiveType"))
//                                .isOptional(!fieldElement.getAttribute("presence").isEmpty())
//                                .length(fieldElement.getAttribute("length"))
//                                .build();
//                        fields[j] = field;
//                    }
//                }
//                CompositeType type = new CompositeType(name, fields);
//                types[i] = type;
//            }
//        }
//        return types;
//    }

}
