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
 * The {@code UdpConnections} class represents a UDP connection.
 */
public class UdpConnections extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The UPD input endpoint.
     */
    UdpConnections(Service service, String path) {
        super(service, path);
    }

    /**
     * Return the server name.
     *
     * @return The server name.
     */
    public String getGroup() {
        return getString("group", null);
    }
}
