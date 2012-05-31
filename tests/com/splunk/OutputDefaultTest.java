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

public class OutputDefaultTest extends SplunkTestCase {
    final static String assertRoot = "Output Default assert: ";

    @Test public void testOutputDefault() throws Exception {
        Service service = connect();

        OutputDefault outputDefault = service.getOutputDefault();

        outputDefault.getAutoLB();
        outputDefault.blockOnQueueFull();
        outputDefault.getAutoLBFrequency();
        int onQueueFull = outputDefault.getDropEventsOnQueueFull();
        outputDefault.getForwardedIndex0Whitelist();
        outputDefault.getForwardedIndex1Blacklist();
        outputDefault.getForwardedIndex2Whitelist();
        int heartbeat = outputDefault.getHeartbeatFrequency();
        outputDefault.getMaxQueueSize();
        boolean forward = outputDefault.indexAndForward();
        outputDefault.isCompressed();
        outputDefault.isDisabled();
        outputDefault.isForwardedIndexFilterDisable();
        boolean cookedData = outputDefault.getSendCookedData();

        // set-ables

        outputDefault.setDropEventsOnQueueFull(0);
        outputDefault.setHeartbeatFrequency(heartbeat+1);
        outputDefault.setIndexAndForward(!forward);
        outputDefault.setSendCookedData(!cookedData);

        outputDefault.update();

        //check
        assertEquals(assertRoot + "#1", 0,
            outputDefault.getDropEventsOnQueueFull());
        assertEquals(assertRoot + "#2", heartbeat+1,
            outputDefault.getHeartbeatFrequency());
        assertEquals(assertRoot + "#3", !forward,
            outputDefault.indexAndForward());
        assertEquals(assertRoot + "#4", !cookedData,
            outputDefault.getSendCookedData());

        // restore
        outputDefault.setDropEventsOnQueueFull(onQueueFull);
        outputDefault.setHeartbeatFrequency(heartbeat);
        outputDefault.setIndexAndForward(forward);
        outputDefault.setSendCookedData(cookedData);

        outputDefault.update();

        //check
        assertEquals(assertRoot + "#5", onQueueFull,
            outputDefault.getDropEventsOnQueueFull());
        assertEquals(assertRoot + "#6", heartbeat,
            outputDefault.getHeartbeatFrequency());
        assertEquals(assertRoot + "#7", forward,
            outputDefault.indexAndForward());
        assertEquals(assertRoot + "#8", cookedData,
            outputDefault.getSendCookedData());
    }
}
