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

public class DistributedPeerTest extends SplunkTestCase {
    @Test public void testDistributedPeer() throws Exception {
        Service service = connect();

        EntityCollection<DistributedPeer> distributedPeers =
                service.getDistributedPeers();

        String name = command.host + ":" + command.port;

        if (distributedPeers.containsKey(name)) {
            distributedPeers.remove(name);
        }

        assertFalse(distributedPeers.containsKey(name));
        Args args = new Args();
        args.put("remotePassword", command.password);
        args.put("remoteUsername", command.username);
        DistributedPeer newDistributedPeer =
                distributedPeers.create(name, args);
        assertTrue(distributedPeers.containsKey(name));

        // created as enabled
        newDistributedPeer.disable();
        assertTrue(newDistributedPeer.isDisabled());
        newDistributedPeer.enable();
        assertFalse(newDistributedPeer.isDisabled());
        // N.B. these are write only... so can't check if they take
        // setter method update.
        newDistributedPeer.setRemotePassword(command.password + "xx");
        newDistributedPeer.setRemoteUsername(command.username + "xx");
        newDistributedPeer.update();

        for (DistributedPeer distributedPeer: distributedPeers.values()) {
            distributedPeer.getTitle();
            distributedPeer.getBuild();
            distributedPeer.getBundleVersions();
            distributedPeer.getGuid();
            distributedPeer.getLicenseSignature();
            distributedPeer.getPeerName();
            distributedPeer.getPeerType();
            distributedPeer.getReplicationStatus();
            distributedPeer.getStatus();
            distributedPeer.getVersion();
            distributedPeer.isDisabled();
            distributedPeer.isHttps();
        }

        newDistributedPeer.remove();
        distributedPeers.refresh();
        assertFalse(distributedPeers.containsKey(name));
    }
}
