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
     * Returns the type of output processor for this forwarder group. 
     * Valid values are: tcpout, syslog, and httpout.
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
        return getStringArray("servers");
    }

    /**
     *  Sets the list of servers included in this group.
     *
     * @param servers A comma separated list of servers.
     */
    public void setServers(String servers) {
        setCacheValue("servers", servers);
    }

    /**
     * Sets whether or not this forwarder performs automatic load balancing.
     *
     * If set to {@codetrue}, forwarder performs automatic load balancing.
     * In automatic mode, the forwarder selects a new indexer every
     * {@code autoLBFrequency} seconds. If the connection to the current
     * indexer is lost, the forwarder selects a new live indexer to forward
     * data to.
     *
     * Do not alter the default setting, unless you have some overriding need
     * to use round-robin load balancing. Round-robin load balancing
     * (autoLB=false) was previously the default load balancing method.
     * Starting with release 4.2, however, round-robin load balancing has
     * been deprecated, and the default has been changed to automatic load
     * balancing (autoLB=true).
     *
     * @param autoLB whether or not auto load balancing is set for this
     * forwarder.
     */
    public void setAutoLB(boolean autoLB) {
        setCacheValue("autoLB", autoLB);
    }

    /**
     * Sets whether or not this forwarder sends compressed data. If set to
     * {@code true}, the receiver port must also have compression turned on.
     *
     * @param compressed whether or not this forwarder compresses data.
     */
    public void setCompressed(boolean compressed) {
        setCacheValue("compressed", compressed);
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
     * Sets the type of output processor. Valid values are, {@code tcpout} and
     * {@code syslog}.
     *
     * @param method whether to send cooked or raw data events.
     */
    public void setMethod(String method) {
        setCacheValue("method", method);
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

    @Override public void update() {
        if (!isUpdateKeyPresent("servers")) {
            setCacheValue("servers", getServers()); // requires servers
        }
        super.update();
    }
}
