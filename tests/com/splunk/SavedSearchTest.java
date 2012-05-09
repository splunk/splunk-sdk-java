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

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SavedSearchTest extends SplunkTestCase {
    @Test public void testSavedSearches() {
        Service service = connect();

        SavedSearchCollection savedSearches = service.getSavedSearches();

        // Iterate saved searches and make sure we can read them.
        for (SavedSearch savedSearch : savedSearches.values()) {
            // Resource properties
            savedSearch.getName();
            savedSearch.getTitle();
            savedSearch.getPath();

            // SavedSearch properties get
            savedSearch.getActionEmailAuthPassword();
            savedSearch.getActionEmailAuthUsername();
            savedSearch.getActionEmailSendResults();
            savedSearch.getActionEmailBcc();
            savedSearch.getActionEmailCc();
            savedSearch.getActionEmailCommand();
            savedSearch.getActionEmailFormat();
            savedSearch.getActionEmailInline();
            savedSearch.getActionEmailMailServer();
            savedSearch.getActionEmailMaxResults();
            savedSearch.getActionEmailMaxTime();
            savedSearch.getActionEmailReportPaperOrientation();
            savedSearch.getActionEmailReportPaperSize();
            savedSearch.getActionEmailReportServerEnabled();
            savedSearch.getActionEmailReportServerUrl();
            savedSearch.getActionEmailSendPdf();
            savedSearch.getActionEmailSendResults();
            savedSearch.getActionEmailSubject();
            savedSearch.getActionEmailTo();
            savedSearch.getActionEmailTrackAlert();
            savedSearch.getActionEmailTtl();
            savedSearch.getActionEmailUseSsl();
            savedSearch.getActionEmailUseTls();
            savedSearch.getActionEmailWidthSortColumns();
            savedSearch.getActionPopulateLookupCommand();
            savedSearch.getActionPopulateLookupDest();
            savedSearch.getActionPopulateLookupHostname();
            savedSearch.getActionPopulateLookupMaxResults();
            savedSearch.getActionPopulateLookupMaxTime();
            savedSearch.getActionPopulateLookupTrackAlert();
            savedSearch.getActionPopulateLookupTtl();
            savedSearch.getActionRssCommand();
            savedSearch.getActionRssHostname();
            savedSearch.getActionRssMaxResults();
            savedSearch.getActionRssMaxTime();
            savedSearch.getActionRssTrackAlert();
            savedSearch.getActionRssTtl();
            savedSearch.getActionScriptCommand();
            savedSearch.getActionScriptFilename();
            savedSearch.getActionScriptHostname();
            savedSearch.getActionScriptMaxResults();
            savedSearch.getActionScriptMaxTime();
            savedSearch.getActionScriptTrackAlert();
            savedSearch.getActionScriptTtl();
            savedSearch.getActionSummaryIndexName();
            savedSearch.getActionSummaryIndexCommand();
            savedSearch.getActionSummaryIndexHostname();
            savedSearch.getActionSummaryIndexInline();
            savedSearch.getActionSummaryIndexMaxResults();
            savedSearch.getActionSummaryIndexMaxTime();
            savedSearch.getActionSummaryIndexTrackAlert();
            savedSearch.getActionSummaryIndexTtl();
            savedSearch.getAlertDigestMode();
            savedSearch.getAlertExpires();
            savedSearch.getAlertSeverity();
            savedSearch.getAlertSuppress();
            savedSearch.getAlertSuppressFields();
            savedSearch.getAlertSuppressPeriod();
            savedSearch.getAlertTrack();
            savedSearch.getAlertComparator();
            savedSearch.getAlertCondition();
            savedSearch.getAlertThreshold();
            savedSearch.getAlertType();
            savedSearch.getCronSchedule();
            savedSearch.getDescription();
            savedSearch.getDispatchBuckets();
            savedSearch.getDispatchEarliestTime();
            savedSearch.getDispatchLatestTime();
            savedSearch.getDispatchLookups();
            savedSearch.getDispatchMaxCount();
            savedSearch.getDispatchMaxTime();
            savedSearch.getDispatchReduceFreq();
            savedSearch.getDispatchRtBackfill();
            savedSearch.getDispatchSpawnProcess();
            savedSearch.getDispatchTimeFormat();
            savedSearch.getDispatchTtl();
            savedSearch.getDisplayView();
            savedSearch.getMaxConcurrent();
            savedSearch.getNextScheduledTime();
            savedSearch.getQualifiedSearch();
            savedSearch.getRealtimeSchedule();
            savedSearch.getRequestUiDispatchApp();
            savedSearch.getRequestUiDispatchView();
            savedSearch.getRestartOnSearchPeerAdd();
            savedSearch.getRunOnStartup();
            savedSearch.getSearch();
            savedSearch.getVsid();
            savedSearch.isActionEmail();
            savedSearch.isActionPopulateLookup();
            savedSearch.isActionRss();
            savedSearch.isActioncScript();
            savedSearch.isActionSummaryIndex();
            savedSearch.isDigestMode();
            savedSearch.isDisabled();
            savedSearch.isScheduled();
            savedSearch.isVisible();
        }
    }

    @Test public void testSavedSearchCrud() {
        Service service = connect();

        SavedSearchCollection savedSearches = service.getSavedSearches();

        // Ensure test starts in a known good state
        if (savedSearches.containsKey("sdk-test1"))
            savedSearches.remove("sdk-test1");
        assertFalse(savedSearches.containsKey("sdk-test1"));

        SavedSearch savedSearch;
        String search = "search index=sdk-tests * earliest=-1m";

        // Create a saved search
        savedSearches.create("sdk-test1", search);
        assertTrue(savedSearches.containsKey("sdk-test1"));

        // Read the saved search
        savedSearch = savedSearches.get("sdk-test1");
        assertEquals(savedSearch.isVisible(), true);
        // CONSIDER: Test some additinal default property values.

        // Update search properties, but don't specify required args to test
        // pulling them from the existing object
        savedSearch.update(new Args("is_visible", false));
        savedSearch.refresh();
        assertEquals(savedSearch.isVisible(), false);

        // Delete the saved search
        savedSearches.remove("sdk-test1");
        assertFalse(savedSearches.containsKey("sdk-test1"));

        // Create a saved search with some additional arguments
        savedSearch = savedSearches.create(
            "sdk-test1", search, new Args("is_visible", false));
        assertEquals(savedSearch.isVisible(), false);

        // set email params
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
        //savedSearch.setActionScriptFilename(String  filename);
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
        savedSearch.setSearch(search);

        savedSearch.update();

        // check
        assertTrue(savedSearch.isActionEmail());
        assertTrue(savedSearch.isActionPopulateLookup());
        assertTrue(savedSearch.isActionRss());
        assertTrue(savedSearch.isActioncScript());
        assertTrue(savedSearch.isActionSummaryIndex());
        assertTrue(savedSearch.isDigestMode());

        assertEquals(savedSearch.getActionEmailAuthPassword(), "sdk-password");
        assertEquals(savedSearch.getActionEmailAuthUsername(), "sdk-username");
        assertEquals(savedSearch.getActionEmailBcc(), "sdk-bcc@splunk.com");
        assertEquals(savedSearch.getActionEmailCc(), "sdk-cc@splunk.com");
        assertEquals(savedSearch.getActionEmailCommand(), "$name1$");
        assertEquals(savedSearch.getActionEmailFormat(), "text");
        assertEquals(savedSearch.getActionEmailFrom(), "sdk@splunk.com");
        assertEquals(savedSearch.getActionEmailHostname(), "dummy1.host.com");
        assertEquals(savedSearch.getActionEmailInline(), true);
        assertEquals(savedSearch.getActionEmailMailServer(), "splunk.com");
        assertEquals(savedSearch.getActionEmailMaxResults(), 101);
        assertEquals(savedSearch.getActionEmailMaxTime(), "10s");
        assertEquals(savedSearch.getActionEmailPdfView(), "dummy");
        assertEquals(savedSearch.getActionEmailPreProcessResults(), "*");
        assertEquals(savedSearch.getActionEmailReportPaperOrientation(),
                "landscape");
        assertEquals(savedSearch.getActionEmailReportPaperSize(), "letter");
        assertEquals(savedSearch.getActionEmailReportServerEnabled(), false);
        assertEquals(savedSearch.getActionEmailReportServerUrl(), "splunk.com");
        assertEquals(savedSearch.getActionEmailSendPdf(), false);
        assertEquals(savedSearch.getActionEmailSendResults(), false);
        assertEquals(savedSearch.getActionEmailSubject(), "sdk-subject");
        assertEquals(savedSearch.getActionEmailTo(), "sdk-to@splunk.com");
        assertEquals(savedSearch.getActionEmailTrackAlert(), false);
        assertEquals(savedSearch.getActionEmailTtl(), "61");
        assertEquals(savedSearch.getActionEmailUseSsl(), false);
        assertEquals(savedSearch.getActionEmailUseTls(), false);
        assertEquals(savedSearch.getActionEmailWidthSortColumns(), false);
        assertEquals(savedSearch.getActionPopulateLookupCommand(), "$name2$");
        assertEquals(savedSearch.getActionPopulateLookupDest(), "dummypath");
        assertEquals(savedSearch.getActionPopulateLookupHostname(),
                "dummy2.host.com");
        assertEquals(savedSearch.getActionPopulateLookupMaxResults(), 102);
        assertEquals(savedSearch.getActionPopulateLookupMaxTime(), "20s");
        assertEquals(savedSearch.getActionPopulateLookupTrackAlert(), false);
        assertEquals(savedSearch.getActionPopulateLookupTtl(), "62");
        assertEquals(savedSearch.getActionRssCommand(), "$name3$");
        assertEquals(savedSearch.getActionRssHostname(), "dummy3.host.com");
        assertEquals(savedSearch.getActionRssMaxResults(), 103);
        assertEquals(savedSearch.getActionRssMaxTime(), "30s");
        assertEquals(savedSearch.getActionRssTrackAlert(), false);
        assertEquals(savedSearch.getActionRssTtl(), "63");
        assertEquals(savedSearch.getActionScriptCommand(), "$name4$");
        //savedSearch.setActionScriptFilename(String  filename);
        assertEquals(savedSearch.getActionScriptHostname(), "dummy4.host.com");
        assertEquals(savedSearch.getActionScriptMaxResults(), 104);
        assertEquals(savedSearch.getActionScriptMaxTime(), "40s");
        assertEquals(savedSearch.getActionScriptTrackAlert(), false);
        assertEquals(savedSearch.getActionScriptTtl(), "64");
        assertEquals(savedSearch.getActionSummaryIndexName(), "default");
        assertEquals(savedSearch.getActionSummaryIndexCommand(), "$name5$");
        assertEquals(savedSearch.getActionSummaryIndexHostname(),
                "dummy5.host.com");
        assertEquals(savedSearch.getActionSummaryIndexInline(), false);
        assertEquals(savedSearch.getActionSummaryIndexMaxResults(), 105);
        assertEquals(savedSearch.getActionSummaryIndexMaxTime(), "50s");
        assertEquals(savedSearch.getActionSummaryIndexTrackAlert(), false);
        assertEquals(savedSearch.getActionSummaryIndexTtl(), "65");

        // Delete the saved search - using alternative method
        savedSearch.remove();
        savedSearches.refresh();
        assertFalse(savedSearches.containsKey("sdk-test1"));
    }

    @Test public void testDispatch() throws IOException {
        Service service = connect();

        SavedSearchCollection savedSearches = service.getSavedSearches();

        // Ensure test starts in a known good state
        if (savedSearches.containsKey("sdk-test1"))
            savedSearches.remove("sdk-test1");
        assertFalse(savedSearches.containsKey("sdk-test1"));

        String search = "search index=sdk-tests * earliest=-1m";

        // Create a saved search
        SavedSearch savedSearch = savedSearches.create("sdk-test1", search);

        Job job;

        // Dispatch the saved search and wait for results.
        job = savedSearch.dispatch();
        wait(job);
        job.getResults().close();
        job.cancel();

        // Dispatch with some additional search options
        job = savedSearch.dispatch(new Args("dispatch.buckets", 100));
        wait(job);
        job.getTimeline().close();
        job.cancel();

        // Delete the saved search
        savedSearches.remove("sdk-test1");
        assertFalse(savedSearches.containsKey("sdk-test1"));
    }

    boolean contains(Job[] history, String sid) {
        for (int i = 0; i < history.length; ++i)
            if (history[i].getSid().equals(sid))
                return true;
        return false;
    }

    @Test public void testHistory() {
        Service service = connect();

        SavedSearchCollection savedSearches = service.getSavedSearches();

        // Ensure test starts in a known good state
        if (savedSearches.containsKey("sdk-test1"))
            savedSearches.remove("sdk-test1");
        assertFalse(savedSearches.containsKey("sdk-test1"));

        String search = "search index=sdk-tests * earliest=-1m";

        // Create a saved search
        SavedSearch savedSearch = savedSearches.create("sdk test1", search);

        // Clear the history - even though we have a newly create saved search
        // its possible there was a previous saved search with the same name
        // that had a matching history.
        Job[] history = savedSearch.history();
        for (Job job : history) job.cancel();

        history = savedSearch.history();
        assertEquals(history.length, 0);

        Job job1 = savedSearch.dispatch();
        history = savedSearch.history();
        assertEquals(history.length, 1);
        assertTrue(contains(history, job1.getSid()));

        Job job2 = savedSearch.dispatch();
        history = savedSearch.history();
        assertEquals(history.length, 2);
        assertTrue(contains(history, job1.getSid()));
        assertTrue(contains(history, job2.getSid()));

        job1.cancel();
        history = savedSearch.history();
        assertEquals(history.length, 1);
        assertTrue(contains(history, job2.getSid()));

        job2.cancel();
        history = savedSearch.history();
        assertEquals(history.length, 0);

        // Delete the saved search
        savedSearches.remove("sdk test1");
        assertFalse(savedSearches.containsKey("sdk test1"));
    }
}

