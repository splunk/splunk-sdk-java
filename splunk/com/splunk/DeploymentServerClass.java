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
 * Representation of the Splunk deployment server class.
 */
public class DeploymentServerClass extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The deployment server class endpoint.
     */
    DeploymentServerClass(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns a comma separated list of hosts excluded from this server class,
     * or null if not specified.
     *
     * @return A comma separated list of hosts excluded from this server class.
     */
    public String getBlackList() {
        return getString("blacklist", null);
    }

    /**
     * Return excluded client address by index.
     *
     * @return Excluded client address by index.
     */
    public String getBlackListByIndex(int index) {
        return getString(String.format("blacklist.%d", index), null);
    }

    /**
     * Returns whether lookups halt on first server match (false) or continues
     * to match against multiple servers (true).
     *
     * @return whether server matches first or multiple servers.
     */
    public boolean getContinueMatching() {
        return getBoolean("continueMatching");
    }

    /**
     * Returns the URL endpoint for deployment client content downloads.
     *
     * @return URL endpoint for deployment client downloads.
     */
    public String getEndpoint() {
        return getString("endpoint", null);
    }

    /**
     * Returns the filter type applied first. If filterType is whitelist, all
     * whitelist filters are applied first, followed by blacklist filters. If
     * filterType is blacklist, all blacklist filters are applied first,
     * followed by whitelist filters.
     *
     * @return Filter type.
     */
    public String getFilterType() {
        return getString("filterType");
    }

    /**
     * Returns the deployment server content storage file path. Note that the path
     * may contain macro expansions or substitutions.
     *
     * @return The deployment server content storage file path.
     */
    public String getRepositoryLocation() {
        return getString("repositoryLocation");
    }

    /**
     * Returns the deployment client content storage file path. Note that the path
     * may contain macro expansions or substitutions.
     *
     * @return The deployment client content storage file path.
     */
    public String getTargetRepositoryLocation() {
        return getString("targetRepositoryLocation", null);
    }

    /**
     * Returns the deployment server's working file path. Note that the path
     * may contain macro expansions or substitutions.
     * @return the deployment server's working file path.
     */
    public String getTmpFolder() {
        return getString("tmpFolder", null);
    }

    /**
     * Returns a comma separated list of hosts included for this server class,
     * or null if not specified.
     *
     * @return A comma separated list of hosts included for this server class.
     */
    public String getWhiteList() {
        return getString("whitelist", null);
    }

    /**
     * Return included client address by index.
     *
     * @return Included client address by index.
     */
    public String getWhiteListByIndex(int index) {
        return getString(String.format("whitelist.%d", index), null);
    }
}
