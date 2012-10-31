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
     * Indicates whether Splunk automatically adds all discovered servers.
     *
     * @return {@code true} if Splunk automatically adds servers, {@code false}
     * if not.
     */
    public boolean getAutoAddServers() {
        return getBoolean("autoAddServers");
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
        return getInteger("checkTimedOutServersFrequency");
    }

    /**
     * Returns the period between heartbeat checks on other servers' health.
     *
     * @return The heartbeat period, in seconds. If 0, heartbeat checks are
     * disabled.
     */
    public int getHeartbeatFrequency() {
        return getInteger("heartbeatFrequency");
    }

    /**
     * Returns the multicast address where each Splunk server sends and 
     * listens for heartbeat messages.
     *
     * @return The multicast address for discovery and heartbeat messages, 
     * or {@code null} if not available.
     */
    public String getHeartbeatMcastAddress() {
        return getString("heartbeatMcastAddr", null);
    }

    /**
     * Returns the port where each Splunk server sends and listens for heartbeat
     * messages.
     *
     * @return The heartbeat port.
     */
    public int getHeartbeatPort() {
        return getInteger("heartbeatPort");
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
        return getBoolean("removedTimedOutServers");
    }

    /**
     * @deprecated Use specific {@code getConnectionTimeout}, 
     * {@code getReceiveTimeout}, and {@code getSendTimeout}.
     *
     * Returns the server time-out period.
     *
     * @return The server time-out period, in seconds.
     */
    public int getServerTimeout() {
        return getInteger("serverTimeout");
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
        return getBoolean("shareBundles");
    }

    /**
     * Indicates whether this server participates in a search or call. If set to
     * {@code true}, this server is skipped and does not participate. This 
     * setting is used for building a node that only merges the results from 
     * other servers.
     *
     * @return {@code true} if the server does not participate as a server in
     * any search, {@code false} if it does.
     */
    public boolean getSkipOurselves() {
        return getBoolean("skipOurselves");
    }

    /**
     * Returns the time-out period for gathering a search peer's basic info.
     * @see ServiceInfo
     *
     * @return The time-out period, in seconds.
     */
    public int getStatusTimeout() {
        return getInteger("statusTimeout");
    }

    /**
     * Returns the time-to-live (ttl) of heartbeat messages.
     *
     * @return The time-to-live of heartbeat messages.
     */
    public int getTtl() {
        return getInteger("ttl");
    }

    /**
     * Indicates whether distributed search is enabled.
     *
     * @return {@code true} if distributed search is enabled, {@code false} if
     * disabled.
     */
    public boolean isDistSearchEnabled() {
        return getBoolean("dist_search_enabled");
    }

    /**
     * Sets whether to automatically add discovered servers. 
     *
     * @param autoAdd {@code true} to add servers automatically, {@code false} 
     * if not. 
     */
    public void setAutoAddServers(boolean autoAdd) {
        setCacheValue("autoAddServers", autoAdd);
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
     * Sets the heartbeat frequency between peers.
     *
     * @param seconds The time for peer heartbeats, in seconds. If set to 0,
     * heartbeat messages are disabled.
     */
    public void setHeartbeatFrequency(int seconds) {
        setCacheValue("heartbeatFrequency", seconds);
    }

    /**
     * Sets the heartbeat multicast address. This address is used for server
     * auto discovery. The default address is "224.0.0.37".
     *
     * @param mCastAddress The multicast address for server auto discovery.
     */
    public void setHeartbeatMcastAddr(String mCastAddress) {
        setCacheValue("heartbeatMcastAddr", mCastAddress);
    }

    /**
     * Sets the port where Splunk sends and listens for heartbeat messages.
     *
     * @param port The heartbeat port.
     */
    public void setHeartbeatPort(int port) {
        setCacheValue("heartbeatPort", port);
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
     * @see #getAutoAddServers
     * @see #setAutoAddServers
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
     * Sets whether this server skips participation as a server in any search or
     * other call. This setting is used for building a node that only merges the
     * results from other servers.
     *
     * @param skipOurselves {@code true} to skip participation, {@code false} to
     * participate as a server in searches and calls.
     */
    public void setSkipOurselves(boolean skipOurselves) {
        setCacheValue("skipOurselves", skipOurselves);
    }

    /**
     * Sets the time-out period for connecting to a search peer for getting its
     * basic info.
     * @see ServiceInfo
     *
     * @param seconds The connection time-out period, in seconds.
     */
    public void setStatusTimeout(int seconds) {
        setCacheValue("statusTimeout", seconds);
    }

    /**
     * Sets the time-to-live (ttl) of heartbeat messages. Increasing this number
     * allows UDP packets to spread beyond the current sub-net to the specified
     * number of hops.
     * <p>
     * <b>Note:</b> This feature only works when routers along the way are 
     * configured to pass UDP multicast packets.
     *
     * @param value The time-to-live value of heartbeat messages.
     */
    public void setTTL(int value) {
        setCacheValue("ttl", value);
    }
}
