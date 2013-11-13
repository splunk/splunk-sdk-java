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

import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

public class IndexTest extends SDKTestCase {
    private String indexName;
    private Index index;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        indexName = createTemporaryName();
        index = service.getIndexes().create(indexName);
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return service.getIndexes().containsKey(indexName);
            }
        });
    }

    @After
    @Override
    public void tearDown() throws Exception {
        if (service.versionIsAtLeast("5.0.0")) {
            if (service.getIndexes().containsKey(indexName)) {
                index.remove();
            }
        } else {
            // Can't delete indexes via the REST API. Just let them build up.
        }
        
        super.tearDown();
    }

    @Test
    public void testDeletion() {
        if (service.versionIsEarlierThan("5.0.0")) {
            // Can't delete indexes via the REST API.
            return;
        }
        
        assertTrue(service.getIndexes().containsKey(indexName));
        
        index.remove();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return !service.getIndexes().containsKey(indexName);
            }
        });
    }

    @Test
    public void testDeletionFromCollection() {
        if (service.versionIsEarlierThan("5.0.0")) {
            // Can't delete indexes via the REST API.
            return;
        }
        
        assertTrue(service.getIndexes().containsKey(indexName));
        service.getIndexes().remove(indexName);
        
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return !service.getIndexes().containsKey(indexName);
            }
        });
    }

    @Test
    public void testAttachWith() throws IOException {
        final int originalEventCount = index.getTotalEventCount();
        
        index.attachWith(new ReceiverBehavior() {
            public void run(OutputStream stream) throws IOException {
                String s = createTimestamp() + " Boris the mad baboon!\r\n";
                stream.write(s.getBytes("UTF8"));
            }
        });
        
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            { tries = 60; }

            @Override
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == originalEventCount + 1;
            }
        });
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testIndexGettersThrowNoErrors() {
        index.getAssureUTF8();
        index.getBlockSignatureDatabase();
        index.getBlockSignSize();
        index.getBloomfilterTotalSizeKB();
        index.getColdPath();
        index.getColdPathExpanded();
        index.getColdToFrozenDir();
        index.getColdToFrozenScript();
        index.getCompressRawdata();
        index.getCurrentDBSizeMB();
        index.getDefaultDatabase();
        index.getEnableRealtimeSearch();
        index.getFrozenTimePeriodInSecs();
        index.getHomePath();
        index.getHomePathExpanded();
        index.getIndexThreads();
        index.getLastInitTime();
        index.getMaxBloomBackfillBucketAge();
        index.getMaxConcurrentOptimizes();
        index.getMaxDataSize();
        index.getMaxHotBuckets();
        index.getMaxHotIdleSecs();
        index.getMaxHotSpanSecs();
        index.getMaxMemMB();
        index.getMaxMetaEntries();
        index.getMaxRunningProcessGroups();
        index.getMaxTime();
        index.getMaxTotalDataSizeMB();
        index.getMaxWarmDBCount();
        index.getMemPoolMB();
        index.getMinRawFileSyncSecs();
        index.getMinTime();
        index.getNumBloomfilters();
        index.getNumHotBuckets();
        index.getNumWarmBuckets();
        index.getPartialServiceMetaPeriod();
        index.getQuarantineFutureSecs();
        index.getQuarantinePastSecs();
        index.getRawChunkSizeBytes();
        index.getRotatePeriodInSecs();
        index.getServiceMetaPeriod();
        index.getSuppressBannerList();
        index.getSync();
        index.getSyncMeta();
        index.getThawedPath();
        index.getThawedPathExpanded();
        index.getThrottleCheckPeriod();
        index.getTotalEventCount();
        index.isDisabled();
        index.isInternal();
        
        // Fields only available from 5.0 on.
        if (service.versionIsAtLeast("5.0.0")) {
            index.getBucketRebuildMemoryHint();
            index.getMaxTimeUnreplicatedNoAcks();
            index.getMaxTimeUnreplicatedWithAcks();
        }
    }

    @Test
    public void testSetters() {
        int newBlockSignSize = index.getBlockSignSize() + 1;
        index.setBlockSignSize(newBlockSignSize);
        int newFrozenTimePeriodInSecs = index.getFrozenTimePeriodInSecs()+1;
        index.setFrozenTimePeriodInSecs(newFrozenTimePeriodInSecs);
        int newMaxConcurrentOptimizes = index.getMaxConcurrentOptimizes()+1;
        index.setMaxConcurrentOptimizes(newMaxConcurrentOptimizes);
        String newMaxDataSize = "auto";
        index.setMaxDataSize(newMaxDataSize);
        int newMaxHotBuckets = index.getMaxHotBuckets()+1;
        index.setMaxHotBuckets(newMaxHotBuckets);
        int newMaxHotIdleSecs = index.getMaxHotIdleSecs()+1;
        index.setMaxHotIdleSecs(newMaxHotIdleSecs);
        int newMaxMemMB = index.getMaxMemMB()+1;
        index.setMaxMemMB(newMaxMemMB);
        int newMaxMetaEntries = index.getMaxMetaEntries()+1;
        index.setMaxMetaEntries(newMaxMetaEntries);
        int newMaxTotalDataSizeMB = index.getMaxTotalDataSizeMB()+1;
        index.setMaxTotalDataSizeMB(newMaxTotalDataSizeMB);
        int newMaxWarmDBCount = index.getMaxWarmDBCount()+1;
        index.setMaxWarmDBCount(newMaxWarmDBCount);
        String newMinRawFileSyncSecs = "disable";
        index.setMinRawFileSyncSecs(newMinRawFileSyncSecs);
        int newPartialServiceMetaPeriod = index.getPartialServiceMetaPeriod()+1;
        index.setPartialServiceMetaPeriod(newPartialServiceMetaPeriod);
        int newQuarantineFutureSecs = index.getQuarantineFutureSecs()+1;
        index.setQuarantineFutureSecs(newQuarantineFutureSecs);
        int newQuarantinePastSecs = index.getQuarantinePastSecs()+1;
        index.setQuarantinePastSecs(newQuarantinePastSecs);
        int newRawChunkSizeBytes = index.getRawChunkSizeBytes()+1;
        index.setRawChunkSizeBytes(newRawChunkSizeBytes);
        int newRotatePeriodInSecs = index.getRotatePeriodInSecs()+1;
        index.setRotatePeriodInSecs(newRotatePeriodInSecs);
        int newServiceMetaPeriod = index.getServiceMetaPeriod()+1;
        index.setServiceMetaPeriod(newServiceMetaPeriod);
        boolean newSyncMeta = !index.getSyncMeta();
        index.setSyncMeta(newSyncMeta);
        int newThrottleCheckPeriod = index.getThrottleCheckPeriod()+1;
        index.setThrottleCheckPeriod(newThrottleCheckPeriod);
        String coldToFrozenDir = index.getColdToFrozenDir();
        if (service.getInfo().getOsName().equals("Windows")) {
            index.setColdToFrozenDir("C:\\frozenDir\\" + index.getName());
        } else {
            index.setColdToFrozenDir("/tmp/foobar" + index.getName());
        }

        boolean newEnableOnlineBucketRepair = false;
        String newMaxBloomBackfillBucketAge = null;
        if (service.versionIsAtLeast("4.3")) {
            newEnableOnlineBucketRepair = !index.getEnableOnlineBucketRepair();
            index.setEnableOnlineBucketRepair(newEnableOnlineBucketRepair);
            newMaxBloomBackfillBucketAge = "20d";
            index.setMaxBloomBackfillBucketAge(newMaxBloomBackfillBucketAge);
        }
        
        String newBucketRebuildMemoryHint = null;
        int newMaxTimeUnreplicatedNoAcks = -1;
        int newMaxTimeUnreplicatedWithAcks = -1;
        if (service.versionIsAtLeast("5.0")) {
            newBucketRebuildMemoryHint = "auto";
            index.setBucketRebuildMemoryHint(newBucketRebuildMemoryHint);
            newMaxTimeUnreplicatedNoAcks = 300;
            index.setMaxTimeUnreplicatedNoAcks(newMaxTimeUnreplicatedNoAcks);
            newMaxTimeUnreplicatedWithAcks = 60;
            index.setMaxTimeUnreplicatedWithAcks(newMaxTimeUnreplicatedWithAcks);
        }

        index.update();
        index.refresh();

        assertEquals(newBlockSignSize, index.getBlockSignSize());
        assertEquals(newFrozenTimePeriodInSecs, index.getFrozenTimePeriodInSecs());
        assertEquals(newMaxConcurrentOptimizes, index.getMaxConcurrentOptimizes());
        assertEquals(newMaxDataSize, index.getMaxDataSize());
        assertEquals(newMaxHotBuckets, index.getMaxHotBuckets());
        assertEquals(newMaxHotIdleSecs, index.getMaxHotIdleSecs());
        assertEquals(newMaxMemMB, index.getMaxMemMB());
        assertEquals(newMaxMetaEntries, index.getMaxMetaEntries());
        assertEquals(newMaxTotalDataSizeMB, index.getMaxTotalDataSizeMB());
        assertEquals(newMaxWarmDBCount, index.getMaxWarmDBCount());
        assertEquals(newMinRawFileSyncSecs, index.getMinRawFileSyncSecs());
        assertEquals(newPartialServiceMetaPeriod, index.getPartialServiceMetaPeriod());
        assertEquals(newQuarantineFutureSecs, index.getQuarantineFutureSecs());
        assertEquals(newQuarantinePastSecs, index.getQuarantinePastSecs());
        assertEquals(newRawChunkSizeBytes, index.getRawChunkSizeBytes());
        assertEquals(newRotatePeriodInSecs, index.getRotatePeriodInSecs());
        assertEquals(newServiceMetaPeriod, index.getServiceMetaPeriod());
        assertEquals(newSyncMeta, index.getSyncMeta());
        assertEquals(newThrottleCheckPeriod, index.getThrottleCheckPeriod());
        if (service.getInfo().getOsName().equals("Windows")) {
            assertEquals("C:\\frozenDir\\" + index.getName(), index.getColdToFrozenDir());
        } else {
            assertEquals("/tmp/foobar" + index.getName(), index.getColdToFrozenDir());
        }
        if (service.versionIsAtLeast("4.3")) {
            assertEquals(
                    newEnableOnlineBucketRepair,
                    index.getEnableOnlineBucketRepair()
            );
            assertEquals(
                    newMaxBloomBackfillBucketAge,
                    index.getMaxBloomBackfillBucketAge()
            );
        }
        if (service.versionIsAtLeast("5.0")) {
            assertEquals(
                    newBucketRebuildMemoryHint,
                    index.getBucketRebuildMemoryHint()
            );
            assertEquals(
                    newMaxTimeUnreplicatedNoAcks,
                    index.getMaxTimeUnreplicatedNoAcks()
            );
            assertEquals(
                    newMaxTimeUnreplicatedWithAcks,
                    index.getMaxTimeUnreplicatedWithAcks()
            );
        }
        
        index.setColdToFrozenDir(coldToFrozenDir == null ? "" : coldToFrozenDir);
        index.update();
        
        String coldToFrozenScript = index.getColdToFrozenScript();
        index.setColdToFrozenScript("/bin/sh");
        index.update();
        assertEquals("/bin/sh", index.getColdToFrozenScript());
        index.setColdToFrozenScript(coldToFrozenScript == null ? "" : coldToFrozenScript);
        index.update();
        //index.setColdToFrozenScript(coldToFrozenScript);
        
        if (restartRequired()) {
            splunkRestart();
        }
    }

    @Test
    public void testEnable() {
        assertFalse(index.isDisabled());
        
        // Force the index to be disabled
        index.disable();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override public boolean predicate() {
                index.refresh();
                return index.isDisabled();
            }
        });

        // Disabling an index before Splunk 6 puts Splunk into a weird state that actually
        // requires a restart to get out of.
        if (service.versionIsEarlierThan("6.0.0")) {
            splunkRestart();
        }

        // And then enable it
        index.enable();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override public boolean predicate() {
                index.refresh();
                return !index.isDisabled();
            }
        });
    }

    @Test
    public void testSubmitOne() throws Exception {
        try {
            tryTestSubmitOne();
        } catch (AssertionFailedError e) {
            if (e.getMessage().contains("Test timed out before true.") && 
                    restartRequired()) {
                System.out.println(
                        "WARNING: Splunk indicated restart required while " +
                        "running a test. Trying to recover...");
                splunkRestart();
                
                tryTestSubmitOne();
            } else {
                throw e;
            }
        }
    }
    
    private void tryTestSubmitOne() {
        assertTrue(getResultCountOfIndex() == 0);
        assertTrue(index.getTotalEventCount() == 0);
        
        index.submit(createTimestamp() + " This is a test of the emergency broadcasting system.");
        
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return getResultCountOfIndex() == 1;
            }
        });
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == 1;
            }
        });
    }

    @Test
    public void testSubmitOneArgs() throws Exception {
        try {
            tryTestSubmitOneArgs();
        } catch (AssertionFailedError e) {
            if (e.getMessage().contains("Test timed out before true.") && 
                    restartRequired()) {
                System.out.println(
                        "WARNING: Splunk indicated restart required while " +
                        "running a test. Trying to recover...");
                splunkRestart();
                
                tryTestSubmitOne();
            } else {
                throw e;
            }
        }
    }
    
    private void tryTestSubmitOneArgs() {
        assertTrue(getResultCountOfIndex() == 0);
        assertTrue(index.getTotalEventCount() == 0);
        
        Args args = Args.create("sourcetype", "mysourcetype");
        index.submit(args, createTimestamp() + " This is a test of the emergency broadcasting system.");
        
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return getResultCountOfIndex() == 1;
            }
        });
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == 1;
            }
        });
    }
    
    @Test
    public void testSubmitOneInEachCall() {
        assertTrue(getResultCountOfIndex() == 0);
        assertTrue(index.getTotalEventCount() == 0);
        
        index.submit(createTimestamp() + " Hello world!\u0150");
        index.submit(createTimestamp() + " Goodbye world!\u0150");
        
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return getResultCountOfIndex() == 2;
            }
        });
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                index.refresh();
                
                // Some versions of Splunk only increase event count by 1.
                // Event count should never go up by more than the result count.
                int tec = index.getTotalEventCount();
                return (1 <= tec) && (tec <= 2);
            }
        });
    }
    
    @Test
    public void testSubmitMultipleInOneCall() {
        assertTrue(getResultCountOfIndex() == 0);
        assertTrue(index.getTotalEventCount() == 0);
        
        index.submit(
                createTimestamp() + " Hello world!\u0150" + "\r\n" +
                createTimestamp() + " Goodbye world!\u0150");
        
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return getResultCountOfIndex() == 2;
            }
        });
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                index.refresh();
                
                // Some versions of Splunk only increase event count by 1.
                // Event count should never go up by more than the result count.
                int tec = index.getTotalEventCount();
                return (1 <= tec) && (tec <= 2);
            }
        });
    }

    @Test
    public void testAttach() throws IOException {

        assertTrue(getResultCountOfIndex() == 0);
        assertTrue(index.getTotalEventCount() == 0);

        Socket socket = index.attach();
        OutputStream ostream = socket.getOutputStream();
        Writer out = new OutputStreamWriter(ostream, "UTF8");
        out.write(createTimestamp() + " Hello world!\u0150\r\n");
        out.write(createTimestamp() + " Goodbye world!\u0150\r\n");

        out.flush();
        socket.close();

        index.refresh();

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            { tries = 60; }

            @Override
            public boolean predicate() {
                index.refresh();
                return getResultCountOfIndex() == 2;
            }
        });

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                index.refresh();
                
                // Some versions of Splunk only increase event count by 1.
                // Event count should never go up by more than the result count.
                int tec = index.getTotalEventCount();
                return (1 <= tec) && (tec <= 2);
            }
        });
    }

    @Test
    public void testAttachArgs() throws IOException {
        assertTrue(getResultCountOfIndex() == 0);
        assertTrue(index.getTotalEventCount() == 0);
        
        Args args = Args.create("sourcetype", "mysourcetype");
        Socket socket = index.attach(args);
        OutputStream ostream = socket.getOutputStream();
        Writer out = new OutputStreamWriter(ostream, "UTF8");
        out.write(createTimestamp() + " Hello world!\u0150\r\n");
        out.write(createTimestamp() + " Goodbye world!\u0150\r\n");
        out.write(createTimestamp() + " Goodbye world again!\u0150\r\n");

        out.flush();
        socket.close();
        
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            { tries = 60; }
            @Override
            public boolean predicate() {
                return getResultCountOfIndex() == 3;
            }
        });
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            { tries = 60; }
            @Override
            public boolean predicate() {
                index.refresh();
                
                // Some versions of Splunk only increase event count by 1.
                // Event count should never go up by more than the result count.
                int tec = index.getTotalEventCount();
                return (1 <= tec) && (tec <= 3);
            }
        });
    }

    @Test
    public void testUpload() throws Exception {
        if (!hasTestData()) {
            System.out.println("WARNING: sdk-app-collection not installed in Splunk; skipping test.");
            return;
        }

        installApplicationFromTestData("file_to_upload");
        
        assertTrue(getResultCountOfIndex() == 0);
        assertTrue(index.getTotalEventCount() == 0);

        String fileToUpload = joinServerPath(new String[] {
                service.getSettings().getSplunkHome(),
                "etc", "apps", "file_to_upload", "log.txt"});
        index.upload(fileToUpload);
        
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return getResultCountOfIndex() == 4;
            }
        });
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                index.refresh();
                
                // Some versions of Splunk only increase event count by 1.
                // Event count should never go up by more than the result count.
                int tec = index.getTotalEventCount();
                return (1 <= tec) && (tec <= 4);
            }
        });
    }

    @Test
    public void testSubmitAndClean() throws InterruptedException {
        try {
            tryTestSubmitAndClean();
        } catch (SplunkException e) {
            if (e.getCode() == SplunkException.TIMEOUT) {
                // Due to flakiness of the underlying implementation,
                // this index clean method doesn't always work on a "dirty"
                // Splunk instance. Try again on a "clean" instance.
                System.out.println(
                        "WARNING: Index clean timed out. Trying again on a " +
                        "freshly restarted Splunk instance...");
                uncheckedSplunkRestart();
                
                tryTestSubmitAndClean();
            } else {
                throw e;
            }
        }
    }
    
    private void tryTestSubmitAndClean() throws InterruptedException {
        assertTrue(getResultCountOfIndex() == 0);
        
        // Make sure the index is not empty.
        index.submit("Hello world");
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            {
                tries = 50;
            }

            @Override
            public boolean predicate() {
                return getResultCountOfIndex() == 1;
            }
        });
        
        // Clean the index and make sure it's empty.
        // NOTE: Average time for this is 65s (!!!). Have seen 110+.
        index.clean(150);
        assertTrue(getResultCountOfIndex() == 0);
    }
    
    @Test
    public void testUpdateNameShouldFail() {
        try {
            index.update(new Args("name", createTemporaryName()));
            fail("Expected IllegalStateException.");
        }
        catch (IllegalStateException e) {
            // Good
        }
    }
    
    // === Utility ===

    private int getResultCountOfIndex() {
        InputStream results = service.oneshotSearch("search index=" + indexName);
        try {
            ResultsReaderXml resultsReader = new ResultsReaderXml(results);
            
            int numEvents = 0;
            while (resultsReader.getNextEvent() != null) {
                numEvents++;
            }
            return numEvents;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

