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

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

public class Collection extends Endpoint {

    public Collection() {
        // default constructor - needed?
    }

    public Collection(Service serv, String path) {
        super(serv, path);
    }

    private Map<String,String> read(List<String> items) throws Exception {
        Map<String,String> response = new HashMap<String, String>();
        Element element = super.get();
        for (String item: items) {
            for (Entry entry: element.entry) {
                for (String key: entry.content.keySet()) {
                    if (key.startsWith(item)) {
                        response.put(key, entry.content.get(key));
                    }
                }
            }
        }
        return response;
    }

    public boolean contains(String key) throws Exception {
        List<String> names = list(); //UNDONE: check to see if this is what we should be looking through.
        return names.contains(key);
    }

    public List<String> list() throws Exception {
        List<String> retList = new ArrayList<String>();
        Element element = super.get();

         for (Entry entry: element.entry) {
             retList.add(entry.title);
         }
        return retList;
    }

    public Map<String,String> itemmeta() throws Exception {
        Map<String,String> response = new HashMap<String, String>();
        List<String> items = new ArrayList<String>();
        items.add("eai:acl");
        items.add("eai:attributes");

        Element element = super.get();
        for (String item: items) {
            for (Entry entry: element.entry) {
                for (String key: entry.content.keySet()) {
                    if (key.startsWith(item)) {
                        response.put(key, entry.content.get(key));
                    }
                }
            }
        }
        return response;
    }

    // delete should delete an element of the collection, thus we require a relative path that
    // has at least one character.
    public Element delete(String relpath, Map<String,String> args) throws Exception {
        if (relpath.length() == 0) {
            throw new Exception("relative path must be a string with at least one character");
        }
        Convert converter = new Convert();
        return converter.convertXMLData(service.delete(path + relpath, args).getContent());
    }

    public Element delete(String relpath) throws Exception {
        if (relpath.length() == 0) {
            throw new Exception("relative path must be a string with at least one character");
        }
        Convert converter = new Convert();
        return converter.convertXMLData(service.delete(path + relpath).getContent());
    }


    // allow for a few variants of create
    public Element create(Map<String,String> args) throws Exception {
        Convert converter = new Convert();
        // assume 'name' has already been added to the argument list.
        return converter.convertXMLData(service.post(path, args).getContent());
    }

    public Element create(String name, Map<String,String> args) throws Exception {
        Convert converter = new Convert();
        args.put("name", name); // if already there, we forcibly overwrite
        return converter.convertXMLData(service.post(path, args).getContent());
    }

    public Element create(String name) throws Exception {
        Convert converter = new Convert();
        Map<String,String> args = new HashMap<String, String>();
        args.put("name", name);
        return converter.convertXMLData(service.post(path, args).getContent());
    }
}
