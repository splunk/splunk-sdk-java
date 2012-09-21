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
 * The {@code InputKind} enumeration defines the different types of Splunk data
 * inputs (<i>input kinds</i>).
 */
public enum InputKind {
    /** Unknown input kind. */
    Unknown("Unknown", Input.class),

    /** {@code Monitor} input kind. */
    Monitor("monitor", MonitorInput.class),

    /** {@code Script} input kind. */
    Script("script", ScriptInput.class),

    /** {@code TCP} input kind, raw input data. */
    Tcp("tcp/raw", TcpInput.class),

    /** {@code TCP} input kind, processed input data. */
    TcpSplunk("tcp/cooked", TcpSplunkInput.class),

    /** {@code UDP} input kind. */
    Udp("udp", UdpInput.class),

    /** {@code Windows Active Directory} input kind. */
    WindowsActiveDirectory("ad", WindowsActiveDirectoryInput.class),

    /** {@code Windows Event Log} input kind. */
    WindowsEventLog("win-event-log-collections",WindowsEventLogInput.class),

    /** {@code Windows Perfmon} input kind. */
    WindowsPerfmon("win-perfmon", WindowsPerfmonInput.class),

    /** {@code Windows Registry} input kind. */
    WindowsRegistry("registry", WindowsRegistryInput.class),

    /** {@code Windows WMI} input kind. */
    WindowsWmi("win-wmi-collections", WindowsWmiInput.class),

    /** {@code modular} input kind */
    Modular("modular", ModularInput.class);
    /**
     * Sets the relative path and input class for the active object.
     *
     * @param relpath The relative path.
     * @param inputClass The input class.
     */
    InputKind(String relpath, Class inputClass) {
        this.relpath = relpath;
        this.inputClass = inputClass;
    }

    InputKind(String relpath, Class inputClass, String inputType) {
        this.relpath = relpath;
        this.inputClass = inputClass;
    }

    String relpath;
    Class inputClass;
}
