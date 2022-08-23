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

import org.junit.*;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
            if (service.getIndexes().containsKey(indexName) && System.getenv("TRAVIS") == null) {
                try {
                    index.remove();
                } catch(HttpException he) {
                    if (he.getStatus() == 400) {
                        uncheckedSplunkRestart();
                        index.remove();
                    } else {
                        throw he;
                    }
                }
            }
        } else {
            // Can't delete indexes via the REST API. Just let them build up.
        }

        // At least in CI the test exists with a required restart
        uncheckedSplunkRestart();

        super.tearDown();
    }

    @Test
    public void testAttachWithCookieHeader() throws IOException {
    	Assume.assumeTrue(System.getenv("TRAVIS") == null);
        if (service.versionIsEarlierThan("6.2")) {
            // Cookies not implemented before version 6.2
            return;
        }
        // Check that their are Splunk Auth cookies at all
        Assert.assertTrue(service.hasSplunkAuthCookies());

        // Make a service that only has that cookie
        String validCookie = service.stringifyCookies();
        Args args = new Args();
        args.put("cookie", validCookie);
        final Service s = new Service(args);

        // Get the index with our service that only has a cookie
        Index localIndex = s.getIndexes().get(indexName);
        final int oldResultCountOfIndex = getResultCountOfIndex(s);
        final int oldIndexTotalEventCount = index.getTotalEventCount();

        Assert.assertEquals(0, oldResultCountOfIndex);
        Assert.assertEquals(0, oldIndexTotalEventCount);

        Socket socket = localIndex.attach();

        OutputStream ostream = socket.getOutputStream();
        Writer out = new OutputStreamWriter(ostream, "UTF-8");
        out.write(createTimestamp() + " Hello world!\u0150\r\n");
        out.write(createTimestamp() + " Goodbye world!\u0150\r\n");

        out.flush();
        socket.close();

        localIndex.refresh();

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            { tries = 60; }

            @Override
            public boolean predicate() {
                index.refresh();
                return getResultCountOfIndex(s) == oldResultCountOfIndex + 2;
            }
        });

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                index.refresh();

                // Some versions of Splunk only increase event count by 1.
                // Event count should never go up by more than the result count.
                int tec = index.getTotalEventCount();
                return tec == oldIndexTotalEventCount + 1;
            }
        });


    }

    @Test
    public void testDeletion() {
        if (service.versionIsEarlierThan("5.0.0")) {
            // Can't delete indexes via the REST API.
            return;
        }

        Assert.assertTrue(service.getIndexes().containsKey(indexName));

        try {
            index.remove();
        } catch(HttpException he) {
            if (he.getStatus() == 400) {
                uncheckedSplunkRestart();
                index.remove();
            } else {
                throw he;
            }
        }
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

        Assert.assertTrue(service.getIndexes().containsKey(indexName));

        try {
            service.getIndexes().remove(indexName);
        } catch(HttpException he) {
            if (he.getStatus() == 400) {
                uncheckedSplunkRestart();
                service.getIndexes().remove(indexName);
            } else {
                throw he;
            }
        }

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
                stream.write(s.getBytes("UTF-8"));
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
        int newFrozenTimePeriodInSecs = index.getFrozenTimePeriodInSecs()+1;
        index.setFrozenTimePeriodInSecs(newFrozenTimePeriodInSecs);
        int newMaxConcurrentOptimizes = index.getMaxConcurrentOptimizes()+1;
        index.setMaxConcurrentOptimizes(newMaxConcurrentOptimizes);
        String newMaxDataSize = "auto";
        index.setMaxDataSize(newMaxDataSize);
        String newMaxHotBuckets = null;
        if (service.versionIsEarlierThan("8.1.0")) {
            int newMaxHotBucketsInt = Integer.parseInt(index.getMaxHotBuckets())+1;
            newMaxHotBuckets = String.valueOf(newMaxHotBucketsInt);
        } else {
            newMaxHotBuckets = "auto";
        }
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

        Assert.assertEquals(newFrozenTimePeriodInSecs, index.getFrozenTimePeriodInSecs());
        Assert.assertEquals(newMaxConcurrentOptimizes, index.getMaxConcurrentOptimizes());
        Assert.assertEquals(newMaxDataSize, index.getMaxDataSize());
        Assert.assertEquals(newMaxHotBuckets, index.getMaxHotBuckets());
        Assert.assertEquals(newMaxHotIdleSecs, index.getMaxHotIdleSecs());
        Assert.assertEquals(newMaxMemMB, index.getMaxMemMB());
        Assert.assertEquals(newMaxMetaEntries, index.getMaxMetaEntries());
        Assert.assertEquals(newMaxTotalDataSizeMB, index.getMaxTotalDataSizeMB());
        Assert.assertEquals(newMaxWarmDBCount, index.getMaxWarmDBCount());
        Assert.assertEquals(newMinRawFileSyncSecs, index.getMinRawFileSyncSecs());
        Assert.assertEquals(newPartialServiceMetaPeriod, index.getPartialServiceMetaPeriod());
        Assert.assertEquals(newQuarantineFutureSecs, index.getQuarantineFutureSecs());
        Assert.assertEquals(newQuarantinePastSecs, index.getQuarantinePastSecs());
        Assert.assertEquals(newRawChunkSizeBytes, index.getRawChunkSizeBytes());
        Assert.assertEquals(newRotatePeriodInSecs, index.getRotatePeriodInSecs());
        Assert.assertEquals(newServiceMetaPeriod, index.getServiceMetaPeriod());
        Assert.assertEquals(newSyncMeta, index.getSyncMeta());
        Assert.assertEquals(newThrottleCheckPeriod, index.getThrottleCheckPeriod());
        if (service.getInfo().getOsName().equals("Windows")) {
            Assert.assertEquals("C:\\frozenDir\\" + index.getName(), index.getColdToFrozenDir());
        } else {
            Assert.assertEquals("/tmp/foobar" + index.getName(), index.getColdToFrozenDir());
        }
        if (service.versionIsAtLeast("4.3")) {
            Assert.assertEquals(
                    newEnableOnlineBucketRepair,
                    index.getEnableOnlineBucketRepair()
            );
            Assert.assertEquals(
                    newMaxBloomBackfillBucketAge,
                    index.getMaxBloomBackfillBucketAge()
            );
        }
        if (service.versionIsAtLeast("5.0")) {
            Assert.assertEquals(
                    newBucketRebuildMemoryHint,
                    index.getBucketRebuildMemoryHint()
            );
            Assert.assertEquals(
                    newMaxTimeUnreplicatedNoAcks,
                    index.getMaxTimeUnreplicatedNoAcks()
            );
            Assert.assertEquals(
                    newMaxTimeUnreplicatedWithAcks,
                    index.getMaxTimeUnreplicatedWithAcks()
            );
        }

        index.setColdToFrozenDir(coldToFrozenDir == null ? "" : coldToFrozenDir);
        index.update();

        String coldToFrozenScript = index.getColdToFrozenScript();
        index.setColdToFrozenScript("/bin/sh");
        index.update();
        Assert.assertEquals("/bin/sh", index.getColdToFrozenScript());
        index.setColdToFrozenScript(coldToFrozenScript == null ? "" : coldToFrozenScript);
        index.update();
        //index.setColdToFrozenScript(coldToFrozenScript);

        if (restartRequired()) {
            splunkRestart();
        }
    }

    @Test
    public void testEnable() {
        Assert.assertFalse(index.isDisabled());

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
        Assert.assertTrue(getResultCountOfIndex(service) == 0);
        Assert.assertTrue(index.getTotalEventCount() == 0);

        index.submit(createTimestamp() + " This is a test of the emergency broadcasting system.");

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return getResultCountOfIndex(service) == 1;
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
    public void testSubmitOneWithNamespacedService() {
        Map<String, Object> opts = new HashMap<String, Object>(command.opts);
        opts.put("app", "search");
        final Service service = Service.connect(opts);
        Assert.assertNotNull(service);
        
        final String indexName = createTemporaryName();
        final Index index = service.getIndexes().create(indexName);

        Assert.assertTrue(getResultCountOfIndex(service) == 0);
        Assert.assertTrue(index.getTotalEventCount() == 0);
        index.submit(createTimestamp() + " This is a test of the emergency broadcasting system.");
        
        assertEventuallyTrue(new EventuallyTrueBehavior() {

            @Override
            public boolean predicate() {
                return getResultCountOfIndex(service, indexName) == 1;
            }
        });
        
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == 1;
            }
        });
        try {
            index.remove();
        } catch (Exception e) {
            System.out.println(
                    "WARNING: index " + indexName + " cannot be deleted." +
                            " Error: " + e.toString());
        }
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
        Assert.assertTrue(getResultCountOfIndex(service) == 0);
        Assert.assertTrue(index.getTotalEventCount() == 0);

        Args args = Args.create("sourcetype", "mysourcetype");
        index.submit(args, createTimestamp() + " This is a test of the emergency broadcasting system.");

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return getResultCountOfIndex(service) == 1;
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
        Assert.assertTrue(getResultCountOfIndex(service) == 0);
        Assert.assertTrue(index.getTotalEventCount() == 0);

        index.submit(createTimestamp() + " Hello world!\u0150");
        index.submit(createTimestamp() + " Goodbye world!\u0150");

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return getResultCountOfIndex(service) == 2;
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
        Assert.assertTrue(getResultCountOfIndex(service) == 0);
        Assert.assertTrue(index.getTotalEventCount() == 0);

        index.submit(
                createTimestamp() + " Hello world!\u0150" + "\r\n" +
                createTimestamp() + " Goodbye world!\u0150");

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return getResultCountOfIndex(service) == 2;
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

        Assert.assertTrue(getResultCountOfIndex(service) == 0);
        Assert.assertTrue(index.getTotalEventCount() == 0);

        Socket socket = index.attach();
        OutputStream ostream = socket.getOutputStream();
        Writer out = new OutputStreamWriter(ostream, "UTF-8");
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
                return getResultCountOfIndex(service) == 2;
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
        Assert.assertTrue(getResultCountOfIndex(service) == 0);
        Assert.assertTrue(index.getTotalEventCount() == 0);

        Args args = Args.create("sourcetype", "mysourcetype");
        Socket socket = index.attach(args);
        OutputStream ostream = socket.getOutputStream();
        Writer out = new OutputStreamWriter(ostream, "UTF-8");
        out.write(createTimestamp() + " Hello world!\u0150\r\n");
        out.write(createTimestamp() + " Goodbye world!\u0150\r\n");
        out.write(createTimestamp() + " Goodbye world again!\u0150\r\n");

        out.flush();
        socket.close();

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            { tries = 60; }
            @Override
            public boolean predicate() {
                return getResultCountOfIndex(service) == 3;
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
    public void testUploadArgs() throws Exception {
        if (!hasTestData()) {
            System.out.println("WARNING: sdk-app-collection not installed in Splunk; skipping test.");
            return;
        }

        installApplicationFromTestData("file_to_upload");

        Assert.assertTrue(getResultCountOfIndex(service) == 0);
        Assert.assertTrue(index.getTotalEventCount() == 0);

        String fileToUpload = joinServerPath(new String[] {
                service.getSettings().getSplunkHome(),
                "etc", "apps", "file_to_upload", "log.txt"});

        Args args = new Args();
        args.add("sourcetype", "log");
        args.add("host", "IndexTest");
        args.add("rename-source", "IndexTestSrc");

        index.upload(fileToUpload, args);

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                Service con = index.getService();
                Job search = con.search("search index=" + index.getTitle() + " sourcetype=log host=IndexTest source=IndexTestSrc");
                return getResultCountOfIndex(service) == 4 && search.getEventCount() == 4;
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
    public void testUploadArgsFailure() throws Exception{
        if (!hasTestData()) {
            System.out.println("WARNING: sdk-app-collection not installed in Splunk; skipping test.");
            return;
        }
        installApplicationFromTestData("file_to_upload");

        Assert.assertTrue(getResultCountOfIndex(service) == 0);
        Assert.assertTrue(index.getTotalEventCount() == 0);

        String fileToUpload = joinServerPath(new String[] {
                service.getSettings().getSplunkHome(),
                "etc", "apps", "file_to_upload", "log.txt"});

        Args args = new Args();
        args.add("sourcetype", "log");
        args.add("host", "IndexTest");
        args.add("index", index.getTitle());
        args.add("rename-source", "IndexTestSrc");
        // The index argument cannot be passed into the upload function.
        try{
            index.upload(fileToUpload, args);
            Assert.fail("Uploading to an index with an index argument? No need for redundancy!");
        }
        catch(Exception e){
            Assert.assertEquals(e.getMessage(), "The 'index' parameter cannot be passed to an index's oneshot upload.");
        }

    }

    @Test
    public void testUpload() throws Exception {
        if (!hasTestData()) {
            System.out.println("WARNING: sdk-app-collection not installed in Splunk; skipping test.");
            return;
        }

        installApplicationFromTestData("file_to_upload");

        Assert.assertTrue(getResultCountOfIndex(service) == 0);
        Assert.assertTrue(index.getTotalEventCount() == 0);

        String fileToUpload = joinServerPath(new String[] {
                service.getSettings().getSplunkHome(),
                "etc", "apps", "file_to_upload", "log.txt"});
        index.upload(fileToUpload);

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return getResultCountOfIndex(service) == 4;
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

    // @Test
    // public void testSubmitAndClean() throws InterruptedException {
    //     try {
    //         tryTestSubmitAndClean();
    //     } catch (SplunkException e) {
    //         if (e.getCode() == SplunkException.TIMEOUT) {
    //             // Due to flakiness of the underlying implementation,
    //             // this index clean method doesn't always work on a "dirty"
    //             // Splunk instance. Try again on a "clean" instance.
    //             System.out.println(
    //                     "WARNING: Index clean timed out. Trying again on a " +
    //                     "freshly restarted Splunk instance...");
    //             uncheckedSplunkRestart();

    //             tryTestSubmitAndClean();
    //         } else {
    //             throw e;
    //         }
    //     }
    // }

    // private void tryTestSubmitAndClean() throws InterruptedException {
    //     Assert.assertTrue(getResultCountOfIndex(service) == 0);

    //     // Make sure the index is not empty.
    //     index.submit("Hello world");
    //     assertEventuallyTrue(new EventuallyTrueBehavior() {
    //         {
    //             tries = 50;
    //         }

    //         @Override
    //         public boolean predicate() {
    //             return getResultCountOfIndex(service) == 1;
    //         }
    //     });

    //     // Clean the index and make sure it's empty.
    //     // NOTE: Average time for this is 65s (!!!). Have seen 110+.
    //     index.clean(150);
    //     Assert.assertTrue(getResultCountOfIndex(service) == 0);
    // }

    @Test
    public void testUpdateNameShouldFail() {
        try {
            index.update(new Args("name", createTemporaryName()));
            Assert.fail("Expected IllegalStateException.");
        }
        catch (IllegalStateException e) {
            // Good
        }
    }

    // === Utility ===

    private int getResultCountOfIndex(Service s) {
        return getResultCountOfIndex(s, indexName);
    }
    
    private int getResultCountOfIndex(Service s, String indexName) {
        InputStream results = s.oneshotSearch("search index=" + indexName);
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

