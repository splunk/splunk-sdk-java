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

import com.splunk.sdk.Command;

import junit.framework.TestCase;
import org.junit.*;

public class DistributedConfigurationTest extends TestCase {
    Command command;

    public DistributedConfigurationTest() {}

    Service connect() {
        return Service.connect(command.opts);
    }

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
    }

    @Test public void testDistributedConfiguration() throws Exception {
        Service service = connect();

        DistributedConfiguration dc = service.getDistributedConfiguration();

        Args savedSetup = new Args();
        savedSetup.put("autoAddServers", dc.getAutoAddServers());
        savedSetup.put("blacklistNames", dc.getBlacklistNames());
        savedSetup.put("blacklistURLs", dc.getBlacklistUrls());
        savedSetup.put("checkTimedOutServersFrequency",
                        dc.getCheckTimedOutServersFrequency());
        savedSetup.put("disabled", dc.isDisabled());
        savedSetup.put("heartbeatFrequency", dc.getHeartbeatFrequency());
        savedSetup.put("heartbeatMcastAddr", dc.getHeartbeatMcastAddress());
        savedSetup.put("heartbeatPort", dc.getHeartbeatPort());
        savedSetup.put("removedTimedOutServers", dc.getRemovedTimedOutServers());
        savedSetup.put("serverTimeout", dc.getServerTimeout());
        savedSetup.put("servers", dc.getServers());
        savedSetup.put("shareBundles", dc.getShareBundles());
        savedSetup.put("skipOurselves", dc.getSkipOurselves());
        savedSetup.put("statusTimeout", dc.getStatusTimeout());
        savedSetup.put("ttl", dc.getTtl());

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

        dc.update(updateArgs);
        assertEquals(
            dc.getAutoAddServers(), updateArgs.get("autoAddServers"));
        assertEquals(
            dc.getBlacklistNames(), updateArgs.get("blacklistNames"));
        assertEquals(
            dc.getBlacklistUrls(), updateArgs.get("blacklistURLs"));
        assertEquals(
            dc.getCheckTimedOutServersFrequency(),
            updateArgs.get("checkTimedOutServersFrequency"));
        assertEquals(dc.isDisabled(), updateArgs.get("disabled"));
        assertEquals(
            dc.getHeartbeatFrequency(), updateArgs.get("heartbeatFrequency"));
        assertEquals(
            dc.getHeartbeatMcastAddress(),
            updateArgs.get("heartbeatMcastAddr"));
        assertEquals(
            dc.getHeartbeatPort(), updateArgs.get("heartbeatPort"));
        assertEquals(
            dc.getRemovedTimedOutServers(),
            updateArgs.get("removedTimedOutServers"));
        assertEquals(
            dc.getServerTimeout(),updateArgs.get("serverTimeout"));
        assertEquals(dc.getServers(), updateArgs.get("servers"));
        assertEquals(
            dc.getShareBundles(), updateArgs.get("shareBundles"));
        assertEquals(
            dc.getSkipOurselves(), updateArgs.get("skipOurselves"));
        assertEquals(
            dc.getStatusTimeout(), updateArgs.get("statusTimeout"));
        assertEquals(dc.getTtl(), updateArgs.get("ttl"));

        // handle nulls specially
        if (savedSetup.get("blacklistNames") == null)
            savedSetup.put("blacklistNames", "");
        if (savedSetup.get("blacklistURLs") == null)
            savedSetup.put("blacklistURLs", "");

        dc.update(savedSetup);
        assertEquals(
            dc.getAutoAddServers(), savedSetup.get("autoAddServers"));
        assertEquals(
            dc.getBlacklistNames() == null ? "" : dc.getBlacklistNames(),
            savedSetup.get("blacklistNames") );
        assertEquals(
            dc.getBlacklistUrls() == null ? "" : dc.getBlacklistUrls(),
            savedSetup.get("blacklistURLs"));
        assertEquals(
            dc.getCheckTimedOutServersFrequency(),
            savedSetup.get("checkTimedOutServersFrequency"));
        assertEquals(dc.isDisabled(), savedSetup.get("disabled"));
        assertEquals(
            dc.getHeartbeatFrequency(), savedSetup.get("heartbeatFrequency"));
        assertEquals(
            dc.getHeartbeatMcastAddress(),
            savedSetup.get("heartbeatMcastAddr"));
        assertEquals(
            dc.getHeartbeatPort(), savedSetup.get("heartbeatPort"));
        assertEquals(
            dc.getRemovedTimedOutServers(),
            savedSetup.get("removedTimedOutServers"));
        assertEquals(
            dc.getServerTimeout(), savedSetup.get("serverTimeout"));
        assertEquals(dc.getServers(), savedSetup.get("servers"));
        assertEquals(
            dc.getShareBundles(), savedSetup.get("shareBundles"));
        assertEquals(
            dc.getSkipOurselves(), savedSetup.get("skipOurselves"));
        assertEquals(
            dc.getStatusTimeout(), savedSetup.get("statusTimeout"));
        assertEquals(dc.getTtl(), savedSetup.get("ttl"));
    }
}
