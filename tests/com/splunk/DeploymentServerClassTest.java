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

public class DeploymentServerClassTest extends TestCase {
    Command command;

    public DeploymentServerClassTest() {}

    Service connect() {
        return Service.connect(command.opts);
    }

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
    }

    @Test public void testDeploymentServerClass() throws Exception {
        Service service = connect();

        EntityCollection<DeploymentServerClass> dscs =
            service.getDeploymentServerClasses();
        DeploymentServerClass dsc;

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

        if (!dscs.containsKey("sdk-tests")) {
            dsc = dscs.create("sdk-tests", args);
        } else {
            dsc = dscs.get("sdk-tests");
            dsc.update(args);
        }

        assertEquals(dsc.getBlackList0(), args.get("blacklist.0"));
        assertEquals(dsc.getBlackList1(), args.get("blacklist.1"));
        assertEquals(dsc.getBlackList2(), args.get("blacklist.2"));
        assertEquals(dsc.getBlackList3(), args.get("blacklist.3"));
        assertEquals(dsc.getBlackList4(), args.get("blacklist.4"));
        assertEquals(dsc.getBlackList5(), args.get("blacklist.5"));
        assertEquals(dsc.getBlackList6(), args.get("blacklist.6"));
        assertEquals(dsc.getBlackList7(), args.get("blacklist.7"));
        assertEquals(dsc.getBlackList8(), args.get("blacklist.8"));
        assertEquals(dsc.getBlackList9(), args.get("blacklist.9"));
        assertEquals(
            dsc.getContinueMatching(), args.get("continueMatching"));
        assertEquals(dsc.getFilterType(), args.get("filterType"));
        assertEquals(dsc.getWhiteList0(), args.get("whitelist.0"));
        assertEquals(dsc.getWhiteList1(), args.get("whitelist.1"));
        assertEquals(dsc.getWhiteList2(), args.get("whitelist.2"));
        assertEquals(dsc.getWhiteList3(), args.get("whitelist.3"));
        assertEquals(dsc.getWhiteList4(), args.get("whitelist.4"));
        assertEquals(dsc.getWhiteList5(), args.get("whitelist.5"));
        assertEquals(dsc.getWhiteList6(), args.get("whitelist.6"));
        assertEquals(dsc.getWhiteList7(), args.get("whitelist.7"));
        assertEquals(dsc.getWhiteList8(), args.get("whitelist.8"));
        assertEquals(dsc.getWhiteList9(), args.get("whitelist.9"));

        for (DeploymentServerClass entity: dscs.values()) {
            entity.get(); // force a read
            assertTrue(entity.getRepositoryLocation().length() > 0);
        }

        // N.B. No REST endpoint to delete a deployment server class.
    }
}
