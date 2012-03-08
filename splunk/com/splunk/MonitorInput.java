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
* The {@code MonitorInput} class represents a monitor input, which is a file,
* directory, script, or network port that is monitored for new data.
 */
public class MonitorInput extends Input {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The monitor input endpoint.
     */
    MonitorInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the file count of this monitor input.
     *
     * @return The file count.
     */
    public int getFileCount() {
        return getInteger("filecount", -1);
    }

    /**
     * Returns the host for this monitor input.
     *
     * @return The host, or {@code null} if not specified.
     */
    public String getHost() {
        return getString("host", null);
    }

    /**
     * Returns the index name for this monitor input.
     *
     * @return The index name.
     */
    public String getIndex() {
        return getString("index");
    }

    /**
     * Returns the type of monitor input.
     * @see InputKind
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.Monitor;
    }

    /**
     * Returns value of the {@code _rcvbuf} attribute for this monitor input.
     *
     * @return The {@code _rcvbuf} value.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }
}
