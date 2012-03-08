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
 * The {@code UdpInput} class represents a UDP input.
 */
public class UdpInput extends Input {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The UDP input endpoint.
     */
    UdpInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the style of host connection. Valid values are: {@code ip}, {@code dns}, and {@code none}.
     *
     * @return The style of host connection, or {@code null} if not specified.
     */
    public String getConnectionHost() {
        return getString("connection_host", null);
    }

    /**
     * Returns the group for this UDP input.
     *
     * @return The group.
     */
    public String getGroup() {
        return getString("group");
    }

    /**
     * Returns the source host for this UDP input, where this indexer gets its data.
     *
     * @return The source host, or {@code null} if not specified.
     */
    public String getHost() {
        return getString("host");
    }

    /**
     * Returns the index name for this UDP input.
     *
     * @return The index name.
     */
    public String getIndex() {
        return getString("index");
    }

    /**
     * Returns the input type for this UDP input.
     * @see InputKind
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.Udp;
    }

    /**
     * Returns the queue for this UDP input. Valid values are: {@code parsingQueue} and {@code indexQueue}.
     *
     * @return The queue, or {@code null} if not specified.
     */
    public String getQueue() {
        return getString("queue", null);
    }

    /**
     * Returns the value of the {@code _rcvbuf} attribute for this UDP input.
     *
     * @return The {@code _rcvbuf} value.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }

    /**
     * Returns the initial source key for this UDP input.
     * Typically, this value is the input file path.
     *
     * @return The source, or {@code null} if not specified.
     */
    public String getSource() {
        return getString("source", null);
    }

    /**
     * Returns the event source type for this UDP input.
     *
     * @return The event source type, or {@code null} if not specified.
     */
    public String getSourceType() {
        return getString("sourceType", null);
    }

    /**
     * Indicates whether Splunk prepends a timestamp and hostname to
     * incoming events.
     *
     * @return {@code true} if Splunk does <i>not</i> prepend a timestamp and hostname to
     * incoming events, {@code false} if it does.
     */
    public boolean noAppendingTimeStamp() {
        return getBoolean("no_appending_timestamp", false);
    }

    /**
     * Indicates whether Splunk removes the priority field from incoming
     * events. 
     *
     * @return {@code true} if Splunk does <i>not</i> remove the priority field 
     * from incoming syslog events, {@code false} if it does.
     */
    public boolean noPriorityStripping() {
        return getBoolean("no_priority_stripping", false);
    }
}
