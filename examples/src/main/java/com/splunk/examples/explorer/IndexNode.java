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

import java.util.Date;

class IndexNode extends EntityNode {
    IndexNode(Entity value) { 
        super(value); 
    }

    @Override protected PropertyList getMetadata() {
        PropertyList list = super.getMetadata();
        list.add(boolean.class, "getAssureUTF8");
        list.add(int.class, "getBlockSignSize");
        list.add(String.class, "getBlockSignatureDatabase");
        list.add(String.class, "getColdPath");
        list.add(String.class, "getColdPathExpanded");
        list.add(String.class, "getColdToFrozenDir");
        list.add(String.class, "getColdToFrozenScript");
        list.add(boolean.class, "getCompressRawdata");
        list.add(int.class, "getCurrentDBSizeMB");
        list.add(String.class, "getDefaultDatabase");
        list.add(boolean.class, "getEnableRealtimeSearch");
        list.add(int.class, "getFrozenTimePeriodInSecs");
        list.add(String.class, "getHomePath");
        list.add(String.class, "getHomePathExpanded");
        list.add(String.class, "getIndexThreads");
        list.add(String.class, "getLastInitTime");
        list.add(int.class, "getMaxConcurrentOptimizes");
        list.add(String.class, "getMaxDataSize");
        list.add(int.class, "getMaxHotBuckets");
        list.add(int.class, "getMaxHotIdleSecs");
        list.add(int.class, "getMaxHotIdleSecs");
        list.add(int.class, "getMaxHotSpanSecs");
        list.add(int.class, "getMaxMemMB");
        list.add(int.class, "getMaxMetaEntries");
        list.add(int.class, "getMaxRunningProcessGroups");
        list.add(Date.class, "getMaxTime");
        list.add(int.class, "getMaxTotalDataSizeMB");
        list.add(int.class, "getMaxWarmDBCount");
        list.add(int.class, "getMemPoolMB");
        list.add(String.class, "getMinRawFileSyncSecs");
        list.add(Date.class, "getMinTime");
        list.add(int.class, "getPartialServiceMetaPeriod");
        list.add(int.class, "getQuarantineFutureSecs");
        list.add(int.class, "getQuarantinePastSecs");
        list.add(int.class, "getRawChunkSizeBytes");
        list.add(int.class, "getRotatePeriodInSecs");
        list.add(int.class, "getServiceMetaPeriod");
        list.add(String.class, "getSuppressBannerList");
        list.add(boolean.class, "getSync");
        list.add(boolean.class, "getSyncMeta");
        list.add(String.class, "getThawedPath");
        list.add(String.class, "getThawedPathExpanded");
        list.add(int.class, "getThrottleCheckPeriod");
        list.add(int.class, "getTotalEventCount");
        list.add(boolean.class, "isDisabled");
        list.add(boolean.class, "isInternal");
        return list;
    }
}
