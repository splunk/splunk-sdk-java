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

/**
 * The {@code Role} class represents a Splunk role, which is a collection of
 * permissions and capabilities. The user's role determines what the user can see 
 * and interact with in Splunk. 
 */
public class Role extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The resource path.
     */
    Role(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns an array of capabilities assigned to this role.
     *
     * @return An array of capabilities.
     */
    public String[] getCapabilities() {
        return getStringArray("capabilities");
    }

    /**
     * Returns the app to use as the default app for this role. The user
     * can override this setting.
     *
     * @return The default app for this role.
     */
    public String getDefaultApp() {
        return getString("defaultApp", null);
    }

    /**
     * Returns an array of capabilities imported for this role.
     *
     * @return An array of capabilities.
     */
    public String[] getImportedCapabilities() {
        return getStringArray("imported_capabilities", null);
    }

    /**
     * Returns an array of roles used to import attributes from, such as capabilities 
     * and allowed indexes to search.
     *
     * @return An array of roles.
     */
    public String[] getImportedRoles() {
        return getStringArray("imported_roles", null);
    }

    /**
     * Returns the maximum number of concurrent real-time search jobs a user with this role 
     * is allowed to run. 
     *
     * @return The imported quota for real-time search jobs.
     */
    public int getImportedRtSearchJobsQuota() {
        return getInteger("imported_rtSrchJobsQuota");
    }

    /**
     * Returns the maximum disk space that can be used for search jobs by a user with this role.
     *
     * @return The imported search disk quota, in megabytes.
     */
    public int getImportedSearchDiskQuota() {
        return getInteger("imported_srchDiskQuota");
    }

    /**
     * Returns a search string that restricts the scope of searches run by this role. 
     * Only those events that also match this search string are shown to the user. 
     * If a user has multiple roles with different search filters, they are combined with an OR.
     *
     * @return The imported search filter.
     */
    public String getImportedSearchFilter() {
        return getString("imported_srchFilter", null);
    }

    /**
     * Returns an array of indexes that a user with this role has permissions to search.
     *
     * @return The imported array of allowed indexes.
     */
    public String[] getImportedIndexesAllowed() {
        return getStringArray("imported_srchIndexesAllowed", null);
    }

    /**
     * Returns an array of indexes to search by default when no index is specified
     * for a user with this role.
     *
     * @return The imported array of default indexes.
     */
    public String[] getImportedIndexesDefault() {
        return getStringArray("imported_srchIndexesDefault", null);
    }

    /**
     * Returns the maximum number of concurrent searches a user with this role 
     * is allowed to run.
     *
     * @return The imported quota for normal search jobs.
     */
    public int getImportedSearchJobsQuota() {
        return getInteger("imported_srchJobsQuota");
    }

    /**
     * Returns the maximum number of concurrent real-time search jobs a user with this role 
     * is allowed to run. 
     *
     * @return Maximum number of concurrent real-time search jobs.
     */
    public int getRtSearchJobsQuota() {
        return getInteger("rtSrchJobsQuota");
    }

    /**
     * Returns the maximum disk space that can be used for search jobs by a user with this role.
     *
     * @return Maximum disk space usage, in megabytes.
     */
    public int getSearchDiskQuota() {
        return getInteger("srchDiskQuota");
    }

    /**
     * Returns a search string that restricts the scope of searches run by this role. 
     * Only those events that also match this search string are shown to the user. 
     * If a user has multiple roles with different search filters, they are combined with an OR.
     *
     * @return The search filter.
     */
    public String getSearchFilter() {
        return getString("srchFilter", null);
    }

    /**
     * Returns an array of indexes that a user with this role has permissions to search.
     *
     * @return Array of allowed indexes.
     */
    public String[] getSearchIndexesAllowed() {
        return getStringArray("srchIndexesAllowed", null);
    }

    /**
     * Returns an array of indexes to search by default when no index is specified
     * for a user with this role.
     *
     * @return An array of default indexes.
     */
    public String[] getSearchIndexesDefault() {
        return getStringArray("srchIndexesDefault", null);
    }

    /**
     * Returns the maximum number of concurrent searches a user with this role 
     * is allowed to run.
     *
     * @return The quota for normal search jobs.
     */
    public int getSearchJobsQuota() {
        return getInteger("srchJobsQuota");
    }

    /**
     * Returns the maximum time span of a search that is allowed for users in this
     * role.
     *
     * @return Maximum time span of a search, in seconds.
     */
    public int getSearchTimeWin() {
        return getInteger("srchTimeWin");
    }
}
