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
 * Representation of a Windows Perfmon input.
 */
public class WindowsPerfmonInput extends Input {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The Windows Perfmon input endpoint.
     */
    WindowsPerfmonInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this Windows Perfmon input's monitored counters. This is a comma
     * separated list. Null is returned if not specified.
     *
     * @return This Windows Perfmon input's counters.
     */
    public String getCounters() {
        return getString("counters", null);
    }

    /**
     * Returns this Windows Perfmon input's index name, or null if not
     * specified.
     *
     * @return This Windows Perfmon input's index name.
     */
    public String getIndex() {
        return getString("index", null);
    }

    /**
     * Returns this Windows Perfmon input's counter instances. A wildcard
     * equivocates to all instances.
     *
     * @return This Windows Perfmon input's counter instances.
     */
    public String getInstances() {
        return getString("instances", null);
    }

    /**
     * Returns the frequency, in seconds, to poll this Windows Perfmon input's
     * performance counters.
     *
     * @return This Windows Perfmon polling frequency.
     */
    public int getInterval() {
        return getInteger("interval");
    }

    /**
     * Returns the Windows Perfmon input kind.
     *
     * @return The Windows Perfmon input kind.
     */
    public InputKind getKind() {
        return InputKind.WindowsPerfmon;
    }

    /**
     * Returns this Windows Perfmon input's Windows performance object.
     *
     * @return This Windows Perfmon input's Windows performance object.
     */
    public String getObject() {
        return getString("object");
    }
}
