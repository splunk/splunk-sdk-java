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

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

/**
 * Representation of a search job.
 */
public class Job extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     * @param path The jobs search endpoint.
     */
    Job(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the action path.
     *
     * @param action Action requested.
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
     * @param action The action to perform
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
     * Disables preview generation on this job.
     *
     * @return The job.
     */
    public Job disablePreview() {
        return control("disablepreview");
    }

    /**
     * Enables preview generation on this job (may slow search considerably).
     *
     * @return The job.
     */
    public Job enablePreview() {
        return control("enablepreview");
    }

    /**
     * Stops the search, and provides intermediate results available for
     * retrieval.
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
     * Returns this job's cursorTime attribute.
     *
     * @return This job's cursorTime attribute.
     */
    public Date getCursorTime() {
        return getDate("cursorTime");
    }

    /**
     * Returns this job's delegate attribute.
     *
     * @return This job's delegate attribute.
     */
    public String getDelegate() {
        return getString("delegate", null);
    }

    /**
     * Returns this job's disk usage, in bytes.
     *
     * @return This job's disk usage, in bytes.
     */
    public int getDiskUsage() {
        return getInteger("diskUsage");
    }

    /**
     * Returns this job's dispatch state. Valid values are from the set:
     * QUEUED, PARSING, RUNNING, PAUSED, FINALIZING, FAILED or DONE.
     *
     * @return This job's dispatch state.
     */
    public String getDispatchState() {
        return getString("dispatchState");
    }

    /**
     * Returns this job's progress, in the range of 0.0 to 1.0.
     *
     * @return This job's progress.
     */
    public float getDoneProgress() {
        return getFloat("doneProgress");
    }

    /**
     * Returns this job's dropCount attribute.
     *
     * @return This jobs dropCount attribute.
     */
    public int getDropCount() {
        return getInteger("dropCount");
    }

    /**
     * Returns this job's earliestTime attribute.
     *
     * @return This job's earlietTime attribute.
     */
    public Date getEarliestTime() {
        return getDate("earliestTime");
    }

    /**
     * Returns this job's eventAvailableCount attribute.
     *
     * @return This job's eventAvailableCount attribute.
     */
    public int getEventAvailableCount() {
        return getInteger("eventAvailableCount");
    }

    /**
     * Returns this job's number of events being returned.
     *
     * @return This job's number of events being returned.
     */
    public int getEventCount() {
        return getInteger("eventCount");
    }

    /**
     * Returns this job's eventFieldCount attribute.
     *
     * @return This job's eventFieldCount attribute.
     */
    public int getEventFieldCount() {
        return getInteger("eventFieldCount");
    }

    /**
     * Returns whether or not this job's event return is streaming.
     *
     * @return Whether or not this job's event return is streaming.
     */
    public boolean getEventIsStreaming() {
        return getBoolean("eventIsStreaming");
    }

    /**
     * Returns whether or not this job's event return is truncated.
     *
     * @return Whether or not this job's event return is truncated.
     */
    public boolean getEventIsTruncated() {
        return getBoolean("eventIsTruncated");
    }

    /**
     * Returns this job's event InputStream IO handle.
     *
     * @return This job's event InputStream IO handle.
     */
    public InputStream getEvents() {
        return getEvents(null);
    }

    /**
     * Returns this job's event InputStream IO handle.
     *
     * @param args Optional arguments to the event get.
     *
     * @return This job's event InputStream IO handle.
     */
    public InputStream getEvents(Map args) {
        ResponseMessage response = service.get(path + "/events", args);
        return response.getContent();
    }

    /**
     * Returns this job's original search query.
     *
     * @return This job's original search query.
     */
    public String getEventSearch() {
        return getString("eventSearch", null);
    }

    /**
     * Returns this job's eventSorting attribute.
     *
     * @return This job's eventSorting attribute.
     */
    public String getEventSorting() {
        return getString("eventSorting");
    }

    /**
     * Returns this job's keywords attribute.
     *
     * @return This job's keywords attribute.
     */
    public String getKeywords() {
        return getString("keywords", null);
    }

    /**
     * Returns this job's label.
     *
     * @return This job's label.
     */
    public String getLabel() {
        return getString("label", null);
    }

    /**
     * Returns this job's latestTime attribute.
     *
     * @return This job's latestTime attribute.
     */
    public Date getLatestTime() {
        return getDate("latestTime");
    }

    /**
     * Returns this job's name, or more aptly its SID.
     *
     * @return This job's name.
     */
    @Override public String getName() {
        return getSid();
    }

    /**
     * Returns this job's numPreviews attribute.
     *
     * @return This job's numPreviews attribute.
     */
    public int getNumPreviews() {
        return getInteger("numPreviews");
    }

    /**
     * Returns this job's priority in the range of 0-10.
     *
     * @return This job's priority.
     */
    public int getPriority() {
        return getInteger("priority");
    }

    /**
     * Sets this job's priority in the range of 0-10.
     *
     * @param value This job's new priority.
     */
    public void setPriority(int value) {
        control("setpriority", new Args("priority", value));
    }

    /**
     * Returns this job's remote search query string.
     *
     * @return This job's remote search query string.
     */
    public String getRemoteSearch() {
        return getString("remoteSearch", null);
    }

    /**
     * Returns this job's reportSearch attribute.
     *
     * @return This job's reportSearch attribute.
     */
    public String getReportSearch() {
        return getString("reportSearch", null);
    }

    /**
     * Returns this job's resultCount attribute.
     *
     * @return This job's resultCount attribute.
     */
    public int getResultCount() {
        return getInteger("resultCount");
    }

    /**
     * Returns whether or not this job's result is streaming.
     *
     * @return Whether or not this job's result is streaming.
     */
    public boolean getResultIsStreaming() {
        return getBoolean("resultIsStreaming");
    }

    /**
     * Returns this job's resultPreviewCount attribute.
     *
     * @return This job's resultPreviewCount attribute.
     */
    public int getResultPreviewCount() {
        return getInteger("resultPreviewCount");
    }

    /**
     * Returns this job's results InputStream IO handle.
     *
     * @return This job's results InputStream IO handle.
     */
    public InputStream getResults() {
        return getResults(null);
    }

    /**
     * Returns this job's results InputStream IO handle.
     *
     * @param args Optional arguments.
     * @return This job's results InputStream IO handle.
     */
    public InputStream getResults(Map args) {
        ResponseMessage response = service.get(path + "/results", args);
        return response.getContent();
    }

    /**
     * Returns this job's preview results InputStream IO handle.
     *
     * @return This job's preview results InputStream IO handle.
     */
    public InputStream getResultsPreview() {
        return getResultsPreview(null);
    }

    /**
     * Returns this job's preview results InputStream IO handle.
     *
     * @param  args Optional arguments.
     * @return This job's preview results InputStream IO handle.
     */
    public InputStream getResultsPreview(Map args) {
        ResponseMessage response = service.get(path + "/results_preview", args);
        return response.getContent();
    }

    /**
     * Returns this job's run-time duration.
     *
     * @return This job's run-time duration.
     */
    public float getRunDuration() {
        return getFloat("runDuration");
    }

    /**
     * Returns this job's scanCount attribute.
     *
     * @return This job's scanCount attribute.
     */
    public int getScanCount() {
        return getInteger("scanCount");
    }

    /**
     * Returns this job's search title.
     *
     * @return This job's search title.
     */
    public String getSearch() {
        return getTitle();
    }

    /**
     * Returns this job's earliest search time.
     *
     * @return This job's earliest search time.
     */
    public String getSearchEarliestTime() {
        return getString("searchEarliestTime", null);
    }

    /**
     * Returns this job's latest search time.
     *
     * @return This job's latest search time.
     */
    public String getSearchLatestTime() {
        return getString("searchLatestTime", null);
    }

    /**
     * Returns this job's search log InputStream IO handle.
     *
     * @return This job's search log InputStream IO handle.
     */
    public InputStream getSearchLog() {
        return getSearchLog(null);
    }

    /**
     * Returns this job's search log InputStream IO handle.
     *
     * @param args Optional arguments
     * @return This job's search log InputStream IO handle.
     */
    public InputStream getSearchLog(Map args) {
        ResponseMessage response = service.get(path + "/search.log", args);
        return response.getContent();
    }

    /**
     * Returns this job's SID.
     *
     * @return This job's SID.
     */
    public String getSid() {
        return getString("sid");
    }

    /**
     * Returns this job's SID from within a response message
     *
     * @param response The response message.
     * @return This job's SID.
     */
    static String getSid(ResponseMessage response) {
        return Xml.parse(response.getContent())
            .getElementsByTagName("sid").item(0).getTextContent();
    }

    /**
     * Returns this jobs summary InputStream IO handle.
     *
     * @return This job's summary InputStream IO handle.
     */
    public InputStream getSummary() {
        return getSummary(null);
    }

    /**
     * Returns this jobs summary InputStream IO handle.
     *
     * @param args Optional arguments.
     * @return This job's summary InputStream IO handle.
     */
    public InputStream getSummary(Map args) {
        ResponseMessage response = service.get(path + "/summary", args);
        return response.getContent();
    }

    /**
     * Returns this job's statusBuckets attribute.
     *
     * @return This job's statusBuckets attribute.
     */
    public int getStatusBuckets() {
        return getInteger("statusBuckets");
    }

    /**
     * Returns this job's timeline InputStream IO handle.
     *
     * @return this job's timeline InputStream IO handle.
     */
    public InputStream getTimeline() {
        return getTimeline(null);
    }

    /**
     * Returns this job's timeline InputStream IO handle.
     *
     * @param args Optional arguments.
     * @return this job's timeline InputStream IO handle.
     */
    public InputStream getTimeline(Map args) {
        ResponseMessage response = service.get(path + "/timeline", args);
        return response.getContent();
    }

    /**
     * Returns this job's time-to-live.
     *
     * @return This job's time-to-live.
     */
    public int getTtl() {
        return getInteger("ttl");
    }

    /**
     * Returns whether or not this job is done.
     *
     * @return Whether or not this jobs is done.
     */
    public boolean isDone() {
        return getBoolean("isDone");
    }

    /**
     * Returns whether or not this job failed.
     *
     * @return Whether or not this job failed.
     */
    public boolean isFailed() {
        return getBoolean("isFailed");
    }

    /**
     * Returns whether or not this job is finalized.
     *
     * @return Whether or not this jobs is finalized.
     */
    public boolean isFinalized() {
        return getBoolean("isFinalized");
    }

    /**
     * Returns whether or not this jobs is paused.
     *
     * @return Whether or not this jobs is paused.
     */
    public boolean isPaused() {
        return getBoolean("isPaused");
    }

    /**
     * Returns whether or not this job's preview is enabled.
     *
     * @return Whether or not this job's preview is enabled.
     */
    public boolean isPreviewEnabled() {
        return getBoolean("isPreviewEnabled");
    }

    /**
     * Returns whether or not this job is a real time search.
     *
     * @return Whether or not this job is a real time search.
     */
    public boolean isRealTimeSearch() {
        return getBoolean("isRealTimeSearch");
    }

    /**
     * Returns whether or not this job has a remote timeline component.
     *
     * @return whether or not this job has a remote timeline component.
     */
    public boolean isRemoteTimeline() {
        return getBoolean("isRemoteTimeline");
    }

    /**
     * Returns this job's isSaved attribute.
     *
     * @return this job's isSaved attribute.
     */
    public boolean isSaved() {
        return getBoolean("isSaved");
    }

    /**
     * Return whether or not this search is a saved-search.
     *
     * @return Whether or not this search is a saved-search.
     */
    public boolean isSavedSearch() {
        return getBoolean("isSavedSearch");
    }

    /**
     * returns this job's isZombie attribute.
     *
     * @return Thsi job's isZombie attribute.
     */
    public boolean isZombie() {
        return getBoolean("isZombie");
    }

    // Job "entities" don't return an AtomFeed, only an AtomEntry.

    /**
     * Refresh this job.
     *
     * @return This job.
     */
    @Override public Job refresh() {
        ResponseMessage response = service.get(path);
        AtomEntry entry = AtomEntry.parse(response.getContent());
        load(entry);
        return this;
    }

    /**
     * Removes this job; which is unsupported and will throw an exception.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

