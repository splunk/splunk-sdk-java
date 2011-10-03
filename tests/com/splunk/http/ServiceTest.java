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

import junit.framework.TestCase;
import org.junit.*;
import static org.junit.Assert.*;

import com.splunk.http.*;
import com.splunk.sdk.Program;

public class ServiceTest extends TestCase {
    Program program = new Program();

    public ServiceTest() {}

    void checkResponse(ResponseMessage response) {
        assertEquals(response.getStatus(), 200);
    }

    @Before public void setUp() {
        this.program.init(); // Pick up .splunkrc settings
    }

    @Test public void test() {
        Service service;
        try {
            service = new Service(
                program.host, program.port, program.scheme);
            assertTrue(true);
        }
        catch (Exception e) {
            assertTrue(false);
            return;
        }

        try {
            ResponseMessage response = service.get("/");
            assertEquals(200, response.getStatus());
        }
        catch (Exception e) {
            assertTrue(false);
        }
    }
}

