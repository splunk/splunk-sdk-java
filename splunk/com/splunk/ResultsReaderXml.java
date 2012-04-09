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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.stream.*;
import javax.xml.stream.events.*;

public class ResultsReaderXml extends ResultsReader {

    private XMLEventReader xmlReader = null;

    /**
     * Class Constructor.
     *
     * Construct a streaming XML reader for the event stream. One should only
     * attempt to parse an XML stream with the XML reader. Using a non-XML
     * stream will yield unpredictable results.
     *
     * @param inputStream The stream to be parsed.
     * @throws Exception On exception.
     */
    public ResultsReaderXml(InputStream inputStream) throws Exception {
        super(inputStream);
        reader = new BufferedReader(inputStreamReader);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
        xmlReader = inputFactory.createXMLEventReader(reader);
        // at initialization, skip everything in the start until we get to the
        // end of the meta header and the beginning of raw event data
        while (xmlReader.hasNext()){
            XMLEvent xmlEvent = xmlReader.nextEvent();
            if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
                EndElement endElement = xmlEvent.asEndElement();
                if (endElement.getName().getLocalPart().equals("meta")) {
                    // at the end of the meta data, eat everything until start.
                    while (xmlReader.peek().getEventType()
                            != XMLStreamConstants.START_ELEMENT)
                        xmlReader.nextEvent();
                    return;
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void close() throws IOException {
        inputStreamReader.close();
        inputStreamReader = null;
    }

    /** {@inheritDoc} */
    public HashMap<String, String> getNextEvent() throws Exception {
        XMLEvent xmlEvent;
        int eType;

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
