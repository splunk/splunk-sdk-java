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
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them (see {@link CollectionArgs}).
     */
    JobCollection(Service service, Args args) {
        super(service, "search/jobs", Job.class, args);
    }

    /**
     * Creates a search with a UTF8 pre-encoded search request.
     * <p>
     * <b>Note:</b> You can't create a "oneshot" search using this method. 
     * Instead, use the {@link Service#oneshotSearch} method.
     *
     * @param query The search query.
     * @return The unique search identifier (SID).
     */
    public Job create(String query) {
        return create(query, (Map)null);
    }

    /**
     * Creates a search.
     * <p>
     * <b>Note:</b> You can't create a "oneshot" search using this method. 
     * Instead, use the {@link Service#oneshotSearch} method.
     *
     * @param query The search query.
     * @param args Additional arguments for this job (see {@link JobArgs}).
     * @return The unique search identifier (SID).
     */
    public Job create(String query, Map args) {
        if (args != null && args.containsKey("exec_mode")) {
            if (args.get("exec_mode").equals("oneshot"))
                throw new RuntimeException(oneShotNotAllowed);
        }
        if (args != null && !args.containsKey("segmentation")) {
            // By default, don't include notations in the results to highlight
            // search terms (i.e., <sg> elements in XML output).
            args.put("segmentation", "none");
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
     * Creates a search.
     * <p>
     * <b>Note:</b> You can't create a "oneshot" search using this method. 
     * Instead, use the {@link Service#oneshotSearch} method.
     *
     * @param query The search query.
     * @param args Additional arguments for this job (see {@link JobArgs}).
     * @return The unique search identifier (SID) for the job.
     */
    // NOTE: This overload exists primarily to provide better documentation
    //       for the "args" parameter.
    public Job create(String query, JobArgs args) {
        return this.create(query, (Map<String, Object>) args);
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
