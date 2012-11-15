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
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamConstants;

/**
 * The {@code AtomObject} class represents a generic Atom object. This class is
 * a common base class shared by {@code AtomFeed} and {@code AtomEntry}.
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
     * Instantiates the XMLStreamReader, advances to the root element and 
     * validates the root document structure. This initialization code is shared
     * by the {@code AtomFeed} and {@code AtomEntry} parsers.
     *
     * @param input The input stream.
     * @return An {@code XMLStreamReader} initialized reader, advanced to the
     *         first element of the document.
     */
    protected static XMLStreamReader createReader(InputStream input) {
        XMLInputFactory factory = XMLInputFactory.newInstance();

        // The Atom parser assumes that all adjacent text nodes are coalesced
        factory.setProperty(XMLInputFactory.IS_COALESCING, true);
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, true);

        XMLStreamReader reader;
        try {
            reader = factory.createXMLStreamReader(input);
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        assert reader.getEventType() == XMLStreamConstants.START_DOCUMENT;

        // Scan ahead to first element
        scanTag(reader);

        return reader;
    }

    /**
     * Initialize a property of the current instance based on the given XML
     * element.
     *
     * @param reader The XML reader.
     */
    void init(XMLStreamReader reader) {
        assert reader.isStartElement();

        String name = reader.getLocalName();

        if (name.equals("id")) {
            this.id = parseText(reader);
        }
        else if (name.equals("link")) {
            String rel = reader.getAttributeValue(null, "rel");
            String href = reader.getAttributeValue(null, "href");
            this.links.put(rel, href);
            parseEnd(reader);
        }
        else if (name.equals("title")) {
            this.title = parseText(reader);
        }
        else if (name.equals("updated")) {
            this.updated = parseText(reader);
        }
        else {
            parseEnd(reader); // Ignore
        }
    }

    /**
     * Initializes the current instance from the given XML element by calling
     * the {@code init} method on each child of the XML element.
     *
     * @param reader The XML reader.
     */
    void load(XMLStreamReader reader, String localName) {
        assert isStartElement(reader, localName);

        String name = reader.getLocalName();

        scan(reader);
        while (reader.isStartElement()) {
            init(reader);
        }

        if (!isEndElement(reader, name))
            syntaxError(reader);

        scan(reader); // Consume the end element
    }

    /**
     * Parses the element at the current cursor position and reads the
     * corresponding end element.
     *
     * @param reader The XML reader.
     */
    protected void parseEnd(XMLStreamReader reader) {
        scanEnd(reader); // Scan ahead to the end element
        scan(reader);    // Consume the end element
    }

    /**
     * Parses and returns the text value of the element at the current cursor
     * position and reads the corresponding end element.
     *
     * @param reader The XML reader.
     * @return The element's text value.
     */
    protected String parseText(XMLStreamReader reader) {
        assert reader.isStartElement();

        String name = reader.getLocalName();

        String value = getElementText(reader);

        if (!isEndElement(reader, name))
            syntaxError(reader);

        scan(reader); // Consume the end element

        return value;
    }

    //
    // Lexical helpers
    //

    protected static String getElementText(XMLStreamReader reader) {
        try {
            return reader.getElementText();
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected static boolean
    isEndElement(XMLStreamReader reader, String localName) {
        return reader.isEndElement() 
            && reader.getLocalName().equals(localName);
    }

    protected static boolean
    isStartElement(XMLStreamReader reader, String localName) {
        return reader.isStartElement()
            && reader.getLocalName().equals(localName);
    }

    // Scan ahead to the next token, skipping whitespace 
    protected static void scan(XMLStreamReader reader) {
        assert !reader.isWhiteSpace(); // current should never be white
        try {
            do {
                reader.next();
            }
            while (reader.isWhiteSpace()); // Ignore whitespace
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // Scan ahead to the end element that matches the current start element.
    // Note: function returns cursor located at matching end element.
    protected static void scanEnd(XMLStreamReader reader) {
        assert reader.isStartElement();

        String name = reader.getLocalName();

        while (true) {
            scan(reader);

            switch (reader.getEventType()) {
            case XMLStreamConstants.CHARACTERS:
                continue;

            case XMLStreamConstants.START_ELEMENT:
                scanEnd(reader);
                continue;

            case XMLStreamConstants.END_ELEMENT:
                if (!reader.getLocalName().equals(name))
                    syntaxError(reader);
                return;

            default:
                syntaxError(reader);
            }
        }
    }

    // Scan ahead until the next start tag.
    protected static void scanTag(XMLStreamReader reader) {
        try {
            reader.nextTag();
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // Raises a Syntax error runtime exception 
    protected static void syntaxError(XMLStreamReader reader) {
        Location location = reader.getLocation();
        String where = location.toString();
        String message = String.format("Syntax error @ %s", where);
        throw new RuntimeException(message);
    }
}
