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

package com.splunk.sdk.tests.com.splunk;

import com.splunk.*;
import com.splunk.sdk.Command;
import com.splunk.Service;

import junit.framework.Assert;
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
        Assert.assertEquals(
            dc.getAutoAddServers(), updateArgs.get("autoAddServers"));
        Assert.assertEquals(
            dc.getBlacklistNames(), updateArgs.get("blacklistNames"));
        Assert.assertEquals(
            dc.getBlacklistUrls(), updateArgs.get("blacklistURLs"));
        Assert.assertEquals(
            dc.getCheckTimedOutServersFrequency(),
            updateArgs.get("checkTimedOutServersFrequency"));
        Assert.assertEquals(dc.isDisabled(), updateArgs.get("disabled"));
        Assert.assertEquals(
            dc.getHeartbeatFrequency(), updateArgs.get("heartbeatFrequency"));
        Assert.assertEquals(
            dc.getHeartbeatMcastAddress(),
            updateArgs.get("heartbeatMcastAddr"));
        Assert.assertEquals(
            dc.getHeartbeatPort(), updateArgs.get("heartbeatPort"));
        Assert.assertEquals(
            dc.getRemovedTimedOutServers(),
            updateArgs.get("removedTimedOutServers"));
        Assert.assertEquals(
            dc.getServerTimeout(),updateArgs.get("serverTimeout"));
        Assert.assertEquals(dc.getServers(), updateArgs.get("servers"));
        Assert.assertEquals(
            dc.getShareBundles(), updateArgs.get("shareBundles"));
        Assert.assertEquals(
            dc.getSkipOurselves(), updateArgs.get("skipOurselves"));
        Assert.assertEquals(
            dc.getStatusTimeout(), updateArgs.get("statusTimeout"));
        Assert.assertEquals(dc.getTtl(), updateArgs.get("ttl"));

        // handle nulls specially
        if (savedSetup.get("blacklistNames") == null)
            savedSetup.put("blacklistNames", "");
        if (savedSetup.get("blacklistURLs") == null)
            savedSetup.put("blacklistURLs", "");

        dc.update(savedSetup);
        Assert.assertEquals(
            dc.getAutoAddServers(), savedSetup.get("autoAddServers"));
        Assert.assertEquals(
            dc.getBlacklistNames() == null ? "" : dc.getBlacklistNames(),
            savedSetup.get("blacklistNames") );
        Assert.assertEquals(
            dc.getBlacklistUrls() == null ? "" : dc.getBlacklistUrls(),
            savedSetup.get("blacklistURLs"));
        Assert.assertEquals(
            dc.getCheckTimedOutServersFrequency(),
            savedSetup.get("checkTimedOutServersFrequency"));
        Assert.assertEquals(dc.isDisabled(), savedSetup.get("disabled"));
        Assert.assertEquals(
            dc.getHeartbeatFrequency(), savedSetup.get("heartbeatFrequency"));
        Assert.assertEquals(
            dc.getHeartbeatMcastAddress(),
            savedSetup.get("heartbeatMcastAddr"));
        Assert.assertEquals(
            dc.getHeartbeatPort(), savedSetup.get("heartbeatPort"));
        Assert.assertEquals(
            dc.getRemovedTimedOutServers(),
            savedSetup.get("removedTimedOutServers"));
        Assert.assertEquals(
            dc.getServerTimeout(),savedSetup.get("serverTimeout"));
        Assert.assertEquals(dc.getServers(), savedSetup.get("servers"));
        Assert.assertEquals(
            dc.getShareBundles(), savedSetup.get("shareBundles"));
        Assert.assertEquals(
            dc.getSkipOurselves(), savedSetup.get("skipOurselves"));
        Assert.assertEquals(
            dc.getStatusTimeout(), savedSetup.get("statusTimeout"));
        Assert.assertEquals(dc.getTtl(), savedSetup.get("ttl"));
    }
}
