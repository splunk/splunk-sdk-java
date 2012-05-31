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

public class DeploymentTenantTest extends SplunkTestCase {
    final static String assertRoot = "Deployment Tenant assert: ";

    @Test public void testDeploymentTenant() throws Exception {
        Service service = connect();

        EntityCollection<DeploymentTenant> deploymentTenants =
                service.getDeploymentTenants();
        if (deploymentTenants.values().size() == 0) {
            System.out.println("WARNING: Deployment Tenant not configured");
            return;
        }

        for (DeploymentTenant deploymentTenant: deploymentTenants.values()) {
            assertTrue(assertRoot + "#1",
                deploymentTenant.getWhitelistByIndex(0).length() > 0);
            deploymentTenant.isDisabled();
            for (int i=0; i<10; i++)
                deploymentTenant.getWhitelistByIndex(i);
        }
    }
}
