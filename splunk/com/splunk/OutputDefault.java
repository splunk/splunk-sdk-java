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

import java.util.Map;

/**
 * The {@code OutputDefault} class represents the default TCP output
 * configuration, providing access to global TCP output properties.
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
     * When {@code true}, this forwarder selects a new indexer using the
     * frequency specified by {@code autoLBFrequency}, or immediately when an
     * open connection to an indexer is lost.
     *
     * @see #getAutoLBFrequency
     *
     * @return {@code true} if this forwarder performs automatic load balancing,
     * {@code false} if not.
     */
    public boolean getAutoLB() {
        return getBoolean("autoLB", false);
    }

    /**
     * Indicates whether this forwarder blocks when the output queue is full. 
     * Blocking causes further blocking up the processing chain--if any target
     * group's queue is blocked, no more data reaches any other target group.
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
     * @return The automatic load-balancing frequency in seconds, or -1 if not
     * specified.
     */
    public int getAutoLBFrequency() {
        return getInteger("autoLBFrequency", -1);
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
     * This is an ordered list of whitelists and blacklists, which together
     * decide if events should be forwarded to an index.
     *
     * @return The inclusive set of indexes.
     */
    public String getForwardedIndex0Whitelist() {
        return getString("forwardedindex.0.whitelist");
    }

    /**
     * Returns the exclusive set of indexes (blacklist 1) for this forwarder.
     * This is an ordered list of whitelists and blacklists, which together
     * decide if events should be forwarded to an index.
     *
     * @return The exclusive set of indexes.
     */
    public String getForwardedIndex1Blacklist() {
        return getString("forwardedindex.1.blacklist");
    }

    /**
     * Returns the inclusive set of indexes (whitelist 2) for this forwarder.
     * This is an ordered list of whitelists and blacklists, which together
     * decide if events should be forwarded to an index.
     *
     * @return The inclusive set of indexes.
     */
    public String getForwardedIndex2Whitelist() {
        return getString("forwardedindex.2.whitelist");
    }

    /**
     * Returns the frequency that specifies how often to send a heartbeat packet
     * to the receiving server.
     * <p>
     * <b>Note:</b> This field is only used when {@code SendCookedData} is
     * {@code true}.
     *
     * @see #getSendCookedData
     * @see #setSendCookedData
     * @return The heartbeat frequency, in seconds.
     */
    public int getHeartbeatFrequency() {
        return getInteger("heartbeatFrequency", 30);
    }

    /**
     * Returns the maximum size of the output queue for this forwarder.
     * The maximum size of the wait queue is set to three times this value. 
     *
     * @return The maximum size of the output queue. The string maybe "auto" or a number followed by one of the suffixes
     *  "B", "KB", "MB", or "GB" (for bytes, kilobytes, megabytes, and gigabytes, respectively).
     */
    public String getMaxQueueSize() {
        return getString("maxQueueSize");
    }

    /**
     * Indicates whether Splunk has processed ("cooked") the event data.
     *
     * @return {@code true} if the event data has been processed, {@code false}
     * if not.
     */
    public boolean getSendCookedData() {
        return getBoolean("sendCookedData", true);
    }

    /**
     * Indicates whether to index data locally in addition to forwarding it.
     * <p>
     * <b>Note:</b> This setting is not available for light forwarders.
     *
     * @return {@code true} if data is indexed locally and forwarded,
     * {@code false} if not.
     */
    public boolean indexAndForward() {
        return getBoolean("indexAndForward", false);
    }

    /**
     * Indicates whether this forwarder sends compressed data.
     *
     * @return {@code true} if this forwarder sends compressed data,
     * {@code false} if not.
     */
    public boolean isCompressed() {
        return getBoolean("compressed", false);
    }
    
    @Override
    protected boolean isNameChangeAllowed() {
        // The "name" property is actually required by the underlying POST
        // request that update() uses
        return true;
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
     * Sets how long to wait before throwing out all new events until the output
     * queue has space. The default value is -1, which means to not drop events.
     * <p><blockquote>
     * <b>Caution:</b> Do not set this value to a positive integer if you are
     * monitoring files.
     * </blockquote><p>
     * Setting this to -1 or 0 causes the output queue to block when it gets
     * full, which causes further blocking up the processing chain. If any
     * target group's queue is blocked, no more data reaches any other target
     * group.
     * <p>
     * Using auto load-balancing is the best way to minimize this condition,
     * because, in that case, multiple receivers must be down or jammed before 
     * queue blocking can occur.
     * @see #getAutoLB
     *
     * @param dropEventsOnQueueFull The time to wait before throwing out events,
     * in seconds, or -1 to not drop events.
     */
    public void setDropEventsOnQueueFull(int dropEventsOnQueueFull) {
        setCacheValue("dropEventsOnQueueFull", dropEventsOnQueueFull);
    }

    /**
     * Sets the frequency between heartbeat packets that are sent to the
     * receiving server.
     * <p>
     * <b>Note:</b> Heartbeats are only sent when {@code SendCookedData} is 
     * {@code true}. 
     * @see #getSendCookedData
     * @see #setSendCookedData
     *
     * @param frequency The frequency, in seconds. 
     */
    public void setHeartbeatFrequency(int frequency) {
        setCacheValue("heartbeatFrequency", frequency);
    }

    /**
     * Sets whether to index all data locally, in addition to forwarding it.
     *
     * This configuration is known as "index-and-forward". This attribute is
     * only available for heavy forwarders, at the top-level {@code [tcpout]}
     * stanza in outputs.conf. It cannot be overridden in a target group.
     *
     * @param indexAndForward {@code true} to index and forward data, 
     * {@code false} to forward only. 
     */
    public void setIndexAndForward(boolean indexAndForward) {
        setCacheValue("indexAndForward", indexAndForward);
    }

    /**
     * Sets the maximum size of the forwarder's output queue. This value also 
     * sets the maximum size of the wait queue to three times this
     * value, if you have enabled indexer acknowledgment (the {@code useACK} 
     * attribute is set to {@code true} in the forwarder's outputs.conf).
     * <p>
     * Although the wait queue and the output queues are both configured by
     * this attribute, they are separate queues. The setting determines the
     * maximum size of the queue's in-memory (RAM) buffer.
     * <p>
     * For heavy forwarders that send parsed data, {@code maxQueueSize} is the
     * maximum number of events. Because events are typically much shorter than 
     * data blocks, the memory consumed by the queue on a parsing forwarder will
     * likely be much smaller than on a non-parsing forwarder, if you use this
     * version of the setting.
     * <p>
     * If you specify an integer (for example, "100"), {@code maxQueueSize}
     * indicates the maximum number of queued events (for parsed data) or blocks
     * of data (for unparsed data). A block of data is approximately 64KB. For 
     * non-parsing forwarders that send unparsed data, such as universal 
     * forwarders, {@code maxQueueSize} is the maximum number of data blocks.
     * <p>
     * If specified as an integer followed by "KB", MB", or "GB" (for example,
     * "100MB"), {@code maxQueueSize} indicates the maximum RAM allocated
     * to the queue buffer. The default is 500KB (which means a maximum size of
     * 500KB for the output queue and 1500KB for the wait queue, if any).
     *
     * @param maxQueueSize The maximum queue size as a number, or a number 
     * followed by "KB", MB", or "GB".
     */
    public void setMaxQueueSize(String maxQueueSize) {
        setCacheValue("maxQueueSize", maxQueueSize);
    }

    /**
     * Sets the name. Only "tcpout" is valid.
     */
    public void setName() {
        setCacheValue("name", "tcpout");
    }

    /**
     * Sets whether to forward cooked data. 
     *
     * @param sendCookedData {@code true} for Splunk to process events before 
     * sending them, {@code false} to send raw and untouched events. 
     */
    public void setSendCookedData(boolean sendCookedData) {
        setCacheValue("sendCookedData", sendCookedData);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update(Map<String, Object> args) {
        // Add required arguments if not already present
        if (!args.containsKey("name")) {
            args = Args.create(args).add("name", "tcpout");
        }
        super.update(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update() {
        // If not present in the update keys, add required attribute as long
        // as one pre-existing update pair exists
        if (toUpdate.size() > 0 && !toUpdate.containsKey("name")) {
            setCacheValue("name", "tcpout");
        }
        super.update();
    }
}
