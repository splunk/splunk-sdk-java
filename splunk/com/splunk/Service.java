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

// UNDONE: Support for splunk path fragments (prefix based on namespace)
// UNDONE: Figure out where to put public response parsers for XML, JSON, ..

package com.splunk;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.splunk.http.*;

public class Service extends com.splunk.http.Service {
    String token = null;

    public Service(String host, int port, String scheme) {
        super(host, port, scheme);
    }

    public Service login(String username, String password) throws IOException {
        HashMap<String, String> args = new HashMap<String, String>();
        args.put("username", username);
        args.put("password", password);
        ResponseMessage response = super.post("/services/auth/login/", args);
        // UNDONE: Check status
        String sessionKey = parseXml(response)
            .getElementsByTagName("sessionKey")
            .item(0)
            .getTextContent();
        this.token = "Splunk " + sessionKey;
        return this;
    }

    // Returns the response content as an XML DOM.
    Document parseXml(ResponseMessage response) {
        try {
            InputStream content = response.getContent();
            DocumentBuilderFactory factory = 
                DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new InputStreamReader(content));
            return builder.parse(inputSource);
        }
        catch (Exception e) {
            // UNDONE: SplunkException
            throw new RuntimeException(e.getMessage());
        }
    }

    public ResponseMessage send(RequestMessage request) throws IOException {
        request.getHeader().put("Authorization", token);
        return super.send(request);
    }
}

