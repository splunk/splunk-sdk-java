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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * The {@code UdpInput} class represents a UDP data input.
 */
public class UdpInput extends PortInput {

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
     * Returns an object that contains the inbound UDP connections.
     *
     * @return The UDP connections object.
     */
    public UdpConnections connections() {
        return new UdpConnections(service, path + "/connections");
    }

    /**
     * Returns the style of host connection. Valid values are: "ip", "dns", and
     * "none".
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
     * Returns the input kind for this input.
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.Udp;
    }

    /**
     * Returns the queue for this UDP input. Valid values are:
     * "parsingQueue" and "indexQueue".
     *
     * @return The queue, or {@code null} if not specified.
     */
    public String getQueue() {
        return getString("queue", null);
    }

    /**
     * Returns the value of the {@code _rcvbuf} attribute for this 
     * UDP input.
     *
     * @return The {@code _rcvbuf} value.
     * @deprecated This is not used anymore. No replacement.
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
     * Indicates whether Splunk prepends a timestamp and host name to
     * incoming events.
     *
     * @return {@code true} if Splunk does not prepend a timestamp and
     * host name to incoming events, {@code false} if it does.
     */
    public boolean getNoAppendingTimeStamp() {
        return getBoolean("no_appending_timestamp", false);
    }

    /**
     * Indicates whether Splunk removes the <b>priority</b> field from incoming
     * events. 
     *
     * @return {@code true} if Splunk does not remove the <b>priority</b> field 
     * from incoming syslog events, {@code false} if it does.
     */
    public boolean getNoPriorityStripping() {
        return getBoolean("no_priority_stripping", false);
    }

    /**
     * Sets the value of the <b>from-host</b> field for the remote server that 
     * is sending data.
     * Valid values are: <ul>
     * <li>"ip": Sets the host to the IP address of the remote server sending 
     * data.</li>
     * <li>"dns": Sets the host to the reverse DNS entry for the IP address of 
     * the remote server sending data.</li>
     * <li>"none": Leaves the host as specified in inputs.conf, which is 
     * typically the Splunk system host name.</li></ul>
     *
     * @param connection_host The connection host information.
     */
    public void setConnectionHost(String connection_host) {
        setCacheValue("connection_host", connection_host);
    }

    /**
     * Sets the host from which the indexer gets data.
     *
     * @param host The host.
     */
    public void setHost(String host) {
        setCacheValue("host", host);
    }

    /**
     * Sets the index in which to store all generated events.
     *
     * @param index The index.
     */
    public void setIndex(String index) {
        setCacheValue("index", index);
    }

    /**
     * Sets whether Splunk should prepend a timestamp and host name to incoming
     * events.
     *
     * @param no_appending_timestamp {@code true} to <i>not</i> prepend a 
     * timestamp and host name to incoming events, {@code false} to prepend that
     * information.
     */
    public void setNoAppendingTimeStamp(boolean no_appending_timestamp) {
        setCacheValue("no_appending_timestamp", no_appending_timestamp);
    }

    /**
     * Sets whether Splunk should strip the <b>priority</b> field from incoming
     * events.
     *
     * @param no_priority_stripping {@code true} to <i>not</i> strip the 
     * <b>priority</b> field, {@code false} to remove it.
     */
    public void setNoPriorityStripping(boolean no_priority_stripping) {
        setCacheValue("no_priority_stripping", no_priority_stripping);
    }

    /**
     * Sets how the input processor should deposit the events it reads. Valid 
     * values are:<ul>
     * <li>"parsingQueue": Applies props.conf and other parsing rules to 
     * your data.</li>
     * <li>"indexQueue": Sends your data directly into the index.</li>
     * </ul>
     *
     * @param queue The queue-processing type.
     */
    public void setQueue(String queue) {
        setCacheValue("queue", queue);
    }

    /**
     * Sets the initial value for the source key for events from this 
     * input. The source key is used during parsing and indexing. The 
     * <b>source</b> field is used for searches. As a convenience, the source 
     * string is prepended with "source::".
     * <p>
     * <b>Note:</b> Overriding the source key is generally not recommended. 
     * Typically, the input layer provides a more accurate string to aid in 
     * problem analysis and investigation, accurately recording the file from 
     * which the data was retrieved. Consider the use of source types, tagging, 
     * and search wildcards before overriding this value.
     *
     * @param source The source.
     */
    public void setSource(String source) {
        setCacheValue("source", source);
    }

    /**
     * Sets the source type for events from this input.
     *
     * @param sourcetype The source type.
     */
    public void setSourceType(String sourcetype) {
        setCacheValue("sourcetype", sourcetype);
    }

    /**
     * Send a string to this UDP input.
     *
     * @param eventBody The text to send.
     */
    public void submit(String eventBody) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(this.service.getHost());
        int port = this.getPort();
        byte[] buffer = eventBody.getBytes("UTF-8");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
        socket.close();
    }
}
