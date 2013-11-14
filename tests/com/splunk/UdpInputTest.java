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

    @Before
    public void setUp() throws Exception {
        super.setUp();

        indexName = createTemporaryName();
        index = service.getIndexes().create(indexName);

        udpPort = 10000;
        while (service.getInputs().containsKey(Integer.toString(udpPort))) {
            udpPort++;
        }

        udpInput = service.getInputs().create(
                String.valueOf(udpPort),
                InputKind.Udp,
                new Args("index", indexName));
    }

    @After
    public void tearDown() throws Exception {
        if (index != null && service.versionCompare("5.0") >= 0) {
            index.remove();
        }
        if (udpInput != null) {
            udpInput.remove();
        }
        
        super.tearDown();
    }

    @Test
    public void testSubmit() throws Exception {
        final int nEvents = index.getTotalEventCount();

        udpInput.submit(createTimestamp() + " Boris the mad baboon!\r\n");

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            {
                tries = 60;
            }

            @Override
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == nEvents + 1;
            }
        });
    }
    
}
