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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public void parseCompositeTypes(Document doc, Map<String, Type> types){
        NodeList typeList = doc.getElementsByTagName("composite");
        for (int i = 0; i < typeList.getLength(); i++) {
            Node node = typeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String name = element.getAttribute("name");
                NodeList nestedTypesList = element.getElementsByTagName("type");
                Type[] nestedTypes = new SimpleType[nestedTypesList.getLength()];
                for (int j = 0; j < nestedTypesList.getLength(); j++) {
                    Node nestedType = nestedTypesList.item(j);
                    if (nestedType.getNodeType() == Node.ELEMENT_NODE) {
                        Element fieldElement = (Element) nestedType;
                        nestedTypes[j] = types.get(fieldElement.getAttribute("name"));
                    }
                }
                CompositeType type = new CompositeType(name, nestedTypes);
                types.put(type.getName(), type);
            }
        }
    }

    public void parseEnumTypes(Document doc, Map<String, Type> types){
        NodeList enumsList = doc.getElementsByTagName("enum");
        for (int i = 0; i < enumsList.getLength(); i++) {
            Node node = enumsList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String name = element.getAttribute("name");
                NodeList valueList = element.getElementsByTagName("validValue");
                Set<Character> validValues = new HashSet<>();
                for (int j = 0; j < valueList.getLength(); j++) {
                    Node valueNode = valueList.item(j);
                    if (valueNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element valueElement = (Element) valueNode;
                        validValues.add(valueElement.getTextContent().charAt(0));
                    }
                }
                EnumType type = new EnumType(name, validValues);
                types.put(type.getName(), type);
            }
        }
    }
}
