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

/**
 * The {@code Password} class represents a saved credential.
 */
public class Password extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The password endpoint.
     */
    Password(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the clear-text password for this credential.
     *
     * @return The clear-text password.
     */
    public String getClearPassword() {
        return getString("clear_password");
    }

    /**
     * Returns the encrypted password for this credential.
     *
     * @return The encrypted password.
     */
    public String getEncryptedPassword() {
        return getString("encr_password");
    }

    /**
     * Returns the username for this credential.
     *
     * @return The username.
     */
    @Override public String getName() {
        return getUsername();
    }

    /**
     * Returns the displayable password string for this credential.
     *
     * @return The displayable password string, as asterisks.
     */
    public String getPassword() {
        return getString("password");
    }

    /**
     * Returns the credential realm.
     *
     * @return The realm.
     */
    public String getRealm() {
        return getString("realm", null);
    }

    /**
     * Returns the username for this credential.
     *
     * @return The username.
     */
    public String getUsername() {
        return getString("username");
    }


    /**
     * Sets the password for this credential.
     *
     * @param password The password.
     */
    public void setPassword(String password) {
        setCacheValue("password", password);
    }
}
