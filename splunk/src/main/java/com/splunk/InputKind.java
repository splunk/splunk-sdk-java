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

    private static Map<String, InputKind> knownRelpaths = new HashMap<String, InputKind>();

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
    // As of Splunk 6, "win-event-log-collections" has changed to "WinEventLog".
    public static final InputKind WinEventLog = new InputKind("WinEventLog", WindowsEventLogInput.class);

    /** Windows performance monitor input. */
    public static final InputKind WindowsPerfmon = new InputKind("win-perfmon", WindowsPerfmonInput.class);

    /** Windows Registry input. */
    public static final InputKind WindowsRegistry = new InputKind("registry", WindowsRegistryInput.class);
    // As of Splunk 6, "registry" has changed to "WinRegMon".
    public static final InputKind WinRegMon = new InputKind("WinRegMon", WindowsRegistryInput.class);

    /** Windows Management Instrumentation (WMI) input. */
    public static final InputKind WindowsWmi = new InputKind("win-wmi-collections", WindowsWmiInput.class);

    private InputKind(String relpath, Class<? extends Input> inputClass, String kind) {
        this.relpath = relpath;
        this.inputClass = inputClass;
        this.kind = kind;
        
        knownRelpaths.put(relpath, this);
    }

    private InputKind(String relpath, Class<? extends Input> inputClass) {
        this(
            relpath,
            inputClass,
            relpath
        );
    }

    /**
     * @return A string that specifies the input kind, as it is
     *         represented in the Atom entry for an input entity.
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
     * Create an {@code InputKind} object from a {@code String} giving
     * the relative path from data/inputs/ to the kind. For example,
     * "tcp/raw" or "monitor".
     *
     * {@code InputKind}'s constructors are private. You should use this method
     * to create an {@code InputKind}.
     *
     * @param relpath The relative path from data/inputs specifying the {@code InputKind} to create.
     * @return An {@code InputKind} object.
     */
    public static InputKind create(String relpath) {
        if (knownRelpaths.containsKey(relpath)) {
            return knownRelpaths.get(relpath);
        } else {
            return new InputKind(relpath, Input.class);
        }
    }
    
    /**
     * @return Textual representation for debugging purposes.
     */
    public String toString() {
        return relpath;
    }
}
