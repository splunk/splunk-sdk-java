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

public class ServiceInfo extends Entity {
    ServiceInfo(Service service) {
        super(service, "server/info");
    }

    public int getBuild() {
        return getInteger("build");
    }

    public String getCpuArch() {
        return getString("cpu_arch");
    }

    public String getGuid() {
        return getString("guid");
    }

    public String[] getLicenseKeys() {
        return getStringArray("licenseKeys", null);
    }

    public String getLicenseSignature() {
        return getString("licenseSignature");
    }

    public String getLicenseState() {
        return getString("licenseState");
    }

    public String getMasterGuid() {
        return getString("master_guid");
    }

    public String getMode() {
        return getString("mode");
    }

    public String getOsBuild() {
        return getString("os_build");
    }

    public String getOsVersion() {
        return getString("os_version");
    }

    public String getServerName() {
        return getString("serverName");
    }

    public String getVersion() {
        return getString("version");
    }

    public boolean isFree() {
        return getBoolean("isFree");
    }

    public boolean isTrial() {
        return getBoolean("isTrial");
    }
}
