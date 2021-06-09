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
* The {@code LicenseGroup} class represents a license group, which is a
* collection of one or more license stacks. 
*/
public class LicenseGroup extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The license group endpoint.
     */
    LicenseGroup(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the stack IDs of the license stacks in this license group.
     *
     * @return The stack IDs, or {@code null} if not specified.
     */
    public String[] getStackIds() {
        return getStringArray("stack_ids", new String[]{""});
    }

    /**
     * Indicates whether this license group is active.
     *
     * @return {@code true} if this license group is active, {@code false}
     * if not. 
     */
    public boolean isActive() {
        return getBoolean("is_active");
    }

    /**
     * Sets whether this license group is active.
     *
     * @param value {@code true} to set this license group as active,
     * {@code false} for inactive.
     */
    public void setActive(boolean value) {
        setCacheValue("is_active", value);
    }
}
