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

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class HttpServiceTest extends SDKTestCase {
    protected HttpService httpService;

    @Before @Override public void setUp() throws Exception {
        super.setUp();
        httpService = new HttpService(
                (String)connectionArgs.get("host"),
                (Integer)connectionArgs.get("port"),
                (String)connectionArgs.get("scheme")
        );
    }

    private boolean firstLineIsXmlDtd(InputStream stream) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream)
        );
        try {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".equals(
                    reader.readLine()
            );
        } catch (IOException e) {
            fail(e.toString());
            return false;
        }
    }

    @Test public void testGet() {
        ResponseMessage response = httpService.get("/");
        assertEquals(200, response.getStatus());
        assertTrue(firstLineIsXmlDtd(response.getContent()));
    }

    @Test public void testPost() {
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("foo", "bar");
        ResponseMessage response = httpService.post("/", args);
        assertEquals(200, response.getStatus());
        assertTrue(firstLineIsXmlDtd(response.getContent()));
    }

    @Test public void testSend() {
        RequestMessage request = new RequestMessage("GET");
        ResponseMessage response = service.send("/", request);
        assertEquals(200, response.getStatus());
        assertTrue(firstLineIsXmlDtd(response.getContent()));
    }
}
