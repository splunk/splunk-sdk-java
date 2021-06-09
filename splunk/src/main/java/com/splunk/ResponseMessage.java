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

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * The {@code ResponseMessage} class represents an HTTP response message that 
 * includes status codes, response headers, and body content.
 */
public class ResponseMessage {
    int status;
    Map<String, String> header = null;
    InputStream content;

    /**
     * Default class constructor.
     */
    ResponseMessage() {}

    /**
     * Class constructor.
     *
     * @param status The initial status.
     */
    ResponseMessage(int status) {
        this.status = status;
    }

    ResponseMessage(int status, InputStream content) {
        this.status = status;
        this.content = content;
    }

    /**
     * Returns the body content stream.
     *
     * @return The content stream.
     */
    public InputStream getContent() {
        return this.content;
    }

    /**
     * Returns the response headers.
     *
     * @return Response headers.
     */
    public Map<String, String> getHeader() {
        if (this.header == null)
            this.header =
                new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        return this.header;
    }

    /**
     * Returns the response status.
     *
     * @return The response status.
     */
    public int getStatus() {
        return this.status;
    }
}
