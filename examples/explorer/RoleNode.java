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

import com.splunk.Entity;

class RoleNode extends EntityNode {
    RoleNode(Entity entity) {
        super(entity);
    }

    @Override protected PropertyList getMetadata() {
        return new PropertyList() {{
            // UNDONE: add(List<String>.class, "getCapabilities");
            add(String.class, "getDefaultApp");
            // UNDONE: add(List<String>.class, "getImportedCapabilities");
            // UNDONE: add(List<String>.class, "getImportedRoles");
            add(int.class, "getImportedRtSearchJobsQuota");
            add(int.class, "getImportedSearchDiskQuota");
            add(String.class, "getImportedSearchFilter");
            // UNDONE: add(List<String>.class, "getImportedIndexesAllowed");
            // UNDONE: add(List<String>.class, "getImportedIndexesDefault");
            add(int.class, "getImportedSearchJobsQuota");
            add(int.class, "getRtSearchJobsQuota");
            add(int.class, "getSearchDiskQuota");
            add(String.class, "getSearchFilter");
            // UNDONE: add(List<String>.class, "getSearchIndexesAllowed");
            // UNDONE: add(List<String>.class, "getSearchIndexesDefault");
            add(int.class, "getSearchJobsQuota");
            add(int.class, "getSearchTimeWin");
        }};
    }
}

