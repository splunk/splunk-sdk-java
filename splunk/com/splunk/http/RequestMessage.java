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

package com.splunk.http;

import java.io.OutputStream;
import java.util.TreeMap;
import java.util.Map;

public class RequestMessage {
    String method = "GET";  // "GET" | "PUT" | "POST" | "DELETE"
    String path = null;
    Map<String, String> header = null;
    Object content = null;

    public RequestMessage() {}

    public RequestMessage(String method) {
        this.method = method;
    }

    public RequestMessage(String method, String path) {
        this.method = method;
        this.path = path;
    }

    boolean checkMethod(String value) {
        return
            value.equalsIgnoreCase("GET") ||
            value.equalsIgnoreCase("PUT") ||
            value.equalsIgnoreCase("POST") ||
            value.equalsIgnoreCase("DELETE");
    }

    public Map<String, String> getHeader() {
        if (this.header == null)
            this.header = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        return this.header;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String value) {
        value = value.toUpperCase();
        if (!checkMethod(value))
            throw new IllegalArgumentException();
        this.method = value;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String value) {
        this.path = value;
    }
    
    public Object getContent() {
        return this.content;
    }

    public void setContent(String value) {
        this.content = value;
    }

    public void setContent(OutputStream value) {
        this.content = value;
    }
}

