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
 * Representation of Fired Alerts.
 */
public class FiredAlert extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The fired alert endpoint.
     */
    FiredAlert(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this alert's actions, or null if not available.
     *
     * @return This alert's actions.
     */
    public String getAction() {
        return getString("actions", null);
    }

    /**
     * Returns this alert's type, or null if not available.
     *
     * @return This alert's type.
     */
    public String getAlertType() {
        return getString("alert_type", null);
    }

    /**
     * Returns this alerts's rendered expiration time, or null if not available.
     * (4.3+)
     *
     * @return This alert's rendered expiration time.
     */
    public String getExpirationTime() {
        return getString("expiration_time_rendered", null);
    }

    /**
     * Returns this alert's saved search name, or null if not available.
     *
     * @return this alert's saved search name.
     */
    public String getSavedSearchName() {
        return getString("savedsearch_name", null);
    }

    /**
     * Returns this alert's severity, on a scale of 1 to 10, with 1 being the
     * highest priority. -1 if value not specified.
     *
     * @return This alert's severity.
     */
    public int getSeverity() {
        return getInteger("severity", -1);
    }

    /**
     * Returns this alert's SID, or null if not available.
     *
     * @return This alerts SID.
     */
    public String getSid() {
        return getString("sid", null);
    }

    /**
     * Returns the number of triggered alerts, or -1 if not specified (4.3+)
     *
     * @return The number of triggered alerts.
     */
    public int getTriggeredAlertCount() {
        return getInteger("triggered_alerts", -1);
    }

    /**
     * Returns this alert's trigger time, or null if not available.
     *
     * @return This alert's trigger time.
     */
    public Date getTriggerTime() {
        return getDateFromEpoch("trigger_time", null);
    }

    /**
     * Returns this alert's rendered trigger time, or null if not available.
     * (4.3+)
     *
     * @return This alert's rendered trigger time.
     */
    public String getTriggerTimeRendered() {
        return getString("trigger_time_rendered", null);
    }

    /**
     * Returned whether or not the result is a digest, or false if not
     * specified (4.3+)
     *
     * @return whether ot nor the result is a digest.
     */
    public boolean isDigestMode() {
        return getBoolean("digest_mode", false);
    }
}
