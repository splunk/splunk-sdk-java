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

import java.util.Date;

/**
 * Representation of a license.
 */
public class License extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The license endpoint.
     */
    License(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this license's creation time.
     *
     * @return This license's creation time.
     */
    public Date getCreationTime() {
        return getDateFromEpoch("creation_time");
    }

    /**
     * Returns this license's expiration time.
     *
     * @return This license's expiration time.
     */
    public Date getExpirationTime() {
        return getDateFromEpoch("expiration_time");
    }

    /**
     * Returns this license's list of enabled features.
     *
     * @return This license's list of enabled features.
     */
    public String[] getFeatures() {
        return getStringArray("features");
    }

    /**
     * Returns this license's group ID, or null if not available.
     *
     * @return This license's group ID.
     */
    public String getGroupId() {
        return getString("group_id", null);
    }

    /**
     * Returns this license's label, or null if not available.
     *
     * @return This license's label.
     */
    public String getLabel() {
        return getString("label", null);
    }

    /**
     * Returns this license's hash value.
     *
     * @return This license's hash value.
     */
    public String getLicenseHash() {
        return getString("license_hash");
    }

    /**
     * Returns this license's maximum violation value.
     *
     * @return This license's maximum violation value.
     */
    public int getMaxViolations() {
        return getInteger("max_violations");
    }

    /**
     * Returns this license's ingest data quota.
     *
     * @return This license's ingest data quota.
     */
    public long getQuota() {
        return getByteCount("quota");
    }

    /**
     * Returns this license's sourcetypes, or null if not specified.
     *
     * @return This license's sourcetypes.
     */
    public String[] getSourceTypes() {
        return getStringArray("sourcetypes", null);
    }

    /**
     * Returns this license's stack ID.
     *
     * @return This license's stack ID.
     */
    public String getStackId() {
        return getString("stack_id");
    }

    /**
     * Returns this license's status.
     *
     * @return This license's status.
     */
    public String getStatus() {
        return getString("status");
    }

    /**
     * Returns this license's type.
     *
     * @return This license's type.
     */
    public String getType() {
        return getString("type");
    }

    /**
     * Returns this license's window period.
     *
     * @return This license's window period.
     */
    public int getWindowPeriod() {
        return getInteger("window_period");
    }
}

