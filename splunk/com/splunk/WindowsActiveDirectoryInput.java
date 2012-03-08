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
 * The {@code WindowsActiveDirectoryInput} class represents a Windows Active Directory input.
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
     * Returns the starting location in the directory path for this Windows Active
     * Directory input. If not specified, the the root of the directory tree is used.
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
     * @return The fully-qualified domain name, or {@code null} if not specified.
     */
    public String getTargetDc() {
        return getString("targetDc", null);
    }
}
