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
public class TcpSplunkInput extends PortInput {

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
     * Returns an object that contains the inbound cooked TCP connections.
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
     * Returns the group for this TCP cooked input.
     *
     * @return The group.
     */
    public String getGroup() {
        return getString("group");
    }

    /**
     * Returns the source host for this TCP cooked input where this indexer
     * gets its data.
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
     * Returns the input kind for this TCP cooked input.
     * @see InputKind
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.TcpSplunk;
    }

    /**
     * Returns the queue for this TCP cooked input. Valid values are:
     * "parsingQueue" and "indexQueue".
     *
     * @return The queue, or {@code null} if not specified.
     */
    public String getQueue() {
        return getString("queue", null);
    }

    /**
     * @deprecated Returns the value of the {@code _rcvbuf} attribute for this 
     * TCP cooked
     * input.
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
     * Indicates whether this TCP cooked input is using secure socket layer 
     * (SSL).
     *
     * @return {@code true} if this TCP cooked input is using SSL,
     * {@code false} if not.
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
     * @param host The host.
     */
    public void setHost(String host) {
        setCacheValue("host", host);
    }

    /**
     * Sets a restriction to accept inputs from the specified host only.
     *
     * @param restrictToHost The host.
     */
    public void setRestrictToHost(String restrictToHost) {
        setCacheValue("restrictToHost", restrictToHost);
    }
}
