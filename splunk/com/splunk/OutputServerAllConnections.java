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

public class OutputServerAllConnections extends Entity {
    public OutputServerAllConnections(Service service, String path) {
        super(service, path);
    }

    public String getDestHost() {
        return getString("destHost");
    }

    public String getDestIp() {
        return getString("destIp");
    }

    public int getDestPort() {
        return getInteger("destPort");
    }

    public int getSourcePort() {
        return getInteger("sourcePort");
    }

    public String getStatus() {
        return getString("status");
    }
}
