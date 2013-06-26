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
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class ModularInputComponentClassTest {

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

    public void assertXmlEqual(Document a, Document b) throws TransformerException, ParserConfigurationException {
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
            throw new AssertionError("Parser configuration failed: " + e.toString(), e);
        }

        InputStream resource = SDKTestCase.openResource(path);
        try {
            Document doc = documentBuilder.parse(resource);
            return doc;
        } catch (SAXException e) {
            throw new AssertionError("Could not parse XML file at " + path, e);
        } catch (IOException e) {
            throw new AssertionError("Could not read XML file at " + path, e);
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
            throw new AssertionError("Error parsing XML passed to function: " + e.toString(), e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError("Error reading XML passed to function: " + e.toString(), e);
        }
    }

    @Test
    public void testSchemeGenerationWithDefaults() throws TransformerException, ParserConfigurationException {
        // Generate a scheme with as many defaults in place as possible.
        Scheme scheme = new Scheme("abcd");

        Document generatedDocument = scheme.toXml();
        Document expectedDocument = resourceToXmlDocument("modularinput/data/scheme_with_defaults.xml");

        assertXmlEqual(expectedDocument, generatedDocument);
    }

    @Test
    public void testSchemeGeneration() throws TransformerException, ParserConfigurationException {
        Scheme scheme = new Scheme("abcd");
        scheme.setDescription("쎼 and 쎶 and <&> für");
        scheme.setStreamingMode(Scheme.StreamingMode.SIMPLE);
        scheme.setUseExternalValidation(false);
        scheme.setUseSingleInstance(true);

        Argument arg1 = new Argument("arg1");
        scheme.addArgument(arg1);

        Argument arg2 = new Argument("arg2");
        arg2.setDescription("쎼 and 쎶 and <&> für");
        arg2.setDataType(Argument.DataType.NUMBER);
        arg2.setRequiredOnCreate(true);
        arg2.setRequiredOnEdit(true);
        arg2.setValidation("is_pos_int('some_name')");
        scheme.addArgument(arg2);

        Document generatedDocument = scheme.toXml();
        Document expectedDocument = resourceToXmlDocument("modularinput/data/scheme_without_defaults.xml");

        assertXmlEqual(expectedDocument, generatedDocument);
    }

    public static class StringBufferOutputStream extends OutputStream
    {
        private StringBuffer textBuffer = new StringBuffer();

        public StringBufferOutputStream()
        {
            super();
        }

        public void write(int b) throws IOException
        {
            char a = (char)b;
            textBuffer.append(a);
        }

        public String toString()
        {
            return textBuffer.toString();
        }

        public void clear()
        {
            textBuffer.delete(0, textBuffer.length());
        }
    }

    public String nodeToXml(Node node) throws TransformerException, ParserConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        Document doc = node.getOwnerDocument();

        DOMSource source = new DOMSource(doc);
        StringBufferOutputStream buffer = new StringBufferOutputStream();
        StreamResult result = new StreamResult(buffer);
        transformer.transform(source, result);
        String xml = result.getOutputStream().toString();
        return xml;
    }

    @Test
    public void testArgumentGenerationWithDefaults() throws ParserConfigurationException, TransformerException {
        Argument argument = new Argument("some_name");

        Document generatedDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        argument.addToDocument(generatedDoc, generatedDoc);

        Document expectedDoc = resourceToXmlDocument("modularinput/data/argument_with_defaults.xml");

        assertXmlEqual(expectedDoc, generatedDoc);
    }

    @Test
    public void testArgumentGeneration() throws ParserConfigurationException, TransformerException {
        Argument argument = new Argument("some_name");
        argument.setDescription("쎼 and 쎶 and <&> für");
        argument.setDataType(Argument.DataType.BOOLEAN);
        argument.setValidation("is_pos_int('some_name')");
        argument.setRequiredOnEdit(true);
        argument.setRequiredOnCreate(true);

        Document generatedDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        argument.addToDocument(generatedDoc, generatedDoc);

        Document expectedDoc = resourceToXmlDocument("modularinput/data/argument_without_defaults.xml");

        assertXmlEqual(expectedDoc, generatedDoc);
    }

    @Test
    public void testInputDefinitionWithZeroInputs() throws IOException, SAXException, ParserConfigurationException,
            MalformedDataException {
        InputDefinition expectedDefinition = new InputDefinition();
        expectedDefinition.setServerHost("tiny");
        expectedDefinition.setServerUri("https://127.0.0.1:8089");
        expectedDefinition.setCheckpointDir("/some/dir");
        expectedDefinition.setSessionKey("123102983109283019283");

        InputStream stream = SDKTestCase.openResource("modularinput/data/conf_with_0_inputs.xml");
        InputDefinition foundDefinition = InputDefinition.parseDefinition(stream);

        Assert.assertTrue(expectedDefinition.equals(foundDefinition));
    }

    @Test
    public void testInputDefinitionWithThreeInputs() throws ParserConfigurationException, SAXException,
            MalformedDataException, IOException {
        InputDefinition expectedDefinition = new InputDefinition();
        expectedDefinition.setServerHost("tiny");
        expectedDefinition.setServerUri("https://127.0.0.1:8089");
        expectedDefinition.setCheckpointDir("/some/dir");
        expectedDefinition.setSessionKey("123102983109283019283");

        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new SingleValueParameter("param1", "value1"));
        parameters.add(new SingleValueParameter("param2", "value2"));
        parameters.add(new SingleValueParameter("disabled", "0"));
        parameters.add(new SingleValueParameter("index", "default"));
        expectedDefinition.addInput("foobar://aaa", parameters);

        parameters = new ArrayList<Parameter>();
        parameters.add(new SingleValueParameter("param1", "value11"));
        parameters.add(new SingleValueParameter("param2", "value22"));
        parameters.add(new SingleValueParameter("disabled", "0"));
        parameters.add(new SingleValueParameter("index", "default"));
        parameters.add(new MultiValueParameter("multiValue") {{
            appendValue("value1");;
            appendValue("value2");
        }});
        parameters.add(new MultiValueParameter("multiValue2") {{
            appendValue("value3");;
            appendValue("value4");
        }});
        expectedDefinition.addInput("foobar://bbb", parameters);

        InputStream stream = SDKTestCase.openResource("modularinput/data/conf_with_2_inputs.xml");
        InputDefinition foundDefinition = InputDefinition.parseDefinition(stream);

        Assert.assertTrue(expectedDefinition.equals(foundDefinition));
    }

    @Test
    public void testInputDefinitionWithMalformedXml() throws ParserConfigurationException, SAXException, IOException {
        try {
            InputStream stream = SDKTestCase.openResource("modularinput/data/conf_with_invalid_inputs.xml");
            InputDefinition foundDefinition = InputDefinition.parseDefinition(stream);
        } catch (MalformedDataException e) {
            Assert.assertTrue(true);
            return;
        }
        Assert.fail();
    }

    @Test
    public void testValidationParser() throws ParserConfigurationException, SAXException, MalformedDataException,
            IOException {
        InputStream stream = SDKTestCase.openResource("modularinput/data/validation.xml");
        ValidationDefinition found = ValidationDefinition.parseDefinition(stream);

        ValidationDefinition expected = new ValidationDefinition();
        expected.setServerHost("tiny");
        expected.setServerUri("https://127.0.0.1:8089");
        expected.setCheckpointDir("/opt/splunk/var/lib/splunk/modinputs");
        expected.setSessionKey("123102983109283019283");
        expected.setName("aaa");
        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new SingleValueParameter("param1", "value1"));
        parameters.add(new SingleValueParameter("param2", "value2"));
        parameters.add(new SingleValueParameter("disabled", "0"));
        parameters.add(new SingleValueParameter("index", "default"));
        MultiValueParameter mvp = new MultiValueParameter("multiValue");
        mvp.appendValue("value1");
        mvp.appendValue("value2");
        parameters.add(mvp);
        mvp = new MultiValueParameter("multiValue2");
        mvp.appendValue("value3");
        mvp.appendValue("value4");
        parameters.add(mvp);
        expected.setParameters(parameters);

        Assert.assertTrue(expected.equals(found));
    }

    @Test
    public void testEventWithoutEnoughFields() throws XMLStreamException {
        StringBufferOutputStream sb = new StringBufferOutputStream();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sb);

        Event event = new Event();

        try {
            event.writeTo(writer);
        } catch (MalformedDataException e) {
            Assert.assertTrue(true);
            return;
        }
        Assert.fail();
    }

    @Test
    public void testMinimalEvent() throws XMLStreamException, MalformedDataException, TransformerException,
            ParserConfigurationException {
        StringBufferOutputStream sb = new StringBufferOutputStream();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sb);

        Event event = new Event();
        event.setTime(new Date(1372187084000L));
        event.setStanza("fubar");
        event.setData("This is a test of the emergency broadcast system.");
        event.writeTo(writer);
        Document found = stringToXmlDocument(sb.toString());

        Document expected = resourceToXmlDocument("modularinput/data/event_minimal.xml");

        assertXmlEqual(expected, found);
    }

    @Test
    public void testMaximalEvent() throws MalformedDataException, XMLStreamException, TransformerException,
            ParserConfigurationException {
        StringBufferOutputStream sb = new StringBufferOutputStream();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sb);

        Event event = new Event();
        event.setTime(new Date(1372274622493L));
        event.setStanza("fubar");
        event.setData("This is a test of the emergency broadcast system.");
        event.setHost("localhost");
        event.setIndex("main");
        event.setSource("hilda");
        event.setSourceType("misc");
        event.setDone(true);
        event.setUnbroken(true);
        event.writeTo(writer);
        Document found = stringToXmlDocument(sb.toString());

        Document expected = resourceToXmlDocument("modularinput/data/event_maximal.xml");

        assertXmlEqual(expected, found);
    }

    @Test
    public void testEventWriterWithMaximalEvents() throws XMLStreamException, TransformerException,
            ParserConfigurationException, MalformedDataException {
        StringBufferOutputStream out = new StringBufferOutputStream();
        StringBufferOutputStream err = new StringBufferOutputStream();

        EventWriter ew = new EventWriter(out, err);

        Event event = new Event();
        event.setTime(new Date(1372275124466L));
        event.setStanza("fubar");
        event.setData("This is a test of the emergency broadcast system.");
        event.setHost("localhost");
        event.setIndex("main");
        event.setSource("hilda");
        event.setSourceType("misc");
        event.setDone(true);
        event.setUnbroken(true);
        ew.writeEvent(event);

        Document found = stringToXmlDocument(out.toString() + "</stream>");
        Document expected = resourceToXmlDocument("modularinput/data/stream_with_one_event.xml");

        assertXmlEqual(expected, found);
        Assert.assertEquals("", err.toString());

        err.clear();

        ew.writeEvent(event);
        ew.close();

        found = stringToXmlDocument(out.toString());
        expected = resourceToXmlDocument("modularinput/data/stream_with_two_events.xml");
        assertXmlEqual(expected, found);
    }

    @Test
    public void testEventWriterWithBadEvent() throws XMLStreamException {
        StringBufferOutputStream out = new StringBufferOutputStream();
        StringBufferOutputStream err = new StringBufferOutputStream();

        EventWriter ew = new EventWriter(out, err);

        Event event = new Event();
        try {
            ew.writeEvent(event);
        } catch (MalformedDataException e) {
            Assert.assertTrue(err.toString().startsWith("WARNING"));
            return;
        }
        Assert.fail();
    }

    @Test
    public void testEventWriterErrorLogging() throws XMLStreamException, IOException {
        StringBufferOutputStream out = new StringBufferOutputStream();
        StringBufferOutputStream err = new StringBufferOutputStream();

        EventWriter ew = new EventWriter(out, err);

        ew.log(Level.SEVERE, "Something happened!");

        Assert.assertEquals("SEVERE Something happened!\n", err.toString());
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
