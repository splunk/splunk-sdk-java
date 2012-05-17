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
 * The {@code OutputSyslog} class represents a syslog output, providing access
 * to properties of a forwarding server that provides data in standard syslog 
 * format.
 */
public class OutputSyslog extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The output syslog endpoint.
     */
    OutputSyslog(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the destination host:port of this server.
     *
     * @return The destination host:port.
     */
    public String getServer() {
        return getString("server");
    }

    /**
     * Returns the connection type of this server.
     *
     * @return The connection type.
     */
    public String getType() {
        return getString("type");
    }

    /**
     * Sets the syslog priority.
     *
     * @param priority The syslog priority.
     */
    public void setPriority(int priority) {
        setCacheValue("priority", priority);
    }

    /**
     * Sets the location, {@code host:port} where the syslog is sent.
     *
     * @param server The destination host and port to send the syslog.
     */
    public void setServer(String server) {
        setCacheValue("server", server);
    }

    /**
     * Sets the time stamp format that precedes each event sent.
     *
     * @param timestampformat the time stamp format.
     */
    public void setTimestampFormat(String timestampformat) {
        setCacheValue("timestampformat", timestampformat);
    }

    /**
     * Sets protocol type. Valid values are {@code tcp} or {@code udp}.
     *
     * @param type The protocol type for syslog transmission.
     */
    public void setType(String type) {
        setCacheValue("type", type);
    }
}
