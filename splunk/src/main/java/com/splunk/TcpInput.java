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
 * The {@code TcpInput} class represents a raw TCP data input. This differs from
 * a <i>cooked</i> TCP input in that this TCP input is in raw form, and is not
 * processed (or "cooked").
 */
public class TcpInput extends PortInput {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The raw TCP input endpoint.
     */
    TcpInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns a socket attached to this raw TCP input.
     */
    public Socket attach() throws IOException {
        return new Socket(this.service.getHost(), this.getPort());
    }

    /**
     * Submits events to this raw TCP input, reusing the connection.
     *
     * This method passes an output stream connected to the index to the 
     * {@code run} method of the {@code ReceiverBehavior} object, then handles 
     * setting up and tearing down the socket.
     * For an example of how to use this method, see 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ2" target="_blank">How to 
     * get data into Splunk</a> on 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ2" 
     * target="_blank">dev.splunk.com</a>. 
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
     * Returns the group of this raw TCP input.
     *
     * @return The group.
     */
    public String getGroup() {
        return getString("group", null);
    }

    /**
     * Returns the source host of this raw TCP input where this indexer gets its
     * data.
     *
     * @return The source host, or {@code null} if not specified.
     */
    public String getHost() {
        return getString("host", null);
    }

    /**
     * Returns the index name of this raw TCP input.
     *
     * @return The index name, or {@code null} if not specified.
     */
    public String getIndex() {
        return getString("index", null);
    }

    /**
     * Returns the input kind of this input. 
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.Tcp;
    }

    /**
     * Returns the queue for this raw TCP input. Valid values are:
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
     * Returns the incoming host restriction for this raw TCP input. When 
     * specified, this input only accepts data from the specified host. 
     *
     * @return The incoming host restriction, or {@code null} if not specified.
     */
    public String getRestrictToHost() {
        return getString("restrictToHost", null);
    }

    /**
     * Returns the initial source key for this raw TCP input. Typically this 
     * value is the input file path.
     *
     * @return The source key, or {@code null} if not specified.
     */
    public String getSource() {
        return getString("source", null);
    }

    /**
     * Returns the source type for events from this raw TCP input.
     *
     * @return The source type, or {@code null} if not specified.
     */
    public String getSourceType() {
        return getString("sourcetype", null);
    }

    /**
     * Indicates whether this raw TCP input is using secure socket layer (SSL).
     *
     * @return {@code true} if this input is using SSL, {@code false} if not.
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
     * Sets the value for the <b>from-host</b> field for the remote server that
     * is sending data. Valid values are: <ul>
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
     * <p>
     * <b>Note:</b> Using this method requires you to restart Splunk before this
     * setting takes effect. To avoid restarting Splunk, use the 
     * {@code Entity.disable} and {@code Entity.enable} methods instead, which 
     * take effect immediately. 
     *
     * @param disabled {@code true} to disable this input, {@code false} to 
     * enable it.
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
     * Submit a single event to this raw TCP input by opening the connection, 
     * submitting the event, and closing the connection. To submit multiple 
     * events, use {@code attachWith} to open a single connection.
     * @see #attachWith
     *
     * @param eventBody A string that contains the event.
     */
    public void submit(String eventBody) throws IOException {
        Socket socket = null;
        OutputStream output = null;
        try {
            socket = attach();
            output = socket.getOutputStream();
            output.write(eventBody.getBytes("UTF-8"));
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
     * <li>"parsingQueue": Applies props.conf and other parsing rules to your 
     * data.</li>
     * <li>"indexQueue": Sends your data directly into the index.</li></ul>
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
     * Sets the source type for events from this raw TCP input.
     *
     * @param sourcetype The source type.
     */
    public void setSourceType(String sourcetype) {
        setCacheValue("sourcetype", sourcetype);
    }
}
