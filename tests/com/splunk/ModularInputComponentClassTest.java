package com.splunk;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import sun.management.jmxremote.SingleEntryRegistry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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

    public void assertXmlEqual(Node a, Node b) throws TransformerException, ParserConfigurationException {
        try {
            Assert.assertTrue(a.isEqualNode(b));
        } catch (AssertionError e) {
            NodeList aChildren = a.getChildNodes();
            NodeList bChildren = b.getChildNodes();
            if (aChildren.getLength() != bChildren.getLength()) {
                throw new AssertionError("Expected node: \n" +
                        nodeToXml(a) + "\n" +
                        "Generated node:\n" +
                        nodeToXml(b));
            }
            for (int i = 0; i < aChildren.getLength(); i++) {
                assertXmlEqual(aChildren.item(i), bChildren.item(i));
            }
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
        Scheme scheme = new Scheme("abcd");

        Document generatedDocument = scheme.toXml();
        Document expectedDocument = resourceToXmlDocument("data/modular_input/scheme_with_defaults.xml");

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
        Document expectedDocument = resourceToXmlDocument("data/modular_input/scheme_without_defaults.xml");

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

        Document expectedDoc = resourceToXmlDocument("data/modular_input/argument_with_defaults.xml");

        assertXmlEqual(expectedDoc, generatedDoc);
    }

    @Test
    public void testArgumentGeneration() throws ParserConfigurationException, TransformerException {
        Argument argument = new Argument("some_name");
        argument.setDescription("쎼 and 쎶 and <&> für");
        argument.setDataType(Argument.DataType.NUMBER);
        argument.setRequiredOnCreate(true);
        argument.setRequiredOnEdit(true);
        argument.setValidation("is_pos_int('some_name')");

        Document generatedDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        argument.addToDocument(generatedDoc, generatedDoc);

        Document expectedDoc = resourceToXmlDocument("data/modular_input/argument_without_defaults.xml");

        assertXmlEqual(expectedDoc, generatedDoc);
    }

    @Test
    public void testInputDefinitionWithZeroInputs() throws IOException, SAXException, ParserConfigurationException,
            MalformedDataException {
        InputDefinition expectedDefinition = new InputDefinition();
        expectedDefinition.setServerHost("tiny");
        expectedDefinition.setServerUri("https://127.0.0.1:8089");
        expectedDefinition.setCheckpointDir("/opt/splunk/var/lib/splunk/modinputs");
        expectedDefinition.setSessionKey("123102983109283019283");

        InputStream stream = SDKTestCase.openResource("data/modular_input/conf_with_0_inputs.xml");
        InputDefinition foundDefinition = InputDefinition.parseDefinition(stream);

        Assert.assertTrue(expectedDefinition.equals(foundDefinition));
    }

    @Test
    public void testInputDefinitionWithThreeInputs() throws ParserConfigurationException, SAXException,
            MalformedDataException, IOException {
        InputDefinition expectedDefinition = new InputDefinition();
        expectedDefinition.setServerHost("tiny");
        expectedDefinition.setServerUri("https://127.0.0.1:8089");
        expectedDefinition.setCheckpointDir("/opt/splunk/var/lib/splunk/modinputs");
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

        InputStream stream = SDKTestCase.openResource("data/modular_input/conf_with_2_inputs.xml");
        InputDefinition foundDefinition = InputDefinition.parseDefinition(stream);

        Assert.assertTrue(expectedDefinition.equals(foundDefinition));
    }

    @Test
    public void testInputDefinitionWithMalformedXml() throws ParserConfigurationException, SAXException, IOException {
        try {
            InputStream stream = SDKTestCase.openResource("data/modular_input/conf_with_invalid_inputs.xml");
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
        InputStream stream = SDKTestCase.openResource("data/modular_input/validation.xml");
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
}
