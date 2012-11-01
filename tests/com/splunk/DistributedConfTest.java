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

import org.junit.Before;
import org.junit.Test;

public class DistributedConfTest extends SDKTestCase {
    DistributedConfiguration conf;

    @Before @Override
    public void setUp() throws Exception {
        super.setUp();

        conf = service.getDistributedConfiguration();
    }

    @Test
    // We want to continue testing deprecated getters and setters
    @SuppressWarnings("deprecation")
    public void testSetAutoAddServers() {
        boolean originalAutoAddServers = conf.getAutoAddServers();
        boolean newAutoAddServers = !originalAutoAddServers;

        String originalBlacklistNames = conf.getBlacklistNames();
        String newBlacklistNames = "boris,hilda,edward";

        String originalBlacklistUrls = conf.getBlacklistUrls();
        String newBlacklistUrls = "*.google.com,*.yahoo.com";

        int originalTimeoutFrequency = conf.getCheckTimedOutServersFrequency();
        int newTimeoutFrequency = originalTimeoutFrequency + 50;

        int originalHeartbeatFrequency = conf.getHeartbeatFrequency();
        int newHeartbeatFrequency = originalHeartbeatFrequency + 50;

        String originalHeartbeatMcastAddr = conf.getHeartbeatMcastAddress();
        String newHeartbeatMcastAddr = "boris.the.baboon";

        int originalHeartbeatPort = conf.getHeartbeatPort();
        int newHeartbeatPort = originalHeartbeatPort + 20;

        boolean originalRemoveTimedOutServers = conf.getRemovedTimedOutServers();
        boolean newRemoveTimesOutServers = !originalRemoveTimedOutServers;

        String originalServers = conf.getServers();
        String newServers = "boris,natasha,hilda";

        boolean originalShareBundles = conf.getShareBundles();
        boolean newShareBundles = !originalShareBundles;

        boolean originalSkipOurselves = conf.getSkipOurselves();
        boolean newSkipOurselves = !originalSkipOurselves;

        int originalStatusTimeout = conf.getStatusTimeout();
        int newStatusTimeout = originalStatusTimeout + 50;

        int originalTtl = conf.getTtl();
        int newTtl = originalTtl + 50;

        try {

            conf.setAutoAddServers(newAutoAddServers);
            conf.setBlacklistNames(newBlacklistNames);
            conf.setBlacklistURLs(newBlacklistUrls);
            conf.setCheckTimedOutServersFrequency(newTimeoutFrequency);
            conf.setHeartbeatFrequency(newHeartbeatFrequency);
            conf.setHeartbeatMcastAddr(newHeartbeatMcastAddr);
            conf.setHeartbeatPort(newHeartbeatPort);
            conf.setRemoveTimedOutServers(newRemoveTimesOutServers);
            conf.setServers(newServers);
            conf.setShareBundles(newShareBundles);
            conf.setSkipOurselves(newSkipOurselves);
            conf.setStatusTimeout(newStatusTimeout);
            conf.setTTL(newTtl);

            conf.refresh();

            assertEquals(newAutoAddServers, conf.getAutoAddServers());
            assertEquals(newBlacklistNames, conf.getBlacklistNames());
            assertEquals(newBlacklistUrls, conf.getBlacklistUrls());
            assertEquals(newTimeoutFrequency, conf.getCheckTimedOutServersFrequency());
            assertEquals(newHeartbeatFrequency, conf.getHeartbeatFrequency());
            assertEquals(newHeartbeatMcastAddr, conf.getHeartbeatMcastAddress());
            assertEquals(newHeartbeatPort, conf.getHeartbeatPort());
            assertEquals(newRemoveTimesOutServers, conf.getRemovedTimedOutServers());
            assertEquals(newServers, conf.getServers());
            assertEquals(newShareBundles, conf.getShareBundles());
            assertEquals(newSkipOurselves, conf.getSkipOurselves());
            assertEquals(newStatusTimeout, conf.getStatusTimeout());
            assertEquals(newTtl, conf.getTtl());

        } finally {
            conf.setAutoAddServers(originalAutoAddServers);
            conf.setBlacklistNames(originalBlacklistNames);
            conf.setBlacklistURLs(originalBlacklistUrls);
            conf.setCheckTimedOutServersFrequency(originalTimeoutFrequency);
            conf.setHeartbeatFrequency(originalHeartbeatFrequency);
            conf.setHeartbeatMcastAddr(originalHeartbeatMcastAddr);
            conf.setHeartbeatPort(originalHeartbeatPort);
            conf.setRemoveTimedOutServers(originalRemoveTimedOutServers);
            conf.setServers(originalServers);
            conf.setShareBundles(originalShareBundles);
            conf.setSkipOurselves(originalSkipOurselves);
            conf.setStatusTimeout(originalStatusTimeout);
            conf.setTTL(originalTtl);
        }
    }

    @Test
    public void testDisableAndEnable() {
        assertEquals(conf.isDistSearchEnabled(), !conf.isDisabled());

        // Ensure that distributed search is enabled so we know
        // that disable works.
        if (conf.isDisabled()) {
            conf.enable();
            splunkRestart();
        }

        conf.disable();
        splunkRestart();
        conf.refresh();
        assertTrue(conf.isDisabled());

        conf.enable();
        splunkRestart();
        conf.refresh();
        assertTrue(conf.isDistSearchEnabled());
    }
}
