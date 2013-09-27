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

public class MetadataTest extends SDKTestCase {
    @Test
    public void testMetadataGettersOfDefaultEntities() {
        testMetadataGetters(service.getApplications());
        // TODO: Fix these when deployment server stuff is resolved.
        //testMetadataGetters(service.getDeploymentClient());
        //testMetadataGetters(service.getDeploymentServerClasses());
        //testMetadataGetters(service.getDeploymentTenants());
        testMetadataGetters(service.getDistributedConfiguration());
        testMetadataGetters(service.getDistributedPeers());
        testMetadataGetters(service.getEventTypes());
        testMetadataGetters(service.getFiredAlertGroups());
        testMetadataGetters(service.getIndexes());
        testMetadataGetters(service.getInfo());
        testMetadataGetters(service.getInputs());
        testMetadataGetters(service.getJobs());
        testMetadataGetters(service.getLicenseGroups());
        testMetadataGetters(service.getLicensePools());
        testMetadataGetters(service.getLicenseSlaves());
        testMetadataGetters(service.getLicenseStacks());
        testMetadataGetters(service.getLicenses());
        testMetadataGetters(service.getLoggers());
        testMetadataGetters(service.getMessages());
        testMetadataGetters(service.getOutputDefault());
        testMetadataGetters(service.getOutputGroups());
        testMetadataGetters(service.getOutputServers());
        testMetadataGetters(service.getOutputSyslogs());
        testMetadataGetters(service.getPasswords());
        testMetadataGetters(service.getRoles());
        testMetadataGetters(service.getSavedSearches());
        testMetadataGetters(service.getSettings());
        testMetadataGetters(service.getUsers());
    }
    
    private void testMetadataGetters(
            EntityCollection<? extends Entity> entityCollection) {
        for (Entity entity : entityCollection.values()) {
            testMetadataGetters(entity);
        }
    }
    
    private void testMetadataGetters(Entity entity) {
        EntityMetadata metadata = entity.getMetadata();
        if (metadata == null) return;
        
        metadata.canChangePermissions();
        metadata.canShareApp();
        metadata.canShareGlobal();
        metadata.canShareUser();
        metadata.canWrite();
        metadata.getApp();
        metadata.getOwner();
        metadata.getPermissions();
        metadata.getSharing();
        metadata.isModifiable();
    }
}
