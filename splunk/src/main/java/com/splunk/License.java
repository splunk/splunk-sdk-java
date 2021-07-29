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

import java.util.Date;

/**
 * The {@code License} class represents a license, providing access to the
 * licenses for this Splunk instance. Splunk licenses specify how much data 
 * you can index per calendar day (from midnight to midnight by the clock on
 * the license master).
 */
public class License extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The license endpoint.
     */
    License(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the time and date the license was created.
     *
     * @return The creation time and date.
     */
    public Date getCreationTime() {
        return getDate("creation_time");
    }

    /**
     * Returns the time and date this license expires.
     *
     * @return The expiration time and date.
     */
    public Date getExpirationTime() {
        return getDate("expiration_time");
    }

    /**
     * Returns a list of enabled features for this license.
     *
     * @return The feature list.
     */
    public String[] getFeatures() {
        return getStringArray("features");
    }

    /**
     * Returns the group ID for this license.
     *
     * @return The license group ID, or {@code null} if not available.
     */
    public String getGroupId() {
        return getString("group_id", null);
    }

    /**
     * Returns the label for this license.
     *
     * @return This license label, or {@code null} if not available.
     */
    public String getLabel() {
        return getString("label", null);
    }

    /**
     * Returns the hash value for this license.
     *
     * @return The license hash value.
     */
    public String getLicenseHash() {
        return getString("license_hash");
    }

    /**
     * Returns the maximum number of violations allowed for this license. 
     * A violation occurs when you exceed the maximum indexing volume allowed
     * for your license. Exceeding the maximum violations will disable search. 
     *
     * @return The maximum number of license violations. 
     */
    public int getMaxViolations() {
        return getInteger("max_violations");
    }

    /**
     * Returns the daily indexing quota, which is the maximum bytes per day 
     * of indexing volume for this license.
     *
     * @return The daily indexing quota, in bytes.
     */
    public long getQuota() {
        return getByteCount("quota");
    }

    /**
     * Returns the source types that, when indexed, count against the indexing 
     * volume for this license. All source types are allowed if none 
     * are explicitly specified.
     *
     * @return The license source types, or {@code null} if not specified.
     */
    public String[] getSourceTypes() {
        return getStringArray("sourcetypes", null);
    }

    /**
     * Returns the stack ID for this license.
     *
     * @return The license stack ID.
     */
    public String getStackId() {
        return getString("stack_id");
    }

    /**
     * Returns the status of this license.
     *
     * @return The license status.
     */
    public String getStatus() {
        return getString("status");
    }

    /**
     * Returns the license type.
     *
     * @return The license type.
     */
    public String getType() {
        return getString("type");
    }

    /**
     * Returns the number of days remaining in the rolling time window 
     * for this license. A license violation occurs when you have 
     * exceeded the number of allowed warnings within this period of 
     * time. 
     *
     * @return The number of days in the rolling window.
     */
    public int getWindowPeriod() {
        return getInteger("window_period");
    }
}

