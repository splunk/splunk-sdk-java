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

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AtomObject {
    public String Id;
    public Map<String, String> Links = new HashMap<String, String>();
    public String Title;
    public String Updated;

    AtomObject() {}

    void init(Element element) {
        String name = element.getTagName();
        if (name.equals("id")) {
            this.Id = element.getTextContent().trim();
        } 
        else if (name.equals("link")) {
            String rel = element.getAttribute("rel");
            String href = element.getAttribute("href");
            this.Links.put(rel, href);
        }
        else if (name.equals("title")) {
            this.Title = element.getTextContent().trim();
        }
        else if (name.equals("updated")) {
            this.Updated = element.getTextContent().trim();
        }
        else if (name.equals("author") || name.equals("generator")) {
            // Ignore
        }
        else {
            // UNDONE: Warning
            System.out.format("Unrecognized element: '%s'\n", name);
        }
    }

    // Initialize the AtomObject based on the contents of the given XML
    // element.
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
