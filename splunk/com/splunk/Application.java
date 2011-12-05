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
 * Representation of the Splunk Application, commonly referred to as an 'app'.
 */
public class Application extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     * @param path The application endpoint.
     */
    Application(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the app's author name. If a splunkbase app, this should be the
     * username of the splunk.com account. For internal apps, this should be
     * full contact info.
     *
     * @return Splunk author's name string, or null if none exists.
     */
    public String getAuthor() {
        return getString("author", null);
    }

    /**
     * Returns whether or not Splunk checks splunkbase for updates.
     *
     * @return Whether or not Splunk checks splunkbase for updates.
     */
    public boolean getCheckForUpdates() {
        return getBoolean("check_for_updates", false);
    }

    /**
     * Returns the description string which is a short explanatory note that
     * is displayed underneath the app's title in Launcher.
     *
     * @return The description string, or null if none exists.
     */
    public String getDescription() {
        return getString("description", null);
    }

    /**
     * Returns the label string of the app which shows in the Splunk GUI or
     * Launcher.
     *
     * @return The label string, or null if none exists.
     */
    public String getLabel() {
        return getString("label", null);
    }

    /**
     * Returns the version string of the app.
     *
     * @return The version string of the app, or null if none exists.
     */
    public String getVersion() {
        return getString("version", null);
    }

    /**
     * Returns an indication if the app's custom setup has been
     * performed. This field is only available in Splunk's version 4.2.4+.
     *
     * @return Indicates if the app's custom setup has been performed.
     */
    public boolean isConfigured() {
        return getBoolean("configured", false);
    }

    /**
     * Returns whether or not the app can be managed by Splunk Manager.
     *
     * @return Indicates if the app can be managed by Splunk Manager or not.
     */
    public boolean isManageable() {
        return getBoolean("manageable", false);
    }

    /**
     * Returns whether or not an app is visible and navigable from the Splunk
     * GUI.
     *
     * @return Indicates if the app is visible and navigable from the Splunk
     *         GUI.
     */
    public boolean isVisible() {
        return getBoolean("visible", false);
    }

    /**
     * Indicates whether ot not an app state change requires [UNDONE] the app
     * or Splunk to be restarted.
     *
     * @return Indicates if [UNDONE] the app or Splunk needs to be restarted.
     */
    public boolean stateChangeRequiresRestart() {
        return getBoolean("state_change_requires_restart", false);
    }

    /**
     * Archives the appliction into a .spl file on the server's file system.
     * The return object contains location specific information.
     *
     * @return Archival information of the app.
     */
    public ApplicationArchive archive() {
        return new ApplicationArchive(service, path);
    }

    /**
     * Returns the the app's setup information.
     *
     * @return Returns the app's setup information.
     */
    public ApplicationSetup setup() {
        return new ApplicationSetup(service, path);
    }

    /**
     * Returns update information of the app.
     *
     * @return Returns update information for the app.
     */
    public ApplicationUpdate update() {
        return new ApplicationUpdate(service, path);
    }
}

