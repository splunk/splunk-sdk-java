/*
 * Copyright 2012 Splunk, Inc.
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
 * The {@code JobCollection} class represents a collection of jobs. A job
 * is an individual instance of a running or completed search or report, along 
 * with its related output.
 */
public class JobCollection extends EntityCollection<Job> {
    static String oneShotNotAllowed = String.format(
         "Oneshot not allowed, use service oneshot search method");

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     */
    JobCollection(Service service) {
        super(service, "search/jobs", Job.class);
    }

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param args Arguments to use when you instantiate the entity, such as 
     * "count" and "offset".
     */
    JobCollection(Service service, Args args) {
        super(service, "search/jobs", Job.class, args);
    }

    /**
     * Creates a search with a UTF8 pre-encoded search request.
     * <p>
     * <b>Note:</b> A 'oneshot' request is invalid. To create a oneshot search,
     * use the {@code Service.oneshot} method instead.
     * @see Service#oneshot
     *
     * @param query The search query string.
     * @return The unique search identifier (SID).
     */
    public Job create(String query) {
        return create(query, (Map)null);
    }

    /**
     * Creates a search.
     * <p>
     * <b>Note:</b> A 'oneshot' request is invalid. To create a oneshot search,
     * use the {@code Service.oneshot} method instead.
     * @see Service#oneshot
     *
     * @param query The search query string.
     * @param args Additional arguments for this job.
     * @return The unique search identifier (SID).
     */
    public Job create(String query, Map args) {
        if (args != null && args.containsKey("exec_mode")) {
            if (args.get("exec_mode").equals("oneshot"))
                throw new RuntimeException(oneShotNotAllowed);
        }
        args = Args.create(args).add("search", query);
        ResponseMessage response = service.post(path, args);
        assert(response.getStatus() == 201);

        String sid = Xml.parse(response.getContent())
            .getElementsByTagName("sid")
            .item(0)
            .getTextContent();

        invalidate();
        Job job = get(sid);

        // if job not yet scheduled, create an empty job object
        if (job == null) {
            job = new Job(service, "search/jobs/" + sid);
        }

        return job;
    }

    /**
     * Returns the job's response.
     *
     * @return The job's response.
     */
    @Override public ResponseMessage list() {
        return service.get(path + "?count=0");
    }

    /**
     * Returns the job's unique search identifier (SID), which is used as this
     * item's key.
     *
     * @param entry The {@code AtomEntry} response.
     * @return This job's SID.
     */
    @Override protected String itemKey(AtomEntry entry) {
        return (String)entry.content.get("sid");
    }
}
