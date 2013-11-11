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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SearchJobTest extends SDKTestCase {
    private static final String QUERY = "search index=_internal | head 10";
    private static final String SUMMARY_FIELD_MAGIC_42 = "<field k='index' c='10' nc='0' dc='1' exact='1'>";
    private static final String SUMMARY_FIELD_MAGIC_43 = "<field k='host' c='10' nc='0' dc='1' exact='1' relevant='0'>";
    private static final String SUMMARY_FIELD_MAGIC_5x = "<field k=\"host\" c=\"10\" nc=\"0\" dc=\"1\" exact=\"1\" relevant=\"0\">";

    private JobCollection jobs;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        jobs = service.getJobs();
    }

    @Test
    public void testRemove() {
        Args namespace = Args.create();
        namespace.put("app", "search");
        namespace.put("owner", "admin");
        assertNull(jobs.remove("doesntexist", namespace));
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
        assertEquals(10, countEvents(service.oneshotSearch(QUERY)));
    }

    @Test
    public void testOneshotWithGarbageFails() {
        try {
            service.oneshotSearch("syntax-error");
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

    // Splunk can include <sg> tags in and similar XML elements in its output to show user interfaces
    // what terms in the results were matched so they can be called out in the user interface. The following
    // six tests check that oneshot, export, and normal searches all, by default, have such highlighting turned
    // off, and it can be turned on for all of them.
    @Test
    public void testOneshotHasNoSgByDefault() throws IOException {
        if (service.versionIsEarlierThan("5.0.0")) {
            System.out.println("WARNING: 'segmentation=none' has no impact on Splunk 4.3.5 (or earlier); skipping test.");
            return;
        }
        InputStream input = service.oneshotSearch("search index=_internal GET | head 3");
        String data = streamToString(input);
        assertFalse(data.contains("<sg"));
    }

    @Test
    public void testOneshotCanEnableSg() throws IOException {
        JobArgs args = new JobArgs();
        args.put("segmentation", "raw");
        InputStream input = service.oneshotSearch("search index=_internal GET | head 3", args);
        String data = streamToString(input);
        assertTrue(data.contains("<sg"));
    }

    @Test
    public void testExportHasNoSgByDefault() throws IOException {
        if (service.versionIsEarlierThan("5.0.0")) {
            System.out.println("WARNING: 'segmentation=none' has no impact on Splunk 4.3.5 (or earlier); skipping test.");
            return;
        }
        InputStream input = service.export("search index=_internal GET | head 3");
        String data = streamToString(input);
        assertFalse(data.contains("<sg"));
    }

    @Test
    public void testExportCanEnableSg() throws IOException {
        JobArgs args = new JobArgs();
        args.put("segmentation", "raw");
        InputStream input = service.export("search index=_internal GET | head 3", args);
        String data = streamToString(input);
        assertTrue(data.contains("<sg"));
    }

    @Test
    public void testJobHasNoSgByDefault() throws IOException {
        if (service.versionIsEarlierThan("5.0.0")) {
            System.out.println("WARNING: 'segmentation=none' has no impact on Splunk 4.3.5 (or earlier); skipping test.");
            return;
        }
        Job job = service.getJobs().create("search index=_internal GET | head 3");
        waitUntilDone(job);
        String data = streamToString(job.getResults());
        assertFalse(data.contains("<sg"));
    }

    @Test
    public void testJobCanEnableSg() throws IOException {
        Job job = service.getJobs().create("search index=_internal GET | head 3");
        waitUntilDone(job);
        Map<String, String> args = new HashMap<String, String>();
        args.put("segmentation", "raw");
        String data = streamToString(job.getResults(args));
        assertTrue(data.contains("<sg"));
    }

    @Test
    public void testExportArgs() throws IOException {
        JobExportArgs args = new JobExportArgs();
        args.setAutoCancel(5);
        args.setAutoFinalizeEventCount(2);
        args.setAutoPause(10);
        args.setEnableLookups(true);
        args.setMaximumTime(3);
        args.setMaximumLines(1);
        args.setOutputMode(JobExportArgs.OutputMode.XML);
        args.setEarliestTime("-10m");
        args.setLatestTime("-5m");
        args.setTruncationMode(JobExportArgs.TruncationMode.TRUNCATE);
        args.setOutputTimeFormat("%s.%Q");
        args.setRequiredFieldList(new String[] { "_raw", "date_hour" });
        args.setSearchMode(JobExportArgs.SearchMode.NORMAL);

        InputStream input = service.export("search index=_internal | head 200", args);
        ResultsReaderXml reader = new ResultsReaderXml(input);

        int count = 0;
        while(true) {
            HashMap<String, String> found = reader.getNextEvent();
            if (found != null) {
                count++;
                // Verify 'args.setMaximumLines(1)' above
                int numLines = found.get("_raw").split("\\n").length;
                // 'max_lines' works for GET search/jobs/{search_id}/events
                // but not GET search/jobs/export, with Splunk.
                // This is either a Splunk server bug or a Doc bug.
                // Refer to
                // http://docs.splunk.com/Documentation/Splunk/5.0.2/RESTAPI/RESTsearch#search.2Fjobs.2F.7Bsearch_id.7D.2Fevents
                if (numLines != 1) {
                    System.out.println("'max_lines' does not work due to a known bug.");
                    numLines = 1;
                }
                assertEquals(1, numLines);
                assertFalse(found.containsKey("date_month"));
            }
            else {
                break;
            }
        }

        assertEquals(200, count);
    }

    @Test
    public void testJobArgs() throws IOException, InterruptedException {
        String name = createTemporaryName();
        JobArgs args = new JobArgs();
        args.setAutoCancel(5);
        args.setAutoFinalizeEventCount(2);
        args.setAutoPause(10);
        args.setEnableLookups(true);
        args.setMaximumTime(3);
        args.setMaximumCount(10);
        args.setStatusBuckets(1);
        args.setEarliestTime("-600m");
        args.setLatestTime("-5m");
        args.setRequiredFieldList(new String[] { "_raw", "date_hour" });
        args.setSearchMode(JobArgs.SearchMode.NORMAL);
        args.setId(name);

        JobCollection jobs = service.getJobs();
        Job job = jobs.create("search index=_internal | head 200", args);

        while(!job.isDone()) {
            Thread.sleep(1000);
        }

        job.refresh();
        assertEquals(job.get("sid"), name);
        assertTrue(job.getEventCount() < 2000);

        testEventArgs(job);
        testResultArgs(job);
        testPreviewArgs(job);

        job.cancel();
    }

    public void testEventArgs(Job job) throws IOException, InterruptedException {
        JobEventsArgs args = new JobEventsArgs();
        args.setCount(2);
        args.setOffset(2);
        args.setMaximumLines(1);
        args.setFieldList(new String[] { "_raw", "date_hour", "_serial" });
        args.setOutputMode(JobEventsArgs.OutputMode.CSV);
        args.setOutputTimeFormat("%s.%Q");
        args.setSegmentation("full");
        args.setTruncationMode(JobEventsArgs.TruncationMode.TRUNCATE);

        InputStream input = job.getEvents(args);
        ResultsReaderCsv reader = new ResultsReaderCsv(input);

        int count = 0;
        while(true) {
            HashMap<String, String> found = reader.getNextEvent();
            if (found != null) {
                assertEquals(found.get("_raw").split("\n").length, 1);
                assertFalse(found.containsKey("date_month"));
                assertEquals(Integer.parseInt(found.get("_serial")), count + 2);
                count++;
            }
            else {
                break;
            }
        }

        assertEquals(2, count);
    }

    public void testResultArgs(Job job) throws IOException, InterruptedException {
        JobResultsArgs args = new JobResultsArgs();
        args.setCount(2);
        args.setOffset(2);
        args.setFieldList(new String[] { "_raw", "date_hour", "_serial" });
        args.setOutputMode(JobResultsArgs.OutputMode.CSV);

        InputStream input = job.getResults(args);
        ResultsReaderCsv reader = new ResultsReaderCsv(input);

        int count = 0;
        while(true) {
            HashMap<String, String> found = reader.getNextEvent();
            if (found != null) {
                assertEquals(found.get("_raw").split("\n").length, 1);
                assertFalse(found.containsKey("date_month"));
                assertEquals(Integer.parseInt(found.get("_serial")), count + 2);
                count++;
            }
            else {
                break;
            }
        }

        assertEquals(2, count);

        JobResultsArgs args2 = new JobResultsArgs();
        args2.setSearch("stats count");
        args2.setOutputMode(JobResultsArgs.OutputMode.JSON);

        InputStream input2 = job.getResults(args2);
        ResultsReaderJson reader2 = new ResultsReaderJson(input2);

        int count2 = 0;
        while(true) {
            HashMap<String, String> found = reader2.getNextEvent();
            if (found != null) {
                assertEquals(found.get("count"), "10");
                count2++;
            }
            else {
                break;
            }
        }

        assertEquals(1, count2);
    }

    public void testPreviewArgs(Job job) throws IOException, InterruptedException {
        JobResultsPreviewArgs args = new JobResultsPreviewArgs();
        args.setCount(2);
        args.setOffset(2);
        args.setFieldList(new String[] { "_raw", "date_hour", "_serial" });
        args.setOutputMode(JobResultsPreviewArgs.OutputMode.CSV);

        InputStream input = job.getResultsPreview(args);
        ResultsReaderCsv reader = new ResultsReaderCsv(input);

        int count = 0;
        while (true) {
            HashMap<String, String> found = reader.getNextEvent();
            if (found != null) {
                assertEquals(found.get("_raw").split("\n").length, 1);
                assertFalse(found.containsKey("date_month"));
                assertEquals(Integer.parseInt(found.get("_serial")), count + 2);
                count++;
            }
            else {
                break;
            }
        }

        assertEquals(2, count);

        JobResultsPreviewArgs args2 = new JobResultsPreviewArgs();
        args2.setSearch("stats count");
        args2.setOutputMode(JobResultsPreviewArgs.OutputMode.JSON);

        InputStream input2 = job.getResultsPreview(args2);
        ResultsReaderJson reader2 = new ResultsReaderJson(input2);

        int count2 = 0;
        while(true) {
            HashMap<String, String> found = reader2.getNextEvent();
            if (found != null) {
                assertEquals(found.get("count"), "10");
                count2++;
            }
            else {
                break;
            }
        }

        assertEquals(1, count2);
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

        while (!job.isReady()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
        }

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

        while (!job.isReady()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
        }

        String sid = job.getSid();

        jobs.refresh();
        assertTrue(jobs.containsKey(sid));

        job.cancel();
        job.cancel(); // Second cancel should be a nop
        jobs.refresh();
        assertFalse(jobs.containsKey(sid));
    }

    @Test
    public void testCursorTime() {
        Job job = jobs.create(QUERY);

        while (!job.isReady()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
        }

        String sid = job.getSid();

        jobs.refresh();
        assertTrue(jobs.containsKey(sid));

        Date date = job.getCursorTime();
        assertNotNull(date);
    }

    @Test
    public void testRemoteTimeline() throws InterruptedException {
        Job job = jobs.create(QUERY);

        String sid = job.getSid();

        jobs.refresh();
        assertTrue(jobs.containsKey(sid));

        while (!job.isReady()) {
            Thread.sleep(50);
        }

        job.isRemoteTimeline();
    }

    @Test
    public void testRemoveFail() throws InterruptedException {
        Job job = jobs.create(QUERY);

        String sid = job.getSid();

        while (!job.isReady()) {
            Thread.sleep(100);
        }

        jobs.refresh();
        assertTrue(jobs.containsKey(sid));

        try {
            job.remove();
            fail("Exception should be thrown on job removal");
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    @Test
    public void testPreview() throws InterruptedException {
        JobArgs args = new JobArgs();
        args.put("field_list", "source,host,sourcetype");
        args.setStatusBuckets(100);

        Job job = jobs.create(QUERY, args);

        while (!job.isReady()) {
            Thread.sleep(100);
        }

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
        if (!response.contains(SUMMARY_FIELD_MAGIC_42) &&
                !response.contains(SUMMARY_FIELD_MAGIC_43) &&
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
    public void testEnablePreview() throws InterruptedException {
        if (!hasTestData()) {
            System.out.println("WARNING: sdk-app-collection not installed in Splunk; skipping test.");
            return;
        }
        installApplicationFromTestData("sleep_command");
        
        String query = "search index=_internal | sleep 10";
        Args args = new Args();
        args.put("earliest_time", "-1m");
        args.put("priority", 5);
        args.put("latest_time", "now");
        final Job job = jobs.create(query, args);

        while (!job.isReady()) {
            Thread.sleep(150);
        }

        // WORKAROUND (SPL-74890): Splunk 6.0.0 has a bug where the isPreviewEnabled field may not show up
        // for some period after the job is ready.
        if (service.versionCompare("6.0.0") == 0) {
            while (job.getString("isPreviewEnabled", null) == null) {
                job.refresh();
                Thread.sleep(100);
            }
        }
        // END WORKAROUND

        job.refresh();
        assertFalse(job.isPreviewEnabled());
        
        job.enablePreview();
        job.update();

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            { tries = 50; }
            @Override
            public boolean predicate() {
                job.refresh();

                if (!job.isPreviewEnabled() && job.isDone()) {
                    fail("Job finished before preview was enabled.");
                }

                return job.isPreviewEnabled();
            }
        });

        waitForSleepingJobToDie(job);
    }

    @Test
    public void testDisablePreview() throws InterruptedException {
        if (!hasTestData()) {
            System.out.println("WARNING: sdk-app-collection not installed in Splunk; skipping test.");
            return;
        }
        installApplicationFromTestData("sleep_command");
        
        String query = "search index=_internal | sleep 10";
        Args args = new Args();
        args.put("earliest_time", "-1m");
        args.put("priority", 5);
        args.put("latest_time", "now");
        args.put("preview", "1");
        final Job job = jobs.create(query, args);

        while (!job.isReady()) {
            Thread.sleep(150);
        }

        // WORKAROUND (SPL-74890): Splunk 6.0.0 has a bug where the isPreviewEnabled field may not show up
        // for some period after the job is ready.
        if (service.versionCompare("6.0.0") == 0) {
            while (job.getString("isPreviewEnabled", null) == null) {
                job.refresh();
                Thread.sleep(100);
            }
        }
        // END WORKAROUND

        assertTrue(job.isPreviewEnabled());

        job.disablePreview();
        job.update();

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            { tries = 50; }
            @Override
            public boolean predicate() {
                job.refresh();

                if (job.isPreviewEnabled() && job.isDone()) {
                    fail("Job finished before preview was enabled.");
                }

                return !job.isPreviewEnabled();
            }
        });
        
        waitForSleepingJobToDie(job);
    }

    void waitForSleepingJobToDie(Job job) {
        final String sid = job.getSid();
        job.cancel();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                jobs.refresh();
                return !jobs.containsKey(sid);
            }
        });
        
        // On Windows, the sleep command in our search
        // will delay the actual end of the job process
        // by up to 50ms. We wait for 500ms to give it a chance
        // to die.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}
    }
    
    @Test
    public void testSetPriority() throws InterruptedException {
        if (!hasTestData()) {
            System.out.println("WARNING: sdk-app-collection not installed in Splunk; skipping test.");
            return;
        }
        installApplicationFromTestData("sleep_command");
        
        // Note that you can only *decrease* the priority of a job unless
        // splunkd is running as root.This is because Splunk jobs
        // are tied up with operating system processes and their priorities.
        String query = "search index=_internal | sleep 10";
        Args args = new Args();
        args.put("earliest_time", "-1m");
        args.put("priority", 5);
        args.put("latest_time", "now");
        final Job job = jobs.create(query);

        while (!job.isReady()) {
            Thread.sleep(100);
        }

        // WORKAROUND (SPL-74890): Splunk 6.0.0 has a bug where the priority field may not show up
        // for some period after the job is ready.
        if (service.versionCompare("6.0.0") == 0) {
            while (job.getString("priority", null) == null) {
                job.refresh();
                Thread.sleep(100);
            }
        }
        // END WORKAROUND


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
    public void testPause() throws InterruptedException {
        if (!hasTestData()) {
            System.out.println("WARNING: sdk-app-collection not installed in Splunk; skipping test.");
            return;
        }
        installApplicationFromTestData("sleep_command");
        
        String query = "search index=_internal | sleep 10";
        final Job job = jobs.create(query);

        while (!job.isReady()) {
            Thread.sleep(100);
        }

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
    public void testUnpause() throws InterruptedException {
        if (!hasTestData()) {
            System.out.println("WARNING: sdk-app-collection not installed in Splunk; skipping test.");
            return;
        }
        installApplicationFromTestData("sleep_command");
        
        String query = "search index=_internal | sleep 10";
        final Job job = jobs.create(query);

        while (!job.isReady()) {
            Thread.sleep(100);
        }

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
    public void testFinalize() throws InterruptedException {
        if (!hasTestData()) {
            System.out.println("WARNING: sdk-app-collection not installed in Splunk; skipping test.");
            return;
        }
        installApplicationFromTestData("sleep_command");
        
        String query = "search index=_internal | sleep 10";
        final Job job = jobs.create(query);

        while (!job.isReady()) {
            Thread.sleep(100);
        }

        assertFalse(job.isFinalized());

        job.finish();

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                job.refresh();
                return job.isFinalized();
            }
        });

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
        } catch (IOException e) {
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
