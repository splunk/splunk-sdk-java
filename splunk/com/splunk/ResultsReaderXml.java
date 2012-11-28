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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.PushbackReader;

/**
 * The {@code ResultsReaderXml} class represents a streaming XML reader for 
 * Splunk search results.
 */
public class ResultsReaderXml extends ResultsReader {

    private XMLEventReader xmlReader = null;

    /**
     * Class constructor.
     *
     * Constructs a streaming XML reader for the event stream. You should only
     * attempt to parse an XML stream with the XML reader. Unpredictable results
     * may occur if you use a non-XML stream.
     *
     * The pushback reader tweaks export streams, which generates non-strict XML 
     * at the beginning of the stream. The streaming reader ignores preview 
     * data, and only extracts finalized data.
     *
     * @param inputStream The stream to be parsed.
     * @throws Exception On exception.
     */
    public ResultsReaderXml(InputStream inputStream) throws IOException {
        super(inputStream);
        PushbackReader pushbackReader =
            new PushbackReader(inputStreamReader, 256);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();

        // At initialization, skip everything in the start until we get to the
        // first-non preview data "<results preview='0'>", and then the first
        // real event data which starts as "<result offset='0'>". Push back
        // into the stream an opening <doc> tag, and parse the file.

        ArrayList<String> findInOrder = new ArrayList<String>();
        findInOrder.add(0, "<results preview='0'>");
        findInOrder.add(1, "<result offset=");
        String accumulator = "";
        int index = 0;
        while (true) {
            int data = pushbackReader.read();
            if (data < 0) return;
            accumulator = accumulator + (char)data;
            if (findInOrder.get(index).equals(accumulator)) {
                if (index == findInOrder.size()-1) {
                    String putBackString = "<doc>" + findInOrder.get(index);
                    char putBackBytes[] = putBackString.toCharArray();
                    pushbackReader.unread(putBackBytes);
                    break;
                }
                else {
                    index += 1;
                }
            } else if (!findInOrder.get(index).startsWith(accumulator)) {
                accumulator = "";
            }
        }

        // Attach the XML reader to the stream
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
        try {
            xmlReader = inputFactory.createXMLEventReader(pushbackReader);
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
    @Override public HashMap<String, String> getNextEvent() throws IOException {
        XMLEvent xmlEvent;
        int eType;

        if (xmlReader == null)
            return null;

        try {
            while (xmlReader.hasNext()) {
                xmlEvent = xmlReader.nextEvent();
                eType = xmlEvent.getEventType();
                while (eType != XMLStreamConstants.END_DOCUMENT) {
                    if (eType == XMLStreamConstants.START_ELEMENT &&
                            xmlEvent.asStartElement()
                                .getName()
                                .getLocalPart()
                                .equals("result")) {
                        return getResultKVPairs();
                    }
                    if (xmlReader.hasNext()) {
                        xmlEvent = xmlReader.nextEvent();
                        eType = xmlEvent.getEventType();
                    }
                }
            }
        }
        catch (XMLStreamException e) {
            // Because we cannot stuff trailing information into the stream,
            // we expect an XMLStreamingException that contains our
            // corresponding end-of-document </doc> that we injected into the
            // front of the stream. Any other exception we rethrow.
            if (e.getMessage().contains("</doc>")) {
                return null;
            }
            
            throw new RuntimeException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override public Map<String, String[]> getNextEvent2() throws IOException {
        // FIXME
        throw new UnsupportedOperationException();
    }

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
        return returnData;
    }
}
