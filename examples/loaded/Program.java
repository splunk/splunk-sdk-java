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

import com.splunk.atom.*;
import com.splunk.http.ResponseMessage;

import java.io.IOException;
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

    public Service connect() throws IOException {
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
        for (Entity entity : entities) 
            printEntity(entity);
    }

    public void printJob(Job job) {
        System.out.format("CursorTime = %s\n", job.getCursorTime().toString());
        System.out.format("delegate = %s\n", job.getDelegate());
        System.out.format("DiskUsage = %d\n", job.getDiskUsage());
        System.out.format("DispatchState = %s\n", job.getDispatchState());
        System.out.format("DoneProgress = %f\n", job.getDoneProgress());
        System.out.format("DropCount = %d\n", job.getDropCount());
        System.out.format("EarliestTime = %s\n", job.getEarliestTime().toString());
        System.out.format("EventAvailableCount = %d\n", job.getEventAvailableCount());
        System.out.format("EventCount = %d\n", job.getEventCount());
        System.out.format("EventFieldCount = %d\n", job.getEventFieldCount());
        System.out.format("EventIsStreaming = %b\n", job.getEventIsStreaming());
        System.out.format("EventIsTruncated = %b\n", job.getEventIsTruncated());
        System.out.format("EventSearch = %s\n", job.getEventSearch());
        System.out.format("EventSorting = %s\n", job.getEventSorting());
        System.out.format("Keywords = %s\n", job.getKeywords());
        System.out.format("Label = %s\n", job.getLabel());
        System.out.format("LatestTime = %s\n", job.getLatestTime().toString());
        System.out.format("NumPreviews = %d\n", job.getNumPreviews());
        System.out.format("Priority = %d\n", job.getPriority());
        System.out.format("RemoteSearch = %s\n", job.getRemoteSearch());
        System.out.format("ReportSearch = %s\n", job.getReportSearch());
        System.out.format("ResultCount = %d\n", job.getResultCount());
        System.out.format("ResultIsStreaming = %b\n", job.getResultIsStreaming());
        System.out.format("ResultPreviewCount = %d\n", job.getResultPreviewCount());
        System.out.format("RunDuration = %f\n", job.getRunDuration());
        System.out.format("ScanCount = %d\n", job.getScanCount());
        System.out.format("Search = %s\n", job.getSearch());
        System.out.format("SearchEarliestTime = %s\n", job.getSearchEarliestTime());
        System.out.format("SearchLatestTime = %s\n", job.getSearchLatestTime());
        System.out.format("Sid = %s\n", job.getSid());
        System.out.format("StatusBuckets = %d\n", job.getStatusBuckets());
        System.out.format("Ttl = %d\n", job.getTtl());
        System.out.format("IsDone = %b\n", job.isDone());
        System.out.format("IsFailed = %b\n", job.isFailed());
        System.out.format("IsPaused = %b\n", job.isPaused());
        System.out.format("IsPreviewEnabled = %b\n", job.isPreviewEnabled());
        System.out.format("IsRealTimeSearch = %b\n", job.isRealTimeSearch());
        System.out.format("IsRemoteTimeline = %b\n", job.isRemoteTimeline());
        System.out.format("IsSaved = %b\n", job.isSaved());
        System.out.format("IsSavedSearch = %b\n", job.isSavedSearch());
        System.out.format("IsZombie = %s\n", job.isZombie());
    }

    public void run() throws Exception {
        Service service = connect();

        EntityCollection entities;
        
        entities = service.getApplications();
        printEntities(entities);

        entities = service.getEventTypes();
        printEntities(entities);

        entities = service.getIndexes();
        printEntities(entities);

        entities = service.getJobs();
        for (Entity job : service.getJobs())
            printJob((Job)job);

        entities = service.getLoggers();
        printEntities(entities);

        entities = service.getRoles();
        printEntities(entities);

        entities = service.getSearches();
        printEntities(entities);

        entities = service.getUsers();
        printEntities(entities);
    }
}

