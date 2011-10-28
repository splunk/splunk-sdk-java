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
//   * POST, DELETE
//   * Namespaces
//       - Path fragments
//

package com.splunk;

import junit.framework.TestCase;
import org.junit.*;
import static org.junit.Assert.*;

import com.splunk.*;
import com.splunk.atom.*;
import com.splunk.http.HTTPException;
import com.splunk.http.ResponseMessage;
import com.splunk.sdk.Program;

public class ServiceTest extends TestCase {
    Program program = new Program();

    public ServiceTest() {}

    void checkResponse(ResponseMessage response) {
        assertEquals(200, response.getStatus());
        try {
            // Make sure we can at least load the Atom response
            AtomFeed feed = AtomFeed.parse(response.getContent());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    Service connect() {
        Service service  = new Service(
            program.host, program.port, program.scheme);
        service.login(program.username, program.password);
        return service;
    }

    @Before public void setUp() {
        this.program.init(); // Pick up .splunkrc settings
    }

    // Make a few simple requests and make sure the results look ok.
    @Test public void testGet() {
        Service service = connect();

        // Check a few paths that we know exist
        String[] paths = { "/", "/services", "/services/search/jobs" };
        for (String path : paths)
            checkResponse(service.get(path));

        // And make sure we get the expected 404
        try {
            ResponseMessage response = service.get("/zippy");
            fail("Expected HTTPException");
        }
        catch (HTTPException e) {
            assertEquals(e.getStatus(), 404);
        }
    }

    @Test public void testLogin() {
        ResponseMessage response;

        Service service = new Service(
            program.host, 
            program.port, 
            program.scheme);

        // Not logged in, should fail with 401
        try {
            response = service.get("/services/authentication/users");
            fail("Expected HTTPException");
        }
        catch (HTTPException e) {
            assertEquals(e.getStatus(), 401);
        }

        // Logged in, request should succeed
        service.login(program.username, program.password);
        response = service.get("/services/authentication/users");
        checkResponse(response);

        // Logout, the request should fail with a 401
        service.logout();
        try {
            response = service.get("/services/authentication/users");
            fail("Expected HTTPException");
        }
        catch (HTTPException e) {
            assertEquals(e.getStatus(), 401);
        }
    }

    @Test public void testUsers() {
        User user;

        Service service = connect();

        String username = "sdk-user";
        String password = "changeme";

        UserCollection users = service.getUsers();

        users.remove(username); // cleanup
        assertFalse(users.containsKey(username));

        Args args = new Args();
        args.put("password", password);
        args.put("roles", "power");
        user = users.create(username, args);
        assertTrue(users.containsKey(username));
        assertEquals(user.getName(), username);
        assertTrue(user.getRoles().size() == 1);
        assertTrue(user.getRoles().contains("power"));

        users.remove(username);
        assertFalse(users.containsKey(username));

        user = users.create(username, password, "power");
        assertTrue(users.containsKey(username));
        assertEquals(user.getName(), username);
        assertTrue(user.getRoles().size() == 1);
        assertTrue(user.getRoles().contains("power"));

        users.remove(username);
        assertFalse(users.containsKey(username));

        user = users.create(
            username, password, new String[] { "power", "user" });
        assertTrue(users.containsKey(username));
        assertEquals(user.getName(), username);
        assertTrue(user.getRoles().size() == 2);
        assertTrue(user.getRoles().contains("power"));
        assertTrue(user.getRoles().contains("user"));

        users.remove(username);
        assertFalse(users.containsKey(username));
    }
}

