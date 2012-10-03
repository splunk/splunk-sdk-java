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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class SavedSearchTest extends SplunkTestCase {
    final static String assertRoot = "Saved Search assert: ";

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
            savedSearch.isActionScript();
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
        assertFalse(assertRoot + "#1", savedSearches.containsKey("sdk-test1"));

        SavedSearch savedSearch;
        String search = "search index=sdk-tests * earliest=-1m";

        // Create a saved search
        savedSearches.create("sdk-test1", search);
        assertTrue(assertRoot + "#2", savedSearches.containsKey("sdk-test1"));

        // Read the saved search
        savedSearch = savedSearches.get("sdk-test1");
        assertTrue(assertRoot + "#3", savedSearch.isVisible());
        // CONSIDER: Test some additinal default property values.

        // Update search properties, but don't specify required args to test
        // pulling them from the existing object
        savedSearch.update(new Args("is_visible", false));
        savedSearch.refresh();
        assertFalse(assertRoot + "#4", savedSearch.isVisible());

        // Delete the saved search
        savedSearches.remove("sdk-test1");
        assertFalse(assertRoot + "#5", savedSearches.containsKey("sdk-test1"));

        // Create a saved search with some additional arguments
        savedSearch = savedSearches.create(
            "sdk-test1", search, new Args("is_visible", false));
        assertFalse(assertRoot + "#6", savedSearch.isVisible());

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
        assertTrue(assertRoot + "#7", savedSearch.isActionEmail());
        assertTrue(assertRoot + "#8", savedSearch.isActionPopulateLookup());
        assertTrue(assertRoot + "#9", savedSearch.isActionRss());
        assertTrue(assertRoot + "#10", savedSearch.isActionScript());
        assertTrue(assertRoot + "#11", savedSearch.isActionSummaryIndex());
        assertTrue(assertRoot + "#12", savedSearch.isDigestMode());

        assertEquals(assertRoot + "#12", "sdk-password",
            savedSearch.getActionEmailAuthPassword());
        assertEquals(assertRoot + "#13", "sdk-username",
            savedSearch.getActionEmailAuthUsername());
        assertEquals(assertRoot + "#14", "sdk-bcc@splunk.com",
            savedSearch.getActionEmailBcc());
        assertEquals(assertRoot + "#15", "sdk-cc@splunk.com",
            savedSearch.getActionEmailCc());
        assertEquals(assertRoot + "#16", "$name1$",
            savedSearch.getActionEmailCommand());
        assertEquals(assertRoot + "#17", "text",
            savedSearch.getActionEmailFormat());
        assertEquals(assertRoot + "#18", "sdk@splunk.com",
            savedSearch.getActionEmailFrom());
        assertEquals(assertRoot + "#19", "dummy1.host.com",
            savedSearch.getActionEmailHostname());
        assertTrue(assertRoot + "#20", savedSearch.getActionEmailInline());
        assertEquals(assertRoot + "#21", "splunk.com",
            savedSearch.getActionEmailMailServer());
        assertEquals(assertRoot + "#22", 101,
            savedSearch.getActionEmailMaxResults());
        assertEquals(assertRoot + "#23", "10s",
            savedSearch.getActionEmailMaxTime());
        assertEquals(assertRoot + "#24", "dummy",
            savedSearch.getActionEmailPdfView());
        assertEquals(assertRoot + "#25", "*",
            savedSearch.getActionEmailPreProcessResults());
        assertEquals(assertRoot + "#26", "landscape",
            savedSearch.getActionEmailReportPaperOrientation());
        assertEquals(assertRoot + "#27", "letter",
            savedSearch.getActionEmailReportPaperSize());
        assertFalse(assertRoot + "#28",
            savedSearch.getActionEmailReportServerEnabled());
        assertEquals(assertRoot + "#29", "splunk.com",
            savedSearch.getActionEmailReportServerUrl());
        assertFalse(assertRoot + "#30", savedSearch.getActionEmailSendPdf());
        assertFalse(assertRoot + "#31",
            savedSearch.getActionEmailSendResults());
        assertEquals(assertRoot + "#32", "sdk-subject",
            savedSearch.getActionEmailSubject());
        assertEquals(assertRoot + "#33", "sdk-to@splunk.com",
            savedSearch.getActionEmailTo());
        assertFalse(assertRoot + "#34", savedSearch.getActionEmailTrackAlert());
        assertEquals(assertRoot + "#35", "61", savedSearch.getActionEmailTtl());
        assertFalse(assertRoot + "#36", savedSearch.getActionEmailUseSsl());
        assertFalse(assertRoot + "#37", savedSearch.getActionEmailUseTls());
        assertFalse(assertRoot + "#38",
            savedSearch.getActionEmailWidthSortColumns());
        assertEquals(assertRoot + "#39", "$name2$",
            savedSearch.getActionPopulateLookupCommand());
        assertEquals(assertRoot + "#40", "dummypath",
            savedSearch.getActionPopulateLookupDest());
        assertEquals(assertRoot + "#41", "dummy2.host.com",
            savedSearch.getActionPopulateLookupHostname());
        assertEquals(assertRoot + "#42", 102,
            savedSearch.getActionPopulateLookupMaxResults());
        assertEquals(assertRoot + "#43", "20s",
            savedSearch.getActionPopulateLookupMaxTime());
        assertFalse(assertRoot + "#44",
            savedSearch.getActionPopulateLookupTrackAlert());
        assertEquals(assertRoot + "#45", "62",
            savedSearch.getActionPopulateLookupTtl());
        assertEquals(assertRoot + "#46", "$name3$",
            savedSearch.getActionRssCommand());
        assertEquals(assertRoot + "#47", "dummy3.host.com",
            savedSearch.getActionRssHostname());
        assertEquals(assertRoot + "#48", 103,
            savedSearch.getActionRssMaxResults());
        assertEquals(assertRoot + "#49", "30s",
            savedSearch.getActionRssMaxTime());
        assertFalse(assertRoot + "#50", savedSearch.getActionRssTrackAlert());
        assertEquals(assertRoot + "#51", "63", savedSearch.getActionRssTtl());
        assertEquals(assertRoot + "#52", "$name4$",
            savedSearch.getActionScriptCommand());
        //savedSearch.setActionScriptFilename(String  filename);
        assertEquals(assertRoot + "#53", "dummy4.host.com",
            savedSearch.getActionScriptHostname());
        assertEquals(assertRoot + "#54", 104,
            savedSearch.getActionScriptMaxResults());
        assertEquals(assertRoot + "#55", "40s",
            savedSearch.getActionScriptMaxTime());
        assertFalse(assertRoot + "#56",
            savedSearch.getActionScriptTrackAlert());
        assertEquals(assertRoot + "#57", "64",
            savedSearch.getActionScriptTtl());
        assertEquals(assertRoot + "#58", "default",
            savedSearch.getActionSummaryIndexName());
        assertEquals(assertRoot + "#59", "$name5$",
            savedSearch.getActionSummaryIndexCommand());
        assertEquals(assertRoot + "#60", "dummy5.host.com",
            savedSearch.getActionSummaryIndexHostname());
        assertFalse(assertRoot + "#61",
            savedSearch.getActionSummaryIndexInline());
        assertEquals(assertRoot + "#62", 105,
            savedSearch.getActionSummaryIndexMaxResults());
        assertEquals(assertRoot + "#63", "50s",
            savedSearch.getActionSummaryIndexMaxTime());
        assertFalse(assertRoot + "#64",
            savedSearch.getActionSummaryIndexTrackAlert());
        assertEquals(assertRoot + "#65", "65",
            savedSearch.getActionSummaryIndexTtl());

        // Delete the saved search - using alternative method
        savedSearch.remove();
        savedSearches.refresh();
        assertFalse(assertRoot + "#66", savedSearches.containsKey("sdk-test1"));
    }

    @Test public void testDispatch() throws IOException, InterruptedException {
        Service service = connect();

        SavedSearchCollection savedSearches = service.getSavedSearches();

        // Ensure test starts in a known good state
        if (savedSearches.containsKey("sdk-test1"))
            savedSearches.remove("sdk-test1");
        assertFalse(assertRoot + "#67", savedSearches.containsKey("sdk-test1"));

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
        assertFalse(assertRoot + "#68", savedSearches.containsKey("sdk-test1"));
    }

    boolean contains(Job[] history, String sid) {
        for (int i = 0; i < history.length; ++i)
            if (history[i].getSid().equals(sid))
                return true;
        return false;
    }

    @Test public void testHistory() throws InterruptedException {
        Service service = connect();

        SavedSearchCollection savedSearches = service.getSavedSearches();

        // Ensure test starts in a known good state
        if (savedSearches.containsKey("sdk-test1"))
            savedSearches.remove("sdk-test1");
        assertFalse(assertRoot + "#69", savedSearches.containsKey("sdk-test1"));

        String search = "search index=sdk-tests * earliest=-1m";

        // Create a saved search
        SavedSearch savedSearch = savedSearches.create("sdk-test1", search);

        // Clear the history - even though we have a newly create saved search
        // its possible there was a previous saved search with the same name
        // that had a matching history.
        Job[] history = savedSearch.history();
        for (Job job : history) job.cancel();

        history = savedSearch.history();
        assertEquals(assertRoot + "#70", 0, history.length);

        Job job1 = savedSearch.dispatch();
        ready(job1);
        history = savedSearch.history();
        assertEquals(assertRoot + "#71", 1, history.length);
        assertTrue(contains(history, job1.getSid()));

        Job job2 = savedSearch.dispatch();
        ready(job2);
        history = savedSearch.history();
        assertEquals(assertRoot + "#72", 2, history.length);
        assertTrue(assertRoot + "#73", contains(history, job1.getSid()));
        assertTrue(assertRoot + "#74", contains(history, job2.getSid()));

        job1.cancel();
        history = savedSearch.history();
        assertEquals(assertRoot + "#75", 1, history.length);
        assertTrue(assertRoot + "#76", contains(history, job2.getSid()));

        job2.cancel();
        history = savedSearch.history();
        assertEquals(assertRoot + "#77", 0, history.length);

        // Delete the saved search
        savedSearches.remove("sdk-test1");
        assertFalse(assertRoot + "#78", savedSearches.containsKey("sdk-test1"));
    }
    
    @Test public void testListSavedSearches() {
    	Service service = connect();

    	SavedSearchCollectionArgs ascArgs = new SavedSearchCollectionArgs();
    	ascArgs.setSortDirection(CollectionArgs.SortDirection.ASC);
    	
        SavedSearchCollection savedSearchesAsc = service.getSavedSearches(ascArgs);
        List<String> savedSearchNamesAsc = new ArrayList<String>(savedSearchesAsc.keySet());
        
        SavedSearchCollectionArgs descArgs = new SavedSearchCollectionArgs();
    	descArgs.setSortDirection(CollectionArgs.SortDirection.DESC);
    	
        SavedSearchCollection savedSearchesDesc = service.getSavedSearches(descArgs);
        List<String> savedSearchNamesDesc = new ArrayList<String>(savedSearchesDesc.keySet());
        
        List<String> savedSearchNamesDescReversed = savedSearchNamesDesc;
        Collections.reverse(savedSearchNamesDescReversed);
        
        assertEquals(savedSearchNamesAsc, savedSearchNamesDescReversed);
    }
}

