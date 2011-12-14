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

import java.util.Map;

/**
 * Representation of a saved search.
 */
public class SavedSearch extends Entity {
    /**
     * Constructs an instance of the SavedSearch entity.
     *
     * @param service The service the entity is affiliated with.
     * @param path The resource path.
     */
    SavedSearch(Service service, String path) {
        super(service, path);
    }

    /**
     * Acknowledge the suppression of alerts from this saved search and resume
     * alerting.
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
     * Dispatch (execute) the saved search.
     *
     * @return The search job.
     */
    public Job dispatch() {
        return dispatch(null);
    }

    /**
     * Dispatch (execute) the saved search using the given dispatch args.
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

        // UNDONE: If job == null we should probably throw some kind of 
        // exception indicating a failed dispatch.

        return job;
    }

    /**
     * Returns an array of search jobs created from this saved search.
     *
     * @return An array of search jobs created from this saved search.
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
     * Answers if search results are attached to an email.
     *
     * @return Answer if search results are attached to an email.
     */
    // UNDONE: Shouldn't this be a boolean?
    public String getActionEmailSendResults() {
        return getString("action.email.sendresults", null);
    }

    /**
     * Returns a semicolon delimited list of email recipients.
     *
     * @return Semicolon delimited list of email recipients.
     */
    public String getActionEmailTo() {
        return getString("action.email.to", null);
    }

    /**
     * Returns the period of time to show the alert in the dashboard.
     *
     * @return The period of time to show the alert in the dashboard.
     */
    public String getAlertExpires() {
        return getString("alert.expires");
    }

    /**
     * Returns the alert severity level.
     *
     * @return The alert severity level.
     */
    public int getAlertSeverity() {
        return getInteger("alert.severity");
    }

    /**
     * Answers if alert suppression is enabled for this search.
     *
     * @return Answer if alert suppression is enabled for this search.
     */
    // UNDONE: Shouldn't this be a boolean?
    public String getAlertSuppress() {
        return getString("alert.suppress", null);
    }

    /**
     * Returns the alert suppression period.
     *
     * @return The alert suppression period.
     */
    public String getAlertSuppressPeriod() {
        return getString("alert.suppress.period", null);
    }

    /**
     * Returns alert tracking setting.
     *
     * @return Returns alert tracking setting.
     */
    public String getAlertTrack() {
        return getString("alert.track");
    }

    /**
     * Returns the alert comparator.
     *
     * @return The alert comparator.
     */
    public String getAlertComparator() {
        return getString("alert_comparator", null);
    }

    /**
     * Returns a conditional search that is evaluated against the results of
     * the saved search, defaults to empty.
     *
     * @return A conditional search used against the results of the saved
     *         search.
     */
    public String getAlertCondition() {
        return getString("alert_condition", null);
    }

    /**
     * Returns the value to compare to before triggering the alert action.
     *
     * @return The alert threashold value.
     */
    public String getAlertThreshold() {
        return getString("alert_threshold", null);
    }

    /**
     * Returns what to base the alert on, overridden by {@code alert_condition}.
     * Valid values are: always, custom, number of events, number of hosts and
     * number of sources.
     *
     * @return The alert type.
     */
    public String getAlertType() {
        return getString("alert_type");
    }

    /**
     * Returns the cron schedule for executing this saved search.
     *
     * @return The cron schedule for this saved search.
     */
    public String getCronSchedule() {
        return getString("cron_schedule", null);
    }

    /**
     * Returns a human readable description of this saved search, defaults to
     * empty.
     *
     * @return A description of this saved search.
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
     * Returns the earliest time for this search.
     *
     * @return The earliest time for this search.
     */
    public String getDispatchEarliestTime() {
        return getString("dispatch.earliest_time", null);
    }

    /**
     * Returns the latest time for this search.
     *
     * @return The latest time for this search.
     */
    public String getDispatchLatestTime() {
        return getString("dispatch.latest_time", null);
    }

    /**
     * Answers if lookups are enabled for this search.
     *
     * @return {@code true} if lookups are enabled.
     */
    public boolean getDispatchLookups() {
        return getBoolean("dispatch.lookups");
    }


    /**
     * Returns the maximum number of results before finalizing the search.
     *
     * @return The maximum number of results before finalizing the search.
     */
    public int getDispatchMaxCount() {
        return getInteger("dispatch.max_count");
    }

    /**
     * Returns the maximum amount of time (in seconds) before finalizing the
     * search.
     *
     * @return The maximum amount of time before finalizing the search.
     */
    public String getDispatchMaxTime() {
        return getString("dispatch.max_time");
    }

