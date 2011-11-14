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

public class LicensePool extends Entity {
    public LicensePool(Service service, String path) {
        super(service, path);
    }

    public String getDescription() {
        return getString("description", null);
    }

    public long getQuota() {
        String option = getString("quota");
        if (option.equals("MAX"))
            return Long.MAX_VALUE; //UNDONE: correct interpretation?
        return getLong("quota");
    }

    public String[] getSlaves() {
        return getStringArray("slaves");
    }

    public long getSlavesUsageBytes() {
        return getLong("salves_usage_bytes", 0);
    }

    public String getStackId() {
        return getString("stack_id", null);
    }

    public long getUsedBytes() {
        return getLong("used_bytes", 0);
    }
}
