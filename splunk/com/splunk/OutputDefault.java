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
     * @see #getAutoLbFrequency
     *
     * @return {@code true} if this forwarder performs automatic load balancing,
     * {@code false} if not.
     */
    public boolean autoLb() {
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
    public int getAutoLbFrequency() {
        return getInteger("autoLBFrequency", -1);
    }

    /**
     * Returns the default indexer group that this forwarder sends all data to.
     * <br/>
     * Note: This attribute is not required after Splunk version 4.2.
     *
     * @return A comma-separated list that contains one or more target group
     * names, or {@code null} if not specified.
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
     * to the receiving server.<br/>
     * Note: This field is only used when {@code sendCookedData} is
     * {@code true}.
     *
     * @see #getSendCookedData
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
     * <br/>
     * Note: This setting is not available for light forwarders.
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
     * Sets the group names to forward to. Groups is a comma separated list of
     * one or more target group names, specified later in
     * @{code tcpout:<target_group>} stanzas of outputs.conf.spec file. The
     * forwarder sends all data to the specified groups. If you don't want to
     * forward data automatically, don't set this attribute. Can be overridden
     * by an inputs.conf _TCP_ROUTING setting, which in turn can be overridden
     * by a props.conf/transforms.conf modifier.
     *
     * Starting with 4.2, this attribute is no longer required.
     *
     * @param groups The comma separated list of target group names.
     */
    public void setDefaultGroup(String groups) {
        setCacheValue("defaultGroup", groups);
    }

    /**
     * If set to a positive number, wait the specified number of seconds before
     * throwing out all new events until the output queue has space. Defaults
     * to -1 (do not drop events).
     *
     * CAUTION: Do not set this value to a positive integer if you are
     * monitoring files.
     *
     * Setting this to -1 or 0 causes the output queue to block when it gets
     * full, which causes further blocking up the processing chain. If any
     * target group's queue is blocked, no more data reaches any other target
     * group.
     *
     * Using auto load-balancing is the best way to minimize this condition,
     * because, in that case, multiple receivers must be down (or jammed up)
     * before queue blocking can occur.
     *
     * @param dropEventsOnQueueFull The number of seconds before throwing events
     * out when queue is full.
     */
    public void setDropEventsOnQueueFull(int dropEventsOnQueueFull) {
        setCacheValue("dropEventsOnQueueFull", dropEventsOnQueueFull);
    }

    /**
     * Sets the frequency, in seconds, between heartbeat packets sent to the
     * receiving server.
     *
     * Note that eartbeats are only sent if {@code sendCookedData=true}
     *
     * @param frequency The number of seconds between sending heartbeat packets.
     */
    public void setHeartbeatFrequency(int frequency) {
        setCacheValue("heartbeatFrequency", frequency);
    }

    /**
     * Sets whether to index all data locally, in addition to forwarding it.
     *
     * This is known as an "index-and-forward" configuration. This attribute is
     * only available for heavy forwarders. It is available only at the top
     * level [tcpout] stanza in outputs.conf. It cannot be overridden in a
     * target group.
     *
     * @param indexAndForward whether to index and foward or, just forward.
     */
    public void setIndexAndForward(boolean indexAndForward) {
        setCacheValue("indexAndForward", indexAndForward);
    }

    /**
     * Sets the maximum size of the forwarder's output queue. It also has the
     * side effect of setting the maximum size of the wait queue to 3x this
     * value, if you have enabled indexer acknowledgment {@code useACK=true}.
     *
     * Values maybe in {@code number} or {@code number} followed by
     * {@code MB, GB}.
     *
     * Although the wait queue and the output queues are both configured by
     * this attribute, they are separate queues. The setting determines the
     * maximum size of the queue's in-memory (RAM) buffer.
     *
     * For heavy forwarders sending parsed data, maxQueueSize is the maximum
     * number of events. Since events are typically much shorter than data
     * blocks, the memory consumed by the queue on a parsing forwarder will
     * likely be much smaller than on a non-parsing forwarder, if you use this
     * version of the setting.
     *
     * If specified as a lone integer (for example, maxQueueSize=100),
     * maxQueueSize indicates the maximum number of queued events
     * (for parsed data) or blocks of data (for unparsed data). A block of data
     * is approximately 64KB. For non-parsing forwarders, such as universal
     * forwarders, that send unparsed data, maxQueueSize is the maximum number
     * of data blocks.
     *
     * If specified as an integer followed by KB, MB, or GB (for example,
     * maxQueueSize=100MB), maxQueueSize indicates the maximum RAM allocated
     * to the queue buffer. Defaults to 500KB (which means a maximum size of
     * 500KB for the output queue and 1500KB for the wait queue, if any).
     *
     * @param maxQueueSize The maximum queue size.
     */
    public void setMaxQueueSize(String maxQueueSize) {
        setCacheValue("maxQueueSize", maxQueueSize);
    }

    /**
     * Sets whether or not to forward cooked data. {@code true} indicates events
     * have been processed by splunk. {@code false} indicates events are raw and
     * untouched prior to sending.
     *
     * @param sendCookedData whether to send cooked or raw data events.
     */
    public void setSendCookedData(boolean sendCookedData) {
        setCacheValue("sendCookedData", sendCookedData);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update() {
        if (!isUpdateKeyPresent("name")) {
            setCacheValue("name", "tcpout"); // requires name
        }
        super.update();
    }
}
