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

package com.splunk;

import java.util.Map;

/**
 * Representation of a collection of jobs.
 */
public class JobCollection extends EntityCollection<Job> {
    static String oneShotNotAllowed = String.format(
         "Oneshot not allowed, use service oneShotSearch method");

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     */
    JobCollection(Service service) {
        super(service, "search/jobs", Job.class);
    }

    /**
     * Creates a search job with a UTF8 pre-encoded search request. Note that
     * a 'oneshot' request is invalid here. Please use the createOneShot method
     * instead.
     *
     * @param query The search query string.
     * @return The search job SID.
     */
    public Job create(String query) {
        return create(query, null);
    }

    /**
     * Creates a search job. Note that a 'oneshot' request is invalid here.
     * Please use the createOneShot method instead.
     *
     * @param query The search query.
     * @param args The arguments supplied to this job's creation.
     * @return The search job SID.
     */
    public Job create(String query, Map args) {
        if (args != null && args.containsKey("exec_mode")) {
            if (args.get("exec_mode").equals("oneshot"))
                throw new RuntimeException(oneShotNotAllowed);
        }
        args = Args.create(args).add("search", query);
        ResponseMessage response = service.post(path, args);
        assert(response.getStatus() == 201);
        invalidate();
        String sid = Job.getSid(response);
        Job job = get(sid);
        while (job == null) {
            try { Thread.sleep(500); }
            catch (InterruptedException e) {}
            invalidate();
            job = get(sid);
        }
        return job;
    }

    /**
     * Returns this job's response.
     *
     * @return This job's response.
     */
    @Override public ResponseMessage list() {
        return service.get(path + "?count=0");
    }

    /**
     * Returns this job's SID. The SID is used as this item's key.
     *
     * @param entry The Atom response.
     * @return This job's SID.
     */
    @Override protected String itemKey(AtomEntry entry) {
        return (String)entry.content.get("sid");
    }
}
