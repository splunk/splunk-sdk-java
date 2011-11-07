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

class SavedSearchNode extends EntityNode {
    SavedSearchNode(Entity entity) {
        super(entity);
    }

    @Override protected PropertyList getMetadata() {
        return new PropertyList() {{
            add(String.class, "getActionEmailSendResults");
            add(String.class, "getActionEmailTo");
            add(String.class, "getAlertExpires");
            add(int.class, "getAlertSeverity");
            add(String.class, "getAlertSuppress");
            add(String.class, "getAlertSuppressPeriod");
            add(String.class, "getAlertTrack");
            add(String.class, "getAlertComparator");
            add(String.class, "getAlertCondition");
            add(String.class, "getAlertThreshold");
            add(String.class, "getAlertType");
            add(String.class, "getCronSchedule");
            add(String.class, "getDescription");
            add(int.class, "getDispatchBuckets");
            add(String.class, "getDispatchEarliestTime");
            add(String.class, "getDispatchLatestTime");
            add(boolean.class, "getDispatchLookups");
            add(int.class, "getDispatchMaxCount");
            add(String.class, "getDispatchMaxTime");
            add(int.class, "getDispatchReduceFreq");
            add(boolean.class, "getDispatchSpawnProcess");
            add(String.class, "getDispatchTimeFormat");
            add(String.class, "getDispatchTtl");
            add(String.class, "getDisplayView");
            add(int.class, "getMaxConcurrent");
            add(String.class, "getNextScheduledTime");
            add(String.class, "getQualifiedSearch");
            add(boolean.class, "getRealtimeSchedule");
            add(String.class, "getRequestUiDispatchApp");
            add(String.class, "getRequestUiDispatchView");
            add(boolean.class, "getRestartOnSearchPeerAdd");
            add(boolean.class, "getRunOnStartup");
            add(String.class, "getSearch");
            add(String.class, "getVsid");
            add(boolean.class, "isActionEmail");
            add(boolean.class, "isActionPopulateLookup");
            add(boolean.class, "isActionRss");
            add(boolean.class, "isActioncScript");
            add(boolean.class, "isActionSummaryIndex");
            add(boolean.class, "isDigestMode");
            add(boolean.class, "isDisabled");
            add(boolean.class, "isScheduled");
            add(boolean.class, "isVisible");
        }};
    }
}
