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

public class LicensePoolTest extends SDKTestCase {
    @Test
    public void testCreateWithNoQuota() throws Exception {
        LicensePoolCollection licensePoolCollection = service.getLicensePools();
        try {
            // The following will fail because there is no quota available
            licensePoolCollection.create("sdk-test", "MAX", "download-trial");
            Assert.fail("Expected pool create to fail");
        }
        catch (HttpException e) {
            Assert.assertEquals(400, e.getStatus());
        }
    }
    
    @Test
    public void testLicensePoolSetters() throws Exception {
        LicensePoolCollection licensePoolCollection = service.getLicensePools();
        
        // Try updating some pools ..
        boolean foundAnUpdatablePool = false;
        for (LicensePool licensePool : licensePoolCollection.values()) {
            if (licensePool.getStackId().equals("download-trial"))
                continue; // Can't edit a pool in stack: download-trial
            if (licensePool.getStackId().equals("forwarder"))
                continue; // Can't edit a pool in stack: forwarder
            if (licensePool.getStackId().equals("free"))
                continue; // Can't edit a pool in stack: free
            foundAnUpdatablePool = true;

            String originalDescription = licensePool.getDescription();
            
            // Probe
            {
                licensePool.setDescription("sdk-test description");
                licensePool.setQuota("1048576");
                licensePool.update();
    
                Assert.assertEquals("sdk-test description", licensePool.getDescription());
                Assert.assertEquals("1048576", licensePool.getQuota());
            }

            licensePool.update(new Args("description", originalDescription));
            licensePool.update(new Args("quota", "MAX"));
        }
        
        if (!foundAnUpdatablePool) {
            System.out.println("WARNING: Didn't find any updatable license pools.");
        }
    }
    
    @Test
    public void testLicensePoolGetters() {
        LicensePoolCollection licensePoolCollection = service.getLicensePools();
        for (LicensePool licensePool : licensePoolCollection.values()) {
            testLicensePoolGetters(licensePool);
        }
    }
    
    private void testLicensePoolGetters(LicensePool licensePool) {
        licensePool.getDescription();
        licensePool.getQuota();
        licensePool.getSlaves();
        licensePool.getSlavesUsageBytes();
        licensePool.getStackId();
        licensePool.getUsedBytes();
    }
}
