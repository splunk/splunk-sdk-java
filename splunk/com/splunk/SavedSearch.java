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

public class SavedSearch extends Entity {
    public SavedSearch(Service service, String path) {
        super(service, path);
    }

    public String getActionEmailSendResults() {
        return getString("action.email.sendresults", null);
    }

    public String getActionEmailTo() {
        return getString("action.email.to", null);
    }

    public String getAlertExpires() {
        return getString("alert.expires");
    }

    public int getAlertSeverity() {
        return getInteger("alert.severity");
    }

    public String getAlertSuppress() {
        return getString("alert.suppress", null);
    }

    public String getAlertSuppressPeriod() {
        return getString("alert.suppress.period", null);
    }

    public String getAlertTrack() {
        return getString("alert.track");
    }

    public String getAlertComparator() {
        return getString("alert_comparator", null);
    }

    public String getAlertCondition() {
        return getString("alert_condition", null);
    }

    public String getAlertThreshold() {
        return getString("alert_threshold", null);
    }

    public String getAlertType() {
        return getString("alert_type");
    }

    public String getCronSchedule() {
        return getString("cron_schedule", null);
    }

    public String getDescription() {
        return getString("description", null);
    }

    public int getDispatchBuckets() {
        return getInteger("dispatch.buckets");
    }

    public String getDispatchEarliestTime() {
        return getString("dispatch.earliest_time", null);
    }

    public String getDispatchLatestTime() {
        return getString("dispatch.latest_time", null);
    }

    public boolean getDispatchLookups() {
        return getBoolean("dispatch.lookups");
    }

    public int getDispatchMaxCount() {
        return getInteger("dispatch.max_count");
    }

    public String getDispatchMaxTime() {
        return getString("dispatch.max_time");
    }

    public int getDispatchReduceFreq() {
        return getInteger("dispatch.reduce_freq");
    }

    public boolean getDispatchSpawnProcess() {
        return getBoolean("dispatch.spawn_process");
    }

    public String getDispatchTimeFormat() {
        return getString("dispatch.time_format");
    }

    public String getDispatchTtl() {
        return getString("dispatch.ttl");
    }

    public String getDisplayView() {
        return getString("displayview", null);
    }

    public int getMaxConcurrent() {
        return getInteger("max_concurrent");
    }

    public String getNextScheduledTime() {
        return getString("next_scheduled_time", null);
    }

    public String getQualifiedSearch() {
        return getString("qualifiedSearch");
    }

    public boolean getRealtimeSchedule() {
        return getBoolean("realtime_schedule");
    }

    public String getRequestUiDispatchApp() {
        return getString("request.ui_dispatch_app", null);
    }

    public String getRequestUiDispatchView() {
        return getString("request.ui_dispatch_view", null);
    }

    public boolean getRestartOnSearchPeerAdd() {
        return getBoolean("restart_on_searchpeer_add");
    }

    public boolean getRunOnStartup() {
        return getBoolean("run_on_startup");
    }

    public String getSearch() {
        return getString("search");
    }

    public String getVsid() {
        return getString("vsid", null);
    }

    public boolean isActionEmail() {
        return getBoolean("action.email");
    }

    public boolean isActionPopulateLookup() {
        return getBoolean("action.populate_lookup");
    }

    public boolean isActionRss() {
        return getBoolean("action.rss");
    }

    public boolean isActioncScript() {
        return getBoolean("action.script");
    }

    public boolean isActionSummaryIndex() {
        return getBoolean("action.summary_index");
    }

    public boolean isDigestMode() {
        return getBoolean("alert.digest_mode");
    }

    public boolean isDisabled() {
        return getBoolean("disabled");
    }

    public boolean isScheduled() {
        return getBoolean("is_scheduled");
    }

    public boolean isVisible() {
        return getBoolean("is_visible");
    }

}

