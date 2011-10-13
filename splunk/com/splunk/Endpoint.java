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

import java.util.Map;
import java.util.HashMap;

public class Endpoint {

    public Service service = null;
    public String path = null;

    public Endpoint() {

    }

    public Endpoint(Service serv, String pth) {
        service = serv;
        path = pth;
    }

    // UNDONE: some overloads may not be relevant.

    public Element get(String relpath) throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(service.get(path + relpath).getContent());
    }

    public Element get() throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(service.get(path).getContent());
    }



    public Element post(String relpath, Map<String,String> args) throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(service.post(path + relpath, args).getContent());
    }

    public Element post(String relpath) throws Exception {
        Map<String,String> args = new HashMap<String,String>();
        Convert converter = new Convert();
        return converter.convertXMLData(service.post(path + relpath, args).getContent());
    }

    public Element post(Map<String,String> args) throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(service.post(path, args).getContent());
    }

    public Element post() throws Exception {
        Map<String,String> args = new HashMap<String,String>();
        Convert converter = new Convert();
        return converter.convertXMLData(service.post(path, args).getContent());
    }
}
