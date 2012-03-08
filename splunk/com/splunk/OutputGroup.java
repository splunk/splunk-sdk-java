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
 * The {@code OutputGroup} class represents an output group, providing
 * access to the configuration of a group of one or more data-forwarding destinations.
 */
public class OutputGroup extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The output group endpoint.
     */
    OutputGroup(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the type of output processor for this forwarder group. 
     * Valid values are: tcpout, syslog, and httpout.
     *
     * @return The output processor type, or {@code null} if not specified.
     */
    public String getMethod() {
        return getString("method", null);
    }

    /**
     * Returns the list of servers for this forwarder group.
     *
     * @return The server list.
     */
    public String[] getServers() {
        return getStringArray("servers");
    }
}
