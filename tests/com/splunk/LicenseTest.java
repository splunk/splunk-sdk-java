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

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class LicenseTest extends SDKTestCase {
    // List of features, empirically created
    private static final List<String> KNOWN_FEATURES = Arrays.asList(
        "Auth", "FwdData", "RcvData", "DeployClient", "SplunkWeb",
        "SyslogOutputProcessor", "SigningProcessor", "LocalSearch",
        "DistSearch", "RcvSearch", "ScheduledSearch", "Alerting",
        "DeployServer", "SigningProcessor", "SyslogOutputProcessor",
        "AllowDuplicateKeys", "CanBeRemoteMaster");

    // List of groupids, empirically created
    private static final List<String> KNOWN_GROUP_IDS = Arrays.asList(
        "Forwarder", "Enterprise", "Free", "Trial");

    // List of statuses, empirically created
    private static final List<String> KNOWN_STATUSES = Arrays.asList("VALID", "EXPIRED");

    // List of types, empirically created
    private static final List<String> KNOWN_TYPES = Arrays.asList(
        "forwarder", "enterprise", "free", "download-trial");
    
    @Test
    public void testStacks() throws Exception {
        EntityCollection<LicenseStack> stacks = service.getLicenseStacks();
        for (LicenseStack stack : stacks.values()) {
            Assert.assertTrue(stack.getQuota() >= 0);
            Assert.assertTrue(!stack.getType().isEmpty());
            Assert.assertTrue(!stack.getLabel().isEmpty());
        }
    }
    
    @Test
    public void testDefaultLicensesHaveKnownProperties() throws Exception {
        EntityCollection<License> licenses = service.getLicenses();

        // Test for sane data in licenses
        for (License license: licenses.values()) {
            Assert.assertTrue(license.getCreationTime().after(new Date(0)));
            Assert.assertTrue(license.getExpirationTime().after(new Date(0)));
            Assert.assertTrue(license.getQuota() > 0);
            Assert.assertEquals(64, license.getLicenseHash().length());
            for (String feature: license.getFeatures()) {
                Assert.assertTrue(KNOWN_FEATURES.contains(feature));
            }
            Assert.assertTrue(KNOWN_GROUP_IDS.contains(license.getGroupId()));
            Assert.assertTrue(license.getLabel().length() > 0);
            Assert.assertNotSame(0, license.getMaxViolations());
            Assert.assertTrue(KNOWN_STATUSES.contains(license.getStatus()));
            Assert.assertTrue(KNOWN_TYPES.contains(license.getType()));
            license.getSourceTypes();
            license.getStackId();
            license.getWindowPeriod();
        }
    }

    @Test
    public void testCreateDelete() throws Exception {
        EntityCollection<License> licenses = service.getLicenses();
        
        if (licenses.containsKey("sdk-test")) {
            licenses.remove("sdk-test");
        }
        Assert.assertFalse(licenses.containsKey("sdk-test"));

        String licenseKey;
        String licenseFilename;
        if (service.versionIsAtLeast("6.1")) {
            licenseKey = "dTEDyZNMuiTE7Nu1Y3ZUr8l9pbGaX/RRo/eAhsGBHuo23VyYzaiyHnPHyr8IPL7d3opT/4EBfrCNI4HGwN1EOdiES9mt91fDQvEFx8f+p+RJnlFJfRQqbbWZWWjqxfJvYIlBc9GDiZ0xSo+Bc+DuYdlkG4f+WywHIH/k9HN6snqCxVojRJwiJAGjDn5FmUVIcaKCF84MaRVFPmRlicNzQ6pYvQjimPaUHNoP5NB9rgWg4ehaZ+bfR2AcjpgSBNiJOwKRI9EdNLA7GPXphcujyKynl45RSWZmqd5vQMHIH4a2BTeK+0QwPoJni4CUtN19yUFUNOiXHjx6Aunjw9qVtA==";
            licenseFilename = "splunk_at_least_cupcake.license";
        } else {
            licenseKey = "6B7AD703356A487BDC513EE92B96A9B403C070EFAA30029C9784B0E240FA3101";
            licenseFilename = "splunk.license";
        }

        if (licenses.containsKey(licenseKey)) {
            licenses.remove(licenseKey);
        }
        Assert.assertFalse(licenses.containsKey(licenseKey));
        
        // Read test license from disk
        byte[] licensePayload = new byte[2048];
        InputStream licenseStream = SDKTestCase.openResource(licenseFilename);
        Assert.assertNotNull("Could not find " + licenseFilename, licenseStream);
        try {
            licenseStream.read(licensePayload);
        }
        finally {
            licenseStream.close();
        }
        
        // Create
        licenses.create("sdk-test", new Args("payload", new String(licensePayload)));
        
        // Remove
        Assert.assertTrue(licenses.containsKey(licenseKey));
        licenses.remove(licenseKey);
        Assert.assertFalse(licenses.containsKey(licenseKey));
        
        clearRestartMessage();
    }
}
