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

public class DeploymentServerClassTest extends SplunkTestCase {
    final static String assertRoot = "Deployment Server Class assert: ";

    @Test public void testDeploymentServerClass() throws Exception {
        Service service = connect();

        EntityCollection<DeploymentServerClass> deploymentServerClasses =
                service.getDeploymentServerClasses();
        DeploymentServerClass deploymentServerClass;

        Args args = new Args();
        for (int i=0; i< 10; i++)
            args.put(String.format("blacklist.%d", i),
                     String.format("bad%d.splunk.com", i));
        args.put("continueMatching", false);
        args.put("filterType","whitelist");
        for (int i=0; i< 10; i++)
            args.put(String.format("whitelist.%d", i),
                     String.format("good%d.splunk.com", i));

        // create or get
        if (!deploymentServerClasses.containsKey("sdk-tests")) {
            deploymentServerClass =
                    deploymentServerClasses.create("sdk-tests", args);
        } else {
            deploymentServerClass = deploymentServerClasses.get("sdk-tests");
            deploymentServerClass.update(args);
        }

        // check for sanity
        for (int i=0; i< 10; i++)
            assertEquals(assertRoot + "#1",
                String.format("bad%d.splunk.com", i),
                deploymentServerClass.getBlackListByIndex(i));
        assertEquals(assertRoot + "#2",
            args.get("continueMatching"),
            deploymentServerClass.getContinueMatching());
        assertEquals(assertRoot + "#3",
            args.get("filterType"), deploymentServerClass.getFilterType());
        for (int i=0; i< 10; i++)
            assertEquals(assertRoot + "#4",
                String.format("good%d.splunk.com", i),
                deploymentServerClass.getWhiteListByIndex(i));
        String filter = deploymentServerClass.getFilterType();

        // modify
        // N.B. paths are OS specific, and not tested here.
        // and updates to black and whitelist are all or nothing.
        for (int i=0; i< 10; i++)
            deploymentServerClass.setBlackListByIndex(i,
                    String.format("maybe%d.splunk.com", i));
        deploymentServerClass.setContinueMatching(true);
        deploymentServerClass.setFilterType("blacklist");
        for (int i=0; i< 10; i++)
            deploymentServerClass.setWhiteListByIndex(i,
                    String.format("maybe%d.splunk.com", i));
        deploymentServerClass.update();

        // check update
        for (int i=0; i< 10; i++)
            assertEquals(assertRoot + "#5",
                String.format("maybe%d.splunk.com", i),
                deploymentServerClass.getBlackListByIndex(i));
        assertTrue(assertRoot + "#6",
            deploymentServerClass.getContinueMatching());
        assertEquals(assertRoot + "#7",
            "blacklist", deploymentServerClass.getFilterType());
        for (int i=0; i< 10; i++)
            assertEquals(assertRoot + "#8",
                String.format("maybe%d.splunk.com", i),
                deploymentServerClass.getWhiteListByIndex(i));

        // cleanup & restore sane values
        for (int i=0; i< 10; i++)
            deploymentServerClass.setBlackListByIndex(i,
                    String.format("bad%d.splunk.com", i));
        deploymentServerClass.setContinueMatching(false);
        deploymentServerClass.setFilterType(filter);
        for (int i=0; i< 10; i++)
            deploymentServerClass.setWhiteListByIndex(i,
                    String.format("good%d.splunk.com", i));

        for (DeploymentServerClass deploymentServerClass1:
                deploymentServerClasses.values()) {
            assertTrue(assertRoot + "#9",
                deploymentServerClass1.getRepositoryLocation().length() > 0);
            deploymentServerClass1.getBlackList();
            deploymentServerClass1.getEndpoint();
            deploymentServerClass1.getTargetRepositoryLocation();
            deploymentServerClass1.getTmpFolder();
            deploymentServerClass1.getWhiteList();
        }

        // N.B. No REST endpoint to delete a deployment server class.
    }
}
