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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
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
     */
    public static AtomFeed parseStream(InputStream input) {
        XMLStreamReader reader = createReader(input);

        AtomFeed result = AtomFeed.parse(reader);

        try {
            reader.close();
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return result;
    }

    /**
     * Creates a new {@code AtomFeed} instance based on a given XML element.
     *
     * @param input The XML stream.
     * @return An {@code AtomFeed} instance representing the parsed element.
     * @throws RuntimeException The runtime exception if a parse error occurs.
     */
    static AtomFeed parse(XMLStreamReader input) {
        AtomFeed feed = AtomFeed.create();
        feed.load(input, "feed");
        return feed;
    }

    /**
     * Initializes the current instance from a given XML element.
     *
     * @param reader The XML reader.
     */
    @Override void init(XMLStreamReader reader) {
        assert reader.isStartElement();

        String name = reader.getLocalName();

        if (name.equals("entry")) {
            AtomEntry entry = AtomEntry.parse(reader);
            this.entries.add(entry);
        }
        else if (name.equals("messages")) {
            parseEnd(reader);
        }
        else if (name.equals("totalResults")) {
            this.totalResults = parseText(reader);
        }
        else if (name.equals("itemsPerPage")) {
            this.itemsPerPage = parseText(reader);
        }
        else if (name.equals("startIndex")) {
            this.startIndex = parseText(reader);
        }
        else {
            super.init(reader);
        }
    }
}

