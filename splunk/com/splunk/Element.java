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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class Element {

    // header
    public Header header = new Header();

    // In order to correctly represent a collection of entities, entries is a nameList.
    // This means that a single Entity has a nameList of one Entry, while a collection
    // has a nameList of more than one.
    public List<Entry> entry = new ArrayList<Entry>();

    public Element() {
        // default constructor: nothing
    }

    private boolean isLeaf(Node node) {
        if (!node.hasChildNodes()) return true;
        Node child = node.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) return false;
            child = child.getNextSibling();
        }

        return true;
    }

    private String join(java.util.Collection<String> s, String delimiter) {
        if (s == null || s.isEmpty())
            return "";
        Iterator<String> iter = s.iterator();
        StringBuilder buffer = new StringBuilder(iter.next());
        while (iter.hasNext()) {
            String item = iter.next();
            if( item.contains(delimiter)) {
                item = String.format("\"%s\"", item);
            }
            buffer.append(delimiter).append(item);
        }
        return buffer.toString();
    }

    private Map<String, String> nodeToHashMap(Node node) {
        return nodeToHashMap(node, "");
    }

    private Map<String, String> nodeToHashMap(Node node, String prefix) {
        NodeList contents = node.getChildNodes();
        HashMap<String, String> primitive = new LinkedHashMap<String, String>();
        for(int i=0; i<contents.getLength(); i++)
        {
            String name;
            String val;
            if( contents.item(i).getNodeType() == Node.ELEMENT_NODE ) {
                Node containerNode = contents.item(i);

                if (containerNode.getNodeName().equals("s:dict")) {
                    // get s:dict node
                    for (Node keyNode=containerNode.getFirstChild(); keyNode!=null; keyNode=keyNode.getNextSibling()) {
                        // get s:key child nodes of s:dict
                        if( keyNode.getNodeType() == Node.ELEMENT_NODE ){
                            if(keyNode.hasChildNodes()) {
                                // non-terminal key nodes
                                NodeList childNodes = keyNode.getChildNodes();
                                for( int j=0; j<childNodes.getLength(); j++ ) {
                                    Node childNode = childNodes.item(j);
                                    if ( childNode.getNodeType() == Node.ELEMENT_NODE ) {
                                        // of them take element nodes
                                        name = keyNode.getAttributes().getNamedItem("name").getTextContent();
                                        if (prefix.length() > 0) {
                                            name = prefix + "." + name;
                                        }
                                        // unfold them
                                        Map<String, String> subPrimitive = nodeToHashMap(keyNode, name);
                                        primitive.putAll(subPrimitive);
                                        break;
                                    }
                                    else if( childNode.getNodeType() == Node.TEXT_NODE && childNodes.getLength() == 1) {
                                        // and text nodes, but if there's no other nodes
                                        name = keyNode.getAttributes().getNamedItem("name").getTextContent();
                                        if (prefix.length() > 0) {
                                            name = prefix + "." + name;
                                        }
                                        val = childNode.getTextContent();
                                        primitive.put(name, val) ;
                                    }
                                }
                            }
                            else {
                                // terminal key nodes
                                name = keyNode.getAttributes().getNamedItem("name").getTextContent();
                                if (prefix.length() > 0) {
                                    name = prefix + "." + name;
                                }
                                val = keyNode.getTextContent();
                                primitive.put(name, val) ;
                            }
                        }
                    }
                }
                else if (containerNode.getNodeName().equals("s:nameList")) {
                    // get s:nameList node
                    if (prefix.length() > 0) {
                        name = prefix;
                    }
                    else {
                        name = node.getAttributes().getNamedItem("name").getTextContent();
                    }

                    // get s:key child nodes of s:nameList
                    List<String> values = new ArrayList<String>();
                    for (Node keyNode=containerNode.getFirstChild(); keyNode!=null; keyNode=keyNode.getNextSibling()) {
                        if( keyNode.getNodeType() == Node.ELEMENT_NODE ) {
                            values.add(keyNode.getTextContent());
                        }
                    }
                    primitive.put(name, join(values, ","));
                }
                else if (containerNode.getNodeName().equals("s:list")){
                    // get s:list node
                    if (prefix.length() > 0) {
                        name = prefix;
                    }
                    else {
                        name = node.getAttributes().getNamedItem("name").getTextContent();
                    }

                    // get s:item child nodes of s:list
                    List<String> values = new ArrayList<String>();
                    for (Node keyNode=containerNode.getFirstChild(); keyNode!=null; keyNode=keyNode.getNextSibling()) {
                        if( keyNode.getNodeType() == Node.ELEMENT_NODE ) {
                            values.add(keyNode.getTextContent());
                        }
                    }
                    primitive.put(name, join(values, ","));
                } else {
                    System.out.println("internal error in nodeToHashMap() -- missed case: "+ containerNode.getNodeName());
                }
            }
        }
        return primitive;
    }

    public Entry parseEntry(Node root) {
        // run through the XML entry node, parsing and extracting

        Entry entry = new Entry();

        // parse: build up an Entity object, high level data
        List<String> firstLevel = Arrays.asList("id", "title", "updated", "link", "entry", "author", "published");
        Node node = root.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String name = node.getNodeName();
                // remove prefix through to colon, if one exists -- to behave like python SDK
                if (name.contains(":")) {
                    name = name.split(":")[1];
                }

                if (firstLevel.contains(name)) {

                    HashMap<String,String> attributes = new HashMap<String, String>();
                    String value = node.getTextContent().trim();
                    org.w3c.dom.NamedNodeMap attrs = node.getAttributes();

                    if (isLeaf(node)) {
                        if (name.equals("id")) {
                            entry.id = value;
                        } else if (name.equals("title")) {
                            entry.title = value;
                        } else if (name.equals("updated")) {
                            entry.updated = value;
                        } else if (name.equals("link")) {
                            if (attrs != null) {
                                int count = attrs.getLength();
                                if (count > 0) {
                                    for (int idx=0; idx<count; idx++) {
                                        attributes.put(attrs.item(idx).getNodeName(), attrs.item(idx).getNodeValue());
                                    }
                                }
                            }
                            entry.link.add(attributes);
                        } else if (name.equals("published")) {
                            entry.published = value;
                        }
                    } else {
                        // only non-leaf at first level is author.
                        if (name.equals("author")) {
                            Node child = node.getFirstChild();
                            while (child != null) {
                                if (child.getNodeType() == Node.ELEMENT_NODE) {
                                    String cname = child.getNodeName();
                                    String cvalue = child.getTextContent().trim();
                                    entry.author.put(cname, cvalue);
                                }
                                child = child.getNextSibling();
                            }
                        }
                    }
                } else {
                    if (name.equals("content")) {
                        // content is usually a series of (perhaps) nested dictionaries, lists and keys.
                        entry.content = nodeToHashMap(node);
                    } else {
                        System.out.println("internal error parseEntry, not in list: " + name);
                    }
                }
            }
            node = node.getNextSibling();
        }

        return entry;
    }

    public Map<String,String> read(List<String> items) throws Exception {
        Map<String,String> response = new HashMap<String, String>();
        for (String item: items) {
            for (Entry ent: entry) {
                for (String key: ent.content.keySet()) {
                    if (key.startsWith(item)) {
                        response.put(key, ent.content.get(key));
                    }
                }
            }
        }
        return response;
    }
}