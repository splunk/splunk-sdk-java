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

import org.junit.Test;

public class FiredAlertTest extends SplunkTestCase {
    final static String assertRoot = "Event Type assert: ";

    @Test public void testFiredAlerts() throws Exception {
        Service service = connect();

        // Create an index if one is not present.
        EntityCollection<Index> indexes = service.getIndexes();
        if (!indexes.containsKey("sdk-tests")) {
            indexes.create("sdk-tests");
            indexes.refresh();
        }
        assertTrue(assertRoot + "#1", indexes.containsKey("sdk-tests"));
        Index index = indexes.get("sdk-tests");

        String searchName = "sdk-test-search";

        // Clean the test index
        index.clean(60);
        assertEquals(assertRoot + "#2", 0, index.getTotalEventCount());

        // Ensure test starts in a known good state
        SavedSearchCollection savedSearches = service.getSavedSearches();
        if (savedSearches.containsKey(searchName))
            savedSearches.remove(searchName);
        assertFalse(assertRoot + "#3", savedSearches.containsKey(searchName));

        // Create a saved search that will register an alert for any event
        // submitted to the sdk-tests index.
        String search = "index=sdk-tests";

        // Create a saved search
        Args args = new Args();
        args.put("actions", "rss");
        args.put("alert_type", "always");
        args.put("alert_comparator", "greater than");
        args.put("alert_threshold", "0");
        args.put("alert.severity", "5");
        args.put("alert.suppress", "0");
        args.put("alert.track", "1");
        args.put("dispatch.earliest_time", "rt");
        args.put("dispatch.latest_time", "rt");
        args.put("is_scheduled", "0");
        args.put("realtime_schedule", "1");
        args.put("cron_schedule", "* * * * *");
        savedSearches.create(searchName, search, args);
        assertTrue(assertRoot + "#4", savedSearches.containsKey(searchName));
        SavedSearch savedSearch = savedSearches.get(searchName);

        // Cancel any running search with this name
        for (Job job: savedSearch.history()) {
            job.cancel();
        }

        // wait for all jobs to cancel; try for 30 seconds
        for (int i=0; i < 30; i++) {
            savedSearch.refresh();
            if (savedSearch.history().length == 0) break;
            sleep(1000);
        }
        assertEquals(assertRoot + "#4", 0, savedSearch.history().length);

        // Fire up the search
        savedSearch.update(new Args("is_scheduled", "1"));
        savedSearch.refresh();
        assertTrue(assertRoot + "#5", savedSearch.isScheduled());

        // Wait for the search to run
        for (int i=0; i < 30; i++) {
            savedSearch.refresh();
            if (savedSearch.history().length == 1) break;
            Thread.sleep(1000);
        }
        assertEquals(assertRoot + "#6", 1, savedSearch.history().length);

        for (int count=1; count < 6; count++) {
            index.submit("Hello --> " + count);
            for (int j=0; j<30; j++) {
                index.refresh();
                if (index.getTotalEventCount() == count) break;
                Thread.sleep(1000);
            }
            assertEquals(assertRoot + "#7", count, index.getTotalEventCount());
            Thread.sleep(2000); // Needed, or the fired alerts are not ready

            // After all that setup, check the fired alerts.
            FiredAlertGroupCollection
                firedAlertGroupCollection = service.getFiredAlertGroups();

            assertTrue(assertRoot + "#8",
                firedAlertGroupCollection.containsKey(searchName));
            FiredAlertGroup
                firedAlertGroup = firedAlertGroupCollection.get(searchName);

            assertEquals(assertRoot + "#9", count,
                firedAlertGroup.getAlerts().size());
        }

        savedSearch.remove();
        savedSearches.refresh();
        assertFalse(assertRoot + "#10", savedSearches.containsKey(searchName));
        FiredAlertGroupCollection
            firedAlertGroupCollection = service.getFiredAlertGroups();
        assertFalse(assertRoot + "#11",
            firedAlertGroupCollection.containsKey(searchName));
    }
}