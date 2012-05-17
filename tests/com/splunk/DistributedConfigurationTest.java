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

        // use setters for update
        distributedConfiguration.setAutoAddServers(false);
        distributedConfiguration.setBlacklistNames("black1,black2,black3");
        distributedConfiguration.setBlacklistURLs("black1.splunk.com:8089");
        distributedConfiguration.setCheckTimedOutServersFrequency(120);
        distributedConfiguration.setDisabled(true);
        distributedConfiguration.setHeartbeatFrequency(120);
        distributedConfiguration.setHeartbeatMcastAddr("224.0.1.37");
        distributedConfiguration.setHeartbeatPort(8889);
        distributedConfiguration.setRemoveTimedOutServers(false);
        distributedConfiguration.setSendTimeout(120);
        distributedConfiguration.setServers("good.splunk.com,good2.splunk.com");
        distributedConfiguration.setShareBudles(false);
        distributedConfiguration.setSkipOurselves(true);
        distributedConfiguration.setStatusTimeout(100);
        distributedConfiguration.setTTL(5);
        distributedConfiguration.update();

        assertEquals(distributedConfiguration.getAutoAddServers(), false);
        assertEquals(distributedConfiguration.getBlacklistNames(),
                "black1,black2,black3");
        assertEquals(distributedConfiguration.getBlacklistUrls(),
                "black1.splunk.com:8089");
        assertEquals(
                distributedConfiguration.getCheckTimedOutServersFrequency(),
                120);
        assertEquals(distributedConfiguration.isDisabled(),true);
        assertEquals(distributedConfiguration.getHeartbeatFrequency(), 120);
        assertEquals(distributedConfiguration.getHeartbeatMcastAddress(),
                "224.0.1.37");
        assertEquals(distributedConfiguration.getHeartbeatPort(), 8889);
        assertEquals(distributedConfiguration.getRemovedTimedOutServers(),
                false);
        assertEquals(distributedConfiguration.getServers(),
                "good.splunk.com,good2.splunk.com");
        assertEquals(distributedConfiguration.getShareBundles(), false);
        assertEquals(distributedConfiguration.getSkipOurselves(), true);
        assertEquals(distributedConfiguration.getStatusTimeout(), 100);
        assertEquals(distributedConfiguration.getTtl(), 5);

        // handle nulls specially
        if (savedSetup.get("blacklistNames") == null)
            savedSetup.put("blacklistNames", "");
        if (savedSetup.get("blacklistURLs") == null)
            savedSetup.put("blacklistURLs", "");

        // use map method for update.
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
