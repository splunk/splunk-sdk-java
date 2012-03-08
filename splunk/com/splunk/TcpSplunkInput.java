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
 * The {@code TcpSplunkInput} class represents a TCP Splunk-processed "cooked" 
 * input. This differs from a TCP raw input in that this TCP cooked data is 
 * processed by Splunk and is not in raw form.
 */
public class TcpSplunkInput extends Input {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The TCP cooked input endpoint.
     */
    TcpSplunkInput(Service service, String path) {
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
     * Returns the group for this TCP cooked input.
     *
     * @return The group.
     */
    public String getGroup() {
        return getString("group");
    }

    /**
     * Returns the source host for this TCP cooked input where this indexer gets its
     * data. 
     *
     * @return The source host, or {@code null} if not specified.
     */
    public String getHost() {
        return getString("host");
    }

    /**
     * Returns the index name for this TCP cooked input.
     *
     * @return The index name, or {@code null} if not specified.
     */
    public String getIndex() {
        return getString("index");
    }

    /**
     * Returns the input type for this TCP cooked input.
     * @see InputKind
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.TcpSplunk;
    }

    /**
     * Returns the queue for this TCP cooked input. Valid
     * values are: {@code parsingQueue} and {@code indexQueue}.
     *
     * @return The queue, or {@code null} if not specified.
     */
    public String getQueue() {
        return getString("queue", null);
    }

    /**
     * Returns the value of the {@code _rcvbuf} attribute for this TCP cooked input.
     *
     * @return The {@code _rcvbuf} value.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }

    /**
     * Returns the incoming host restriction for this TCP cooked input.
     *
     * @return The incoming host restriction, or {@code null} if not specified.
     */
    public String getRestrictToHost() {
        return getString("restrictToHost", null);
    }

    /**
     * Returns the initial source key for this TCP cooked input. Typically this 
     * value is the input file path.
     *
     * @return The source, or {@code null} if not specified.
     */
    public String getSource() {
        return getString("source", null);
    }

    /**
     * Returns the event source type for this TCP cooked input.
     *
     * @return The event source type, or {@code null} if not specified.
     */
    public String getSourceType() {
        return getString("sourceType", null);
    }

    /**
     * Indicates whether this TCP cooked input is using SSL.
     *
     * @return {@code true} if this TCP cooked input is using SSL, {@code false} if not.
     */
    public boolean getSSL() {
        return getBoolean("SSL", false);
    }
}
