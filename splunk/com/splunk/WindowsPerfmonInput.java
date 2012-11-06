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
 * The {@code WindowsPerfmonInput} class represents a Windows Performance
 * Monitor (Perfmon) data input.
 */
public class WindowsPerfmonInput extends Input {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The Windows Perfmon input endpoint.
     */
    WindowsPerfmonInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns a list of monitored counters for this Windows Perfmon input. 
     * An asterisk (*) is equivalent to all counters.
     *
     * @return A string array list of counters, or {@code null} if not
     * specified.
     */
    public String[] getCounters() {
        return getStringArray("counters", null);
    }

    /**
     * Returns the index name of this Windows Perfmon input.
     *
     * @return The index name, or {@code null} if not specified.
     */
    public String getIndex() {
        return getString("index", null);
    }

    /**
     * Returns the counter instances of this Windows Perfmon input. An asterisk 
     * (*) is equivalent to all instances.
     *
     * @return A string array of counter instances, or {@code null} if not
     * specified.
     */
    public String[] getInstances() {
        return getStringArray("instances", null);
    }

    /**
     * Returns the interval at which to poll the performance counters for this
     * Windows Perfmon input.
     *
     * @return The polling interval, in seconds.
     */
    public int getInterval() {
        return getInteger("interval");
    }

    /**
     * Returns the input kind of this input.
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.WindowsPerfmon;
    }

    /**
     * Returns the performance monitor object for this Windows 
     * Perfmon input (for example, "Process", "Server", or "PhysicalDisk").
     *
     * @return A string containing the performance monitor object.
     */
    public String getObject() {
        return getString("object");
    }

    /**
     * Sets the counters to monitor.
     *
     * @param counters An array of counters to monitor.
     */
    public void setCounters(String[] counters) {
        setCacheValue("counters", counters);
    }

    /**
     * Sets a counter to monitor. A wildcard value of an asterisk (*) means 
     * all counters. Use this method to set a single counter rather than an 
     * array.
     *
     * @param counter The counter to monitor.
     */
    public void setCounters(String counter) {
        setCacheValue("counters", new String [] { counter });
    }

    /**
     * Sets whether this input is enabled or disabled. 
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
     * Sets a counter instance to monitor. A wildcard value of an asterisk (*)
     * means all instances. Use this method to set a single instance rather than
     * an array.
     *
     * @param instance A counter instance to monitor.
     */
    public void setInstances(String instance) {
        setCacheValue("instances", new String [] { instance });
    }

    /**
     * Sets the frequency to poll the performance counters.
     *
     * @param interval The polling frequency, in seconds.
     */
    public void setInterval(int interval) {
        setCacheValue("interval", interval);
    }

    /**
     * Sets the performance monitor object (for example, "Process", "Server", or
     * "PhysicalDisk").
     *
     * @param object The performance monitor object.
     */
    public void setObject(String object) {
        setCacheValue("object", object);
    }
}
