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

import org.junit.Assert;
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

        String originalBlacklistNames = conf.getBlacklistNames();
        String newBlacklistNames = "boris,hilda,edward";

        String originalBlacklistUrls = conf.getBlacklistUrls();
        String newBlacklistUrls = "*.google.com,*.yahoo.com";

        int originalTimeoutFrequency = conf.getCheckTimedOutServersFrequency();
        int newTimeoutFrequency = originalTimeoutFrequency + 50;

        boolean originalRemoveTimedOutServers = conf.getRemovedTimedOutServers();
        boolean newRemoveTimesOutServers = !originalRemoveTimedOutServers;

        boolean originalShareBundles = conf.getShareBundles();
        boolean newShareBundles = !originalShareBundles;

        int originalStatusTimeout = conf.getStatusTimeout();
        int newStatusTimeout = originalStatusTimeout + 50;

        try {

            conf.setBlacklistNames(newBlacklistNames);
            conf.setBlacklistURLs(newBlacklistUrls);
            conf.setCheckTimedOutServersFrequency(newTimeoutFrequency);
            conf.setRemoveTimedOutServers(newRemoveTimesOutServers);
            conf.setShareBundles(newShareBundles);
            conf.setStatusTimeout(newStatusTimeout);

            conf.refresh();

            Assert.assertEquals(newBlacklistNames, conf.getBlacklistNames());
            Assert.assertEquals(newBlacklistUrls, conf.getBlacklistUrls());
            Assert.assertEquals(newTimeoutFrequency, conf.getCheckTimedOutServersFrequency());
            Assert.assertEquals(newRemoveTimesOutServers, conf.getRemovedTimedOutServers());
            Assert.assertEquals(newShareBundles, conf.getShareBundles());
            Assert.assertEquals(newStatusTimeout, conf.getStatusTimeout());

        } finally {
            conf.setBlacklistNames(originalBlacklistNames);
            conf.setBlacklistURLs(originalBlacklistUrls);
            conf.setCheckTimedOutServersFrequency(originalTimeoutFrequency);
            conf.setRemoveTimedOutServers(originalRemoveTimedOutServers);
            conf.setShareBundles(originalShareBundles);
            conf.setStatusTimeout(originalStatusTimeout);
        }
    }

    @Test
    public void testDisableAndEnable() {
        Assert.assertEquals(conf.isDistSearchEnabled(), !conf.isDisabled());

        // Ensure that distributed search is enabled so we know
        // that disable works.
        if (conf.isDisabled()) {
            conf.enable();
            splunkRestart();
        }

        conf.disable();
        splunkRestart();
        conf.refresh();
        Assert.assertTrue(conf.isDisabled());

        conf.enable();
        splunkRestart();
        conf.refresh();
        Assert.assertTrue(conf.isDistSearchEnabled());
    }
}
