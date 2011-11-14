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

package com.splunk.sdk.tests.com.splunk;

import com.splunk.*;
import com.splunk.sdk.Program;
import com.splunk.Service;

import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.*;

public class LicenseSlaveTest extends TestCase {
    Program program = new Program();

    public LicenseSlaveTest() {}

    Service connect() {
        return new Service(
            program.host, program.port, program.scheme)
                .login(program.username, program.password);
    }

    @Before public void setUp() {
        this.program.init(); // Pick up .splunkrc settings
    }

    @Test public void testLicenseSlave() throws Exception {
        Service service = connect();

        EntityCollection<LicenseSlave> ds = service.getLicenseSlaves();

        // list of pools
        List<String> pools = Arrays.asList("auto_generated_pool_download-trial",
            "auto_generated_pool_enterprise", "auto_generated_pool_forwarder",
            "auto_generated_pool_free");

        List<String> stacks = Arrays.asList(
             "download-trial", "enterprise", "forwarder", "free");

        for (LicenseSlave entity: ds.values()) {
            entity.get(); // force a read
            Assert.assertTrue(entity.getLabel().length() > 0);
            for (String pool: entity.getPoolIds()) {
                // special-case, fixed sourcetype has a hash at the end; so
                // no fixed value will match. Thus only check versus known
                // fixed values from list.
                if (!pool.startsWith("auto_generated_pool_fixed-sourcetype_")) {
                    Assert.assertTrue(pools.contains(pool));
                }
            }
            for (String stack: entity.getStackIds()) {
                Assert.assertTrue(stacks.contains(stack));
            }
        }
    }
}
