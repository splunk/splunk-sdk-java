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
 * Representation of output group.
 */
public class OutputGroup extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The output group endpoint.
     */
    OutputGroup(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this forwarder's group output processor. Valid values are from
     * the set tcpout, syslog, httpout.
     *
     * @return This forwarder's group output processor.
     */
    public String getMethod() {
        return getString("method", null);
    }

    /**
     * Returns this forwarder group's server list.
     *
     * @return This forwarder group's server list.
     */
    public String[] getServers() {
        return getStringArray("servers");
    }
}
