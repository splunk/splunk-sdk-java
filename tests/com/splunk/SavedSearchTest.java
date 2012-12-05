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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SavedSearchTest extends SDKTestCase {
    SavedSearchCollection savedSearches;
    String savedSearchName;
    SavedSearch savedSearch;
    String query = "search index=_internal * earliest=-1m | head 3";

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        savedSearchName = createTemporaryName();
        savedSearches = service.getSavedSearches();
        savedSearch = savedSearches.create(savedSearchName, query);

        savedSearches.refresh();
        assertTrue(savedSearches.containsKey(savedSearchName));
    }

    @After
    @Override
    public void tearDown() throws Exception {
        // Remove this run's saved search.
        for (Job j : savedSearch.history()) {
            j.cancel();
        }
        savedSearch.remove();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                savedSearches.refresh();
                return !savedSearches.containsKey(savedSearchName);
            }
        });

        // Remove any previously created saved searches that
        // somehow escaped.
        for (SavedSearch s : savedSearches.values()) {
            if (s.getName().startsWith("delete-me")) {
                for (Job j : s.history()) {
                    j.cancel();
                }
                s.remove();
            }
        }

        super.tearDown();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testUpdate() {
        boolean isVisible = savedSearch.isVisible();
        savedSearch.setIsVisible(!isVisible);

        savedSearch.setActionEmailAuthPassword("sdk-password");
        savedSearch.setActionEmailAuthUsername("sdk-username");
        savedSearch.setActionEmailBcc("sdk-bcc@splunk.com");
        savedSearch.setActionEmailCc("sdk-cc@splunk.com");
        savedSearch.setActionEmailCommand("$name1$");
        savedSearch.setActionEmailFormat("text");
        savedSearch.setActionEmailFrom("sdk@splunk.com");
        savedSearch.setActionEmailHostname("dummy1.host.com");
        savedSearch.setActionEmailInline(true);
        savedSearch.setActionEmailMailServer("splunk.com");
        savedSearch.setActionEmailMaxResults(101);
        savedSearch.setActionEmailMaxTime("10s");
        savedSearch.setActionEmailPdfView("dummy");
        savedSearch.setActionEmailPreProcessResults("*");
        savedSearch.setActionEmailReportPaperOrientation("landscape");
        savedSearch.setActionEmailReportPaperSize("letter");
        savedSearch.setActionEmailReportServerEnabled(false);
        savedSearch.setActionEmailReportServerUrl("splunk.com");
        savedSearch.setActionEmailSendPdf(false);
        savedSearch.setActionEmailSendResults(false);
        savedSearch.setActionEmailSubject("sdk-subject");
        savedSearch.setActionEmailTo("sdk-to@splunk.com");
        savedSearch.setActionEmailTrackAlert(false);
        savedSearch.setActionEmailTtl("61");
        savedSearch.setActionEmailUseSsl(false);
        savedSearch.setActionEmailUseTls(false);
        savedSearch.setActionEmailWidthSortColumns(false);
        savedSearch.setActionPopulateLookupCommand("$name2$");
        savedSearch.setActionPopulateLookupDest("dummypath");
        savedSearch.setActionPopulateLookupHostname("dummy2.host.com");
        savedSearch.setActionPopulateLookupMaxResults(102);
        savedSearch.setActionPopulateLookupMaxTime("20s");
        savedSearch.setActionPopulateLookupTrackAlert(false);
        savedSearch.setActionPopulateLookupTtl("62");
        savedSearch.setActionRssCommand("$name3$");
        savedSearch.setActionRssHostname("dummy3.host.com");
        savedSearch.setActionRssMaxResults(103);
        savedSearch.setActionRssMaxTime("30s");
        savedSearch.setActionRssTrackAlert(false);
        savedSearch.setActionRssTtl("63");
        savedSearch.setActionScriptFilename("foo.sh");
        savedSearch.setActionScriptCommand("$name4$");
        savedSearch.setActionScriptHostname("dummy4.host.com");
        savedSearch.setActionScriptMaxResults(104);
        savedSearch.setActionScriptMaxTime("40s");
        savedSearch.setActionScriptTrackAlert(false);
        savedSearch.setActionScriptTtl("64");
        savedSearch.setActionSummaryIndexName("default");
        savedSearch.setActionSummaryIndexCommand("$name5$");
        savedSearch.setActionSummaryIndexHostname("dummy5.host.com");
        savedSearch.setActionSummaryIndexInline(false);
        savedSearch.setActionSummaryIndexMaxResults(105);
        savedSearch.setActionSummaryIndexMaxTime("50s");
        savedSearch.setActionSummaryIndexTrackAlert(false);
        savedSearch.setActionSummaryIndexTtl("65");
        savedSearch.setActions(
                "rss,email,populate_lookup,script,summary_index");
        savedSearch.setSearch("search index=boris abcd");
        
        savedSearch.setAlertComparator("greater than");
        savedSearch.setAlertCondition("*");
        savedSearch.setAlertDigestMode(true);   // false causes side effects
        savedSearch.setAlertExpires("23h");
        savedSearch.setAlertSeverity(6);
        savedSearch.setAlertSuppress(true);
        if (service.versionIsAtLeast("4.3")) {
            savedSearch.setAlertSuppressFields("host");
        }
        savedSearch.setAlertSuppressPeriod("1m");
        savedSearch.setAlertThreshold("50%");
        savedSearch.setAlertTrack("0");
        savedSearch.setAlertType("number of events");
        savedSearch.setCronSchedule("*/5 * * * *");
        savedSearch.setDescription("Cake!");
        savedSearch.setDispatchBuckets("100");
        savedSearch.setDispatchEarliestTime("-100s@s");
        savedSearch.setDispatchLatestTime("-1s@s");
        savedSearch.setDispatchLookups(false);
        savedSearch.setDispatchMaxCount(100000);
        savedSearch.setDispatchMaxTime(120);
        savedSearch.setDispatchSpawnProcess(true);
        savedSearch.setDispatchTimeFormat("%FT%T.%Q");
        savedSearch.setDispatchTtl("3p");
        savedSearch.setDisplayView("flash_timeline");
        savedSearch.setMaxConcurrent(2);
        savedSearch.setRealtimeSchedule(false);
        savedSearch.setRequestUiDispatchApp("foo");
        savedSearch.setRequestUiDispatchView("bar");
        savedSearch.setRunOnStartup(true);
        // TODO: Create a vsid to test this properly
        savedSearch.setVsid("");
        
        savedSearch.setDispatchReduceFrequency(11);
        savedSearch.setDispatchRealTimeBackfill(true);
        savedSearch.setRestartOnSearchpeerAdd(false);
        savedSearch.setDisabled(true);

        savedSearch.update();
        savedSearch.refresh();

        assertTrue(savedSearch.isActionEmail());
        assertTrue(savedSearch.isActionPopulateLookup());
        assertTrue(savedSearch.isActionRss());
        assertTrue(savedSearch.isActionScript());
        assertTrue(savedSearch.isActionSummaryIndex());
        assertTrue(savedSearch.isDigestMode());

        assertEquals("sdk-password", savedSearch.getActionEmailAuthPassword());
        assertEquals("sdk-username", savedSearch.getActionEmailAuthUsername());
        assertEquals("sdk-bcc@splunk.com", savedSearch.getActionEmailBcc());
        assertEquals("sdk-cc@splunk.com", savedSearch.getActionEmailCc());
        assertEquals("$name1$", savedSearch.getActionEmailCommand());
        assertEquals("text", savedSearch.getActionEmailFormat());
        assertEquals("sdk@splunk.com", savedSearch.getActionEmailFrom());
        assertEquals("dummy1.host.com", savedSearch.getActionEmailHostname());
        assertTrue(savedSearch.getActionEmailInline());
        assertEquals("splunk.com", savedSearch.getActionEmailMailServer());
        assertEquals(101, savedSearch.getActionEmailMaxResults());
        assertEquals("10s", savedSearch.getActionEmailMaxTime());
        assertEquals("dummy", savedSearch.getActionEmailPdfView());
        assertEquals("*", savedSearch.getActionEmailPreProcessResults());
        assertEquals("landscape", savedSearch.getActionEmailReportPaperOrientation());
        assertEquals("letter", savedSearch.getActionEmailReportPaperSize());
        assertFalse(savedSearch.getActionEmailReportServerEnabled());
        assertEquals("splunk.com", savedSearch.getActionEmailReportServerUrl());
        assertFalse(savedSearch.getActionEmailSendPdf());
        assertFalse(savedSearch.getActionEmailSendResults());
        assertEquals("sdk-subject", savedSearch.getActionEmailSubject());
        assertEquals("sdk-to@splunk.com", savedSearch.getActionEmailTo());
        assertFalse(savedSearch.getActionEmailTrackAlert());
        assertEquals("61", savedSearch.getActionEmailTtl());
        assertFalse(savedSearch.getActionEmailUseSsl());
        assertFalse(savedSearch.getActionEmailUseTls());
        assertFalse(savedSearch.getActionEmailWidthSortColumns());
        assertEquals("$name2$", savedSearch.getActionPopulateLookupCommand());
        assertEquals("dummypath", savedSearch.getActionPopulateLookupDest());
        assertEquals("dummy2.host.com", savedSearch.getActionPopulateLookupHostname());
        assertEquals(102, savedSearch.getActionPopulateLookupMaxResults());
        assertEquals("20s", savedSearch.getActionPopulateLookupMaxTime());
        assertFalse(savedSearch.getActionPopulateLookupTrackAlert());
        assertEquals("62", savedSearch.getActionPopulateLookupTtl());
        assertEquals("$name3$", savedSearch.getActionRssCommand());
        assertEquals("dummy3.host.com", savedSearch.getActionRssHostname());
        assertEquals(103, savedSearch.getActionRssMaxResults());
        assertEquals("30s", savedSearch.getActionRssMaxTime());
        assertFalse(savedSearch.getActionRssTrackAlert());
        assertEquals("63", savedSearch.getActionRssTtl());
        assertEquals("foo.sh", savedSearch.getActionScriptFilename());
        assertEquals("$name4$", savedSearch.getActionScriptCommand());
        assertEquals("dummy4.host.com", savedSearch.getActionScriptHostname());
        assertEquals(104, savedSearch.getActionScriptMaxResults());
        assertEquals("40s", savedSearch.getActionScriptMaxTime());
        assertFalse(savedSearch.getActionScriptTrackAlert());
        assertEquals("64", savedSearch.getActionScriptTtl());
        assertEquals("default", savedSearch.getActionSummaryIndexName());
        assertEquals("$name5$", savedSearch.getActionSummaryIndexCommand());
        assertEquals("dummy5.host.com", savedSearch.getActionSummaryIndexHostname());
        assertFalse(savedSearch.getActionSummaryIndexInline());
        assertEquals(105, savedSearch.getActionSummaryIndexMaxResults());
        assertEquals("50s", savedSearch.getActionSummaryIndexMaxTime());
        assertFalse(savedSearch.getActionSummaryIndexTrackAlert());
        assertEquals("65", savedSearch.getActionSummaryIndexTtl());
        assertEquals(savedSearch.isVisible(), !isVisible);
        assertNull(savedSearch.getNextScheduledTime());
        if (service.versionIsEarlierThan("4.3")) {
            assertEquals("search  search index=boris abcd", savedSearch.getQualifiedSearch());
        } else {
            assertEquals("search search index=boris abcd", savedSearch.getQualifiedSearch());
        }
        
        assertEquals("greater than", savedSearch.getAlertComparator());
        assertEquals("*", savedSearch.getAlertCondition());
        assertEquals(true, savedSearch.getAlertDigestMode());
        assertEquals("23h", savedSearch.getAlertExpires());
        assertEquals(6, savedSearch.getAlertSeverity());
        assertEquals(true, savedSearch.getAlertSuppress());
        if (service.versionIsAtLeast("4.3")) {
            assertEquals("host", savedSearch.getAlertSuppressFields());
        }
        assertEquals("1m", savedSearch.getAlertSuppressPeriod());
        assertEquals("50%", savedSearch.getAlertThreshold());
        // NOTE: Always returns "0" or "1". Never "auto". Vince notified.
        assertEquals("0", savedSearch.getAlertTrack());
        assertEquals("number of events", savedSearch.getAlertType());
        assertEquals("*/5 * * * *", savedSearch.getCronSchedule());
        assertEquals("Cake!", savedSearch.getDescription());
        assertEquals(100, savedSearch.getDispatchBuckets());
        assertEquals("-100s@s", savedSearch.getDispatchEarliestTime());
        assertEquals("-1s@s", savedSearch.getDispatchLatestTime());
        assertEquals(false, savedSearch.getDispatchLookups());
        assertEquals(100000, savedSearch.getDispatchMaxCount());
        // NOTE: Should be int to match setter. See DVPL-1268.
        assertEquals(120, savedSearch.getDispatchMaxTime());
        assertEquals(true, savedSearch.getDispatchSpawnProcess());
        assertEquals("%FT%T.%Q", savedSearch.getDispatchTimeFormat());
        assertEquals("3p", savedSearch.getDispatchTtl());
        assertEquals("flash_timeline", savedSearch.getDisplayView());
        assertEquals(2, savedSearch.getMaxConcurrent());
        assertEquals(false, savedSearch.getRealtimeSchedule());
        assertEquals("foo", savedSearch.getRequestUiDispatchApp());
        assertEquals("bar", savedSearch.getRequestUiDispatchView());
        assertEquals(true, savedSearch.getRunOnStartup());
        assertEquals(null, savedSearch.getVsid());
        
        assertEquals(11, savedSearch.getDispatchReduceFrequency());
        assertEquals(true, savedSearch.getDispatchRtBackfill());
        assertEquals(false, savedSearch.getRestartOnSearchPeerAdd());
        assertEquals(true, savedSearch.isDisabled());
    }
    
    @Test
    public void testScheduled() {
        SavedSearch savedSearch = this.savedSearches.create(createTemporaryName(), "search index=_internal | head 1");
        
        assertFalse(savedSearch.isScheduled());
        savedSearch.setCronSchedule("*/5 * * * *");
        savedSearch.setIsScheduled(true);
        savedSearch.update();
        assertTrue(savedSearch.isScheduled());
        
        savedSearch.remove();
    }
    
    @Test
    public void testCreateWithNoSearch() {
        try {
            this.savedSearches.create(createTemporaryName());
            fail("Should've thrown!");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAcknowledge() {
        savedSearch.acknowledge();
    }

    @Test
    public void testUpdateWithBogusKeysFails() {
        try {
            Args args = new Args("borisTheMadBaboon", "Arrgh!");
            savedSearch.update(args);
            fail("Expected an exception.");
        } catch (Exception e) {}
    }

    @Test
    public void testCannotUpdateName() {
        String newName = savedSearchName + "-alteration";
        try {
            Args args = new Args("name", newName);
            savedSearch.update(args);
            savedSearch.refresh();
            fail("Expected exception to be raised when trying to update name.");
        } catch (Exception e) {}
    }

    @Test
    public void testDispatch() {
        final JobCollection jobs = service.getJobs();

        SavedSearchDispatchArgs args = new SavedSearchDispatchArgs();
        args.setDispatchBuckets(100);

        try {
            final Job job = savedSearch.dispatch(args);

            assertEventuallyTrue(new EventuallyTrueBehavior() {
                @Override
                public boolean predicate() {
                    return job.isReady();
                }
            });

            assertTrue(jobs.containsKey(job.getSid()));
        } catch (InterruptedException e) {
            fail(e.toString());
        }
    }

    @Test
    public void testDispatchWithoutOptions() {
        final JobCollection jobs = service.getJobs();

        try {
            final Job job = savedSearch.dispatch();

            assertEventuallyTrue(new EventuallyTrueBehavior() {
                @Override
                public boolean predicate() {
                    return job.isReady();
                }
            });

            assertTrue(jobs.containsKey(job.getSid()));
        } catch (InterruptedException e) {
            fail(e.toString());
        }
    }

    @Test
    public void testHistory() {
        savedSearch.refresh();
        Job[] oldJobs = savedSearch.history();

        try {
            final Job job = savedSearch.dispatch();
            assertEventuallyTrue(new EventuallyTrueBehavior() {
                @Override
                public boolean predicate() {
                    return job.isReady();
                }
            });

            assertEquals(oldJobs.length + 1, savedSearch.history().length);

            boolean isFound = false;
            for (Job j : savedSearch.history()) {
                if (j.getSid().equals(job.getSid())) {
                    isFound = true;
                }
            }
            assertTrue(isFound);

        } catch (InterruptedException e) {
            fail(e.toString());
        }
    }
}
