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

public class UdpInputTest extends SDKTestCase {
    protected int udpPort;
    protected UdpInput udpInput = null;
    protected String indexName;
    protected Index index = null;

    public int findNextUnusedUdpPort(int startingPort) {
        int port = startingPort;
        InputCollection inputs = service.getInputs();
        while (inputs.containsKey(String.valueOf(port))) {
            port += 1;
        }
        return port;
    }

    @Before public void setUp() throws Exception {
        super.setUp();

        indexName = createTemporaryName();
        index = service.getIndexes().create(indexName);

        udpPort = findNextUnusedUdpPort(10000);
        Args args = new Args();
        args.add("index", indexName);
        udpInput = service.getInputs().create(String.valueOf(udpPort), InputKind.Udp, args);
    }

    @After public void tearDown() throws Exception {
        super.tearDown();
        if (index != null && service.versionCompare("5.0") >= 0) {
            index.remove();
        }
        if (udpInput != null) {
            udpInput.remove();
        }
    }

    @Test public void testSubmit() {
        final int nEvents = index.getTotalEventCount();

        try {
            udpInput.submit(createTimestamp() + " Boris the mad baboon!\r\n");
        } catch (Exception e) {
            fail(e.toString());
        }

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            {
                tries = 50;
            }

            @Override
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == nEvents + 1;
            }
        });
    }

}
