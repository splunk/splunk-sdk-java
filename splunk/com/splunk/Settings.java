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

public class Settings extends Entity {
    public Settings(Service service) {
        super(service, "server/settings");
    }

    public String getSplunkDB() {
        return getString("SPLUNK_DB");
    }

    public String getSplunkHome() {
        return getString("SPLUNK_HOME");
    }

    public boolean getEnableSplunkWebSSL() {
        return getBoolean("enableSplunkWebSSL");
    }

    public String getHost() {
        return getString("host");
    }

    public int getHttpPort() {
        return getInteger("httpport");
    }

    public int getMgmtPort() {
        return getInteger("mgmtHostPort");
    }

    public int getMinFreeSpace() {
        return getInteger("minFreeSpace");
    }

    public String getPass4SymmKey() {
        return getString("pass4SymmKey");
    }

    public String getServerName() {
        return getString("serverName");
    }

    public String getSessionTimeout() {
        return getString("sessionTimeout");
    }

    public boolean getStartWebServer() {
        return getBoolean("startwebserver");
    }

    public String getTrustedIP() {
        return getString("trustedIP", null);
    }
}

