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

import java.util.List;

public class DistributedConfiguration extends Entity {
    public DistributedConfiguration(Service service) {
        super(service, "search/distributed/config");
    }

    public boolean getAutoAddServers() {
        return getBoolean("autoAddServers");
    }

    public List<String> getBlacklistNames() {
        return (List<String>)getValue("blacklistNames");
    }

    public List<String> getBlacklistUrls() {
        return (List<String>)getValue("blacklistURLs");
    }

    public int getCheckTimedOutServersFrequency() {
        return getInteger("checkTimedOutServersFrequency");
    }

    public int getHeartbeatFrequency() {
        return getInteger("heartbeatFrequency");
    }

    public String getHeartbeatMcastAddress() {
        return getString("heartbeatMcastAddr", null);
    }

    public int getHeartbeatPort() {
        return getInteger("heartbeatPort");
    }

    public boolean getRemovedTimedOutServers() {
        return getBoolean("removedTimedOutServers");
    }

    public int getServerTimeout() {
        return getInteger("serverTimeout");
    }

    public String getServers() {
        return getString("servers", null);
    }

    public boolean getShareBundles() {
        return getBoolean("shareBundles");
    }

    public boolean getSkipOurselves() {
        return getBoolean("skipOurselves");
    }

    public int getStatusTimeout() {
        return getInteger("statusTimeout");
    }

    public int getTtl() {
        return getInteger("ttl");
    }

    public boolean isDisabled() {
        return getBoolean("disabled");
    }

    //UNDONE: docs can post these, example does not return them in Atom.
    //connectionTimeout (int)  *
    //receiveTimeout (int)     *
    //sendTimeout (int)        *

}

