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

public class Role extends Entity {
    public Role(Service service, String path) {
        super(service, path);
    }

    public String[] getCapabilities() {
        return getStringArray("capabilities");
    }

    public String getDefaultApp() {
        return getString("defaultApp", null);
    }

    public String[] getImportedCapabilities() {
        return getStringArray("imported_capabilities", null);
    }

    public String[] getImportedRoles() {
        return getStringArray("imported_roles", null);
    }

    public int getImportedRtSearchJobsQuota() {
        return getInteger("imported_rtSrchJobsQuota");
    }

    public int getImportedSearchDiskQuota() {
        return getInteger("imported_srchDiskQuota");
    }

    public String getImportedSearchFilter() {
        return getString("imported_srchFilter", null);
    }

    public String[] getImportedIndexesAllowed() {
        return getStringArray("imported_srchIndexesAllowed", null);
    }

    public String[] getImportedIndexesDefault() {
        return getStringArray("imported_srchIndexesDefault", null);
    }

    public int getImportedSearchJobsQuota() {
        return getInteger("imported_srchJobsQuota");
    }

    public int getRtSearchJobsQuota() {
        return getInteger("rtSrchJobsQuota");
    }

    public int getSearchDiskQuota() {
        return getInteger("srchDiskQuota");
    }

    public String getSearchFilter() {
        return getString("srchFilter", null);
    }

    public String[] getSearchIndexesAllowed() {
        return getStringArray("srchIndexesAllowed", null);
    }

    public String[] getSearchIndexesDefault() {
        return getStringArray("srchIndexesDefault", null);
    }

    public int getSearchJobsQuota() {
        return getInteger("srchJobsQuota");
    }

    public int getSearchTimeWin() {
        return getInteger("srchTimeWin");
    }
}

