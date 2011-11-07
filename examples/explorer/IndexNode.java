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

import java.util.Date;

class IndexNode extends EntityNode {
    IndexNode(Entity entity) { 
        super(entity); 
    }

    @Override protected PropertyList getMetadata() {
        return new PropertyList() {{
            add(boolean.class, "getAssureUTF8");
            add(int.class, "getBlockSignSize");
            add(String.class, "getBlockSignatureDatabase");
            add(String.class, "getColdPath");
            add(String.class, "getColdPathExpanded");
            add(String.class, "getColdToFrozenDir");
            add(String.class, "getColdToFrozenScript");
            add(boolean.class, "getCompressRawdata");
            add(int.class, "getCurrentDBSizeMB");
            add(String.class, "getDefaultDatabase");
            add(boolean.class, "getEnableRealtimeSearch");
            add(int.class, "getFrozenTimePeriodInSecs");
            add(String.class, "getHomePath");
            add(String.class, "getHomePathExpanded");
            add(String.class, "getIndexThreads");
            add(String.class, "getLastInitTime");
            add(int.class, "getMaxConcurrentOptimizes");
            add(String.class, "getMaxDataSize");
            add(int.class, "getMaxHotBuckets");
            add(int.class, "getMaxHotIdleSecs");
            add(int.class, "getMaxHotIdleSecs");
            add(int.class, "getMaxHotSpanSecs");
            add(int.class, "getMaxMemMB");
            add(int.class, "getMaxMetaEntries");
            add(int.class, "getMaxRunningProcessGroups");
            add(Date.class, "getMaxTime");
            add(int.class, "getMaxTotalDataSizeMB");
            add(int.class, "getMaxWarmDBCount");
            add(int.class, "getMemPoolMB");
            add(String.class, "getMinRawFileSyncSecs");
            add(Date.class, "getMinTime");
            add(int.class, "getPartialServiceMetaPeriod");
            add(int.class, "getQuarantineFutureSecs");
            add(int.class, "getQuarantinePastSecs");
            add(int.class, "getRawChunkSizeBytes");
            add(int.class, "getRotatePeriodInSecs");
            add(int.class, "getServiceMetaPeriod");
            add(String.class, "getSuppressBannerList");
            add(boolean.class, "getSync");
            add(boolean.class, "getSyncMeta");
            add(String.class, "getThawedPath");
            add(String.class, "getThawedPathExpanded");
            add(int.class, "getThrottleCheckPeriod");
            add(int.class, "getTotalEventCount");
            add(boolean.class, "isDisabled");
            add(boolean.class, "isInternal");
        }};
    }
}
