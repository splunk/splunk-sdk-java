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
 * The {@code LicenseStack} class represents a license stack, which is 
 * a collection of licenses of the same type.
 */
public class LicenseStack extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The license stack endpoint.
     */
    LicenseStack(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the label of this license stack.
     *
     * @return This license stack's label, or {@code null} if not specified.
     */
    public String getLabel() {
        return getString("label", null);
    }

    /**
     * Returns the combined daily indexing quota for all of the licenses 
     * in this license stack.
     *
     * @return The daily indexing quota, in bytes.
     */
    public long getQuota() {
        return getByteCount("quota", 0);
    }

    /**
     * Returns the license type of the licenses in this license stack.
     *
     * @return The license type.
     */
    public String getType() {
        return getString("type");
    }
}

