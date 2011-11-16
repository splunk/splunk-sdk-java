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

//
// UNDONE: Test basic HTTP level interactions
//   * send/get/post/delete
//       - Headers, params, result codes ...
//   * timeouts
//   * Server cert validation
//   * Proxy tunneling
//

package com.splunk.http;

import java.io.IOException;
import java.util.HashMap;
import junit.framework.TestCase;
import org.junit.*;
import static org.junit.Assert.*;

import com.splunk.http.*;
import com.splunk.sdk.Command;

public class ServiceTest extends TestCase {
    Command command;

    public ServiceTest() {}

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
    }

    @Test public void testGet() throws IOException {
        Service service = new Service(
            command.host, command.port, command.scheme);
        ResponseMessage response = service.get("/");
        assertEquals(200, response.getStatus());
    }

    @Test public void testPost() throws IOException {
        Service service = new Service(
            command.host, command.port, command.scheme);
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("foo", "bar");
        ResponseMessage response = service.post("/", args);
        // We are taking advantage of the fact that a post to the root of
        // the REST API hierarchy ignores POST args and returns the same
        // results as a GET. It could be argued this is not a very good
        // test because we can't distinguish between a GET & POST result ..
        // But I dont have a better idea for how to excercise this code path
        // at this layer (without authenticating against Splunk).
        assertEquals(200, response.getStatus());
    }

    @Test public void testSend() throws IOException {
        Service service = new Service(
            command.host, command.port, command.scheme);
        RequestMessage request = new RequestMessage("GET");
        ResponseMessage response = service.send("/", request);
        assertEquals(200, response.getStatus());
    }
}

