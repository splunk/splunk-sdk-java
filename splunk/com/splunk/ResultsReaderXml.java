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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.PushbackReader;

/**
 * The {@code ResultsReaderXml} class represents a streaming XML reader for 
 * Splunk search results. For export, it skips any previews,
 * which can be read with {@code MultiResultsReaderXml}.
 */
public class ResultsReaderXml
        extends  ResultsReader<ResultsReaderXml> {

    private XMLEventReader xmlReader = null;
    private ArrayList<String> fields = new ArrayList<String>();

    /**
     * Class constructor.
     *
     * Constructs a streaming XML reader for the event stream. You should only
     * attempt to parse an XML stream with the XML reader. Unpredictable results
     * may occur if you use a non-XML stream.
     * <br>
     * The pushback reader tweaks export streams to generate non-strict XML 
     * at the beginning of the stream. The streaming reader ignores preview 
     * data, and only extracts finalized data.
     *
     * @param inputStream The stream to parse.
     * @throws Exception On exception.
     */
    public ResultsReaderXml(InputStream inputStream) throws IOException {
        this(inputStream, false);
    }

    ResultsReaderXml(
            InputStream inputStream,
            boolean isMultiReader)
            throws IOException {
        super(inputStream, isMultiReader);
        PushbackReader pushbackReader =
            new PushbackReader(inputStreamReader, 256);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();

        // At initialization, skip everything in the start until we get to the
        // first-non preview data "<results preview='0'>", and then the first
        // real event data which starts as "<result offset='0'>". Push back
        // into the stream an opening <doc> tag, and parse the file.

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
            readyForIterable();
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
    public Collection<String> getFields(){
        return fields;
    }

    @Override protected Event getNextInner() throws IOException {
        try {
            Event event = null;
            XMLEvent xmlEvent = readIntoElementAtSameLevel("result");
            if (xmlEvent != null) {
                event = getResultKVPairs();
            }
            return event;
        } catch (XMLStreamException e) {
           throw new RuntimeException(e);
        }
    }

    boolean readIntoNextResultsElement()
            throws XMLStreamException, IOException {
        XMLEvent xmlEvent = null;
        try {
            xmlEvent = readToFollowing("results");
        } catch (XMLStreamException e) {
            // Because we cannot stuff trailing information into the stream,
            // we expect an XMLStreamingException that contains our
            // corresponding end-of-document </doc> that we injected into the
            // front of the stream. Any other exception we rethrow.
            if (!(e.getMessage().contains("</doc>") ||
                e.getMessage().contains(
                    "XML document structures must start and end within the same entity."))) {
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

        final String meta = "meta";
        if (readIntoElementAtSameLevel(meta) != null) {
            if (readIntoElementAtSameLevel("fieldOrder") != null) {
                readFields();
            }
            readToEnd(meta);
        }
        return true;
    }

    XMLEvent readToFollowing(String name) throws XMLStreamException {
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
                    .equals(name)){
                return xmlEvent;
            }
        }
        return null;
    }

    void readToEnd(String name) throws XMLStreamException {
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
                            .equals(name)) {
                        return;
                    }
                    break;
                default:
                    break;
            }
        }

        throw new RuntimeException("End tag of " + name + " not found.");
    }

    XMLEvent readIntoElementAtSameLevel(String name) throws XMLStreamException {
        XMLEvent xmlEvent;
        int eType;
        int level = 0;

        while (xmlReader.hasNext()) {
            xmlEvent = xmlReader.nextEvent();
            eType = xmlEvent.getEventType();
            switch (eType) {
                case XMLStreamConstants.START_ELEMENT:
                    if (level++ >  0){
                        continue;
                    }
                    StartElement startElement = xmlEvent.asStartElement();
                    if(startElement
                            .getName()
                            .getLocalPart()
                            .equals(name)){
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
        }

        throw new RuntimeException("Parent end element not found:" + name);
    }

    // At the end, move off the end element of 'fieldOrder'
    private void readFields()
            throws IOException, XMLStreamException {
        XMLEvent xmlEvent;
        int eType;
        int level = 0;

        // Event results are flat, so extract k/v pairs based on XML indentation
        // level throwing away the uninteresting non-data.

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
                   @SuppressWarnings("unchecked")
                    Iterator<Attribute> attrIttr =
                        xmlEvent.asStartElement().getAttributes();
                    if (level == 0) {
                        if (attrIttr.hasNext())
                            key =  attrIttr.next().getValue();
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

    @Override boolean moveToNextSetStreamPosition() throws IOException{
        try {
            return readIntoNextResultsElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            // Invalid xml (<doc> and multiple <results> may results in
            // this exception in the xml reader at:
            // com.sun.org.apache.xerces.internal.impl.XMLEntityScanner.load(XMLEntityScanner.java:1748)
            return false;
        }
    }
}

