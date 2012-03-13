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
 * Representation of a License Group.
 */
public class LicenseGroup extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The license group endpoint.
     */
    LicenseGroup(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this license group's stack ID, or null if not specified.
     *
     * @return This license group's stack ID.
     */
    public String[] getStackIds() {
        return getStringArray("stack_ids", new String[]{""});
    }

    /**
     * Returns whether or not this license group is active.
     *
     * @return Whether or not this license group is active.
     */
    public boolean isActive() {
        return getBoolean("is_active");
    }
}
