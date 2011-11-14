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

public class License extends Entity {
    public License(Service service, String path) {
        super(service, path);
    }

    public Date getCreationTime() {
        return getDateFromEpoch("creation_time");
    }

    public Date getExpirationTime() {
        return getDateFromEpoch("expiration_time");
    }

    public String[] getFeatures() {
        return getStringArray("features");
    }

    public String getGroupId() {
        return getString("group_id", null);
    }

    public String getLabel() {
        return getString("label", null);
    }

    public String getLicenseHash() {
        return getString("license_hash");
    }

    public int getMaxViolations() {
        return getInteger("max_violations");
    }

    public long getQuota() {
        return getLong("quota");
    }

    public String[] getSourceTypes() {
        return getStringArray("sourcetypes", null);
    }

    public String getStackId() {
        return getString("stack_id");
    }

    public String getStatus() {
        return getString("status");
    }

    public String getType() {
        return getString("type");
    }

    public int getWindowPeriod() {
        return getInteger("window_period");
    }
}

