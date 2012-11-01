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

class SavedSearchNode extends EntityNode {
    SavedSearchNode(Entity value) {
        super(value);
    }

    @Override protected PropertyList getMetadata() {
        PropertyList list = super.getMetadata();
        list.add(String.class, "getActionEmailSendResults");
        list.add(String.class, "getActionEmailTo");
        list.add(String.class, "getAlertExpires");
        list.add(int.class, "getAlertSeverity");
        list.add(String.class, "getAlertSuppress");
        list.add(String.class, "getAlertSuppressPeriod");
        list.add(String.class, "getAlertTrack");
        list.add(String.class, "getAlertComparator");
        list.add(String.class, "getAlertCondition");
        list.add(String.class, "getAlertThreshold");
        list.add(String.class, "getAlertType");
        list.add(String.class, "getCronSchedule");
        list.add(String.class, "getDescription");
        list.add(int.class, "getDispatchBuckets");
        list.add(String.class, "getDispatchEarliestTime");
        list.add(String.class, "getDispatchLatestTime");
        list.add(boolean.class, "getDispatchLookups");
        list.add(int.class, "getDispatchMaxCount");
        list.add(String.class, "getDispatchMaxTime");
        list.add(int.class, "getDispatchReduceFreq");
        list.add(boolean.class, "getDispatchSpawnProcess");
        list.add(String.class, "getDispatchTimeFormat");
        list.add(String.class, "getDispatchTtl");
        list.add(String.class, "getDisplayView");
        list.add(int.class, "getMaxConcurrent");
        list.add(String.class, "getNextScheduledTime");
        list.add(String.class, "getQualifiedSearch");
        list.add(boolean.class, "getRealtimeSchedule");
        list.add(String.class, "getRequestUiDispatchApp");
        list.add(String.class, "getRequestUiDispatchView");
        list.add(boolean.class, "getRestartOnSearchPeerAdd");
        list.add(boolean.class, "getRunOnStartup");
        list.add(String.class, "getSearch");
        list.add(String.class, "getVsid");
        list.add(boolean.class, "isActionEmail");
        list.add(boolean.class, "isActionPopulateLookup");
        list.add(boolean.class, "isActionRss");
        list.add(boolean.class, "isActionScript");
        list.add(boolean.class, "isActionSummaryIndex");
        list.add(boolean.class, "isDigestMode");
        list.add(boolean.class, "isDisabled");
        list.add(boolean.class, "isScheduled");
        list.add(boolean.class, "isVisible");
        return list;
    }
}
