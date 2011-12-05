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

import org.junit.Test;

public class DistributedConfigurationTest extends SplunkTestCase {
    @Test public void testDistributedConfiguration() throws Exception {
        Service service = connect();

        DistributedConfiguration distributedConfiguration =
                service.getDistributedConfiguration();

        Args savedSetup = new Args();
        savedSetup.put("autoAddServers",
                distributedConfiguration.getAutoAddServers());
        savedSetup.put("blacklistNames",
                distributedConfiguration.getBlacklistNames());
        savedSetup.put("blacklistURLs",
                distributedConfiguration.getBlacklistUrls());
        savedSetup.put("checkTimedOutServersFrequency",
                distributedConfiguration.getCheckTimedOutServersFrequency());
        savedSetup.put("disabled", distributedConfiguration.isDisabled());
        savedSetup.put("heartbeatFrequency",
                distributedConfiguration.getHeartbeatFrequency());
        savedSetup.put("heartbeatMcastAddr",
                distributedConfiguration.getHeartbeatMcastAddress());
        savedSetup.put("heartbeatPort",
                distributedConfiguration.getHeartbeatPort());
        savedSetup.put("removedTimedOutServers",
                distributedConfiguration.getRemovedTimedOutServers());
        savedSetup.put("serverTimeout",
                distributedConfiguration.getServerTimeout());
        savedSetup.put("servers", distributedConfiguration.getServers());
        savedSetup.put("shareBundles",
                distributedConfiguration.getShareBundles());
        savedSetup.put("skipOurselves",
                distributedConfiguration.getSkipOurselves());
        savedSetup.put("statusTimeout",
                distributedConfiguration.getStatusTimeout());
        savedSetup.put("ttl", distributedConfiguration.getTtl());
        for (String key: savedSetup.keySet()) {
            if (savedSetup.get(key) == null)
                savedSetup.put(key, "");
        }

        Args updateArgs = new Args();

        updateArgs.put("autoAddServers", false);
        updateArgs.put("blacklistNames", "black1,black2,black3");
        updateArgs.put("blacklistURLs", "black1.splunk.com:8089");
        updateArgs.put("checkTimedOutServersFrequency", 120);
        updateArgs.put("disabled", true);
        updateArgs.put("heartbeatFrequency", 120);
        updateArgs.put("heartbeatMcastAddr", "224.0.1.37");
        updateArgs.put("heartbeatPort", 8889);
        updateArgs.put("removedTimedOutServers", false);
        updateArgs.put("serverTimeout", 180);
        updateArgs.put("servers", "good1.splunk.com,good2.splunk.com");
        updateArgs.put("shareBundles", false);
        updateArgs.put("skipOurselves", true);
        updateArgs.put("statusTimeout", 100);
        updateArgs.put("ttl", 5);

        distributedConfiguration.update(updateArgs);
        assertEquals(
                distributedConfiguration.getAutoAddServers(),
                updateArgs.get("autoAddServers"));
        assertEquals(
                distributedConfiguration.getBlacklistNames(),
                updateArgs.get("blacklistNames"));
        assertEquals(
                distributedConfiguration.getBlacklistUrls(),
                updateArgs.get("blacklistURLs"));
        assertEquals(
                distributedConfiguration.getCheckTimedOutServersFrequency(),
                updateArgs.get("checkTimedOutServersFrequency"));
        assertEquals(distributedConfiguration.isDisabled(),
                updateArgs.get("disabled"));
        assertEquals(
                distributedConfiguration.getHeartbeatFrequency(),
                updateArgs.get("heartbeatFrequency"));
        assertEquals(
                distributedConfiguration.getHeartbeatMcastAddress(),
                updateArgs.get("heartbeatMcastAddr"));
        assertEquals(
                distributedConfiguration.getHeartbeatPort(),
                updateArgs.get("heartbeatPort"));
        assertEquals(
                distributedConfiguration.getRemovedTimedOutServers(),
                updateArgs.get("removedTimedOutServers"));
        assertEquals(
                distributedConfiguration.getServerTimeout(),
                updateArgs.get("serverTimeout"));
        assertEquals(distributedConfiguration.getServers(),
                updateArgs.get("servers"));
        assertEquals(
                distributedConfiguration.getShareBundles(),
                updateArgs.get("shareBundles"));
        assertEquals(
                distributedConfiguration.getSkipOurselves(),
                updateArgs.get("skipOurselves"));
        assertEquals(
                distributedConfiguration.getStatusTimeout(),
                updateArgs.get("statusTimeout"));
        assertEquals(distributedConfiguration.getTtl(), updateArgs.get("ttl"));

        // handle nulls specially
        if (savedSetup.get("blacklistNames") == null)
            savedSetup.put("blacklistNames", "");
        if (savedSetup.get("blacklistURLs") == null)
            savedSetup.put("blacklistURLs", "");

        distributedConfiguration.update(savedSetup);

        assertEquals(
                distributedConfiguration.getAutoAddServers(),
                savedSetup.get("autoAddServers"));
        assertEquals(
                distributedConfiguration.getBlacklistNames() == null ? "" :
                distributedConfiguration.getBlacklistNames(),
                savedSetup.get("blacklistNames") );
        assertEquals(
                distributedConfiguration.getBlacklistUrls() == null ? "" :
                distributedConfiguration.getBlacklistUrls(),
                savedSetup.get("blacklistURLs"));
        assertEquals(
                distributedConfiguration.getCheckTimedOutServersFrequency(),
                savedSetup.get("checkTimedOutServersFrequency"));
        assertEquals(distributedConfiguration.isDisabled(),
                savedSetup.get("disabled"));
        assertEquals(
                distributedConfiguration.getHeartbeatFrequency(),
                savedSetup.get("heartbeatFrequency"));
        assertEquals(
                distributedConfiguration.getHeartbeatMcastAddress(),
                savedSetup.get("heartbeatMcastAddr"));
        assertEquals(
                distributedConfiguration.getHeartbeatPort(),
                savedSetup.get("heartbeatPort"));
        assertEquals(
                distributedConfiguration.getRemovedTimedOutServers(),
                savedSetup.get("removedTimedOutServers"));
        assertEquals(
                distributedConfiguration.getServerTimeout(),
                savedSetup.get("serverTimeout"));
        assertEquals(
                distributedConfiguration.getServers() == null ? "" :
                distributedConfiguration.getServers(),
                savedSetup.get("servers"));
        assertEquals(
                distributedConfiguration.getShareBundles(),
                savedSetup.get("shareBundles"));
        assertEquals(
                distributedConfiguration.getSkipOurselves(),
                savedSetup.get("skipOurselves"));
        assertEquals(
                distributedConfiguration.getStatusTimeout(),
                savedSetup.get("statusTimeout"));
        assertEquals(distributedConfiguration.getTtl(),
                savedSetup.get("ttl"));

        distributedConfiguration.getConnectionTimeout();
        distributedConfiguration.getReceiveTimeout();
        distributedConfiguration.getSendTimeout();

        distributedConfiguration.isDistSearchEnabled();
    }
}
