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
     * @return {@code true} if Splunk checks Splunkbase for app updates,
     * {@code false} if not.
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
     * Indicates whether to reload objects contained in the locally-installed 
     * app.
     *
     * @return {@code true} if objects are reloaded, {@code false} if not.
     */
    public boolean getRefresh() {
        return getBoolean("refresh", false);
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
     * @return {@code true} if custom setup has been performed, {@code false}
     * if not.
     */
    public boolean isConfigured() {
        return getBoolean("configured", false);
    }

    /**
     * Indicates whether the app is visible and navigable from Splunk Web.
     *
     * @return {@code true} if the app is visible and navigable from Splunk
     * Web, {@code false} if not.
     */
    public boolean isVisible() {
        return getBoolean("visible", false);
    }

    /**
     * Indicates whether a state change requires the app to be restarted.
     *
     * @return {@code true} if state changes require the app to be restarted,
     * {@code false} if not.
     */
    public boolean stateChangeRequiresRestart() {
        return getBoolean("state_change_requires_restart", false);
    }

    /***
     * Sets the  name of the app's author. For Splunkbase apps, this value is 
     * the username of the Splunk.com account. For internal apps, this value is
     * the full name.
     *
     * @param author The author name.
     */
    public void setAuthor(String author) {
        setCacheValue("author", author);
    }

    /**
     * Sets whether Splunk checks Splunkbase for updates to the app.
     *
     * @param value {@code true} if Splunk checks Splunkbase for app updates, 
     * {@code false} if not.
     */
    public void setCheckForUpdates(boolean value) {
        setCacheValue("check_for_updates", value);
    }

    /**
     * Sets whether the app's custom setup has been performed. This field 
     * is available in Splunk 4.2.4 and later.
     *
     * @param value {@code true} if the app has run its custom setup, 
     * {@code false} if not.
     */
    public void setConfigured(boolean value) {
        setCacheValue("configured", value);
    }

    /**
     * Sets a short description of the application, which is displayed below
     * the app's title on the Splunk Home tab in Splunk Web.
     *
     * @param description The short description of the app.
     */
    public void setDescription(String description) {
        setCacheValue("description", description);
    }

    /**
     * Sets the app's name, which is displayed in Splunk Web. The name should be
     * between 5-80 characters and should not include the prefix "Splunk For".
     *
     * @param label The label (name) of the app.
     */
    public void setLabel(String label) {
        setCacheValue("label", label);
    }

    /**
     * Sets the version of the app.
     *
     * @param version The app's version.
     */
    public void setVersion(String version) {
        setCacheValue("version", version);
    }

    /**
     * Sets whether the app is visible and navigable from Splunk Web.
     *
     * @param visible {@code true} if the app can be visible and navigable
     * from Splunk Web, {@code false} if not.
     */
    public void setVisible(boolean visible) {
        setCacheValue("visible", visible);
    }

    /**
     * Archives the app on the server file system. 
     *
     * @return The location of the archived app, as {app_name}.spl.
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

