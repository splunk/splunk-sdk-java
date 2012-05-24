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
    final static String assertRoot = "Distributed Configuration assert: ";

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
        distributedConfiguration.setShareBundles(false);
        distributedConfiguration.setSkipOurselves(true);
        distributedConfiguration.setStatusTimeout(100);
        distributedConfiguration.setTTL(5);
        distributedConfiguration.update();

        assertFalse(assertRoot + "#1",
            distributedConfiguration.getAutoAddServers());
        assertEquals(assertRoot + "#2",
            "black1,black2,black3",
            distributedConfiguration.getBlacklistNames());
        assertEquals(assertRoot + "#3",
            "black1.splunk.com:8089",
            distributedConfiguration.getBlacklistUrls());
        assertEquals(assertRoot + "#4",
            120, distributedConfiguration.getCheckTimedOutServersFrequency());
        assertTrue(assertRoot + "#5", distributedConfiguration.isDisabled());
        assertEquals(assertRoot + "#6",
            120, distributedConfiguration.getHeartbeatFrequency());
        assertEquals(assertRoot + "#7",
            "224.0.1.37", distributedConfiguration.getHeartbeatMcastAddress());
        assertEquals(assertRoot + "#8",
            8889, distributedConfiguration.getHeartbeatPort());
        assertFalse(assertRoot + "#9",
            distributedConfiguration.getRemovedTimedOutServers());
        assertEquals(assertRoot + "#10",
            "good.splunk.com,good2.splunk.com",
            distributedConfiguration.getServers());
        assertFalse(assertRoot + "#11",
            distributedConfiguration.getShareBundles());
        assertTrue(assertRoot + "#12",
            distributedConfiguration.getSkipOurselves());
        assertEquals(assertRoot + "#13",
            100, distributedConfiguration.getStatusTimeout());
        assertEquals(assertRoot + "#14", 5, distributedConfiguration.getTtl());

        // handle nulls specially
        if (savedSetup.get("blacklistNames") == null)
            savedSetup.put("blacklistNames", "");
        if (savedSetup.get("blacklistURLs") == null)
            savedSetup.put("blacklistURLs", "");

        // use map method for update.
        distributedConfiguration.update(savedSetup);

        assertEquals(assertRoot + "#15",
            savedSetup.get("autoAddServers"),
            distributedConfiguration.getAutoAddServers());
        assertEquals(assertRoot + "#16",
            savedSetup.get("blacklistNames"),
            distributedConfiguration.getBlacklistNames() == null ? "" :
            distributedConfiguration.getBlacklistNames());
        assertEquals(assertRoot + "#17",
            savedSetup.get("blacklistURLs"),
            distributedConfiguration.getBlacklistUrls() == null ? "" :
            distributedConfiguration.getBlacklistUrls());
        assertEquals(assertRoot + "#18",
            savedSetup.get("checkTimedOutServersFrequency"),
            distributedConfiguration.getCheckTimedOutServersFrequency());
        assertEquals(assertRoot + "#19",
            savedSetup.get("disabled"),
            distributedConfiguration.isDisabled());
        assertEquals(assertRoot + "#20",
            savedSetup.get("heartbeatFrequency"),
            distributedConfiguration.getHeartbeatFrequency());
        assertEquals(assertRoot + "#21",
            savedSetup.get("heartbeatMcastAddr"),
            distributedConfiguration.getHeartbeatMcastAddress());
        assertEquals(assertRoot + "#22",
            savedSetup.get("heartbeatPort"),
            distributedConfiguration.getHeartbeatPort());
        assertEquals(assertRoot + "#23",
            savedSetup.get("removedTimedOutServers"),
            distributedConfiguration.getRemovedTimedOutServers());
        assertEquals(assertRoot + "#24",
            savedSetup.get("servers"),
            distributedConfiguration.getServers() == null ? "" :
            distributedConfiguration.getServers());
        assertEquals(assertRoot + "#24",
            savedSetup.get("shareBundles"),
            distributedConfiguration.getShareBundles());
        assertEquals(assertRoot + "#25",
            savedSetup.get("skipOurselves"),
            distributedConfiguration.getSkipOurselves());
        assertEquals(assertRoot + "#26",
            savedSetup.get("statusTimeout"),
            distributedConfiguration.getStatusTimeout());
        assertEquals(assertRoot + "#27",
            savedSetup.get("ttl"),
            distributedConfiguration.getTtl());

        distributedConfiguration.getConnectionTimeout();
        distributedConfiguration.getReceiveTimeout();
        distributedConfiguration.getSendTimeout();

        distributedConfiguration.isDistSearchEnabled();
    }
}
