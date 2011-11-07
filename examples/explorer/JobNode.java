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

import com.splunk.Entity;
import com.splunk.Job;

import java.util.Date;

class JobNode extends EntityNode {
    JobNode(Entity entity) {
        super(entity);
        Job job = (Job)entity;
        setDisplayName(job.getTitle());
    }

    @Override protected PropertyList getMetadata() {
        return new PropertyList() {{
            add(Date.class, "getCursorTime");
            add(String.class, "getDelegate");
            add(int.class, "getDiskUsage");
            add(String.class, "getDispatchState");
            add(float.class, "getDoneProgress");
            add(int.class, "getDropCount");
            add(Date.class, "getEarliestTime");
            add(int.class, "getEventAvailableCount");
            add(int.class, "getEventCount");
            add(int.class, "getEventFieldCount");
            add(boolean.class, "getEventIsStreaming");
            add(boolean.class, "getEventIsTruncated");
            add(String.class, "getEventSearch");
            add(String.class, "getEventSorting");
            add(String.class, "getKeywords");
            add(String.class, "getLabel");
            add(Date.class, "getLatestTime");
            add(int.class, "getNumPreviews");
            add(int.class, "getPriority");
            add(String.class, "getRemoteSearch");
            add(String.class, "getReportSearch");
            add(int.class, "getResultCount");
            add(boolean.class, "getResultIsStreaming");
            add(int.class, "getResultPreviewCount");
            add(float.class, "getRunDuration");
            add(int.class, "getScanCount");
            add(String.class, "getSearch");
            add(String.class, "getSearchLatestTime");
            add(String.class, "getSid");
            add(int.class, "getStatusBuckets");
            add(int.class, "getTtl");
            add(boolean.class, "isDone");
            add(boolean.class, "isFailed");
            add(boolean.class, "isFinalized");
            add(boolean.class, "isPaused");
            add(boolean.class, "isPreviewEnabled");
            add(boolean.class, "isRemoteTimeline");
            add(boolean.class, "isSaved");
            add(boolean.class, "isSavedSearch");
            add(boolean.class, "isZombie");
        }};
    }
}
