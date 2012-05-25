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
 * The {@code TcpInput} class represents a TCP raw input. This differs from a
 * TCP <i>cooked</i> input in that this TCP input is in raw form, and is not
 * processed (or "cooked").
 */
public class TcpInput extends Input {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The TCP input endpoint.
     */
    TcpInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns an object that contains the inbound raw TCP connections.
     *
     * @return The TCP connections object.
     */
    public TcpConnections connections() {
        return new TcpConnections(service, path + "/connections");
    }

    /**
     * Returns the style of host connection. Valid values are: {@code ip},
     * {@code dns}, and {@code none}.
     *
     * @return The style of host connection, or {@code null} if not specified.
     */
    public String getConnectionHost() {
        return getString("connection_host", null);
    }

    /**
     * Returns the group of this TCP input.
     *
     * @return The group.
     */
    public String getGroup() {
        return getString("group");
    }

    /**
     * Returns the source host of this TCP input where this indexer gets its
     * data.
     *
     * @return The source host, or {@code null} if not specified.
     */
    public String getHost() {
        return getString("host", null);
    }

    /**
     * Returns the index name of this TCP input.
     *
     * @return The index name, or {@code null} if not specified.
     */
    public String getIndex() {
        return getString("index", null);
    }

    /**
     * Returns the input type of this TCP input.
     * @see InputKind
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.Tcp;
    }

    /**
     * Returns the queue for this TCP input. Valid values are:
     * {@code parsingQueue} and {@code indexQueue}.
     *
     * @return The queue, or {@code null} if not specified.
     */
    public String getQueue() {
        return getString("queue", null);
    }

    /**
     * @deprecated Returns the value of the {@code _rcvbuf} attribute for this TCP input.
     *
     * @return The {@code _rcvbuf} value.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }

    /**
     * Returns the incoming host restriction for this TCP input. When specified, 
     * this inputonly accepts data from the specified host. 
     *
     * @return The incoming host restriction, or {@code null} if not specified.
     */
    public String getRestrictToHost() {
        return getString("restrictToHost", null);
    }

    /**
     * Returns the initial source key for this TCP input.
     * Typically this value is the input file path.
     *
     * @return The source key, or {@code null} if not specified.
     */
    public String getSource() {
        return getString("source", null);
    }

    /**
     * Returns the event source type for this TCP input.
     *
     * @return The event source type, or {@code null} if not specified.
     */
    public String getSourceType() {
        return getString("sourcetype", null);
    }

    /**
     * Indicates whether this TCP input is using SSL.
     *
     * @return {@code true} if this TCP input is using SSL, {@code false} if
     * not.
     */
    public boolean getSSL() {
        return getBoolean("SSL", false);
    }

    /**
     * Sets Whether or not SSL is used.
     *
     * @param SSL Whether or not SSL is used.
     */
    public void setSSL(boolean SSL) {
        setCacheValue("SSL", SSL);
    }

    /**
     * Sets the {@code from-host} for the remote server that is sending data.
     * Valid values are {@code ip, dns} or {@code none}.
     *
     * {@code ip} sets the host to the IP address of the remote server sending
     * data. {@code dns} sets the host to the reverse DNS entry for the IP
     * address of the remote server sending data.
     *
     * {@code none} leaves the host as specified in inputs.conf, which is
     * typically the Splunk system hostname.
     *
     * @param connection_host How to set the from-host information.
     */
    public void setConnectionHost(String connection_host) {
        setCacheValue("connection_host", connection_host);
    }

    /**
     * Sets whether this input is enabled or disabled. Note that the
     * supported disabled mechanism, is to use the @{code disable} action.
     *
     * @param disabled {@code true} to disabled to script input,
     * {@code false} to enable.
     */
    public void setDisabled(boolean disabled) {
        setCacheValue("disabled", disabled);
    }

    /**
     * Sets the host from which the indexer gets data.
     *
     * @param host The host from which the indexer gets data.
     */
    public void setHost(String host) {
        setCacheValue("host", host);
    }

    /**
     * Sets index in which to store all generated events.
     *
     * @param index The index in which to store all generated events.
     */
    public void setIndex(String index) {
        setCacheValue("index", index);
    }

    /**
     * Sets where the input processor should deposit the events it reads.
     * Valid values are {code parsingQueue} or {@code indexQueue}.
     *
     * Defaults to {@code parsingQueue}.
     *
     * Set queue to parsingQueue to apply props.conf and other parsing rules
     * to your data. For more information about props.conf and rules for
     * timestamping and linebreaking.
     *
     * Set queue to {@code indexQueue} to send your data directly into the
     * index.
     *
     * @param queue The queue processing type.
     */
    public void setQueue(String queue) {
        setCacheValue("queue", queue);
    }

    /**
     * Sets the timeout value for adding a Done-key, in seconds. The default
     * value is 10 seconds.
     *
     *  If a connection over the port specified by name remains idle after
     *  receiving data for specified number of seconds, it adds a Done-key. This
     *  implies the last event has been completely received. Introduced in
     *  4.3.
     *
     * @param rawTcpDoneTimeout timeout value for adding a Done-key.
     */
    public void setRawTcpDoneTimeout(int rawTcpDoneTimeout) {
        setCacheValue("rawTcpDoneTimeout", rawTcpDoneTimeout);
    }

    /**
     * Sets a restriction to accept inputs from only this host.
     *
     * @param restrictToHost Restrict to accept inputs only from this host.
     */
    public void setRestrictToHost(String restrictToHost) {
        setCacheValue("restrictToHost", restrictToHost);
    }

    /**
     * Sets the source key/field for events from this input. Defaults to the
     * input file path.
     *
     * Sets the source key's initial value. The key is used during
     * parsing/indexing, in particular to set the source field during indexing.
     * It is also the source field used at search time. As a convenience,
     * the chosen string is prepended with 'source::'.
     *
     * Note: Overriding the source key is generally not recommended. Typically,
     * the input layer provides a more accurate string to aid in problem
     * analysis and investigation, accurately recording the file from which
     * the data was retreived. Consider use of source types, tagging, and search
     * wildcards before overriding this value.
     *
     * @param source the source key/field for events from this input.
     */
    public void setSource(String source) {
        setCacheValue("source", source);
    }

    /**
     * Sets the source type for events from this input.
     *
     * @param sourcetype the for events from this input.
     */
    public void setSourceType(String sourcetype) {
        setCacheValue("sourcetype", sourcetype);
    }
}
