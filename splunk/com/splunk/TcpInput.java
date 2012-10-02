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
import java.io.OutputStream;
import java.net.Socket;

/**
 * The {@code TcpInput} class represents a TCP raw input. This differs from a
 * TCP <i>cooked</i> input in that this TCP input is in raw form, and is not
 * processed (or "cooked").
 */
public class TcpInput extends Input {
    public static interface ReceiverBehavior { public void run(OutputStream stream) throws IOException; }

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The TCP raw input endpoint.
     */
    TcpInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Return a socket attached to this TCP raw input.
     */
    public Socket attach() throws IOException {
        String hostname = this.service.getHost();
        int port = java.lang.Integer.parseInt(this.getName());
        Socket socket = new Socket(hostname, port);
        return socket;
    }

    /**
     * Submit events to this TCP input, reusing the connection.
     *
     * attachWith passes a {@code OutputStream} connected to the TCP input
     * to a {@code TcpInput.ReceiverBehavior}'s {@code run} method, and handles all
     * the set up and tear down of the socket.
     *
     * Example:
     *
     *     Service service = Service.connect(...);
     *     TcpInput input = service.getInputs().get('10000', InputKind.Tcp);
     *     input.attachWith(new TcpInput.TcpInputReceiverBehavior() {
     *         public void run(OutputStream stream) {
     *             stream.print(getTimestamp() + " Boris the mad baboon!\r\n");
     *         }
     *     });
     */
    public void attachWith(ReceiverBehavior behavior) throws IOException {
        Socket socket = null;
        OutputStream output = null;
        try {
            socket = attach();
            output = socket.getOutputStream();
            behavior.run(output);
            output.flush();
        } finally {
            if (output != null) { output.close(); }
            if (socket != null) { socket.close(); }
        }
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
     * Returns the style of host connection. Valid values are: "ip", "dns", and
     * "none".
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
     * Returns the input kind of this TCP input.
     * @see InputKind
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.Tcp;
    }

    /**
     * Returns the queue for this TCP input. Valid values are:
     * "parsingQueue" and "indexQueue".
     *
     * @return The queue, or {@code null} if not specified.
     */
    public String getQueue() {
        return getString("queue", null);
    }

    /**
     * @deprecated Returns the value of the {@code _rcvbuf} attribute for this 
     * TCP input.
     *
     * @return The {@code _rcvbuf} value.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }

    /**
     * Returns the incoming host restriction for this TCP input. When specified, 
     * this input only accepts data from the specified host. 
     *
     * @return The incoming host restriction, or {@code null} if not specified.
     */
    public String getRestrictToHost() {
        return getString("restrictToHost", null);
    }

    /**
     * Returns the initial source key for this TCP input. Typically this value 
     * is the input file path.
     *
     * @return The source key, or {@code null} if not specified.
     */
    public String getSource() {
        return getString("source", null);
    }

    /**
     * Returns the source type for events from this TCP input.
     *
     * @return The source type, or {@code null} if not specified.
     */
    public String getSourceType() {
        return getString("sourcetype", null);
    }

    /**
     * Indicates whether this TCP input is using secure socket layer (SSL).
     *
     * @return {@code true} if this TCP input is using SSL, {@code false} if
     * not.
     */
    public boolean getSSL() {
        return getBoolean("SSL", false);
    }

    /**
     * Sets whether to use secure socket layer (SSL).
     *
     * @param SSL {@code true} to use SSL, {@code false} if not.
     */
    public void setSSL(boolean SSL) {
        setCacheValue("SSL", SSL);
    }

    /**
     * Sets the {@code from-host} for the remote server that is sending data.
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
     * Sets whether this input is enabled or disabled.
     * You can also do this using the {@code Entity.disable} and 
     * {@code Entity.enable} methods. 
     * @see Entity#disable
     * @see Entity#enable
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
     * @param host The host name.
     */
    public void setHost(String host) {
        setCacheValue("host", host);
    }

    /**
     * Sets the index in which to store all generated events.
     *
     * @param index The index name.
     */
    public void setIndex(String index) {
        setCacheValue("index", index);
    }

    /**
     * Submit a single event to this input.
     *
     * Opens a connection, submits, and closes the connection. If you need to
     * submit many events, use attachWith, which will open a single connection.
     *
     * @param eventBody String containing the event to submit.
     */
    public void submit(String eventBody) throws IOException {
        Socket socket = null;
        OutputStream output = null;
        try {
            socket = attach();
            output = socket.getOutputStream();
            output.write(eventBody.getBytes("UTF8"));
            output.flush();
            output.close();
            socket.close();
        } finally {
            if (output != null) { output.close(); }
            if (socket != null) { socket.close(); }
        }
    }

    /**
     * Sets how the input processor should deposit the events it reads. Valid 
     * values are:<ul>
     * <li>{@code parsingQueue}: Applies props.conf and other parsing rules to 
     * your data.</li>
     * <li>{@code indexQueue}: Sends your data directly into the index.</li>
     * </ul>
     *
     * @param queue The queue-processing type.
     */
    public void setQueue(String queue) {
        setCacheValue("queue", queue);
    }

    /**
     * Sets the timeout value for adding a Done key. 
     *
     * If a connection over the input port specified by {@code name} remains 
     * idle after receiving data for this specified number of seconds, it adds 
     * a Done key, implying that the last event has been completely received.
     *
     * @param rawTcpDoneTimeout The timeout value, in seconds.
     */
    public void setRawTcpDoneTimeout(int rawTcpDoneTimeout) {
        setCacheValue("rawTcpDoneTimeout", rawTcpDoneTimeout);
    }

    /**
     * Sets a restriction to accept inputs from the specified host only.
     *
     * @param restrictToHost The host.
     */
    public void setRestrictToHost(String restrictToHost) {
        setCacheValue("restrictToHost", restrictToHost);
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
}
