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

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.*;

/**
 * Thrown for HTTP responses that return an error status code.
 */
public class HttpException extends RuntimeException {
    private int status;
    private String detail; // Error message detail

    HttpException(int status, String message, String detail) {
        super(message);
        this.status = status;
        this.detail = detail;
    }

    /**
     * Create an {@code HttpException} instance based on the given response.
     *
     * @param response The HTTP response that returned an error code.
     * @return A new {@code HttpException) instance.
     */
    static HttpException create(ResponseMessage response) {
        int status = response.getStatus();

        StringBuilder s = new StringBuilder();
        InputStreamReader r;
        try {
            r = new InputStreamReader(response.getContent(), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            throw new AssertionError("How does your system not support UTF-8?");
        }

        int c = -1;
        while (true) {
            try {
                c = r.read();
            } catch (IOException e1) {
                // Not much to be done here if that stream is bad...
            }
            if (c == -1) break;

            s.appendCodePoint(c);
        }

        String detail = "";
        try {
            // Attempt to read the error detail from the error response content as XML
            Document document = Xml.parse(new ByteArrayInputStream(detail.getBytes()));
            NodeList msgs = document.getElementsByTagName("msg");
            if (msgs.getLength() > 0)
                detail = msgs.item(0).getTextContent();
        }
        catch (Exception e) {
            // Not an XML document; return the raw string.
            detail = s.toString();
        }

        String message = String.format("HTTP %d", status);  

        if (detail != null && detail.length() > 0)
            message = message + " -- " + detail;

        return new HttpException(status, message, detail);
    }

    /**
     * Returns the HTTP error response message detail.
     *
     * @return HTTP error response message detail.
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Returns the HTTP status code.
     *
     * @return HTTP status code.
     */
    public int getStatus() {
        return status;
    }
}

