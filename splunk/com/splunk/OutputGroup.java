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
 * The {@code OutputGroup} class represents an output group, providing
 * access to the configuration of a group of one or more data-forwarding
 * destinations.
 */
public class OutputGroup extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The output group endpoint.
     */
    OutputGroup(Service service, String path) {
        super(service, path);
    }

    /**
     * Indicates whether this forwarder performs automatic load balancing.
     *
     * @return {@code true} if the forwarder performs automatic load balancing, 
     * {@code false} if not.
     */
    public boolean getAutoLB() {
        return getBoolean("autoLB", false);
    }

    /**
     * Returns the type of output processor for this forwarder group. 
     * Valid values are: "tcpout", "syslog", and "httpout".
     *
     * @return The output processor type, or {@code null} if not specified.
     */
    public String getMethod() {
        return getString("method", null);
    }

    /**
     * Returns the list of servers for this forwarder group.
     *
     * @return The server list.
     */
    public String[] getServers() {
        return getString("servers").split(",");
    }

    /**
     *  Sets the list of servers included in this group.
     *
     * @param servers A comma-separated list of servers.
     */
    public void setServers(String servers) {
        setCacheValue("servers", servers);
    }

    /**
     * Sets whether this forwarder performs automatic load balancing.
     *
     * If set to {@code true}, the forwarder performs automatic load balancing.
     * In automatic mode, the forwarder selects a new indexer every
     * {@code autoLBFrequency} seconds. If the connection to the current
     * indexer is lost, the forwarder selects a new live indexer to forward
     * data to.
     *
     * Do not change the default setting, unless you have some overriding need
     * to use round-robin load balancing. Round-robin load balancing
     * ({@code autoLB} is {@code false}) was previously the default 
     * load-balancing method. Starting with release 4.2, however, round-robin 
     * load balancing has been deprecated, and the default has been changed to 
     * automatic load balancing ({@code autoLB} is {@code true}).
     * @see #getAutoLB
     * @see OutputDefault#getAutoLBFrequency
     *
     * @param autoLB {@code true} to perform automatic load balancing on this 
     * forwarder, {@code false} if not.
     */
    public void setAutoLB(boolean autoLB) {
        setCacheValue("autoLB", autoLB);
    }

    /**
     * Sets whether this forwarder sends compressed data. The receiver port must
     * also have compression enabled for this to work.
     *
     * @param compressed {@code true} for this forwarder to send compressed 
     * data, {@code false} if not.
     */
    public void setCompressed(boolean compressed) {
        setCacheValue("compressed", compressed);
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
     * @see OutputDefault#getSendCookedData
     *
     * @param frequency The frequency, in seconds. 
     */
    public void setHeartbeatFrequency(int frequency) {
        setCacheValue("heartbeatFrequency", frequency);
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
     * Sets the type of output processor. Valid values are "tcpout" and
     * "syslog".
     *
     * @param method The output processor type.
     */
    public void setMethod(String method) {
        setCacheValue("method", method);
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
        if (!args.containsKey("servers")) {
            args = Args.create(args).add("servers", getString("servers"));
        }
        super.update(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update() {
        // If not present in the update keys, add required attribute as long
        // as one pre-existing update pair exists
        if (toUpdate.size() > 0 && !toUpdate.containsKey("servers")) {
            setCacheValue("servers", getString("servers"));
        }
        super.update();
    }
}
