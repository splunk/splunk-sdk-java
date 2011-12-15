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
 * Representation of a monitor input.
 */
public class MonitorInput extends Input {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The monitor input endpoint.
     */
    MonitorInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this monitor input's filecount.
     *
     * @return This monitor input's filecount.
     */
    public int getFileCount() {
        return getInteger("filecount", -1);
    }

    /**
     * Returns this monitor input's host, or null if not specified.
     *
     * @return This monitor input's host.
     */
    public String getHost() {
        return getString("host", null);
    }

    /**
     * Returns this monitor input's index name.
     *
     * @return This monitor input's index name.
     */
    public String getIndex() {
        return getString("index");
    }

    /**
     * Returns the monitor input kind.
     *
     * @return The monitor input kind.
     */
    public InputKind getKind() {
        return InputKind.Monitor;
    }

    /**
     * Returns this monitor input's _rcvbuf attribute.
     *
     * @return This monitor input's _rcvbuf attribute.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }
}
