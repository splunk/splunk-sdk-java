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

public class WindowsWmiInput extends Input {
    WindowsWmiInput(Service service, String path) {
        super(service, path);
    }

    public String getClasses() {
        return getString("classes");
    }

    public String [] getFields() {
        return getStringArray("fields", null);
    }

    public String getIndex() {
        return getString("index", null);
    }

    public String [] getInstances() {
        return getStringArray("instances", null);
    }

    public int getInterval() {
        return getInteger("interval");
    }

    public InputKind getKind() {
        return InputKind.WindowsWmi;
    }

    public String getLookupHost() {
        return getString("lookup_host");
    }

    public String getLocalName() {
        return getString("name");
    }

    public String getServer() {
        return getString("server", null);
    }

    public String getWq1() {
        return getString("wq1");
    }
}
