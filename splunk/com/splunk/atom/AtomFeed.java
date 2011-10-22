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

package com.splunk.atom;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class AtomFeed extends AtomObject {
    public ArrayList<AtomEntry> entries = new ArrayList<AtomEntry>();
    public int itemsPerPage = 0;
    public int startIndex = 0;
    public int totalResults = 0;

    static AtomFeed create() {
        return new AtomFeed();
    }

    public static AtomFeed create(InputStream input) {
        Element root = parseXml(input).getDocumentElement();

        String rname = root.getTagName();
        String xmlns = root.getAttribute("xmlns");
        if (!rname.equals("feed") ||
            !xmlns.equals("http://www.w3.org/2005/Atom"))
            throw new RuntimeException("Unrecognized format");

        return AtomFeed.create(root);
    }

    static AtomFeed create(Element element) {
        AtomFeed feed = AtomFeed.create();
        feed.load(element);
        return feed;
    }

    @Override void init(Element element) {
        String name = element.getTagName();
        if (name.equals("entry")) {
            AtomEntry entry = AtomEntry.create(element);
            this.entries.add(entry);
        }
        else if (name.equals("s:messages")) {
            // UNDONE
        }
        else if (name.equals("opensearch:totalResults")) {
            String value = element.getTextContent().trim();
            this.totalResults = Integer.parseInt(value);
        }
        else if (name.equals("opensearch:itemsPerPage")) {
            String value = element.getTextContent().trim();
            this.itemsPerPage = Integer.parseInt(value);
        }
        else if (name.equals("opensearch:startIndex")) {
            String value = element.getTextContent().trim();
            this.startIndex = Integer.parseInt(value);
        }
        else {
            super.init(element);
        }
    }

    // Returns the given input stream as an XML DOM.
    static Document parseXml(InputStream input) {
        try {
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
            // We are glossing namespaces for now as we can currently 
            // disambiguate everything in the Splunk REST API via local name.
            factory.setNamespaceAware(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new InputStreamReader(input));
            return builder.parse(inputSource);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