    /**
     * Returns how frequently Splunk should run the reduce phase on accumulated
     * map values.
     *
     * @return Frequency Splunk should run the reduce phase on accumulated
     *         map values.
     */
    public int getDispatchReduceFreq() {
        return getInteger("dispatch.reduce_freq");
    }

    /**
     * Answers if Splunk spawns a new search process when this saved search
     * is executed.
     *
     * @return {@code true} if Splunk spawns a new process when this saved
     *         search is executed.
     */
    public boolean getDispatchSpawnProcess() {
        return getBoolean("dispatch.spawn_process");
    }

    /**
     * Returns the time format used to specify the earliest and latest times
     * for this search.
     *
     * @return The time format used to specify the earliest and latest times
     *         for this search.
     */
    public String getDispatchTimeFormat() {
        return getString("dispatch.time_format");
    }

    /**
     * Returns the time to live (in seconds) for artifacts of the scheduled
     * search, if no alerts are triggered.
     *
     * @return The time to live for artifacts of the scheduled search.
     */
    public String getDispatchTtl() {
        return getString("dispatch.ttl");
    }

    /**
     * Returns the default UI view in which to load results
     *
     * @return  The default UI view in which to load results.
     */
    public String getDisplayView() {
        return getString("displayview", null);
    }

    /**
     * The maximum number of concurrent instances of this search the scheduler
     * is allowed to run.
     *
     * @return Maximum number of concurrent instances of this search allowed.
     */
    public int getMaxConcurrent() {
        return getInteger("max_concurrent");
    }

    /**
     * Returns the method used by the scheduler to compute the next execution
     * time of a scheduled search.
     *
     * @return The method for computing the next execution time of a scheduled
     *         search.
     */
    public boolean getRealtimeSchedule() {
        return getBoolean("realtime_schedule");
    }

    /**
     * Returns the app this search should be dispatched in.
     *
     * @return the app this search should be dispatched in.
     */
    public String getRequestUiDispatchApp() {
        return getString("request.ui_dispatch_app", null);
    }

    /**
     * Returns the view this search should be displayed in.
     *
     * @return The view this search should be displayed in.
     */
    public String getRequestUiDispatchView() {
        return getString("request.ui_dispatch_view", null);
    }

    /**
     * Answers if a realtime search managed by the scheduler is restarted when
     * a search peer becomes available for this saved search.
     *
     * @return {@code true} if a realtime search is restarted when a new
     *         search peer becomes available.
     */
    public boolean getRestartOnSearchPeerAdd() {
        return getBoolean("restart_on_searchpeer_add");
    }

    /**
     * Answers if this search is run when Splunk starts. If the search is not
     * run on startup, then it runs at the next scheduled time.
     *
     * @return {@code true} if this search is run when Splunk starts.
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
     * Returns thew viewstate id associated with the view named in the
     * {@code displayView} property.
     *
     * @return The viewstate id.
     */
    public String getVsid() {
        return getString("vsid", null);
    }

    /**
     * Answers if the email action is enabled.
     *
     * @return {@code true} if the email action is enabled.
     */
    public boolean isActionEmail() {
        return getBoolean("action.email");
    }

    /**
     * Answers if the populate-lookup action is enabled.
     *
     * @return {@code true} if the populate-lookup action is enabled.
     */
    public boolean isActionPopulateLookup() {
        return getBoolean("action.populate_lookup");
    }

    /**
     * Answers if the RSS action is enabled.
     *
     * @return {@code true} if the RSS action is enabled.
     */
    public boolean isActionRss() {
        return getBoolean("action.rss");
    }

    /**
     * Answers if the script action is enabled.
     *
     * @return {@code true} if the script action is enabled.
     */
    public boolean isActioncScript() {
        return getBoolean("action.script");
    }

    /**
     * Answers if the summary-index action is enabled.
     *
     * @return {@code true} if the summary-index action is enabled.
     */
    public boolean isActionSummaryIndex() {
        return getBoolean("action.summary_index");
    }

    /**
     * Answers if Splunk applies the alert actions to the entire result set
     * or to each individual search result.
     *
     * @return Answer if Splunk applies the alert to the entire result set or
     *         to each individual search result.
     */
    public boolean isDigestMode() {
        return getBoolean("alert.digest_mode");
    }

    /**
     * Answers if this search is run on a schedule.
     *
     * @return {@code true} if this search is run on a schedule.
     */
    public boolean isScheduled() {
        return getBoolean("is_scheduled");
    }

    /**
     * Answers if the search should be visible in the saved search list.
     *
     * @return {@code true} if the search should be visible in the saved search
     *         list.
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
