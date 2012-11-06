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
 * The {@code ApplicationArchive} class represents an archive of a Splunk app.
 */
public class ApplicationArchive extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The application endpoint.
     */
    ApplicationArchive(Service service, String path) {
        super(service, path + "/package");
    }

    /**
     * Returns the name of the app.
     *
     * @return The app's name.
     */
    public String getAppName() {
        return getString("name");
    }

    /**
     * Returns a path indicating where the app archive file is stored on the
     * server, for direct file access.
     *
     * @return The path to the archive file.
     */
    public String getFilePath() {
        return getString("path");
    }

    /**
     * Indicates whether to reload the objects contained in the 
     * locally-installed app.
     *
     * @return {@code true} if objects are reloaded, {@code false} if not.
     */
    public boolean getRefresh() {
        return getBoolean("refresh", false);
    }

    /**
     * Returns a URL to the app archive file on the server, for web browser
     * access.
     *
     * @return The URL to the archive file.
     */
    public String getUrl() {
        return getString("url");
    }
}

