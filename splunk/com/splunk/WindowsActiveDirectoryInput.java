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
 * The {@code WindowsActiveDirectoryInput} class represents a Windows Active
 * Directory input.
 */
public class WindowsActiveDirectoryInput extends Input {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The Windows Active Directory input endpoint.
     */
    WindowsActiveDirectoryInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the index name of this Windows Active Directory input.
     *
     * @return The index name, or {@code null}  if not specified.
     */
    public String getIndex() {
        return getString("index", null);
    }

    /**
     * Returns the input type of this Windows Active Directory input.
     * @see InputKind
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.WindowsActiveDirectory;
    }

    /**
     * Indicates whether the directory path subtree is being monitored for
     * this Windows Active Directory input.
     *
     * @return {@code true} if the directory path subtree is being monitored, 
     * {@code false} if not.
     */
    public boolean getMonitorSubtree() {
        return getBoolean("monitorSubtree");
    }

    /**
     * Returns the starting location in the directory path for this Windows
     * Active Directory input. If not specified, the the root of the directory
     * tree is used.
     *
     * @return The starting location in the directory path, or {@code null} if 
     * not specified.
     */
    public String getStartingNode() {
        return getString("startingNode", null);
    }

    /**
     * Returns the fully-qualified domain name of a valid, network-accessible
     * domain controller. If not specified, the local machine is used.
     *
     * @return The fully-qualified domain name, or {@code null} if not
     * specified.
     */
    public String getTargetDc() {
        return getString("targetDc", null);
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
     * Sets index in which to store all generated events.
     *
     * @param index The index in which to store all generated events.
     */
    public void setIndex(String index) {
        setCacheValue("index", index);
    }

    /**
     * Sets whether or not to monitor the subtree(s) of the given directory tree
     * path. {@code True} means to monitor, {@code false} mean not to monitor
     * the subtree(s)
     *
     * @param monitorSubtree Whether or not to monitor the subtree(s).
     */
    public void setMonitorSubtree(boolean monitorSubtree) {
        setCacheValue("monitorSubtree", monitorSubtree);
    }

    /**
     * Sets the starting Active Directory directory to start monitoring. If not
     * specified, splunk attempts to start at the root of the directory tree.
     *
     * @param startingNode The starting Active Directory directory to start
     * monitoring.
     */
    public void setStartingNode(String startingNode) {
        setCacheValue("startingNode", startingNode);
    }

    /**
     * Sets the fully qualified domain name of a valid, network-accessible DC.
     * If not specified, Splunk will obtain the local computer's domain
     * controller.
     *
     * @param targetDc The fully qualified domain name.
     */
    public void setTargetDc(String targetDc) {
        setCacheValue("targetDc", targetDc);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update(Map<String, Object> args) {
        // Add required arguments if not already present
        validateFromUpdate();
        if (!args.containsKey("monitorSubtree")) {
            args = Args.create(args).add(
                "monitorSubtree", getObjectForUpdate("monitorSubtree"));
        }
        super.update(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update() {
        // If not present in the update keys, add required attribute as long
        // as one pre-existing update pair exists
        validateFromUpdate();
        if (toUpdate.size() > 0 && !toUpdate.containsKey("monitorSubtree")) {
            setCacheValueFromUpdate(
                "monitorSubtree", getObjectForUpdate("monitorSubtree"));
        }
        super.update();
    }
}
