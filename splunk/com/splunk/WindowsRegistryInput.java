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
 * Representation of the Windows Registry input subclass.
 */
public class WindowsRegistryInput extends Input {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The Windows Registry input endpoint.
     */
    WindowsRegistryInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns whether or not this Windows Registry input has a baseline
     * established.
     *
     * @return Whether or not this Windows Registry input has a baseline
     * established.
     */
    public boolean getBaseline() {
        return getBoolean("baseline");
    }

    /**
     * Returns this Windows Registry input's regular expression (regex) that is
     * executed against process names when including/excluding events.
     *
     * @return This Windows Registry input's process regular expression.
     */
    public String getProc() {
        return getString("proc");
    }

    /**
     * Returns this Windows Registry input's hive name to monitor.
     *
     * @return This Windows Registry input's hive name to monitor.
     */
    public String getHive() {
        return getString("hive");
    }

    /**
     * Returns this Windows Registry input's index name, or null if not
     * specified.
     *
     * @return This Windows Registry input's index name.
     */
    public String getIndex() {
        return getString("index");
    }

    /**
     * Returns the Windows Registry input kind.
     *
     * @return The Windows Registry input kind.
     */
    public InputKind getKind() {
        return InputKind.WindowsRegistry;
    }

    /**
     * Returns whether or not this Windows Registry input monitors all
     * sub-nodes under a given hive.
     *
     * @return Whether or not this Windows Registry input monitors all
     * sub-nodes under a given hive.
     */
    public boolean getMonitorSubnodes() {
        return getBoolean("monitorSubnodes", false);
    }

    /**
     * Returns this Windows Registry input's regular expression (regex) that is
     * executed against registry event types.
     *
     * @return This Windows Registry input's registry type regular expression.
     */
    public String getType() {
        return getString("type", null);
    }
}
