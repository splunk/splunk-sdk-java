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

    public Element element = null;

    public Endpoint() {

    }

    public Endpoint(Service serv, String pth) {
        this.service = serv;
        this.path = pth;
    }

    private String sanePath(String relpath) {
        // sanity
        if (path.endsWith("/") && relpath.startsWith("/")) {
           relpath = relpath.replaceFirst("/", "");
        } else if (!path.endsWith("/") && !relpath.startsWith("/")) {
            relpath = "/" + relpath;
        }
        return  path + relpath;
    }

    public Endpoint get(Map<String,String> args) throws Exception {
        Convert converter = new Convert();
        if (!args.containsKey("count")) {
            args.put("count", "-1");
        }
        this.element = converter.convertXMLData(service
                                            .get(path, args)
                                            .getContent());
        return this;
    }

    public Endpoint get(String relpath) throws Exception {
        Convert converter = new Convert();
        Map<String,String> args = new HashMap<String, String>();
        args.put("count", "-1");

        this.element = converter.convertXMLData(service
                                            .get(path + relpath)
                                            .getContent());
        return this;
    }

    public Endpoint get() throws Exception {
        Convert converter = new Convert();
        Map<String,String> args = new HashMap<String, String>();
        args.put("count", "-1");
        this.element = converter.convertXMLData(service.get(path, args)
                                                  .getContent());
        return this;
    }



    public Endpoint post(String relpath,
                        Map<String,String> args) throws Exception {
        Convert converter = new Convert();
        String fullpath = sanePath(relpath);
        this.element = converter.convertXMLData(service
                                            .post(fullpath, args)
                                            .getContent());
        return this;
    }

    public Endpoint post(String relpath) throws Exception {
        Map<String,String> args = new HashMap<String,String>();
        Convert converter = new Convert();
        // sanity
        if (path.endsWith("/") && relpath.startsWith("/")) {
           relpath = relpath.replaceFirst("/", "");
        } else if (!path.endsWith("/") && !relpath.startsWith("/")) {
            relpath = "/" + relpath;
        }
        this.element = converter.convertXMLData(service
                                            .post(path + relpath, args)
                                            .getContent());
        return this;
    }

    public Endpoint post(Map<String,String> args) throws Exception {
        Convert converter = new Convert();
        this.element = converter.convertXMLData(service
                                            .post(path, args)
                                            .getContent());
        return this;
    }

    public Endpoint post() throws Exception {
        Map<String,String> args = new HashMap<String,String>();
        Convert converter = new Convert();
        this.element = converter.convertXMLData(service
                                            .post(path, args)
                                            .getContent());
        return this;
    }
}
