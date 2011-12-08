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
 * Representation of output syslog.
 */
public class OutputSyslog extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The output syslog endpoint.
     */
    OutputSyslog(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this server's destination host:port.
     *
     * @return This server's destination host:port.
     */
    public String getServer() {
        return getString("server");
    }

    /**
     * Returns this server's connection type.
     *
     * @return this server's connection type.
     */
    public String getType() {
        return getString("type");
    }
}
