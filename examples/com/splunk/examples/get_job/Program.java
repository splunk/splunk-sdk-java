/*
 * Copyright 2015 Splunk, Inc.
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

package com.splunk.examples.get_job;

import com.splunk.*;

/**
 * This example shows a better way to retrieve a Job by it's sid
 * using the new getJob() method.
 * 
 * Previously, the only way to do this would be the following:
 *
 *     Job job = service.getJobs().get(sid);
 *
 * The above has a significant overhead of getting all search jobs from
 * the Splunk REST API in order to get a single Job.
 *
 */

public class Program {
    public static void main(String[] args) {
        Command command = Command.splunk("info").parse(args);
        Service service = Service.connect(command.opts);

        String sid = service.search("search index=_internal | head 5").getSid();
        Job job = service.getJob(sid);

        while (!job.isDone()) {
            job.refresh();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("Number of events found: " + job.getEventCount());
    }
}