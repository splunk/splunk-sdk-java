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
 * Information regarding a service instance.
 */
public class ServiceInfo extends Entity {
    ServiceInfo(Service service) {
        super(service, "server/info");
    }

    /**
     * Returns the service's build number.
     *
     * @return Build number.
     */
    public int getBuild() {
        return getInteger("build");
    }

    /**
     * Returns the service's CPU architecture.
     *
     * @return CPU architecture.
     */
    public String getCpuArch() {
        return getString("cpu_arch");
    }

    /**
     * Returns a GUID identifying the Splunk instance.
     *
     * @return GUID identifying the Splunk instance.
     */
    public String getGuid() {
        return getString("guid");
    }

    /**
     * Returns an array of the service's license keys.
     *
     * @return Array of license keys.
     */
    public String[] getLicenseKeys() {
        return getStringArray("licenseKeys", null);
    }

    /**
     * Returns the service's license signature.
     *
     * @return License signature.
     */
    public String getLicenseSignature() {
        return getString("licenseSignature");
    }

    /**
     * Returns the service's curernt license state.
     *
     * @return License state.
     */
    public String getLicenseState() {
        return getString("licenseState");
    }

    /**
     * Returns the GUID identifying the license master.
     *
     * @return GUID of the license master.
     */
    public String getMasterGuid() {
        return getString("master_guid");
    }

    /**
     * Returns the service's current mode.
     *
     * @return Service mode.
     */
    public String getMode() {
        return getString("mode");
    }

    /**
     * Returns the service's OS build.
     *
     * @return OS build.
     */
    public String getOsBuild() {
        return getString("os_build");
    }

    /**
     * Returns the service's OS version.
     *
     * @return OS version.
     */
    public String getOsVersion() {
        return getString("os_version");
    }

    /**
     * Returns the services server name.
     *
     * @return Server name.
     */
    public String getServerName() {
        return getString("serverName");
    }

    /**
     * Returns the services Splunk version number.
     *
     * @return Splunk version number.
     */
    public String getVersion() {
        return getString("version");
    }

    /**
     * Answers if the service is running under a free license.
     *
     * @return {@code true} if the current license is a free license.
     */
    public boolean isFree() {
        return getBoolean("isFree");
    }

    /**
     * Answers if the service is running under a trial license.
     *
     * @return {@code true} if the current license is a trial license.
     */
    public boolean isTrial() {
        return getBoolean("isTrial");
    }
}
