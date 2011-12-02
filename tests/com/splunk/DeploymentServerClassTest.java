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
    @Test public void testDeploymentServerClass() throws Exception {
        Service service = connect();

        EntityCollection<DeploymentServerClass> deploymentServerClasses =
                service.getDeploymentServerClasses();
        DeploymentServerClass deploymentServerClass;

        Args args = new Args();
        args.put("blacklist.0", "bad0.splunk.com");
        args.put("blacklist.1", "bad1.splunk.com");
        args.put("blacklist.2", "bad2.splunk.com");
        args.put("blacklist.3", "bad3.splunk.com");
        args.put("blacklist.4", "bad4.splunk.com");
        args.put("blacklist.5", "bad5.splunk.com");
        args.put("blacklist.6", "bad6.splunk.com");
        args.put("blacklist.7", "bad7.splunk.com");
        args.put("blacklist.8", "bad8.splunk.com");
        args.put("blacklist.9", "bad9.splunk.com");
        args.put("continueMatching", false);
        args.put("filterType","whitelist");
        args.put("whitelist.0", "good0.splunk.com");
        args.put("whitelist.1", "good1.splunk.com");
        args.put("whitelist.2", "good2.splunk.com");
        args.put("whitelist.3", "good3.splunk.com");
        args.put("whitelist.4", "good4.splunk.com");
        args.put("whitelist.5", "good5.splunk.com");
        args.put("whitelist.6", "good6.splunk.com");
        args.put("whitelist.7", "good7.splunk.com");
        args.put("whitelist.8", "good8.splunk.com");
        args.put("whitelist.9", "good9.splunk.com");

        if (!deploymentServerClasses.containsKey("sdk-tests")) {
            deploymentServerClass =
                    deploymentServerClasses.create("sdk-tests", args);
        } else {
            deploymentServerClass = deploymentServerClasses.get("sdk-tests");
            deploymentServerClass.update(args);
        }

        assertEquals(deploymentServerClass.getBlackList0(),
                args.get("blacklist.0"));
        assertEquals(deploymentServerClass.getBlackList1(),
                args.get("blacklist.1"));
        assertEquals(deploymentServerClass.getBlackList2(),
                args.get("blacklist.2"));
        assertEquals(deploymentServerClass.getBlackList3(),
                args.get("blacklist.3"));
        assertEquals(deploymentServerClass.getBlackList4(),
                args.get("blacklist.4"));
        assertEquals(deploymentServerClass.getBlackList5(),
                args.get("blacklist.5"));
        assertEquals(deploymentServerClass.getBlackList6(),
                args.get("blacklist.6"));
        assertEquals(deploymentServerClass.getBlackList7(),
                args.get("blacklist.7"));
        assertEquals(deploymentServerClass.getBlackList8(),
                args.get("blacklist.8"));
        assertEquals(deploymentServerClass.getBlackList9(),
                args.get("blacklist.9"));
        assertEquals(deploymentServerClass.getContinueMatching(),
                args.get("continueMatching"));
        assertEquals(deploymentServerClass.getFilterType(),
                args.get("filterType"));
        assertEquals(deploymentServerClass.getWhiteList0(),
                args.get("whitelist.0"));
        assertEquals(deploymentServerClass.getWhiteList1(),
                args.get("whitelist.1"));
        assertEquals(deploymentServerClass.getWhiteList2(),
                args.get("whitelist.2"));
        assertEquals(deploymentServerClass.getWhiteList3(),
                args.get("whitelist.3"));
        assertEquals(deploymentServerClass.getWhiteList4(),
                args.get("whitelist.4"));
        assertEquals(deploymentServerClass.getWhiteList5(),
                args.get("whitelist.5"));
        assertEquals(deploymentServerClass.getWhiteList6(),
                args.get("whitelist.6"));
        assertEquals(deploymentServerClass.getWhiteList7(),
                args.get("whitelist.7"));
        assertEquals(deploymentServerClass.getWhiteList8(),
                args.get("whitelist.8"));
        assertEquals(deploymentServerClass.getWhiteList9(),
                args.get("whitelist.9"));

        for (DeploymentServerClass deploymentServerClass1:
                deploymentServerClasses.values()) {
            assertTrue(
                deploymentServerClass1.getRepositoryLocation().length() > 0);
            deploymentServerClass1.getBlackList();
            deploymentServerClass1.getBlackListDot();
            deploymentServerClass1.getEndpoint();
            deploymentServerClass1.getTargetRepositoryLocation();
            deploymentServerClass1.getTmpFolder();
            deploymentServerClass1.getWhiteList();
            deploymentServerClass1.getWhiteListDot();
        }

        // N.B. No REST endpoint to delete a deployment server class.
    }
}
