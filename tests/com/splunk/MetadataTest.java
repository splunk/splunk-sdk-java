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

public class MetadataTest extends SplunkTestCase {

    void checkMetadata(Entity entity) {
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

    void checkMetadata(EntityCollection entityCollection) {
        for (Object entity : entityCollection.values())
            checkMetadata((Entity)entity);
    }

    @Test public void testMetadata() {
        Service service = connect();

        checkMetadata(service.getApplications());
        checkMetadata(service.getDeploymentClient());
        checkMetadata(service.getDeploymentServerClasses());
        checkMetadata(service.getDeploymentTenants());
        checkMetadata(service.getDistributedConfiguration());
        checkMetadata(service.getDistributedPeers());
        checkMetadata(service.getEventTypes());
        checkMetadata(service.getFiredAlertGroups());
        checkMetadata(service.getIndexes());
        checkMetadata(service.getInfo());
        checkMetadata(service.getInputs());
        checkMetadata(service.getJobs());
        checkMetadata(service.getLicenseGroups());
        checkMetadata(service.getLicensePools());
        checkMetadata(service.getLicenseSlaves());
        checkMetadata(service.getLicenseStacks());
        checkMetadata(service.getLicenses());
        checkMetadata(service.getLoggers());
        checkMetadata(service.getMessages());
        checkMetadata(service.getOutputDefault());
        checkMetadata(service.getOutputGroups());
        checkMetadata(service.getOutputServers());
        checkMetadata(service.getOutputSyslogs());
        checkMetadata(service.getPasswords());
        checkMetadata(service.getRoles());
        checkMetadata(service.getSavedSearches());
        checkMetadata(service.getSettings());
        checkMetadata(service.getUsers());
    }
}
