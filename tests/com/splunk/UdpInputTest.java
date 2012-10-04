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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * [Insert documentation here]
 */
public class UdpInputTest extends SplunkTestCase {        Service service;
    int udpPort;
    UdpInput udpInput = null;
    public String indexName;
    public Index index = null;

    public int findNextUnusedUdpPort(int startingPort) {
        int port = startingPort;
        while (this.service.getInputs().containsKey(String.valueOf(port))) {
            port += 1;
        }
        return port;
    }

    @Before
    public void setUp() {
        super.setUp();
        this.service = connect();

        this.indexName = temporaryName();
        this.index = service.getIndexes().create(indexName);

        this.udpPort = findNextUnusedUdpPort(10000);
        Args args = new Args();
        args.add("index", indexName);
        this.udpInput = service.getInputs().create(String.valueOf(this.udpPort), InputKind.Udp, args);
    }

    @After
    public void tearDown() {
        if (this.index != null && this.service.versionCompare("5.0") >= 0) {
            this.index.remove();
        }
        if (this.udpInput != null) {
            this.udpInput.remove();
        }
    }

    @Test
    public void testSubmit() throws Exception {
        final int nEvents = this.index.getTotalEventCount();
        final Index index = this.index;

        this.udpInput.submit(createTimestamp() + " Boris the mad baboon!\r\n");

        SplunkTestCase.assertEventuallyTrue(new EventuallyTrueBehavior() {
            { tries = 50; }
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == nEvents + 1;
            }
        });
    }

}
