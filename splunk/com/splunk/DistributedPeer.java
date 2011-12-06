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
 * Representation of the Splunk distributed peer.
 */
public class DistributedPeer extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The deployment server endpoint.
     */
    DistributedPeer(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this peer's build number, or null if not specified.
     *
     * @return This peer's build number.
     */
    public String getBuild() {
        return getString("build", null);
    }

    /**
     * Returns the list of bundle versions, or null if not specified.
     *
     * @return The list of bundle versions.
     */
    public String[] getBundleVersions() {
        return getStringArray("bundle_versions", null);
    }

    /**
     * Returns this peer's GUID, or null if not specified.
     *
     * @return this peer's GUID.
     */
    public String getGuid() {
        return getString("guid", null);
    }

    /**
     * Returns this peer's license signature, or null if not specified.
     *
     * @return This peer's license signature.
     */
    public String getLicenseSignature() {
        return getString("licenseSignature", null);
    }

    /**
     * Returns this peer's name, or null if not specified.
     *
     * @return Thid peer's name.
     */
    public String getPeerName() {
        return getString("peerName", null);
    }

    /**
     * Returns this peer's type, or null if not specified.
     *
     * @return This peer's type.
     */
    public String getPeerType() {
        return getString("peerType", null);
    }

    /**
     * Returns this peer's replication status, or null if not specified.
     *
     * @return This peer's replication status.
     */
    public String getReplicationStatus() {
        return getString("replicationStatus", null);
    }

    /**
     * Return this peer's overall status, or null if not specified.
     *
     * @return This peer's overall status.
     */
    public String getStatus() {
        return getString("status", null);
    }

    /**
     * Returns this peer's version, or null if not specified.
     *
     * @return This peer's version.
     */
    public String getVersion() {
        return getString("version", null);
    }

    /**
     * Returns whether or not this peer is using HTTPS, or null if not
     * specified.
     *
     * @return whether or not this peer is using HTTPS.
     */
    public boolean isHttps() {
        return getBoolean("is_https", true);
    }
}

