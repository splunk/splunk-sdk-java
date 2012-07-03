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
import java.util.*;

import javax.xml.stream.events.*;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;

/**
 * The {@code AtomFeed} class represents an Atom feed.
 */
public class AtomFeed extends AtomObject {
    /** The list of Atom entries contained in this {@code AtomFeed} object. */
    public ArrayList<AtomEntry> entries = new ArrayList<AtomEntry>();

    /** The value of the Atom feed's {@code <itemsPerPage>} element. */
    public String itemsPerPage = null;

    /** The value of the Atom feed's {@code <startIndex>} element. */
    public String startIndex = null;

    /** The value of the Atom feed's {@code <totalResults>} element. */
    public String totalResults = null;

    /**
     * Creates a new {@code AtomFeed} instance.
     *
     * @return A new {@code AtomFeed} instance.
     */
    static AtomFeed create() {
        return new AtomFeed();
    }

    /**
     * Creates a new {@code AtomFeed} instance based on the given stream.
     *
     * @param input The input stream.
     * @return An {@code AtomFeed} instance representing the parsed stream.
     * @throws Exception if a streaming error occurs
     */
    public static AtomFeed parseStream(InputStream input) throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader xmlReader = inputFactory.createXMLEventReader(input);
        if (xmlReader.nextEvent().getEventType() !=
            XMLStreamConstants.START_DOCUMENT) {
            throw new RuntimeException("Unrecognized format");
        }
        // skip to feed start element
        XMLEvent xmlEvent;
        while ((xmlEvent = xmlReader.peek()).getEventType() !=
            XMLStreamConstants.START_ELEMENT) {
            xmlReader.nextEvent();
        }

        // sanity check we have an Atom feed
        if (!xmlEvent
                .asStartElement()
                .getName()
                .getLocalPart()
                .equals("feed")  &&
            !xmlEvent
                .asStartElement()
                .getNamespaceURI("")
                .equals("http://www.w3.org/2005/Atom")) {
            throw new RuntimeException("Unrecognized format");
        }

        return AtomFeed.parse(xmlReader);
    }

    /**
     * Create a new {@code AtomFeed} instance based on a given XML element.
     *
     * @param input The XML stream
     * @return An {@code AtomFeed} instance representing the parsed element.
     * @throws Exception if a streaming error occurs
     */
    static AtomFeed parse(XMLEventReader input) throws Exception {
        AtomFeed feed = AtomFeed.create();
        feed.load(input);
        return feed;
    }

    /**
     * Initializes the current instance from a given XML element.
     *
     * @param xmlEventReader The XML element.
     */
    @Override void init(XMLEventReader xmlEventReader) throws Exception {
        XMLEvent xmlEvent = xmlEventReader.peek();

        String name = xmlEvent.asStartElement().getName().getLocalPart();
        if (name.equals("entry")) {
            AtomEntry entry = AtomEntry.parse(xmlEventReader);
            this.entries.add(entry);
        }
        else if (name.equals("messages")) {
            getXmlSimpleText(xmlEventReader); // ignore messages
        }
        else if (name.equals("totalResults")) {
            this.totalResults =
                getXmlSimpleText(xmlEventReader);
        }
        else if (name.equals("itemsPerPage")) {
            this.itemsPerPage =
                getXmlSimpleText(xmlEventReader);
        }
        else if (name.equals("startIndex")) {
            this.startIndex = getXmlSimpleText(xmlEventReader);
        }
        else {
            super.init(xmlEventReader);
        }
    }
}

