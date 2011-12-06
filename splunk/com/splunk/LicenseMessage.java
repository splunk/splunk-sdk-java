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

/**
 * Representation of License Message.
 */
public class LicenseMessage extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The license group endpoint.
     */
    LicenseMessage(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this license message's category.
     *
     * @return This license message's category.
     */
    public String getCategory() {
        return getString("category");
    }

    /**
     * Returns this license message's creation time.
     *
     * @return This license message's creation time.
     */
    public Date getCreationTime() {
        return getDateFromEpoch("create_time");
    }

    /**
     * Returns this license message's description,, or null if not specified.
     *
     * @return This license message's description.
     */
    public String getDescription() {
        return getString("description", null);
    }

    /**
     * Returns this license message's pool ID, or null if not specified.
     *
     * @return This license message's pool ID.
     */
    public String getPoolId() {
        return getString("pool_id", null);
    }

    /**
     * Returns this license message's severity, or null if not specified.
     *
     * @return This license message's severity.
     */
    public String getSeverity() {
        return getString("severity", null);
    }

    /**
     * Returns this license message's slave ID, or null if not specified.
     *
     * @return this license message's slave ID.
     */
    public String getSlaveId() {
        return getString("slave_id", null);
    }

    /**
     * Returns this license message's stack ID, or null if not specified.
     *
     * @return This license message's stack ID.
     */
    public String getStackId() {
        return getString("stack_id", null);
    }
}

