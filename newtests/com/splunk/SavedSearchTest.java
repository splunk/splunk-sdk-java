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
        Job[] oldJobs = savedSearch.history();

        try {
            final Job job = savedSearch.dispatch();
            assertEventuallyTrue(new EventuallyTrueBehavior() {
                @Override
                public boolean predicate() {
                    return job.isReady();
                }
            });

            assertEquals(oldJobs.length + 1, service.getJobs().size());

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
