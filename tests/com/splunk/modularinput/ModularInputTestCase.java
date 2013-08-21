package com.splunk.modularinput;

import com.splunk.SDKTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class ModularInputTestCase {
    /**
     * XML parsing may introduce text nodes containing only white space between elements. These are purely formatting
     * in our case, so we want to eliminate them.
     *
     * @param node a org.w3c.dom.Node object containing XML.
     */
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

    /**
     * Assert whether two org.w3c.dom.Node objects contain identical XML, ignoring whitespace. If they do not match,
     * assertXmlEqual will recursively descend through the element tree to find where they do not match.
     *
     * @param expected an org.w3c.dom.Node object containing the expected XML document.
     * @param found an org.w3c.dom.Node object containing the XML document actually produced.
     * @throws javax.xml.transform.TransformerException
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    public void assertXmlEqual(Node expected, Node found) throws TransformerException, ParserConfigurationException {
        try {
            Assert.assertTrue(expected.isEqualNode(found));
        } catch (AssertionError e) {
            NodeList expectedChildren = expected.getChildNodes();
            NodeList foundChildren = found.getChildNodes();
            if (expectedChildren.getLength() != foundChildren.getLength()) {
                throw new AssertionError("Expected node: \n" +
                        nodeToXml(expected) + "\n" +
                        "Generated node:\n" +
                        nodeToXml(found));
            }
            for (int i = 0; i < expectedChildren.getLength(); i++) {
                assertXmlEqual(expectedChildren.item(i), foundChildren.item(i));
            }
            if (expected.getNodeType() == expected.TEXT_NODE && found.getNodeType() == found.TEXT_NODE) {
                Assert.assertEquals(((Text)expected).getData(), ((Text)found).getData());
            }
            throw new AssertionError("Parents unequal but couldn't find unmatched child in node " + expected.getNodeName());
        }
    }

    /**
     * Assert whether two org.w3c.dom.Document objects contain identical XML, ignoring whitespace. If they do not match,
     * assertXmlEqual will recursively descend through the element tree to find where they do not match.
     *
     * @param expected an org.w3c.dom.Document object containing the expected XML document.
     * @param found an org.w3c.dom.Document object containing the XML document actually produced.
     * @throws javax.xml.transform.TransformerException
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    public void assertXmlEqual(Document expected, Document found) throws TransformerException, ParserConfigurationException {
        removeBlankTextNodes(expected.getDocumentElement());
        removeBlankTextNodes(found.getDocumentElement());
        expected.normalizeDocument();
        found.normalizeDocument();

        assertXmlEqual((Node) expected, (Node) found);
    }

    /**
     * Open a resource from the Splunk SDK for Java project and parse it into an org.w3c.dom.Document object.
     *
     * @param path a path relative to the test directory of the SDK.
     * @return an org.w3c.dom.Document object containing the parsed XML.
     */
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

    /**
     * Parse XML in a string into an org.w3c.dom.Document object.
     *
     * @param xml a String containing XML.
     * @return an org.w3c.dom.Document object.
     */
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

    /**
     * Transform the given org.w3c.dom.Node object into a String containing the corresponding XML.
     *
     * This function is primarily for showing sensible error messages.
     *
     * @param node the org.w3c.dom.Node object to serialize.
     * @return a String containing generated XML.
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    public String nodeToXml(Node node) throws TransformerException, ParserConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        Document doc = node.getOwnerDocument();

        DOMSource source = new DOMSource(doc);
        StringWriter buffer = new StringWriter();
        StreamResult result = new StreamResult(buffer);
        transformer.transform(source, result);
        String xml = buffer.toString();
        return xml;
    }

    /**
     * Tries the known cases supported by XmlUtil.normalizeBoolean, and asserts that it throws an error
     * when passed an unknown case.
     */
    @Test
    public void testNormalizeBoolean() throws MalformedDataException {
        String[] trueValues = new String[] {"true", "t", "TRUE  ", "y", "  YeS", "1", "ON"};
        String[] falseValues = new String[] {"false", "f", "FALSE  ", "   oFF", "no", "0", "n"};
        String[] invalidValues = new String[] {null, "boris", "fal"};

        for (String s : trueValues) {
            Assert.assertTrue(XmlUtil.normalizeBoolean(s));
        }

        for (String s : falseValues) {
            Assert.assertFalse(XmlUtil.normalizeBoolean(s));
        }

        boolean fail;
        for (String s : invalidValues) {
            fail = true;
            try {
                XmlUtil.normalizeBoolean(null);
            } catch (MalformedDataException e) {
                fail = false;
            } finally {
                Assert.assertFalse(fail);
            }
        }
    }

    /**
     * Test the methods on SingleValueParameter to coerce its value to a boolean or various kinds of numbers.
     */
    @Test
    public void testCoercionMethods() throws MalformedDataException {
        Assert.assertEquals(true, new SingleValueParameter("name", "TRuE  ").getBoolean());
        Assert.assertEquals(5,    new SingleValueParameter("name", "5").getInt());
        Assert.assertEquals(27, new SingleValueParameter("name", "27").getLong());
        Assert.assertEquals(5.2, new SingleValueParameter("name", "5.2").getFloat(), 1e-6);
        Assert.assertEquals(5.2, new SingleValueParameter("name", "5.2").getDouble(), 1e-6);
    }
}
