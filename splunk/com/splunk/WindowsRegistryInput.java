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

import java.util.Collection;
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
     * Indicates whether this Windows Registry input has an established
     * baseline.
     *
     * @return {@code true} if this Windows Registry input has an established
     * baseline, {@code false} if not.
     */
    public boolean getBaseline() {
        return getBoolean("baseline");
    }

    /**
     * Returns the regular expression (regex) that is compared to process 
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
     * Returns the input kind for this Windows Registry input.
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
     * Returns the regular expressions (regexes) that are compared to registry
     * event types for this Windows Registry input. Only types that match
     * at least one of the regexes are monitored.
     * 
     * @return A String array of the regular expressions.
     */
    public String[] getType() {
        String[] value = getStringArray("type");
        String[] typeRegex;
        if (value.length == 1 && value[0].contains("|")) {
            typeRegex = value[0].split("|");
        } else {
            typeRegex = value;
        }
        return typeRegex;
    }

    /**
     * Sets whether to establish a baseline value for the registry keys.
     *
     * @param baseline {@code true} to establish a baseline value, {@code false}
     * if not.
     */
    public void setBaseline(boolean baseline) {
        setCacheValue("baseline", baseline);
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
     * Sets the registry hive under which to monitor for changes.
     *
     * @param hive The registry hive.
     */
    public void setHive(String hive) {
        setCacheValue("hive", hive);
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
     * Sets whether to monitor the subnodes of the given registry hive.
     *
     * @param monitorSubnodes {@code true} for this Windows Registry input to 
     * monitor all sub-nodes, {@code false} if not.
     */
    public void setMonitorSubnodes(boolean monitorSubnodes) {
        setCacheValue("monitorSubnodes", monitorSubnodes);
    }

    /**
     * Sets the process regular expression (regex) that is compared to process 
     * names when including or excluding events for this Windows Registry input.
     * Changes are only collected if a process name matches this regex. 
     *
     * @param proc The process regex.
     */
    public void setProc(String proc) {
        setCacheValue("proc", proc);
    }

    /**
     * Sets the regular expressions (regexes) that are compared to registry
     * event types for this Windows Registry input. Only types that match
     * at least one regex are monitored.
     *
     * @param regexes Array or collection of strings giving the regexes.
     */
    public void setType(String[] regexes) {
        String val = Util.join("|", regexes);
        setCacheValue("type", val);
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
