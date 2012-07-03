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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.events.*;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;

/**
 * The {@code AtomObject}  class represents a generic Atom object. This is a
 * common base class shared by {@code AtomFeed} and {@code AtomEntry}.
 */
public class AtomObject {
    /** The value of the Atom {@code <id>} element. */
    public String id;

    /** The value of the {@code <link>} elements in this {@code AtomObject}. */
    public Map<String, String> links = new HashMap<String, String>();

    /** The value of the Atom {@code <title>} element. */
    public String title;

    /** The value of the Atom {@code <updated>} element. */
    public String updated;

    /**
     * Returns the text pointed to be the start XML element,
     * and updates the xml stream pointer to the next start XML element.
     * @param xmlEventReader the xml event stream reader
     * @return the text found herein.
     * @throws Exception if a streaming error occurs
     */
    protected String
    getXmlSimpleText(XMLEventReader xmlEventReader)
        throws Exception {

        // Skip all XML objects until we get the simple text.
        XMLEvent xmlEvent = xmlEventReader.peek();
        while (xmlEvent.getEventType() != XMLStreamConstants.CHARACTERS) {
            // if we get to and end element before we find text, then
            // the text value is empty.
            if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT)
                return null;
            xmlEvent = xmlEventReader.nextEvent();
        }
        String returnData = xmlEvent.asCharacters().getData();

        // skip to the XML next start element.
        setXmlToNextStart(xmlEventReader);
        return returnData;
    }

    protected XMLEvent
    setXmlToNextStart(XMLEventReader xmlEventReader) throws Exception {

        XMLEvent xmlEvent;
        while (
            (xmlEvent = xmlEventReader.peek()).getEventType() !=
             XMLStreamConstants.START_ELEMENT) {
            if (xmlEvent.getEventType() == XMLStreamConstants.END_DOCUMENT)
                break;
            xmlEventReader.nextEvent();
        }
        return xmlEventReader.peek();
    }

    /**
     * Initialize a property of the current instance based on the given XML
     * element.
     *
     * @param xmlEventReader The XML element.
     */

    void init(XMLEventReader xmlEventReader) throws Exception {

        XMLEvent xmlEvent = xmlEventReader.peek();
        if (xmlEvent.getEventType() != XMLStreamConstants.START_ELEMENT)
            return;

        String name = xmlEvent.asStartElement().getName().getLocalPart();
        if (name.equals("id")) {
            this.id = getXmlSimpleText(xmlEventReader);
        }
        else if (name.equals("link")) {
            QName rel = new QName("rel");
            QName href = new QName("href");
            String relValue = xmlEvent
                .asStartElement()
                .getAttributeByName(rel)
                .getValue();
            String hrefValue = xmlEvent
                .asStartElement()
                .getAttributeByName(href)
                .getValue();
            this.links.put(relValue, hrefValue);
            xmlEventReader.nextEvent();
        }
        else if (name.equals("title")) {
            this.title = getXmlSimpleText(xmlEventReader);
        }
        else if (name.equals("updated")) {
            this.updated = getXmlSimpleText(xmlEventReader);
        }
        else if (name.equals("author")) {
            xmlEvent = xmlEventReader.nextEvent();
            while (xmlEvent.getEventType() != XMLStreamConstants.CHARACTERS) {
                xmlEvent = xmlEventReader.nextEvent();
            }
            // ignore results but still processed to end of author.
            while (true) {
                while ((xmlEvent = xmlEventReader.peek()).getEventType() !=
                    XMLStreamConstants.END_ELEMENT) {
                    xmlEventReader.nextEvent();
                }

                if (xmlEvent
                        .asEndElement()
                        .getName()
                        .getLocalPart()
                        .equals("author")) {
                    break;
                }
                xmlEventReader.nextEvent();
            }
        }
        else if (name.equals("entry")) {
            // eat entry start
            xmlEventReader.nextEvent();
        }
        else {
            getXmlSimpleText(xmlEventReader); // ignore any other key
        }
    }

    /**
     * Initializes the current instance from the given XML element by calling
     * {@code init} on each child of the XML element.
     *
     * @param xmlEventReader The XML element.
     */
    void load(XMLEventReader xmlEventReader) throws Exception {

        XMLEvent xmlEvent = xmlEventReader.peek();
        String name = xmlEvent.asStartElement().getName().getLocalPart();

        do {
            // Process the start elements, everything else but the document
            // end is whitespace
            if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT)  {
                init(xmlEventReader);
            } else {
                xmlEventReader.next();
            }
            xmlEvent = xmlEventReader.peek();
            if (xmlEvent.getEventType() == XMLStreamConstants.END_DOCUMENT)
                break;
            if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT &&
                xmlEvent.asEndElement().getName().getLocalPart().equals(name))
                break;
        }
        while (true);
    }
}
