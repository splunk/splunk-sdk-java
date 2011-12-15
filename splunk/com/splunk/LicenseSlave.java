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
 * Representation of a license slave.
 */
public class LicenseSlave extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The license slave endpoint.
     */
    LicenseSlave(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this license slave's label, or null if not specified.
     *
     * @return This license slave's label.
     */
    public String getLabel() {
        return getString("label", null);
    }

    /**
     * Returns this license slave's list of pool IDs.
     *
     * @return This license slave's list of pool IDs.
     */
    public String[] getPoolIds() {
        return getStringArray("pool_ids");
    }

    /**
     * Returns this license slave's list of stack IDs.
     *
     * @return This license slave's list of stack IDs.
     */
    public String[] getStackIds() {
        return getStringArray("stack_ids");
    }
}

