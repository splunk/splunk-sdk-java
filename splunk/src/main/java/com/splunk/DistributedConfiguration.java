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
 * The {@code DistributedConfiguration} class represents a Splunk distributed 
 * search configuration, providing access to Splunk's distributed search
 * options.
 */
public class DistributedConfiguration extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     */
    DistributedConfiguration(Service service) {
        super(service, "search/distributed/config");
    }

    /**
     * Returns the action path.
     *
     * @param action The requested action.
     * @return The action path.
     */
    @Override protected String actionPath(String action) {
        if (action.equals("edit"))
            return path + "/distributedSearch";
        return super.actionPath(action);
    }

    /**
     * Disable distributed search on this Splunk instance.
     *
     * Requires restarting Splunk before it takes effect.
     */
    @Override
    public void disable() {
        Args args = new Args();
        args.put("disabled", "1");
        update(args);
    }

    /**
     * Enable distributed search on this Splunk instance.
     *
     * Requires restarting Splunk before it takes effect.
     */
    @Override
    public void enable() {
        Args args = new Args();
        args.put("disabled", "0");
        update(args);
    }

    /**
     * Returns a list of server names that are excluded from being peers.
     *
     * @return A comma-separated list of excluded servers by name, or
     * {@code null} if not available.
     */
    public String getBlacklistNames() {
        return getString("blacklistNames", null);
    }

    /**
     * Returns a list of server URLs that are excluded from being peers.
     *
     * @return A comma-separated list of excluded servers by URL in the 
     * format "x.x.x.x:port", or {@code null} if not available.
     */
    public String getBlacklistUrls() {
        return getString("blacklistURLs", null);
    }

    /**
     * Returns the time-out period for connecting to search peers. 
     *
     * @return The connection time-out period, in seconds, or -1 if not
     * specified.
     */
    public int getConnectionTimeout() {
        return getInteger("connectionTimeout", -1);
    }

    /**
     * Returns the frequency at which servers that have timed out are rechecked.
     * If {@code removeTimedOutServers} is false, this setting has no effect.
     *
     * @return The frequency, in seconds, to recheck timed-out servers. If 0,
     * servers are not rechecked. The default is 60 seconds.
     */
    public int getCheckTimedOutServersFrequency() {
        return getInteger("checkTimedOutServersFrequency", 60);
    }

    /**
     * Returns the time-out period for trying to read and receive data from a
     * search peer.
     *
     * @return The receive time-out period, in seconds, or -1 if not specified.
     */
    public int getReceiveTimeout() {
        return getInteger("receiveTimeout", -1);
    }

    /**
     * Indicates whether timed-out servers are removed from the distributed
     * configuration.
     *
     * @return {@code true} if timed-out servers are removed from the
     * distributed configuration, {@code false} if not.
     */
    public boolean getRemovedTimedOutServers() {
        return getBoolean("removedTimedOutServers", false);
    }

    /**
     * Returns the time-out period for trying to write or send data to a search
     * peer.
     *
     * @return The send time-out period, in seconds, or -1 if not specified.
     */
    public int getSendTimeout() {
        return getInteger("sendTimeout", -1);
    }

    /**
     * Returns a list of peer servers.
     *
     * @return The comma-separated list of peer servers, or {@code null} if not
     * available.
     */
    public String getServers() {
        return getString("servers", null);
    }

    /**
     * Indicates whether this server uses bundle replication to share
     * search-time configuration with search peers.
     * <p>
     * <b>Note:</b> If set to {@code false}, the search head assumes that the
     * search peers can access the correct bundles using an NFS share.
     *
     * @return {@code true} if this server uses bundle replication,
     * {@code false} if not.
     */
    public boolean getShareBundles() {
        return getBoolean("shareBundles", true);
    }

    /**
     * Returns the time-out period for gathering a search peer's basic information.
     *
     * @return The time-out period, in seconds.
     * @see ServiceInfo
     */
    public int getStatusTimeout() {
        return getInteger("statusTimeout", 10);
    }

    /**
     * Indicates whether distributed search is enabled.
     *
     * @return {@code true} if distributed search is enabled, {@code false} if
     * disabled.
     */
    public boolean isDistSearchEnabled() {
        return getBoolean("dist_search_enabled", true);
    }

    /**
     * Sets the blacklist server names that are excluded from being peers.
     *
     * @param names A comma-separated list of server names.
     */
    public void setBlacklistNames(String names) {
        setCacheValue("blacklistNames", names);
    }

    /**
     * Sets the blacklist server names or URIs that are excluded from being 
     * peers. The format for a URI is "x.x.x.x:port". 
     *
     * @param names A comma-separated list of server names or URIs.
     */
    public void setBlacklistURLs(String names) {
        setCacheValue("blacklistURLs", names);
    }

    /**
     * Sets the server recheck frequency. 
     * <p>
     * <b>Note:</b> This attribute is only relevant when 
     * {@code removeTimedOutServers} is set to {@code true}--otherwise, this 
     * attribute is ignored. 
     * @see #getRemovedTimedOutServers
     *
     * @param frequency The server recheck frequency, in seconds. If set to 0, a
     * recheck does not occur.
     */
    public void setCheckTimedOutServersFrequency(int frequency) {
        setCacheValue("checkTimedOutServersFrequency", frequency);
    }

    /**
     * Sets the time-out period for establishing a search peer connection.
     *
     * @param seconds The connection time-out period, in seconds.
     */
    public void setConnectionTimeout(int seconds) {
        setCacheValue("connectionTimeout", seconds);
    }

    /**
     * Sets whether the distributed configuration is enabled or disabled. 
     * <p>
     * <b>Note:</b> Using this method requires you to restart Splunk before this 
     * setting takes effect. To avoid restarting Splunk, use the 
     * {@code Entity.disable} and {@code Entity.enable} methods instead, which 
     * take effect immediately. 
     * @see Entity#disable
     * @see Entity#enable
     *
     * @param disabled {@code true} to disabled to deployment client,
     * {@code false} to enable.
     */
    public void setDisabled(boolean disabled) {
        setCacheValue("disabled", disabled);
    }

    /**
     * Sets the time-out period for reading and receiving data from a search 
     * peer.
     *
     * @param seconds The receive time-out period, in seconds.
     */
    public void setReceiveTimeout(int seconds) {
        setCacheValue("receiveTimeout", seconds);
    }

    /**
     * Sets whether to remove a server connection that cannot be made within the
     * time-out period specified by {@code connectionTimeout},
     * {@code sendTimeout}, or {@code receiveTimeout}.
     * If {@code false}, every call to that server attempts to connect, which 
     * might result in a slow user interface.
     * @see #setConnectionTimeout
     * @see #setSendTimeout
     * @see #setReceiveTimeout
     *
     * @param removeTimedOutServers {@code true} to remove timed-out server 
     * connections, {@code false} to attempt every call to the server.
     */
    public void setRemoveTimedOutServers(boolean removeTimedOutServers) {
        setCacheValue("removedTimedOutServers", removeTimedOutServers);
    }

    /**
     * Sets the time-out period for writing and sending data to a search peer.
     *
     * @param seconds The send time-out period, in seconds.
     */
    public void setSendTimeout(int seconds) {
        setCacheValue("sendTimeout", seconds);
    }

    /**
     * Sets the initial peer server list. You don't need to set servers here
     * if you are operating completely in {@code autoAddServers} mode
     * (discovered servers are automatically added).
     *
     * @param servers A comma-separated list of peer servers.
     */
    public void setServers(String servers) {
        setCacheValue("servers", servers);
    }

    /**
     * Sets whether this server uses bundle replication to share search-time 
     * configuration with search peers.
     *
     * If set to {@code false}, the search head assumes that the search peers
     * can access the correct bundles using an NFS share and have correctly
     * configured the options for mounted_bundles and bundles_location in the 
     * distsearch.conf file.
     *
     * @param shareBundles {@code true} to share search-time configuration
     * with peers, {@code false} if not.
     */
    public void setShareBundles(boolean shareBundles) {
        setCacheValue("shareBundles", shareBundles);
    }

    /**
     * Sets the time-out period for connecting to a search peer for getting its
     * basic information.
     *
     * @param seconds The connection time-out period, in seconds.
     * @see ServiceInfo
     */
    public void setStatusTimeout(int seconds) {
        setCacheValue("statusTimeout", seconds);
    }

}
