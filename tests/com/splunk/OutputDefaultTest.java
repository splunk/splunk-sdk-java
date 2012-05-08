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
    @Test public void testOutputDefault() throws Exception {
        Service service = connect();

        OutputDefault outputDefault = service.getOutputDefault();

        outputDefault.autoLb();
        outputDefault.blockOnQueueFull();
        outputDefault.getAutoLbFrequency();
        String defaultGroup = outputDefault.getDefaultGroup();
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

        outputDefault.setDefaultGroup("bad default group");
        outputDefault.setDropEventsOnQueueFull(0);
        outputDefault.setHeartbeatFrequency(heartbeat+1);
        outputDefault.setIndexAndForward(!forward);
        outputDefault.setSendCookedData(!cookedData);

        outputDefault.update();

        //check
        assertEquals(outputDefault.getDefaultGroup(), "bad default group");
        assertEquals(outputDefault.getDropEventsOnQueueFull(), 0);
        assertEquals(outputDefault.getHeartbeatFrequency(), heartbeat+1);
        assertEquals(outputDefault.indexAndForward(), !forward);
        assertEquals(outputDefault.getSendCookedData(), !cookedData);

        // restore
        if (defaultGroup == null)
            outputDefault.setDefaultGroup("");
        else
            outputDefault.setDefaultGroup(defaultGroup);
        outputDefault.setDropEventsOnQueueFull(onQueueFull);
        outputDefault.setHeartbeatFrequency(heartbeat);
        outputDefault.setIndexAndForward(forward);
        outputDefault.setSendCookedData(cookedData);

        outputDefault.update();

        //check
        assertEquals(outputDefault.getDefaultGroup(), defaultGroup);
        assertEquals(outputDefault.getDropEventsOnQueueFull(), onQueueFull);
        assertEquals(outputDefault.getHeartbeatFrequency(), heartbeat);
        assertEquals(outputDefault.indexAndForward(), forward);
        assertEquals(outputDefault.getSendCookedData(), cookedData);

    }
}
