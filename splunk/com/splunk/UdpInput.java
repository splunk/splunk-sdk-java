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
 * Representation of a UDP input.
 */
public class UdpInput extends Input {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The UDP input endpoint.
     */
    UdpInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the style of host connection, or null if not specified. Valid
     * values are from the set ip, dns, and none.
     *
     * @return The style of host connection.
     */
    public String getConnectionHost() {
        return getString("connection_host", null);
    }

    /**
     * Returns this UDP input's group.
     *
     * @return This UDP input's group.
     */
    public String getGroup() {
        return getString("group");
    }

    /**
     * Returns this UDP input's source host, where this indexer gets its data.
     * Null is returned if it is not specified.
     *
     * @return This UDP input's source host.
     */
    public String getHost() {
        return getString("host");
    }

    /**
     * Returns this UDP input's index name, or null if not specified.
     *
     * @return This UDP input's index name.
     */
    public String getIndex() {
        return getString("index");
    }

    /**
     * Returns the UDP input kind.
     *
     * @return The UDP input kind.
     */
    public InputKind getKind() {
        return InputKind.Udp;
    }

    /**
     * Returns this UDP input's queue, or null if not specified. Valid values
     * are from the set parsingQueue, indexQueue.
     *
     * @return This UDP input's queue.
     */
    public String getQueue() {
        return getString("queue", null);
    }

    /**
     * Returns this UDP input's _rcvbuf attribute.
     *
     * @return This UDP input's _rcvbuf attribute.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }

    /**
     * Returns this UDP input's initial source key, or null if not specified.
     * This is normally the input file path.
     *
     * @return This UDP input's source.
     */
    public String getSource() {
        return getString("source", null);
    }

    /**
     * Returns this UDP input's event source type, or null if not specified.
     *
     * @return this UDP input's event source type.
     */
    public String getSourceType() {
        return getString("sourceType", null);
    }

    /**
     * Returns whether or not Splunk prepends a timestamp and hostname to
     * incoming events.
     *
     * @return Whether or not Splunk prepends a timestamp and hostname to
     * incoming events.
     */
    public boolean noAppendingTimeStamp() {
        return getBoolean("no_appending_timestamp", false);
    }

    /**
     * Returns whether or not Splunk removes the priority field to incoming
     * events.
     *
     * @return Whether or not Splunk removes the priority field to incoming
     * events.
     */
    public boolean noPriorityStripping() {
        return getBoolean("no_priority_stripping", false);
    }
}
