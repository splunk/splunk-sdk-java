/*
 * Copyright 2012 Splunk, Inc.
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

import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * The {@code RequestMessage} class represents an HTTP request message including 
 * method, headers, and body content.
 */
public class RequestMessage {
    String method = "GET";  // "GET" | "PUT" | "POST" | "DELETE"
    Map<String, String> header = null;
    Object content = null;

    /** Creates a new {@code RequestMessage} instance. */
    public RequestMessage() {}

    /** Creates a new {@code RequestMessage} instance with a given method */
    public RequestMessage(String method) {
        this.method = method;
    }

    /**
     * Indicates whether the given value is a supported HTTP method.
     *
     * @param value The value to check.
     * @return {@code true} if the value is a supported method, 
     * {@code false} if not.
     */
    boolean checkMethod(String value) {
        return
            value.equalsIgnoreCase("GET") ||
            value.equalsIgnoreCase("PUT") ||
            value.equalsIgnoreCase("POST") ||
            value.equalsIgnoreCase("DELETE");
    }

    /**
     * Returns a map of message headers.
     *
     * @return A {@code Map} of message headers.
     */
    public Map<String, String> getHeader() {
        if (this.header == null)
            this.header = new TreeMap<String, String>(
                String.CASE_INSENSITIVE_ORDER);
        return this.header;
    }

    /**
     * Returns the message's HTTP method.
     *
     * @return The HTTP method.
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * Sets the message's HTTP method.
     *
     * @param value The HTTP method.
     */
    public void setMethod(String value) {
        value = value.toUpperCase();
        if (!checkMethod(value))
            throw new IllegalArgumentException();
        this.method = value;
    }

    /**
     * Returns the message body content.
     *
     * @return The message content.
     */
    public Object getContent() {
        return this.content;
    }

    /**
     * Sets the message body content.
     *
     * @param value The message content.
     */
    public void setContent(String value) {
        this.content = value;
    }

    public void setContent(OutputStream value) {
        this.content = value;
    }
}

