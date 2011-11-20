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

// UNDONE: performance property

package com.splunk;

import java.io.InputStream;
import java.util.Date;

public class Job extends Entity {
    public Job(Service service, String path) {
        super(service, path);
    }

    public Job control(String action) {
        return control(action, null);
    }

    public Job control(String action, Args extra) {
        Args args = new Args("action", action);
        if (extra != null) args.putAll(extra);
        service.post(actionPath("control"), args);
        invalidate();
        return this;
    }

    public Job cancel() {
        return control("cancel");
    }

    public Job disablePreview() {
        return control("disablepreview");
    }

    public Job enablePreview() {
        return control("enablepreview");
    }

    public Job finish() {
        return control("finalize");
    }

    public Job pause() {
        return control("pause");
    }

    public Date getCursorTime() {
        return getDate("cursorTime");
    }

    public String getDelegate() {
        return getString("delegate", null);
    }

    public int getDiskUsage() {
        return getInteger("diskUsage");
    }

    public String getDispatchState() {
        return getString("dispatchState");
    }

    public float getDoneProgress() {
        return getFloat("doneProgress");
    }

    public int getDropCount() {
        return getInteger("dropCount");
    }

    public Date getEarliestTime() {
        return getDate("earliestTime");
    }

    public int getEventAvailableCount() {
        return getInteger("eventAvailableCount");
    }

    public int getEventCount() {
        return getInteger("eventCount");
    }

    public int getEventFieldCount() {
        return getInteger("eventFieldCount");
    }

    public boolean getEventIsStreaming() {
        return getBoolean("eventIsStreaming");
    }

    public boolean getEventIsTruncated() {
        return getBoolean("eventIsTruncated");
    }

    public InputStream getEvents() {
        return getEvents(null);
    }

    public InputStream getEvents(Args args) {
        ResponseMessage response = get("events", args);
        return response.getContent();
    }

    public String getEventSearch() {
        return getString("eventSearch", null);
    }

    public String getEventSorting() {
        return getString("eventSorting");
    }

    public String getKeywords() {
        return getString("keywords", null);
    }

    public String getLabel() {
        return getString("label", null);
    }

    public Date getLatestTime() {
        return getDate("latestTime");
    }

    @Override public String getName() {
        return getSid();
    }

    public int getNumPreviews() {
        return getInteger("numPreviews");
    }

    public int getPriority() {
        return getInteger("priority");
    }

    public void setPriority(int value) {
        control("setpriority", new Args("priority", value));
    }

    public String getRemoteSearch() {
        return getString("remoteSearch", null);
    }

    public String getReportSearch() {
        return getString("reportSearch", null);
    }

    public int getResultCount() {
        return getInteger("resultCount");
    }

    public boolean getResultIsStreaming() {
        return getBoolean("resultIsStreaming");
    }

    public int getResultPreviewCount() {
        return getInteger("resultPreviewCount");
    }

    public InputStream getResults() {
        return getResults(null);
    }

    public InputStream getResults(Args args) {
        ResponseMessage response = get("results", args);
        return response.getContent();
    }

    public InputStream getResultsPreview() {
        return getResultsPreview(null);
    }

    public InputStream getResultsPreview(Args args) {
        ResponseMessage response = get("results_preview", args);
        return response.getContent();
    }

    public float getRunDuration() {
        return getFloat("runDuration");
    }

    public int getScanCount() {
        return getInteger("scanCount");
    }

    public String getSearch() {
        return getTitle();
    }

    public String getSearchEarliestTime() {
        return getString("searchEarliestTime", null);
    }

    public String getSearchLatestTime() {
        return getString("searchLatestTime", null);
    }

    public InputStream getSearchLog() {
        return getSearchLog(null);
    }

    public InputStream getSearchLog(Args args) {
        ResponseMessage response = get("search.log", args);
        return response.getContent();
    }

    public String getSid() {
        return getString("sid");
    }

    public InputStream getSummary() {
        return getSummary(null);
    }

    public InputStream getSummary(Args args) {
        ResponseMessage response = get("summary", args);
        return response.getContent();
    }

    public int getStatusBuckets() {
        return getInteger("statusBuckets");
    }

    public InputStream getTimeline() {
        return getTimeline(null);
    }

    public InputStream getTimeline(Args args) {
        ResponseMessage response = get("timeline", args);
        return response.getContent();
    }

    public int getTtl() {
        return getInteger("ttl");
    }

    public boolean isDone() {
        return getBoolean("isDone");
    }

    public boolean isFailed() {
        return getBoolean("isFailed");
    }

    public boolean isFinalized() {
        return getBoolean("isFinalized");
    }

    public boolean isPaused() {
        return getBoolean("isPaused");
    }

    public boolean isPreviewEnabled() {
        return getBoolean("isPreviewEnabled");
    }

    public boolean isRealTimeSearch() {
        return getBoolean("isRealTimeSearch");
    }

    public boolean isRemoteTimeline() {
        return getBoolean("isRemoteTimeline");
    }

    public boolean isSaved() {
        return getBoolean("isSaved");
    }

    public boolean isSavedSearch() {
        return getBoolean("isSavedSearch");
    }

    public boolean isZombie() {
        return getBoolean("isZombie");
    }

    // Job "entities" dont return an AtomFeed, only an AtomEntry.
    @Override public void refresh() {
        ResponseMessage response = get();
        AtomEntry entry = AtomEntry.parse(response.getContent());
        load(entry);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}

