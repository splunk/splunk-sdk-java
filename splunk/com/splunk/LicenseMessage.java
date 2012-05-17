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

import java.util.Date;

/**
 * The {@code LicenseMessage} class represents a license message.
 * Messages may range from helpful warnings about being close to violations 
 * or licenses expiring, to more severe alerts regarding overages and exceeding 
 * the daily indexing volume limit. 
 */
public class LicenseMessage extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The license group endpoint.
     */
    LicenseMessage(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the category of this license message.
     *
     * @return The category.
     */
    public String getCategory() {
        return getString("category");
    }

    /**
     * Returns the time and date that this license message was created.
     *
     * @return The creation time and date.
     */
    public Date getCreationTime() {
        return getDate("create_time");
    }

    /**
     * Returns the description of this license message.
     *
     * @return The description, or {@code null} if not specified.
     */
    public String getDescription() {
        return getString("description", null);
    }

    /**
     * Returns the pool ID of this license message.
     *
     * @return The pool ID, or {@code null} if not specified.
     */
    public String getPoolId() {
        return getString("pool_id", null);
    }

    /**
     * Returns the severity of this license message.
     *
     * @return The severity, or {@code null} if not specified.
     */
    public String getSeverity() {
        return getString("severity", null);
    }

    /**
     * Returns the slave ID of this license message.
     *
     * @return The slave ID, or {@code null} if not specified.
     */
    public String getSlaveId() {
        return getString("slave_id", null);
    }

    /**
     * Returns the stack ID of this license message.
     *
     * @return The stack ID, or {@code null} if not specified.
     */
    public String getStackId() {
        return getString("stack_id", null);
    }
}

