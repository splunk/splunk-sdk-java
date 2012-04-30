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

import java.util.Map;

/**
 * The {@code WindowsWmiInput} class represents a Windows Management Instrumentation (WMI) input.
 */
public class WindowsWmiInput extends Input {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The Windows WMI input endpoint.
     */
    WindowsWmiInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the WMI class name of this WMI input.
     *
     * @return The WMI class name.
     */
    public String getClasses() {
        return getString("classes");
    }

    /**
     * Returns the properties (fields) collected for this class for this WMI input.
     *
     * @return The list of properties collected for this class, or {@code null} if not specified.
     */
    public String [] getFields() {
        return getStringArray("fields", null);
    }

    /**
     * Returns the index name of this WMI input.
     *
     * @return The index name, or {@code null} if not specified.
     */
    public String getIndex() {
        return getString("index", null);
    }

    /**
     * Returns a list of the WMI class instances for this WMI input.
     *
     * @return A list of the WMI class instances, or {@code null} if not specified.
     */
    public String [] getInstances() {
        return getStringArray("instances", null);
    }

    /**
     * Returns the interval at which WMI input providers are queried for this WMI input.
     *
     * @return The WMI query interval, in seconds.
     */
    public int getInterval() {
        return getInteger("interval");
    }

    /**
     * Returns the input type of this WMI input.
     * @see InputKind
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.WindowsWmi;
    }

    /**
     * Returns the main host for this WMI input. Secondary hosts are specified in
     * the {@code server} attribute.
     * @see #getServer
     *
     * @return The main host.
     */
    public String getLookupHost() {
        return getString("lookup_host");
    }

    /**
     * Returns the collection name for this WMI input. This name appears in
     * configuration file, the source, and the sourcetype of the
     * indexed data.
     *
     * @return The collection name.
     */
    public String getLocalName() {
        return getString("name");
    }

    /**
     * Returns a list of additional servers used in monitoring.
     * @see #getLookupHost
     *
     * @return A comma-separated list of additional servers, or {@code null} if not specified.
     */
    public String getServer() {
        return getString("server", null);
    }

    /**
     * Returns the query string for this WMI input.
     *
     * @return The query string.
     */
    public String getWql() {
        return getString("wql");
    }

    /**
     * Sets the type regular expression. If specified, will only collect
     * changes if a type name matches that regex.
     *
     * @param type The type regular expression.
     */
    public void setClasses(String type) {
        setCacheValue("type", type);
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
     * Sets the Properties (fields) that you want to gather from the given
     * class.
     *
     * @param fields The properties (fields) that you want to gather from the
     * given class.
     */
    public void setFields(String[] fields) {
        setCacheValue("fields", fields);
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
     * Sets the counter instances to monitor.
     *
     * @param instances the counter instances to monitor.
     */
    public void setInstances(String[] instances) {
        setCacheValue("instances", instances);
    }

    /**
     * Sets the frequency, in seconds, at which the WMI provider(s) will be
     * queried.
     *
     * @param interval The polling frequency, in seconds.
     */
    public void setInterval(int interval) {
        setCacheValue("interval", interval);
    }

    /**
     * Sets the host from which we will monitor log events. To specify
     * additional hosts to be monitored via WMI, use the "server" parameter.
     *
     * @param lookup_host The host from which we will monitor log events.
     */
    public void setLookupHost(String lookup_host) {
        setCacheValue("lookup_host", lookup_host);
    }

    /**
     * Sets the additional servers that you want to gather data from. Use this
     * attribute if you need to gather more than a single machine.  This value
     * is a comma-separated list.
     *
     * @param servers The host from which we will monitor log events.
     */
    public void setServers(String servers) {
        setCacheValue("servers", servers);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update(Map<String, Object> args) {
        if (!args.containsKey("classes")) // required
            args = Args.create(args).add("classes", getClasses());
        if (!args.containsKey("interval")) // required
            args = Args.create(args).add("interval", getInterval());
        if (!args.containsKey("lookup_host")) // required
            args = Args.create(args).add("lookup_host", getLookupHost());
        super.update(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update() {
        if (!isUpdateKeyPresent("classes")) {
            setCacheValue("classes", getClasses()); // required
        }
        if (!isUpdateKeyPresent("interval")) {
            setCacheValue("interval", getInterval()); // required
        }
        if (!isUpdateKeyPresent("lookup_host")) {
            setCacheValue("lookup_host", getLookupHost()); // required
        }
        super.update();
    }
}
