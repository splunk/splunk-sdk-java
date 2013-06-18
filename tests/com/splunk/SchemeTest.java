package com.splunk;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: fross
 * Date: 6/18/13
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class SchemeTest {

    public static void removeBlankTextNodes(Element node) {
        NodeList children = node.getChildNodes();

        // Iterate backwards through the collection since we're going to be removing elements
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child instanceof Text && ((Text)child).getData().trim().length() == 0) {
                node.removeChild(child);
            }
            else if (child instanceof Element) {
                removeBlankTextNodes((Element) child);
            }
        }
    }

    public void assertXmlEqual(Node a, Node b) {
        try {
            Assert.assertTrue(a.isEqualNode(b));
        } catch (AssertionError e) {
            NodeList aChildren = a.getChildNodes();
            NodeList bChildren = b.getChildNodes();
            if (aChildren.getLength() != bChildren.getLength()) {
                throw new AssertionError("Node " + a.getNodeName() + " had " + aChildren.getLength() +
                        " children; " + b.getNodeName() + " had " + bChildren.getLength());
            }
            for (int i = 0; i < aChildren.getLength(); i++) {
                assertXmlEqual(aChildren.item(i), bChildren.item(i));
            }
        }
    }

    public void assertXmlEqual(Document a, Document b) {
        removeBlankTextNodes(a.getDocumentElement());
        removeBlankTextNodes(b.getDocumentElement());
        a.normalizeDocument();
        b.normalizeDocument();

        assertXmlEqual((Node) a, (Node) b);
    }

    public Document resourceToXmlDocument(String path) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new AssertionError("Parser configuration failed: " + e.toString());
        }

        InputStream resource = SDKTestCase.openResource(path);
        try {
            Document doc = documentBuilder.parse(resource);
            return doc;
        } catch (SAXException e) {
            throw new AssertionError("Could not parse XML file at " + path);
        } catch (IOException e) {
            throw new AssertionError("Could not read XML file at " + path);
        }
    }

    public Document stringToXmlDocument(String xml) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new AssertionError("Could not configure parser: " + e.toString());
        }

        Document generatedDoc = null;
        try {
            generatedDoc = documentBuilder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            return generatedDoc;
        } catch (SAXException e) {
            e.printStackTrace();
            throw new AssertionError("Error parsing XML passed to function: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError("Error reading XML passed to function: " + e.toString());
        }
    }

    @Test
    public void testSchemeGenerationWithDefaults() throws TransformerException, ParserConfigurationException {
        // Generate a scheme with as many defaults in place as possible.
        Scheme scheme = new Scheme("abcd", "쎼 and 쎶 and <&> für");
        String generatedXml = scheme.toXML();

        Document generatedDocument = stringToXmlDocument(generatedXml);
        Document expectedDocument = resourceToXmlDocument("data/scheme_with_defaults.xml");

        assertXmlEqual(expectedDocument, generatedDocument);
    }

    @Test
    public void testSchemeGeneration() throws TransformerException, ParserConfigurationException {
        Scheme scheme = new Scheme("abcd", "쎼 and 쎶 and <&> für");
        scheme.setStreamingMode(Scheme.StreamingMode.SIMPLE);
        scheme.setUseExternalValidation(false);
        scheme.setUseSingleInstance(true);

        // Add arguments

        String generatedXml = scheme.toXML();

        Document generatedDocument = stringToXmlDocument(generatedXml);
        Document expectedDocument = resourceToXmlDocument("data/scheme_without_defaults.xml");
    }
}
