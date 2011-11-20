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

public class AtomFeed extends AtomObject {
    public ArrayList<AtomEntry> entries = new ArrayList<AtomEntry>();
    public String itemsPerPage = null;
    public String startIndex = null;
    public String totalResults = null;

    static AtomFeed create() {
        return new AtomFeed();
    }

    public static AtomFeed parse(InputStream input) {
        Element root = Xml.parse(input).getDocumentElement();
        String rname = root.getTagName();
        String xmlns = root.getAttribute("xmlns");
        if (!rname.equals("feed") ||
            !xmlns.equals("http://www.w3.org/2005/Atom"))
            throw new RuntimeException("Unrecognized format");
        return AtomFeed.parse(root);
    }

    static AtomFeed parse(Element element) {
        AtomFeed feed = AtomFeed.create();
        feed.load(element);
        return feed;
    }

    // UNDONE: Create dispatch table for property lookups
    @Override void init(Element element) {
        String name = element.getTagName();
        if (name.equals("entry")) {
            AtomEntry entry = AtomEntry.parse(element);
            this.entries.add(entry);
        }
        else if (name.equals("s:messages")) {
            // UNDONE
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

