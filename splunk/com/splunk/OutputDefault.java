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
 * Representation of the default TCP output configuration.
 */
public class OutputDefault extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     */
    OutputDefault(Service service) {
        super(service, "data/outputs/tcp/default");
    }

    /**
     * Returns whether or not this forwarded performs automatic load balancing.
     * When true, this forwarder selects a new indexer every autoLBFrequency
     * seconds, or immediately when an open connection to an indexer is lost.
     *
     * @return Whether or not this forwarder performs automatic load balancing.
     */
    public boolean autoLb() {
        return getBoolean("autoLB");
    }

    /**
     * Returns whether or not this forwarder will block. If set to false, events
     * will get thrown away if no indexers in the group are reachable.
     *
     * @return Whether or not this forwarder will block.
     */
    public boolean blockOnQueueFull() {
        return getBoolean("blockOnQueueFull", true);
    }

    /**
     * Returns this forwarder's auto load balancing frequency, or -1 if not
     * specified.
     *
     * @return this forwards auto load balancing frequency.
     */
    public int getAutoLbFrequency() {
        return getInteger("autoLBFrequency", -1);
    }

    /**
     * Returns this forwarder's default group as a comma separated list, or null
     * if not specified. After Splunk 4.2, this attribute is no longer required.
     *
     * @return this forwarder's default group list.
     */
    public String getDefaultGroup() {
        return getString("defaultGroup", null);
    }

    /**
     * Returns the number of second this forwarder's waits before throwing out
     * events if the output queue is full. A setting of 0 or -1 causes this
     * forwarder to block.
     *
     * @return this forwards wait time, in seconds, before events are dropped
     * under queue full conditions.
     */
    public int getDropEventsOnQueueFull() {
        return getInteger("dropEventsOnQueueFull", -1);
    }

    /**
     * Returns this forwarder's inclusive set of indexes.
     *
     * @return This forwarder's inclusive set of indexes.
     */
    public String getForwardedIndex0Whitelist() {
        return getString("forwardedindex.0.whitelist");
    }

    /**
     * Returns this forwarder's exclusive set of indexes.
     *
     * @return This forwarder's exclusive set of indexes.
     */
    public String getForwardedIndex1Blacklist() {
        return getString("forwardedindex.1.blacklist");
    }

    /**
     * Returns this forwarder's inclusive set of indexes.
     *
     * @return This forwarder's inclusive set of indexes.
     */
    public String getForwardedIndex2Whitelist() {
        return getString("forwardedindex.2.whitelist");
    }

    /**
     * Returns this forwarder's heartbeat frequency sent to the indexer. Note
     * that this field is only used if sendCookedData is set to true.
     *
     * @return This forwarder's heartbeat frequency.
     */
    public int getHeartbeatFrequency() {
        return getInteger("heartbeatFrequency", 30);
    }

    /**
     * Returns this forwarder's outbound event queue's maximum size, in bytes.
     * Implicitly this forwarder's wait queue is 3 times this value.
     *
     * @return This forwarder's output event queue's maximum size.
     */
    public long getMaxQueueSize() {
        return getByteCount("maxQueueSize");
    }

    /**
     * Returns whether or not to index data locally in addition to forwarding.
     * This setting is not available for light forwarders.
     *
     * @return Whether or not to index data locally in addition to forarding.
     */
    public boolean indexAndForward() {
        return getBoolean("indexAndForward", false);
    }

    /**
     * Returns whether or not this forwarder sends compressed data.
     *
     * @return Whether or not this forwarder sends compressed data.
     */
    public boolean isCompressed() {
        return getBoolean("compressed", false);
    }

    /**
     * Returns whether or not this forwarder's index filter is disabled.
     *
     * @return Whether or not this forwarder's index filter is disabled.
     */
    public boolean isForwardedIndexFilterDisable() {
        return getBoolean("forwardedindex.filter.disable");
    }

    /**
     * Returns whether or not the event data has been processed by Splunk.
     *
     * @return Whether ot nor the event data has been processed by Splunk.
     */
    public boolean sendCookedData() {
        return getBoolean("sendCookedData", true);
    }
}
