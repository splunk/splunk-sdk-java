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

import org.junit.Assert;
import org.junit.Test;

public class OutputDefaultTest extends SDKTestCase {
    @Test
    public void testOutputDefault() throws Exception {
        OutputDefault outputDefault = service.getOutputDefault();

        // Getters don't throw exception &
        // Save old values
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
        String maxQueueSize = outputDefault.getMaxQueueSize();

        // Probe
        {
            outputDefault.setMaxQueueSize("1MB");
            Assert.assertEquals("1MB", outputDefault.getMaxQueueSize());
            outputDefault.setMaxQueueSize(maxQueueSize);
            Assert.assertEquals(outputDefault.getMaxQueueSize(), maxQueueSize);
            
            outputDefault.setDropEventsOnQueueFull(0);
            outputDefault.setHeartbeatFrequency(heartbeat+1);
            outputDefault.setIndexAndForward(!forward);
            outputDefault.setSendCookedData(!cookedData);
            outputDefault.update();
    
            Assert.assertEquals(0, outputDefault.getDropEventsOnQueueFull());
            Assert.assertEquals(heartbeat + 1, outputDefault.getHeartbeatFrequency());
            Assert.assertEquals(!forward, outputDefault.indexAndForward());
            Assert.assertEquals(!cookedData, outputDefault.getSendCookedData());
        }

        // Restore original values
        {
            outputDefault.setDropEventsOnQueueFull(onQueueFull);
            outputDefault.setHeartbeatFrequency(heartbeat);
            outputDefault.setIndexAndForward(forward);
            outputDefault.setSendCookedData(cookedData);
            outputDefault.update();
    
            Assert.assertEquals(onQueueFull, outputDefault.getDropEventsOnQueueFull());
            Assert.assertEquals(heartbeat, outputDefault.getHeartbeatFrequency());
            Assert.assertEquals(forward, outputDefault.indexAndForward());
            Assert.assertEquals(cookedData, outputDefault.getSendCookedData());
        }
    }
}
