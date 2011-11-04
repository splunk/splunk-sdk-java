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

import java.util.List;

public class LicenseStack extends Entity {
    public LicenseStack(Service service, String path) {
        super(service, path);
    }

    public String getLabel() {
        return getString("label", null);
    }

    public List<String> getPoolIds() {
        return (List<String>)getContent().get("pool_ids");
    }

    public long getQuota() {
        return getLong("quota", 0);
    }

    public List<String> getStackIds() {
        return (List<String>)getContent().get("stack_ids");
    }
}

