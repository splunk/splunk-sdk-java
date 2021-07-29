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

package com.splunk.examples.explorer;

import com.splunk.Entity;
import com.splunk.Job;

import java.util.Date;

class JobNode extends EntityNode {
    JobNode(Entity value) {
        super(value);
        Job job = (Job)value;
        setDisplayName(job.getTitle());
    }

    @Override protected PropertyList getMetadata() {
        PropertyList list = super.getMetadata();
        list.add(Date.class, "getCursorTime");
        list.add(String.class, "getDelegate");
        list.add(int.class, "getDiskUsage");
        list.add(String.class, "getDispatchState");
        list.add(float.class, "getDoneProgress");
        list.add(int.class, "getDropCount");
        list.add(Date.class, "getEarliestTime");
        list.add(int.class, "getEventAvailableCount");
        list.add(int.class, "getEventCount");
        list.add(int.class, "getEventFieldCount");
        list.add(boolean.class, "getEventIsStreaming");
        list.add(boolean.class, "getEventIsTruncated");
        list.add(String.class, "getEventSearch");
        list.add(String.class, "getEventSorting");
        list.add(String.class, "getKeywords");
        list.add(String.class, "getLabel");
        list.add(Date.class, "getLatestTime");
        list.add(int.class, "getNumPreviews");
        list.add(int.class, "getPriority");
        list.add(String.class, "getRemoteSearch");
        list.add(String.class, "getReportSearch");
        list.add(int.class, "getResultCount");
        list.add(boolean.class, "getResultIsStreaming");
        list.add(int.class, "getResultPreviewCount");
        list.add(float.class, "getRunDuration");
        list.add(int.class, "getScanCount");
        list.add(String.class, "getSearch");
        list.add(String.class, "getSearchLatestTime");
        list.add(String.class, "getSid");
        list.add(int.class, "getStatusBuckets");
        list.add(int.class, "getTtl");
        list.add(boolean.class, "isDone");
        list.add(boolean.class, "isFailed");
        list.add(boolean.class, "isFinalized");
        list.add(boolean.class, "isPaused");
        list.add(boolean.class, "isPreviewEnabled");
        list.add(boolean.class, "isRemoteTimeline");
        list.add(boolean.class, "isSaved");
        list.add(boolean.class, "isSavedSearch");
        list.add(boolean.class, "isZombie");
        return list;
    }
}
