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
     * Returns the alert severity level. The values are
     * {@code 1=DEBUG, 2=INFO, 3=WARN, 4=ERROR, 5=SEVERE, 6=FATAL}.
     *
     * @return The alert severity level.
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
     * {@code greater than}, {@code less than}, {@code equal to},
     * {@code rises by}, {@code drops by}, {@code rises by perc}, and
     * {@code drops by perc}.
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
     * Valid values are: {@code always}, {@code custom},
     * {@code number of events}, {@code number of hosts}, and
     * {@code number of sources}. This value is overridden by
     * {@code AlertCondition} if specified.
     *
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
     * Indicates whether Splunk spawns a new search process when this saved
     * search is executed.
     *
     * @return {@code true} if Splunk spawns a new process when this saved
     * search is executed, {@code false} if not.
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
     * Indicates whether the scheduler computes the next run time of a
     * scheduled search based on the current time or on the last search run
     * time (for continuous scheduling).
     *
     * @return {@code true} if the run time is based on current time,
     * {@code false} if based on the previous search time.
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
     * Indicates whether a real-time search managed by the scheduler is
     * restarted when a search peer becomes available for this saved search.
     *
     * @return {@code true} if a real-time search is restarted,
     * {@code false} if not.
     */
    public boolean getRestartOnSearchPeerAdd() {
        return getBoolean("restart_on_searchpeer_add");
    }

    /**
     * Indicates whether this search is run when Splunk starts. If the search
     * is not run on startup, it runs at the next scheduled time.
     *
     * @return {@code true} if this search is run when Splunk starts,
     * {@code false} if not.
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
     * Returns the view state ID that is associated with the view specified in
     * the {@code DisplayView} property. This ID corresponds to a stanza in the
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
     * @return {@code true} if the email action is enabled, {@code false} if
     * not.
     */
    public boolean isActionEmail() {
        return getBoolean("action.email");
    }

    /**
     * Indicates whether the populate-lookup action is enabled.
     *
     * @return {@code true} if the populate-lookup action is enabled,
     * {@code false} if not.
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
     * @return {@code true} if the script action is enabled, {@code false} if
     * not.
     */
    public boolean isActioncScript() {
        return getBoolean("action.script");
    }

    /**
     * Indicates whether the summary-index action is enabled.
     *
     * @return {@code true} if the summary-index action is enabled,
     * {@code false} if not.
     */
    public boolean isActionSummaryIndex() {
        return getBoolean("action.summary_index");
    }

    /**
     * Indicates whether Splunk applies the alert actions to the entire result
     * set (digest) or to each individual search result (per result).
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
     * @return {@code true} if this search is run on a schedule, {@code false}
     * if not.
     */
    public boolean isScheduled() {
        return getBoolean("is_scheduled");
    }

    /**
     * Indicates whether the search should be visible in the saved search list.
     *
     * @return {@code true} if the search should be listed, {@code false} if
     * not.
     */
    public boolean isVisible() {
        return getBoolean("is_visible");
    }

    /**
     * Sets the password to use when authenticating with the SMTP server.
     * Normally this value will be set when editing the email settings, however
     * you can set a clear text password here and it will be encrypted on the
     * next Splunk restart.
     *
     * @param password The password.
     */
    public void setActionEmailAuthPassword(String password) {
        setCacheValue("action.email.auth_password", password);
    }

    /**
     * Sets the username to use when authenticating with the SMTP server. If
     * this is empty string, no authentication is attempted
     *
     * @param username The username.
     */
    public void setActionEmailAuthUsername(String username) {
        setCacheValue("action.email.auth_username", username);
    }

    /**
     * Sets the BCC email address to use if action.email is enabled.
     *
     * @param bcc The blind carbon copy email address.
     */
    public void setActionEmailBcc(String bcc) {
        setCacheValue("action.email.bcc", bcc);
    }

    /**
     * Sets the CC email address to use if action.email is enabled.
     *
     * @param cc The carbon copy email address.
     */
    public void setActionEmailc(String cc) {
        setCacheValue("action.email.bcc", cc);
    }

    /**
     * Sets the search command (or pipeline) which is responsible for
     * executing the action.
     *
     * Generally the command is a template search pipeline which is realized
     * with values from the saved search. To reference saved search field values
     * wrap them in $, for example to reference the savedsearch name use $name$,
     * to reference the search use $search$.
     *
     * @param command The search command responsible for executing the action.
     */
    public void setActionEmailCommand(String command) {
        setCacheValue("action.email.command", command);
    }

    /**
     * Sets the email format. Valid values are {@code plain, html, raw} or
     * {@code csv}.
     *
     * @param format The search command responsible for executing the action.
     */
    public void setActionEmailFormat(String format) {
        setCacheValue("action.email.format", format);
    }

    /**
     * Sets the email {@code from} name.
     *
     * @param from The name that the email is from.
     */
    public void setActionEmailFrom(String from) {
        setCacheValue("action.email.from", from);
    }

    /**
     * Sets the hostname used in the web link (url) sent in email actions.
     *
     * Valid forms are: {@code hostname} and {@code protocol://hostname:port}.
     *
     * @param hostname used in the web link (url) sent in email actions.
     */
    public void setActionEmailHostname(String hostname) {
        setCacheValue("action.email.hostname", hostname);
    }

    /**
     * Sets whether the search results are contained in the body of the email.
     *
     * @param inline whether the search results are contained in the body of
     * the email.
     */
    public void setActionEmailInline(boolean  inline) {
        setCacheValue("action.email.inline", inline);
    }

    /**
     * Sets the address of the MTA server to be used to send the emails. If not
     * set, defaults to setting in {@code alert_actions.conf}.
     *
     * @param mailServer The mail server address.
     */
    public void setActionEmailMailServer(String  mailServer) {
        setCacheValue("action.email.mailserver", mailServer);
    }

    /**
     * Sets the global maximum number of search results to send when
     * email.action is enabled.
     *
     * @param maxResults The maximum number of events per email.
     */
    public void setActionEmailMaxResults(int  maxResults) {
        setCacheValue("action.email.maxresults", maxResults);
    }

    /**
     * Sets the maximum amount of time the execution of an email action takes
     * before the action is aborted.
     *
     * Valid format is {@code number} followed by one of {@code m, s, h} or
     * {@code d}.
     *
     * @param maxTime The the maximum amount of time the execution of an
     * email action takes before the action is aborted.
     */
    public void setActionEmailMaxTime(String  maxTime) {
        setCacheValue("action.email.maxtime", maxTime);
    }

    /**
     * Sets the name of the view to deliver if {@code sendpdf} is enabled.
     *
     * @param name The the name of the view to deliver.
     */
    public void setActionEmailPdfView(String  name) {
        setCacheValue("action.email.pdfview", name);
    }

    /**
     * Sets the search string to pre-process results before emailing them.
     *
     * @param preprocess The search string to pre-process results before
     * emailing them.
     */
    public void setActionEmailPreProcessResults(String  preprocess) {
        setCacheValue("action.email.preprocess_results", preprocess);
    }

    /**
     * Sets the paper orientation. Valid values are {@code portrait} or
     * {@code landscape}.
     *
     * @param orientation The paper orientation.
     */
    public void setActionEmailReportPaperOrientation(String  orientation) {
        setCacheValue("action.email.reportPaperOrientation", orientation);
    }

    /**
     * Sets the paper size. Valid values are
     * {@code letter, legal, ledger, a2, a3, a4} or {@code a5}.
     *
     * @param size The paper size.
     */
    public void setActionEmailReportPaperSize(String  size) {
        setCacheValue("action.email.reportPaperSize", size);
    }

    /**
     * Sets whether the PDF server is enabled.
     *
     * @param pdfServerEnabled whether the PDF server is enabled.
     */
    public void setActionEmailReportServerEnabled(String  pdfServerEnabled) {
        setCacheValue("action.email.reportServerEnabled", pdfServerEnabled);
    }

    /**
     * Sets the URL of the PDF report server.
     *
     * @param pdfServerUrl the URL of the PDF report server.
     */
    public void setActionEmailReportServerUrl(String  pdfServerUrl) {
        setCacheValue("action.email.reportServerURL", pdfServerUrl);
    }

    /**
     * Sets whether to create and send the results as a PDF.
     *
     * @param sendPdf the URL of the PDF report server.
     */
    public void setActionEmailSendPdf(boolean  sendPdf) {
        setCacheValue("action.email.sendpdf", sendPdf);
    }

    /**
     * Sets whether to attach the search results in the email.
     *
     * @param sendResults the URL of the PDF report server.
     */
    public void setActionEmailSendResults(boolean  sendResults) {
        setCacheValue("action.email.sendresults", sendResults);
    }

    /**
     * Sets the subject line of the email for this report.
     *
     * @param subject The subject line of the email for this report.
     */
    public void setActionEmailSubject(String  subject) {
        setCacheValue("action.email.subject", subject);
    }

    /**
     * Sets the email recipient list. This is a comma or semi-colon separated
     * list.
     *
     * @param to The email recipient list
     */
    public void setActionEmailTo(String  to) {
        setCacheValue("action.email.to", to);
    }

    /**
     * Sets whether the execution of this action signifies a trackable alert.
     *
     * @param trackAlert whether the execution of this action signifies a
     * trackable alert.
     */
    public void setActionEmailTrackAlert(boolean  trackAlert) {
        setCacheValue("action.email.track_alert", trackAlert);
    }

    /**
     * Sets the minimum time-to-live in seconds of the search artifacts if this
     * action is triggered. If the value is predicates the letter {@code p}, the
     * value is not interpreted as seconds, but as periods.
     *
     * @param ttl the minimum time-to-live in seconds of the search artifacts
     * if this action is triggered.
     */
    public void setActionEmailTtl(String  ttl) {
        setCacheValue("action.email.ttl", ttl);
    }

    /**
     * Sets whether to use SSL when communicating with the SMTP server.
     *
     * @param useSsl whether to use SSL when communicating with the SMTP server.
     */
    public void setActionEmailUseSsl(boolean  useSsl) {
        setCacheValue("action.email.use_ssl", useSsl);
    }

    /**
     * Sets whether to use TLS (transport layer security) when communicating
     * with the SMTP server.
     *
     * @param useTls whether to use TLS (transport layer security) when
     * communicating with the SMTP server.
     */
    public void setActionEmailUseTls(boolean  useTls) {
        setCacheValue("action.email.use_tls", useTls);
    }

    /**
     * Sets whether columns should be sorted from least wide to most wide,
     * left to right.
     *
     * Only valid if {@code format=text}.
     *
     * @param widthSortColumns whether columns should be sorted from least wide
     * to most wide, left to right.
     */
    public void setActionEmailWidthSortColumns(boolean  widthSortColumns) {
        setCacheValue("action.email.width_sort_columns", widthSortColumns);
    }

    /**
     * Sets the search command (or pipeline) which is responsible for executing
     * the action.
     *
     * @param command the search command (or pipeline) which is responsible for
     * executing the action.
     */
    public void setActionPopulateLookupCommand(String  command) {
        setCacheValue("action.populate_lookup.command", command);
    }

    /**
     * Sets the Lookup name of path of the lookup to populate
     *
     * @param dest The Lookup name of path of the lookup to populate
     */
    public void setActionPopulateLookupDest(String  dest) {
        setCacheValue("action.populate_lookup.dest", dest);
    }

    /**
     * Sets the hostname used in the web link (url) sent in alert actions.
     *
     * Valid forms are: {@code hostname} and {@code protocol://hostname:port}.
     *
     * @param hostname used in the web link (url) sent in alert actions.
     */
    public void setActionPopulateLookupHostname(String  hostname) {
        setCacheValue("action.populate_lookup.hostname", hostname);
    }

    /**
     * Sets the global maximum number of search results via alerts.
     *
     * @param maxResults The maximum number of events via alerts.
     */
    public void setActionPopulateLookupMaxResults(int  maxResults) {
        setCacheValue("action.populate_lookup.maxresults", maxResults);
    }

    /**
     * Sets the maximum amount of time the execution of an alert
     * before the action is aborted.
     *
     * Valid format is {@code number} followed by one of {@code m, s, h} or
     * {@code d}.
     *
     * @param maxTime The maximum amount of time the execution of an alert
     * before the action is aborted.
     */
    public void setActionPopulateLookupMaxTime(String  maxTime) {
        setCacheValue("action.populate_lookup.maxtime", maxTime);
    }
    /**
     * Sets whether the execution of this action signifies a trackable alert.
     *
     * @param trackAlert whether the execution of this action signifies a
     * trackable alert.
     */
    public void setActionPopulateLookupTrackAlert(boolean  trackAlert) {
        setCacheValue("action.populate_lookup.track_alert", trackAlert);
    }

    /**
     * Sets the minimum time-to-live in seconds of the search artifacts if this
     * action is triggered. If the value is predicates the letter {@code p}, the
     * value is not interpreted as seconds, but as periods.
     *
     * @param ttl the minimum time-to-live in seconds of the search artifacts
     * if this action is triggered.
     */
    public void setActionPopulateLookupTtl(String  ttl) {
        setCacheValue("action.populate_lookup.ttl", ttl);
    }

    /**
     * Sets the search command (or pipeline) which is responsible for executing
     * the action.
     *
     * Generally the command is a template search pipeline which is realized
     * with values from the saved search. To reference saved search field values
     * wrap them in $, for example to reference the savedsearch name use $name$,
     * to reference the search use $search$.
     *
     * @param command the search command (or pipeline) which is responsible
     * for executing the action.
     */
    public void setActionRssCommand(String  command) {
        setCacheValue("action.rss.command", command);
    }

    /**
     * Sets the hostname used in the web link (url) sent in alert actions.
     *
     * Valid forms are: {@code hostname} and {@code protocol://hostname:port}.
     *
     * @param hostname used in the web link (url) sent in alert actions.
     */
    public void setActionRssHostname(String  hostname) {
        setCacheValue("action.rss.hostname", hostname);
    }

    /**
     * Sets the global maximum number of search results via alerts.
     *
     * @param maxResults The maximum number of events via alerts.
     */
    public void setActionRssMaxResults(int  maxResults) {
        setCacheValue("action.rss.maxresults", maxResults);
    }

    /**
     * Sets the maximum amount of time the execution of an alert
     * before the action is aborted.
     *
     * Valid format is {@code number} followed by one of {@code m, s, h} or
     * {@code d}.
     *
     * @param maxTime The maximum amount of time the execution of an alert
     * before the action is aborted.
     */
    public void setActionRssMaxTime(String  maxTime) {
        setCacheValue("action.rss.maxtime", maxTime);
    }

    /**
     * Sets whether the execution of this action signifies a trackable alert.
     *
     * @param trackAlert whether the execution of this action signifies a
     * trackable alert.
     */
    public void setActionRssTrackAlert(boolean  trackAlert) {
        setCacheValue("action.rss.track_alert", trackAlert);
    }

    /**
     * Sets the minimum time-to-live in seconds of the search artifacts if this
     * action is triggered. If the value is predicates the letter {@code p}, the
     * value is not interpreted as seconds, but as periods.
     *
     * @param ttl the minimum time-to-live in seconds of the search artifacts
     * if this action is triggered.
     */
    public void setActionRssTtl(String  ttl) {
        setCacheValue("action.rss.ttl", ttl);
    }

    /**
     * Sets the search command (or pipeline) which is responsible for executing
     * the action.
     *
     * Generally the command is a template search pipeline which is realized
     * with values from the saved search. To reference saved search field values
     * wrap them in $, for example to reference the savedsearch name use $name$,
     * to reference the search use $search$.
     *
     * @param command the search command (or pipeline) which is responsible
     * for executing the action.
     */
    public void setActionScriptCommand(String  command) {
        setCacheValue("action.script.command", command);
    }

    /**
     * Sets the filename of the script to call. Required if script action is
     * enabled.
     *
     * @param filename The filename of the script to call.
     */
    public void setActionScriptFilename(String  filename) {
        setCacheValue("action.script.filename", filename);
    }

    /**
     * Sets the hostname used in the web link (url) sent in alert actions.
     *
     * Valid forms are: {@code hostname} and {@code protocol://hostname:port}.
     *
     * @param hostname used in the web link (url) sent in alert actions.
     */
    public void setActionScriptHostname(String  hostname) {
        setCacheValue("action.script.hostname", hostname);
    }

    /**
     * Sets the global maximum number of search results via alerts.
     *
     * @param maxResults The maximum number of events via alerts.
     */
    public void setActionScriptMaxResults(int  maxResults) {
        setCacheValue("action.script.maxresults", maxResults);
    }

    /**
     * Sets the maximum amount of time the execution of an alert
     * before the action is aborted.
     *
     * Valid format is {@code number} followed by one of {@code m, s, h} or
     * {@code d}.
     *
     * @param maxTime The maximum amount of time the execution of an alert
     * before the action is aborted.
     */
    public void setActionScriptMaxTime(String  maxTime) {
        setCacheValue("action.script.maxtime", maxTime);
    }

    /**
     * Sets whether the execution of this action signifies a trackable alert.
     *
     * @param trackAlert whether the execution of this action signifies a
     * trackable alert.
     */
    public void setActionScriptTrackAlert(boolean  trackAlert) {
        setCacheValue("action.script.track_alert", trackAlert);
    }

    /**
     * Sets the minimum time-to-live in seconds of the search artifacts if this
     * action is triggered. If the value is predicates the letter {@code p}, the
     * value is not interpreted as seconds, but as periods.
     *
     * @param ttl the minimum time-to-live in seconds of the search artifacts
     * if this action is triggered.
     */
    public void setActionScriptTtl(String  ttl) {
        setCacheValue("action.script.ttl", ttl);
    }

    /**
     * Sets the name of the summary index where the results of the scheduled
     * search are saved.
     *
     * @param name The name of the summary index where the results of the
     * scheduled search are saved.
     */
    public void setActionSummaryIndexName(String name) {
        setCacheValue("action.summary_index._name", name);
    }

    /**
     * Sets the search command (or pipeline) which is responsible for executing
     * the action.
     *
     * Generally the command is a template search pipeline which is realized
     * with values from the saved search. To reference saved search field values
     * wrap them in $, for example to reference the savedsearch name use $name$,
     * to reference the search use $search$.
     *
     * @param command the search command (or pipeline) which is responsible
     * for executing the action.
     */
    public void setActionSummaryIndexCommand(String  command) {
        setCacheValue("action.summary_index.command", command);
    }

    /**
     * Sets the hostname used in the web link (url) sent in alert actions.
     *
     * Valid forms are: {@code hostname} and {@code protocol://hostname:port}.
     *
     * @param hostname used in the web link (url) sent in alert actions.
     */
    public void setActionSummaryIndexHostname(String  hostname) {
        setCacheValue("action.summary_index.hostname", hostname);
    }

    /**
     * Sets whether or not to execute the summary indexing action as part of the
     * scheduled search.
     *
     * @param inline whether or not to execute the summary indexing action as
     * part of the scheduled search.
     */
    public void setActionSummaryIndexInline(boolean  inline) {
        setCacheValue("action.summary_index.inline", inline);
    }

    /**
     * Sets the global maximum number of search results via alerts.
     *
     * @param maxResults The maximum number of events via alerts.
     */
    public void setActionSummaryIndexMaxResults(int  maxResults) {
        setCacheValue("action.summary_index.maxresults", maxResults);
    }

    /**
     * Sets the maximum amount of time the execution of an alert
     * before the action is aborted.
     *
     * Valid format is {@code number} followed by one of {@code m, s, h} or
     * {@code d}.
     *
     * @param maxTime The maximum amount of time the execution of an alert
     * before the action is aborted.
     */
    public void setActionSummaryIndexMaxTime(String  maxTime) {
        setCacheValue("action.summary_index.maxtime", maxTime);
    }

    /**
     * Sets whether the execution of this action signifies a trackable alert.
     *
     * @param trackAlert whether the execution of this action signifies a
     * trackable alert.
     */
    public void setActionSummaryIndexTrackAlert(boolean  trackAlert) {
        setCacheValue("action.summary_index.track_alert", trackAlert);
    }

    /**
     * Sets the minimum time-to-live in seconds of the search artifacts if this
     * action is triggered. If the value is predicates the letter {@code p}, the
     * value is not interpreted as seconds, but as periods.
     *
     * @param ttl the minimum time-to-live in seconds of the search artifacts
     * if this action is triggered.
     */
    public void setActionSummaryIndexTtl(String  ttl) {
        setCacheValue("action.summary_index.ttl", ttl);
    }

    /**
     * Sets which actions are enabled.
     *
     * @param actions a comma separated list of enabled actions.
     */
    public void setActions(String actions) {
        setCacheValue("actions", actions);
    }

    /**
     * Sets the wildcard argument that accepts any action.
     *
     * @param action The wildcard argument that accepts any action.
     */
    public void setActionWildcard(String action) {
        setCacheValue("action.*", action);
    }

    /**
     * Sets whether Splunk applies the alert actions to the entire result set or
     * on each individual result.
     *
     * @param digest whether Splunk applies the alert actions to the entire
     * result set or on each individual result.
     */
    public void setAlertDigestMode(boolean digest) {
        setCacheValue("alert.digest_mode", digest);
    }

    /**
     * Sets the period of time to show the alert in the dashboard. Defaults to
     * 24h. Valid values are either a {@code number} which implies seconds, or
     * a {@code number} followed by {@code time-unit}, which are from the set
     * {@code s, h, d, m}.
     *
     * @param period The period of time to show the alert in the dashboard.
     */
    public void setAlertExpires(String period) {
        setCacheValue("alert.expires", period);
    }

    /**
     * Sets the alert severity level. Valid values is an integer from 1 through
     * 6. {@code 1=DEBUG, 2=INFO, 3=WARN, 4=ERROR, 5=SEVERE, 6=FATAL}.
     *
     * @param severity The alert severity level.
     */
    public void setAlertSeverity(int severity) {
        setCacheValue("alert.severity", severity);
    }

    /**
     * Sets whether to alert suppression is enabled for this scheduled search.
     *
     * @param suppress alert suppression is enabled for this scheduled search.
     */
    public void setAlertSuppress(boolean suppress) {
        setCacheValue("alert.suppress", suppress);
    }

    /**
     * Comma delimited list of fields to use for suppression when doing per
     * result alerting. Required if suppression is turned on and per result
     * alerting is enabled.
     *
     * @param fields list of fields to use for suppression.
     */
    public void setAlertSuppressFields(String fields) {
        setCacheValue("alert.suppress.fields", fields);
    }

    /**
     * Sets the suppression period. Only valid if {@code alert.suppress} is
     * enabled.
     *
     * Valid format is {@code number} followed by one of {@code m, s, h} or
     * {@code d}.
     *
     * @param period The suppression period.
     */
    public void setAlertSuppressPeriod(String period) {
        setCacheValue("alert.suppress.period", period);
    }

    /**
     * Sets whether to track the actions triggered by this scheduled search.
     * Valid values are {@code true, false} or {@code auto}.
     *
     * @param track whether to track the actions triggered by this
     * scheduled search.
     */
    public void setAlertTrack(String track) {
        setCacheValue("alert.track", track);
    }

    /**
     * Sets the alert comparator. Valid values are from the set
     * {@code greater than, less than, equal to, rises by, drops by,}
     * @{code rises by perc, drops by perc}.
     *
     * @param comparator The alert comparator
     */
    public void setAlertComparator(String comparator) {
        setCacheValue("alert_comparator", comparator);
    }

    /**
     * Sets a conditional search that is evaluated against the results of the
     * saved search. Defaults to an empty string.
     *
     *  If you specify an alert_condition, do not set {@code counttype},
     *  {@code relation}, or {@code quantity}.
     *
     * @param conditional A conditional search.
     */
    public void setAlertCondition(String conditional) {
        setCacheValue("alert_condition", conditional);
    }

    /**
     * Sets the value to compare before triggering the alert actions. Valid
     * values are in the form {@code number} followed by {@code %}.
     *
     * @param threshold The threshold.
     */
    public void setAlertThreshold(String threshold) {
        setCacheValue("alert_threshold", threshold);
    }

    /**
     * Sets the the trigger on what to base the alert on. Valid values are from
     * the set {@code always, custom, number of events, number of hosts} and
     * {@code number of sources}
     *
     * Note that this can be overwritten by {@code alert_condition}.
     *
     * @param type The alert trigger type.
     */
    public void setAlertType(String type) {
        setCacheValue("alert_type", type);
    }

    /**
     * Sets the wildcard argument that accepts any saved search template
     * argument, such as args.username=foobar when the search is search
     * $username$.
     *
     * @param wildcard The wildcard argument.
     */
    public void setArgsWildcard(String wildcard) {
        setCacheValue("args.*", wildcard);
    }

    /**
     * Sets the cron schedule. The value must be a valid cron schedule string.
     *
     * @param cronSchedule The cron schedule.
     */
    public void setCronSchedule(String cronSchedule) {
        setCacheValue("cron_schedule", cronSchedule);
    }

    /**
     * Sets the description.
     *
     * @param description The description.
     */
    public void setDescription(String description) {
        setCacheValue("description", description);
    }

    /**
     * Sets whether the saved search is enabled or disabled. Note that the
     * supported disabled mechanism, is to use the @{code disable} action.
     *
     * @param disabled {@code true} to disabled the saved search,
     * {@code false} to enable.
     */
    public void setDisabled(boolean disabled) {
        setCacheValue("disabled", disabled);
    }

    /**
     * Sets the maximum number of time-line buckets.
     *
     * @param buckets The maximum number of time-line buckets.
     */
    public void setDispatchBuckets(String buckets) {
        setCacheValue("dispatch.buckets", buckets);
    }

    /**
     * Sets the time string that specifies the earliest time for this search.
     * Can be a relative or absolute time.
     *
     * If this value is an absolute time, use the dispatch.time_format to format
     * the value.
     *
     * @param earliestTime The earliest time for this search.
     */
    public void setDispatchEarliestTime(String earliestTime) {
        setCacheValue("dispatch.earliest_time", earliestTime);
    }

    /**
     * Sets the time string that specifies the latest time for this search.
     * Can be a relative or absolute time.
     *
     * If this value is an absolute time, use the dispatch.time_format to format
     * the value.
     *
     * @param latestTime The latest time for this search.
     */
    public void setDispatchLatestTime(String latestTime) {
        setCacheValue("dispatch.latest_time", latestTime);
    }

    /**
     * Sets whether to Enable or disable the lookups for this search.
     *
     * @param lookups The latest time for this search.
     */
    public void setDispatchLookups(boolean lookups) {
        setCacheValue("dispatch.lookups", lookups);
    }

    /**
     * Sets the maximum number of results before finalizing the search.
     *
     * @param max The maximum number of results before finalizing the search.
     */
    public void setDispatchMaxCount(int max) {
        setCacheValue("dispatch.max_count", max);
    }

    /**
     * Sets the maximum time, in seconds, before finalizing the
     * search.
     *
     * @param max The maximum time, in seconds, before finalizing the search.
     */
    public void setDispatchMaxTime(int max) {
        setCacheValue("dispatch.max_time", max);
    }

    /**
     * Sets how frequently Splunk should run the MapReduce reduce phase on
     * accumulated map values.
     *
     * @param seconds How frequently Splunk should run the MapReduce reduce
     * phase on accumulated map values.
     */
    public void setDispatchReduceFrequency(int seconds) {
        setCacheValue("dispatch.reduce_freq", seconds);
    }

    /**
     * Sets whether to back fill the real time window for this search.
     * Parameter valid only if this is a real time search.
     *
     * @param backfill whether to back fill the real time window for this
     * search.
     */
    public void setDispatchRealTimeBackfill(boolean backfill) {
        setCacheValue("dispatch.rt_backfill", backfill);
    }

    /**
     * Sets whether Splunk spawns a new search process when this saved search
     * is executed.
     *
     * Searches against indexes must run in a separate process.
     *
     * @param spawn whether Splunk spawns a new search process when this
     * saved search.
     */
    public void setDispatchSpawnProcess(boolean spawn) {
        setCacheValue("dispatch.spawn_process", spawn);
    }

    /**
     * Sets the time format string used to specify ealiest and latest times.
     *
     * @param format The time format for earliest and latest times.
     */
    public void setDispatchTimeFormat(String format) {
        setCacheValue("dispatch.time_format", format);
    }

    /**
     * Sets the minimum time-to-live in seconds of the search artifacts if this
     * action is triggered. If the value is predicates the letter {@code p}, the
     * value is not interpreted as seconds, but as periods.     *
     * @param format The time format for earliest and latest times.
     */
    public void setDispatchTtl(String format) {
        setCacheValue("dispatch.ttl", format);
    }

    /**
     * Sets the wildcard argument that accepts any dispatch related argument.
     *
     * @param wildcard The wildcard argument.
     */
    public void setDispatchWildcard(String wildcard) {
        setCacheValue("dispatch.*", wildcard);
    }

    /**
     * Sets the default UI view name (not label) in which to load he results.
     *
     * @param view The default UI view name.
     */
    public void setDisplayView(String view) {
        setCacheValue("displayview", view);
    }

    /**
     * Sets whether this search is to be ran on a schedule.
     *
     * @param value whether this search is to be ran on a schedule.
     */
    public void setIsScheduled(boolean value) {
        setCacheValue("is_scheduled", value);
    }

    /**
     * Sets whether this search is listed in the visible saved search list.
     *
     * @param value whether this search is listed in the visible saved search
     * list.
     */
    public void setIsVisible(boolean value) {
        setCacheValue("is_visible", value);
    }

    /**
     * Sets the maximum number of concurrent instances of this search the
     * scheduler is allowed to run.
     *
     * @param max The maximum number of concurrent instances of this search the
     * scheduler is allowed to run.
     */
    public void setMaxConcurrent(int max) {
        setCacheValue("max_concurrent", max);
    }

    /**
     * Sets the way the scheduler computes the next execution time of a
     * scheduled search. If this value is set to true, the scheduler bases its
     * determination of the next scheduled search execution time on the current
     * time.
     *
     * If this value is set to false, the scheduler bases its determination of
     * the next scheduled search on the last search execution time. This is
     * called continuous scheduling. If set to 0, the scheduler never skips
     * scheduled execution periods. However, the execution of the saved search
     * might fall behind depending on the scheduler's load. Use continuous
     * scheduling whenever you enable the summary index option.
     *
     * If set to true, the scheduler might skip some execution periods to make
     * sure that the scheduler is executing the searches running over the most
     * recent time range.
     *
     * The scheduler tries to execute searches that have realtime_schedule
     * set to true before it executes searches that have continuous scheduling
     * (realtime_schedule = false).
     *
     * @param value controls the next execution time computation.
     */
    public void setRealtimeSchedule(boolean value) {
        setCacheValue("realtime_schedule", value);
    }

    /**
     * Sets a field used by Splunk UI to denote the app this search should be
     * dispatched in.
     *
     * @param app the app name this search should be dispatched in.
     */
    public void setRequestUiDispatchApp(String app) {
        setCacheValue("request.ui_dispatch_app", app);
    }

    /**
     * Sets a field used by Splunk UI to denote the view this search should
     * be displayed in.
     *
     * @param view the view this search should be dispatched in.
     */
    public void setRequestUiDispatchView(String view) {
        setCacheValue("request.ui_dispatch_view", view);
    }

    /**
     * Sets whether to restart a real-time search managed by the scheduler when
     * a search peer becomes available for this saved search.
     *
     * NOTE: The peer can be a newly added peer or a peer that has been down
     * and has become available.
     *
     * @param restart whether to restart a real-time search managed by the
     * scheduler when a search peer becomes available for this saved search.
     */
    public void setRestartOnSearchpeerAdd(boolean restart) {
        setCacheValue("restart_on_searchpeer_add", restart);
    }

    /**
     * Sets whether this search runs when Splunk starts. If it does not run on
     * startup, it runs at the next scheduled time.
     *
     * It is recommended that you set run_on_startup to true for scheduled
     * searches that populate lookup tables.
     *
     * @param startup whether this search runs when Splunk starts.
     */
    public void setRunOnStartup(boolean startup) {
        setCacheValue("run_on_startup", startup);
    }

    /**
     * Sets the viewstate id associated with the UI view listed in
     * {@code displayview}.
     *
     * Note: Must match up to a stanza in {@code viewstates.conf}.
     *
     * @param vsid The viewstate id associated with the UI view listed in
     * {@code displayview}
     */
    public void setVsid(String vsid) {
        setCacheValue("vsid", vsid);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update(Map<String, Object> args) {
        if (!args.containsKey("search")) // requires search string
            args = Args.create(args).add("search", getSearch());
        super.update(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update() {
        if (!isUpdateKeyPresent("search")) {
            setCacheValue("search", getSearch()); // requires search string
        }
        super.update();
    }
}
