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
import com.splunk.sdk.Program;
import com.splunk.Service;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.*;

public class DistributedPeerTest extends TestCase {
    Program program = new Program();

    public DistributedPeerTest() {}

    Service connect() {
        return new Service(
            program.host, program.port, program.scheme)
                .login(program.username, program.password);
    }

    @Before public void setUp() {
        this.program.init(); // Pick up .splunkrc settings
    }

    @Test public void testDistributedPeer() throws Exception {
        Service service = connect();

        EntityCollection<DistributedPeer> dps = service.getDistributedPeers();

        if (dps.values().size() == 0) {
            System.out.println("WARNING: distributedPeer not configured");
            return;
        }

        for (DistributedPeer dp: dps.values()) {
            dp.getTitle();
            dp.getBuild();
            dp.getBundleVersions();
            dp.getGuid();
            dp.getLicenseSignature();
            dp.getPeerName();
            dp.getPeerType();
            dp.getRemotePassword();
            dp.getRemoteUsername();
            dp.getReplicationStatus();
            dp.getStatus();
            dp.getVersion();
            dp.isDisabled();
            dp.isHttps();
        }
    }
}
