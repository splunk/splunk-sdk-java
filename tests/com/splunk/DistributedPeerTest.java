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

import org.junit.*;

@Ignore
/**
 * To execute these test we need a separate splunk instance. As post splunk6.4 it is no longer allow to connect to self
 * as a peer because of name conflicts.
 */
public class DistributedPeerTest extends SDKTestCase {
    EntityCollection<DistributedPeer> peers;
    String temporaryUsername;
    String temporaryPassword;
    User temporaryUser;

    @Before @Override
    public void setUp() throws Exception {
        super.setUp();


        temporaryUsername = createTemporaryName();
        temporaryPassword = createTemporaryName();
        temporaryUser = service.getUsers().create(temporaryUsername, temporaryPassword, "admin");

        // To create search peers sanely, we need to have
        // distributed search enabled.
        if (service.getDistributedConfiguration().isDisabled()) {
            service.getDistributedConfiguration().enable();
            splunkRestart();
        }

        peers = service.getDistributedPeers();

        // If the splunkd instance is already connected to
        // itself as a peer, delete that reference.
        if (peers.containsKey(nameOfPeer())) {
            peers.remove(nameOfPeer());
        }
    }

    private DistributedPeer connectToSelfAsPeer() {
        Args args = new Args();
        args.put("remoteUsername", temporaryUsername);
        args.put("remotePassword", temporaryPassword);
        DistributedPeer peer = peers.create(nameOfPeer(), args);
        return peer;
    }

    private String nameOfPeer() {
        return command.opts.get("host") + ":" +
                String.valueOf(command.opts.get("port"));
    }

    @After @Override
    public void tearDown() throws Exception {
        temporaryUser.remove();

        String name = nameOfPeer();
        if (peers.containsKey(name)) {
            peers.remove(name);
        }
        Assert.assertFalse(peers.containsKey(name));

    }

    @Test
    public void testCreatePeer() {
        DistributedPeer peer = connectToSelfAsPeer();
        Assert.assertTrue(peers.containsKey(peer.getName()));
    }

    @Test
    public void testDeletePeer() {
        DistributedPeer peer = connectToSelfAsPeer();
        String name = peer.getName();
        Assert.assertTrue(peers.containsKey(name));

        peer.remove();

        peers.refresh();
        Assert.assertFalse(peers.containsKey(name));
    }

    @Test
    public void testPeerHasSaneValues() {
        DistributedPeer peer = connectToSelfAsPeer();

        // Since our only search peer is the splunkd instance
        // itself, we can correlate most of the fields.
        Assert.assertEquals(nameOfPeer(), peer.getTitle());
        Assert.assertEquals(service.getInfo().getBuild(), peer.getBuild());
        Assert.assertEquals(service.getInfo().getGuid(), peer.getGuid());
        Assert.assertEquals(service.getInfo().getServerName(), peer.getPeerName());
        Assert.assertEquals("configured", peer.getPeerType());
        Assert.assertEquals("Initial", peer.getReplicationStatus());
        Assert.assertEquals("Duplicate Servername", peer.getStatus());
        Assert.assertEquals(service.getInfo().getVersion(), peer.getVersion());
        Assert.assertFalse(peer.isDisabled());
        Assert.assertTrue(peer.isHttps());

        // Except for these two, which I don't know how to find
        // elsewhere in splunkd.
        peer.getBundleVersions();
        peer.getLicenseSignature();
    }

    @Test
    public void testDisablePeer() {
        final DistributedPeer peer = connectToSelfAsPeer();

        // Make sure the peer is enabled first, so we know that disable
        // is actually doing something.
        if (peer.isDisabled()) {
            peer.enable();
            assertEventuallyTrue(new EventuallyTrueBehavior() {
                @Override
                public boolean predicate() {
                    peer.refresh();
                    return !peer.isDisabled();
                }
            });
        }

        peer.disable();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override public boolean predicate() {
                peer.refresh();
                return peer.isDisabled();
            }
        });
    }

    @Test
    public void testEnablePeer() {
        final DistributedPeer peer = connectToSelfAsPeer();

        // First make sure the peer is disabled so we can
        // tell if enable is actually doing anything.
        if (!peer.isDisabled()) {
            peer.disable();
            assertEventuallyTrue(new EventuallyTrueBehavior() {
                @Override
                public boolean predicate() {
                    peer.refresh();
                    return peer.isDisabled();
                }
            });
        }

        // Enable the peer
        peer.enable();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                peer.refresh();
                return !peer.isDisabled();
            }
        });
    }

    @Test
    public void testSetFieldsOnPeer() {
        // The only fields that can be set on a search peer
        // are remoteUsername and remotePassword. Unfortunately
        // they cannot be fetched afterwards, so it's impossible
        // to test them.
        final DistributedPeer peer = connectToSelfAsPeer();

        String newPassword = createTemporaryName();
        temporaryUser.setPassword(newPassword);
        temporaryUser.update();

        peer.setRemoteUsername(temporaryUsername);
        peer.setRemotePassword(newPassword);
        peer.update();

        // The only assertion we can make is that Splunk
        // did not return an error.
    }
}


