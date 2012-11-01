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

public class OutputGroupTest extends SDKTestCase {
    @Test
    public void testOutputGroup() throws Exception {
        EntityCollection<OutputGroup> outputGroups = service.getOutputGroups();

        if (outputGroups.values().size() == 0) {
            System.out.println("WARNING: No OutputGroups to test");
            return;
        }

        for (OutputGroup outputGroup: outputGroups.values()) {
            // Getters don't throw exception &
            // Save old values
            outputGroup.getMethod();
            String[] servers = outputGroup.getServers();
            outputGroup.isDisabled();
            outputGroup.getAutoLB();

            // Probe
            {
                outputGroup.setServers("1.1.1.1:9997");
                outputGroup.update();
    
                String[] updatedServers = outputGroup.getServers();
                assertTrue(contains(updatedServers, "1.1.1.1:9997"));
            }

            // Restore original values
            {
                outputGroup.setServers(Util.join(",", servers));
                outputGroup.update();
                
                outputGroup.getServers();
            }
        }
    }
}
