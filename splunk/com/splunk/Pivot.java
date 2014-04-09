/*
 * Copyright 2014 Splunk, Inc.
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

import java.io.InputStream;

/**
 * A call of pivot on a PivotSpecification object queries splunkd to get the queries corresponding to that
 * pivot, which will be returned as an instance of the Pivot class. Pivot is a container for the various
 * queries for different purposes returned by the server.
 */
public class Pivot {
    private final String openInSearch;
    private final String drilldownSearch;
    private final String pivotSearch;
    private final String tstatsSearch;
    private final Service service;
    private final String search;

    private Pivot(Service service, AtomEntry entry) {
        this.service = service;
        this.openInSearch = entry.content.getString("open_in_search");
        this.drilldownSearch = entry.content.getString("drilldown_search");
        this.pivotSearch = entry.content.getString("pivot_search");
        this.search = entry.content.getString("search");
        this.tstatsSearch = entry.content.getString("tstats_search", null);
    }

    /**
     * @return a SPL query using the pivot search command to implement this pivot.
     */
    public String getPivotQuery() { return this.pivotSearch; }

    /**
     * @return an SPL query that uses tstats and a tsidx namespace to implement an accelerated
     * version of this pivot. If there is no acceleration available, it returns null.
     */
    public String getAcceleratedQuery() { return this.tstatsSearch; }

    /**
     * @return an SPL query implementing this pivot that is appropriate for use when implementing
     * drilldown interfaces.
     */
    public String getQueryForDrilldown() { return this.drilldownSearch; }

    /**
     * @return a readable version of the SPL query implementing this pivot.
     */
    public String getPrettyQuery() { return this.openInSearch; }

    /**
     * @return an SPL query implementing this pivot with no reference to the pivot command.
     */
    public String getRawQuery() { return this.search; }

    /**
     * @return a Job object running this pivot, accelerated if possible.
     */
    public Job run() {
        return run(new JobArgs());
    }

    /**
     * @param args options for creating a Job
     * @return a Job object running this pivot, accelerated if possible.
     */
    public Job run(JobArgs args) {
        if (this.getAcceleratedQuery() == null) {
            return service.search(this.getPivotQuery(), args);
        } else {
            return service.search(this.getAcceleratedQuery(), args);
        }
    }

    /**
     * Parse a pivot from a response from splunkd.
     *
     * @param service The HTTP service this pivot operates on.
     * @param content an InputStream returned by an HTTP response.
     * @return a Pivot object.
     */
    static Pivot parseStream(Service service, InputStream content) {
        AtomFeed feed = AtomFeed.parseStream(content);
        if (feed.entries.size() != 1) {
            throw new IllegalStateException("Expected one Atom entry; found " + feed.entries.size());
        }
        AtomEntry entry = feed.entries.get(0);
        return new Pivot(service, entry);
    }
}
