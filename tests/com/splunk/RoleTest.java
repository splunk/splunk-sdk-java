/*
 * Copyright 2012 Splunk, Inc.
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
import java.util.List;

import org.junit.Test;

public class RoleTest extends SDKTestCase {
    @Test
    public void testSymmetricAccessors() {
        Role role = service.getRoles().create(createTemporaryName());
        try {
            role.setCapabilities(new String[] { "admin_all_objects" });
            role.setDefaultApp("search");
            role.setImportedRoles(new String[] { "user" });
            role.setSearchDiskQuota(9000);
            role.setSearchFilter("*");
            role.setSearchIndexesAllowed(new String[] { "*" });
            role.setSearchIndexesDefault(new String[] { "main", "_internal" });
            role.setSearchJobsQuota(49);
            
            role.setRealTimeSearchJobsQuota(99);
            role.setSearchTimeWindow(99);
            
            role.update();
            role.refresh();
            
            assertEquals(new String[] { "admin_all_objects" }, role.getCapabilities());
            assertEquals("search", role.getDefaultApp());
            assertEquals(new String[] { "user" }, role.getImportedRoles());
            assertEquals(9000, role.getSearchDiskQuota());
            assertEquals("*", role.getSearchFilter());
            assertEquals(new String[] { "*" }, role.getSearchIndexesAllowed());
            List<String> defaultIndexes = Arrays.asList(role.getSearchIndexesDefault());
            assertEquals(2, defaultIndexes.size());
            assertTrue(defaultIndexes.contains("main"));
            assertTrue(defaultIndexes.contains("_internal"));
            assertEquals(49, role.getSearchJobsQuota());
            
            assertEquals(99, role.getRealTimeSearchJobsQuota());
            assertEquals(99, role.getSearchTimeWindow());
        }
        finally {
            role.remove();
        }
    }
    
    @Test
    public void testArrayPromotingSetters() {
        Role role = service.getRoles().create(createTemporaryName());
        try {
            role.setCapabilities("admin_all_objects");
            role.setImportedRoles("user");
            role.setSearchIndexesAllowed("*");
            role.setSearchIndexesDefault("main");
            
            role.update();
            role.refresh();
            
            assertEquals(new String[] { "admin_all_objects" }, role.getCapabilities());
            assertEquals(new String[] { "user" }, role.getImportedRoles());
            assertEquals(new String[] { "*" }, role.getSearchIndexesAllowed());
            assertEquals(new String[] { "main" }, role.getSearchIndexesDefault());
        }
        finally {
            role.remove();
        }
    }
    
    @Test
    public void testUnpairedImportedGetters() {
        Role role = service.getRoles().create(createTemporaryName());
        try {
            role.setImportedRoles("user");
            
            role.update();
            role.refresh();
            
            /*
             * NOTE: These expected values probably come from the "user" role.
             *       If the default values of the "user" role change in the
             *       future, this test should be rewritten to lookup the values
             *       from the "user" role to determine the expected values here.
             */
            assertTrue(Arrays.asList(role.getImportedCapabilities()).contains(
                    "change_own_password"));
            assertEquals(6, role.getImportedRealTimeSearchJobsQuota());
            assertEquals(100, role.getImportedSearchDiskQuota());
            assertEquals(null, role.getImportedSearchFilter());
            assertEquals(new String[] { "*" }, role.getImportedIndexesAllowed());
            assertEquals(new String[] { "main" }, role.getImportedIndexesDefault());
            assertEquals(3, role.getImportedSearchJobsQuota());
            assertEquals(-1, role.getImportedSearchTimeWindow());
        }
        finally {
            role.remove();
        }
    }
}
