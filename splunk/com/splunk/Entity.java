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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity extends Endpoint {

    public Entity() {
        // default constructor - needed?
    }

    public Entity(Service service, String path) {
        super(service, path);
    }

    public Map<String,String> read(List<String> items) throws Exception {
        Map<String,String> response = new HashMap<String, String>();
        Map<String,String> count = new HashMap<String, String>();
        count.put("count", "-1");
        Element element = super.get(count);
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

    public Map<String,String> readmeta() throws Exception {
        List<String> itemList = new ArrayList<String>();
        itemList.add("eai:acl");
        itemList.add("eai:attributes");
        return read(itemList);
    }

    public Element update(Map<String,String> args) throws Exception {
        super.post(args);
        return super.get();
    }

    public Element disable() throws Exception {
        super.post("/disable");
        return super.get();
    }

    public Element enable () throws Exception {
        super.post("/enable");
        return super.get();
    }

    public Element reload () throws Exception {
        super.post("/_reload");
        return super.get();
    }

    public Element delete(Map<String,String> args) throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(service.delete(path, args).getContent());
    }

    public Element delete() throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(service.delete(path).getContent());
    }
}

