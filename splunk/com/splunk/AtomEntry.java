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
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * The {@code AtomEntry} class represents an Atom {@code <entry>} element.
 */
public class AtomEntry extends AtomObject {
    /** The value of the Atom entry's {@code <published>} element. */
    public String published;

    /** The value of the Atom entry's {@code <content>} element. */
    public Record content;

    /**
     * Creates a new {@code AtomEntry} instance.
     *
     * @return A new {@code AtomEntry} instance.
     */
    static AtomEntry create() {
        return new AtomEntry();
    }

    /**
     * Creates a new {@code AtomEntry} instance based on a given stream.
     * A few endpoints, such as {@code search/jobs/{sid}},
     * return an Atom {@code <entry>} element as the root of the response.
     *
     * @param input The input stream.
     * @return An {@code AtomEntry} instance representing the parsed stream.
     * @throws Exception on a streaming error.
     */

    public static AtomEntry parseStream(InputStream input) throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader xmlReader = inputFactory.createXMLEventReader(input);
        if (xmlReader.nextEvent().getEventType() !=
            XMLStreamConstants.START_DOCUMENT) {
            throw new RuntimeException("Unrecognized format");
        }
        // skip to start element
        XMLEvent xmlEvent;
        while ((xmlEvent = xmlReader.peek()).getEventType() !=
                XMLStreamConstants.START_ELEMENT) {
            xmlReader.nextEvent();
        }

        if (!xmlEvent
                .asStartElement()
                .getName()
                .getLocalPart()
                .equals("feed") &&
            !xmlEvent
                .asStartElement()
                .getNamespaceURI("")
                .equals("http://www.w3.org/2005/Atom")) {
            throw new RuntimeException("Unrecognized format");
        }

