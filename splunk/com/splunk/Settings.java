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

/**
 * Representation of configuration information for an instance of Splunk.
 */
public class Settings extends Entity {
    Settings(Service service) {
        super(service, "server/settings");
    }

    /**
     * Returns the fully qualified path to the directory containing the Splunk
     * index directories.
     *
     * @return Path to Splunk index directories.
     */
    public String getSplunkDB() {
        return getString("SPLUNK_DB");
    }

    /**
     * Returns the fully qualified path to the Splunk install directory.
     *
     * @return Path to Splunk install directory.
     */
    public String getSplunkHome() {
        return getString("SPLUNK_HOME");
    }

    /**
     * Answers if SSL is enabled on the Splunk maangement port.
     *
     * @return {@code true} if SSL is enabled.
     */
    public boolean getEnableSplunkWebSSL() {
        return getBoolean("enableSplunkWebSSL");
    }

    /**
     * Returns the service's host name.
     *
     * @return Service host name.
     */
    public String getHost() {
        return getString("host", null);
    }

    /**
     * Returns the splunkweb port number.
     *
     * @return Splunkweb port number.
     */
    public int getHttpPort() {
        return getInteger("httpport");
    }

    /**
     * Returns the splunkd port number.
     *
     * @return Splunkd port number.
     */
    public int getMgmtPort() {
        return getInteger("mgmtHostPort");
    }

    /**
     * Returns the value in megabytes of free disk space required for Splunk
     * to continue operating.
     *
     * @return Megabyes of free disk space required by Splunk.
     */
    public int getMinFreeSpace() {
        return getInteger("minFreeSpace");
    }

    /**
     * Returns the key that is prepended to the splunk symmetric key to
     * generate the final key used to sign all traffic between master & slave
     * licensers.
     *
     * @return Licenser symm key.
     */
    public String getPass4SymmKey() {
        return getString("pass4SymmKey");
    }

    /**
     * Returns the name used to identify this Splunk instance for features
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
     * @return The amount of time before a user session times out.
     */
    public String getSessionTimeout() {
        return getString("sessionTimeout");
    }

    /**
     * Answers if the instance is configured to start splunkweb.
     *
     * @return {@code true} if the instance is configured to start splunkweb.
     */
    public boolean getStartWebServer() {
        return getBoolean("startwebserver");
    }

    /**
     * Returns the IP address of the authenticating proxy.
     *
     * @return IP address of authenticating proxy.
     */
    public String getTrustedIP() {
        return getString("trustedIP", null);
    }
}
