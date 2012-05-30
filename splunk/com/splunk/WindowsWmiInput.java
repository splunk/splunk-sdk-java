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
 * The {@code WindowsWmiInput} class represents a Windows Management
 * Instrumentation (WMI) input.
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
     * Returns the properties (fields) collected for this class for this WMI
     * input.
     *
     * @return The list of properties collected for this class, or {@code null}
     * if not specified.
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
     * @return A list of the WMI class instances, or {@code null} if not
     * specified.
     */
    public String [] getInstances() {
        return getStringArray("instances", null);
    }

    /**
     * Returns the interval at which WMI input providers are queried for this
     * WMI input.
     *
     * @return The WMI query interval, in seconds.
     */
    public int getInterval() {
        return getInteger("interval");
    }

    /**
     * Returns the input kind of this WMI input.
     * @see InputKind
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.WindowsWmi;
    }

    /**
     * Returns the main host for this WMI input. Secondary hosts are specified
     * in the {@code server} attribute.
     *
     * @return The main host.
     */
    public String getLookupHost() {
        return getString("lookup_host");
    }

    /**
     * Returns the collection name for this WMI input. This name appears in
     * configuration file, the source, and the sourcetype of the indexed data.
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
     * @return A comma-separated list of additional servers, or {@code null}
     * if not specified.
     */
    public String getServers() {
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
     * Sets the WMI class name.
     *
     * @param classes A valid WMI class name.
     */
    public void setClasses(String classes) {
        setCacheValue("classes", classes);
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
     * Sets the properties (fields) that you want to gather from the given
     * class.
     *
     * @param fields An array of properties (fields) to gather.
     */
    public void setFields(String[] fields) {
        setCacheValue("fields", fields);
    }

    /**
     * Sets a property (field) that you want to gather from the given
     * class. Use this method to set a single field rather than an array.
     *
     * @param field A property (field) to gather.
     */
    public void setFields(String field) {
        setCacheValue("fields", new String [] { field });
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
     * Sets the counter instances to monitor.
     *
     * @param instances The counter instances to monitor.
     */
    public void setInstances(String[] instances) {
        setCacheValue("instances", instances);
    }

    /**
     * Sets a counter instance to monitor. Use this method to set a single
     * instance rather than an array.
     *
     * @param instance A counter instance to monitor.
     */
    public void setInstances(String instance) {
        setCacheValue("instances", new String [] { instance });
    }

    /**
     * Sets the frequency at which the WMI providers are queried.
     *
     * @param interval The polling frequency, in seconds.
     */
    public void setInterval(int interval) {
        setCacheValue("interval", interval);
    }

    /**
     * Sets the host from which to monitor log events. To specify
     * additional hosts to monitor using WMI, use the {@code server} parameter.
     *
     * @param lookup_host The host.
     */
    public void setLookupHost(String lookup_host) {
        setCacheValue("lookup_host", lookup_host);
    }

    /**
     * Sets the additional servers that you want to gather data from. Use this
     * property if you need to gather more than a single server.
     *
     * @param servers A comma-separated list of hosts.
     */
    public void setServers(String servers) {
        setCacheValue("server", servers);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update(Map<String, Object> args) {
        // If not present in the update keys, add required attributes
        if (!args.containsKey("classes"))
            args = Args.create(args).add("classes", getClasses());
        if (!args.containsKey("interval"))
            args = Args.create(args).add("interval", getInterval());
        if (!args.containsKey("lookup_host"))
            args = Args.create(args).add(
                "lookup_host", getLookupHost());
        super.update(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update() {
        // If not present in the update keys, add required attributes as long
        // as one pre-existing update pair exists
        if (toUpdate.size() > 0 && !toUpdate.containsKey("classes")) {
            setCacheValue("classes", getClasses());
        }
        if (toUpdate.size() > 0 && !toUpdate.containsKey("interval")) {
            setCacheValue("interval", getInterval());
        }
        if (toUpdate.size() > 0 && !toUpdate.containsKey("lookup_host")) {
            setCacheValue("lookup_host", getLookupHost());
        }
        super.update();
    }
}
