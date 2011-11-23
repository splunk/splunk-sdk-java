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

public enum InputKind {
    Unknown("Unknown", Input.class),
    Monitor("monitor", MonitorInput.class),
    Script("script", ScriptInput.class),
    Tcp("tcp/raw", TcpInput.class),
    TcpSplunk("tcp/cooked", TcpSplunkInput.class),
    Udp("udp", UdpInput.class),
    WindowsActiveDirectory("ad", WindowsActiveDirectoryInput.class),
    WindowsEventLog("win-event-log-collections",WindowsEventLogInput.class),
    WindowsPerfmon("win-perfmon", WindowsPerfmonInput.class),
    WindowsRegistry("registry", WindowsRegistryInput.class),
    WindowsWmi("win-wmi-collections", WindowsWmiInput.class);

    InputKind(String relpath, Class inputClass) {
        this.relpath = relpath;
        this.inputClass = inputClass;
    }

    String relpath;
    Class inputClass;
}
