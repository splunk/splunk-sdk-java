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

public class Program {
    public static void main(String[] args) {
        Command command = Command.splunk("info").parse(args);
        Service service = Service.connect(command.opts);

        String sid = service.search("search index=_internal | head 5").getSid();
        Job job = new Job(service, "search/jobs/" + sid);

        while (!job.isDone()) {
            job.refresh();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        // Now job is done
        System.out.println(job.getEventCount());


        // Using service.getJob();

        Job job2 = service.getJob(sid);

        while (!job2.isDone()) {
            job2.refresh();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println(job2.getEventCount());
    }
}