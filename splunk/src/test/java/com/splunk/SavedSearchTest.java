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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class SavedSearchTest extends SDKTestCase {
    SavedSearchCollection savedSearches;
    String savedSearchName;
    SavedSearch savedSearch;
    String query = "search index=_internal * earliest=-1m | head 3";

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        Service.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_1);

        savedSearchName = createTemporaryName();
        savedSearches = service.getSavedSearches();
        savedSearch = savedSearches.create(savedSearchName, query);

        savedSearches.refresh();
        Assert.assertTrue(savedSearches.containsKey(savedSearchName));
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

        Assert.assertTrue(savedSearch.isActionEmail());
        Assert.assertTrue(savedSearch.isActionPopulateLookup());
        Assert.assertTrue(savedSearch.isActionRss());
        Assert.assertTrue(savedSearch.isActionScript());
        Assert.assertTrue(savedSearch.isActionSummaryIndex());
        Assert.assertTrue(savedSearch.isDigestMode());

        Assert.assertEquals("sdk-password", savedSearch.getActionEmailAuthPassword());
        Assert.assertEquals("sdk-username", savedSearch.getActionEmailAuthUsername());
        Assert.assertEquals("sdk-bcc@splunk.com", savedSearch.getActionEmailBcc());
        Assert.assertEquals("sdk-cc@splunk.com", savedSearch.getActionEmailCc());
        Assert.assertEquals("$name1$", savedSearch.getActionEmailCommand());
        Assert.assertEquals("text", savedSearch.getActionEmailFormat());
        Assert.assertEquals("sdk@splunk.com", savedSearch.getActionEmailFrom());
        Assert.assertEquals("dummy1.host.com", savedSearch.getActionEmailHostname());
        Assert.assertTrue(savedSearch.getActionEmailInline());
        Assert.assertEquals("splunk.com", savedSearch.getActionEmailMailServer());
        Assert.assertEquals(101, savedSearch.getActionEmailMaxResults());
        Assert.assertEquals("10s", savedSearch.getActionEmailMaxTime());
        Assert.assertEquals("dummy", savedSearch.getActionEmailPdfView());
        Assert.assertEquals("*", savedSearch.getActionEmailPreProcessResults());
        Assert.assertEquals("landscape", savedSearch.getActionEmailReportPaperOrientation());
        Assert.assertEquals("letter", savedSearch.getActionEmailReportPaperSize());
        Assert.assertFalse(savedSearch.getActionEmailReportServerEnabled());
        Assert.assertEquals("splunk.com", savedSearch.getActionEmailReportServerUrl());
        Assert.assertFalse(savedSearch.getActionEmailSendPdf());
        Assert.assertFalse(savedSearch.getActionEmailSendResults());
        Assert.assertEquals("sdk-subject", savedSearch.getActionEmailSubject());
        Assert.assertEquals("sdk-to@splunk.com", savedSearch.getActionEmailTo());
        Assert.assertFalse(savedSearch.getActionEmailTrackAlert());
        Assert.assertEquals("61", savedSearch.getActionEmailTtl());
        Assert.assertFalse(savedSearch.getActionEmailUseSsl());
        Assert.assertFalse(savedSearch.getActionEmailUseTls());
        Assert.assertFalse(savedSearch.getActionEmailWidthSortColumns());
        Assert.assertEquals("$name2$", savedSearch.getActionPopulateLookupCommand());
        Assert.assertEquals("dummypath", savedSearch.getActionPopulateLookupDest());
        Assert.assertEquals("dummy2.host.com", savedSearch.getActionPopulateLookupHostname());
        Assert.assertEquals(102, savedSearch.getActionPopulateLookupMaxResults());
        Assert.assertEquals("20s", savedSearch.getActionPopulateLookupMaxTime());
        Assert.assertFalse(savedSearch.getActionPopulateLookupTrackAlert());
        Assert.assertEquals("62", savedSearch.getActionPopulateLookupTtl());
        Assert.assertEquals("$name3$", savedSearch.getActionRssCommand());
        Assert.assertEquals("dummy3.host.com", savedSearch.getActionRssHostname());
        Assert.assertEquals(103, savedSearch.getActionRssMaxResults());
        Assert.assertEquals("30s", savedSearch.getActionRssMaxTime());
        Assert.assertFalse(savedSearch.getActionRssTrackAlert());
        Assert.assertEquals("63", savedSearch.getActionRssTtl());
        Assert.assertEquals("foo.sh", savedSearch.getActionScriptFilename());
        Assert.assertEquals("$name4$", savedSearch.getActionScriptCommand());
        Assert.assertEquals("dummy4.host.com", savedSearch.getActionScriptHostname());
        Assert.assertEquals(104, savedSearch.getActionScriptMaxResults());
        Assert.assertEquals("40s", savedSearch.getActionScriptMaxTime());
        Assert.assertFalse(savedSearch.getActionScriptTrackAlert());
        Assert.assertEquals("64", savedSearch.getActionScriptTtl());
        Assert.assertEquals("default", savedSearch.getActionSummaryIndexName());
        Assert.assertEquals("$name5$", savedSearch.getActionSummaryIndexCommand());
        Assert.assertEquals("dummy5.host.com", savedSearch.getActionSummaryIndexHostname());
        Assert.assertFalse(savedSearch.getActionSummaryIndexInline());
        Assert.assertEquals(105, savedSearch.getActionSummaryIndexMaxResults());
        Assert.assertEquals("50s", savedSearch.getActionSummaryIndexMaxTime());
        Assert.assertFalse(savedSearch.getActionSummaryIndexTrackAlert());
        Assert.assertEquals("65", savedSearch.getActionSummaryIndexTtl());
        Assert.assertEquals(savedSearch.isVisible(), !isVisible);
        
        boolean isPre620 = service.versionIsEarlierThan("6.2.0");
        try {
        	Assert.assertEquals(savedSearch.isEmbedEnabled(), false);
            Assert.assertNull(savedSearch.getEmbedToken());
            if (isPre620)
            	Assert.fail("Expected UnsupportedOperationException");
        } catch(UnsupportedOperationException uoe) {
        	if (!isPre620)
        		Assert.fail("Unexpected UnsupportedOperationException");
        	else
        		Assert.assertNotNull(uoe);
        }
        
        Assert.assertNull(savedSearch.getNextScheduledTime());
        if (service.versionIsEarlierThan("4.3")) {
            Assert.assertEquals("search  search index=boris abcd", savedSearch.getQualifiedSearch());
        } else {
            Assert.assertEquals("search search index=boris abcd", savedSearch.getQualifiedSearch());
        }
        
        Assert.assertEquals("greater than", savedSearch.getAlertComparator());
        Assert.assertEquals("*", savedSearch.getAlertCondition());
        Assert.assertEquals(true, savedSearch.getAlertDigestMode());
        Assert.assertEquals("23h", savedSearch.getAlertExpires());
        Assert.assertEquals(6, savedSearch.getAlertSeverity());
        Assert.assertEquals(true, savedSearch.getAlertSuppress());
        if (service.versionIsAtLeast("4.3")) {
            Assert.assertEquals("host", savedSearch.getAlertSuppressFields());
        }
        Assert.assertEquals("1m", savedSearch.getAlertSuppressPeriod());
        Assert.assertEquals("50%", savedSearch.getAlertThreshold());
        // NOTE: Always returns "0" or "1". Never "auto". Vince notified.
        Assert.assertEquals("0", savedSearch.getAlertTrack());
        Assert.assertEquals("number of events", savedSearch.getAlertType());
        Assert.assertEquals("*/5 * * * *", savedSearch.getCronSchedule());
        Assert.assertEquals("Cake!", savedSearch.getDescription());
        Assert.assertEquals(100, savedSearch.getDispatchBuckets());
        Assert.assertEquals("-100s@s", savedSearch.getDispatchEarliestTime());
        Assert.assertEquals("-1s@s", savedSearch.getDispatchLatestTime());
        Assert.assertEquals(false, savedSearch.getDispatchLookups());
        Assert.assertEquals(100000, savedSearch.getDispatchMaxCount());
        // NOTE: Should be int to match setter. See DVPL-1268.
        Assert.assertEquals(120, savedSearch.getDispatchMaxTime());
        Assert.assertEquals(true, savedSearch.getDispatchSpawnProcess());
        Assert.assertEquals("%FT%T.%Q", savedSearch.getDispatchTimeFormat());
        Assert.assertEquals("3p", savedSearch.getDispatchTtl());
        Assert.assertEquals("flash_timeline", savedSearch.getDisplayView());
        Assert.assertEquals(2, savedSearch.getMaxConcurrent());
        Assert.assertEquals(false, savedSearch.getRealtimeSchedule());
        Assert.assertEquals("foo", savedSearch.getRequestUiDispatchApp());
        Assert.assertEquals("bar", savedSearch.getRequestUiDispatchView());
        Assert.assertEquals(true, savedSearch.getRunOnStartup());
        Assert.assertEquals(null, savedSearch.getVsid());
        
        Assert.assertEquals(11, savedSearch.getDispatchReduceFrequency());
        Assert.assertEquals(true, savedSearch.getDispatchRtBackfill());
        Assert.assertEquals(false, savedSearch.getRestartOnSearchPeerAdd());
        Assert.assertEquals(true, savedSearch.isDisabled());
    }
    
    @Test
    public void testScheduled() {
        SavedSearch savedSearch = this.savedSearches.create(createTemporaryName(), "search index=_internal | head 1");
        
        Assert.assertFalse(savedSearch.isScheduled());
        savedSearch.setCronSchedule("*/5 * * * *");
        savedSearch.setIsScheduled(true);
        savedSearch.update();
        Assert.assertTrue(savedSearch.isScheduled());
        
        savedSearch.remove();
    }

    @Test
    public void testGetSavedSearchByTitle(){
        try{
            SavedSearch fetchedSavedSearch = this.savedSearches.getService().getSavedSearch(savedSearchName);
            Assert.assertEquals(fetchedSavedSearch.getName(),savedSearch.getName());
        }catch (Exception e) { }
    }

    @Test
    public void testCreateWithNoSearch() {
        try {
            this.savedSearches.create(createTemporaryName());
            Assert.fail("Should've thrown!");
        } catch (Exception e) {
            Assert.assertTrue(true);
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
            Assert.fail("Expected an exception.");
        } catch (Exception e) {}
    }

    @Test
    public void testCannotUpdateName() {
        String newName = savedSearchName + "-alteration";
        try {
            Args args = new Args("name", newName);
            savedSearch.update(args);
            savedSearch.refresh();
            Assert.fail("Expected exception to be raised when trying to update name.");
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

            Assert.assertTrue(jobs.containsKey(job.getSid()));
        } catch (InterruptedException e) {
            Assert.fail(e.toString());
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

            Assert.assertTrue(jobs.containsKey(job.getSid()));
        } catch (InterruptedException e) {
            Assert.fail(e.toString());
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

            Assert.assertEquals(oldJobs.length + 1, savedSearch.history().length);

            boolean isFound = false;
            for (Job j : savedSearch.history()) {
                if (j.getSid().equals(job.getSid())) {
                    isFound = true;
                }
            }
            Assert.assertTrue(isFound);

        } catch (InterruptedException e) {
            Assert.fail(e.toString());
        }
    }
    
    @Test
    public void testHistoryWithArgs(){
        savedSearch.refresh();
        Assert.assertEquals(0, savedSearch.history().length);
        try {
            Job job;
            for(int i = 0 ; i < 31 ; i++){
                job = savedSearch.dispatch();
                while(!job.isReady()){
                    sleep(2);
                }
            }
            //history without any argument, it will return max 30 jobs only.
            Assert.assertEquals(30, savedSearch.history().length);

            //history with argument 'count' set to '0' i.e it returns the whole history
            HashMap<String, Object> args = new HashMap<String, Object>();
            args.put("count", 0);
            Assert.assertEquals(31, savedSearch.history(args).length);

            //history with argument 'count' set to '10' i.e. it will return only 10 jobs from history
            args.put("count", 10);
            args.put("sort_dir", "desc");
            Assert.assertEquals(10, savedSearch.history(args).length);

        } catch (InterruptedException e) {
            Assert.fail(e.toString());
        }
    }
}
