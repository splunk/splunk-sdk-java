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

public class DistributedPeer extends Entity {
    public DistributedPeer(Service service, String path) {
        super(service, path);
    }

    public String getBuild() {
        return getString("build");
    }

    public String[] getBundleVersions() {
        return getStringArray("bundle_versions", null);
    }

    public String getGuid() {
        return getString("guid");
    }

    public String getLicenseSignature() {
        return getString("licenseSignature");
    }

    public String getPeerName() {
        return getString("peerName");
    }

    public String getPeerType() {
        return getString("peerType");
    }

    public String getReplicationStatus() {
        return getString("replicationStatus");
    }

    public String getStatus() {
        return getString("status");
    }

    public String getVersion() {
        return getString("version");
    }

    public boolean isDisabled() {
        return getBoolean("disabled");
    }

    public boolean isHttps() {
        return getBoolean("is_https");
    }
}

