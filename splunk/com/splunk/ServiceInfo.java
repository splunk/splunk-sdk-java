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
 * The {@code ServiceInfo} class contains information about a running Splunk 
 * {@code Service} instance (Splunkd).
 */
public class ServiceInfo extends Entity {
    ServiceInfo(Service service) {
        // We have to use an absolute path here, since
        // server/info returns HTTP code 403 if it is
        // used with any namespace specifier besides
        // /services (i.e., don't use of servicesNS).
        super(service, "/services/server/info");
    }

    /**
     * Returns the build number of this Splunk instance.
     *
     * @return The build number.
     */
    public int getBuild() {
        return getInteger("build");
    }

    /**
     * Returns the CPU architecture of this Splunk instance.
     *
     * @return The CPU architecture.
     */
    public String getCpuArch() {
        return getString("cpu_arch");
    }

    /**
     * Returns a GUID identifying this Splunk instance.
     *
     * @return The Splunk instance GUID.
     */
    public String getGuid() {
        return getString("guid");
    }

     /**
      * Returns an array of the service's license labels.
      *
      * @return An array of license labels.
      */
    public String[] getLicenseLabels() {
        return getStringArray("license_labels", null);
    }

    /**
     * Returns an array of the license keys for this Splunk instance.
     *
     * @return An array of license keys.
     */
    public String[] getLicenseKeys() {
        return getStringArray("licenseKeys", null);
    }

    /**
     * Returns the license signature for this Splunk instance.
     *
     * @return The license signature.
     */
    public String getLicenseSignature() {
        return getString("licenseSignature");
    }

    /**
     * Returns the current license state of this Splunk instance.
     *
     * @return The license state.
     */
    public String getLicenseState() {
        return getString("licenseState");
    }

    /**
     * Returns a GUID identifying the license master for this Splunk instance.
     *
     * @return The license master GUID.
     */
    public String getMasterGuid() {
        return getString("master_guid");
    }

    /**
     * Returns the current mode of this Splunk instance.
     *
     * @return The mode.
     */
    public String getMode() {
        return getString("mode");
    }

    /**
     * Returns the OS build of this Splunk instance.
     *
     * @return The OS build.
     */
    public String getOsBuild() {
        return getString("os_build");
    }

    /**
     * Returns the service's OS name (type).
     *
     * @return The OS name.
     */
    public String getOsName() {
        return getString("os_name");
    }

    /**
     * Returns the OS version of this Splunk instance.
     *
     * @return The OS version.
     */
    public String getOsVersion() {
        return getString("os_version");
    }

    /**
     * Returns the server name of this Splunk instance.
     *
     * @return The server name.
     */
    public String getServerName() {
        return getString("serverName");
    }

    /**
     * Returns the version number of this Splunk instance.
     *
     * @return The Splunk version number.
     */
    public String getVersion() {
        return getString("version");
    }

    /**
     * Indicates whether this Splunk instance is running under a free license.
     *
     * @return {@code true} if the current license is a free license, 
     * {@code false} if not.
     */
    public boolean isFree() {
        return getBoolean("isFree");
    }

    /**
     * Indicates whether real-time search is enabled for the service.
     *
     * @return {@code true} if real-time search is enabled, {@code false} if 
     * not.
     */
    public boolean isRtSearchEnabled() {
        return getBoolean("rtsearch_enabled", false);
    }

    /**
     * Indicates whether this Splunk instance is running under a trial license.
     *
     * @return {@code true} if the current license is a trial license, 
     * {@code false} if not.
     */
    public boolean isTrial() {
        return getBoolean("isTrial");
    }
}
