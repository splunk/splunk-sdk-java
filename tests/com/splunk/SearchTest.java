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

import java.io.InputStream;
import java.io.IOException;
import org.junit.Test;

public class SearchTest extends SplunkTestCase {
    // Run the given query.
    Job run(Service service, String query) {
        return run(service, query, null);
    }

    // Run the given query with the given query args.
    Job run(Service service, String query, Args args) {
        return service.getJobs().create(query, args);
    }

    // Run the given query and wait for the job to complete.
    Job runWait(Service service, String query) {
        return runWait(service, query, null);
    }

    // Run the given query with the given query args and wait for the job to
    // complete.
    Job runWait(Service service, String query, Args args) {
        Job job = service.getJobs().create(query, args);
        return wait(job);
    }

    // Check retrieval of search job events.
    @Test public void testEvents() throws IOException {
        Service service = connect();

        String query = "search index=sdk-tests * earliest=-1m";

        Job job;
        
        job = runWait(service, query);
        job.getEvents().close();
        job.cancel();

        job = runWait(service, query);
        job.getEvents(new Args("output_mode", "csv")).close();
        job.cancel();

        job = runWait(service, query);
        job.getEvents(new Args("output_mode", "json")).close();
        job.cancel();
    }

    @Test public void testExport() throws IOException {
        Service service = connect();

        String query = "search index=sdk-tests * | head 1";

        InputStream stream;
        
        stream = service.export(query);
        stream.close();

        Args args;

        args = new Args("output_mode", "csv");
        stream = service.export(query, args);
        stream.close();

        args = new Args("output_mode", "json");
        stream = service.export(query, args);
        stream.close();
    }

    @Test public void testParse() {
        Service service = connect();

        String query = "search index=sdk-tests * | head 1";

        // Check simple parse.
        ResponseMessage response = service.parse(query);
        assertEquals(response.getStatus(), 200);

        Args parseArgs;

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

        Service service = connect();

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
        Service service = connect();

        String query = "search index=_internal * earliest=-1m";

        Args args = new Args();
        args.put("field_list", "source,host,sourcetype");
        args.put("status_buckets", 100);

        Job job;
        
        job = run(service, query, args);
        job.getResultsPreview().close();
        job.cancel();

        job = run(service, query, args);
        job.getResultsPreview(new Args("output_mode", "csv")).close();
        job.cancel();

        job = run(service, query, args);
        job.getResultsPreview(new Args("output_mode", "json")).close();
        job.cancel();
    }

    // Check retrieval of search job results.
    @Test public void testResults() throws IOException {
        Service service = connect();

        String query = "search index=_internal * earliest=-1m | stats count";

        Job job;
        
        job = runWait(service, query);
        job.getResults().close();
        job.cancel();

        job = runWait(service, query);
        job.getResults(new Args("output_mode", "csv")).close();
        job.cancel();

        job = runWait(service, query);
        job.getResults(new Args("output_mode", "json")).close();
        job.cancel();
    }

    // Check retrieval of search log.
    @Test public void testSearchLog() throws IOException {
        Service service = connect();

        String query = "search index=sdk-tests * | head 100";

        Job job = runWait(service, query);
        job.getSearchLog().close();
        job.cancel();
    }

    // Check retrieval of search job summary.
    @Test public void testSummary() throws IOException {
        Service service = connect();

        String query = "search index=sdk-tests * earliest=-1m";
        
        Job job = runWait(service, query, new Args("status_buckets", 100));
        job.getSummary().close();
        job.cancel();
    }

    // Check retrieval of search job timeline.
    @Test public void testTimeline() throws IOException {
        Service service = connect();

        String query = "search index=sdk-tests * earliest=-1m";
        
        Job job = runWait(service, query, new Args("status_buckets", 100));
        job.getTimeline().close();
        job.cancel();
    }
}
