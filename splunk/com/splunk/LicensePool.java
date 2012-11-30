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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* The {@code LicensePool} class represents a license pool, which is made up 
* of a single license master and zero or more license slave instances of Splunk 
* that are configured to use the licensing volume from a set license or license
 * stack.
 */
public class LicensePool extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The license pool endpoint.
     */
    LicensePool(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the description of this license pool.
     *
     * @return The description, or {@code null} if not specified.
     */
    public String getDescription() {
        return getString("description", null);
    }

    /**
     * Returns the indexing quota for this license pool.
     *
     * @return A string containing the indexing quota in bytes, or "MAX" to
     * indicate the maximum amount that is allowed.
     */
    public String getQuota() {
        return getString("quota", "0");
    }

    /**
     * Returns the list of slaves for this license pool.
     *
     * @return A comma-separated list of slaves by ID, or {@code null} if not
     * specified.
     */
    public String[] getSlaves() {
        if (toUpdate.containsKey("slaves")) {
            String value = (String)toUpdate.get("slaves");
            return value.split(",");
        }
        else {
            return getStringArray("slaves", null);
        }
    }

    /**
     * Returns the usage of indexing volume by slave licenses in this license
     * pool.
     *
     * @return A map from each slave GUID to the number of bytes it is using.
     */
    public Map<String, Long> getSlavesUsageBytes() {
        @SuppressWarnings("unchecked")
        HashMap<String, Object> values = (HashMap<String, Object>)get("slaves_usage_bytes");
        if (values == null) {
            values = new HashMap<String, Object>();
        }
        
        HashMap<String, Long> usageBytes = new HashMap<String, Long>();
        
        for(String key : values.keySet()) {
            String value = (String)values.get(key);
            usageBytes.put(key, Long.parseLong(value));
        }
        
        return usageBytes;
    }

    /**
     * Returns the stack ID for this license pool. Valid values are:
     * <p><ul>
     * <li>"download-trial"</li>
     * <li>"enterprise"</li>
     * <li>"forwarder"</li>
     * <li>"free"</li></ul>
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
     * Sets whether to append or overwrite slaves to this license pool.
     *
     * @param appendSlaves {@code true} to append slaves, {@code false} to 
     * overwrite slaves.
     */
    public void setAppendSlaves(boolean appendSlaves) {
        setCacheValue("append_slaves", appendSlaves);
    }

    /**
     * Sets the description of this license pool.
     *
     * @param description The description.
     */
    public void setDescription(String description) {
        setCacheValue("description", description);
    }

    /**
     * Sets the byte quota of this license pool.
     *
     * @param quota The indexing quota of this license pool, specified as:
     * <ul><li><i>number</i></li>
     * <li><i>number</i> followed by "MB" or "GB" (for example, "10GB")</li>
     * <li>"MAX" (Only one license pool can have "MAX" size in a stack.)</li>
     * </ul>
     */
    public void setQuota(String quota) {
        setCacheValue("quota", quota);
    }

    /**
     * Sets the list of slaves that are members of this license pool.
     *
     * @param slaves The comma-separated list of slaves. Use an asterisk ("*") 
     * to accept all slaves.
     */
    public void setSlaves(String slaves) {
        setCacheValue("slaves", slaves);
    }

    /**
     * Sets the list of slaves that are members of this license pool.
     *
     * @param slaves The array of slaves. To accept all slaves, use an 
     * array with a single asterisk element ("*").
     */
    public void setSlaves(String[] slaves) {
        setSlaves(Util.join(",", slaves));
    }
}
