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
public class InputKind {
    String kind;
    String relpath;
    Class inputClass;

    public InputKind(String relpath, Class inputClass, String kind) {
        this.relpath = relpath;
        this.inputClass = inputClass;
        this.kind = kind;
    }

    public InputKind(String relpath, Class inputClass) {
        this(relpath, inputClass, relpath.substring(relpath.lastIndexOf("/") + 1));
    }

    /** Unknown input kind. */
    public static InputKind Unknown = new InputKind(null, Input.class, "unknown");

    /** {@code Monitor} input kind. */
    public static InputKind Monitor = new InputKind("monitor", MonitorInput.class);

    /** {@code Script} input kind. */
    public static InputKind Script = new InputKind("script", ScriptInput.class);

    /** {@code TCP} input kind, raw input data. */
    public static InputKind Tcp = new InputKind("tcp/raw", TcpInput.class, "tcp");

    /** {@code TCP} input kind, processed input data. */
    public static InputKind TcpSplunk = new InputKind("tcp/cooked", TcpSplunkInput.class);

    /** {@code UDP} input kind. */
    public static InputKind Udp = new InputKind("udp", UdpInput.class);

    /** {@code Windows Active Directory} input kind. */
    public static InputKind WindowsActiveDirectory = new InputKind("ad", WindowsActiveDirectoryInput.class);

    /** {@code Windows Event Log} input kind. */
    public static InputKind WindowsEventLog = new InputKind("win-event-log-collections",WindowsEventLogInput.class);

    /** {@code Windows Perfmon} input kind. */
    public static InputKind WindowsPerfmon = new InputKind("win-perfmon", WindowsPerfmonInput.class);

    /** {@code Windows Registry} input kind. */
    public static InputKind WindowsRegistry = new InputKind("registry", WindowsRegistryInput.class);

    /** {@code Windows WMI} input kind. */
    public static InputKind WindowsWmi = new InputKind("win-wmi-collections", WindowsWmiInput.class);

    public static InputKind makeInputKind(String kind) {
        if (kind == "monitor") {
            return Monitor;
        } else if (kind == "script") {
            return Script;
        } else if (kind == "tcp/raw") {
            return Tcp;
        } else if (kind == "tcp/cooked") {
            return TcpSplunk;
        } else if (kind == "udp") {
            return Udp;
        } else if (kind == "ad") {
            return WindowsActiveDirectory;
        } else if (kind == "win-event-log-collections") {
            return WindowsEventLog;
        } else if (kind == "win-perfmon") {
            return WindowsPerfmon;
        } else if (kind == "registry") {
            return WindowsRegistry;
        } else if (kind == "win-wmi-collections") {
            return WindowsWmi;
        } else {
            return new InputKind("data/inputs/" + kind, Input.class);
        }
    }
}
