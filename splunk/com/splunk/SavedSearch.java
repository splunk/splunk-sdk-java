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
import java.util.Map;

/**
 * The {@code SavedSearch} class represents a saved search.
 */
public class SavedSearch extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The saved searches endpoint.
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
    public Job dispatch() throws InterruptedException {
        return dispatch(null);
    }

    /**
     * Runs the saved search using dispatch arguments.
     *
     * @param args Dispatch arguments: <ul>
     * <li>"dispatch.now": A time string that is used to dispatch the search as 
     * though the specified time were the current time.</li>
     * <li>"dispatch.*": Overwrites the value of the search field specified in 
     * "*".</li>
     * <li>"trigger_actions": A Boolean that indicates whether to trigger alert 
     * actions.</li>
     * <li>"force_dispatch": A Boolean that indicates whether to start a new 
     * search if another instance of this search is already running.</li></ul>
     * @return The search job.
     */
    public Job dispatch(Map args) throws InterruptedException {
        ResponseMessage response = service.post(actionPath("dispatch"), args);
        invalidate();
        String sid = Job.getSid(response);

        Job job;
        JobCollection jobs = service.getJobs();
        job = jobs.get(sid);

        // if job not yet scheduled, create an empty job object
        if (job == null) {
            job = new Job(service, "search/jobs/" + sid);
        }

        return job;
    }
    
    /**
     * Runs the saved search using dispatch arguments.
     *
     * @param args Dispatch arguments (see {@link SavedSearchDispatchArgs}).
     * @return The search job.
     */
    // NOTE: This overload exists primarily to provide better documentation
    //       for the "args" parameter.
    public Job dispatch(SavedSearchDispatchArgs args) throws InterruptedException {
        return dispatch((Map<String, Object>) args);
    }

    /**
     * Returns an array of search jobs created from this saved search.
     *
     * @return An array of search jobs.
     */
    public Job[] history() {
        ResponseMessage response = service.get(actionPath("history"));
        AtomFeed feed;
        try {
            feed = AtomFeed.parseStream(response.getContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int count = feed.entries.size();
        Job[] result = new Job[count];
        for (int i = 0; i < count; ++i) {
            String sid = feed.entries.get(i).title;
            result[i] = new Job(service, "search/jobs/" + sid);
        }
        return result;
    }

    /**
     * Returns the email password.
     *
     * @return The email password.
     */
    public String getActionEmailAuthPassword() {
        return getString("action.email.auth_password", null);
    }

    /**
     * Returns the email username.
     *
     * @return The email username.
     */
    public String getActionEmailAuthUsername() {
        return getString("action.email.auth_username", null);
    }

    /**
     * Returns the blind carbon copy (BCC) email address.
     *
     * @return The BCC address.
     */
    public String getActionEmailBcc() {
        return getString("action.email.bcc", null);
    }

    /**
     * Returns the carbon copy (CC) email address.
     *
     * @return The CC address.
     */
    public String getActionEmailCc() {
        return getString("action.email.cc", null);
    }

    /**
     * Returns the search command (or pipeline) that runs the action.
     * <p>
     * Generally, this command is a template search pipeline that is realized
     * with values from the saved search. To reference saved search field
     * values, wrap them in "$". For example, use "$name$" to reference the 
     * saved search name, or use "$search$" to reference the search query.
     *
     * @return The search command (or pipeline).
     */
    public String getActionEmailCommand() {
        return getString("action.email.command", null);
    }

    /**
     * Returns the format of text in the email. This value also applies to any
     * attachments formats. Valid values are: "plain", "html", "raw", and "csv".
     *
     * @return The email format.
     */
    public String getActionEmailFormat() {
        return getString("action.email.format", null);
    }

    /**
     * Returns the email sender's name.
     *
     * @return The sender's name.
     */
    public String getActionEmailFrom() {
        return getString("action.email.from", null);
    }

    /**
     * Returns the host name used in the web link (URL) that is sent in email
     * alerts.
     *
     * Valid forms are "hostname" and "protocol://hostname:port".
     *
     * @return The host name used in the URL.
     */
    public String getActionEmailHostname() {
        return getString("action.email.hostname", null);
    }

    /**
     * Indicates whether the search results are contained in the body of the
     * email.
     *
     * @return {@code true} if search results are contained in the body of the
     * email, {@code false} if not.
     */
    public boolean getActionEmailInline() {
        return getBoolean("action.email.inline", false);
    }

    /**
     * Returns the address of the MTA server that is used to send the emails. If
     * this attribute is not set, this value defaults to the setting in the
     * alert_actions.conf file.
     *
     * @return The address of the MTA server.
     */
    public String getActionEmailMailServer() {
        return getString("action.email.mailserver", null);
    }

    /**
     * Returns the maximum number of search results to send in email alerts.
     *
     * @return The maximum number of search results per email.
     */
    public int getActionEmailMaxResults() {
        return getInteger("action.email.maxresults", -1);
    }

    /**
     * Returns the maximum amount of time an email action takes before the
     * action is canceled.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     *
     * @return The maximum amount of time.
     */
    public String getActionEmailMaxTime() {
        return getString("action.email.maxtime", null);
    }

    /**
     * Returns the name of the view to deliver if {@code ActionEmailSendPdf} is
     * enabled.
     * @see #getActionEmailSendPdf
     *
     * @return The name of the PDF view.
     */
    public String getActionEmailPdfView() {
        return getString("action.email.pdfview", null);
    }

    /**
     * Returns the search string for pre-processing results before emailing
     * them. Usually preprocessing consists of filtering out unwanted internal
     * fields.
     *
     * @return The search string for pre-processing results.
     */
    public String getActionEmailPreProcessResults() {
        return getString("action.email.preprocess_results", null);
    }

    /**
     * Returns the paper orientation. Valid values are "portrait" and
     * "landscape".
     *
     * @return The paper orientation.
     */
    public String getActionEmailReportPaperOrientation() {
        return getString("action.email.reportPaperOrientation", null);
    }

    /**
     * Returns the paper size for PDFs. Valid values are:
     * "letter", "legal", "ledger", "a2", "a3", "a4", and "a5".
     *
     * @return The paper size.
     */
    public String getActionEmailReportPaperSize() {
        return getString("action.email.reportPaperSize", null);
    }

    /**
     * Indicates whether the PDF server is enabled.
     *
     * @return {@code true} if the PDF server is enabled, {@code false} if not.
     */
    public boolean getActionEmailReportServerEnabled() {
        return getBoolean("action.email.reportServerEnabled", false);
    }

    /**
     * Returns the URL of the PDF report server, if one is set up and available
     * on the network.
     *
     * @return The URL of the PDF report server.
     */
    public String getActionEmailReportServerUrl() {
        return getString("action.email.reportServerURL", null);
    }

    /**
     * Indicates whether to create and send the results in PDF format.
     *
     * @return {@code true} if results are sent in PDF format, {@code false} if
     * not.
     */
    public boolean getActionEmailSendPdf() {
        return getBoolean("action.email.sendpdf", false);
    }

    /**
     * Indicates whether search results are attached to an email.
     *
     * @return {@code true} if search results are attached to an email,
     * {@code false} if not.
     */
    public boolean getActionEmailSendResults() {
        return getBoolean("action.email.sendresults", false);
    }

    /**
     * Returns the subject line of the email.
     *
     * @return The subject line of the email.
     */
    public String getActionEmailSubject() {
        return getString("action.email.subject", null);
    }

    /**
     * Returns a list of email recipients.
     *
     * @return A comma- or semicolon-delimited list of email recipients.
     */
    public String getActionEmailTo() {
        return getString("action.email.to", null);
    }

    /**
     * Indicates whether running this email action results in a trackable alert.
     *
     * @return {@code true} for a trackable alert, {@code false} if not.
     */
    public boolean getActionEmailTrackAlert() {
        return getBoolean("action.email.track_alert", false);
    }

    /**
     * Returns the minimum time-to-live (ttl) of search artifacts if
     * this email action is triggered. If the value is a number followed by "p",
     * it is the number of scheduled search periods.
     *
     * @return The minimum time-to-live in seconds, or the number of scheduled
     * periods.
     */
    public String getActionEmailTtl() {
        return getString("action.email.ttl", null);
    }

    /**
     * Indicates whether to use secure socket layer (SSL) when communicating
     * with the SMTP server.
     *
     * @return {@code true} if SSL is used, {@code false} if not.
     */
    public boolean getActionEmailUseSsl() {
        return getBoolean("action.email.use_ssl", false);
    }

    /**
     * Indicates whether to use transport layer security (TLS) when
     * communicating with the SMTP server.
     *
     * @return {@code true} if TLS is used, {@code false} if not.
     */
    public boolean getActionEmailUseTls() {
        return getBoolean("action.email.use_tls", false);
    }

    /**
     * Indicates whether columns should be sorted from least wide to most wide,
     * left to right.
     * This value is only used when {@code ActionEmailFormat} is "plain".
     * @see #getActionEmailFormat
     *
     * @return {@code true} if columns are sorted from least wide to most wide,
     * {@code false} if not.
     */
    public boolean getActionEmailWidthSortColumns() {
        return getBoolean("action.email.width_sort_columns", false);
    }

    /**
     * Returns the search command (or pipeline) that runs the action.
     *
     * @return The search command (or pipeline).
     */
    public String getActionPopulateLookupCommand() {
        return getString("action.populate_lookup.command", null);
    }

    /**
     * Returns the name of the lookup table or lookup path to populate.
     *
     * @return The lookup name or path.
     */
    public String getActionPopulateLookupDest() {
        return getString("action.populate_lookup.dest", null);
    }

    /**
     * Returns the host name used in the web link (URL) that is sent in
     * populate-lookup alerts.
     * Valid forms are "hostname" and "protocol://hostname:port".
     *
     * @return The hostname used in the URL.
     */
    public String getActionPopulateLookupHostname() {
        return getString("action.populate_lookup.hostname", null);
    }

    /**
     * Returns the maximum number of search results to send in populate-lookup
     * alerts.
     *
     * @return The maximum number of search results per alert.
     */
    public int getActionPopulateLookupMaxResults() {
        return getInteger("action.populate_lookup.maxresults", -1);
    }

    /**
     * Returns the maximum amount of time an alert action takes before the
     * action is canceled.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     *
     * @return The maximum amount of time.
     */
    public String getActionPopulateLookupMaxTime() {
        return getString("action.populate_lookup.maxtime", null);
    }

    /**
     * Indicates whether running this populate-lookup action results in a
     * trackable alert.
     *
     * @return {@code true} for a trackable alert, {@code false} if not.
     */
    public boolean getActionPopulateLookupTrackAlert() {
        return getBoolean("action.populate_lookup.track_alert", false);
    }

    /**
     * Returns the minimum time-to-live (ttl) of search artifacts if
     * this populate-lookup action is triggered. If the value is a number
     * followed by "p", it is the number of scheduled search periods.
     *
     * @return The minimum time-to-live in seconds, or the number of scheduled
     * periods.
     */
    public String getActionPopulateLookupTtl() {
        return getString("action.populate_lookup.ttl", null);
    }

    /**
     * Returns the search command (or pipeline) that runs the action.
     *
     * @return The search command (or pipeline).
     */
    public String getActionRssCommand() {
        return getString("action.rss.command", null);
    }

    /**
     * Returns the host name used in the web link (URL) that is sent in RSS
     * alerts.
     *
     * Valid forms are "hostname" and "protocol://hostname:port".
     *
     * @return The host name used in the URL.
     */
    public String getActionRssHostname() {
        return getString("action.rss.hostname", null);
    }

    /**
     * Returns the maximum number of search results to send in RSS alerts.
     *
     * @return The maximum number of search results per alert.
     */
    public int getActionRssMaxResults() {
        return getInteger("action.rss.maxresults", -1);
    }

    /**
     * Returns the maximum amount of time an RSS alert action takes before the
     * action is canceled.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     *
     * @return The maximum amount of time.
     */
    public String getActionRssMaxTime() {
        return getString("action.rss.maxtime", null);
    }

    /**
     * Indicates whether running this RSS action results in a trackable alert.
     *
     * @return {@code true} for a trackable alert, {@code false} if not.
     */
    public boolean getActionRssTrackAlert() {
        return getBoolean("action.rss.track_alert", false);
    }

    /**
     * Returns the minimum time-to-live (ttl) of search artifacts if
     * this RSS action is triggered. If the value is a number followed by "p",
     * it is the number of scheduled search periods.
     *
     * @return The minimum time-to-live in seconds, or the number of scheduled
     * periods.
     */
    public String getActionRssTtl() {
        return getString("action.rss.ttl", null);
    }

    /**
     * Returns the search command (or pipeline) that runs the action.
     *
     * @return The search command (or pipeline).
     */
    public String getActionScriptCommand() {
        return getString("action.script.command", null);
    }

    /**
     * Returns the filename of the script to call.
     *
     * @return The filename of the script.
     */
    public String getActionScriptFilename() {
        return getString("action.script.filename", null);
    }

    /**
     * Returns the host name used in the web link (URL) that is sent in script
     * alerts.
     *
     * Valid forms are "hostname" and "protocol://hostname:port".
     *
     * @return The host name used in the URL.
     */
    public String getActionScriptHostname() {
        return getString("action.script.hostname", null);
    }

    /**
     * Returns the maximum number of search results to send in script alerts.
     *
     * @return The maximum number of search results per alert.
     */
    public int getActionScriptMaxResults() {
        return getInteger("action.script.maxresults", -1);
    }

    /**
     * Returns the maximum amount of time a script action takes before the
     * action is canceled.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     *
     * @return The maximum amount of time.
     */
    public String getActionScriptMaxTime() {
        return getString("action.script.maxtime", null);
    }

    /**
     * Indicates whether running this script action results in a trackable
     * alert.
     *
     * @return {@code true} for a trackable alert, {@code false} if not.
     */
    public boolean getActionScriptTrackAlert() {
        return getBoolean("action.script.track_alert", false);
    }

    /**
     * Returns the minimum time-to-live (ttl) of search artifacts if
     * this script action is triggered. If the value is a number followed by
     * "p", it is the number of scheduled search periods.
     *
     * @return The minimum time-to-live in seconds, or the number of scheduled
     * periods.
     */
    public String getActionScriptTtl() {
        return getString("action.script.ttl", null);
    }

    /**
     * Returns the name of the summary index where the results of the scheduled
     * search are saved.
     *
     * @return The name of the summary index.
     */
    public String getActionSummaryIndexName() {
        return getString("action.summary_index._name", null);
    }

    /**
     * Returns the search command (or pipeline) that runs the action.
     * <p>
     * Generally, this command is a template search pipeline that is realized
     * with values from the saved search. To reference saved search field
     * values, wrap them in "$". For example, use "$name$" to reference the 
     * saved search name, or use "$search$" to reference the search query.
     *
     * @return The search command (or pipeline).
     */
    public String getActionSummaryIndexCommand() {
        return getString("action.summary_index.command", null);
    }

    /**
     * Returns the host name used in the web link (URL) that is sent in
     * summary-index alerts.
     *
     * Valid forms are "hostname" and "protocol://hostname:port".
     *
     * @return The host name used in the URL.
     */
    public String getActionSummaryIndexHostname() {
        return getString("action.summary_index.hostname", null);
    }

    /**
     * Indicates whether to run the summary indexing action as part of the
     * scheduled search.
     *
     * @return {@code true} if the summary indexing action runs with the
     * scheduled search, {@code false} if not.
     */
    public boolean getActionSummaryIndexInline() {
        return getBoolean("action.summary_index.inline", false);
    }

    /**
     * Returns the maximum number of search results to send in summary-index
     * alerts.
     *
     * @return The maximum number of search results per alert.
     */
    public int getActionSummaryIndexMaxResults() {
        return getInteger("action.summary_index.maxresults", -1);
    }

    /**
     * Returns the maximum amount of time a summary action takes before the
     * action is canceled.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     *
     * @return The maximum amount of time.
     */
    public String getActionSummaryIndexMaxTime() {
        return getString("action.summary_index.maxtime", null);
    }

    /**
     * Indicates whether running this summary-index action results in a
     * trackable alert.
     *
     * @return {@code true} for a trackable alert, {@code false} if not.
     */
    public boolean getActionSummaryIndexTrackAlert() {
        return getBoolean("action.summary_index.track_alert", false);
    }

    /**
     * Returns the minimum time-to-live (ttl) of search artifacts if
     * a summary-index action is triggered. If the value is a number followed by
     * "p", it is the number of scheduled search periods.
     *
     * @return The minimum time-to-live in seconds, or the number of scheduled
     * periods.
     */
    public String getActionSummaryIndexTtl() {
        return getString("action.summary_index.ttl", null);
    }

    /**
     * Indicates whether Splunk applies the alert actions to the entire result
     * set (digest) or to each individual search result (per result).
     *
     * @return {@code true} if Splunk applies the alert actions to the entire
     * result set (digest), {@code false} if actions are applied to individual
     * search results (per result).
     */
    public boolean getAlertDigestMode() {
        return getBoolean("alert.digest_mode", false);
    }

    /**
     * Returns the amount of time to show the alert in the dashboard.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     *
     * @return The amount of time.
     */
    public String getAlertExpires() {
        return getString("alert.expires");
    }

    /**
     * Returns the alert severity level. Valid values are:
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
    public boolean getAlertSuppress() {
        return getBoolean("alert.suppress", false);
    }

    /**
     * Returns a list of fields to use for alert suppression.
     *
     * @return A comma-delimited list of fields.
     */
    public String getAlertSuppressFields() {
        return getString("alert.suppress.fields", null);
    }

    /**
     * Returns the alert suppression period, which is only valid if
     * {@code AlertSuppress} is enabled.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     * @see #getAlertSuppress
     *
     * @return The alert suppression period.
     */
    public String getAlertSuppressPeriod() {
        return getString("alert.suppress.period", null);
    }

    /**
     * Returns a value that indicates how to track the actions triggered
     * by this saved search. Valid values are: "true" (enabled), "false"
     * (disabled), and "auto" (tracking is based on the setting of each action).
     *
     * @return The alert tracking setting.
     */
    public String getAlertTrack() {
        return getString("alert.track");
    }

    /**
     * Returns the alert comparator. Valid values are: "greater than", "less
     * than", "equal to", "rises by", "drops by", "rises by perc", and "drops by
     * perc".
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
     * If this value is expressed as a percentage, it indicates the value to use
     * when {@code AlertComparator} is set to "rises by perc" or "drops by
     * perc."
     * @see #getAlertComparator
     *
     * @return The alert threshold value.
     */
    public String getAlertThreshold() {
        return getString("alert_threshold", null);
    }

    /**
     * Returns a value that indicates what to base the alert on. Valid values
     * are: "always", "custom", "number of events", "number of hosts", and
     * "number of sources". This value is overridden by {@code AlertCondition}
     * if specified.
     *
     * @see #getAlertCondition
     *
     * @return The alert trigger type.
     */
    public String getAlertType() {
        return getString("alert_type");
    }

    /**
     * Returns the cron-style schedule for running this saved search.
     *
     * @return The schedule, as a valid cron-style string.
     */
    public String getCronSchedule() {
        return getString("cron_schedule", null);
    }

    /**
     * Returns a description of this saved search.
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
     * Indicates whether lookups are enabled for this search.
     *
     * @return {@code true} if lookups are enabled, {@code false} if not.
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
    public int getDispatchMaxTime() {
        return getInteger("dispatch.max_time");
    }
    
    /**
     * Returns how frequently Splunk runs the MapReduce reduce phase
     * on accumulated map values.
     *
     * @return The reduce frequency.
     */
    public int getDispatchReduceFrequency() {
        return getInteger("dispatch.reduce_freq");
    }

    /**
     * Indicates whether to back fill the real-time window for this search.
     * This attribute only applies to real-time searches.
     *
     * @return {@code true} if Splunk back fills the real-time window,
     * {@code false} if not.
     * @deprecated Use {@link #getDispatchRealTimeBackfill()} instead.
     */
    public boolean getDispatchRtBackfill() {
        return getDispatchRealTimeBackfill();
    }
    
    /**
     * Indicates whether to back fill the real-time window for this search.
     * This attribute only applies to real-time searches.
     *
     * @return {@code true} if Splunk back fills the real-time window,
     * {@code false} if not.
     */
    public boolean getDispatchRealTimeBackfill() {
        return getBoolean("dispatch.rt_backfill", false);
    }

    /**
     * Indicates whether Splunk spawns a new search process when running
     * this saved search.
     *
     * @return {@code true} if Splunk spawns a new search process, {@code false}
     * if not.
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
     * Returns the time to live (ttl) for artifacts of the scheduled search (the
     * time before the search job expires and artifacts are still available),
     * if no alerts are triggered. If the value is a number followed by "p", it
     * is the number of scheduled search periods.
     *
     * @return The time to live, in seconds or as a multiple of the scheduled
     * search period.
     */
    public String getDispatchTtl() {
        return getString("dispatch.ttl");
    }

    /**
     * Returns the default view in which to load results.
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
     * @return The maximum number of concurrent instances.
     */
    public int getMaxConcurrent() {
        return getInteger("max_concurrent");
    }

    /**
     * Returns the next scheduled time.
     *
     * @return The next scheduled time.
     */
    public Date getNextScheduledTime() {
        return getDate("next_scheduled_time", null);
    }

    /**
     * Returns the qualified search.
     *
     * @return The qualified search.
     */
    public String getQualifiedSearch() {
        return getString("qualifiedSearch", null);
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
     * Returns the app in which Splunk Web dispatches this search.
     *
     * @return The app name.
     */
    public String getRequestUiDispatchApp() {
        return getString("request.ui_dispatch_app", null);
    }

    /**
     * Returns the view in which Splunk Web displays this search.
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
     * Returns the search query for this saved search.
     *
     * @return The search query.
     */
    public String getSearch() {
        return getString("search");
    }

    /**
     * Returns the view state ID that is associated with the view specified in
     * the {@code DisplayView} attribute. This ID corresponds to a stanza in the
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
    public boolean isActionScript() {
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
     * Sets the username to use when authenticating the SMTP server. If this
     * string is empty, authentication is not attempted.
     *
     * @param username The username for authentication.
     */
    public void setActionEmailAuthUsername(String username) {
        setCacheValue("action.email.auth_username", username);
    }

    /**
     * Sets the blind carbon copy (BCC) email address to use for email alerts.
     * @see #isActionEmail
     *
     * @param bcc The BCC email address.
     */
    public void setActionEmailBcc(String bcc) {
        setCacheValue("action.email.bcc", bcc);
    }

    /**
     * Sets the carbon copy (CC) email address to use for email alerts.
     * @see #isActionEmail
     *
     * @param cc The CC email address.
     */
    public void setActionEmailCc(String cc) {
        setCacheValue("action.email.cc", cc);
    }

    /**
     * Returns the search command (or pipeline) that runs the action.
     * <p>
     * Generally, this command is a template search pipeline that is realized
     * with values from the saved search. To reference saved search field
     * values, wrap them in "$". For example, use "$name$" to reference the 
     * saved search name, or use "$search$" to reference the search query.
     *
     * @param command The search command (or pipeline).
     */
    public void setActionEmailCommand(String command) {
        setCacheValue("action.email.command", command);
    }

    /**
     * Sets the format of text in the email. This value also applies to any
     * attachments formats. Valid values are: "plain", "html", "raw", and "csv".
     *
     * @param format The email format.
     */
    public void setActionEmailFormat(String format) {
        setCacheValue("action.email.format", format);
    }

    /**
     * Sets the email sender's name.
     *
     * @param from The sender's name.
     */
    public void setActionEmailFrom(String from) {
        setCacheValue("action.email.from", from);
    }

    /**
     * Sets the host name used in the web link (URL) to send in email alerts.
     * Valid forms are "hostname" and "protocol://hostname:port".
     *
     * @param hostname The host name to use in the URL.
     */
    public void setActionEmailHostname(String hostname) {
        setCacheValue("action.email.hostname", hostname);
    }

    /**
     * Sets whether the search results are contained in the body of the email.
     *
     * @param inline {@code true} to include search results in the body of the
     * email, {@code false} if not.
     */
    public void setActionEmailInline(boolean  inline) {
        setCacheValue("action.email.inline", inline);
    }

    /**
     * Sets the address of the MTA server that is used to send the emails. If
     * this parameter is not set, the value defaults to the setting in the
     * alert_actions.conf file.
     *
     * @param mailServer The address of the MTA server.
     */
    public void setActionEmailMailServer(String  mailServer) {
        setCacheValue("action.email.mailserver", mailServer);
    }

    /**
     * Sets the maximum number of search results to send in email alerts.
     *
     * @param maxResults The maximum number of search results per alert.
     */
    public void setActionEmailMaxResults(int  maxResults) {
        setCacheValue("action.email.maxresults", maxResults);
    }

    /**
     * Sets the maximum amount of time an email action takes before the
     * action is canceled.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     *
     * @param maxTime The maximum amount of time.
     */
    public void setActionEmailMaxTime(String  maxTime) {
        setCacheValue("action.email.maxtime", maxTime);
    }

    /**
     * Sets the name of the view to deliver if {@code ActionEmailSendPdf} is
     * enabled.
     * @see #getActionEmailSendPdf
     *
     * @param name The name of the PDF view.
     */
    public void setActionEmailPdfView(String  name) {
        setCacheValue("action.email.pdfview", name);
    }

    /**
     * Sets the search string for pre-processing results before emailing
     * them. Usually preprocessing consists of filtering out unwanted internal
     * fields.
     *
     * @param preprocess The search string for pre-processing results.
     */
    public void setActionEmailPreProcessResults(String  preprocess) {
        setCacheValue("action.email.preprocess_results", preprocess);
    }

    /**
     * Sets the paper orientation. Valid values are "portrait" and
     * "landscape".
     *
     * @param orientation The paper orientation.
     */
    public void setActionEmailReportPaperOrientation(String  orientation) {
        setCacheValue("action.email.reportPaperOrientation", orientation);
    }

    /**
     * Sets the paper size for PDFs. Valid values are: "letter", "legal",
     * "ledger", "a2", "a3", "a4", and "a5".
     *
     * @param size The paper size.
     */
    public void setActionEmailReportPaperSize(String  size) {
        setCacheValue("action.email.reportPaperSize", size);
    }

    /**
     * Sets whether the PDF server is enabled.
     *
     * @param pdfServerEnabled {@code true} if the PDF server is enabled,
     * {@code false} if not.
     */
    public void setActionEmailReportServerEnabled(boolean  pdfServerEnabled) {
        setCacheValue("action.email.reportServerEnabled", pdfServerEnabled);
    }

    /**
     * Sets the URL of the PDF report server.
     *
     * @param pdfServerUrl The URL of the PDF report server.
     */
    public void setActionEmailReportServerUrl(String  pdfServerUrl) {
        setCacheValue("action.email.reportServerURL", pdfServerUrl);
    }

    /**
     * Sets whether to create and send the results in PDF format.
     *
     * @param sendPdf {@code true} to send results in PDF format, {@code false}
     * if not.
     */
    public void setActionEmailSendPdf(boolean  sendPdf) {
        setCacheValue("action.email.sendpdf", sendPdf);
    }

    /**
     * Sets whether to attach the search results to an email.
     *
     * @param sendResults {@code true} to attach search results to an email,
     * {@code false} if not.
     */
    public void setActionEmailSendResults(boolean  sendResults) {
        setCacheValue("action.email.sendresults", sendResults);
    }

    /**
     * Sets the subject line of the email.
     *
     * @param subject The subject line of the email.
     */
    public void setActionEmailSubject(String  subject) {
        setCacheValue("action.email.subject", subject);
    }

    /**
     * Sets a list of email recipients.
     *
     * @param to A comma- or semicolon-delimited list of email recipients.
     */
    public void setActionEmailTo(String  to) {
        setCacheValue("action.email.to", to);
    }

    /**
     * Sets whether running this email action results in a trackable alert.
     *
     * @param trackAlert {@code true} for a trackable alert, {@code false} if
     * not.
     */
    public void setActionEmailTrackAlert(boolean  trackAlert) {
        setCacheValue("action.email.track_alert", trackAlert);
    }

    /**
     * Sets the minimum time-to-live in seconds of the search artifacts if an
     * email action is triggered. If the value is a number followed by "p",
     * it is the number of scheduled search periods.
     *
     * @param ttl The minimum time-to-live in seconds, or the number of
     * scheduled periods.
     */
    public void setActionEmailTtl(String  ttl) {
        setCacheValue("action.email.ttl", ttl);
    }

    /**
     * Sets whether to use secure socket layer (SSL) when communicating
     * with the SMTP server.
     *
     * @param useSsl {@code true} to use SSL, {@code false} if not.
     */
    public void setActionEmailUseSsl(boolean  useSsl) {
        setCacheValue("action.email.use_ssl", useSsl);
    }

    /**
     * Sets whether to use transport layer security (TLS) when
     * communicating with the SMTP server.
     *
     * @param useTls {@code true} to use TLS, {@code false} if not.
     */
    public void setActionEmailUseTls(boolean  useTls) {
        setCacheValue("action.email.use_tls", useTls);
    }

    /**
     * Sets whether columns should be sorted from least wide to most wide,
     * left to right.
     * This value is only used when {@code ActionEmailFormat} is "plain".
     * @see #getActionEmailFormat
     *
     * @param widthSortColumns {@code true} to sort columns from least wide to
     * most wide, {@code false} if not.
     */
    public void setActionEmailWidthSortColumns(boolean  widthSortColumns) {
        setCacheValue("action.email.width_sort_columns", widthSortColumns);
    }

    /**
     * Sets the search command (or pipeline) that runs the action.
     *
     * @param command The search command (or pipeline).
     */
    public void setActionPopulateLookupCommand(String  command) {
        setCacheValue("action.populate_lookup.command", command);
    }

    /**
     * Sets the name of the lookup table or lookup path to populate.
     *
     * @param dest The lookup name or path.
     */
    public void setActionPopulateLookupDest(String  dest) {
        setCacheValue("action.populate_lookup.dest", dest);
    }

    /**
     * Sets the host name used in the web link (URL) to send in populate-lookup
     * alerts.
     * Valid forms are "hostname" and "protocol://hostname:port".
     *
     * @param hostname The host name to use in the URL.
     */
    public void setActionPopulateLookupHostname(String  hostname) {
        setCacheValue("action.populate_lookup.hostname", hostname);
    }

    /**
     * Sets the maximum number of search results to send in populate-lookup
     * alerts.
     *
     * @param maxResults The maximum number of search results per alert.
     */
    public void setActionPopulateLookupMaxResults(int  maxResults) {
        setCacheValue("action.populate_lookup.maxresults", maxResults);
    }

    /**
     * Sets the maximum amount of time an alert action takes before the
     * action is canceled.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     *
     * @param maxTime The maximum amount of time.
     */
    public void setActionPopulateLookupMaxTime(String  maxTime) {
        setCacheValue("action.populate_lookup.maxtime", maxTime);
    }
    /**
     * Sets whether running this populate-lookup action results in a trackable
     * alert.
     *
     * @param trackAlert {@code true} for a trackable alert, {@code false} if
     * not.
     */
    public void setActionPopulateLookupTrackAlert(boolean  trackAlert) {
        setCacheValue("action.populate_lookup.track_alert", trackAlert);
    }

    /**
     * Sets the minimum time-to-live (ttl) of the search artifacts if
     * this populate-lookup action is triggered. If the value is a number
     * followed by "p", it is the number of scheduled search periods.
     *
     * @param ttl The minimum time-to-live in seconds, or the number of
     * scheduled periods.
     */
    public void setActionPopulateLookupTtl(String  ttl) {
        setCacheValue("action.populate_lookup.ttl", ttl);
    }

    /**
     * Sets the search command (or pipeline) that runs the action.
     * <p>
     * Generally, this command is a template search pipeline that is realized
     * with values from the saved search. To reference saved search field
     * values, wrap them in "$". For example, use "$name$" to reference the 
     * saved search name, or use "$search$" to reference the search query.
     *
     * @param command The search command (or pipeline).
     */
    public void setActionRssCommand(String  command) {
        setCacheValue("action.rss.command", command);
    }

    /**
     * Sets the host name to use in the web link (URL) to send in RSS alerts.
     * Valid forms are "hostname" and "protocol://hostname:port".
     *
     * @param hostname The host name to use in the URL.
     */
    public void setActionRssHostname(String  hostname) {
        setCacheValue("action.rss.hostname", hostname);
    }

    /**
     * Sets the maximum number of search results to send in RSS alerts.
     *
     * @param maxResults The maximum number of search results per alert.
     */
    public void setActionRssMaxResults(int  maxResults) {
        setCacheValue("action.rss.maxresults", maxResults);
    }

    /**
     * Sets the maximum amount of time an RSS action takes before the
     * action is canceled.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     *
     * @param maxTime The maximum amount of time.
     */
    public void setActionRssMaxTime(String  maxTime) {
        setCacheValue("action.rss.maxtime", maxTime);
    }

    /**
     * Sets whether running this RSS action results in a trackable alert.
     *
     * @param trackAlert {@code true} for a trackable alert, {@code false} if
     * not.
     */
    public void setActionRssTrackAlert(boolean  trackAlert) {
        setCacheValue("action.rss.track_alert", trackAlert);
    }

    /**
     * Sets the minimum time-to-live in seconds of the search artifacts if an
     * RSS action is triggered. If the value is a number followed by "p",
     * it is the number of scheduled search periods.
     *
     * @param ttl The minimum time-to-live in seconds, or the number of
     * scheduled periods.
     */
    public void setActionRssTtl(String  ttl) {
        setCacheValue("action.rss.ttl", ttl);
    }

    /**
     * Sets the search command (or pipeline) that runs the action.
     * <p>
     * Generally, this command is a template search pipeline that is realized
     * with values from the saved search. To reference saved search field
     * values, wrap them in "$". For example, use "$name$" to reference the 
     * saved search name, or use "$search$" to reference the search query.
     *
     * @param command The search command (or pipeline).
     */
    public void setActionScriptCommand(String  command) {
        setCacheValue("action.script.command", command);
    }

    /**
     * Sets the file name of the script to call. This value is required if
     * {@code ActionScript} is enabled.
     * @see #isActionScript
     *
     * @param filename The file name of the script.
     */
    public void setActionScriptFilename(String  filename) {
        setCacheValue("action.script.filename", filename);
    }

    /**
     * Sets the host name used in the web link (URL) to send in script alerts.
     * Valid forms are "hostname" and "protocol://hostname:port".
     *
     * @param hostname The host name to use in the URL.
     */
    public void setActionScriptHostname(String  hostname) {
        setCacheValue("action.script.hostname", hostname);
    }

    /**
     * Sets the maximum number of search results to send in script alerts.
     *
     * @param maxResults The maximum number of search results per alert.
     */
    public void setActionScriptMaxResults(int  maxResults) {
        setCacheValue("action.script.maxresults", maxResults);
    }

    /**
     * Sets the maximum amount of time a script action takes before the
     * action is canceled.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     *
     * @param maxTime The maximum amount of time.
     */
    public void setActionScriptMaxTime(String  maxTime) {
        setCacheValue("action.script.maxtime", maxTime);
    }

    /**
     * Sets whether running this script action results in a trackable alert.
     *
     * @param trackAlert {@code true} for a trackable alert, {@code false} if
     * not.
     */
    public void setActionScriptTrackAlert(boolean  trackAlert) {
        setCacheValue("action.script.track_alert", trackAlert);
    }

    /**
     * Sets the minimum time-to-live in seconds of the search artifacts if a
     * script action is triggered. If the value is a number followed by "p",
     * it is the number of scheduled search periods.
     *
     * @param ttl The minimum time-to-live in seconds, or the number of
     * scheduled periods.
     */
    public void setActionScriptTtl(String  ttl) {
        setCacheValue("action.script.ttl", ttl);
    }

    /**
     * Sets the name of the summary index where the results of the scheduled
     * search are saved.
     *
     * @param name The name of the summary index.
     */
    public void setActionSummaryIndexName(String name) {
        setCacheValue("action.summary_index._name", name);
    }

    /**
     * Sets the search command (or pipeline) that runs the action.
     * <p>
     * Generally, this command is a template search pipeline that is realized
     * with values from the saved search. To reference saved search field
     * values, wrap them in "$". For example, use "$name$" to reference the 
     * saved search name, or use "$search$" to reference the search query.
     *
     * @param command The search command (or pipeline).
     */
    public void setActionSummaryIndexCommand(String  command) {
        setCacheValue("action.summary_index.command", command);
    }

    /**
     * Sets the host name used in the web link (URL) to send in summary-index
     * alerts.
     * Valid forms are "hostname" and "protocol://hostname:port".
     *
     * @param hostname The host name to use in the URL.
     */
    public void setActionSummaryIndexHostname(String  hostname) {
        setCacheValue("action.summary_index.hostname", hostname);
    }

    /**
     * Sets whether to run the summary indexing action as part of the
     * scheduled search.
     *
     * @param inline {@code true} to run the summary indexing action with the
     * scheduled search, {@code false} if not.
     */
    public void setActionSummaryIndexInline(boolean  inline) {
        setCacheValue("action.summary_index.inline", inline);
    }

    /**
     * Sets the maximum number of search results to send in summary-index
     * alerts.
     *
     * @param maxResults The maximum number of search results per alert.
     */
    public void setActionSummaryIndexMaxResults(int  maxResults) {
        setCacheValue("action.summary_index.maxresults", maxResults);
    }

    /**
     * Sets the maximum amount of time a summary-index action takes before the
     * action is canceled.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     *
     * @param maxTime The maximum amount of time.
     */
    public void setActionSummaryIndexMaxTime(String  maxTime) {
        setCacheValue("action.summary_index.maxtime", maxTime);
    }

    /**
     * Sets whether running summary-index action results in a trackable alert.
     *
     * @param trackAlert {@code true} for a trackable alert, {@code false} if
     * not.
     */
    public void setActionSummaryIndexTrackAlert(boolean  trackAlert) {
        setCacheValue("action.summary_index.track_alert", trackAlert);
    }

    /**
     * Sets the minimum time-to-live in seconds of the search artifacts if a
     * summary-index action is triggered. If the value is a number followed by
     * "p", it is the number of scheduled search periods.
     *
     * @param ttl The minimum time-to-live in seconds, or the number of
     * scheduled periods.
     */
    public void setActionSummaryIndexTtl(String  ttl) {
        setCacheValue("action.summary_index.ttl", ttl);
    }

    /**
     * Sets whichs actions to enable. Valid actions are: "email",
     * "populate_lookup", "rss", "script", and "summary_index".
     *
     * @param actions A comma-separated list of actions.
     */
    public void setActions(String actions) {
        setCacheValue("actions", actions);
    }

    /**
     * Sets whether Splunk applies the alert actions to the entire result set or
     * on each individual result.
     *
     * @param digest {@code true} if Splunk applies the alert actions to the
     * entire result set (digest), {@code false} if actions are applied to
     * individual search results (per result).
     */
    public void setAlertDigestMode(boolean digest) {
        setCacheValue("alert.digest_mode", digest);
    }

    /**
     * Sets the period of time to show the alert in the dashboard.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     *
     * @param period The amount of time.
     */
    public void setAlertExpires(String period) {
        setCacheValue("alert.expires", period);
    }

    /**
     * Sets the alert severity level, which is an integer from 1-6 (1=DEBUG,
     * 2=INFO, 3=WARN, 4=ERROR, 5=SEVERE, 6=FATAL).
     *
     * @param severity The alert severity level.
     */
    public void setAlertSeverity(int severity) {
        setCacheValue("alert.severity", severity);
    }

    /**
     * Sets whether to enable alert suppression for this scheduled search.
     *
     * @param suppress {@code true} to enable alert suppression, {@code false}
     * if not.
     */
    public void setAlertSuppress(boolean suppress) {
        setCacheValue("alert.suppress", suppress);
    }

    /**
     * Specifies a list of fields to use for alert suppression. This attribute
     * is required when alert supression and per-result alerting are enabled.
     * @see #setAlertSuppress
     * @see #isDigestMode
     *
     * @param fields A comma-delimited list of fields.
     */
    public void setAlertSuppressFields(String fields) {
        setCacheValue("alert.suppress.fields", fields);
    }

    /**
     * Sets the period for alert suppression. This attribute is only valid when
     * {@code AlertSuppress} is enabled.
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d").
     * @see #setAlertSuppress
     *
     * @param period The suppression period.
     */
    public void setAlertSuppressPeriod(String period) {
        setCacheValue("alert.suppress.period", period);
    }

    /**
     * Sets how to track the actions triggered by this saved search. Valid
     * values are: "true" (enabled), "false" (disabled), and "auto" (tracking
     * is based on the setting of each action).
     *
     * @param track The alert tracking setting.
     */
    public void setAlertTrack(String track) {
        setCacheValue("alert.track", track);
    }

    /**
     * Sets the alert comparator. Valid values are: "greater than", "less
     * than", "equal to", "rises by", "drops by", "rises by perc", and "drops by
     * perc".
     *
     * @param comparator The alert comparator.
     */
    public void setAlertComparator(String comparator) {
        setCacheValue("alert_comparator", comparator);
    }

    /**
     * Sets a conditional search that is evaluated against the results of the
     * saved search.
     * <p>
     * <b>Note:</b> If you specify an alert_condition, do not set
     * {@code counttype}, {@code relation}, or {@code quantity}.
     *
     * @param conditional A conditional search.
     */
    public void setAlertCondition(String conditional) {
        setCacheValue("alert_condition", conditional);
    }

    /**
     * Sets the value to compare to before triggering the alert action.
     * If this value is expressed as a percentage, it indicates the value to use
     * when {@code AlertComparator} is set to "rises by perc" or "drops by
     * perc."
     * @see #getAlertComparator
     *
     * @param threshold The threshold as a number or percentage (a number
     * followed by "%").
     */
    public void setAlertThreshold(String threshold) {
        setCacheValue("alert_threshold", threshold);
    }

    /**
     * Sets a value that indicates what to base the alert on. Valid values
     * are: "always", "custom", "number of events", "number of hosts", and
     * "number of sources". This value is overridden by {@code AlertCondition}
     * if specified.
     *
     * @param type The alert trigger type.
     */
    public void setAlertType(String type) {
        setCacheValue("alert_type", type);
    }

    /**
     * Sets the cron schedule for running this saved search.
     *
     * @param cronSchedule The schedule, as a valid cron-style string.
     */
    public void setCronSchedule(String cronSchedule) {
        setCacheValue("cron_schedule", cronSchedule);
    }

    /**
     * Sets the description of this saved search.
     *
     * @param description The description.
     */
    public void setDescription(String description) {
        setCacheValue("description", description);
    }

    /**
     * Sets whether the saved search is disabled. Disabled searches are not
     * visible in Splunk Web.
     *
     * @param disabled {@code true} to disable the saved search, {@code false}
     * to enable it.
     */
    public void setDisabled(boolean disabled) {
        setCacheValue("disabled", disabled);
    }

    /**
     * Sets the maximum number of timeline buckets.
     *
     * @param buckets The maximum number of timeline buckets.
     * @deprecated Use {@link #setDispatchBuckets(int)} instead.
     */
    public void setDispatchBuckets(String buckets) {
        setDispatchBuckets(Integer.parseInt(buckets));
    }
    
    /**
     * Sets the maximum number of timeline buckets.
     *
     * @param buckets The maximum number of timeline buckets.
     */
    public void setDispatchBuckets(int buckets) {
        setCacheValue("dispatch.buckets", buckets);
    }

    /**
     * Sets the earliest time for this search. This value can be a relative
     * time, or absolute time as formatted by {@code DispatchTimeFormat}.
     * @see #getDispatchTimeFormat
     *
     * @param earliestTime The earliest time for this search.
     */
    public void setDispatchEarliestTime(String earliestTime) {
        setCacheValue("dispatch.earliest_time", earliestTime);
    }

    /**
     * Sets the latest time for this search. This value can be a relative time,
     * or absolute time as formatted by {@code DispatchTimeFormat}.
     * @see #getDispatchTimeFormat
     *
     * @param latestTime The latest time for this search.
     */
    public void setDispatchLatestTime(String latestTime) {
        setCacheValue("dispatch.latest_time", latestTime);
    }

    /**
     * Sets whether to enable lookups for this search.
     *
     * @param lookups {@code true} to enable lookups, {@code false} if not.
     */
    public void setDispatchLookups(boolean lookups) {
        setCacheValue("dispatch.lookups", lookups);
    }

    /**
     * Sets the maximum number of results before finalizing the search.
     *
     * @param max The maximum number of results.
     */
    public void setDispatchMaxCount(int max) {
        setCacheValue("dispatch.max_count", max);
    }

    /**
     * Sets the maximum time before finalizing the search.
     *
     * @param max The maximum time, in seconds.
     */
    public void setDispatchMaxTime(int max) {
        setCacheValue("dispatch.max_time", max);
    }

    /**
     * Sets how frequently Splunk should run the MapReduce reduce phase on
     * accumulated map values.
     *
     * @param seconds The reduce frequency.
     */
    public void setDispatchReduceFrequency(int seconds) {
        setCacheValue("dispatch.reduce_freq", seconds);
    }

    /**
     * Sets whether to back fill the real-time window for this search.
     * This attribute only applies to real-time searches.
     *
     * @param backfill {@code true} if Splunk back fills the real-time window,
     * {@code false} if not.
     */
    public void setDispatchRealTimeBackfill(boolean backfill) {
        setCacheValue("dispatch.rt_backfill", backfill);
    }

    /**
     * Sets whether Splunk spawns a new search process when running this saved
     * search. Searches against indexes must run in a separate process.
     *
     * @param spawn {@code true} if Splunk spawns a new search process,
     * {@code false} if not.
     */
    public void setDispatchSpawnProcess(boolean spawn) {
        setCacheValue("dispatch.spawn_process", spawn);
    }

    /**
     * Sets the time format used to specify the earliest and latest times
     * for this search.
     *
     * @param format The time format.
     */
    public void setDispatchTimeFormat(String format) {
        setCacheValue("dispatch.time_format", format);
    }

    /**
     * Sets the minimum time-to-live in seconds of the search artifacts if no
     * actions are triggered. If the value is a number followed by "p",
     * it is the number of scheduled search periods.
     *
     * @param format The time to live, in seconds or as a multiple of the
     * scheduled search period.
     */
    public void setDispatchTtl(String format) {
        setCacheValue("dispatch.ttl", format);
    }

    /**
     * Sets the default view in which to load the results.
     *
     * @param view The view name (not label).
     */
    public void setDisplayView(String view) {
        setCacheValue("displayview", view);
    }

    /**
     * Sets whether this search runs on a schedule.
     *
     * @param value {@code true} to run this search on a schedule, {@code false}
     * if not.
     */
    public void setIsScheduled(boolean value) {
        setCacheValue("is_scheduled", value);
    }

    /**
     * Sets whether this search appears in the visible list of saved searches.
     *
     * @param value {@code true} to display this in the visible list,
     * {@code false} if not.
     */
    public void setIsVisible(boolean value) {
        setCacheValue("is_visible", value);
    }

    /**
     * Sets the maximum number of concurrent instances of this search the
     * scheduler is allowed to run.
     *
     * @param max The maximum number of concurrent instances.
     */
    public void setMaxConcurrent(int max) {
        setCacheValue("max_concurrent", max);
    }

    /**
     * Sets how the scheduler computes the next time a scheduled search is run.
     * <ul>
     * <li>When {@code true}: The schedule is based on the current time.
     * The scheduler might skip some scheduled periods to make
     * sure that searches over the most recent time range are run.</li>
     * <li>When {@code false}: The schedule is based on the last search run time
     * (referred to as "continuous scheduling") and the scheduler never skips
     * scheduled periods. However, the scheduler might fall behind depending on
     * its load. Use continuous scheduling whenever you enable the summary index
     * option ({@code ActionSummaryIndex}).</li>
     * </ul>
     * The scheduler tries to run searches that have real-time schedules enabled
     * before running searches that have continuous scheduling enabled.
     * @see #isActionSummaryIndex
     *
     * @param value {@code true} to enable a real-time schedule for this search,
     * {@code false} to enable continuous scheduling for this search.
     */
    public void setRealtimeSchedule(boolean value) {
        setCacheValue("realtime_schedule", value);
    }

    /**
     * Sets the app in which Splunk Web dispatches this search.
     *
     * @param app The app name.
     */
    public void setRequestUiDispatchApp(String app) {
        setCacheValue("request.ui_dispatch_app", app);
    }

    /**
     * Sets the view in which Splunk Web displays this search.
     *
     * @param view The view name.
     */
    public void setRequestUiDispatchView(String view) {
        setCacheValue("request.ui_dispatch_view", view);
    }

    /**
     * Sets whether a real-time search managed by the scheduler is
     * restarted when a search peer becomes available for this saved search.
     * <p>
     * <b>Note:</b> The peer can be one that is newly added or one that has
     * become available after being down.
     *
     * @param restart {@code true} to restart a real-time search, {@code false}
     * if not.
     * @deprecated Use {@link #setRestartOnSearchPeerAdd(boolean)} instead.
     */
    public void setRestartOnSearchpeerAdd(boolean restart) {
        setRestartOnSearchPeerAdd(restart);
    }
    
    /**
     * Sets whether a real-time search managed by the scheduler is
     * restarted when a search peer becomes available for this saved search.
     * <p>
     * <b>Note:</b> The peer can be one that is newly added or one that has
     * become available after being down.
     *
     * @param restart {@code true} to restart a real-time search, {@code false}
     * if not.
     */
    public void setRestartOnSearchPeerAdd(boolean restart) {
        setCacheValue("restart_on_searchpeer_add", restart);
    }

    /**
     * Sets whether this search is run when Splunk starts. If the search
     * is not run on startup, it runs at the next scheduled time.
     * <p>
     * It is recommended that you set this value to {@code true} for scheduled
     * searches that populate lookup tables.
     *
     * @param startup {@code true} to run this search when Splunk starts,
     * {@code false} if not.
     */
    public void setRunOnStartup(boolean startup) {
        setCacheValue("run_on_startup", startup);
    }

    /**
     * Sets the search query for this saved search.
     *
     * @param search The search query.
     */
    public void setSearch(String search) {
        setCacheValue("search", search);
    }

    /**
     * Sets the view state ID that is associated with the view specified in
     * the {@code DisplayView} attribute.
     * <p>
     * <b>Note:</b> This ID must match a stanza in the from the viewstates.conf
     * configuration file.
     * @see #getDisplayView
     *
     * @param vsid The view state ID.
     */
    public void setVsid(String vsid) {
        setCacheValue("vsid", vsid);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update(Map<String, Object> args) {
        // Add required arguments if not already present
        if (!args.containsKey("search")) {
            args = Args.create(args).add("search", getSearch());
        }

        super.update(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update() {
        // If not present in the update keys, add required attribute as long
        // as one pre-existing update pair exists
        if (toUpdate.size() > 0 && !toUpdate.containsKey("search")) {
            setCacheValue("search", getSearch());
        }
        super.update();
    }
}
