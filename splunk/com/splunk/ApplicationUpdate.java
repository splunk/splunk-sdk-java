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
 * Representation of the Splunk Application update information.
 */
public class ApplicationUpdate extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path the full-path of the parent end point.
     */
    ApplicationUpdate(Service service, String path) {
        super(service, path + "/update");
    }

    /**
     * Returns the app's fully qualified update URL, or null if not present.
     *
     * @return the app's update URL.
     */
    public String getAppUrl() {
        return getString("update.appurl", null);
    }

    /**
     * Returns the checksum of the app, or null if not present.
     *
     * @return The checksum of them app.
     */
    public String getChecksum() {
        return getString("update.checksum", null);
    }

    /**
     * Returns the checksum type of the app, or null if not present.
     *
     * @return the checksum type of the app.
     */
    public String getChecksumType() {
        return getString("update.checksum.type", null);
    }

    /**
     * Returns the app's fully qualified homepage URL, or null if not
     * present.
     *
     * @return The app's homepage URL.
     */
    public String getHomepage() {
        return getString("update.homepage", null);
    }

    /**
     * Returns the app's name.
     *
     * @return The app's name.
     */
    public String getUpdateName() {
        return getString("update.name", null);
    }

    /**
     * Returns the app's update size, in bytes.
     *
     * @return The app's update size.
     */
    public int getSize() {
        return getInteger("update.size", -1);
    }

    /**
     * Returns the app's version string.
     *
     * @return The app's version.
     */
    public String getVersion() {
        return getString("update.version", null);
    }

    /**
     * Returns whether or not implicit ID is required.
     *
     * @return A boolean indicating if implicit ID is required.
     */
    public boolean isImplicitIdRequired() {
        return getBoolean("update.implicit_id_required", false);
    }
}
