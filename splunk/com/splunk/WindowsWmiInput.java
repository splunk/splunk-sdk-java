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
}
