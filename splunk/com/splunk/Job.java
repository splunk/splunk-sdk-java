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
import java.util.Map;

/**
 * The {@code Job} class represents a job, which is an individual 
 * instance of a running or completed search or report, along with its related
 * output.
 */
public class Job extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The jobs search endpoint.
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
     * Performs the requested action on this job.
     *
     * @param action The action to perform.
     * @return The job.
     */
    public Job control(String action) {
        return control(action, null);
    }

    /**
     * Performs the requested action on this job.
     *
     * @param action The action to perform.
     * @param args Optional arguments for this action.
     * @return The job.
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
     * @return The job.
     */
    public Job cancel() {
        return control("cancel");
    }

    /**
     * Disables preview for this job.
     *
     * @return The job.
     */
    public Job disablePreview() {
        return control("disablepreview");
    }

    /**
     * Enables preview for this job (although it might slow search
     * considerably).
     *
     * @return The job.
     */
    public Job enablePreview() {
        return control("enablepreview");
    }

    /**
     * Stops the job and provides intermediate results available for retrieval.
     *
     * @return  The job.
     */
    public Job finish() {
        return control("finalize");
    }

    /**
     * Suspends the execution of the current search.
     *
     * @return The job.
     */
    public Job pause() {
        return control("pause");
    }

    /**
     * Returns the earliest time from which we are sure no events later than
     * this time will be scanned later. (Use this as a progress indicator.)
     * @see #getLatestTime
     * @see #getEarliestTime
     * @see #getDoneProgress
     *
     * @return The earliest time.
     */
    public Date getCursorTime() {
        return getDate("cursorTime");
    }

    /**
     * Returns a value that indicates jobs how were started (such as the
     * scheduler).
     *
     * @return The delegate, or {@code null} if not specified.
     */
    public String getDelegate() {
        return getString("delegate", null);
    }

    /**
     * Returns the disk usage for this job.
     *
     * @return The disk usage, in bytes.
     */
    public int getDiskUsage() {
        return getInteger("diskUsage");
    }

    /**
     * Returns the dispatch state for this job. </br>
     * Valid values are: QUEUED, PARSING, RUNNING, PAUSED, FINALIZING, FAILED,
     * or DONE.
     *
     * @return This job's dispatch state.
     */
    public String getDispatchState() {
        return getString("dispatchState");
    }

    /**
     * Returns the approximate progress of the job, in the range of 0.0 to 1.0.
     * </br>doneProgress = (latestTime-cursorTime) / (latestTime-earliestTime)
     * @see #getLatestTime
     * @see #getCursorTime
     * @see #getEarliestTime
     * 
     * @return This job's progress.
     */
    public float getDoneProgress() {
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
        return getInteger("dropCount");
    }

    /**
     * Returns the earliest time a search job is configured to start.
     * @see #getLatestTime
     * @see #getCursorTime
     * @see #getDoneProgress
     *
     * @return The earliest time, in UTC format.
     */
    public Date getEarliestTime() {
        return getDate("earliestTime");
    }

    /**
     * Returns the count of events stored by search that are available to be
     * retrieved from the events endpoint.
     *
     * @return The count of available events.
     */
    public int getEventAvailableCount() {
        return getInteger("eventAvailableCount");
    }

    /**
     * Returns the count of events (pre-transforming) that were generated.
     *
     * @return The number of events.
     */
    public int getEventCount() {
        return getInteger("eventCount");
    }

    /**
     * Returns the count of event fields.
     *
     * @return The number of event fields.
     */
    public int getEventFieldCount() {
        return getInteger("eventFieldCount");
    }

    /**
     * Indicates whether the events from this job are available by streaming.
     *
     * @return {@code true} if events can be streamed, {@code false} if not. 
     */
    public boolean getEventIsStreaming() {
        return getBoolean("eventIsStreaming");
    }

    /**
     * Indicates whether any events from this job have not been stored. 
     * @return {@code true} if the event return is truncated, {@code false} if
     * not.
     */
    public boolean getEventIsTruncated() {
        return getBoolean("eventIsTruncated");
    }

    /**
     * Returns the InputStream IO handle for this job's events.
     *
     * @return The event InputStream IO handle.
     */
    public InputStream getEvents() {
        return getEvents(null);
    }

    /**
     * Returns the InputStream IO handle for this job's events.
     *
     * @param args Optional arguments.
     *
     * @return The event InputStream IO handle.
     */
    public InputStream getEvents(Map args) {
        ResponseMessage response = service.get(path + "/events", args);
        return response.getContent();
    }

    /**
     * Returns the subset of the entire search that is before any transforming 
     * commands. The original search should be the eventSearch + reportSearch.
     * @see #getReportSearch
     * @return The event search query.
     */
    public String getEventSearch() {
        return getString("eventSearch", null);
    }

    /**
     * Returns a value that indicates how events are sorted. 
     *
     * @return {@code desc} if events are sorted in inverse time order (latest
     * first). {@code asc} if events are sorted in time order (oldest first),
     * or {@code none} if events are not sorted.
     */
    public String getEventSorting() {
        return getString("eventSorting");
    }

    /**
     * Returns all positive keywords used by this job. A positive keyword is 
     * a keyword that is not in a NOT clause.
     *
     * @return The job keywords.
     */
    public String getKeywords() {
        return getString("keywords", null);
    }

    /**
     * Returns this job's label.
     *
     * @return The job label.
     */
    public String getLabel() {
        return getString("label", null);
    }

    /**
     * Returns the latest time a search job is configured to start.
     * @see #getCursorTime
     * @see #getEarliestTime
     * @see #getDoneProgress
     *
     * @return The latest time, in UTC format.
     */
    public Date getLatestTime() {
        return getDate("latestTime");
    }

    /**
     * Returns this job's name (its SID).
     *
     * @return The job name.
     */
    @Override public String getName() {
        return getSid();
    }

    /**
     * Returns the number of previews that have been generated so far for this
     * job.
     *
     * @return The number of previews.
     */
    public int getNumPreviews() {
        return getInteger("numPreviews");
    }

    /**
     * Returns this job's priority in the range of 0-10.
     *
     * @return The job priority.
     */
    public int getPriority() {
        return getInteger("priority");
    }

    /**
     * Sets this job's priority in the range of 0-10.
     *
     * @param value The new priority.
     */
    public void setPriority(int value) {
        control("setpriority", new Args("priority", value));
    }

    /**
     * Returns the search string that is sent to every search peer for this job.
     *
     * @return The remote search query string.
     */
    public String getRemoteSearch() {
        return getString("remoteSearch", null);
    }

    /**
     * Returns the reporting subset of this search, which is the streaming part
     * of the search that is send to remote providers if reporting commands are
     * used. The original search should be the eventSearch + reportSearch.
     * @see #getEventSearch
     *
     * @return The reporting search query.
     */
    public String getReportSearch() {
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
        return getInteger("resultCount");
    }

    /**
     * Indicates whether the job's result is available by streaming.
     *
     * @return {@code true} if results can be streamed, {@code false} if not.
     */
    public boolean getResultIsStreaming() {
        return getBoolean("resultIsStreaming");
    }

    /**
     * Returns the number of result rows in the latest preview results for this
     * job.
     *
     * @return The number of result rows.
     */
    public int getResultPreviewCount() {
        return getInteger("resultPreviewCount");
    }

    /**
     * Returns the InputStream IO handle for the results from this job.
     *
     * @return The results InputStream IO handle.
     */
    public InputStream getResults() {
        return getResults(null);
    }

    /**
     * Returns the InputStream IO handle for the results from this job.
     *
     * @param args Optional arguments.
     * @return The results InputStream IO handle.
     */
    public InputStream getResults(Map args) {
        ResponseMessage response = service.get(path + "/results", args);
        return response.getContent();
    }

    /**
     * Returns the InputStream IO handle for the preview results from this job.
     *
     * @return The preview results InputStream IO handle.
     */
    public InputStream getResultsPreview() {
        return getResultsPreview(null);
    }

    /**
     * Returns the InputStream IO handle for the preview results from this job.
     *
     * @param  args Optional arguments.
     * @return The preview results InputStream IO handle.
     */
    public InputStream getResultsPreview(Map args) {
        ResponseMessage response = service.get(path + "/results_preview", args);
        return response.getContent();
    }

    /**
     * Returns the time that the search job took to complete.
     *
     * @return The run-time duration, in seconds.
     */
    public float getRunDuration() {
        return getFloat("runDuration");
    }

    /**
     * Returns the number of events that are scanned or read off disk.
     *
     * @return The number of events.
     */
    public int getScanCount() {
        return getInteger("scanCount");
    }

    /**
     * Returns this job's search title.
     *
     * @return The search title.
     */
    public String getSearch() {
        return getTitle();
    }

    /**
     * Returns the earliest time a search job is configured to start.
     * @see #getLatestTime
     * @see #getCursorTime
     * @see #getDoneProgress
     *
     * @return This earliest search time, in epoch format.
     */
    public String getSearchEarliestTime() {
        return getString("searchEarliestTime", null);
    }

    /**
     * Returns the latest time a search job is configured to start.
     * @see #getEarliestTime
     * @see #getCursorTime
     * @see #getDoneProgress
     *
     * @return The latest search time, in epoch format.
     */
    public String getSearchLatestTime() {
        return getString("searchLatestTime", null);
    }

    /**
     * Returns the InputStream IO handle to the search log for this job.
     *
     * @return The search log InputStream IO handle.
     */
    public InputStream getSearchLog() {
        return getSearchLog(null);
    }

    /**
     * Returns the InputStream IO handle to the search log for this job.
     *
     * @param args Optional arguments
     * @return The search log InputStream IO handle.
     */
    public InputStream getSearchLog(Map args) {
        ResponseMessage response = service.get(path + "/search.log", args);
        return response.getContent();
    }

    public String[] getSearchProviders() {
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
     * Returns this job's SID from within a response message.
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
     * Returns the InputStream IO handle for the summary for this job.
     *
     * @return The summary InputStream IO handle.
     */
    public InputStream getSummary() {
        return getSummary(null);
    }

    /**
     * Returns the InputStream IO handle for the summary for this job.
     *
     * @param args Optional arguments.
     * @return The summary InputStream IO handle.
     */
    public InputStream getSummary(Map args) {
        ResponseMessage response = service.get(path + "/summary", args);
        return response.getContent();
    }

    /**
     * Returns the maximum number of timeline buckets for this job.
     *
     * @return The number of timeline buckets.
     */
    public int getStatusBuckets() {
        return getInteger("statusBuckets");
    }

    /**
     * Returns the InputStream IO handle for the timeline for this job.
     *
     * @return The timeline InputStream IO handle.
     */
    public InputStream getTimeline() {
        return getTimeline(null);
    }

    /**
     * Returns the InputStream IO handle for the timeline for this job.
     *
     * @param args Optional arguments.
     * @return The timeline InputStream IO handle.
     */
    public InputStream getTimeline(Map args) {
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
        return getInteger("ttl");
    }

    /**
     * Indicates whether the job is done.
     *
     * @return {@code true} if the job is done, {@code false} if not.
     */
    public boolean isDone() {
        return getBoolean("isDone");
    }

    /**
     * Indicates whether the job failed.
     *
     * @return {@code true} if the job failed, {@code false} if not.
     */
    public boolean isFailed() {
        return getBoolean("isFailed");
    }

    /**
     * Indicates whether the job is finalized (forced to finish).
     *
     * @return {@code true} if the job is finalized, {@code false} if not.
     */
    public boolean isFinalized() {
        return getBoolean("isFinalized");
    }

    /**
     * Indicates whether the jobs is paused.
     *
     * @return {@code true} if the job is paused, {@code false} if not.
     */
    public boolean isPaused() {
        return getBoolean("isPaused");
    }

    /**
     * Indicates whether preview for the job is enabled.
     *
     * @return {@code true} if preview is enabled, {@code false} if not.
     */
    public boolean isPreviewEnabled() {
        return getBoolean("isPreviewEnabled");
    }

    /**
     * Indicates whether the job is a real-time search.
     *
     * @return {@code true} if the job is a real-time search, {@code false} if
     * not.
     */
    public boolean isRealTimeSearch() {
        return getBoolean("isRealTimeSearch");
    }

    /**
     * Indicates whether the job has a remote timeline component.
     *
     * @return {@code true} if the job has a remote timeline component,
     * {@code false} if not.
     */
    public boolean isRemoteTimeline() {
        return getBoolean("isRemoteTimeline");
    }

    /**
     * Indicates whether the job is to be saved indefinitely.
     *
     * @return {@code true} if the job has been saved, {@code false} if not.
     */
    public boolean isSaved() {
        return getBoolean("isSaved");
    }

    /**
     * Indicates whether this job was run as a saved search (via scheduler).
     *
     * @return {@code true} if the job is from a saved search, {@code false}
     * if not.
     */
    public boolean isSavedSearch() {
        return getBoolean("isSavedSearch");
    }

    /**
     * Indicates whether the process running the search is dead but with the
     * search not finished.
     *
     * @return {@code true} if the job is a zombie, {@code false} if not.
     */
    public boolean isZombie() {
        return getBoolean("isZombie");
    }

    // Job "entities" don't return an AtomFeed, only an AtomEntry.

    /**
     * Refreshes this job.
     *
     * @return This job.
     */
    @Override public Job refresh() {
        update();
        ResponseMessage response = service.get(path);
        if (response.getStatus() == 204) {
            // Empty response from server means the job has not yet been
            // scheduled; so throw an exception up to the caller.
            throw new SplunkException(SplunkException.JOB_NOTREADY,
                                      "Job not yet scheduled by server");
        }

        AtomEntry entry = AtomEntry.parse(response.getContent());
        load(entry);
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

