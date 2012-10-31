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

public class DeploymentClientTest extends SDKTestCase {
    @Test
    public void testDeploymentClient() throws Exception {
        DeploymentClient deploymentClient = service.getDeploymentClient();
        String uri = deploymentClient.getTargetUri();
        if (uri == null) {
            System.out.println("WARNING: No DeploymentClient entities to test");
            return;
        }
        
        // Try to disable & enable with standard methods (which should fail)
        try {
            deploymentClient.disable();
            fail("Expected disable to fail.");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        try {
            deploymentClient.enable();
            fail("Expected enable to fail.");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        
        // Try to disable & enable with setters
        deploymentClient.setDisabled(true);
        // TODO: Should there be an update here?
        deploymentClient.setDisabled(false);
        // TODO: Should there be an update here?
        
        deploymentClient.reload();
        
        // Probe via setter
        deploymentClient.setTargetUri("1.2.3.4:8080");
        deploymentClient.update();
        assertEquals("1.2.3.4:8080", deploymentClient.getTargetUri());
        
        // Prove via argument map
        deploymentClient.update(new Args("targetUri", uri));
        assertEquals(uri, deploymentClient.getTargetUri());
        
        // Ensure getters throw no exceptions
        deploymentClient.getServerClasses();
        deploymentClient.getTargetUri();
    }
}
