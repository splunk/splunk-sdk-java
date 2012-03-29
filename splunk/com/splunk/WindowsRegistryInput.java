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
 * The {@code WindowsRegistryInput} class represents a Windows Registry input.
 */
public class WindowsRegistryInput extends Input {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The Windows Registry input endpoint.
     */
    WindowsRegistryInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Indicates whether this Windows Registry input has an established baseline.
     *
     * @return {@code true} if this Windows Registry input has an established
     * baseline, {@code false} if not.
     */
    public boolean getBaseline() {
        return getBoolean("baseline");
    }

    /**
     * Returns the regular expression (regex) that is executed against process 
     * names when including or excluding events for this Windows Registry input.
     * Changes are only collected if a process name matches this regex. 
     *
     * @return The process names regex.
     */
    public String getProc() {
        return getString("proc");
    }

    /**
     * Returns the hive name to monitor for this Windows Registry input.
     *
     * @return The hive name to monitor.
     */
    public String getHive() {
        return getString("hive");
    }

    /**
     * Returns the index name for this Windows Registry input.
     *
     * @return The index name.
     */
    public String getIndex() {
        return getString("index");
    }

    /**
     * Returns the input type for this Windows Registry input.
     * @see InputKind
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.WindowsRegistry;
    }

    /**
     * Indicates whether this Windows Registry input monitors all
     * sub-nodes under a given hive.
     *
     * @return {@code true} if this Windows Registry input monitors all
     * sub-nodes under a given hive, {@code false} if not.
     */
    public boolean getMonitorSubnodes() {
        return getBoolean("monitorSubnodes", false);
    }

    /**
     * Returns the regular expression (regex) that is executed against registry
     * event types for this Windows Registry input. Only types that match
     * this regex are monitored.
     *
     * @return The registry type regex, or {@code null} if not specified.
     */
    public String getType() {
        return getString("type", null);
    }
}
