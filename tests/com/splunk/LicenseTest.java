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
import com.splunk.sdk.Command;
import com.splunk.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.*;

public class LicenseTest extends TestCase {
    Command command;

    public LicenseTest() {}

    Service connect() {
        return Service.connect(command.opts);
    }

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
    }

    @Test public void testLicense() throws Exception {
        Service service = connect();

        EntityCollection<License> ds = service.getLicenses();

        // list of features, empirically created
        List<String> features = Arrays.asList(
            "Auth", "FwdData", "RcvData", "DeployClient", "SplunkWeb",
            "SyslogOutputProcessor", "SigningProcessor", "LocalSearch",
            "DistSearch", "RcvSearch", "ScheduledSearch", "Alerting",
            "DeployServer", "SigningProcessor", "SyslogOutputProcessor",
            "AllowDuplicateKeys", "CanBeRemoteMaster");

        // list of groupids, empirically created
        List<String> groups = Arrays.asList(
            "Forwarder", "Enterprise", "Free", "Trial");

        // list of statuses, empirically created
        List<String> stati = Arrays.asList("VALID", "EXPIRED");

        // list of types, empirically created
        List<String> types = Arrays.asList(
            "forwarder", "enterprise", "free", "download-trial");

        // test for sane data in licenses
        for (License entity: ds.values()) {
            entity.get(); // force a read
            Assert.assertTrue(entity.getCreationTime().after(new Date(0)));
            Assert.assertTrue(entity.getExpirationTime().after(new Date(0)));
            Assert.assertTrue(entity.getQuota() > 0);
            Assert.assertTrue(entity.getLicenseHash().length() == 64);
            for (String feature: entity.getFeatures()) {
                Assert.assertTrue(features.contains(feature));
            }
            Assert.assertTrue(groups.contains(entity.getGroupId()));
            Assert.assertTrue(entity.getLabel().length() > 0);
            Assert.assertTrue(entity.getMaxViolations() != 0);
            //System.out.println(entity.getSourceTypes());
            Assert.assertTrue(stati.contains(entity.getStatus()));
            Assert.assertTrue(types.contains(entity.getType()));
            //System.out.println(entity.getWindowPeriod());
        }
    }
}
