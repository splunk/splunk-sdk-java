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

public class Application extends Entity {
    Application(Service service, String path) {
        super(service, path);
    }

    /**
     *
     * @return The name of the app author or null if none exists. If a
     *         splunkbase app, this should be the username of the splunk.com
     *         account. For internal apps, this should be full contact info.
     */
    public String getAuthor() {
        return getString("author", null);
    }

    /**
     *
     * @return Whether or not Splunk checks splunkbase for updates.
     */
    public boolean getCheckForUpdates() {
        return getBoolean("check_for_updates", false);
    }

    /**
     *
     * @return A short explanatory that is displayed underneath the app's
     *         title in the launcher or null if none exists.
     */
    public String getDescription() {
        return getString("description", null);
    }

    /**
     *
     * @return The name of the app shown in the Splunk GUI and launcher or null
     *         if none exists.
     */
    public String getLabel() {
        return getString("label", null);
    }

    /**
     *
     * @return The version number of the app or null if none exists.
     */
    public String getVersion() {
        return getString("version", null);
    }

    /**
     *
     * @return Indicates if the app's custom setup has been performed (4.2.4+)
     */
    public boolean isConfigured() {
        return getBoolean("configured", false);
    }

    /**
     *
     * @return Indicates if the app can be managed by Splunk Manager.
     */
    public boolean isManageable() {
        return getBoolean("manageable", false);
    }

    /**
     *
     * @return Indicates if the app is visible and navigable from the Splunk
     *         GUI.
     */
    public boolean isVisible() {
        return getBoolean("visible", false);
    }

    /**
     *
     * @return Indicates if the [UNDONE] app or Splunk needs to be restarted.
     */
    public boolean stateChangeRequiresRestart() {
        return getBoolean("state_change_requires_restart", false);
    }

    /**
     *
     * @return Archives the app.
     */
    public ApplicationArchive archive() {
        return new ApplicationArchive(service, path + "/package");
    }

    /**
     *
     * @return Returns the app's setup information.
     */
    public ApplicationSetup setup() {
        return new ApplicationSetup(service, path + "/setup");
    }

    /**
     *
     * @return Returns any update information for the app.
     */
    public ApplicationUpdate update() {
        return new ApplicationUpdate(service, path + "/update");
    }
}

