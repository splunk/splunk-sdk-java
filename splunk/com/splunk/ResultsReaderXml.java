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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.PushbackReader;

public class ResultsReaderXml extends ResultsReader {

    private XMLEventReader xmlReader = null;

    /**
     * Class Constructor.
     *
     * Construct a streaming XML reader for the event stream. One should only
     * attempt to parse an XML stream with the XML reader. Using a non-XML
     * stream will yield unpredictable results.
     *
     * Note we use the pushback reader to tweak export streams which generates
     * non-strict XML at the beginning of the stream. The streaming reader
     * ignores preview data, and only extracts finalized data.
     *
     * @param inputStream The stream to be parsed.
     * @throws Exception On exception.
     */
    public ResultsReaderXml(InputStream inputStream) throws Exception {
        super(inputStream);
        PushbackReader pushbackReader =
            new PushbackReader(inputStreamReader, 256);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();

        // at initialization, skip everything in the start until we get to the
        // first-non preview data "<results preview='0'>", and then the first
        // real event data which starts as "<result offset='0'>"
        // add opening <doc> and parse the file, we need to be careful to handle
        // the end of the stream exception of a missing </doc> tag.

        ArrayList<String> findInOrder = new ArrayList<String>();
        findInOrder.add(0, "<results preview='0'>");
        findInOrder.add(1, "<result offset='0'>");
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

        // now attach the XML reader to the stream
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
        xmlReader = inputFactory.createXMLEventReader(pushbackReader);
    }

    /** {@inheritDoc} */
    @Override public void close() throws Exception {
        super.close();
        if (xmlReader != null) xmlReader.close();
        xmlReader = null;
    }

    /** {@inheritDoc} */
    @Override public HashMap<String, String> getNextEvent() throws Exception {
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
        catch (XMLStreamException exception) {
            // Because we cannot stuff trailing information into the stream,
            // we expect an XMLStreamingException that contains our
            // corresponding end-of-document </doc> that we injected into the
            // front of the stream. Any other exception we rethrow.
            if (exception.getMessage().contains("</doc>")) {
                return null;
            }
            throw exception;
        }
        return null;
    }

    private HashMap<String, String> getResultKVPairs() throws Exception {
        HashMap<String, String> returnData = new HashMap<String, String>();
        XMLEvent xmlEvent;
        int eType;
        String key = null;
        StringBuilder value = new StringBuilder();
        int level = 0;

        // event results are flat, extract k/v pairs based on XML indentation
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
                        returnData.put(key, value.toString());
                        value.setLength(0); //clear
                        key = null;
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (level > 1) {
                        if (value.length() > 0) value.append("\n");
                        value.append(xmlEvent.asCharacters().getData());
                    }
                    break;
                default:
                    break;
            }
        }
        return returnData;
    }
}
