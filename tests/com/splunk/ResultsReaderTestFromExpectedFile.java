/*
 * Copyright 2012 Splunk, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.splunk;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import static junit.framework.TestCase.*;

/**
 * Test the parsing of XML from results endpoints.
 *
 * All the raw XML to parse is in the data/results/ directory, and the expected
 * behavior is specified in the data/resultsreader_test_data.json file.
 */
@RunWith(Parameterized.class)
public class ResultsReaderTestFromExpectedFile {
    private static Gson reader = new Gson();
    private static Map<String, Object> expectedData =
        reader.fromJson(
            SDKTestCase.streamToString(
                SDKTestCase.openResource(
                    "data/resultsreader_test_data.json")),
                Map.class);

    private Map<String, Object> expectedResultsSet;
    private String version;
    private String testName;
    private InputStream xmlStream;

    public ResultsReaderTestFromExpectedFile(String version, String testName) {
        this.version = version;
        this.testName = testName;
        this.xmlStream = SDKTestCase.openResource(
                "data/results/" + version + "/" + testName + ".xml");
        Map<String, Object> versionData =
                (Map<String, Object>)expectedData.get(version);
        this.expectedResultsSet =
                (Map<String, Object>)versionData.get(testName);
    }

    @Parameterized.Parameters(name="{1} from version {0}")
    public static Collection<Object[]> testCases() {
        Collection<Object[]> cases = new ArrayList<Object[]>();
        for (String version : (Set<String>)expectedData.keySet()) {
            Map<String, Object> casesForThisVersion =
                    (Map<String, Object>)expectedData.get(version);
            for (String testName : (Set<String>)casesForThisVersion.keySet()) {
                cases.add(new Object[] {version, testName});
            }
        }
        return cases;
    }

    @Test
    public void testResultsReader() throws IOException, XMLStreamException {
        // TODO: This test fails on Windows due to XML attributes being out of order
        ResultsReaderXml resultsReader = new ResultsReaderXml(this.xmlStream);
        List<Map<String, Object>> expectedEvents =
            (List<Map<String, Object>>)this.expectedResultsSet.get("results");
        verifyResultsReader(resultsReader, expectedEvents);
    }

    static String xmlToString(String originalXml) throws Exception
    {
        DocumentBuilderFactory fctr = DocumentBuilderFactory.newInstance();
        DocumentBuilder bldr = fctr.newDocumentBuilder();
        InputSource insrc = new InputSource(new StringReader(originalXml));
        Document xml = bldr.parse(insrc);
        xml.normalize();
        
        DOMSource domSource = new DOMSource(xml);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        
        return writer.toString();
    }

    static void verifyResultsReader(
            ResultsReaderXml resultsReader,
            List<Map<String, Object>> expectedEvents)
            throws IOException, XMLStreamException {
        for (Map<String, Object> expectedEvent : expectedEvents) {
            Event foundEvent = resultsReader.getNextEvent();
            assertNotNull(
                    "Did not parse as many events from the XML as expected.",
                    foundEvent);
            if (foundEvent.containsKey("_raw")) {
                final String segmentedRaw = foundEvent.getSegmentedRaw();
                final String keySegmentedRaw = "RAW_XML";
                try {
                    final String parsedExpected = xmlToString((String)expectedEvent.get(keySegmentedRaw));
                    final String parsedReceived = xmlToString(segmentedRaw);
					assertEquals(parsedExpected, parsedReceived);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					assertFalse(true);
				}
                verifyXml(segmentedRaw);
            }
            Map<String, Object> expectedFields =
                    (Map<String,Object>)expectedEvent.get("fields");
            Set<String> expectedKeys = expectedFields.keySet();
            assertEquals(expectedKeys, foundEvent.keySet());
            for (String key : expectedFields.keySet()) {
                assertTrue(foundEvent.containsKey(key));
                if (expectedFields.get(key) instanceof List) {
                    assertEquals(
                            expectedFields.get(key),
                            Arrays.asList(foundEvent.getArray(key)));
                } else {
                    assertEquals(expectedFields.get(key), foundEvent.get(key));
                }
            }
        }
        assertNull(resultsReader.getNextEvent());
    }

    // Verify XML by round-tripping.
    static private void verifyXml(String xml)
            throws XMLStreamException, IOException {
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(xml.getBytes());
        XMLEventReader reader = XMLInputFactory.newInstance().
                createXMLEventReader(byteArrayInputStream);

        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();
        XMLEventWriter writer = XMLOutputFactory.newInstance().
                createXMLEventWriter(byteArrayOutputStream);
        writer.add(reader);
        writer.close();
        
        String xmlWithDtd = byteArrayOutputStream.toString();
        int lengthOfDtd = xmlWithDtd.indexOf(">") + 1;
        String dtd = xmlWithDtd.substring(0, lengthOfDtd);
        assertTrue("Expected xml to have a DTD", dtd.startsWith("<?"));
        // Remove the XML DTD
        String xmlWithoutDtd = xmlWithDtd.substring(lengthOfDtd);

        assertEquals(xml, xmlWithoutDtd);
        byteArrayInputStream.close();
    }
}
