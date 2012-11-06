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
 * The {@code FiredAlert} class represents a fired alert.
 */
public class FiredAlert extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The fired alert endpoint.
     */
    FiredAlert(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this alert's actions (such as notifying by email, running a 
     * script, adding to RSS, tracking in Alert Manager, and enabling 
     * summary indexing). 
     *
     * @return The alert actions, or {@code null} if not available.
     */
    public String[] getAction() {
        return getStringArray("actions", null);
    }

    /**
     * Returns this alert's type.
     *
     * @return The alert type, or {@code null} if not available.
     */
    public String getAlertType() {
        return getString("alert_type", null);
    }

    /**
     * Returns the rendered expiration time for this alert.
     * This method is available in Splunk 4.3 and later.
     *
     * @return This alert's rendered expiration time, or {@code null} if not
     * available.
     */
    public String getExpirationTime() {
        return getString("expiration_time_rendered", null);
    }

    /**
     * Returns the saved search for this alert.
     *
     * @return The saved search name, or {@code null} if not available.
     */
    public String getSavedSearchName() {
        return getString("savedsearch_name", null);
    }

    /**
     * Returns this alert's severity on a scale of 1 to 10, with 1 being the
     * highest severity.
     *
     * @return This alert's severity, or -1 if the value is not specified.
     */
    public int getSeverity() {
        return getInteger("severity", -1);
    }

    /**
     * Returns this alert's search ID (SID).
     *
     * @return This alert's SID, or {@code null} if not available.
     */
    public String getSid() {
        return getString("sid", null);
    }

    /**
     * Returns the count of triggered alerts. 
     * This method is available in Splunk 4.3 and later.
     *
     * @return The number of triggered alerts, or -1 if not specified.
     */
    public int getTriggeredAlertCount() {
        return getInteger("triggered_alerts", -1);
    }

    /**
     * Returns the time this alert was triggered.
     *
     * @return This alert's trigger time, or {@code null} if not available.
     */
    public Date getTriggerTime() {
        return getDate("trigger_time", null);
    }

    /**
     * Returns this alert's rendered trigger time.
     * This method is available in Splunk 4.3 and later.
     *
     * @return This alert's trigger time, or {@code null} if not available.
     */
    public Date getTriggerTimeRendered() {
        return getDate("trigger_time_rendered", null);
    }

    /**
     * Indicates whether the result is a set of events (digest) or a single
     * event (per result).
     *
     * This method is available in Splunk 4.3 and later.
     *
     * @return {@code true} if the result is a digest, {@code false} if per
     * result.
     */
    public boolean isDigestMode() {
        return getBoolean("digest_mode", false);
    }
}
