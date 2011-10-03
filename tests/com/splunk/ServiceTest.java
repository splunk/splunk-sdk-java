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
// UNDONE:
//   * Authentication
//   * send/get/post/delete
//       - Status code, response schema
//   * Namespaces
//       - Path fragments
//
// UNDONE: What message should we print on exception?
//

package com.splunk;

import junit.framework.TestCase;
import org.junit.*;
import static org.junit.Assert.*;

import com.splunk.*;
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

    @Test public void testLogin() {
        // UNDONE: Should be a better way in JUnit to check for exception
        try {
            Service service = new Service(
                program.host, program.port, program.scheme);
            service.login(program.username, program.password);
            assertTrue(true);
        }
        catch (Exception e) {
            assertTrue(false);
        }
    }

    // Make a few simple requests and make sure the results look ok.
    @Test public void testAtom() {
        Service service;

        // UNDONE: login throws an IOException
        try {
            service = new Service(
                program.host, program.port, program.scheme)
                    .login(program.username, program.password);
        }
        catch (Exception e) {
            assertTrue(false);
            return;
        }

        try {
            ResponseMessage response = service.get("/");
            checkResponse(response);
        }
        catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test public void testBaz() {
        assertTrue(true);
    }
}

