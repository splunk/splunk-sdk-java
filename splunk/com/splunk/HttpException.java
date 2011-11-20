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

public class HttpException extends RuntimeException {
    private int status;
    private String detail; // Error message detail

    HttpException(int status, String message, String detail) {
        super(message);
        this.status = status;
        this.detail = detail;
    }

    static HttpException create(ResponseMessage response) {
        int status = response.getStatus();

        // Attempt to read the error detail from the error response content.
        String detail = "";
        try {
            Document document = Xml.parse(response.getContent());
            NodeList msgs = document.getElementsByTagName("msg");
            if (msgs.getLength() > 0)
                detail = msgs.item(0).getTextContent();
        }
        catch (Exception e) {} // Couldn't get detail

        String message = String.format("HTTP %d", status);  

        if (detail != null)
            message = message + " -- " + detail;

        return new HttpException(status, message, detail);
    }

    public String getDetail() {
        return detail;
    }

    public int getStatus() {
        return status;
    }
}

