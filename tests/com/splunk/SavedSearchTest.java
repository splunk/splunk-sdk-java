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

            // SavedSearch properties
            savedSearch.getActionEmailSendResults();
            savedSearch.getActionEmailTo();
            savedSearch.getAlertExpires();
            savedSearch.getAlertSeverity();
            savedSearch.getAlertSuppress();
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
            savedSearch.getDispatchSpawnProcess();
            savedSearch.getDispatchTimeFormat();
            savedSearch.getDispatchTtl();
            savedSearch.getDisplayView();
            savedSearch.getMaxConcurrent();
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

        // Update search properties
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

