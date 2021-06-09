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
            // License features changed in 6.2, so don't check against our hardcoded list
            if (service.versionIsEarlierThan("6.2")) {
                for (String feature: license.getFeatures()) {
                    Assert.assertTrue(KNOWN_FEATURES.contains(feature));
                }
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

    // This test is very hard to maintain correctly do to the fact that licenses
    // expires. We're going to leave it out for now.
    /*@Test
    public void testCreateDelete() throws Exception {
        EntityCollection<License> licenses = service.getLicenses();

        String activeGroup = null;
        EntityCollection<Entity> licenseGroups = new EntityCollection<Entity>(service, "licenser/groups");
        for (Entity entity : licenseGroups.values()) {
            if (entity.getBoolean("is_active", false)) {
                activeGroup = entity.getName();
                break;
            }
        }

        try {
        Args args = new Args();
        args.put("is_active", "1");
        service.post("licenser/groups/Free", args);
        splunkRestart();

        String licenseKey;
        String licenseFilename;
        if (service.versionIsAtLeast("6.1")) {
            licenseKey = "EEDD55456662E29733FE185604ED22C44D1F472220BA3616E2605ECB322E4ACF";
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
        try {
            licenseStream.read(licensePayload);
        }
        finally {
            licenseStream.close();
        }
        
        // Create
        licenses.create(licenseKey, new Args("payload", new String(licensePayload)));
        
        // Remove
        Assert.assertTrue(licenses.containsKey(licenseKey));
        licenses.remove(licenseKey);
        Assert.assertFalse(licenses.containsKey(licenseKey));

        } finally {
            if (activeGroup != null) {
                Args args = new Args();
                args.put("is_active", "1");
                service.post("licenser/groups/" + activeGroup, args);
                splunkRestart();
            }
        }
    }*/
}
