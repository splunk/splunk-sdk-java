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

/**
 * The {@code DistributedPeer} class represents a Splunk distributed peer,
 * providing distributed peer server management.
 */
public class DistributedPeer extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The deployment server endpoint.
     */
    DistributedPeer(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this peer's build number.
     *
     * @return The build number.
     */
    public int getBuild() {
        return getInteger("build");
    }

    /**
     * Returns a list of bundle versions.
     *
     * @return The bundle versions, or {@code null} if not specified.
     */
    public String[] getBundleVersions() {
        return getStringArray("bundle_versions", null);
    }

    /**
     * Returns this peer's GUID.
     *
     * @return The GUID, or {@code null} if not specified.
     */
    public String getGuid() {
        return getString("guid", null);
    }

    /**
     * Returns this peer's license signature.
     *
     * @return The license signature, or {@code null} if not specified.
     */
    public String getLicenseSignature() {
        return getString("licenseSignature", null);
    }

    /**
     * Returns this peer's name.
     *
     * @return The name, or {@code null} if not specified.
     */
    public String getPeerName() {
        return getString("peerName", null);
    }

    /**
     * Returns this peer's type.
     *
     * @return The type, or {@code null} if not specified.
     */
    public String getPeerType() {
        return getString("peerType", null);
    }

    /**
     * Returns this peer's replication status.
     *
     * @return The replication status, or {@code null} if not specified.
     */
    public String getReplicationStatus() {
        return getString("replicationStatus", null);
    }

    /**
     * Return this peer's overall status.
     *
     * @return The overall status, or {@code null} if not specified.
     */
    public String getStatus() {
        return getString("status", null);
    }

    /**
     * Returns this peer's version.
     *
     * @return The version, or {@code null} if not specified.
     */
    public String getVersion() {
        return getString("version", null);
    }

    /**
     * Indicates whether this peer is using HTTPS.
     *
     * @return {@code true} if this peer is using HTTPS, {@code false} if not.
     */
    public boolean isHttps() {
        return getBoolean("is_https", true);
    }

    /**
     * Sets the remote password. 
     * <p>
     * <b>Note:</b> The username and password must be set at the same time.
     * @see #setRemoteUsername
     *
     * @param password The remote password.
     */
    public void setRemotePassword(String password) {
        setCacheValue("remotePassword", password);
    }

    /**
     * Sets the remote username. 
     * <p>
     * <b>Note:</b> The username and password must be set at the same time.
     * @see #setRemotePassword
     *
     * @param username The remote username.
     */
    public void setRemoteUsername(String username) {
        setCacheValue("remoteUsername", username);
    }
}

