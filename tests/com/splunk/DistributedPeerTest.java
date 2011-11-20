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

public class DistributedPeerTest extends TestCase {
    Command command;

    public DistributedPeerTest() {}

    Service connect() {
        return Service.connect(command.opts);
    }

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
    }

    @Test public void testDistributedPeer() throws Exception {
        Service service = connect();

        EntityCollection<DistributedPeer> dps = service.getDistributedPeers();

        String name = command.host + ":" + command.port;

        if (dps.containsKey(name)) {
            dps.remove(name);
        }

        assertFalse(dps.containsKey(name));
        Args args = new Args();
        args.put("remotePassword", command.password);
        args.put("remoteUsername", command.username);
        DistributedPeer newdp = dps.create(name, args);
        assertTrue(dps.containsKey(name));

        // created as enabled
        newdp.disable();
        assertTrue(newdp.isDisabled());
        newdp.enable();
        assertFalse(newdp.isDisabled());
        // N.B. these are write only... so can't check if they take
        args.put("remotePassword", command.password + "xx");
        args.put("remoteUsername", command.username + "xx");
        newdp.update(args);

        for (DistributedPeer dp: dps.values()) {
            dp.getTitle();
            dp.getBuild();
            dp.getBundleVersions();
            dp.getGuid();
            dp.getLicenseSignature();
            dp.getPeerName();
            dp.getPeerType();
            dp.getReplicationStatus();
            dp.getStatus();
            dp.getVersion();
            dp.isDisabled();
            dp.isHttps();
        }

        newdp.remove();
        dps.refresh();
        assertFalse(dps.containsKey(name));
    }
}
