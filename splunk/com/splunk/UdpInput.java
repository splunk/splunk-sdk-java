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
     * Returns an object that contains the inbound raw TCP connections.
     *
     * @return The TCP connections object.
     */
    public UdpConnections connections() {
        return new UdpConnections(service, path + "/connections");
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
     * Returns the group for this UDP input.
     *
     * @return The group.
     */
    public String getGroup() {
        return getString("group");
    }

    /**
     * Returns the source host for this UDP input, where this indexer gets its
     * data.
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
     * Returns the queue for this UDP input. Valid values are:
     * {@code parsingQueue} and {@code indexQueue}.
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
        return getString("sourcetype", null);
    }

    /**
     * Indicates whether Splunk prepends a timestamp and hostname to
     * incoming events.
     *
     * @return {@code true} if Splunk does <i>not</i> prepend a timestamp and
     * hostname to incoming events, {@code false} if it does.
     */
    public boolean getNoAppendingTimeStamp() {
        return getBoolean("no_appending_timestamp", false);
    }

    /**
     * Indicates whether Splunk removes the priority field from incoming
     * events. 
     *
     * @return {@code true} if Splunk does <i>not</i> remove the priority field 
     * from incoming syslog events, {@code false} if it does.
     */
    public boolean getNoPriorityStripping() {
        return getBoolean("no_priority_stripping", false);
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
     * Sets whether or not splunk prepends a timestamp and hostname to incoming
     * events.
     *
     * @param no_appending_timestamp whether or not splunk prepends a timestamp
     * and hostname to incoming events.
     */
    public void setNoAppendingTimeStamp(boolean no_appending_timestamp) {
        setCacheValue("no_appending_timestamp", no_appending_timestamp);
    }

    /**
     * Sets whether or not splunk strips the priority field from incoming
     * events.
     *
     * @param no_priority_stripping whether or not splunk strips the priority
     * field from incoming events.
     */
    public void setNoPriorityStripping(boolean no_priority_stripping) {
        setCacheValue("no_priority_stripping", no_priority_stripping);
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
