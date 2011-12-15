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
 * Representation of a license stack.
 */
public class LicenseStack extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The license stack endpoint.
     */
    LicenseStack(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this license stack's label, or null if not specified.
     *
     * @return This license stack's label.
     */
    public String getLabel() {
        return getString("label", null);
    }

    /**
     * Returns this license stack's daily indexing quota.
     *
     * @return This license stack's daily indexing quota.
     */
    public long getQuota() {
        return getByteCount("quota", 0);
    }

    /**
     * Returns this license stack's type.
     *
     * @return This license stack's type.
     */
    public String getType() {
        return getString("type");
    }
}

