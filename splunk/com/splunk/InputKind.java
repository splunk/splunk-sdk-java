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
 *
 * For example, the type of a raw TCP input is {@code InputKind.Tcp}. The type of a cooked TCP input is
 * {@code InputKind.TcpSplunk}. Each modular input kind shows up as a separate instance of {@code InputKind}.
 *
 * Note that {@code InputKind}s are not hierarchical, so even though there are instances for both cooked and
 * raw TCP, there is none for all TCP inputs.
 */
public class InputKind {
    private String kind;
    private String relpath;
    private Class<? extends Input> inputClass;

    private static Map<String, InputKind> knownRelpaths = new HashMap<String, InputKind>();

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
     * @return String representing the kind of this InputKind, as it is
     *         represented in the Atom entry for an input entity.
     */
    String getKind() {
        return kind;
    }

    /**
     * @return String giving the relative path from data/inputs/ to this InputKind.
     */
    String getRelativePath() {
        return relpath;
    }

    /**
     * @return The class this InputKind's instances should be created with.
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
