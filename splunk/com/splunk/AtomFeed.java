/*
 * Copyright 2011 Splunk, Inc.
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
import org.w3c.dom.Element;

/**
 * Representation of an Atom feed.
 */
public class AtomFeed extends AtomObject {
    /** The list of Atom entries contained by this Atom feed. */
    public ArrayList<AtomEntry> entries = new ArrayList<AtomEntry>();

    /** The value of the Atom feed's {@code itemsPerPage} element. */
    public String itemsPerPage = null;

    /** The value of the Atom feed's {@code startIndex} element. */
    public String startIndex = null;

    /** The value of the Atom feed's {@code totalResults} element. */
    public String totalResults = null;

    /**
     * Creates a new AtomFeed instance.
     *
     * @return A new AtomFeed instance.
     */
    static AtomFeed create() {
        return new AtomFeed();
    }

    /**
     * Creates a new AtomFeed instance based on the given stream.
     *
     * @param input The input stream.
     * @return An {@code AtomFeed} instance representing the parsed stream.
     */
    public static AtomFeed parse(InputStream input) {
        Element root = Xml.parse(input).getDocumentElement();
        String rname = root.getTagName();
        String xmlns = root.getAttribute("xmlns");
        if (!rname.equals("feed") ||
            !xmlns.equals("http://www.w3.org/2005/Atom"))
            throw new RuntimeException("Unrecognized format");
        return AtomFeed.parse(root);
    }

    /**
     * Create a new {@code AtomFeed} instance based on the given XML element.
     *
     * @param element The XML element.
     * @return An {@code AtomFeed} instance representing the parsed element.
     */
    static AtomFeed parse(Element element) {
        AtomFeed feed = AtomFeed.create();
        feed.load(element);
        return feed;
    }

    /**
     * Initialize the current instance from the given XML element.
     *
     * @param element The XML element.
     */
    @Override void init(Element element) {
        String name = element.getTagName();
        if (name.equals("entry")) {
            AtomEntry entry = AtomEntry.parse(element);
            this.entries.add(entry);
        }
        else if (name.equals("s:messages")) {
            // Ignore
        }
        else if (name.equals("opensearch:totalResults")) {
            this.totalResults = element.getTextContent().trim();
        }
        else if (name.equals("opensearch:itemsPerPage")) {
            this.itemsPerPage = element.getTextContent().trim();
        }
        else if (name.equals("opensearch:startIndex")) {
            this.startIndex = element.getTextContent().trim();
        }
        else {
            super.init(element);
        }
    }
}

