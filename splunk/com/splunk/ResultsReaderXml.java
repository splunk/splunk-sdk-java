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

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * The {@code ResultsReaderXml} class represents a streaming XML reader for
 * Splunk search results. When a stream from an export search is passed to this
 * reader, it skips any preview events in the stream. If you want to access the
 * preview events, use the {@link MultiResultsReaderXml} class.
 */
public class ResultsReaderXml
    extends ResultsReader {

    private XMLEventReader xmlReader = null;
    private ArrayList<String> fields = new ArrayList<String>();

    /**
     * Class constructor.
     *
     * Constructs a streaming XML reader for the event stream. You should only
     * attempt to parse an XML stream with this reader. If you attempt to parse 
     * a different type of stream, unpredictable results may occur. 
     * <br>
     * The pushback reader modifies export streams to generate non-strict XML 
     * at the beginning of the stream. The streaming reader ignores preview 
     * data, and only extracts finalized data.
     *
     * @param inputStream The XML stream to parse.
     * @throws IOException
     */
    public ResultsReaderXml(InputStream inputStream) throws IOException {
        this(inputStream, false);
    }

    ResultsReaderXml(
            InputStream inputStream,
            boolean isInMultiReader)
            throws IOException {
        super(inputStream, isInMultiReader);
        PushbackReader pushbackReader =
            new PushbackReader(inputStreamReader, 256);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();

        // At initialization, skip everything in the start until we get to the
        // first-non preview data "<results",
        // Push back into the stream an opening <doc> tag, and parse the file.
        // We do this because the XML parser requires a single root element.
        // Below is an example of an input stream, with a single 'results'
        // element. With a stream from an export point, there can be
        // multiple ones.
        //
        //        <?xml version='1.0' encoding='UTF-8'?>
        //        <results preview='0'>
        //        <meta>
        //        <fieldOrder>
        //        <field>series</field>
        //        <field>sum(kb)</field>
        //        </fieldOrder>
        //        </meta>
        //        <messages>
        //        <msg type='DEBUG'>base lispy: [ AND ]</msg>
        //        <msg type='DEBUG'>search context: user='admin', app='search', bs-pathname='/some/path'</msg>
        //        </messages>
        //        <result offset='0'>
        //        <field k='series'>
        //        <value><text>twitter</text></value>
        //        </field>
        //        <field k='sum(kb)'>
        //        <value><text>14372242.758775</text></value>
        //        </field>
        //        </result>
        //        <result offset='1'>
        //        <field k='series'>
        //        <value><text>splunkd</text></value>
        //        </field>
        //        <field k='sum(kb)'>
        //        <value><text>267802.333926</text></value>
        //        </field>
        //        </result>
        //        </results>

        String findToken = "<results";
        String accumulator = "";
        int index = 0;
        while (true) {
            int data = pushbackReader.read();
            if (data < 0) return;
            accumulator = accumulator + (char)data;
            if ("<results".equals(accumulator)) {
                    String putBackString = "<doc>" + findToken;
                    char putBackBytes[] = putBackString.toCharArray();
                    pushbackReader.unread(putBackBytes);
                    break;
            } else if (!findToken.startsWith(accumulator)) {
                accumulator = "";
            }
        }

        // Attach the XML reader to the stream
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
        try {
            xmlReader = inputFactory.createXMLEventReader(pushbackReader);
            finishInitialization();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    @Override public void close() throws IOException {
        if (xmlReader != null) {
            try {
                xmlReader.close();
            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        }
        xmlReader = null;

        super.close();
    }

    /** {@inheritDoc} */
    public boolean isPreview() {
        return isPreview;
    }

    /** {@inheritDoc} */
    public Collection<String> getFields() {
        return fields;
    }

    @Override Event getNextEventInCurrentSet() throws IOException {
        // Handle empty stream or other cases where xmlReader is
        // not constructed.
        if (xmlReader == null) {
            return null;
        }
        try {
            Event event = null;
            XMLEvent xmlEvent = readToStartOfElementAtSameLevelWithName("result");
            if (xmlEvent != null) {
                event = getResultKVPairs();
            }
            return event;
        } catch (XMLStreamException e) {
           throw new RuntimeException(e);
        }
    }

    // Reads the preview flag and field name list, and position in the middle of
    // the result element for reading actual results later.
    // Return value indicates whether the next 'results' element is found.
    boolean readIntoNextResultsElement()
            throws XMLStreamException, IOException {
        XMLEvent xmlEvent = null;
        try {
            xmlEvent = readToStartOfElementWithName("results");
        } catch (XMLStreamException e) {
            // Because we cannot stuff trailing information into the stream,
            // we expect an XMLStreamingException that contains our
            // corresponding end-of-document </doc> that we injected into the
            // front of the stream. Any other exception we rethrow.
            if (!(e.getMessage().contains("</doc>")
                || e.getMessage().contains("XML document structures must start and end within the same entity.")
                || e.getMessage().contains("was expecting a close tag for element <doc>"))) {
                throw e;
            }
        }
        if (xmlEvent == null) {
            return false;
        }

        if (xmlEvent != null &&
            xmlEvent.asStartElement()
                .getAttributeByName(QName.valueOf("preview"))
                .getValue()
                .equals("0") ){
            isPreview = false;
        } else {
            isPreview = true;
        }

        // Read <meta> element.
        final String meta = "meta";
        if (readToStartOfElementAtSameLevelWithName(meta) != null) {
            readFieldOrderElement();
            readToEndElementWithName(meta);
        }
        return true;
    }

    XMLEvent readToStartOfElementWithName(String elementName)
        throws XMLStreamException {
        while (xmlReader.hasNext()) {
            XMLEvent xmlEvent = xmlReader.nextEvent();
            int eType = xmlEvent.getEventType();
            if (eType != XMLStreamConstants.START_ELEMENT){
                continue;
            }

            StartElement startElement = xmlEvent.asStartElement();
            if(startElement
                    .getName()
                    .getLocalPart()
                    .equals(elementName)){
                return xmlEvent;
            }
        }
        return null;
    }

    void readToEndElementWithName(String elementName) throws XMLStreamException {
        XMLEvent xmlEvent;
        int eType;

        while (xmlReader.hasNext()) {
            xmlEvent = xmlReader.nextEvent();
            eType = xmlEvent.getEventType();
            switch (eType) {
                case XMLStreamConstants.START_ELEMENT:
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (xmlEvent.asEndElement()
                            .getName()
                            .getLocalPart()
                            .equals(elementName)) {
                        return;
                    }
                    break;
                default:
                    break;
            }
        }

        throw new RuntimeException("End tag of " + elementName + " not found.");
    }

    /**
     * Reads to the next specified start element at the same level. The reader 
     * stops past that element if it is found. Otherwise, the reader stops 
     * before the end element of the current level.
     * 
     * @param elementName The name of the start element.
     * @return  The start element, or {@code null} if not found.
     * @throws XMLStreamException
     */
    XMLEvent readToStartOfElementAtSameLevelWithName(String elementName)
            throws XMLStreamException {
        XMLEvent xmlEvent;
        int eType;
        int level = 0;
        while (xmlReader.hasNext()) {
            xmlEvent = xmlReader.peek();
            eType = xmlEvent.getEventType();
            switch (eType) {
                case XMLStreamConstants.START_ELEMENT:
                    if (level++ > 0){
                        break;
                    }
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement
                            .getName()
                            .getLocalPart()
                            .equals(elementName)) {
                        xmlReader.nextEvent();
                        return xmlEvent;
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (level-- == 0) {
                        return  null;
                    }
                    break;
                default:
                    break;
            }
            xmlReader.nextEvent();
        }

        throw new RuntimeException("Parent end element not found:" + elementName);
    }

    // At the end, move off the end element of 'fieldOrder'
    private void readFieldOrderElement()
            throws IOException, XMLStreamException {
        XMLEvent xmlEvent;
        int eType;
        int level = 0;

        if (readToStartOfElementAtSameLevelWithName("fieldOrder") == null)
            return;

        while (xmlReader.hasNext()) {
            xmlEvent = xmlReader.nextEvent();
            eType = xmlEvent.getEventType();
            switch (eType) {
                case XMLStreamConstants.START_ELEMENT:
                    level++;
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (xmlEvent.asEndElement()
                            .getName()
                            .getLocalPart()
                            .equals("fieldOrder")) {
                        return;
                    }
                    level--;
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (level == 1) {
                        fields.add(xmlEvent.asCharacters().getData());
                    }
                    break;
                default:
                    break;
            }
        }

        throw new RuntimeException("End tag of fieldOrder not found.");
    }

    // At the end, move off the end tag of 'result'
    private Event getResultKVPairs()
            throws IOException, XMLStreamException {
        
        Event returnData = new Event();
        XMLEvent xmlEvent;
        int eType;
        String key = null;
        List<String> values = new ArrayList<String>();
        int level = 0;

        // Event results are flat, so extract k/v pairs based on XML indentation
        // level throwing away the uninteresting non-data.

        while (xmlReader.hasNext()) {
            xmlEvent = xmlReader.nextEvent();
            eType = xmlEvent.getEventType();
            switch (eType) {
                case XMLStreamConstants.START_ELEMENT:
                    final StartElement startElement = xmlEvent.asStartElement();
                    @SuppressWarnings("unchecked")
                    Iterator<Attribute> attrIttr =
                        startElement.getAttributes();
                    if (level == 0) {
                        if (attrIttr.hasNext())
                            key =  attrIttr.next().getValue();
                    } else if (level == 1 &&
                            key.equals("_raw") &&
                            startElement
                                .getName()
                                .getLocalPart()
                                .equals("v")) {
                        StringBuilder asString = new StringBuilder();
                        StringWriter asXml = new StringWriter();
                        readSubtree(startElement, asString, asXml);
                        values.add(asString.toString());
                        returnData.putSegmentedRaw(asXml.toString());
                        level--;
                    }
                    level++;
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (xmlEvent.asEndElement()
                            .getName()
                            .getLocalPart()
                            .equals("result"))
                        return returnData;
                    
                    if (--level == 0) {
                        String[] valuesArray = 
                                values.toArray(new String[values.size()]);
                        returnData.putArray(key, valuesArray);
                        
                        key = null;
                        values.clear();
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (level > 1) {
                        values.add(xmlEvent.asCharacters().getData());
                    }
                    break;
                default:
                    break;
            }
        }

        throw new RuntimeException("End tag of 'result' not found.");
    }

    @Override boolean advanceStreamToNextSet() throws IOException {
        // Handle empty stream or other cases where xmlReader is
        // not constructed.
        if (xmlReader == null) {
            return false;
        }
        try {
            return readIntoNextResultsElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            // Invalid xml (<doc> and multiple <results> may results in
            // this exception in the xml reader with JDK 1.7 at:
            // com.sun.org.apache.xerces.internal.impl.XMLEntityScanner.load(XMLEntityScanner.java:1748)
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            // Invalid xml (<doc> and multiple <results> may results in
            // this exception in the xml reader with JDK 1.6 at:
            // com.sun.org.apache.xerces.internal.impl.XMLDocumentFragmentScannerImpl.endEntity(XMLDocumentFragmentScannerImpl.java:904)
            return false;
        }
    }

    /**
     * Read the whole element including those contained in the outer element.
     * @param startElement start element (tag) of the outer element.
     * @param asString output builder that the element's inner-text
     *                 will be appended to, with markup removed and
     *                 characters un-escaped
     * @param asXml    output builder that full xml including markups
     *                 will be appended to. Characters are escaped as
     *                 needed.
     * @throws IOException
     * @throws XMLStreamException
     */
    void readSubtree(
            StartElement startElement,
            StringBuilder asString,
            StringWriter asXml)
            throws IOException, XMLStreamException {
        XMLEventWriter xmlWriter = XMLOutputFactory.newInstance().
                createXMLEventWriter(asXml);
        XMLEvent xmlEvent = startElement;
        int level = 0;
        do {
            xmlWriter.add(xmlEvent);
            int eType = xmlEvent.getEventType();
            switch (eType) {
                case XMLStreamConstants.START_ELEMENT:
                    level++;
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (--level == 0) {
                        xmlWriter.close();
                        return;
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    asString.append(xmlEvent.asCharacters().getData());
                default:
                    break;
            }
            xmlEvent = xmlReader.nextEvent();
        } while (xmlReader.hasNext());
        throw new RuntimeException("Invalid XML format.");
    }
}

