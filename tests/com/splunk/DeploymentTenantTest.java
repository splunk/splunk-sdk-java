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

import org.junit.Assume;
import org.junit.Test;

public class DeploymentTenantTest extends SDKTestCase {
    @Test
    public void testDeploymentTenant() throws Exception {
        if (service.versionIsAtLeast("6.0")) {
            // WORKAROUND (DVPL-2993): The deployment server has changed in Splunk 6, and is not yet supported.
            return;
        }

        EntityCollection<DeploymentTenant> deploymentTenants =
                service.getDeploymentTenants();
        if (deploymentTenants.values().size() == 0) {
            System.out.println("WARNING: No DeploymentTenant entities to test");
            return;
        }

        for (DeploymentTenant deploymentTenant: deploymentTenants.values()) {
            // Ensure getters throw no exceptions
            deploymentTenant.isDisabled();
            assertTrue(deploymentTenant.getWhitelistByIndex(0).length() > 0);
            for (int i=0; i<10; i++) {
                deploymentTenant.getWhitelistByIndex(i);
            }
        }
    }
}
