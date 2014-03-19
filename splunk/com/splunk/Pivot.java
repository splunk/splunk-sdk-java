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
import java.util.Map;

public class Pivot {
    private final String openInSearch;
    private final String drilldownSearch;
    private final String pivotSearch;
    private final String tstatsSearch;
    private final Service service;
    private final String search;

    Pivot(Service service, AtomEntry entry) {
        this.service = service;
        this.openInSearch = entry.content.getString("open_in_search");
        this.drilldownSearch = entry.content.getString("drilldown_search");
        this.pivotSearch = entry.content.getString("pivot_search");
        this.search = entry.content.getString("search");
        this.tstatsSearch = entry.content.getString("tstats_search", null);
    }

    public String getPivotQuery() { return this.pivotSearch; }
    public String getAcceleratedQuery() { return this.tstatsSearch; }
    public String getQueryForDrilldown() { return this.drilldownSearch; }
    public String getPrettyQuery() { return this.openInSearch; }
    public String getRawQuery() { return this.search; }

    public Job run() {
        return run(new JobArgs());
    }

    public Job run(JobArgs args) {
        if (this.getAcceleratedQuery() == null) {
            return service.search(this.getPivotQuery(), args);
        } else {
            return service.search(this.getAcceleratedQuery(), args);
        }
    }

    public static Pivot parseStream(Service service, InputStream content) {
        AtomFeed feed = AtomFeed.parseStream(content);
        if (feed.entries.size() != 1) {
            throw new IllegalStateException("Expected one Atom entry; found " + feed.entries.size());
        }
        AtomEntry entry = feed.entries.get(0);
        return new Pivot(service, entry);
    }
}
