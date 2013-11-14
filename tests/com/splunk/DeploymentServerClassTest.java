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

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public class DeploymentServerClassTest extends SDKTestCase {
    private EntityCollection<DeploymentServerClass> classes;
    private String serverClassName;
    private DeploymentServerClass serverClass;

    @Before @Override
    public void setUp() throws Exception {
        super.setUp();

        classes = service.getDeploymentServerClasses();

        // Create a temporary deployment serverclass.
        serverClassName = createTemporaryName();
        serverClass = classes.create(serverClassName);
        // NOTE: We cannot delete server classes via the
        // REST API, so randomly named ones will just accumulate.
    }

    @Test
    public void testCreate() {
        if (service.versionIsAtLeast("6.0")) {
            // WORKAROUND (DVPL-2993): The deployment server has changed in Splunk 6, and is not yet supported.
            return;
        }

        assertTrue(classes.containsKey(serverClassName));
    }

    @Test
    public void testDisable() {
        if (service.versionIsAtLeast("6.0")) {
            // WORKAROUND (DVPL-2993): The deployment server has changed in Splunk 6, and is not yet supported.
            return;
        }

        if (serverClass.isDisabled()) {
            serverClass.enable();
            serverClass.refresh();
            assertFalse(serverClass.isDisabled());
        }

        serverClass.disable();
        serverClass.refresh();
        assertTrue(serverClass.isDisabled());
    }

    @Test
    public void testEnable() {
        if (service.versionIsAtLeast("6.0")) {
            // WORKAROUND (DVPL-2993): The deployment server has changed in Splunk 6, and is not yet supported.
            return;
        }

        if (!serverClass.isDisabled()) {
            serverClass.disable();
            serverClass.refresh();
            assertTrue(serverClass.isDisabled());
        }

        serverClass.enable();
        serverClass.refresh();
        assertFalse(serverClass.isDisabled());
    }

    @Test
    public void testBlacklist() {
        if (service.versionIsAtLeast("6.0")) {
            // WORKAROUND (DVPL-2993): The deployment server has changed in Splunk 6, and is not yet supported.
            return;
        }

        String[] blacklist = {"bad1.splunk.com", "bad2.splunk.com"};

        for (int i = 0; i < blacklist.length; i++) {
            serverClass.setBlacklistByIndex(i, blacklist[i]);
        }

        serverClass.update();
        serverClass.refresh();

        for (int i = 0; i < blacklist.length; i++) {
            assertEquals(blacklist[i], serverClass.getBlacklistByIndex(i));
        }

        assertEquals(Util.join(",", blacklist), serverClass.getBlacklist());
    }

    @Test
    public void testWhitelist() {
        if (service.versionIsAtLeast("6.0")) {
            // WORKAROUND (DVPL-2993): The deployment server has changed in Splunk 6, and is not yet supported.
            return;
        }

        String[] whitelist = {"bad1.splunk.com", "bad2.splunk.com"};

        for (int i = 0; i < whitelist.length; i++) {
            serverClass.setWhitelistByIndex(i, whitelist[i]);
        }

        serverClass.update();
        serverClass.refresh();

        for (int i = 0; i < whitelist.length; i++) {
            assertEquals(whitelist[i], serverClass.getWhitelistByIndex(i));
        }

        assertEquals(Util.join(",", whitelist), serverClass.getWhitelist());
    }

    @Test
    public void testContinueMatching() {
        if (service.versionIsAtLeast("6.0")) {
            // WORKAROUND (DVPL-2993): The deployment server has changed in Splunk 6, and is not yet supported.
            return;
        }

        assertFalse(serverClass.getContinueMatching());
        serverClass.setContinueMatching(true);
        serverClass.update();
        serverClass.refresh();
        assertTrue(serverClass.getContinueMatching());
    }

    @Test
    public void testEndpoint() {
        if (service.versionIsAtLeast("6.0")) {
            // WORKAROUND (DVPL-2993): The deployment server has changed in Splunk 6, and is not yet supported.
            return;
        }

        serverClass.setEndPoint("boris the mad baboon");
        serverClass.update();
        serverClass.refresh();
        assertEquals("boris the mad baboon", serverClass.getEndpoint());
    }

    @Test
    public void testFilterType() {
        if (service.versionIsAtLeast("6.0")) {
            // WORKAROUND (DVPL-2993): The deployment server has changed in Splunk 6, and is not yet supported.
            return;
        }

        serverClass.setFilterType("whitelist");
        serverClass.update();
        serverClass.refresh();
        assertEquals("whitelist", serverClass.getFilterType());

        serverClass.setFilterType("blacklist");
        serverClass.refresh();
        assertEquals("blacklist", serverClass.getFilterType());
    }
}

