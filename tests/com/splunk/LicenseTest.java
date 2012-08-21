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
    final static String assertRoot = "License assert: ";

    @Test public void testLicense() throws Exception {
        Service service = connect();

        EntityCollection<License> licenses = service.getLicenses();

        // List of features, empirically created
        List<String> features = Arrays.asList(
            "Auth", "FwdData", "RcvData", "DeployClient", "SplunkWeb",
            "SyslogOutputProcessor", "SigningProcessor", "LocalSearch",
            "DistSearch", "RcvSearch", "ScheduledSearch", "Alerting",
            "DeployServer", "SigningProcessor", "SyslogOutputProcessor",
            "AllowDuplicateKeys", "CanBeRemoteMaster");

        // List of groupids, empirically created
        List<String> groups = Arrays.asList(
            "Forwarder", "Enterprise", "Free", "Trial");

        // List of statuses, empirically created
        List<String> stati = Arrays.asList("VALID", "EXPIRED");

        // List of types, empirically created
        List<String> types = Arrays.asList(
            "forwarder", "enterprise", "free", "download-trial");

        // Test for sane data in licenses
        for (License license: licenses.values()) {
            assertTrue(assertRoot + "#1",
                license.getCreationTime().after(new Date(0)));
            assertTrue(assertRoot + "#2",
                license.getExpirationTime().after(new Date(0)));
            assertTrue(assertRoot + "#3", license.getQuota() > 0);
            assertEquals(assertRoot + "#4", 64,
                license.getLicenseHash().length());
            for (String feature: license.getFeatures()) {
                assertTrue(assertRoot + "#5", features.contains(feature));
            }
            assertTrue(assertRoot + "#6", groups.contains(license.getGroupId()));
            assertTrue(assertRoot + "#7", license.getLabel().length() > 0);
            assertNotSame(assertRoot + "#8", 0, license.getMaxViolations());
            assertTrue(assertRoot + "#9", stati.contains(license.getStatus()));
            assertTrue(assertRoot + "#10", types.contains(license.getType()));
            license.getSourceTypes();
            license.getStackId();
            license.getWindowPeriod();
        }

        if (licenses.containsKey("sdk-test")) {
            licenses.remove("sdk-test");
        }
        assertFalse(assertRoot + "#11", licenses.containsKey("sdk-test"));

        String licenseKey = "6B7AD703356A487BDC513EE92B96A9B403C070EFAA30029C9784B0E240FA3101";
        if (licenses.containsKey(licenseKey)) {
            licenses.remove(licenseKey);
        }
        assertFalse(licenses.containsKey(licenseKey));

        // Create
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
        licenses.create("sdk-test", args);
        assertTrue(assertRoot + "#12", licenses.containsKey(licenseKey));
        licenses.remove(licenseKey);
        assertFalse(assertRoot + "#13", licenses.containsKey(licenseKey));
    }
}
