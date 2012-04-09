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
 * The {@code Application} class represents a locally-installed Splunk app.
 */
public class Application extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The application endpoint.
     */
    Application(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the name of the app's author. For Splunkbase
     * apps, this value is the username of the Splunk.com account. For internal
     * apps, this value is the full name.
     *
     * @return The author name, or {@code null} if not specified.
     */
    public String getAuthor() {
        return getString("author", null);
    }

    /**
     * Indicates whether Splunk checks Splunkbase for updates.
     *
     * @return {@code true} if Splunk checks Splunkbase for updates, {@code false} if not.
     */
    public boolean getCheckForUpdates() {
        return getBoolean("check_for_updates", false);
    }

    /**
     * Returns a short description of the app.
     *
     * @return The description, or {@code null} if not specified.
     */
    public String getDescription() {
        return getString("description", null);
    }

    /**
     * Returns the app's label (its name).
     *
     * @return The label, or {@code null} if not specified.
     */
    public String getLabel() {
        return getString("label", null);
    }

    /**
     * Returns the version of the app.
     *
     * @return The version, or {@code null} if not specified.
     */
    public String getVersion() {
        return getString("version", null);
    }

    /**
     * Indicates whether the app's custom setup has been
     * performed. This field is available in Splunk version 4.2.4 and later.
     *
     * @return {@code true} if custom setup has been performed, {@code false} if not.
     */
    public boolean isConfigured() {
        return getBoolean("configured", false);
    }

    /**
     * Indicates whether the app can be managed by Splunk Manager.
     *
     * @return {@code true} if the app can be managed by Splunk Manager, {@code false} if not.
     */
    public boolean isManageable() {
        return getBoolean("manageable", false);
    }

    /**
     * Indicates whether an app is visible and navigable from Splunk Web.
     *
     * @return {@code true} if the app is visible and navigable from Splunk Web, {@code false} if not.
     */
    public boolean isVisible() {
        return getBoolean("visible", false);
    }

    /**
     * Indicates whether a state change requires the app to be restarted.
     *
     * @return {@code true} if state changes require the app to be restarted, {@code false} if not.
     */
    public boolean stateChangeRequiresRestart() {
        return getBoolean("state_change_requires_restart", false);
    }

    /**
     * Archives the app on the server file system. 
     *
     * @return Location of the archived app, as {app_name}.spl.
     */
    public ApplicationArchive archive() {
        return new ApplicationArchive(service, path);
    }

    /**
     * Returns the app's setup information.
     *
     * @return The app's setup information.
     */
    public ApplicationSetup setup() {
        return new ApplicationSetup(service, path);
    }

    /**
     * Returns any update information that is available for the app.
     *
     * @return Update information for the app.
     */
    public ApplicationUpdate getUpdate() {
        return new ApplicationUpdate(service, path);
    }
}

