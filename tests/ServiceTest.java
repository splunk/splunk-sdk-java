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

// UNDONE:
//   * HTTP interface, send, get, post, delete
//      = Headers, result codes ...
//   * com.splunk
//      = Authentication
//      = Namespaces
//          - Path fragments

package com.splunk.sdk.tests;

import org.junit.*;
import static org.junit.Assert.*;

import com.splunk.*;
// import com.splunk.http.*;
import com.splunk.sdk.Program;

public class ServiceTest extends Program {
    public ServiceTest() {}

    @Before
    public void setUp() {
        super.init(); // Pick up .splunkrc settings
    }

    @Test 
    public void testLogin() {
        try {
            Service service = new Service(this.host, this.port, this.scheme);
            service.login(this.username, this.password);
            assertTrue(true);
        }
        catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test 
    public void testBar() {
        assertTrue(true);
    }

    @Test 
    public void testBaz() {
        assertTrue(true);
    }
}
