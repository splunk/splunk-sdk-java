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
* The {@code LicensePool} class represents a license pool, which is made up 
* of a single license master and zero or more license slave instances of Splunk 
* that are configured to use the licensing volume from a set license or license stack.
 */
public class LicensePool extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The license group endpoint.
     */
    LicensePool(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the description of this license pool.
     *
     * @return This description, or {@code null} if not specified.
     */
    public String getDescription() {
        return getString("description", null);
    }

    /**
     * Returns the indexing quota for this license pool.
     *
     * @return A string containing the indexing quota in bytes, or "MAX" to indicate the
     * maximum amount that is allowed.
     */
    public String getQuota() {
        return getString("quota", "0");
    }

    /**
     * Returns the list of slaves for this license pool.
     *
     * @return A comma-separated list of slaves by ID, or {@code null} if not specified.
     */
    public String[] getSlaves() {
        return getStringArray("slaves", null);
    }

    /**
     * Returns the usage of indexing volume by slave licenses in this license pool. 
     *
     * @return The overall license slave usage, in bytes.
     */
    public long getSlavesUsageBytes() {
        return getLong("salves_usage_bytes", 0);
    }

    /**
     * Returns the stack ID for this license pool. Valid values are: download-trial, 
     * enterprise, forwarder, and free.
     *
     * @return The license pool stack ID, or {@code null} if not specified.
     */
    public String getStackId() {
        return getString("stack_id", null);
    }

    /**
     * Returns the usage of indexing volume for this license pool. 
     *
     * @return This license pool's usage, in bytes.
     */
    public long getUsedBytes() {
        return getLong("used_bytes", 0);
    }

    /**
     * Sets whether or not to append slaves. The alternative to being appended
     * is being overwritten.
     *
     * @param appendSlaves A value of {@code true} appends slaves, a value of
     * {@code false} overwrites slaves.
     */
    public void setAppendSlaves(boolean appendSlaves) {
        setCacheValue("append_slaves", appendSlaves);
    }

    /**
     * Sets the description.
     *
     * @param description The description.
     */
    public void setDescription(String description) {
        setCacheValue("description", description);
    }

    /**
     * Sets the byte quota of this pool. Note that values may be specified as:
     *
     * {@code number}, {@code number} followed by {@code MB} or {@code GB}, or
     * finally {@code MAX}. Note that one can only have one pool with MAX size
     * in a stack.
     *
     * @param quota The description.
     */
    public void setQuota(String quota) {
        setCacheValue("quota", quota);
    }

    /**
     * Sets the list of slaves that are members of this pool. One can specify
     * {@code *} to accept all slaves.
     *
     * @param slaves The comma separated list of slaves.
     */
    public void setSlaves(String slaves) {
        setCacheValue("slaves", slaves);
    }
}
