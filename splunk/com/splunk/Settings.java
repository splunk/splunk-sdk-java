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
 * The {@code Settings} class represents configuration information for an
 * instance of Splunk.
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
     * generate the final key that used to sign all traffic between master and
     * slave licensers.
     *
     * @return Licenser symmetric key.
     */
    public String getPass4SymmKey() {
        return getString("pass4SymmKey");
    }

    /**
     * Returns the name that is used to identify this Splunk instance for
     * features such as distributed search.
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
     * Sets the fully-qualified local path to the default index.
     * The default value is {@code $SPLUNK_HOME/var/lib/splunk/defaultdb/db/}.
     *
     * @param path The local path to the default index.
     */
    public void setSplunkDBPath(String path) {
        setCacheValue("SPLUNK_DB", path);
    }

    /**
     * Sets whether Splunk Web uses HTTP or HTTPS. 
     *
     * @param useHttps {@code true} to use SSL and HTTPS, {@code false} to use 
     * HTTP.
     */
    public void setEnableSplunkWebSSL(boolean useHttps) {
        setCacheValue("enableSplunkWebSSL", useHttps);
    }

    /**
     * Sets the default host name to use for data inputs that do not override
     * this setting.
     *
     * @param host The default host name.
     */
    public void setHost(String host) {
        setCacheValue("host", host);
    }

    /**
     * Sets the Splunk Web listening port. If Splunk uses SSL and HTTPS, this 
     * value should be set to the HTTPS port number.
     * <p>
     * <b>Note:</b> The port must be present for Splunk Web to start. If this 
     * value is omitted or set to 0, the server will not start an HTTP listener.
     * @see #getEnableSplunkWebSSL
     *
     * @param port The Splunk Web listening port.
     */
    public void setHttpPort(int port) {
        setCacheValue("httpport", port);
    }

    /**
     * Sets the management port for splunkd.
     * The default value is {@code 8089}.
     *
     * @param port The port for the management interface.
     */
    public void setMgmtPort(int port) {
        setCacheValue("mgmtHostPort", port);
    }

    /**
     * Sets the amount of free disk space that must exist for splunkd to
     * continue operating.
     * <p>
     * Before attempting to run a search, Splunk requires this amount of
     * free space on the file system where the dispatch directory is stored
     * ({@code $SPLUNK_HOME/var/run/splunk/dispatch}).
     *
     * @param minFreeSpace The minimum free space, in megabytes.
     */
    public void setMinimumFreeSpace(int minFreeSpace) {
        setCacheValue("minFreeSpace", minFreeSpace);
    }

    /**
     * Sets the password string that is prepended to the Splunk symmetric key
     * to generate the final key, which is used to sign all traffic between
     * master/slave licensers.
     *
     * @param pass4SymmKey The prepended password string.
     */
    public void setPasswordSymmKey(String pass4SymmKey) {
        setCacheValue("pass4SymmKey", pass4SymmKey);
    }

    /**
     * Sets the name that is used to identify this Splunk instance for features
     * such as distributed search. The default value is
     * {@code <hostname>-<user running splunk>}.
     *
     * @param serverName The server name.
     */
    public void setServerName(String serverName) {
        setCacheValue("serverName", serverName);
    }

    /**
     * Sets the session timeout. 
     * The valid format is <i>number</i> followed by a time unit ("s", "h",
     * or "d").
     *
     * @param sessionTimeout The session timeout value.
     */
    public void setSessionTimeout(String sessionTimeout) {
        setCacheValue("sessionTimeout", sessionTimeout);
    }

    /**
     * Sets whether to start Splunk Web. 
     *
     * @param startwebserver {@code true} to start Splunk Web, {@code false} if
     * not. 
     */
    public void setStartWebServer(boolean startwebserver) {
        setCacheValue("startwebserver", startwebserver);
    }

    /**
     * Sets the IP address of the authenticating proxy. Set this value to a 
     * valid IP address to enable SSO.
     *
     * This attribute is disabled by default. The normal value is "127.0.0.1".
     *
     * @param trustedIP The authenticating proxy's IP address.
     */
    public void setTrustedIP(String trustedIP) {
        setCacheValue("trustedIP", trustedIP);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update(Map<String, Object> args) {
        // Merge cached setters and live args together before updating.
        HashMap<String, Object> mergedArgs = new HashMap<String, Object>();
        mergedArgs.putAll(toUpdate);
        mergedArgs.putAll(args);
        service.post(path + "/settings", mergedArgs);
        toUpdate.clear();
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update() {
        service.post(path + "/settings", toUpdate);
        invalidate();
    }
}
