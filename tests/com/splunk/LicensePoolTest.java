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
    void checkLicensePool(LicensePool pool) {
        pool.getDescription();
        pool.getQuota();
        pool.getSlaves();
        pool.getSlavesUsageBytes();
        pool.getStackId();
        pool.getUsedBytes();
    }

    void checkLicensePools(LicensePoolCollection pools) {
        for (LicensePool pool : pools.values())
            checkLicensePool(pool);
    }

    @Test public void testLicensePool() throws Exception {
        Service service = connect();

        LicensePoolCollection pools = service.getLicensePools();

        checkLicensePools(pools);

        try {
            // The following will fail because there is no quota available
            pools.create("sdk-test", 1, "download-trial");
            fail("Expected pool create to fail");
        }
        catch (HttpException e) {
            assertEquals(e.getStatus(), 400);
        }
      
        // Try updating some pools ..
        for (LicensePool pool : pools.values()) {
            if (pool.getStackId().equals("download-trial"))
                continue; // Can't edit a pool in stack: download-trial
            if (pool.getStackId().equals("forwarder"))
                continue; // Can't edit a pool in stack: forwarder
            if (pool.getStackId().equals("free"))
                continue; // Can't edit a pool in stack: free

            Args saved = new Args();
            saved.put("description", pool.getDescription());

            Args args = new Args();
            args.put("description", "sdk-test description");
            args.put("quota", 1024*1024);
            pool.update(args);
            assertEquals(pool.getDescription(), args.get("description"));
            assertEquals(pool.getQuota(), 1024*1024);

            saved.put("quota", "MAX");
            pool.update(saved);
            assertEquals(pool.getDescription(),saved.get("description"));
        }
    }
}
