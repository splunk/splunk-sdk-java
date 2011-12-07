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
 * Representation of the Windows WMI input subclass.
 */
public class WindowsWmiInput extends Input {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The Windows WMI input endpoint.
     */
    WindowsWmiInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this Windows WMI input's WMI class name.
     *
     * @return This Windows WMI input's WMI class name.
     */
    public String getClasses() {
        return getString("classes");
    }

    /**
     * Returns this Windows WMI input's properties list collected for
     * this class.
     *
     * @return This Windows WMI input's properties list collected for
     * this class.
     */
    public String [] getFields() {
        return getStringArray("fields", null);
    }

    /**
     * Returns this Windows WMI input's index name, or null if not specified.
     *
     * @return This Windows WMI input's index name.
     */
    public String getIndex() {
        return getString("index", null);
    }

    /**
     * Returns a list of this Windows WMI class instances.
     *
     * @return A list of this Windows WMI class instances.
     */
    public String [] getInstances() {
        return getStringArray("instances", null);
    }

    /**
     * Returns the frequency, in seconds, to query this Windows WMI input
     * providers.
     *
     * @return This Windows WMI query frequency.
     */
    public int getInterval() {
        return getInteger("interval");
    }

    /**
     * Returns the Windows WMI input kind.
     *
     * @return The Windows WMI input kind.
     */
    public InputKind getKind() {
        return InputKind.WindowsWmi;
    }

    /**
     * Returns this Windows WMI input's main host. Secondary hosts are
     * specified in the server attribute.
     *
     * @return This Windows WMI input's main host.
     */
    public String getLookupHost() {
        return getString("lookup_host");
    }

    /**
     * Returns this Windows WMI input's collection name. This name appears in
     * configuration file, as well as the source and the sourcetype of the
     * indexed data.
     *
     * @return This Windows WMI input's collection.
     */
    public String getLocalName() {
        return getString("name");
    }

    /**
     * Returns a comma separated list of additional servers used in monitoring.
     *
     * @return additional servers used in monitoring.
     */
    public String getServer() {
        return getString("server", null);
    }

    /**
     * Returns this Windows WMI input's query string.
     *
     * @return This Windows WMI input's query string.
     */
    public String getWql() {
        return getString("wql");
    }
}
