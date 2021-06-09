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
 * The {@code LicenseSlave} class represents a license slave, which is a member 
 * of one or more license pools. The access a license slave has to license
 * volume is controlled by its license master.
 */
public class LicenseSlave extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The license slave endpoint.
     */
    LicenseSlave(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the label for this license slave.
     *
     * @return The label, or {@code null} if not specified.
     */
    public String getLabel() {
        return getString("label", null);
    }

    /**
     * Returns a list of pool IDs for this license slave.
     *
     * @return The list of pool IDs.
     */
    public String[] getPoolIds() {
        return getStringArray("pool_ids");
    }

    /**
     * Returns a list of stack IDs for this license pool. 
     *
     * @return The list of stack IDs.
     */
    public String[] getStackIds() {
        return getStringArray("stack_ids");
    }
}

