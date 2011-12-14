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
 * Representation of the TCP cooked input subclass. This differs from TCP, as
 * the data indexed is processed and not in raw form.
 */
public class TcpSplunkInput extends Input {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The TCP cooked input endpoint.
     */
    TcpSplunkInput(Service service, String path) {
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
     * Returns this TCP cooked input's group.
     *
     * @return This TCP cooked input's group.
     */
    public String getGroup() {
        return getString("group");
    }

    /**
     * Returns this TCP cooked input's source host, where this indexer gets its
     * data. Null is returned if it is not specified.
     *
     * @return This TCP cooked input's source host.
     */
    public String getHost() {
        return getString("host");
    }

    /**
     * Returns this TCP cooked input's index name, or null if not specified.
     *
     * @return This TCP cooked input's index name.
     */
    public String getIndex() {
        return getString("index");
    }

    /**
     * Returns the TCP cooked input kind.
     *
     * @return The TCP cooked input kind.
     */
    public InputKind getKind() {
        return InputKind.TcpSplunk;
    }

    /**
     * Returns this TCP cooked input's queue, or null if not specified. Valid
     * values are from the set parsingQueue, indexQueue.
     *
     * @return This TCP cooked input's queue.
     */
    public String getQueue() {
        return getString("queue", null);
    }

    /**
     * Returns this TCP cooked input's _rcvbuf attribute.
     *
     * @return This TCP cooked input's _rcvbuf attribute.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }

    /**
     * Returns this TCP cooked input's incoming host restriction, or null if not
     * specified.
     *
     * @return This TCP cooked input's incoming host restriction.
     */
    public String getRestrictToHost() {
        return getString("restrictToHost", null);
    }

    /**
     * Returns this TCP cooked input's initial source key, or null if not
     * specified. This is normally the input file path.
     *
     * @return This TCP cooked input's source.
     */
    public String getSource() {
        return getString("source", null);
    }

    /**
     * Returns this TCP cooked input's event source type, or null if not
     * specified.
     *
     * @return this TCP cooked input's event source type.
     */
    public String getSourceType() {
        return getString("sourceType", null);
    }

    /**
     * Returns Whether or not this TCP cooked input is using SSL.
     *
     * @return Whether or not this TCP cooked input is using SSL.
     */
    public boolean getSSL() {
        return getBoolean("SSL", false);
    }
}
