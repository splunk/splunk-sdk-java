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

public class FiredAlertsTest extends SDKTestCase {
    private String indexName;
    private Index index;
    private String savedSearchName;
    private SavedSearch savedSearch;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        indexName = createTemporaryName();
        assertFalse(service.getIndexes().containsKey(indexName));
        
        index = service.getIndexes().create(indexName);
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            { pauseTime = 500; tries = 50; }
            
            @Override
            public boolean predicate() {
                index.refresh();
                return !index.isDisabled();
            }
        });

        savedSearchName = createTemporaryName();
        
        String searchString = "search index=" + indexName;
        Args args = new Args();
        args.put("alert_type", "always");
        args.put("alert.severity", "3");
        args.put("alert.suppress", "0");
        args.put("alert.track", "1");
        args.put("dispatch.earliest_time", "-1h");
        args.put("dispatch.latest_time", "now");
        args.put("is_scheduled", "1");
        args.put("cron_schedule", "* * * * *");
        savedSearch = service.getSavedSearches().create(
                savedSearchName, searchString, args
        );
    }

    @After
    @Override
    public void tearDown() throws Exception {
        if (service.versionIsAtLeast("5.0.0")) {
            index.remove();
        }
        
        for (Job job : savedSearch.history()) {
            job.cancel();
        }
        savedSearch.remove();
        
        super.tearDown();
    }

    @Test
    public void testAlertsShowUp() {
        assertFalse(
                "Found alerts before sending any events.",
                service.getFiredAlertGroups().containsKey(savedSearchName)
        );

        // Send events
        index.submit(createTimestamp() + " Boris the mad baboon runs amok!");
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == 1;
            }
        });

        try {
            savedSearch.dispatch(); // Force the search to run now.
        } catch (InterruptedException e) {}
        
        final EntityCollection<FiredAlertGroup> firedAlertGroups =
                service.getFiredAlertGroups();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            { pauseTime = 2000; tries = 100; }
            
            @Override
            public boolean predicate() {
                firedAlertGroups.refresh();
                return firedAlertGroups.containsKey(savedSearchName);
            }
        });
        
        FiredAlertGroup firedAlertGroup =
                service.getFiredAlertGroups().get(savedSearchName);
        assertNotNull(firedAlertGroup);
        assertEquals(1, firedAlertGroup.getAlerts().size());

        for (FiredAlert firedAlert : firedAlertGroup.getAlerts().values()) {
            firedAlert.getAction();
            firedAlert.getAlertType();
            firedAlert.getExpirationTime();
            firedAlert.getSavedSearchName();
            firedAlert.getSeverity();
            firedAlert.getSid();
            firedAlert.getTriggeredAlertCount();
            firedAlert.getTriggerTime();
            firedAlert.getTriggerTimeRendered();
            firedAlert.isDigestMode();
        }
    }
}
