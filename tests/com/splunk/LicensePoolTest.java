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

import com.splunk.sdk.Command;

import junit.framework.TestCase;
import org.junit.*;

public class LicensePoolTest extends TestCase {
    Command command;

    public LicensePoolTest() {}

    Service connect() {
        return Service.connect(command.opts);
    }

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
    }

    @Test public void testLicensePool() throws Exception {
        Service service = connect();

        EntityCollection<LicensePool> lps= service.getLicensePools();

        // test for sane data in licenses
        for (LicensePool entity: lps.values()) {
            entity.get(); // force a read
            entity.getDescription();
            entity.getQuota();
            entity.getSlaves();
            entity.getSlavesUsageBytes();
            entity.getStackId();
            entity.getUsedBytes();
        }

        // use well known auto pool
        LicensePool pe = lps.get("auto_generated_pool_enterprise");
        if (pe != null) {
            Args saved = new Args();
            saved.put("description", pe.getDescription());

            Args update = new Args();
            update.put("description", "sdk-test description");
            update.put("quota", 1024*1024);
            update.put("slaves", pe.getSlaves());
            update.put("append_slaves", true);

            pe.update(update);
            assertEquals(pe.getDescription(), update.get("description"));
            assertEquals(pe.getQuota(), 1024*1024);

            saved.put("quota", "MAX");
            saved.put("append_slaves", false);
            saved.put("slaves", "*");
            pe.update(saved);
            assertEquals(pe.getDescription(), saved.get("description"));
        }
    }
}
