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

    /**
     * Sets whether or not to establish a baseline value for the registry keys.
     * {@code True} means yes, {@code False} means no.
     *
     * @param baseline Whether or not to establish a baseline value for the
     * registry keys.
     */
    public void setBaseline(boolean baseline) {
        setCacheValue("baseline", baseline);
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
     * Sets the registry hive under which to monitor for changes.
     *
     * @param hive The registry hive under which to monitor for changes.
     */
    public void setHive(String hive) {
        setCacheValue("hive", hive);
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
     * Sets whether or not to monitor the subnodes of the given registry hive.
     * {@code True} means to monitor, {@code false} mean not to monitor
     * the subnodes.
     *
     * @param monitorSubnodes Whether or not to monitor the subnodes.
     */
    public void setMonitorSubnodes(boolean monitorSubnodes) {
        setCacheValue("monitorSubnodes", monitorSubnodes);
    }

    /**
     * Sets the process regular expression. If specified, will only collect
     * changes if a process name matches that regex.
     *
     * @param proc The process regular expression.
     */
    public void setProc(String proc) {
        setCacheValue("proc", proc);
    }

    /**
     * Sets the type regular expression. If specified, will only collect
     * changes if a type name matches that regex.
     *
     * @param type The type regular expression.
     */
    public void setType(String type) {
        setCacheValue("type", type);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update(Map<String, Object> args) {
        // Add required arguments if not already present
        if (!args.containsKey("baseline")) {
            args = Args.create(args).add("baseline", getBaseline());
        }
        if (!args.containsKey("hive")) {
            args = Args.create(args).add("hive", getHive());
        }
        if (!args.containsKey("proc")) {
            args = Args.create(args).add("proc", getProc());
        }
        if (!args.containsKey("type")) {
            args = Args.create(args).add("type", getType());
        }
        super.update(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update() {
        // If not present in the update keys, add required attributes as long
        // as one pre-existing update pair exists
        if (toUpdate.size() > 0 && !toUpdate.containsKey("baseline")) {
            setCacheValue("baseline", getBaseline());
        }
        if (toUpdate.size() > 0 && !toUpdate.containsKey("hive")) {
            setCacheValue("hive", getHive());
        }
        if (toUpdate.size() > 0 && !toUpdate.containsKey("proc")) {
            setCacheValue("proc", getProc());
        }
        if (toUpdate.size() > 0 && !toUpdate.containsKey("type")) {
            setCacheValue("type", getType());
        }
        super.update();
    }
}
