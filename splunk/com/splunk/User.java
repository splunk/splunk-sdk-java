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
 * The {@code User} class represents a Splunk user who is registered on the
 * current Splunk server.
 */
public class User extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The resource path.
     */
    User(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the user's default app.
     *
     * @return The default app.
     */
    public String getDefaultApp() {
        return getString("defaultApp", null);
    }

    /**
     * Indicates whether the user's default app was set specifically by the user.
     *
     * @return {@code true} if the default app was set by the user, {@code false} if not.
     */
    public boolean getDefaultAppIsUserOverride() {
        return getBoolean("defaultAppIsUserOverride");
    }

    /**
     * Returns the name of the role that the default app was inherited from, or 
     * {@code system} if it was inherited from the default system setting.
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
     * @return The user's full name.
     */
    public String getRealName() {
        return getString("realname", null);
    }

    /**
     * Returns an array of roles assigned to this user.
     *
     * @return An array of roles.
     */
    public String[] getRoles() {
        return getStringArray("roles", null);
    }

    /**
     * Returns the authentication method responsible for this user.
     *
     * @return The authentication method for the user.
     */
    public String getType() {
        return getString("type", null);
    }

    /**
     * Returns the time zone to use when displaying dates for this user.
     *
     * @return The user's time zone.
     */
    public String getTz() {
        return getString("tz", null);
    }

    /**
     * Sets the default app for this user.
     *
     * @param defaultApp The default app for this user.
     */
    public void setDefaultApp(String defaultApp) {
        setCacheValue("defaultApp", defaultApp);
    }

    /**
     * Sets the email address for this user.
     *
     * @param email The email address for this user.
     */
    public void setEmail(String email) {
        setCacheValue("email", email);
    }

    /**
     * Sets this user's password.
     *
     * @param password This user's password.
     */
    public void setPassword(String password) {
        setCacheValue("password", password);
    }

    /**
     * Sets this user's real name.
     *
     * @param realname This user's real name.
     */
    public void setRealName(String realname) {
        setCacheValue("realname", realname);
    }

    /**
     * Sets whether or not to restart  background search jobs when Splunk
     * restarts.
     *
     * If {@code true}, a background search job for this user that has not
     * completed is restarted when Splunk restarts.
     *
     * @param restart_background_jobs whether or not to restart background
     * search jobs when Splunk restarts.
     */
    public void setRestartBackgroundJobs(boolean restart_background_jobs) {
        setCacheValue("restart_background_jobs", restart_background_jobs);
    }

    /**
     * Sets this user's roles.
     *
     * @param roles This user's roles.
     */
    public void setRoles(String[] roles) {
        setCacheValue("roles", roles);
    }

    /**
     * Sets this user's time zone, for display purposes.
     *
     * Note this is valid for splunk 4.3+ only.
     *
     * @param tz This user's timezone.
     */
    public void setRoles(String tz) {
        setCacheValue("tz", tz);
    }
}
