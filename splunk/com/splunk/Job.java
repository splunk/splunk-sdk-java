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

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Job} class represents a job, which is an individual 
 * instance of a running or completed search or report, along with its related
 * output.
 */
public class Job extends Entity {

    private boolean isReady = false;

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The search jobs endpoint.
     */
    Job(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the action path.
     *
     * @param action The requested action.
     * @return The action path.
     */
    @Override protected String actionPath(String action) {
        if (action.equals("control"))
            return path + "/control";
        return super.actionPath(action);
    }

    /**
     * Performs the requested action on this job. Valid values are: "pause", 
     * "unpause", "finalize", "cancel", "touch", "setttl", "setpriority", 
     * "enablepreview", and "disablepreview".
     *
     * @param action The action to perform.
     * @return The search job.
     */
    public Job control(String action) {
        return control(action, null);
    }

    /**
     * Performs the requested action on this job. Valid values are: "pause", 
     * "unpause", "finalize", "cancel", "touch", "setttl", "setpriority", 
     * "enablepreview", and "disablepreview".
     *
     * @param action The action to perform.
     * @param args Optional arguments for this action ("ttl" and "priority").
     * @return The search job.
     */
    public Job control(String action, Map args) {
        args = Args.create(args).add("action", action);
        service.post(actionPath("control"), args);
        invalidate();
        return this;
    }

    /**
     * Stops the current search and deletes the result cache.
     *
     * @return The search job.
     */
    public Job cancel() {
        try {
            return control("cancel");
        } catch (HttpException e) {
            if (e.getStatus() == 404) {
                // Already cancelled; cancel is a nop.
            } else {
                throw e;
            }
        }
        return this;
    }

    /**
     * Checks whether the job is ready to be accessed, and throws an exception
     * if it is not.
     */
    private void checkReady() {
        if (!isReady()) throw new SplunkException(SplunkException.JOB_NOTREADY,
            "Job not yet scheduled by server");
    }

    /**
     * Disables preview for this job.
     *
     * @return The search job.
     */
    public Job disablePreview() {
        return control("disablepreview");
    }

    /**
     * Enables preview for this job (although it might slow search
     * considerably).
     *
     * @return The search job.
     */
    public Job enablePreview() {
        return control("enablepreview");
    }

    /**
     * Stops the job and provides intermediate results available for retrieval.
     *
     * @return  The search job.
     */
    public Job finish() {
        return control("finalize");
    }

    /**
     * Pauses the current search.
     *
     * @return The search job.
     */
    public Job pause() {
        return control("pause");
    }

    /**
     * Returns the earliest time from which no events are later scanned.
     * (Use this as a progress indicator.)
     * @see #getLatestTime
     * @see #getEarliestTime
     * @see #getDoneProgress
     *
     * @return The earliest time.
     */
    public Date getCursorTime() {
        checkReady();
        return getDate("cursorTime");
    }

    /**
     * Returns a value that indicates how jobs were started (such as the
     * scheduler).
     *
     * @return The delegate, or {@code null} if not specified.
     */
    public String getDelegate() {
        checkReady();
        return getString("delegate", null);
    }

    /**
     * Returns the disk usage for this job.
     *
     * @return The disk usage, in bytes.
     */
    public int getDiskUsage() {
        checkReady();
        return getInteger("diskUsage");
    }

    /**
     * Returns the dispatch state for this job. <br>
     * Valid values are: QUEUED, PARSING, RUNNING, PAUSED, FINALIZING, FAILED,
     * or DONE.
     *
     * @return This job's dispatch state.
     */
    public String getDispatchState() {
        checkReady();
        return getString("dispatchState");
    }

    /**
     * Returns the approximate progress of the job, in the range of 0.0 to 1.0.
     * <br>
     * {@code doneProgress = (latestTime-cursorTime)/(latestTime-earliestTime)}
     * @see #getLatestTime
     * @see #getCursorTime
     * @see #getEarliestTime
     * 
     * @return This job's progress.
     */
    public float getDoneProgress() {
        checkReady();
        return getFloat("doneProgress");
    }

    /**
     * Returns the number of possible events that were dropped due to the
     * {@code rt_queue_size} (the number of events that the indexer should use
     * for this search). For real-time searches only.
     *
     * @return The number of dropped events.
     */
    public int getDropCount() {
        checkReady();
        return getInteger("dropCount", 0);
    }

    /**
     * Returns the earliest time in the time range to search. 
     * @see #getLatestTime
     * @see #getCursorTime
     * @see #getDoneProgress
     *
     * @return The earliest time, in UTC format.
     */
    public Date getEarliestTime() {
        checkReady();
        return getDate("earliestTime");
    }

    /**
     * Returns the count of events stored by search that are available to be
     * retrieved from the events endpoint.
     *
     * @return The count of available events.
     */
    public int getEventAvailableCount() {
        checkReady();
        return getInteger("eventAvailableCount");
    }

    /**
     * Returns the count of events (pre-transforming) that were generated.
     *
     * @return The number of events.
     */
    public int getEventCount() {
        checkReady();
        return getInteger("eventCount");
    }

    /**
     * Returns the count of event fields.
     *
     * @return The number of event fields.
     */
    public int getEventFieldCount() {
        checkReady();
        return getInteger("eventFieldCount");
    }

    /**
     * Indicates whether the events from this job are available by streaming.
     *
     * @return {@code true} if events can be streamed, {@code false} if not. 
     */
    public boolean getEventIsStreaming() {
        checkReady();
        return getBoolean("eventIsStreaming");
    }

    /**
     * Indicates whether any events from this job have not been stored. 
     * @return {@code true} if the event return is truncated, {@code false} if
     * not.
     */
    public boolean getEventIsTruncated() {
        checkReady();
        return getBoolean("eventIsTruncated");
    }

    /**
     * Returns the {@code InputStream} IO handle for this job's events.
     *
     * @return The event {@code InputStream} IO handle.
     */
    public InputStream getEvents() {
        checkReady();
        return getEvents(null);
    }

    /**
     * Returns the {@code InputStream} IO handle for this job's events.
     *
     * @param args Optional arguments. 
     * For a list of possible parameters, see the Request parameters for the 
     * <a href="http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTsearch#GET_search.2Fjobs.2F.7Bsearch_id.7D.2Fevents" 
     * target="_blank">GET search/jobs/{search_id}/events</a>
     * endpoint in the REST API documentation.
     *
     * @return The event {@code InputStream} IO handle.
     */
    public InputStream getEvents(Map args) {
        return getEventsMethod("/events", args);
    }
    
    /**
     * Returns the {@code InputStream} IO handle for this job's events.
     *
     * @param args Optional arguments (see {@link JobEventsArgs}).
     *
     * @return The event {@code InputStream} IO handle.
     */
    // NOTE: This overload exists primarily to provide better documentation
    //       for the "args" parameter.
    public InputStream getEvents(JobEventsArgs args) {
        checkReady();
        return getEvents((Map<String, Object>) args);
    }

    /**
     * Returns the subset of the entire search that is before any transforming 
     * commands. The original search should be the "eventSearch" + 
     * "reportSearch".
     * @see #getReportSearch
     * @return The event search query.
     */
    public String getEventSearch() {
        checkReady();
        return getString("eventSearch", null);
    }

    /**
     * Returns a value that indicates how events are sorted. 
     *
     * @return "asc" if events are sorted in time order (oldest first),
     * "desc" if events are sorted in inverse time order (latest first), 
     * or "none" if events are not sorted.
     */
    public String getEventSorting() {
        checkReady();
        return getString("eventSorting");
    }

    private InputStream getEventsMethod(String methodPath, Map args) {
        checkReady();

        if (args == null) {
            args = new HashMap<String, Object>();
        }
        if (!args.containsKey("segmentation")) {
            // By default, don't include notations in the results to highlight
            // search terms (i.e., <sg> elements in XML output).
            args.put("segmentation", "none");
        }

        ResponseMessage response = service.get(path + methodPath, args);
        return response.getContent();
    }

    /**
     * Returns all positive keywords used by this job. A positive keyword is 
     * a keyword that is not in a NOT clause.
     *
     * @return The search job keywords.
     */
    public String getKeywords() {
        checkReady();
        return getString("keywords", null);
    }

    /**
     * Returns this job's label.
     *
     * @return The search job label.
     */
    public String getLabel() {
        checkReady();
        return getString("label", null);
    }

    /**
     * Returns the latest time in the time range to search. 
     * @see #getCursorTime
     * @see #getEarliestTime
     * @see #getDoneProgress
     *
     * @return The latest time, in UTC format.
     */
    public Date getLatestTime() {
        checkReady();
        return getDate("latestTime");
    }

    /**
     * Returns this job's name (its search ID).
     *
     * @return The search job name.
     */
    @Override public String getName() {
        checkReady();
        return getSid();
    }

    /**
     * Returns the number of previews that have been generated so far for this
     * job.
     *
     * @return The number of previews.
     */
    public int getNumPreviews() {
        checkReady();
        return getInteger("numPreviews");
    }

    /**
     * Returns this job's priority in the range of 0-10.
     *
     * @return The search job priority.
     */
    public int getPriority() {
        checkReady();
        return getInteger("priority");
    }

    /**
     * Sets this job's priority in the range of 0-10.
     *
     * @param value The new priority.
     */
    public void setPriority(int value) {
        checkReady();
        control("setpriority", new Args("priority", value));
    }

    /**
     * Returns the search string that is sent to every search peer for this job.
     *
     * @return The remote search query string.
     */
    public String getRemoteSearch() {
        checkReady();
        return getString("remoteSearch", null);
    }

    /**
     * Returns the reporting subset of this search, which is the streaming part
     * of the search that is send to remote providers if reporting commands are
     * used. The original search should be the "eventSearch" + "reportSearch".
     * @see #getEventSearch
     *
     * @return The reporting search query.
     */
    public String getReportSearch() {
        checkReady();
        return getString("reportSearch", null);
    }

    /**
     * Returns the total count of results returned for this search job.
     * This is the subset of scanned events that actually matches the search
     * terms.
     *
     * @return The number of results.
     */
    public int getResultCount() {
        checkReady();
        return getInteger("resultCount");
    }

    /**
     * Indicates whether the job's results are available by streaming.
     *
     * @return {@code true} if results can be streamed, {@code false} if not.
     */
    public boolean getResultIsStreaming() {
        checkReady();
        return getBoolean("resultIsStreaming");
    }

    /**
     * Returns the number of result rows in the latest preview results for this
     * job.
     *
     * @return The number of result rows.
     */
    public int getResultPreviewCount() {
        checkReady();
        return getInteger("resultPreviewCount");
    }

    /**
     * Returns the {@code InputStream} IO handle for the results from this job.
     *
     * @return The results {@code InputStream} IO handle.
     */
    public InputStream getResults() {
        checkReady();
        return getResults(null);
    }

    /**
     * Returns the {@code InputStream} IO handle for the results from this job.
     *
     * @param args Optional arguments.
     * For a list of possible parameters, see the Request parameters for the 
     * <a href="http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTsearch#GET_search.2Fjobs.2F.7Bsearch_id.7D.2Fresults" 
     * target="_blank">GET search/jobs/{search_id}/results</a>
     * endpoint in the REST API documentation.
     * @return The results {@code InputStream} IO handle.
     */
    public InputStream getResults(Map args) {
        return getEventsMethod("/results", args);
    }
    
    /**
     * Returns the {@code InputStream} IO handle for the results from this job.
     *
     * @param args Optional arguments (see {@link JobResultsArgs}).
     * @return The results {@code InputStream} IO handle.
     */
    // NOTE: This overload exists primarily to provide better documentation
    //       for the "args" parameter.
    public InputStream getResults(JobResultsArgs args) {
        checkReady();
        return getResults((Map<String, Object>) args);
    }

    /**
     * Returns the {@code InputStream} IO handle for the preview results from 
     * this job.
     *
     * @return The preview results {@code InputStream} IO handle.
     */
    public InputStream getResultsPreview() {
        checkReady();
        return getResultsPreview(null);
    }

    /**
     * Returns the {@code InputStream} IO handle for the preview results from 
     * this job.
     *
     * @param args Optional arguments.
     * For a list of possible parameters, see the Request parameters for the 
     * <a href="http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTsearch#GET_search.2Fjobs.2F.7Bsearch_id.7D.2Fresults_preview" 
     * target="_blank">GET search/jobs/{search_id}/results_preview</a>
     * endpoint in the REST API documentation.
     * @return The preview results {@code InputStream} IO handle.
     */
    public InputStream getResultsPreview(Map args) {
        return getEventsMethod("/results_preview", args);
    }
    
    /**
     * Returns the {@code InputStream} IO handle for the preview results from 
     * this job.
     *
     * @param  args Optional arguments (see {@link JobResultsPreviewArgs}).
     * @return The preview results {@code InputStream} IO handle.
     */
    // NOTE: This overload exists primarily to provide better documentation
    //       for the "args" parameter.
    public InputStream getResultsPreview(JobResultsPreviewArgs args) {
        checkReady();
        return getResultsPreview((Map<String, Object>) args);
    }

    /**
     * Returns the time that the search job took to complete.
     *
     * @return The run-time duration, in seconds.
     */
    public float getRunDuration() {
        checkReady();
        return getFloat("runDuration");
    }

    /**
     * Returns the number of events that are scanned or read off disk.
     *
     * @return The number of events.
     */
    public int getScanCount() {
        checkReady();
        return getInteger("scanCount");
    }

    /**
     * Returns this job's search title.
     *
     * @return The search title.
     */
    public String getSearch() {
        checkReady();
        return getTitle();
    }

    /**
     * Returns the earliest time in the time range to search. 
     * @see #getLatestTime
     * @see #getCursorTime
     * @see #getDoneProgress
     *
     * @return This earliest search time, in epoch format.
     */
    public String getSearchEarliestTime() {
        checkReady();
        return getString("searchEarliestTime", null);
    }

    /**
     * Returns the latest time in the time range to search. 
     * @see #getEarliestTime
     * @see #getCursorTime
     * @see #getDoneProgress
     *
     * @return The latest search time, in epoch format.
     */
    public String getSearchLatestTime() {
        checkReady();
        return getString("searchLatestTime", null);
    }

    /**
     * Returns the {@code InputStream} IO handle to the search log for this job.
     *
     * @return The search log {@code InputStream} IO handle.
     */
    public InputStream getSearchLog() {
        checkReady();
        return getSearchLog(null);
    }

    /**
     * Returns the {@code InputStream} IO handle to the search log for this job.
     *
     * @param args Optional argument ("attachment").
     * @return The search log {@code InputStream} IO handle.
     */
    public InputStream getSearchLog(Map args) {
        checkReady();
        ResponseMessage response = service.get(path + "/search.log", args);
        return response.getContent();
    }

    /**
     * Returns a list of search peers that were contacted for this search.
     *
     * @return The search peers.
     */
    public String[] getSearchProviders() {
        checkReady();
        return getStringArray("searchProviders", null);
    }
    /**
     * Returns the unique search identifier (SID) for this job.
     *
     * @return The job's SID.
     */
    public String getSid() {
        return getString("sid");
    }

    /**
     * Returns this job's search ID from within a response message.
     *
     * @param response The response message.
     * @return This job's SID.
     */
    static String getSid(ResponseMessage response) {
        return Xml.parse(response.getContent())
            .getElementsByTagName("sid")
            .item(0)
            .getTextContent();
    }

    /**
     * Returns the {@code InputStream} IO handle for the summary for this job.
     *
     * @return The summary {@code InputStream} IO handle.
     */
    public InputStream getSummary() {
        checkReady();
        return getSummary(null);
    }

    /**
     * Returns the {@code InputStream} IO handle for the summary for this job.
     *
     * @param args Optional arguments.
     * For a list of possible parameters, see the Request parameters for the 
     * <a href="http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTsearch#GET_search.2Fjobs.2F.7Bsearch_id.7D.2Fsummary" 
     * target="_blank">GET search/jobs/{search_id}/summary</a>
     * endpoint in the REST API documentation.
     * @return The summary {@code InputStream} IO handle.
     */
    public InputStream getSummary(Map args) {
        checkReady();
        ResponseMessage response = service.get(path + "/summary", args);
        return response.getContent();
    }
    
    /**
     * Returns the {@code InputStream} IO handle for the summary for this job.
     *
     * @param args Optional arguments (see {@link JobSummaryArgs}).
     * @return The summary {@code InputStream} IO handle.
     */
    // NOTE: This overload exists primarily to provide better documentation
    //       for the "args" parameter.
    public InputStream getSummary(JobSummaryArgs args) {
        checkReady();
        return getSummary((Map<String, Object>) args);
    }

    /**
     * Returns the maximum number of timeline buckets for this job.
     *
     * @return The number of timeline buckets.
     */
    public int getStatusBuckets() {
        checkReady();
        return getInteger("statusBuckets");
    }

    /**
     * Returns the {@code InputStream} IO handle for the timeline for this job.
     *
     * @return The timeline {@code InputStream} IO handle.
     */
    public InputStream getTimeline() {
        checkReady();
        return getTimeline(null);
    }

    /**
     * Returns the {@code InputStream} IO handle for the timeline for this job.
     *
     * @param args Optional arguments ("output_time_format" and "time_format").
     * @return The timeline {@code InputStream} IO handle.
     */
    public InputStream getTimeline(Map args) {
        checkReady();
        ResponseMessage response = service.get(path + "/timeline", args);
        return response.getContent();
    }

    /**
     * Returns this job's time to live--that is, the time
     * before the search job expires and is still available.
     * If this value is 0, it means the job has expired.
     *
     * @return The time to live, in seconds. 
     */
    public int getTtl() {
        checkReady();
        return getInteger("ttl");
    }

    /**
     * Indicates whether the job is done.
     *
     * @return {@code true} if the job is done, {@code false} if not.
     */
    public boolean isDone() {
        if (!isReady())
            return false;
        return getBoolean("isDone");
    }

    /**
     * Indicates whether the job failed.
     *
     * @return {@code true} if the job failed, {@code false} if not.
     */
    public boolean isFailed() {
        checkReady();
        return getBoolean("isFailed");
    }

    /**
     * Indicates whether the job is finalized (forced to finish).
     *
     * @return {@code true} if the job is finalized, {@code false} if not.
     */
    public boolean isFinalized() {
        checkReady();
        return getBoolean("isFinalized");
    }

    /**
     * Indicates whether the job is paused.
     *
     * @return {@code true} if the job is paused, {@code false} if not.
     */
    public boolean isPaused() {
        checkReady();
        return getBoolean("isPaused");
    }

    /**
     * Indicates whether preview for the job is enabled.
     *
     * @return {@code true} if preview is enabled, {@code false} if not.
     */
    public boolean isPreviewEnabled() {
        checkReady();
        return getBoolean("isPreviewEnabled");
    }

    /**
     * Indicates whether the job has been scheduled and is ready to
     * return data.
     *
     * @return {@code true} if the job is ready to return data, {@code false} if
     * not.
     */
    public boolean isReady() {
        this.refresh();
        return isReady;
    }

    /**
     * Indicates whether the job is a real-time search.
     *
     * @return {@code true} if the job is a real-time search, {@code false} if
     * not.
     */
    public boolean isRealTimeSearch() {
        checkReady();
        return getBoolean("isRealTimeSearch");
    }

    /**
     * Indicates whether the job has a remote timeline component.
     *
     * @return {@code true} if the job has a remote timeline component,
     * {@code false} if not.
     */
    public boolean isRemoteTimeline() {
        checkReady();
        return getBoolean("isRemoteTimeline");
    }

    /**
     * Indicates whether the job is to be saved indefinitely.
     *
     * @return {@code true} if the job has been saved, {@code false} if not.
     */
    public boolean isSaved() {
        checkReady();
        return getBoolean("isSaved");
    }

    /**
     * Indicates whether this job was run as a saved search (via scheduler).
     *
     * @return {@code true} if the job is from a saved search, {@code false}
     * if not.
     */
    public boolean isSavedSearch() {
        checkReady();
        return getBoolean("isSavedSearch");
    }

    /**
     * Indicates whether the process running the search is dead but with the
     * search not finished.
     *
     * @return {@code true} if the job is a zombie, {@code false} if not.
     */
    public boolean isZombie() {
        checkReady();
        return getBoolean("isZombie");
    }

    // Job "entities" don't return an AtomFeed, only an AtomEntry.

    /**
     * Refreshes this job.
     *
     * @return The search job.
     */
    @Override public Job refresh() {
        update();
        ResponseMessage response = service.get(path);
        if (response.getStatus() == 204) {
            isReady = false;
            return this;
        }

        AtomEntry entry;
        try {
            entry = AtomEntry.parseStream(response.getContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        load(entry);

        if (getString("dispatchState").equals("QUEUED") || getString("dispatchState").equals("PARSING")) {
            isReady = false;
        } else {
            isReady = true;
        }


        return this;
    }

    /**
     * Unsupported. Removes this job. This method is unsupported and will throw
     * an exception.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

