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
 * The {@code DeploymentServerClass} class represents a Splunk deployment server
 * class, providing access to the configuration of a server class.
 */
public class DeploymentServerClass extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The deployment server class endpoint.
     */
    DeploymentServerClass(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns a list of the hosts that are excluded from this server class.
     *
     * @return A comma-separated list of excluded hosts, or {@code null} if not specified.
     */
    public String getBlackList() {
        return getString("blacklist", null);
    }

    /**
     * Returns a list of excluded client addresses, by index.
     *
     * @return A list of excluded client addresses, or {@code null} if not specified.
     */
    public String getBlackListByIndex(int index) {
        return getString(String.format("blacklist.%d", index), null);
    }

    /**
     * Indicates whether look-ups halt on the first server match or continue 
     * to match against multiple servers. Matching is done in the order that server classes are defined.
     *
     * @return {@code true} if configuration look-ups continue matching server classes, 
     * beyond the first match. {@code false} if only the first match is used.
     */
    public boolean getContinueMatching() {
        return getBoolean("continueMatching");
    }

    /**
     * Returns the URL template string, which specifies the endpoint from which 
     * content can be downloaded by a deployment client.
     *
     * @return The URL template string for deployment client downloads, or {@code null} if not specified.
     */
    public String getEndpoint() {
        return getString("endpoint", null);
    }

    /**
     * Returns the filter type that is applied first. If {@code filterType} is 
     * whitelist, all whitelist filters are applied first, followed by blacklist filters.
     * If {@code filterType} is blacklist, all blacklist filters are applied first,
     * followed by whitelist filters.
     *
     * @return The filter type.
     */
    public String getFilterType() {
        return getString("filterType");
    }

    /**
     * Returns the location on the deployment server to store the content 
     * that is to be deployed for this server class. 
     * Note: The path may contain macro expansions or substitutions.
     *
     * @return The file path for content storage on the deployment server.
     */
    public String getRepositoryLocation() {
        return getString("repositoryLocation");
    }

    /**
     * Returns the location on the deployment client where the content to be deployed
     * for this server class should be installed. 
     * Note: The path may contain macro expansions or substitutions.
     *
     * @return The file path for content storage on the deployment client.
     */
    public String getTargetRepositoryLocation() {
        return getString("targetRepositoryLocation", null);
    }

    /**
     * Returns the location of the working folder used by the deployment server. 
     * Note: The path may contain macro expansions or substitutions.
     *
     * @return The path to the deployment server's working folder, or {@code null} if not specified.
     */
    public String getTmpFolder() {
        return getString("tmpFolder", null);
    }

    /**
     * Returns a list of hosts included for this server class.
     * @return A comma-separated list of included hosts, or {@code null} if not specified.
     */
    public String getWhiteList() {
        return getString("whitelist", null);
    }

    /**
     * Returns a list of included client addresses, by index.
     * @return A list of included client addresses, or {@code null} if not specified.
     */
    public String getWhiteListByIndex(int index) {
        return getString(String.format("whitelist.%d", index), null);
    }
}
