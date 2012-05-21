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
     * @return {@code true} if Splunk checks Splunkbase for updates,
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
     * Returns whether or not to reload objects contains in the locally
     * installed app.
     *
     * @return  Whether or not to reload objects contains in the locally
     * installed app.
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
     * Indicates whether the app can be managed by Splunk Manager.
     *
     * @return {@code true} if the app can be managed by Splunk Manager,
     * {@code false} if not.
     */
    public boolean isManageable() {
        return getBoolean("manageable", false);
    }

    /**
     * Indicates whether an app is visible and navigable from Splunk Web.
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
     * Sets the  name of the app's author. For Splunkbase
     * apps, this value is the username of the Splunk.com account. For internal
     * apps, this value is the full name.
     *
     * @param author the author name for this splunk application
     */
    public void setAuthor(String author) {
        setCacheValue("author", author);
    }

    /**
     * Sets whether or not Splunk checks Splunkbase for updates to this app.
     *
     * @param value {@code true} if the app is checked for updates
     * {@code false} if it is not to be checked.
     */
    public void setCheckForUpdates(boolean value) {
        setCacheValue("check_for_updates", value);
    }

    /**
     * Sets whether the app's custom setup has been
     * performed. This field is available in Splunk version 4.2.4 and later.
     *
     * @param value {@code true} if the app has executed its custom setup or
     * {@code false} if it has not.
     */
    public void setConfigured(boolean value) {
        setCacheValue("configured", value);
    }

    /**
     * Sets a short description of the application that is displayed underneath
     * the app's title in Launcher.
     *
     * @param description The short description of the application
     */
    public void setDescription(String description) {
        setCacheValue("description", description);
    }

    /**
     * Sets the app's name that is shown in the Splunk GUI and Launcher. Note
     * that the name should be between 5 and 80 characters and not include the
     * "Splunk For" prefix.
     *
     * @param label The label of this app.
     */
    public void setLabel(String label) {
        setCacheValue("label", label);
    }

    /**
     * Sets whether the app can be managed by Splunk Manager.
     *
     * @param value {@code true} if the app can be managed by Splunk Manager,
     * {@code false} if not.
     */
    public void setManageable(boolean value) {
        setCacheValue("manageable", value);
    }

    /**
     * Sets the app's version string.
     *
     * @param version The version string.
     */
    public void setVersion(String version) {
        setCacheValue("version", version);
    }

    /**
     * Sets whether an app is visible and navigable from Splunk Web.
     *
     * @param visible {@code true} if the app is made visible and navigable
     * from Splunk Web, {@code false} if not.
     */
    public void setVisible(boolean visible) {
        setCacheValue("visible", visible);
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

