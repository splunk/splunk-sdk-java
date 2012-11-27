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
     * @return A comma-separated list of excluded hosts, or {@code null} if not
     * specified.
     */
    public String getBlacklist() {
        return getString("blacklist", null);
    }

    /**
     * Returns a list of excluded client addresses, by index. 
     *
     * @param index The index of the blacklist entry to return. The only valid
     * index is 0.
     * @return A list of excluded client addresses, or {@code null} if not
     * specified.
     */
    public String getBlacklistByIndex(int index) {
        return getString(String.format("blacklist.%d", index), null);
    }

    /**
     * Indicates whether look-ups halt on the first server match or continue 
     * to match against multiple servers. Matching is done in the order that
     * server classes are defined.
     *
     * @return {@code true} if configuration look-ups continue matching server
     * classes, beyond the first match. {@code false} if only the first match
     * is used.
     */
    public boolean getContinueMatching() {
        return getBoolean("continueMatching", false);
    }

    /**
     * Returns the URL template string, which specifies the endpoint from which 
     * content can be downloaded by a deployment client.
     *
     * @return The URL template string for deployment client downloads, or
     * {@code null} if not specified.
     */
    public String getEndpoint() {
        return getString("endpoint", null);
    }

    /**
     * Returns the filter type that is applied first. If {@code filterType} is 
     * "whitelist", all whitelist filters are applied first, followed by 
     * blacklist filters. If {@code filterType} is "blacklist", all blacklist 
     * filters are applied first, followed by whitelist filters.
     *
     * @return The filter type.
     */
    public String getFilterType() {
        return getString("filterType");
    }

    /**
     * Returns the location on the deployment server to store the content 
     * that is to be deployed for this server class. 
     * <p>
     * <b>Note:</b> The path may contain macro expansions or substitutions.
     *
     * @return The file path for content storage on the deployment server.
     */
    public String getRepositoryLocation() {
        return getString("repositoryLocation");
    }

    /**
     * Returns the location on the deployment client where the content to be
     * deployed for this server class should be installed.
     * <p>
     * <b>Note:</b> The path may contain macro expansions or substitutions.
     *
     * @return The file path for content storage on the deployment client.
     */
    public String getTargetRepositoryLocation() {
        return getString("targetRepositoryLocation", null);
    }

    /**
     * Returns the location of the working folder used by the deployment server. 
     * <p>
     * <b>Note:</b> The path may contain macro expansions or substitutions.
     *
     * @return The path to the deployment server's working folder, or
     * {@code null} if not specified.
     */
    public String getTmpFolder() {
        return getString("tmpFolder", null);
    }

    /**
     * Returns a list of hosts included for this server class.
     * @return A comma-separated list of included hosts, or {@code null} if not
     * specified.
     */
    public String getWhitelist() {
        return getString("whitelist", null);
    }

    /**
     * Returns a list of included client addresses, by index.
     *
     * @param index The index of the whitelist entry to return. Valid indexes
     * are 0 and 1.
     * @return A list of included client addresses, or {@code null} if not
     * specified.
     */
    public String getWhitelistByIndex(int index) {
        return getString(String.format("whitelist.%d", index), null);
    }

    /**
     * Sets a specific blacklist entry.
     *
     * @param index The index of the blacklist entry to set.
     * @param blacklist The blacklist entry.
     */
    public void setBlacklistByIndex(int index, String blacklist) {
        setCacheValue(String.format("blacklist.%d", index), blacklist);
    }

    /**
     * Sets how this deployment server class controls its configurations
     * across classes and server-specific settings. If set to {@code true},
     * configuration look-ups continue matching server classes after the first
     * match. If set to {@code false}, only the first match is used. Matching
     * is done in the order that server classes are defined.
     *
     * @param matching {@code true} to continue matching, {@code false} to use 
     * the first match. 
     */
    public void setContinueMatching(boolean matching) {
        setCacheValue("continueMatching", matching);
    }

    /**
     * Sets a URL template string that specifies the endpoint from which
     * content can be downloaded by a deployment client. The deployment client
     * knows how to substitute the values of the variables in the URL. Any
     * custom URL can be provided here as long as it uses the specified
     * variables.
     *
     * You don't have to set this URL template string unless you have a very
     * specific need--for example, you need to acquire deployment application 
     * files from a third-party httpd for extremely large environments.
     *
     * @param endPoint The endpoint URL.
     */
    public void setEndPoint(String endPoint) {
        setCacheValue("endpoint", endPoint);
    }

    /**
     * Sets the order to apply filters:
     * <ul><li>"whitelist" applies the whitelist filters first, followed by 
     * blacklist filters.</li>
     * <li>"blacklist" applies the blacklist filters first, followed by 
     * whitelist filters. </li></ul>
     *
     * @param filterType The filter type to apply first.
     */
    public void setFilterType(String filterType) {
        setCacheValue("filterType", filterType);
    }

    /**
     * Sets the location on the deployment server to store the content that is 
     * to be deployed for this server class.
     *
     * @param location The location (path) for storing content.
     */
    public void setRepositoryLocation(String location) {
        setCacheValue("repositoryLocation", location);

    }

    /**
     * Sets a specific whitelist entry.
     *
     * @param index The index of the whitelist entry to set.
     * @param whitelist The whitelist entry.
     */
    public void setWhitelistByIndex(int index, String whitelist) {
        setCacheValue(String.format("whitelist.%d", index), whitelist);
    }
}
