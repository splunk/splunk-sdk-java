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

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code InputKind} enumeration defines the different types of Splunk data
 * inputs (<i>input kinds</i>).
 */
public class InputKind {
    private String kind;
    private String relpath;
    private Class inputClass;

    private static Map<String, InputKind> knownKinds = new HashMap<String, InputKind>();

    public InputKind(String relpath, Class inputClass, String kind) {
        this.relpath = relpath;
        this.inputClass = inputClass;
        this.kind = kind;
        knownKinds.put(kind, this);
    }


    public InputKind(String relpath, Class inputClass) {
        this(
            relpath,
            inputClass,
            Util.substringAfter(relpath, "data/inputs/", relpath)
        );
    }

    public String getKind() {
        return kind;
    }

    public String getRelpath() {
        return relpath;
    }

    public Class getInputClass() {
        return inputClass;
    }

    /** Unknown input kind. */
    public static final InputKind Unknown = new InputKind(null, Input.class, "unknown");

    /** {@code Monitor} input kind. */
    public static final InputKind Monitor = new InputKind("monitor", MonitorInput.class);

    /** {@code Script} input kind. */
    public static final InputKind Script = new InputKind("script", ScriptInput.class);

    /** {@code TCP} input kind, raw input data. */
    public static final InputKind Tcp = new InputKind("tcp/raw", TcpInput.class, "tcp");

    /** {@code TCP} input kind, processed input data. */
    public static final InputKind TcpSplunk = new InputKind("tcp/cooked", TcpSplunkInput.class);

    /** {@code UDP} input kind. */
    public static final InputKind Udp = new InputKind("udp", UdpInput.class);

    /** {@code Windows Active Directory} input kind. */
    public static final InputKind WindowsActiveDirectory = new InputKind("ad", WindowsActiveDirectoryInput.class);

    /** {@code Windows Event Log} input kind. */
    public static final InputKind WindowsEventLog = new InputKind("win-event-log-collections",WindowsEventLogInput.class);

    /** {@code Windows Perfmon} input kind. */
    public static final InputKind WindowsPerfmon = new InputKind("win-perfmon", WindowsPerfmonInput.class);

    /** {@code Windows Registry} input kind. */
    public static final InputKind WindowsRegistry = new InputKind("registry", WindowsRegistryInput.class);

    /** {@code Windows WMI} input kind. */
    public static final InputKind WindowsWmi = new InputKind("win-wmi-collections", WindowsWmiInput.class);

    public static InputKind createInputKind(String kind) {
        if (knownKinds.containsKey(kind)) {
            return knownKinds.get(kind);
        } else {
            return new InputKind(kind, Input.class);
        }
    }
}
