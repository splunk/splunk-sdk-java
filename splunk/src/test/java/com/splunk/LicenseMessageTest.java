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

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class LicenseMessageTest extends SDKTestCase {
    @Test
    public void testLicenseMessage() throws Exception {
        // SPL-83050: Known to fail. Should be fixed before Cupcake is released.
        EntityCollection<LicenseMessage> licenseMessages =
            service.getLicenseMessages();
        
        if (licenseMessages.values().isEmpty()) {
            System.out.println("WARNING: No license messages found to test.");
            return;
        }

        for (LicenseMessage licenseMessage: licenseMessages.values()) {
            // Test getters
            Assert.assertTrue(licenseMessage.getCreationTime().after(new Date(0)));
            licenseMessage.getCategory();
            licenseMessage.getDescription();
            licenseMessage.getPoolId();
            licenseMessage.getSeverity();
            licenseMessage.getSlaveId();
            licenseMessage.getStackId();
        }
    }
}
