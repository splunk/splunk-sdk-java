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
 * Representation of a Splunk application archive.
 */
public class ApplicationArchive extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path the full-path of the parent endpoint.
     */
    ApplicationArchive(Service service, String path) {
        super(service, path + "/package");
    }

    /**
     * Returns the app name.
     *
     * @return The app name string.
     */
    public String getAppName() {
        return getString("name");
    }

    /**
     * Returns the file path, on the server where the archive file is stored,
     * accessible with direct file access on the server.
     *
     * @return The server file path of the archive file.
     */
    public String getFilePath() {
        return getString("path");
    }

    /**
     * Returns the URL that points to the archive file on the server, accessible
     * with a browser.
     *
     * @return The URL of the archive file.
     */
    public String getUrl() {
        return getString("url");
    }
}

