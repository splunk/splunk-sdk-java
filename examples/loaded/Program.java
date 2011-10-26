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

import com.splunk.*;

import java.util.Date;
import java.util.Map;

public class Program extends com.splunk.sdk.Program {
    public static void main(String[] args) {
        Program program = new Program();
        try {
            program.init(args).run();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Service connect() {
        Service service = new Service(this.host, this.port, this.scheme);
        service.login(this.username, this.password);
        return service;
    }

    public void printActions(Map<String, String> actions) {
        for (Map.Entry entry : actions.entrySet()) {
            System.out.format("* %s => %s\n", 
                entry.getKey(), entry.getValue());
        }
    }

    public void printEntity(Entity entity) {
        if (entity == null) {
            System.out.println("null\n");
            return;
        }
        System.out.format("## %s\n", entity.getPath());
        System.out.format("title = %s\n", entity.getTitle());
        printActions(entity.getActions());
        Map<String, Object> content = entity.getContent();
        if (content != null) {
            for (Map.Entry entry : content.entrySet()) {
                System.out.format("%s = %s\n",
                    entry.getKey(), entry.getValue().toString());
            }
        }
        System.out.println("");
    }

    public void printEntities(EntityCollection entities) {
        System.out.format("# %s\n", entities.getPath());
        printActions(entities.getActions());
        for (Entity entity : entities.values()) 
            printEntity(entity);
    }

    void printField(String field, boolean value) {
        System.out.format("%s = %b\n", field, value);
    }

    void printField(String field, float value) {
        System.out.format("%s = %f\n", field, value);
    }

    void printField(String field, Date value) {
        System.out.format("%s = %s\n", 
            field, value == null ? "null" : value.toString());
    }

    void printField(String field, int value) {
        System.out.format("%s = %d\n", field, value);
    }

    void printField(String field, String value) {
        System.out.format("%s = %s\n", field, value == null ? "null" : value);
    }

    void printIndex(Index index) {
        System.out.format("## %s\n", index.getTitle());
        printField("AssureUTF8", index.getAssureUTF8());
        printField("BlockSignSize", index.getBlockSignSize());
        printField("BlockSignatureDatabase", index.getBlockSignatureDatabase());
        printField("ColdPath", index.getColdPath());
        printField("ColdPathExpanded", index.getColdPathExpanded());
        printField("ColdToFrozenDir", index.getColdToFrozenDir());
        printField("ColdToFrozenScript", index.getColdToFrozenScript());
        printField("CompressRawdata", index.getCompressRawdata());
        printField("CurrentDBSizeMB", index.getCurrentDBSizeMB());
        printField("DefaultDatabase", index.getDefaultDatabase());
        printField("EnableRealtimeSearch", index.getEnableRealtimeSearch());
        printField("FrozenTimePeriodInSecs", index.getFrozenTimePeriodInSecs());
        printField("HomePath", index.getHomePath());
        printField("HomePathExpanded", index.getHomePathExpanded());
        printField("IndexThreads", index.getIndexThreads());
        printField("LastInitTime", index.getLastInitTime());
        printField("MaxConcurrentOptimizes", index.getMaxConcurrentOptimizes());
        printField("MaxDataSize", index.getMaxDataSize());
        printField("MaxHotBuckets", index.getMaxHotBuckets());
        printField("MaxHotIdleSecs", index.getMaxHotIdleSecs());
        printField("MaxHotSpanSecs", index.getMaxHotSpanSecs());
        printField("MaxMemMB", index.getMaxMemMB());
        printField("MaxMetaEntries", index.getMaxMetaEntries());
        printField("MaxRunningProcessGroups", index.getMaxRunningProcessGroups());
        printField("MaxTime", index.getMaxTime());
        printField("MaxTotalDataSizeMB", index.getMaxTotalDataSizeMB());
        printField("MaxWarmDBCount", index.getMaxWarmDBCount());
        printField("MemPoolMB", index.getMemPoolMB());
        printField("MinRawFileSyncSecs", index.getMinRawFileSyncSecs());
        printField("MinTime", index.getMinTime());
        printField("PartialServiceMetaPeriod", index.getPartialServiceMetaPeriod());
        printField("QuarantineFutureSecs", index.getQuarantineFutureSecs());
        printField("QuarantimePastSecs", index.getQuarantinePastSecs());
        printField("RawChunkSizeBytes", index.getRawChunkSizeBytes());
        printField("RotatePeriodInSecs", index.getRotatePeriodInSecs());
        printField("ServiceMetaPeriod", index.getServiceMetaPeriod());
        printField("SuppressBannerList", index.getSuppressBannerList());
        printField("Sync", index.getSync());
        printField("SyncMeta", index.getSyncMeta());
        printField("ThawedPath", index.getThawedPath());
        printField("ThawedPathExpanded", index.getThawedPathExpanded());
        printField("ThrottleCheckPeriod", index.getThrottleCheckPeriod());
        printField("TotalEventCount", index.getTotalEventCount());
        printField("isDisabled", index.isDisabled());
        printField("isInternal", index.isInternal());
        System.out.println("");
    }

    void printJob(Job job) {
        System.out.format("## %s\n", job.getTitle());
        printField("CursorTime", job.getCursorTime().toString());
        printField("delegate", job.getDelegate());
        printField("DiskUsage", job.getDiskUsage());
        printField("DispatchState", job.getDispatchState());
        printField("DoneProgress", job.getDoneProgress());
        printField("DropCount", job.getDropCount());
        printField("EarliestTime", job.getEarliestTime().toString());
        printField("EventAvailableCount", job.getEventAvailableCount());
        printField("EventCount", job.getEventCount());
        printField("EventFieldCount", job.getEventFieldCount());
        printField("EventIsStreaming", job.getEventIsStreaming());
        printField("EventIsTruncated", job.getEventIsTruncated());
        printField("EventSearch", job.getEventSearch());
        printField("EventSorting", job.getEventSorting());
        printField("Keywords", job.getKeywords());
        printField("Label", job.getLabel());
        printField("LatestTime", job.getLatestTime().toString());
        printField("NumPreviews", job.getNumPreviews());
        printField("Priority", job.getPriority());
        printField("RemoteSearch", job.getRemoteSearch());
        printField("ReportSearch", job.getReportSearch());
        printField("ResultCount", job.getResultCount());
        printField("ResultIsStreaming", job.getResultIsStreaming());
        printField("ResultPreviewCount", job.getResultPreviewCount());
        printField("RunDuration", job.getRunDuration());
        printField("ScanCount", job.getScanCount());
        printField("Search", job.getSearch());
        printField("SearchEarliestTime", job.getSearchEarliestTime());
        printField("SearchLatestTime", job.getSearchLatestTime());
        printField("Sid", job.getSid());
        printField("StatusBuckets", job.getStatusBuckets());
        printField("Ttl", job.getTtl());
        printField("IsDone", job.isDone());
        printField("IsFailed", job.isFailed());
        printField("IsPaused", job.isPaused());
        printField("IsPreviewEnabled", job.isPreviewEnabled());
        printField("IsRealTimeSearch", job.isRealTimeSearch());
        printField("IsRemoteTimeline", job.isRemoteTimeline());
        printField("IsSaved", job.isSaved());
        printField("IsSavedSearch", job.isSavedSearch());
        printField("IsZombie", job.isZombie());
        System.out.println("");
    }

    public void run() throws Exception {
        Service service = connect();

        System.out.println("**** Applications ****");
        printEntities(service.getApplications());

        System.out.println("**** Capabilities ****");
        for (String capability : service.getCapabilities())
            System.out.println(capability);
        System.out.println("");

        System.out.println("**** DeploymentClient ****");
        printEntity(service.getDeploymentClient());

        System.out.println("**** DeploymentServers ****");
        printEntities(service.getDeploymentServers());

        System.out.println("**** DeploymentServerClasses ****");
        printEntities(service.getDeploymentServerClasses());

        System.out.println("**** DeploymentTenants ****");
        printEntities(service.getDeploymentTenants());

        System.out.println("**** EventTypes ****");
        printEntities(service.getEventTypes());

        System.out.println("**** Indexes ****");
        for (Entity index : service.getIndexes().values())
            printIndex((Index)index);

        System.out.println("**** Jobs ****");
        for (Entity job : service.getJobs().values())
            printJob((Job)job);

        System.out.println("**** LicenseGroups ****");
        printEntities(service.getLicenseGroups());

        System.out.println("**** LicenseMessages ****");
        printEntities(service.getLicenseMessages());

        System.out.println("**** LicensePools ****");
        printEntities(service.getLicensePools());

        System.out.println("**** LicenseSlaves ****");
        printEntities(service.getLicenseSlaves());

        System.out.println("**** LicenseStacks ****");
        printEntities(service.getLicenseStacks());

        System.out.println("**** Licenses ****");
        printEntities(service.getLicenses());

        System.out.println("**** Loggers ****");
        printEntities(service.getLoggers());

        System.out.println("**** Info ****");
        printEntity(service.getInfo());

        System.out.println("**** Roles ****");
        printEntities(service.getRoles());

        System.out.println("**** Searches ****");
        printEntities(service.getSearches());

        System.out.println("**** Users ****");
        printEntities(service.getUsers());
    }
}

