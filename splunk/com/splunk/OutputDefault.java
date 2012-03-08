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
 * The {@code OutputDefault} class represents the default TCP output configuration,
 * providing access to global TCP output properties.
 */
public class OutputDefault extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     */
    OutputDefault(Service service) {
        super(service, "data/outputs/tcp/default");
    }

    /**
     * Indicates whether this forwarder performs automatic load balancing.
     * When {@code true}, this forwarder selects a new indexer using the frequency
     * specified by {@code autoLBFrequency}, or immediately when an open connection
     * to an indexer is lost.
     *
     * @see #getAutoLbFrequency
     *
     * @return {@code true} if this forwarder performs automatic load balancing,
     * {@code false} if not.
     */
    public boolean autoLb() {
        return getBoolean("autoLB");
    }

    /**
     * Indicates whether this forwarder blocks when the output queue is full. 
     * Blocking causes further blocking up the processing chain--if any target group's queue
     * is blocked, no more data reaches any other target group.
     * When {@code false}, events are dropped when indexers in the group 
     * can't be reached. 
     *
     * @return {@code true} if this forwarder will block, {@code false} if not.
     */
    public boolean blockOnQueueFull() {
        return getBoolean("blockOnQueueFull", true);
    }

    /**
     * Returns the frequency of automatic load balancing. 
     *
     * @return The automatic load-balancing frequency in seconds, or -1 if not specified.
     */
    public int getAutoLbFrequency() {
        return getInteger("autoLBFrequency", -1);
    }

    /**
     * Returns the default indexer group that this forwarder sends all data to. <br/>
     * Note: This attribute is not required after Splunk version 4.2.
     *
     * @return A comma-separated list that contains one or more target group names,
     * or {@code null} if not specified.
     */
    public String getDefaultGroup() {
        return getString("defaultGroup", null);
    }

    /**
     * Returns the amount of time this forwarder waits before dropping
     * events if the output queue is full. A setting of 0 or -1 causes this
     * forwarder to block.
     *
     * @return The wait time, in seconds.
     */
    public int getDropEventsOnQueueFull() {
        return getInteger("dropEventsOnQueueFull", -1);
    }

    /**
     * Returns the inclusive set of indexes (whitelist 0) for this forwarder.
     * This is an ordered list of whitelists and blacklists, which together decide
     * if events should be forwarded to an index.
     *
     * @return The inclusive set of indexes.
     */
    public String getForwardedIndex0Whitelist() {
        return getString("forwardedindex.0.whitelist");
    }

    /**
     * Returns the exclusive set of indexes (blacklist 1) for this forwarder.
     * This is an ordered list of whitelists and blacklists, which together decide
     * if events should be forwarded to an index.
     *
     * @return The exclusive set of indexes.
     */
    public String getForwardedIndex1Blacklist() {
        return getString("forwardedindex.1.blacklist");
    }

    /**
     * Returns the inclusive set of indexes (whitelist 2) for this forwarder.
     * This is an ordered list of whitelists and blacklists, which together decide
     * if events should be forwarded to an index.
     *
     * @return The inclusive set of indexes.
     */
    public String getForwardedIndex2Whitelist() {
        return getString("forwardedindex.2.whitelist");
    }

    /**
     * Returns the frequency that specifies how often to send a heartbeat packet
     * to the receiving server.<br/>
     * Note: This field is only used when {@code sendCookedData} is {@code true}.
     *
     * @see #sendCookedData
     * @return The heartbeat frequency, in seconds.
     */
    public int getHeartbeatFrequency() {
        return getInteger("heartbeatFrequency", 30);
    }

    /**
     * Returns the maximum size of the output queue for this forwarder.
     * The maximum size of the wait queue is set to three times this value. 
     *
     * @return The maximum size of the output queue, in bytes.
     */
    public long getMaxQueueSize() {
        return getByteCount("maxQueueSize");
    }

    /**
     * Indicates whether to index data locally in addition to forwarding it.<br/>
     * Note: This setting is not available for light forwarders.
     *
     * @return {@code true} if data is indexed locally and forwarded, {@code false} 
     * if not.
     */
    public boolean indexAndForward() {
        return getBoolean("indexAndForward", false);
    }

    /**
     * Indicates whether this forwarder sends compressed data.
     *
     * @return {@code true} if this forwarder sends compressed data, {@code false}
     * if not.
     */
    public boolean isCompressed() {
        return getBoolean("compressed", false);
    }

    /**
     * Indicates whether the index filter for this forwarder is disabled.
     *
     * @return {@code true} if the index filter is disabled, {@code false}
     * if events are raw and untouched before sending.
     */
    public boolean isForwardedIndexFilterDisable() {
        return getBoolean("forwardedindex.filter.disable");
    }

    /**
     * Indicates whether Splunk has processed ("cooked") the event data.
     *
     * @return {@code true} if the event data has been processed, {@code false}
     * if not.
     */
    public boolean sendCookedData() {
        return getBoolean("sendCookedData", true);
    }
}
