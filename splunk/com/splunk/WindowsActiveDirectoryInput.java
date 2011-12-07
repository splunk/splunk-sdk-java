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
 * Representation of the Windows Active Directory input subclass.
 */
public class WindowsActiveDirectoryInput extends Input {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The Windows Active Directory input endpoint.
     */
    WindowsActiveDirectoryInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this Windows Active Directory input's index name, or null if not
     * specified.
     *
     * @return This Windows Active Directory input's index name.
     */
    public String getIndex() {
        return getString("index", null);
    }

    /**
     * Returns the Windows Active Directory input kind.
     *
     * @return The Windows Active Directory input kind.
     */
    public InputKind getKind() {
        return InputKind.WindowsActiveDirectory;
    }

    /**
     * Returns whether or not this Windows Active Directory input's directory
     * path subtree is being monitored.
     *
     * @return Whether or not this Windows Active Directory input's directory
     * path subtree is being monitored.
     */
    public boolean getMonitorSubtree() {
        return getBoolean("monitorSubtree");
    }

    /**
     * Returns this Windows Active Directory input's starting location in the
     * directory path. Null if not specified. If not specified, the the root of
     * the directory tree is used.
     *
     * @return this Windows Active Directory input's starting location in the
     * directory path.
     */
    public String getStartingNode() {
        return getString("startingNode", null);
    }

    /**
     * Returns the fully qualified domain name of a valid, network accessible
     * Domain Controller. Null if not specified. If not specified, the local
     * machine is used.
     *
     * @return The fully qualified domain name.
     */
    public String getTargetDc() {
        return getString("targetDc", null);
    }
}
