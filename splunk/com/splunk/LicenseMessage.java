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

public class LicenseMessage extends Entity {
    LicenseMessage(Service service, String path) {
        super(service, path);
    }

    public String getCategory() {
        return getString("category");
    }

    public Date getCreationTime() {
        return getDateFromEpoch("create_time");
    }

    public String getDescription() {
        return getString("description", null);
    }

    public String getPoolId() {
        return getString("pool_id", null);
    }

    public String getSeverity() {
        return getString("severity", null);
    }

    public String getSlaveId() {
        return getString("slave_id", null);
    }

    public String getStackId() {
        return getString("stack_id", null);
    }
}

