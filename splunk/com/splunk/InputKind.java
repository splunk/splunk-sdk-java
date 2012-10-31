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
 * inputs (<i>input kinds</i>). For example, a raw TCP input is 
 * {@code InputKind.Tcp}, and a cooked TCP input is {@code InputKind.TcpSplunk}.
 * Each modular input kind shows up as a separate instance of {@code InputKind}.
 */
public class InputKind {
    private String kind;
    private String relpath;
    private Class<? extends Input> inputClass;

    private static Map<String, InputKind> knownKinds = new HashMap<String, InputKind>();

    /** Unknown type of input. */
    public static final InputKind Unknown = new InputKind(null, Input.class, "unknown");

    /** Monitor input. */
    public static final InputKind Monitor = new InputKind("monitor", MonitorInput.class);

    /** Script input. */
    public static final InputKind Script = new InputKind("script", ScriptInput.class);

    /** Raw TCP input. */
    public static final InputKind Tcp = new InputKind("tcp/raw", TcpInput.class, "tcp");

    /** Cooked TCP input. */
    public static final InputKind TcpSplunk = new InputKind("tcp/cooked", TcpSplunkInput.class);

    /** UDP input. */
    public static final InputKind Udp = new InputKind("udp", UdpInput.class);

    /** Windows Active Directory input. */
    public static final InputKind WindowsActiveDirectory = new InputKind("ad", WindowsActiveDirectoryInput.class);

    /** Windows event log input. */
    public static final InputKind WindowsEventLog = new InputKind("win-event-log-collections",WindowsEventLogInput.class);

    /** Windows performance monitor input. */
    public static final InputKind WindowsPerfmon = new InputKind("win-perfmon", WindowsPerfmonInput.class);

    /** Windows Registry input. */
    public static final InputKind WindowsRegistry = new InputKind("registry", WindowsRegistryInput.class);

    /** Windows Management Instrumentation (WMI) input. */
    public static final InputKind WindowsWmi = new InputKind("win-wmi-collections", WindowsWmiInput.class);

    private InputKind(String relpath, Class inputClass, String kind) {
        this.relpath = relpath;
        this.inputClass = inputClass;
        this.kind = kind;
        knownKinds.put(kind, this);
    }

    private InputKind(String relpath, Class inputClass) {
        this(
            relpath,
            inputClass,
            relpath
        );
    }

    /**
     * @return A string that specifies the input kind.
     */
    String getKind() {
        return kind;
    }

    /**
     * @return A string that contains the relative endpoint path from the 
     * data/inputs/ endpoint to this input kind.
     */
    String getRelativePath() {
        return relpath;
    }

    /**
     * @return The class to use to create instances for this input kind. 
     */
    Class<? extends Input> getInputClass() {
        return inputClass;
    }

    /**
     * Creates an {@code InputKind} object from a string that contains either 
     * the {@code InputKind} or a relative endpoint path (the path that follows
     * the data/inputs/ endpoint). 
     * <p>
     * Input kinds and relative endpoint paths are not always the same. For 
     * example, for raw TCP inputs, the {@code InputKind} string is "tcp", and 
     * its relative endpoint path is "tcp/raw". For cooked TCP inputs, the 
     * {@code InputKind} string is "splunktcp", and the relative endpoint path is 
     * "tcp/cooked".
     *
     * @param kindOrRelpath The input kind or relative endpoint path.
     * @return An {@code InputKind} object.
     */
    public static InputKind create(String kindOrRelpath) {
        if (knownKinds.containsKey(kindOrRelpath)) {
            return knownKinds.get(kindOrRelpath);
        } else {
            return new InputKind(kindOrRelpath, Input.class);
        }
    }
}
