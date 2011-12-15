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
 * Representation of a Splunk user.
 */
public class User extends Entity {

    /**
     * Constructs an instance of the User entity.
     *
     * @param service The service the entity is affiliated with.
     * @param path The resource path.
     */
    User(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the user's default app.
     *
     * @return The user's default app.
     */
    public String getDefaultApp() {
        return getString("defaultApp", null);
    }

    /**
     * Answers if the user's default app was set specifically by the user.
     *
     * @return {@code true} if the default app was set by the user.
     */
    public boolean getDefaultAppIsUserOverride() {
        return getBoolean("defaultAppIsUserOverride");
    }

    /**
     * If the default app was inherited from a role, this returns the name of
     * the role it was inherited from, or {@code system} if it was inherited
     * from the default system setting.
     *
     * @return The name of the role the default app was inherited from, or
     *         {@code system} if it was inherited from the default system
     *         settings.
     */
    public String getDefaultAppSourceRole() {
        return getString("defaultAppSourceRole");
    }

    /**
     * Returns the user's email address.
     *
     * @return The user's email address.
     */
    public String getEmail() {
        return getString("email", null);
    }

    /**
     * Returns the user's password.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return getString("password", null);
    }

    /**
     * Returns the full name associated with this user.
     *
     * @return The full name associated with this user.
     */
    public String getRealName() {
        return getString("realname", null);
    }

    /**
     * Returns an array of roles assigned to this user.
     *
     * @return Array of roles assigned to this user.
     */
    public String[] getRoles() {
        return getStringArray("roles", null);
    }

    /**
     * Returns the authentication method responsible for this user.
     *
     * @return The authentication method repsonsible for this user.
     */
    public String getType() {
        return getString("type", null);
    }

    /**
     * Returns the timezone to use when displaying dates for this user.
     *
     * @return The timezone to use when displaying dates for this user.
     */
    public String getTz() {
        return getString("tz", null);
    }
}
