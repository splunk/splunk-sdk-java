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

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SearchJobTest extends SDKTestCase {
    private static final String QUERY = "search index=_internal | head 10";
    private static final String SUMMARY_FIELD_MAGIC_4x = "<field k='host' c='10' nc='0' dc='1' exact='1' relevant='0'>";
    private static final String SUMMARY_FIELD_MAGIC_5x = "<field k=\"host\" c=\"10\" nc=\"0\" dc=\"1\" exact=\"1\" relevant=\"0\">";
    
    private JobCollection jobs;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        jobs = service.getJobs();
    }

    @Test
    public void testEventsFromJob() {
        Job job = jobs.create(QUERY);
        waitUntilDone(job);

        assertEquals(10, countEvents(job.getEvents()));

        job.cancel();
    }

    @Test
    public void testResultsFromJob() {
        Job job = jobs.create(QUERY);
        waitUntilDone(job);

        assertEquals(10, countEvents(job.getResults()));

        job.cancel();
    }

    @Test
    public void testBlockingSearch() {
        assertEquals(10, countEvents(service.oneshot(QUERY)));
    }

    @Test
    public void testOneshotWithGarbageFails() {
        try {
            service.oneshot("syntax-error");
            fail("Expected an exception from oneshot with garbage.");
        } catch (HttpException e) {
            assertEquals(400, e.getStatus());
        }
    }

    @Test
    public void testBlockingExport() {
        assertEquals(10, countEvents(service.export(QUERY)));
    }

    @Test
    public void testExportWithGarbageFails() {
        try {
            service.export("syntax-error");
            fail("Expected an exception from export with garbage.");
        } catch (HttpException e) {
            assertEquals(400, e.getStatus());
        }
    }

    @Test
    public void testAsyncSearchWithGarbageFails() {
        Job job = null;
        try {
            job = jobs.create("syntax-error");
            fail("Expected an exception from creating a job with garbage.");
        } catch (HttpException e) {
            assertEquals(400, e.getStatus());
            if (job != null) {
                assertTrue(job.isFailed());
            }
        }
    }

    @Test
    public void testSimpleParse() {
        String response = inputStreamToString(
                service.parse(QUERY).getContent()
        );

        assertTrue(response.contains("<key name=\"command\">search</key>"));
        assertTrue(response.contains("<key name=\"command\">head</key>"));
    }

    @Test
    public void testParseWithParseOnly() {
        Args args = new Args("output_mode", "json");
        String response = inputStreamToString(
                service.parse(QUERY, args).getContent()
        );

        assertTrue(response.startsWith("{"));
    }

    @Test
    public void testParseError() {
        String badQuery = "syntax-error";

        try {
            service.parse(badQuery);
            fail("Expected a parse error.");
        } catch (HttpException e) {
            assertEquals(400, e.getStatus());
        }
    }

    @Test
    public void testParseErrorWithArgs() {
        String badQuery = "syntax-error";

        try {
            Args args = new Args("output_mode", "json");
            service.parse(badQuery, args);
            fail("Expected a parse error.");
        } catch (HttpException e) {
            assertEquals(400, e.getStatus());
        }
    }

    @Test
    public void testCancel() {
        Job job = jobs.create(QUERY);

        String sid = job.getSid();

        jobs.refresh();
        assertTrue(jobs.containsKey(sid));

        job.cancel();
        jobs.refresh();
        assertFalse(jobs.containsKey(sid));
    }

    @Test
    public void testCancelIsIdempotent() {
        Job job = jobs.create(QUERY);

        String sid = job.getSid();

        jobs.refresh();
        assertTrue(jobs.containsKey(sid));

        job.cancel();
        job.cancel(); // Second cancel should be a nop
        jobs.refresh();
        assertFalse(jobs.containsKey(sid));
    }

    @Test
    public void testPreview() {
        JobArgs args = new JobArgs();
        args.put("field_list", "source,host,sourcetype");
        args.setStatusBuckets(100);

        Job job = jobs.create(QUERY, args);
        assertTrue(10 >= countEvents(job.getResultsPreview()));

        job.cancel();
    }

    @Test
    public void testSearchLog() {
        Job job = jobs.create(QUERY);
        waitUntilDone(job);
        String response = inputStreamToString(job.getSearchLog());

        assertTrue(response.contains("dispatchRunner"));

        job.cancel();
    }
    
    @Test
    public void testSummary() {
        // status_buckets > 0 and arguments to
        // required_fields_list guarantees content
        // in the summary.
        JobArgs args = new JobArgs();
        args.setStatusBuckets(100);
        args.put("required_fields_list", "source,host");
        Job job = jobs.create(QUERY, args);
        waitUntilDone(job);

        // Ensure at least one field comes back
        String response = inputStreamToString(job.getSummary());
        if (!response.contains(SUMMARY_FIELD_MAGIC_4x) &&
                !response.contains(SUMMARY_FIELD_MAGIC_5x)) {
            fail("Couldn't find <field> in response: " + response);
        }

        job.cancel();
    }

    @Test
    public void testTimeline() {
        Args args = new Args();
        args.put("status_buckets", 100);
        Job job = jobs.create(QUERY, args);
        waitUntilDone(job);

        String response = inputStreamToString(job.getTimeline());
        assertTrue(response.contains("<bucket"));

        job.cancel();
    }

    @Test
    public void testTimelineWithJobArgs() {
        JobArgs args = new JobArgs();
        args.setStatusBuckets(100);
        Job job = jobs.create(QUERY, args);
        waitUntilDone(job);

        String response = inputStreamToString(job.getTimeline());
        assertTrue(response.contains("<bucket"));

        job.cancel();
    }

    @Test
    public void testAttributes() {
        Args args = new Args();
        args.put("status_buckets", 100);
        Job job = jobs.create(QUERY, args);
        waitUntilDone(job);

        String response = Util.join(",", job.getSearchProviders());
        assertTrue(response.contains(service.getSettings().getServerName()));

        job.cancel();
    }

    @Test
    public void testEnablePreview() {
        installApplicationFromTestData("sleep_command");
        String query = "search index=_internal | sleep done=100";
        Args args = new Args();
        args.put("earliest_time", "-1m");
        args.put("priority", 5);
        args.put("latest_time", "now");
        final Job job = jobs.create(query, args);
        assertFalse(job.isPreviewEnabled());

        job.enablePreview();
        job.update();

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                job.refresh();

                if (job.isDone()) {
                    fail("Job finished before preview was enabled.");
                }

                return job.isPreviewEnabled();
            }
        });

        job.cancel();
    }

    @Test
    public void testSetPriority() {
        // Note that you can only *decrease* the priority of a job unless
        // splunkd is running as root.This is because Splunk jobs
        // are tied up with operating system processes and their priorities.
        installApplicationFromTestData("sleep_command");
        String query = "search index=_internal | sleep done=100";
        Args args = new Args();
        args.put("earliest_time", "-1m");
        args.put("priority", 5);
        args.put("latest_time", "now");
        final Job job = jobs.create(query);

        assertEquals(5, job.getPriority()); // The default priority is 5

        final int newPriority = 3;
        job.setPriority(newPriority);
        job.update();

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                job.refresh();
                return job.getPriority() == newPriority;
            }
        });

        job.cancel();
    }

    @Test
    public void testPause() {
        installApplicationFromTestData("sleep_command");
        String query = "search index=_internal | sleep done=100";
        final Job job = jobs.create(query);

        if (job.isPaused()) {
            job.control("unpause");
            job.refresh();
            assertFalse(job.isPaused());
        }

        job.pause();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                job.refresh();
                return job.isPaused();
            }
        });

        job.cancel();
    }

    @Test
    public void testUnpause() {
        installApplicationFromTestData("sleep_command");
        String query = "search index=_internal | sleep done=100";
        final Job job = jobs.create(query);

        if (!job.isPaused()) {
            job.control("pause");
            job.refresh();
            assertTrue(job.isPaused());
        }

        job.control("unpause");
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                job.refresh();
                return !job.isPaused();
            }
        });

        job.cancel();
    }

    @Test
    public void testFinalize() {
        installApplicationFromTestData("sleep_command");
        String query = "search index=_internal | sleep done=100";
        final Job job = jobs.create(query);

        assertFalse(job.isFinalized());

        job.finish();

        job.refresh();
        assertTrue(job.isFinalized());

        job.cancel();
    }
    
    // === Utility ===
    
    private int countEvents(InputStream stream) {
        ResultsReaderXml results = null;
        try {
            results = new ResultsReaderXml(stream);

            int count = 0;
            while (results.getNextEvent() != null) {
                count += 1;
            }

            return count;
        } catch (Exception e) {
            fail(e.toString());
            return -1;
        }
    }
    
    private void waitUntilDone(final Job job) {
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return job.isDone();
            }
        });
    }

    private String inputStreamToString(InputStream stream) {
        try {
            StringBuilder b = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stream, "UTF8")
            );
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                b.append(tmp + "\n");
            }

            return b.toString();
        } catch (IOException e) {
            fail(e.toString());
            return null;
        }
    }
}
