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

package com.splunk.examples.explorer;

import com.splunk.Entity;

class DistributedConfigurationNode extends EntityNode {
    DistributedConfigurationNode(Entity value) { 
        super(value); 
        setDisplayName("Distributed Configuration");
    }

    @Override protected PropertyList getMetadata() {
        PropertyList list = super.getMetadata();
        list.add(boolean.class, "getAutoAddServers");
        list.add(String[].class, "getBlacklistNames");
        list.add(String[].class, "getBlacklistUrls");
        list.add(int.class, "getCheckTimedOutServersFrequency");
        list.add(int.class, "getHeartbeatFrequency");
        list.add(String.class, "getHeartbeatMcastAddress");
        list.add(int.class, "getHeartbeatPort");
        list.add(boolean.class, "getRemovedTimedOutServers");
        list.add(int.class, "getServerTimeout");
        list.add(String.class, "getServers");
        list.add(boolean.class, "getShareBundles");
        list.add(boolean.class, "getSkipOurselves");
        list.add(int.class, "getStatusTimeout");
        list.add(int.class, "getTtl");
        list.add(boolean.class, "isDisabled");
        return list;
    }
}
