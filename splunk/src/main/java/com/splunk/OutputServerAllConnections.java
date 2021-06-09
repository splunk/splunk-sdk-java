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
 * The {@code OutputServerAllConnections} class represents all the connections 
 * of an output server.
 */
public class OutputServerAllConnections extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The output server all-connections endpoint.
     */
    OutputServerAllConnections(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the DNS name of the destination server.
     *
     * @return The destination host name.
     */
    public String getDestHost() {
        return getString("destHost");
    }

    /**
     * Returns the IP address of the destination server for this connection.
     *
     * @return The IP address of the destination server.
     */
    public String getDestIp() {
        return getString("destIp");
    }

    /**
     * Return the port on which the destination server is listening.
     *
     * @return The destination port.
     */
    public int getDestPort() {
        return getInteger("destPort");
    }

    /**
     * Returns the port on the destination server where data is forwarded.
     *
     * @return The source port.
     */
    public int getSourcePort() {
        return getInteger("sourcePort");
    }

    /**
     * Returns the status of this connection.
     *
     * @return The status.
     */
    public String getStatus() {
        return getString("status");
    }
}
