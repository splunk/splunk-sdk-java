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

import java.util.Map;

/**
 * The {@code SavedSearch} class represents a saved search.
 */
public class SavedSearch extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The resource path.
     */
    SavedSearch(Service service, String path) {
        super(service, path);
    }

    /**
     * Acknowledges the suppression of alerts from this saved search
     * and resumes alerting.
     */
    public void acknowledge() {
        service.post(actionPath("acknowledge"));
        invalidate();
    }

    /** {@inheritDoc} */
    @Override protected String actionPath(String action) {
        if (action.equals("acknowledge"))
            return path + "/acknowledge";
        if (action.equals("dispatch"))
            return path + "/dispatch";
        if (action.equals("history"))
            return path + "/history";
        return super.actionPath(action);
    }

    /**
     * Runs the saved search.
     *
     * @return The search job.
     */
    public Job dispatch() {
        return dispatch(null);
    }

    /**
     * Runs the saved search using dispatch arguments.
     *
     * @param args Dispatch arguments.
     * @return The search job.
     */
    // CONSIDER: We should take an optional maxWait argument
    // CONSIDER: Another alternative is to return a MaybeJob object that
    // has an "exists" property, so the user can call maybeJob.refresh()
    // until exists == true.
    public Job dispatch(Map args) {
        ResponseMessage response = service.post(actionPath("dispatch"), args);
        invalidate();
        String sid = Job.getSid(response);

        // The sad fact here is that the search job does not immediately show
        // up once the saved search is dispatched, and we may therefore have 
        // to wait a while in order to return the search job.

        Job job = null;
        JobCollection jobs = service.getJobs();
        for (int retry = 5; retry > 0; --retry) {
            jobs.refresh();
            job = jobs.get(sid);
            if (job != null) break;
            try { Thread.sleep(1000); } 
            catch (InterruptedException e) {}; 
        }

        return job;
    }

    /**
     * Returns an array of search jobs created from this saved search.
     *
     * @return An array of search jobs.
     */
    public Job[] history() {
        ResponseMessage response = service.get(actionPath("history"));
        AtomFeed feed = AtomFeed.parse(response.getContent());
        int count = feed.entries.size();
        Job[] result = new Job[count];
        for (int i = 0; i < count; ++i) {
            String sid = feed.entries.get(i).title;
            result[i] = new Job(service, "search/jobs/" + sid);
        }
        return result;
    }

    /**
     * Indicates whether search results are attached to an email.
     *
     * @return {@code true} if search results are attached to an email,
     * {@code false} if not.
     */
    public String getActionEmailSendResults() {
        return getString("action.email.sendresults", null);
    }

    /**
     * Returns a list of email recipients.
     *
     * @return A semicolon-delimited list of email recipients.
     */
    public String getActionEmailTo() {
        return getString("action.email.to", null);
    }

    /**
     * Returns the amount of time to show the alert in the dashboard.
     *
     * @return The amount of time, in [number][time-unit] format. For example,
     * "24h".
     */
    public String getAlertExpires() {
        return getString("alert.expires");
    }

    /**
     * Returns the alert severity level. The severity levels are:
     * 1 (debug), 2 (info), 3 (warn), 4 (error), 5 (severe), and 6 (fatal).
     *
     * @return The alert severity level as a value from 1-6.
     */
    public int getAlertSeverity() {
        return getInteger("alert.severity");
    }

    /**
     * Indicates whether alert suppression is enabled for this search.
     *
     * @return {@code true} if alert suppression is enabled for this
     * search, {@code false} if not.
     */
    public String getAlertSuppress() {
        return getString("alert.suppress", null);
    }

    /**
     * Returns the alert suppression period, which is only valid if
     * {@code AlertSuppress} is enabled.
     * @see #getAlertSuppress
     *
     * @return The alert suppression period, in [number][time-unit] format. 
     * For example, "24h".
     */
    public String getAlertSuppressPeriod() {
        return getString("alert.suppress.period", null);
    }

    /**
     * Returns a value that indicates how to track the actions triggered
     * by this saved search. Valid values are:  {@code true} (force tracking),
     * {@code false} (disable tracking), and {@code auto} (tracking is based on
     * the setting of each action). 
     *
     * @return Returns the alert tracking setting.
     */
    public String getAlertTrack() {
        return getString("alert.track");
    }

    /**
     * Returns the alert comparator. Valid values are:
     * {@code greater than}, {@code less than}, {@code equal to}, {@code rises by}, 
     * {@code drops by}, {@code rises by perc}, and {@code drops by perc}.
     *
     * @return The alert comparator.
     */
    public String getAlertComparator() {
        return getString("alert_comparator", null);
    }

    /**
     * Returns a conditional search that is evaluated against the results of
     * the saved search.
     *
     * @return A conditional search string.
     */
    public String getAlertCondition() {
        return getString("alert_condition", null);
    }

    /**
     * Returns the value to compare to before triggering the alert action.
     * Valid values are: Integer[%]? 
     *
     * @return The alert threshold value.
     */
    public String getAlertThreshold() {
        return getString("alert_threshold", null);
    }

    /**
     * Returns a value that indicates what to base the alert on.
     * Valid values are: {@code always}, {@code custom}, {@code number of events}, 
     * {@code number of hosts}, and {@code number of sources}. This value is 
     * overridden by {@code AlertCondition} if specified.
     * @see #getAlertCondition
     *
     * @return The alert type.
     */
    public String getAlertType() {
        return getString("alert_type");
    }

    /**
     * Returns the cron schedule for running this saved search.
     *
     * @return The cron string.
     */
    public String getCronSchedule() {
        return getString("cron_schedule", null);
    }

    /**
     * Returns a human-readable description of this saved search.
     *
     * @return A description of the saved search.
     */
    public String getDescription() {
        return getString("description", null);
    }

    /**
     * Returns the maximum number of timeline buckets.
     *
     * @return The maximum number of timeline buckets.
     */
    public int getDispatchBuckets() {
        return getInteger("dispatch.buckets");
    }

    /**
     * Returns the earliest time for this search. This value can be a relative 
     * or absolute time (as formatted by {@code DispatchTimeFormat}).
     * @see #getDispatchTimeFormat
     *
     * @return The earliest time for this search.
     */
    public String getDispatchEarliestTime() {
        return getString("dispatch.earliest_time", null);
    }

    /**
     * Returns the latest time for this search. This value can be a relative 
     * or absolute time (as formatted by {@code DispatchTimeFormat}).
     * @see #getDispatchTimeFormat
     *
     * @return The latest time for this search.
     */
    public String getDispatchLatestTime() {
        return getString("dispatch.latest_time", null);
    }

    /**
     * Indicates whether look-ups are enabled for this search.
     *
     * @return {@code true} if look-ups are enabled, {@code false} if not.
     */
    public boolean getDispatchLookups() {
        return getBoolean("dispatch.lookups");
    }


    /**
     * Returns the maximum number of results before finalizing the search.
     *
     * @return The maximum number of results.
     */
    public int getDispatchMaxCount() {
        return getInteger("dispatch.max_count");
    }

    /**
     * Returns the maximum amount of time before finalizing the search.
     *
     * @return The maximum amount of time, in seconds.
     */
    public String getDispatchMaxTime() {
        return getString("dispatch.max_time");
    }

    /**
     * Returns how frequently Splunk should run the MapReduce reduce phase 
     * on accumulated map values.
     *
     * @return The reduce frequency.
     */
    public int getDispatchReduceFreq() {
        return getInteger("dispatch.reduce_freq");
    }

    /**
     * Indicates whether Splunk spawns a new search process when this saved search
     * is executed.
     *
     * @return {@code true} if Splunk spawns a new process when this saved search
     * is executed, {@code false} if not.
     */
    public boolean getDispatchSpawnProcess() {
        return getBoolean("dispatch.spawn_process");
    }

    /**
     * Returns the time format used to specify the earliest and latest times
     * for this search.
     *
     * @return The time format.
     */
    public String getDispatchTimeFormat() {
        return getString("dispatch.time_format");
    }

    /**
     * Returns the time to live for artifacts of the scheduled search (the time
     * before the search job expires and artifacts are still available), 
     * if no alerts are triggered.
     * If the integer is followed by the letter 'p', Splunk interprets the time
     * to live as a multiple of the scheduled search period. 
     *
     * @return The time to live, in seconds or as a multiple of the scheduled
     * search period.
     */
    public String getDispatchTtl() {
        return getString("dispatch.ttl");
    }

    /**
     * Returns the default UI view in which to load results.
     *
     * @return  The view name.
     */
    public String getDisplayView() {
        return getString("displayview", null);
    }

    /**
     * Returns the maximum number of concurrent instances of this search 
     * the scheduler is allowed to run.
     *
     * @return The maximum number of concurrent instances that are allowed.
     */
    public int getMaxConcurrent() {
        return getInteger("max_concurrent");
    }

    /**
     * Indicates whether the scheduler computes the next run time of a scheduled search
     * based on the current time or on the last search run time (for continuous 
     * scheduling).
     *
     * @return {@code true} if the run time is based on current time, {@code false} 
     * if based on the previous search time.
     */
    public boolean getRealtimeSchedule() {
        return getBoolean("realtime_schedule");
    }

    /**
     * Returns the app this search should be dispatched in.
     *
     * @return The app name.
     */
    public String getRequestUiDispatchApp() {
        return getString("request.ui_dispatch_app", null);
    }

    /**
     * Returns the view this search should be displayed in.
     *
     * @return The view name.
     */
    public String getRequestUiDispatchView() {
        return getString("request.ui_dispatch_view", null);
    }

    /**
     * Indicates whether a real-time search managed by the scheduler is restarted when
     * a search peer becomes available for this saved search.
     *
     * @return {@code true} if a real-time search is restarted, {@code false} if not.
     */
    public boolean getRestartOnSearchPeerAdd() {
        return getBoolean("restart_on_searchpeer_add");
    }

    /**
     * Indicates whether this search is run when Splunk starts. If the search is not
     * run on startup, it runs at the next scheduled time.
     *
     * @return {@code true} if this search is run when Splunk starts, {@code false}
     * if not.
     */
    public boolean getRunOnStartup() {
        return getBoolean("run_on_startup");
    }

    /**
     * Returns the search expression for this saved search.
     *
     * @return The search expression.
     */
    public String getSearch() {
        return getString("search");
    }

    /**
     * Returns the view state ID that is associated with the view specified in the
     * {@code DisplayView} property. This ID corresponds to a stanza in the 
     * viewstates.conf configuration file.
     * @see #getDisplayView
     *
     * @return The view state ID.
     */
    public String getVsid() {
        return getString("vsid", null);
    }

    /**
     * Indicates whether the email action is enabled.
     *
     * @return {@code true} if the email action is enabled, {@code false} if not.
     */
    public boolean isActionEmail() {
        return getBoolean("action.email");
    }

    /**
     * Indicates whether the populate-lookup action is enabled.
     *
     * @return {@code true} if the populate-lookup action is enabled, {@code false} if not.
     */
    public boolean isActionPopulateLookup() {
        return getBoolean("action.populate_lookup");
    }

    /**
     * Indicates whether the RSS action is enabled.
     *
     * @return {@code true} if the RSS action is enabled, {@code false} if not.
     */
    public boolean isActionRss() {
        return getBoolean("action.rss");
    }

    /**
     * Indicates whether the script action is enabled.
     *
     * @return {@code true} if the script action is enabled, {@code false} if not.
     */
    public boolean isActioncScript() {
        return getBoolean("action.script");
    }

    /**
     * Indicates whether the summary-index action is enabled.
     *
     * @return {@code true} if the summary-index action is enabled, {@code false} if not.
     */
    public boolean isActionSummaryIndex() {
        return getBoolean("action.summary_index");
    }

    /**
     * Indicates whether Splunk applies the alert actions to the entire result set 
     * (digest) or to each individual search result (per result).
     *
     * @return {@code true} if actions are applied per digest, 
     * {@code false} if per result.
     */
    public boolean isDigestMode() {
        return getBoolean("alert.digest_mode");
    }

    /**
     * Indicates whether this search is run on a schedule.
     *
     * @return {@code true} if this search is run on a schedule, {@code false} if not.
     */
    public boolean isScheduled() {
        return getBoolean("is_scheduled");
    }

    /**
     * Indicates whether the search should be visible in the saved search list.
     *
     * @return {@code true} if the search should be listed, {@code false} if not.
     */
    public boolean isVisible() {
        return getBoolean("is_visible");
    }

    /** {@inheritDoc} */
    @Override public void update(Map<String, Object> args) {
        // Updates to a saved search *require* that the search string be 
        // passed, so add the current search string here if the value wasn't
        // passed in by the caller.
        if (!args.containsKey("search"))
            args = Args.create(args).add("search", getSearch());
        super.update(args);
    }
}
