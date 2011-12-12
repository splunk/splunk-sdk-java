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
 * Representation of a collection of jobs
 */
public class JobCollection extends EntityCollection<Job> {

    final String oneShotNotAllowed = String.format(
                 "Oneshot not allowed, use createOneShot method");
    final String onlyOneShotAllowed = String.format(
                "When using oneshot, no other exec_mode is allowed");
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
     * a 'oneshot'request is invalid here. Please use the createOneShot method
     * instead.
     *
     * @param query The search query string.
     * @return The search job SID.
     */
    public Job create(String query) {
        if (query.contains("exec_mode=oneshot")) {
            throw new RuntimeException(oneShotNotAllowed);
        }
        return create(query, null);
    }

    /**
     * Creates a search job. Note that a 'oneshot' request is invalid here.
     * Please use the craeteOneShot method instead.
     *
     * @param query The search query.
     * @param args The arguments supplied to this job's creation.
     * @return The search job SID.
     */
    public Job create(String query, Map args) {
        if (args.containsKey("exec_mode") &&
            args.get("exec_mode").equals("oneshot")) {
            throw new RuntimeException(oneShotNotAllowed);
        }
        args = Args.create(args).add("search", query);
        ResponseMessage response = service.post(path, args);
        assert(response.getStatus() == 201);
        invalidate();
        String sid = Job.getSid(response);
        return get(sid);
    }

    /**
     * Creates a 'oneshot' synchronous search with a UTF8 pre-encoded search
     * request.
     *
     * @param query The search query string.
     * @return The response message of the search.
     */
    public ResponseMessage createOneShot(String query) {
       return createOneShot(query, new Args());
    }

    /**
     * Creates a 'oneshot' synchronous search.
     *
     * @param query The search query.
     * @param args The arguments to the search.
     * @return The results of the search.
     */
    public ResponseMessage createOneShot(String query, Map args) {
        if (args.containsKey("exec_mode") &&
            !args.get("exec_mode").equals("oneshot")) {
            throw new RuntimeException(onlyOneShotAllowed);
        }
        args.put("search", query);
        if (!args.containsKey("exec_mode")) {
            args.put("exec_mode", "oneshot");
        }

        return service.post("search/jobs/", args);
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
