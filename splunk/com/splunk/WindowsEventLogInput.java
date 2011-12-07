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
 * Representation of the Windows Event Log input subclass.
 */
public class WindowsEventLogInput extends Input {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The Windows Event Log input endpoint.
     */
    WindowsEventLogInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns a comma separated list of additional hosts used in monitoring.
     *
     * @return additional hosts used in monitoring.
     */
    public String getHosts() {
        return getString("hosts", null);
    }

    /**
     * Returns this Windows Event Log input's index name, or null if not
     * specified.
     *
     * @return This Windows Event Log input's index name.
     */
    public String getIndex() {
        return getString("index", null);
    }

    /**
     * Returns the Windows Event Log input kind.
     *
     * @return The Windows Event Log input kind.
     */
    public InputKind getKind() {
        return InputKind.WindowsEventLog;
    }

    /**
     * Returns this Windows Event log input's list of event log names.
     *
     * @return This Windows Event log input's list of event log names.
     */
    public String [] getLogs() {
        return getStringArray("logs", null);
    }

    /**
     * Returns this Windows Event log input's collection name. This name
     * appears in configuration file, as well as the source and the sourcetype of
     * the indexed data. If the value is "localhost", it will use native event
     * log collection; otherwise, it will use WMI.
     *
     * @return This Windows Event log input's collection.
     */
    public String getLocalName() {
        return getString("name");
    }

    /**
     * Returns this Windows Event log input's main host. Secondary hosts are
     * specified in the hosts attribute.
     *
     * @return This Windows Event log input's main host.
     */
    public String getLookupHost() {
        return getString("lookup_host");
    }
}
