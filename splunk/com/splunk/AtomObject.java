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

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Representation of a generic Atom object. This is a common base class shared
 * by {@code AtomFeed} and {@code AtomEntry}
 */
public class AtomObject {
    /** The value of the Atom {@code id} element. */
    public String id;

    /** The value of any {@code link} elements contains by this Atom object. */
    public Map<String, String> links = new HashMap<String, String>();

    /** The value of the Atom {@code title} element. */
    public String title;

    /** The value of the Atom {@code updated} element. */
    public String updated;

    /**
     * Initialize a property of the current instance based on the given XML
     * element.
     *
     * @param element The XML element.
     */
    void init(Element element) {
        String name = element.getTagName();
        if (name.equals("id")) {
            this.id = element.getTextContent().trim();
        } 
        else if (name.equals("link")) {
            String rel = element.getAttribute("rel");
            String href = element.getAttribute("href");
            this.links.put(rel, href);
        }
        else if (name.equals("title")) {
            this.title = element.getTextContent().trim();
        }
        else if (name.equals("updated")) {
            this.updated = element.getTextContent().trim();
        }
        else if (name.equals("author") || name.equals("generator")) {
            // Ignore
        }
        else {
            // Ignore
        }
    }

    /**
     * Initialize the current instance from the given XML element by calling
     * {@code init} on each child of the XML element.
     *
     * @param element The XML element.
     */
    void load(Element element) {
        for (Node child = element.getFirstChild();
             child != null;
             child = child.getNextSibling()) 
        {
            if (child.getNodeType() != Node.ELEMENT_NODE) 
                continue;
            init((Element)child);
        }
    }
}
