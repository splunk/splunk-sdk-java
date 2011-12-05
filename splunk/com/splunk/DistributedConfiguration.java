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
 * Representation of the Splunk distributed search configuration.
 */
public class DistributedConfiguration extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
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
     * Returns whether Splunk automatically discovers other servers.
     *
     * @return whether Splunk automatically discovers other servers.
     */
    public boolean getAutoAddServers() {
        return getBoolean("autoAddServers");
    }

    /**
     * Returns a comma separated list of server names that are excluded from
     * being peers.
     *
     * @return servers, by name, that are excluded from being peers.
     */
    public String getBlacklistNames() {
        return getString("blacklistNames", null);
    }

    /**
     * Returns a comma separated list of server URLs that are excluded from
     * being peers. URLs are in the form x.x.x.x:port.
     *
     * @return servers, by URL, that are excluded from being peers.
     */
    public String getBlacklistUrls() {
        return getString("blacklistURLs", null);
    }

    /**
     * Returns the number of seconds between checking servers that are already
     * timed-out. Zero means do not recheck. The default is sixty seconds. If
     * removeTimedOutServers is false the setting has no effect.
     *
     * @return The number of seconds to check timed-out servers.
     */
    public int getCheckTimedOutServersFrequency() {
        return getInteger("checkTimedOutServersFrequency");
    }

    /**
     * Returns the number of seconds between checking on other servers health.
     * If set to zero, heartbeat checks are disabled.
     *
     * @return The number of seconds between checking on other servers health.
     */
    public int getHeartbeatFrequency() {
        return getInteger("heartbeatFrequency");
    }

    /**
     * Returns the multi-cast address to discover other servers and receive
     * heartbeats.
     *
     * @return The multi-cast address for discovery and heartbeat messages.
     */
    public String getHeartbeatMcastAddress() {
        return getString("heartbeatMcastAddr", null);
    }

    /**
     * Returns the port used for heartbeat messages.
     *
     * @return The heatbeat port.
     */
    public int getHeartbeatPort() {
        return getInteger("heartbeatPort");
    }

    /**
     * Returns whether or not timed out servers are removed from the distributed
     * configuration.
     *
     * @return whether ot not timed out servers are removed from the distributed
     * configuration.
     */
    public boolean getRemovedTimedOutServers() {
        return getBoolean("removedTimedOutServers");
    }

    /**
     * @deprecated
     *
     * Returns the timeout in seconds, to determine a server timeout.
     *
     * @return The timeout in seconds, to determine a server timeout.
     */
    public int getServerTimeout() {
        return getInteger("serverTimeout");
    }

    /**
     * Returns a comma separated list of peer servers.
     *
     * @return The list of peer servers.
     */
    public String getServers() {
        return getString("servers", null);
    }

    /**
     * Returns whether or not this server uses bundle replication to share
     * search time configuration with search peers. Note: if set to false,
     * the search head assumes the search peers can access a bundles through
     * shared storage (NAS).
     *
     * @return whether or not this server uses bundle replication.
     */
    public boolean getShareBundles() {
        return getBoolean("shareBundles");
    }

    /**
     * Returns whether or not this server participates in a search. If set to
     * true, this server does NOT participate as a server in any search or other
     * call. This is used for building a node that does nothing but merge the
     * results from other servers.
     *
     * @return Whether or not this server participates in a search.
     */
    public boolean getSkipOurselves() {
        return getBoolean("skipOurselves");
    }

    /**
     * Returns the number of seconds for connection timeouts when gathering a
     * search peer's basic information.
     *
     * @return The number of seconds for connection timeout.
     */
    public int getStatusTimeout() {
        return getInteger("statusTimeout");
    }

    /**
     * Returns the time-to-live of heartbeat messages.
     *
     * @return The time-to-live of heartbeat messages.
     */
    public int getTtl() {
        return getInteger("ttl");
    }

    /**
     * Returns whether or not distributed search is enabled.
     *
     * @return If distributed search is enabled or disabled.
     */
    public boolean isDistSearchEnabled() {
        return getBoolean("dist_search_enabled");
    }

    // UNDONE: no samples for:
    // connectionTimeout, receiveTimeout, sendTimeout

}
