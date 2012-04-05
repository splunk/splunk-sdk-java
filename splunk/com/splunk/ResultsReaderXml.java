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
import javax.xml.stream.*;
import javax.xml.stream.events.*;

public class ResultsReaderXml extends ResultsReader {

    private XMLEventReader xmlReader = null;

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
                    return;
                }
            }
        }
    }
    public HashMap<String, String> getNextEvent() throws Exception {
        HashMap<String, String> returnData = null;

        return returnData;
    }

    public void close() throws IOException {
        inputStreamReader.close();
        inputStreamReader = null;
    }
}
