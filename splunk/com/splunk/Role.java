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
 * permissions and capabilities. The user's role determines what the user can
 * see and interact with in Splunk.
 */
public class Role extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The role endpoint.
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
     * @return An array of imported capabilities.
     */
    public String[] getImportedCapabilities() {
        return getStringArray("imported_capabilities", null);
    }

    /**
     * Returns an array of roles to import attributes from, such as capabilities
     * and allowed indexes to search.
     *
     * @return An array of imported roles.
     */
    public String[] getImportedRoles() {
        return getStringArray("imported_roles", null);
    }

    /**
     * Returns the maximum number of concurrent real-time search jobs a user
     * with this role is allowed to run.
     *
     * @return The imported quota for real-time search jobs.
     * @deprecated Use {@link #getImportedRealTimeSearchJobsQuota()} instead.
     */
    public int getImportedRtSearchJobsQuota() {
        return getInteger("imported_rtSrchJobsQuota");
    }
    
    /**
     * Returns the maximum number of concurrent real-time search jobs a user
     * with this role is allowed to run.
     *
     * @return The imported quota for real-time search jobs.
     */
    public int getImportedRealTimeSearchJobsQuota() {
        return getImportedRtSearchJobsQuota();
    }

    /**
     * Returns the maximum disk space that can be used for search jobs by a user
     * with this role.
     *
     * @return The imported search disk quota, in megabytes.
     */
    public int getImportedSearchDiskQuota() {
        return getInteger("imported_srchDiskQuota");
    }

    /**
     * Returns a search string that restricts the scope of searches run by this
     * role. Only those events that also match this search string are shown to
     * the user. If a user has multiple roles with different search filters,
     * they are combined with an {@code OR}.
     *
     * @return The imported search filter.
     */
    public String getImportedSearchFilter() {
        return getString("imported_srchFilter", null);
    }

    /**
     * Returns an array of indexes that a user with this role has permissions to
     * search.
     *
     * @return The imported array of allowed indexes.
     */
    public String[] getImportedIndexesAllowed() {
        return getStringArray("imported_srchIndexesAllowed", null);
    }

    /**
     * Returns an array of indexes to search by default when no index is
     * specified for a user with this role.
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
     * Returns the maximum time span of a search, in seconds. 
     *
     * @return The maximum time span of a search, in seconds. 
     */
    public int getImportedSearchTimeWindow() {
        return getInteger("imported_srchTimeWin");
    }

    /**
     * Returns the maximum number of concurrent real-time search jobs a user
     * with this role is allowed to run.
     *
     * @return Maximum number of concurrent real-time search jobs.
     * @deprecated Use {@link #getRealTimeSearchJobsQuota()} instead.
     */
    public int getRtSearchJobsQuota() {
        return getInteger("rtSrchJobsQuota");
    }
    
    /**
     * Returns the maximum number of concurrent real-time search jobs a user
     * with this role is allowed to run.
     *
     * @return Maximum number of concurrent real-time search jobs.
     */
    public int getRealTimeSearchJobsQuota() {
        return getRtSearchJobsQuota();
    }

    /**
     * Returns the maximum disk space that can be used for search jobs by a user
     * with this role.
     *
     * @return Maximum disk space usage, in megabytes.
     */
    public int getSearchDiskQuota() {
        return getInteger("srchDiskQuota");
    }

    /**
     * Returns a search string that restricts the scope of searches run by this
     * role. Only those events that also match this search string are shown to
     * the user. If a user has multiple roles with different search filters,
     * they are combined with an OR.
     *
     * @return The search filter.
     */
    public String getSearchFilter() {
        return getString("srchFilter", null);
    }

    /**
     * Returns an array of indexes that a user with this role has permissions
     * to search.
     *
     * @return An array of allowed indexes.
     */
    public String[] getSearchIndexesAllowed() {
        return getStringArray("srchIndexesAllowed", null);
    }

    /**
     * Returns an array of indexes to search by default when no index is
     * specified for a user with this role.
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
     * Returns the maximum time span of a search that is allowed for users in
     * this role.
     *
     * @return Maximum time span of a search, in seconds.
     * @deprecated Use {@link #getSearchTimeWindow()} instead.
     */
    public int getSearchTimeWin() {
        return getInteger("srchTimeWin");
    }
    
    /**
     * Returns the maximum time span of a search that is allowed for users in
     * this role.
     *
     * @return Maximum time span of a search, in seconds.
     */
    public int getSearchTimeWindow() {
        return getSearchTimeWin();
    }

    /**
     * Assigns an array of capabilities to this role. For a list of possible 
     * capabilities, see 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ7#capabilities"
     * target="_blank">Capabilities</a> on
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ7" 
     * target="_blank">dev.splunk.com</a>.
     *
     * @param capabilities An array of capabilities.
     */
    public void setCapabilities(String[] capabilities) {
        setCacheValue("capabilities", capabilities);
    }

    /**
     * Assigns a single capability to this role. 
     * For a list of possible capabilities, see 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ7#capabilities"
     * target="_blank">Capabilities</a> on
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ7" 
     * target="_blank">dev.splunk.com</a>.
     *
     * @param capability The capability to set.
     */
    public void setCapabilities(String capability) {
        setCapabilities(new String[] { capability });
    }

    /**
     * Sets the default app for this role.
     *
     * @param defaultApp The default app (the name of the folder that contains
     * the app).
     */
    public void setDefaultApp(String defaultApp) {
        setCacheValue("defaultApp", defaultApp);
    }

    /**
     * Sets a list of roles to import attributes from, such as capabilities and 
     * allowed indexes to search. In combining multiple roles, the effective 
     * value for each attribute is the value with the broadest permissions. 
     * <p>
     * Default Splunk roles are:
     * <p><ul>
     * <li>admin</li>
     * <li>can_delete</li>
     * <li>power</li>
     * <li>user</li>
     * </ul><p>
     * You also can specify additional roles that have been created. 
     *
     * @param importedRoles An array of roles from which to import attributes.
     */
    public void setImportedRoles(String[] importedRoles) {
        setCacheValue("imported_roles", importedRoles);
    }

    /**
     * Sets a role to import attributes from, such as capabilities and allowed 
     * indexes to search. Use this method to set a single role.
     * <p>Importing other roles imports all aspects of that role, such as 
     * capabilities and allowed indexes to search. 
     * <p>
     * Default Splunk roles are:
     * <p><ul>
     * <li>admin</li>
     * <li>can_delete</li>
     * <li>power</li>
     * <li>user</li>
     * </ul><p>
     * You also can specify additional roles that have been created. 
     *
     * @param importedRole A role from which to import attributes.
     */
    public void setImportedRoles(String importedRole) {
        setImportedRoles(new String[] { importedRole });
    }

    /**
     * Sets the maximum number of concurrent real-time search jobs a user with
     * this role is allowed to run. This count is independent from the normal 
     * search jobs limit.
     *
     * @param numJobs The maximum number of real-time search jobs.
     */
    public void setRealTimeSearchJobsQuota(int numJobs) {
        setCacheValue("rtSrchJobsQuota", numJobs);
    }

    /**
     * Sets the maximum disk space that can be used for search jobs by a user 
     * with this role.
     *
     * @param srchDiskQuota The maximum disk space to allocate, in megabytes.
     */
    public void setSearchDiskQuota(int srchDiskQuota) {
        setCacheValue("srchDiskQuota", srchDiskQuota);
    }

    /**
     * Sets a search string that restricts the scope of searches run by this
     * role. Search results for this role only show events that also match the
     * search string you specify. In the case that a user has multiple roles
     * with different search filters, they are combined with an {@code OR}.
     *
     * @param srchFilter The restrictive search string.
     */
    public void setSearchFilter(String srchFilter) {
        setCacheValue("srchFilter", srchFilter);
    }

    /**
     * Sets the indexes that a user with this role has permissions to search.
     *
     * @param indexesAllowed An array of allowed indexes.
     */
    public void setSearchIndexesAllowed(String[] indexesAllowed) {
        setCacheValue("srchIndexesAllowed", indexesAllowed);
    }

    /**
     * Sets the index that a user with this role has permissions to search. Use 
     * this method to set a single index.
     *
     * @param indexAllowed The allowed index.
     */
    public void setSearchIndexesAllowed(String indexAllowed) {
        setSearchIndexesAllowed(new String[] { indexAllowed });
    }

    /**
     * Sets the indexes to search by default when no index is specified for a 
     * user with this role.
     *
     * @param srchIndexesDefault An array of default indexes.
     */
    public void setSearchIndexesDefault(String[] srchIndexesDefault) {
        setCacheValue("srchIndexesDefault", srchIndexesDefault);
    }

    /**
     * Sets the index to search by default when no index is specified for a 
     * user with this role. Use this method to set a single default index.
     *
     * @param srchIndexDefault The default index.
     */
    public void setSearchIndexesDefault(String srchIndexDefault) {
        setSearchIndexesDefault(new String[] { srchIndexDefault });
    }

    /**
     * Sets the maximum number of concurrent searches a user with this role is
     * allowed to run. In the event of many roles per user, the maximum of
     * these quotas is applied.
     *
     * @param srchJobsQuota The maximum number of concurrent jobs.
     */
    public void setSearchJobsQuota(int srchJobsQuota) {
        setCacheValue("srchJobsQuota", srchJobsQuota);
    }

    /**
     * Sets the maximum time span of a search that is allowed for users in this 
     * role.
     *
     * @param srchTimeWin The maximum time span of a search, in seconds.
     */
    public void setSearchTimeWindow(int srchTimeWin) {
        setCacheValue("srchTimeWin", srchTimeWin);
    }
}
