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

import com.splunk.Resource;

class RoleNode extends ResourceNode {
    RoleNode(Resource value) {
        super(value);
    }

    @Override protected PropertyList getMetadata() {
        PropertyList list = super.getMetadata();
        // UNDONE: list.add(List<String>.class, "getCapabilities");
        list.add(String.class, "getDefaultApp");
        // UNDONE: list.add(List<String>.class, "getImportedCapabilities");
        // UNDONE: list.add(List<String>.class, "getImportedRoles");
        list.add(int.class, "getImportedRtSearchJobsQuota");
        list.add(int.class, "getImportedSearchDiskQuota");
        list.add(String.class, "getImportedSearchFilter");
        // UNDONE: list.add(List<String>.class, "getImportedIndexesAllowed");
        // UNDONE: list.add(List<String>.class, "getImportedIndexesDefault");
        list.add(int.class, "getImportedSearchJobsQuota");
        list.add(int.class, "getRtSearchJobsQuota");
        list.add(int.class, "getSearchDiskQuota");
        list.add(String.class, "getSearchFilter");
        // UNDONE: list.add(List<String>.class, "getSearchIndexesAllowed");
        // UNDONE: list.add(List<String>.class, "getSearchIndexesDefault");
        list.add(int.class, "getSearchJobsQuota");
        list.add(int.class, "getSearchTimeWin");
        return list;
    }
}

