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

import org.junit.Test;

public class PasswordTest extends SplunkTestCase {
    final static String assertRoot = "Password assert: ";

    void checkPassword(Password password) {
        password.getClearPassword();
        password.getEncryptedPassword();
        password.getPassword();
        password.getRealm();
        password.getUsername();
    }

    @Test public void testPasswords() {
        Service service = connect();
        for (Password password : service.getPasswords().values())
            checkPassword(password);
    }

    @Test public void testPasswordCrud() {
        Service service = connect();

        PasswordCollection passwords = service.getPasswords();

        String name = "sdk-test";
        String value = "sdk-test-password";
        String realm = "sdk-test-realm";

        if (passwords.containsKey(name))
            passwords.remove(name);
        assertFalse(assertRoot + "#1", passwords.containsKey(name));

        Password password;

        // Create a password without realm
        password = passwords.create(name, value);
        assertTrue(assertRoot + "#2", passwords.containsKey(name));
        assertEquals(assertRoot + "#3", name, password.getUsername());
        assertEquals(assertRoot + "#4", value, password.getClearPassword());
        assertEquals(assertRoot + "#5", null, password.getRealm());

        Args args;

        // Update the password
        args = new Args("password", "foobar");
        password.update(args);
        assertTrue(assertRoot + "#6", passwords.containsKey(name));
        assertEquals(assertRoot + "#7", name, password.getUsername());
        assertEquals(assertRoot + "#8", "foobar", password.getClearPassword());
        assertEquals(assertRoot + "#9", null, password.getRealm());

        passwords.remove(name);
        assertFalse(passwords.containsKey(name));

        // Create a password with a realm
        password = passwords.create(name, value, realm);
        assertTrue(assertRoot + "#10", passwords.containsKey(name));
        assertEquals(assertRoot + "#11", name, password.getUsername());
        assertEquals(assertRoot + "#12", value, password.getClearPassword());
        assertEquals(assertRoot + "#13", realm, password.getRealm());

        // Update the password
        args = new Args("password", "bizbaz");
        password.update(args);
        assertTrue(assertRoot + "#14", passwords.containsKey(name));
        assertEquals(assertRoot + "#15", name, password.getUsername());
        assertEquals(assertRoot + "#16", "bizbaz", password.getClearPassword());
        assertEquals(assertRoot + "#17", realm, password.getRealm());

        passwords.remove(name);
        assertFalse(assertRoot + "#18", passwords.containsKey(name));
    }
}

