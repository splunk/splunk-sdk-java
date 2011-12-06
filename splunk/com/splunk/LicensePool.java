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
 * Representation of License Pool.
 */
public class LicensePool extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The license group endpoint.
     */
    LicensePool(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this license pool's description, or null if not specified.
     *
     * @return This license pool's description.
     */
    public String getDescription() {
        return getString("description", null);
    }

    /**
     * Returns this license pool's ingest quota, in bytes. Note: the return
     * type is a string, because the value can also be a literal "MAX".
     *
     * @return This license pool's data ingest quota.
     */
    public String getQuota() {
        return getString("quota", "0");
    }

    /**
     * Returns this license pool's list of slaves, or null if not specified.
     *
     * @return This license pool's list of slaves.
     */
    public String[] getSlaves() {
        return getStringArray("slaves", null);
    }

    /**
     * Returns this license pool's slave bytes used.
     *
     * @return This license pool's slave bytes used.
     */
    public long getSlavesUsageBytes() {
        return getLong("salves_usage_bytes", 0);
    }

    /**
     * Returns this license pool's stack ID, or null if not specified. The valid
     * values are download-trial, enterprise, forwarder, or free.
     *
     * @return This license pool's stack ID.
     */
    public String getStackId() {
        return getString("stack_id", null);
    }

    /**
     * Returns this license pool's used bytes.
     *
     * @return This license pool's used bytes.
     */
    public long getUsedBytes() {
        return getLong("used_bytes", 0);
    }
}
