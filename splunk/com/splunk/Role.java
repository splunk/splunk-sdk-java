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

/**
 * Representation of a Splunk role.
 */
public class Role extends Entity {

    /**
     * Constructs an instance of the Role entity.
     *
     * @param service The service the entity is affiliated with.
     * @param path The resource path.
     */
    Role(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns an array of capabilities assigned to this role.
     *
     * @return Array of capabilities.
     */
    public String[] getCapabilities() {
        return getStringArray("capabilities");
    }

    /**
     * Returns the name of the app to use as the default for this role.
     *
     * @return The default app for this role.
     */
    public String getDefaultApp() {
        return getString("defaultApp", null);
    }

    /**
     * Returns an array of capabilities imported for this role.
     *
     * @return Array of imported capabilities.
     */
    public String[] getImportedCapabilities() {
        return getStringArray("imported_capabilities", null);
    }

    /**
     * Returns an array of roles used to import attributes from.
     *
     * @return Array of roles imported from.
     */
    public String[] getImportedRoles() {
        return getStringArray("imported_roles", null);
    }

    /**
     * Returns the imported real time search jobs quota.
     *
     * @return Imported real time search jobs quota.
     */
    public int getImportedRtSearchJobsQuota() {
        return getInteger("imported_rtSrchJobsQuota");
    }

    /**
     * Returns the imported search disk quota.
     *
     * @return Imported serach disk quota.
     */
    public int getImportedSearchDiskQuota() {
        return getInteger("imported_srchDiskQuota");
    }

    /**
     * Returns the imported search filter.
     *
     * @return Imported search filter.
     */
    public String getImportedSearchFilter() {
        return getString("imported_srchFilter", null);
    }

    /**
     * Returns an array of imported allowed indexes.
     *
     * @return Array of imported allowed indexes.
     */
    public String[] getImportedIndexesAllowed() {
        return getStringArray("imported_srchIndexesAllowed", null);
    }

    /**
     * Returns an array of imported default indexes.
     *
     * @return Array of imported default indexes.
     */
    public String[] getImportedIndexesDefault() {
        return getStringArray("imported_srchIndexesDefault", null);
    }

    /**
     * Returns the imported normal search jobs quota.
     *
     * @return The imported normal search jobs quota.
     */
    public int getImportedSearchJobsQuota() {
        return getInteger("imported_srchJobsQuota");
    }

    /**
     * Returns the maximum number of concurrent real time search jobs allowed
     * for this role.
     *
     * @return Maximum number of concurrent real time search jobs.
     */
    public int getRtSearchJobsQuota() {
        return getInteger("rtSrchJobsQuota");
    }

    /**
     * Returns the maximum disk space in MB that can be used by a users in
     * this role.
     *
     * @return Maximum disk space usage for this role.
     */
    public int getSearchDiskQuota() {
        return getInteger("srchDiskQuota");
    }

    /**
     * Returns a search string that restricts the scope of searches run by
     * users in this role.
     *
     * @return Search string that restrics scope of searches.
     */
    public String getSearchFilter() {
        return getString("srchFilter", null);
    }

    /**
     * Returns an array of indexes that the role has permission to search.
     *
     * @return Array of indexes the role has permission to search.
     */
    public String[] getSearchIndexesAllowed() {
        return getStringArray("srchIndexesAllowed", null);
    }

    /**
     * Returns an array of default indexes to search when no index is specified.
     *
     * @return An array of default indexes to search when none is specified.
     */
    public String[] getSearchIndexesDefault() {
        return getStringArray("srchIndexesDefault", null);
    }

    /**
     * Returns the maximum number of concurrently running normal searches jobs
     * allowed for users in this role.
     *
     * @return Maximum number of concurrent normal search jobs.
     */
    public int getSearchJobsQuota() {
        return getInteger("srchJobsQuota");
    }

    /**
     * Returns the maximum time span of a search, in seconds, for users in this
     * role.
     *
     * @return Maximum time span of a search, in seconds.
     */
    public int getSearchTimeWin() {
        return getInteger("srchTimeWin");
    }
}