        return AtomEntry.parse(xmlReader);

    }

    /**
     * Creates a new {@code AtomEntry} instance based on a given XML element.
     *
     * @param element The XML element.
     * @return An {@code AtomEntry} instance representing the parsed element.
     * @throws Exception on a streaming error.
     */
    static AtomEntry parse(XMLEventReader element) throws Exception {
        AtomEntry entry = AtomEntry.create();
        entry.load(element);
        return entry;
    }

    /**
     * Initializes the current instance with a given XML element.
     *
     * @param xmlEventReader The XML element.
     */
    @Override void init(XMLEventReader xmlEventReader) throws Exception {
        XMLEvent xmlEvent = xmlEventReader.peek();

        String name = xmlEvent.asStartElement().getName().getLocalPart();
        if (name.equals("published")) {
            xmlEvent = xmlEventReader.nextEvent();
            while (xmlEvent.getEventType() != XMLStreamConstants.CHARACTERS) {
                xmlEvent = xmlEventReader.nextEvent();
            }
            this.published = xmlEvent.asCharacters().getData();
            while (xmlEventReader.peek().getEventType() !=
                   XMLStreamConstants.START_ELEMENT) {
                xmlEventReader.nextEvent();
            }
        }
        else if (name.equals("content")) {
            this.content = parseContent(xmlEventReader);
        }
        else {
            super.init(xmlEventReader);
        }
    }

    /**
     * Parses the {@code <content>} element of an Atom entry.
     *
     * @param xmlEventReader The XML element to parse.
     * @return A {@code Record} object containing the parsed values.
     */
    private Record
    parseContent(XMLEventReader xmlEventReader) throws Exception {

        // Skip the content XML start element and move the next
        // XML start element
        XMLEvent xmlEvent = setXmlToNextStart(xmlEventReader);

        Record content = null;
        do {
            if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
                if (xmlEvent
                        .asEndElement()
                        .getName()
                        .getLocalPart()
                        .equals("content"))
                    break;
            }
            if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
                if (xmlEvent
                        .asStartElement()
                        .getName()
                        .getLocalPart()
                        .equals("dict")) {
                    content = parseDict(xmlEventReader);
                }
            }
            xmlEvent = xmlEventReader.nextEvent();
        } while (true);

        return content;
    }

    /**
     * Parses a {@code <dict>} content element and returns a {@code Record}
     * object containing the parsed values.
     *
     * @param xmlEventReader The {@code <dict>} element to parse.
     * @return A {@code Record} object containing the parsed values.
     */
    private static final QName nameQName = new QName("name");
    private Record parseDict(XMLEventReader xmlEventReader) throws Exception {
        XMLEvent xmlEvent = xmlEventReader.peek();

        Record result = new Record();
        do {
            if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
                if (xmlEvent
                        .asEndElement()
                        .getName()
                        .getLocalPart()
                        .equals("dict"))
                    break;
            }

            if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
                StartElement startElement = xmlEvent.asStartElement();
                if (startElement.getName().getLocalPart().equals("key")) {
                    String key = startElement
                        .getAttributeByName(nameQName)
                        .getValue();
                    if (xmlEventReader.peek().isEndElement()) {
                        // HACK: to handle to empty values -- parseValue 
                        // consumes two tokens!
                        xmlEventReader.nextEvent();
                    }
                    else {
                        Object value = parseValue(xmlEventReader);
                        if (value != null) result.put(key, value);
                    }
                }
            }

            xmlEvent = xmlEventReader.nextEvent();

        } while (true);

        if (result.size() == 0) xmlEventReader.nextEvent();

        return result.size() == 0 ? null : result;
    }

    /**
     * Parse a {@code <list>} element and return a {@code List} object
     * containing the parsed values.
     *
     * @param xmlEventReader The {@code <list>} element to parse.
     * @return A {@code List} object containing the parsed values.
     */
    private List parseList(XMLEventReader xmlEventReader) throws Exception {
        XMLEvent xmlEvent = xmlEventReader.peek();

        List result = new ArrayList();

        do {
            int type = xmlEvent.getEventType();
            if (type == XMLStreamConstants.END_ELEMENT) {
                if (xmlEvent
                        .asEndElement()
                        .getName()
                        .getLocalPart()
                        .equals("list"))
                    break;
            }
            if (type == XMLStreamConstants.START_ELEMENT) {
                if (xmlEvent
                        .asStartElement()
                        .getName()
                        .getLocalPart()
                        .equals("item")) {
                    Object value = parseValue(xmlEventReader);
                    if (value != null) result.add(value);
                }
            }
            xmlEvent = xmlEventReader.nextEvent();
        } while (true);

        if (result.size() == 0) xmlEventReader.nextEvent();

        return result.size() == 0 ? null : result;
    }

    /**
     * Parses the value content of a dict/key or a list/item element. The value
     * is either text, a {@code <dict>} element, or a {@code <list>} element.
     *
     * @param xmlEventReader The XML element containing the values to parse.
     * @return An object containing the parsed values. If the source was a text
     * value, the object is a {@code String}. If the source was a {@code <dict>}
     * element, the object is a {@code Record}. If the source was a
     * {@code <list>} element, the object is a {@code List} object.
     * @throws Exception on a streaming error.
     */
    Object parseValue(XMLEventReader xmlEventReader) throws Exception {

        // If the value is a single element (i.e. no dict or list following,
        // then we can grab just the text, otherwise we will crawl down
        // the element, essentially doing a depth first build up of the object
        XMLEvent xmlEvent = xmlEventReader.nextEvent();

        if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
            String type = xmlEvent.asStartElement().getName().getLocalPart();
            if (type.equals("dict"))
                return parseDict(xmlEventReader);
            else if (type.equals("list"))
                return parseList(xmlEventReader);
            else if (type.equals("key")) {
                return parseValue(xmlEventReader);
            }
            else if (type.equals("item")) { // must be text item of a list
                xmlEvent = xmlEventReader.nextEvent();
                return xmlEvent.asCharacters().getData();
            }
        } else if (xmlEvent.getEventType() == XMLStreamConstants.CHARACTERS) {
            Characters characters = xmlEvent.asCharacters();

            if (characters.isWhiteSpace())
                return parseValue(xmlEventReader);

            return characters.getData();
        } else {
            xmlEventReader.nextEvent(); // whitespace or syntactical ends
        }

        return null;
    }
}
