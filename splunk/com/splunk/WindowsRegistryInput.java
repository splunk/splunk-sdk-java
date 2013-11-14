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
 * The {@code WindowsRegistryInput} class represents a Windows Registry data 
 * input.
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
     * Returns the input kind for this input.
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
     * @return An array of regex strings for event types.
     */
    public String[] getType() {
        if (toUpdate.containsKey("type")) {
            String value = (String)toUpdate.get("type");
            if (value.contains("|")) {
                return value.split("\\|");
            }
            else {
                return new String[]{value};
            }
        }
        else {
            // Before Splunk 6, the type was returned in a form like ["create", "delete"].
            // In Splunk 6, it has changed to create|delete, which is symmetric with the values taken
            // by setType.
            if (service.versionIsEarlierThan("6.0.0")) {
                return getStringArray("type", new String[] {});
            } else {
                String types = getString("type", null);
                if (types == null) {
                    return new String[]{};
                } else {
                    return types.split("\\|");
                }
            }
        }
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
     * <p>
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
        // In Splunk 6, monitor subnodes is a trailing string on hive.
        if (service.versionIsAtLeast("6.0.0")) {
            if (getHive().endsWith("\\\\?.*")) {
                if (!monitorSubnodes) {
                    setHive(getHive().substring(0, getHive().length()-5));
                }
            } else {
                if (monitorSubnodes) {
                    setHive(getHive() + "\\\\?.*");
                }
            }
        } else {
            // In Splunk 5 and earlier, it's a separate field.
            setCacheValue("monitorSubnodes", monitorSubnodes);
        }
    }

    /**
     * Sets the regular expression (regex) that is compared to process 
     * names when including or excluding events for this Windows Registry input.
     * Changes are only collected if a process name matches this regex. 
     *
     * @param proc The process names regex.
     */
    public void setProc(String proc) {
        setCacheValue("proc", proc);
    }
    
    /**
     * Sets the regular expressions (regexes) that are compared to registry
     * event types for this Windows Registry input. Only types that match
     * at least one regex are monitored.
     *
     * @param regexes An array of regex strings for event types.
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
            args = Args.create(args).add("type", Util.join("|", getType()));
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
            setCacheValue("type", Util.join("|", getType()));
        }
        super.update();
    }
}
