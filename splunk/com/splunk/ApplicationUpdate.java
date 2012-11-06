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
 * The {@code ApplicationUpdate} class represents information for an update
 * to a locally-installed Splunk app.
 */
public class ApplicationUpdate extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The application endpoint.
     */
    ApplicationUpdate(Service service, String path) {
        super(service, path + "/update");
    }

    /**
     * Returns the fully-qualified URL to the app update.
     *
     * @return The URL of the app update, or {@code null} if not specified.
     */
    public String getAppUrl() {
        return getString("update.appurl", null);
    }

    /**
     * Returns the checksum of the app.
     *
     * @return The checksum of the app, or {@code null} if not specified.
     */
    public String getChecksum() {
        return getString("update.checksum", null);
    }

    /**
     * Returns the checksum type of the app.
     *
     * @return The checksum type, or {@code null} if not specified.
     */
    public String getChecksumType() {
        return getString("update.checksum.type", null);
    }

    /**
     * Returns the fully-qualified URL to the app's homepage.
     *
     * @return The URL of the app's homepage, or {@code null} if not specified.
     */
    public String getHomepage() {
        return getString("update.homepage", null);
    }

    /**
     * Returns the app's name.
     *
     * @return The app's name, or {@code null} if not specified.
     */
    public String getUpdateName() {
        return getString("update.name", null);
    }

    /**
     * Returns the size of the app update.
     *
     * @return The size of the update, in bytes, or -1 if not specified.
     */
    public int getSize() {
        return getInteger("update.size", -1);
    }

    /**
     * Returns the app's version.
     *
     * @return The app's version, or {@code null} if not specified.
     */
    public String getVersion() {
        return getString("update.version", null);
    }

    /**
     * Indicates whether an implicit ID is required.
     *
     * @return {@code true} if an implicit ID is required, {@code false} if not.
     */
    public boolean isImplicitIdRequired() {
        return getBoolean("update.implicit_id_required", false);
    }
}
