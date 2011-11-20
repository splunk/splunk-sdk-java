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

// parse
// typeahead
// timeparser
// jobs.create
//  - result kinds
// 

package com.splunk;

import com.splunk.sdk.Command;

import java.io.IOException;
import junit.framework.TestCase;
import org.junit.*;

public class SearchTest extends TestCase {
    Command command;
    Service service;

    public SearchTest() {}

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
        service = Service.connect(command.opts);
    }

    // Run the given query.
    Job run(String query) {
        return run(query, null);
    }

    // Run the given query with the given query args.
    Job run(String query, Args args) {
        return service.getJobs().create(query, args);
    }

    // Run the given query and wait for the job to complete.
    Job runWait(String query) {
        return runWait(query, null);
    }

    // Run the given query with the given query args and wait for the job to
    // complete.
    Job runWait(String query, Args args) {
        Job job = service.getJobs().create(query, args);
        while (!job.isDone()) {
            try { Thread.sleep(2000); }
            catch (InterruptedException e) {}
            job.refresh();
        }
        return job;
    }

    // Check retrieval of search job events.
    @Test public void testEvents() throws IOException {
        Job job;

        String query = "search index=sdk-tests * earliest=-1m";
        
        job = runWait(query);
        job.getEvents().close();
        job.cancel();

        job = runWait(query);
        job.getEvents(new Args("output_mode", "csv")).close();
        job.cancel();

        job = runWait(query);
        job.getEvents(new Args("output_mode", "json")).close();
        job.cancel();
    }

    @Test public void testParse() {
        Args parseArgs;
        ResponseMessage response;

        String query = "search index=sdk-tests * | head 1";

        // Check simple parse.
        response = service.parse(query);
        assertEquals(response.getStatus(), 200);

        // Check parse with parse_only argument.
        parseArgs = new Args("parse_only", true);
        service.parse(query, parseArgs); 
        assertEquals(response.getStatus(), 200);

        // Check parse with multiple arguments.
        parseArgs = new Args();
        parseArgs.put("parse_only", false);
        parseArgs.put("output_mode", "json");
        parseArgs.put("enable_lookups", true);
        parseArgs.put("reload_macros", true);
        service.parse(query, parseArgs); 
        assertEquals(response.getStatus(), 200);
    }

    @Test public void testParseFail() {
        String query = "syntax-error";

        // Check for parse error.
        try {
            service.parse(query);
            fail("Expected a parse error");
        }
        catch (HttpException e) { 
            assertEquals(e.getStatus(), 400);
        }

        // Check for parse error with args.
        try {
            Args parseArgs = new Args();
            parseArgs.put("parse_only", false);
            parseArgs.put("output_mode", "json");
            parseArgs.put("enable_lookups", true);
            parseArgs.put("reload_macros", true);
            service.parse(query, parseArgs); 
            fail("Expected a parse error");
        }
        catch (HttpException e) {
            assertEquals(e.getStatus(), 400);
        }

        // Check for argument error.
        try {
            Args parseArgs = new Args("babble", 42);
            service.parse(query, parseArgs);
        }
        catch (HttpException e) {
            assertEquals(e.getStatus(), 400);
        }
    }

    // Check retrieval of search job results.
    @Test public void testPreview() throws IOException {
        Job job;

        String query = "search index=_internal * earliest=-1m";

        Args args = new Args();
        args.put("field_list", "source,host,sourcetype");
        args.put("status_buckets", 100);
        
        job = run(query, args);
        job.getResultsPreview().close();
        job.cancel();

        job = run(query, args);
        job.getResultsPreview(new Args("output_mode", "csv")).close();
        job.cancel();

        job = run(query, args);
        job.getResultsPreview(new Args("output_mode", "json")).close();
        job.cancel();
    }

    // Check retrieval of search job results.
    @Test public void testResults() throws IOException {
        Job job;

        String query = "search index=_internal * earliest=-1m | stats count";
        
        job = runWait(query);
        job.getResults().close();
        job.cancel();

        job = runWait(query);
        job.getResults(new Args("output_mode", "csv")).close();
        job.cancel();

        job = runWait(query);
        job.getResults(new Args("output_mode", "json")).close();
        job.cancel();
    }

    // Check retrieval of search log.
    @Test public void testSearchLog() throws IOException {
        Job job;

        String query = "search index=sdk-tests * | head 100";
        
        job = runWait(query);
        job.getSearchLog().close();
        job.cancel();
    }

    // Check retrieval of search job summary.
    @Test public void testSummary() throws IOException {
        Job job;

        String query = "search index=sdk-tests * earliest=-1m";
        
        job = runWait(query, new Args("status_buckets", 100));
        job.getSummary().close();
        job.cancel();
    }

    // Check retrieval of search job timeline.
    @Test public void testTimeline() throws IOException {
        Job job;

        String query = "search index=sdk-tests * earliest=-1m";
        
        job = runWait(query, new Args("status_buckets", 100));
        job.getTimeline().close();
        job.cancel();
    }
}
