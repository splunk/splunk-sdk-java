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

/**
 * Represenration of Credentials (Password).
 */
public class Password extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The password endpoint.
     */
    Password(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this credential's clear-text password.
     *
     * @return This credential's clear-text password.
     */
    public String getClearPassword() {
        return getString("clear_password");
    }

    /**
     * Returns this credential's encrypted password.
     *
     * @return This credential's encrypted password.
     */
    public String getEncryptedPassword() {
        return getString("encr_password");
    }

    /**
     * Returns this credentials username.
     *
     * @return This credentials username.
     */
    @Override public String getName() {
        return getUsername();
    }

    /**
     * Returns this credentials displayable password string.
     *
     * @return This credentials displayable password string.
     */
    public String getPassword() {
        return getString("password");
    }

    /**
     * Returns this credentials realm.
     *
     * @return This credentials realm.
     */
    public String getRealm() {
        return getString("realm", null);
    }

    /**
     * Returns this credentials username.
     *
     * @return This credentials username.
     */
    public String getUsername() {
        return getString("username");
    }
}
