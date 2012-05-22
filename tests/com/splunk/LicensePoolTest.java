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

public class LicensePoolTest extends SplunkTestCase {
    final static String assertRoot = "License Pool assert: ";

    void checkLicensePool(LicensePool licensePool) {
        licensePool.getDescription();
        licensePool.getQuota();
        licensePool.getSlaves();
        licensePool.getSlavesUsageBytes();
        licensePool.getStackId();
        licensePool.getUsedBytes();
    }

    void checkLicensePools(LicensePoolCollection licensePoolCollection) {
        for (LicensePool licensePool : licensePoolCollection.values())
            checkLicensePool(licensePool);
    }

    @Test public void testLicensePool() throws Exception {
        Service service = connect();

        LicensePoolCollection licensePoolCollection = service.getLicensePools();

        checkLicensePools(licensePoolCollection);

        try {
            // The following will fail because there is no quota available
            licensePoolCollection.create("sdk-test", "MAX", "download-trial");
            fail("Expected pool create to fail");
        }
        catch (HttpException e) {
            assertEquals(assertRoot + "#1", 400, e.getStatus());
        }
      
        // Try updating some pools ..
        for (LicensePool licensePool : licensePoolCollection.values()) {
            if (licensePool.getStackId().equals("download-trial"))
                continue; // Can't edit a pool in stack: download-trial
            if (licensePool.getStackId().equals("forwarder"))
                continue; // Can't edit a pool in stack: forwarder
            if (licensePool.getStackId().equals("free"))
                continue; // Can't edit a pool in stack: free

            Args saved = new Args();
            saved.put("description", licensePool.getDescription());

            // update using setters
            licensePool.setDescription("sdk-test description");
            licensePool.setQuota("1048576");
            licensePool.update();

            assertEquals(assertRoot + "#2", 1024*1024,
                    (int)Integer.valueOf(licensePool.getQuota()));

            // use map technique for restore
            saved.put("quota", "MAX");
            licensePool.update(saved);
            assertEquals(assertRoot + "#3", saved.get("description"),
                licensePool.getDescription());
        }
    }
}
