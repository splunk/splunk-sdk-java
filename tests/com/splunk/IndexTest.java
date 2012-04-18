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

        // get old values, skip saving paths and things we cannot write
        Args restore = new Args();
        index.getAssureUTF8();
        index.getBlockSignatureDatabase();
        restore.put("blockSignSize", index.getBlockSignSize());
        index.getCurrentDBSizeMB();
        index.getDefaultDatabase();
        index.getEnableRealtimeSearch();
        restore.put("frozenTimePeriodInSecs",index.getFrozenTimePeriodInSecs());
        index.getIndexThreads();
        index.getLastInitTime();
        restore.put("maxConcurrentOptimizes", index.getMaxConcurrentOptimizes());
        restore.put("maxDataSize", index.getMaxDataSize());
        restore.put("maxHotBuckets", index.getMaxHotBuckets());
        restore.put("maxHotIdleSecs", index.getMaxHotIdleSecs());
        restore.put("maxHotSpanSecs", index.getMaxHotSpanSecs());
        restore.put("maxMemMB", index.getMaxMemMB());
        restore.put("maxMetaEntries", index.getMaxMetaEntries());
        index.getMaxRunningProcessGroups();
        index.getMaxTime();
        restore.put("maxTotalDataSizeMB", index.getMaxTotalDataSizeMB());
        restore.put("maxWarmDBCount", index.getMaxWarmDBCount());
        index.getMemPoolMB();
        restore.put("minRawFileSyncSecs", index.getMinRawFileSyncSecs());
        index.getMinTime();
        restore.put("partialServiceMetaPeriod", index.getPartialServiceMetaPeriod());
        restore.put("quarantineFutureSecs", index.getQuarantineFutureSecs());
        restore.put("quarantinePastSecs", index.getQuarantinePastSecs());
        restore.put("rawChunkSizeBytes", index.getRawChunkSizeBytes());
        restore.put("rotatePeriodInSecs", index.getRotatePeriodInSecs());
        restore.put("serviceMetaPeriod", index.getServiceMetaPeriod());
        index.getSync();
        restore.put("syncMeta", index.getSyncMeta());
        restore.put("throttleCheckPeriod", index.getThrottleCheckPeriod());
        index.getTotalEventCount();
        index.isDisabled();
        index.isInternal();

        // use setters to update most
        index.setBlockSignSize(index.getBlockSignSize()+1);
        index.setEnableOnlineBucketRepair(!index.getEnableRealtimeSearch());
        index.setFrozenTimePeriodInSecs(index.getFrozenTimePeriodInSecs()+1);
        index.setMaxBloomBackfillBucketAge("20d");
        index.setMaxConcurrentOptimizes(index.getMaxConcurrentOptimizes()+1);
        index.setMaxDataSize("auto");
        index.setMaxHotBuckets(index.getMaxHotBuckets()+1);
        index.setMaxHotIdleSecs(index.getMaxHotIdleSecs()+1);
        index.setMaxMemMB(index.getMaxMemMB()+1);
        index.setMaxMetaEntries(index.getMaxMetaEntries()+1);
        index.setMaxTotalDataSizeMB(index.getMaxTotalDataSizeMB()+1);
        index.setMaxWarmDBCount(index.getMaxWarmDBCount()+1);
        index.setMinRawFileSyncSecs("disable");
        index.setPartialServiceMetaPeriod(index.getPartialServiceMetaPeriod()+1);
        index.setQuarantineFutureSecs(index.getQuarantineFutureSecs()+1);
        index.setQuarantinePastSecs(index.getQuarantinePastSecs()+1);
        index.setRawChunkSizeBytes(index.getRawChunkSizeBytes()+1);
        index.setRotatePeriodInSecs(index.getRotatePeriodInSecs()+1);
        index.setServiceMetaPeriod(index.getServiceMetaPeriod()+1);
        index.setSyncMeta(!index.getSyncMeta());
        index.setThrottleCheckPeriod(index.getThrottleCheckPeriod()+1);
        index.update();

        // check, then restore using map method
        index.update(restore);
        index.refresh();

        index.clean(60);
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
        index.clean(60);
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
        index.clean(60);
        assertEquals(index.getTotalEventCount(), 0);

        ServiceInfo info = service.getInfo();
        String filename;
        if (info.getOsName().equals("Windows"))
            filename = "C:\\Windows\\WindowsUpdate.log"; // normally here
        else if (info.getOsName().equals("Linux"))
            filename = "/var/log/syslog";
        else if (info.getOsName().equals("Darwin")) {
            filename = "/var/log/system.log";
        } else {
            throw new Error("OS: " + info.getOsName() + " not supported");
        }

        try {
            index.upload(filename);
        }
        catch (Exception e) {
            throw new Error("File " + filename + "failed to upload");
        }
    }
}
