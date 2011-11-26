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
        assertFalse(passwords.containsKey(name));

        Password password;

        // Create a password without realm
        password = passwords.create(name, value);
        assertTrue(passwords.containsKey(name));
        assertEquals(password.getUsername(), name);
        assertEquals(password.getClearPassword(), value);
        assertEquals(password.getRealm(), null);

        Args args;

        // Update the password
        args = new Args("password", "foobar");
        password.update(args);
        assertTrue(passwords.containsKey(name));
        assertEquals(password.getUsername(), name);
        assertEquals(password.getClearPassword(), "foobar");
        assertEquals(password.getRealm(), null);

        passwords.remove(name);
        assertFalse(passwords.containsKey(name));

        // Create a password with a realm
        password = passwords.create(name, value, realm);
        assertTrue(passwords.containsKey(name));
        assertEquals(password.getUsername(), name);
        assertEquals(password.getClearPassword(), value);
        assertEquals(password.getRealm(), realm);

        // Update the password
        args = new Args("password", "bizbaz");
        password.update(args);
        assertTrue(passwords.containsKey(name));
        assertEquals(password.getUsername(), name);
        assertEquals(password.getClearPassword(), "bizbaz");
        assertEquals(password.getRealm(), realm);

        passwords.remove(name);
        assertFalse(passwords.containsKey(name));
    }
}

