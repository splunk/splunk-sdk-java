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

package com.splunk.examples.explorer;

import com.splunk.Entity;

class SettingsNode extends EntityNode {
    SettingsNode(Entity value) {
        super(value);
        setDisplayName("Settings");
    }

    @Override protected PropertyList getMetadata() {
        PropertyList list = super.getMetadata();
        list.add(String.class, "getSplunkDB");
        list.add(String.class, "getSplunkHome");
        list.add(boolean.class, "getEnableSplunkWebSSL");
        list.add(String.class, "getHost");
        list.add(int.class, "getHttpPort");
        list.add(int.class, "getMgmtPort");
        list.add(int.class, "getMinFreeSpace");
        list.add(String.class, "getPass4SymmKey");
        list.add(String.class, "getServerName");
        list.add(String.class, "getSessionTimeout");
        list.add(boolean.class, "getStartWebServer");
        list.add(String.class, "getTrustedIP");
        return list;
    }
}
