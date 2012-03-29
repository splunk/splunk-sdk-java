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

import java.util.Map;

/**
 * The {@code Settings} class represents configuration information for an instance of Splunk.
 */
public class Settings extends Entity {
    Settings(Service service) {
        super(service, "server/settings");
    }

    /**
     * Returns the fully-qualified path to the directory containing the 
     * default index for this instance of Splunk.
     *
     * @return The path to the Splunk index directory.
     */
    public String getSplunkDB() {
        return getString("SPLUNK_DB");
    }

    /**
     * Returns the fully-qualified path to the Splunk installation directory.
     *
     * @return The path to the Splunk installation directory.
     */
    public String getSplunkHome() {
        return getString("SPLUNK_HOME");
    }

    /**
     * Indicates whether SSL is enabled on the Splunk management port.
     *
     * @return {@code true} if SSL is enabled, {@code false} if not.
     */
    public boolean getEnableSplunkWebSSL() {
        return getBoolean("enableSplunkWebSSL");
    }

    /**
     * Returns the default host name to use for data inputs.
     *
     * @return The host name.
     */
    public String getHost() {
        return getString("host", null);
    }

    /**
     * Returns the port on which Splunk Web is listening for this 
     * instance of Splunk. The port number defaults to 8000. 
     *
     * @return The Splunk Web port number.
     */
    public int getHttpPort() {
        return getInteger("httpport");
    }

    /**
     * Returns the IP address:port number for Splunkd.
     *
     * @return The IP address:port number.
     */
    public int getMgmtPort() {
        return getInteger("mgmtHostPort");
    }

    /**
     * Returns the amount of free disk space that is required for Splunk
     * to continue searching and indexing.
     *
     * @return The required amount of free disk space, in megabytes.
     */
    public int getMinFreeSpace() {
        return getInteger("minFreeSpace");
    }

    /**
     * Returns the string that is prepended to the Splunk symmetric key to
     * generate the final key that used to sign all traffic between master and slave
     * licensers.
     *
     * @return Licenser symmetric key.
     */
    public String getPass4SymmKey() {
        return getString("pass4SymmKey");
    }

    /**
     * Returns the name that is used to identify this Splunk instance for features
     * such as distributed search.
     *
     * @return The name used to identify the Splunk instance.
     */
    public String getServerName() {
        return getString("serverName");
    }

    /**
     * Returns the amount of time before a user session times out.
     *
     * @return The session time-out.
     */
    public String getSessionTimeout() {
        return getString("sessionTimeout");
    }

    /**
     * Indicates whether the instance is configured to start Splunk Web.
     *
     * @return {@code true} if the instance is configured to start Splunk Web, 
     * {@code false} if Splunk Web is disabled.
     */
    public boolean getStartWebServer() {
        return getBoolean("startwebserver");
    }

    /**
     * Returns the IP address of the authenticating proxy.
     *
     * @return The IP address of the authenticating proxy.
     */
    public String getTrustedIP() {
        return getString("trustedIP", null);
    }

    /**
     * Updates the settings entity with the specified arguments.
     *
     * @param args The arguments being updated.
     */
    @Override public void update(Map<String, Object> args) {
        service.post(path + "/settings", args);
        invalidate();
    }
}
