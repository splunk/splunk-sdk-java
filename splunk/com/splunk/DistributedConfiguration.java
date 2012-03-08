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
 * search configuration, providing access to Splunk's distributed search options.
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
     * @return {@code true} if Splunk automatically adds servers, {@code false} if not.
     */
    public boolean getAutoAddServers() {
        return getBoolean("autoAddServers");
    }

    /**
     * Returns a list of server names that are excluded from
     * being peers.
     *
     * @return A comma-separated list of excluded servers by name, or {@code null} if not available.
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
     * @return The connection time-out period, in seconds, or -1 if not specified.
     */
    public int getConnectionTimeout() {
        return getInteger("connectionTimeout", -1);
    }

    /**
     * Returns the frequency at which servers that have timed out are rechecked.
     * If {@code removeTimedOutServers} is false, this setting has no effect.
     *
     * @return The frequency, in seconds, to recheck timed-out servers. If 0, servers 
     * are not rechecked. The default is 60 seconds.
     */
    public int getCheckTimedOutServersFrequency() {
        return getInteger("checkTimedOutServersFrequency");
    }

    /**
     * Returns the period between heartbeat checks on other servers' health.
     *
     * @return The heartbeat period, in seconds. If 0, heartbeat checks are disabled. 
     */
    public int getHeartbeatFrequency() {
        return getInteger("heartbeatFrequency");
    }

    /**
     * Returns the multi-cast address where each Splunk server sends and 
     * listens for heartbeat messages.
     *
     * @return The multi-cast address for discovery and heartbeat messages, 
     * or {@code null} if not available.
     */
    public String getHeartbeatMcastAddress() {
        return getString("heartbeatMcastAddr", null);
    }

    /**
     * Returns the port where each Splunk server sends and listens for heartbeat messages.
     *
     * @return The heartbeat port.
     */
    public int getHeartbeatPort() {
        return getInteger("heartbeatPort");
    }

    /**
     * Returns the time-out period for trying to read and receive data from a search peer.
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
     * @return {@code true} if timed-out servers are removed from the distributed
     * configuration, {@code false} if not.
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
     * Returns the time-out period for trying to write or send data to a search peer.
     *
     * @return The send time-out period, in seconds, or -1 if not specified.
     */
    public int getSendTimeout() {
        return getInteger("sendTimeout", -1);
    }

    /**
     * Returns a list of peer servers.
     *
     * @return The comma-separated list of peer servers, or {@code null} if not available.
     */
    public String getServers() {
        return getString("servers", null);
    }

    /**
     * Indicates whether this server uses bundle replication to share
     * search-time configuration with search peers. 
     * 
     * Note: If set to {@code false}, the search head assumes that the
     * search peers can access the correct bundles using an NFS share.
     *
     * @return {@code true} if this server uses bundle replication, {@code false} if not.
     */
    public boolean getShareBundles() {
        return getBoolean("shareBundles");
    }

    /**
     * Indicates whether this server participates in a search. If set to
     * true, this server does NOT participate as a server in any search or other
     * call. This setting is used for building a node that does nothing but merge the
     * results from other servers.
     *
     * @return {@code true} if the server does not participate as a server in any
     * search, {@code false} if it does.
     */
    public boolean getSkipOurselves() {
        return getBoolean("skipOurselves");
    }

    /**
     * Returns the time-out period for gathering a search peer's basic info 
     * (/services/server/info).
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
     * @return {@code true} if distributed search is enabled, {@code false} if disabled.
     */
    public boolean isDistSearchEnabled() {
        return getBoolean("dist_search_enabled");
    }
}
