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

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Test;

public class IndexTest extends SplunkTestCase {
    private void wait_event_count(Index index, int value, int seconds) {
        while (seconds > 0) {
            try {
                Thread.sleep(1000); // 1000ms (1 second sleep)
                seconds = seconds -1;
                if (index.getTotalEventCount() == value) {
                    return;
                }
                index.refresh();
            }
            catch (InterruptedException e) {
                return;
            }
            catch (Exception e) {
                return;
            }
        }
    }

    @Test public void testIndex() throws Exception {
        Service service = connect();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String date = sdf.format(new Date());

        EntityCollection<Index> indexes = service.getIndexes();
        for (Index index: indexes.values()) {
            index.getAssureUTF8();
            index.getBlockSignatureDatabase();
            index.getBlockSignSize();
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
        }

        if (!indexes.containsKey("sdk-tests")) {
            indexes.create("sdk-tests");
            indexes.refresh();
        }

        assertTrue(indexes.containsKey("sdk-tests"));

        Index index = indexes.get("sdk-tests");

        index.getAssureUTF8();
        index.getBlockSignatureDatabase();
        index.getBlockSignSize();
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

        index.clean();
        assertEquals(index.getTotalEventCount(), 0);

        index.disable();
        assertTrue(index.isDisabled());

        index.enable();
        assertFalse(index.isDisabled());

        // submit events to index
        index.submit(date + "Hello World. \u0150");
        index.submit(date + "Goodbye world. \u0150");
        wait_event_count(index, 2, 30);
        assertEquals(index.getTotalEventCount(), 2);

        // clean
        index.clean();
        assertEquals(index.getTotalEventCount(), 0);

        // stream events to index
        Socket socket = index.attach();
        OutputStream ostream = socket.getOutputStream();
        Writer out = new OutputStreamWriter(ostream, "UTF8");

        out.write(date + "Hello World again. \u0150\r\n");
        out.write(date + "Goodbye World again.\u0150\r\n");
        out.flush();
        socket.close();

        wait_event_count(index, 2, 30);
        assertEquals(index.getTotalEventCount(), 2);

        // clean
        index.clean();
        assertEquals(index.getTotalEventCount(), 0);

        // test must run on machine where splunkd runs,
        // otherwise an failure is expected and printed as a WARNING

        File file;
        FileReader fileReader;
        String path;
        try {
            file = new File(
                "tests" + File.separator + "com" + File.separator +
                "splunk" + File.separator + "testfile.txt");
            path = file.getAbsolutePath();
            fileReader = new FileReader(path);
        }
        catch (FileNotFoundException e) { return; }
        try {
            index.upload(path);
        }
        catch (HttpException e) {
            if (e.getStatus() == 400) {
                System.out.println("WARNING: index upload failed (is splunkd "
                + "running on this machine?)");
            }
        }
    }
}
