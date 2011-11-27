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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Test;

public class LicenseTest extends SplunkTestCase {
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
            assertTrue(entity.getCreationTime().after(new Date(0)));
            assertTrue(entity.getExpirationTime().after(new Date(0)));
            assertTrue(entity.getQuota() > 0);
            assertTrue(entity.getLicenseHash().length() == 64);
            for (String feature: entity.getFeatures()) {
                assertTrue(features.contains(feature));
            }
            assertTrue(groups.contains(entity.getGroupId()));
            assertTrue(entity.getLabel().length() > 0);
            assertTrue(entity.getMaxViolations() != 0);
            //System.out.println(entity.getSourceTypes());
            assertTrue(stati.contains(entity.getStatus()));
            assertTrue(types.contains(entity.getType()));
            //System.out.println(entity.getWindowPeriod());
        }

        // create
        FileReader fileReader;
        char [] buffer = new char[2048];
        try {
            File file = new File(
                "tests" + File.separator + "com" + File.separator +
                "splunk" + File.separator + "splunk.license");
            fileReader = new FileReader(file.getAbsolutePath());
        }
        catch (FileNotFoundException e) {
            System.out.println("WARNING: can't find test splunk.license file");
            return;
        }

        BufferedReader reader = new BufferedReader(fileReader);
        reader.read(buffer);
        Args args = new Args("payload", new String(buffer));
        License lic = ds.create("sdk-test", args);
        assertTrue(ds.containsKey(
           "6B7AD703356A487BDC513EE92B96A9B403C070EFAA30029C9784B0E240FA3101"));
        ds.remove(
           "6B7AD703356A487BDC513EE92B96A9B403C070EFAA30029C9784B0E240FA3101");
        assertFalse(ds.containsKey(
           "6B7AD703356A487BDC513EE92B96A9B403C070EFAA30029C9784B0E240FA3101"));
    }
}
