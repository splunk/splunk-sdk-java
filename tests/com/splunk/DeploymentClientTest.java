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

public class DeploymentClientTest extends SplunkTestCase {
    @Test public void testDeploymentClient() throws Exception {
        Service service = connect();

        DeploymentClient deploymentClient = service.getDeploymentClient();
        String uri = deploymentClient.getTargetUri();
        if (uri != null) {
            if (deploymentClient.isDisabled()) {
                deploymentClient.enable();
            }
            assertFalse(deploymentClient.isDisabled());
            deploymentClient.disable();
            assertTrue(deploymentClient.isDisabled());
            deploymentClient.enable();
            assertFalse(deploymentClient.isDisabled());
            Args args = new Args();
            args.put("targetUri", "1.2.3.4:8080");
            deploymentClient.update(args);
            assertEquals(deploymentClient.getTargetUri(), "1.2.3.4:8080");
            args.put("targetUri", uri);
            deploymentClient.update(args);
            assertEquals(deploymentClient.getTargetUri(), uri);
            deploymentClient.getServerClasses();
            deploymentClient.reload();
        }
        else {
            System.out.println("WARNING: deploymenClient not configured");
        }
    }
}
