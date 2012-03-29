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

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

/**
 * The {@code Index} class represents an index.
 */
public class Index extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The index endpoint.
     */
    Index(Service service, String path) {
        super(service, path);
    }

    /**
     * Creates a writable socket to this index.
     *
     * @return The writable socket.
     * @throws IOException
     */
    public Socket attach() throws IOException {
        Receiver receiver = service.getReceiver();
        return receiver.attach(getName());
    }

    /**
     * Creates a writable socket to this index.
     *
     * @param args the optional arguments to the streaming endpoint.
     * @return The Socket.
     * @throws IOException
     */
    public Socket attach(Args args) throws IOException {
        Receiver receiver = service.getReceiver();
        return receiver.attach(getName(), args);
    }

    /**
     * Cleans this index, removing all events.
     *
     * @param maxSeconds the maximum number of seconds to wait before returning;
     *                   -1 means effectively wait for ever.
     * @return This index.
     */
    public Index clean(int maxSeconds) {
        Args saved = new Args();
        saved.put("maxTotalDataSizeMB", getMaxTotalDataSizeMB());
        saved.put("frozenTimePeriodInSecs", getFrozenTimePeriodInSecs());

        Args reset = new Args();
        reset.put("maxTotalDataSizeMB", "1");
        reset.put("frozenTimePeriodInSecs", "1");
        update(reset);
        rollHotBuckets();

        while (maxSeconds != 0) {
            try {
                Thread.sleep(1000); // 1000ms (1 second sleep)
                maxSeconds = maxSeconds - 1;
            }
            catch (InterruptedException e) {
                return this; // eat
            }
            if (this.getTotalEventCount() == 0) {
                update(saved);
                return this;
            }
            refresh();
        }
        throw new SplunkException(SplunkException.TIMEOUT,
                                  "Index cleaning timed out");
    }

    /**
     * Indicates whether the data retrieved from this index has been
     * UTF8-encoded.
     *
     * @return {@code true} if the retrieved data is in UTF8, {@code false} if not. 
     */
    public boolean getAssureUTF8() {
        return getBoolean("assureUTF8");
    }

    /**
     * Returns the block signature database for this index.
     *
     * @return The block signature database.
     */
    public String getBlockSignatureDatabase() {
        return getString("blockSignatureDatabase");
    }

    /**
     * Returns the block sign size for this index. This value defines the number of
     * events that make up a block for block signatures. A value of 0 means
     * block signing is disabled.
     *
     * @return The block sign size.
     */
    public int getBlockSignSize() {
        return getInteger("blockSignSize");
    }

    /**
     * Returns the absolute file path to the cold database for this index. 
     * This value may contain shell expansion terms.
     *
     * @return The colddbs's absolute file path, or {@code null} if not specified.
     */
    public String getColdPath() {
        return getString("coldPath", null);
    }

    /**
     * Returns the expanded absolute file path to the cold database for this index.
     *
     * @return The colddbs's expanded absolute file path, or {@code null} if not specified.
     */
    public String getColdPathExpanded() {
        return getString("coldPath_expanded", null);
    }

    /**
     * Returns the frozen archive destination path for this index.
     *
     * @return The frozen archive destination path, or {@code null} if not specified.
     */
    public String getColdToFrozenDir() {
        return getString("coldToFrozenDir", null);
    }

    /**
     *
     * @see <a href="http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTindex#POST_data.2Findexes" 
     * target="_blank">Attributes for the "data/indexes" endpoint in the REST API documentation</a>
     *
     * @return The archiving script, or {@code null} if not specified.
     */
    public String getColdToFrozenScript() {
        return getString("coldToFrozenScript", null);
    }

    /**
     * Indicates whether raw data is compressed.
     *
     * @deprecated Splunk always compresses raw data.
     * @return {@code true} if raw data is compressed, {@code false} if not.
     */
    public boolean getCompressRawdata() {
        return getBoolean("compressRawdata");
    }

    /**
     * Returns the current size of this index.
     *
     * @return The current size of the index, in MB.
     */
    public int getCurrentDBSizeMB() {
        return getInteger("currentDBSizeMB");
    }

    /**
     * Return the default index name of the Splunk instance.
     *
     * @return The default index name.
     */
    public String getDefaultDatabase() {
        return getString("defaultDatabase");
    }

    /**
     * Indicates whether real-time search is enabled for this index.
     *
     * @return {@code true} if real-time search is enabled, {@code false} if not.
     */
    public boolean getEnableRealtimeSearch() {
        return getBoolean("enableRealtimeSearch");
    }

    /**
     * Returns the maximum age for a bucket, after which the data in this index rolls to 
     * frozen. If archiving is necessary for frozen data, see the {@code coldToFrozen} attributes.
     *
     * @return The maximum age, in seconds, after which data rolls to frozen.
     */
    public int getFrozenTimePeriodInSecs() {
        return getInteger("frozenTimePeriodInSecs");
    }

    /**
     * Returns the absolute path to both hot and warm buckets for this index. This value may contain shell expansion terms.
     *
     * @return This index's absolute path to both hot and warm buckets, or {@code null} if not specified.
     */
    public String getHomePath() {
        return getString("homePath", null);
    }

    /**
     * Returns the expanded absolute path to both hot and warm buckets for this index.
     *
     * @return The expanded absolute path to both hot and warm buckets, or {@code null} if not specified.
     */
    public String getHomePathExpanded() {
        return getString("homePath_expanded", null);
    }

    /**
     * Returns the index thread for this index.
     *
     * @return The index thread.
     */
    public String getIndexThreads() {
        return getString("indexThreads");
    }

    /**
     * Returns the last initialization time for this index.
     *
     * @return The last initialization time, or {@code null} if not specified.
     */
    public String getLastInitTime() {
        return getString("lastInitTime", null);
    }

    /**
     * Returns the maximum number of concurrent optimize processes that
     * can run against a hot bucket for this index.
     *
     * @return The maximum number of concurrent optimize processes.
     */
    public int getMaxConcurrentOptimizes() {
        return getInteger("maxConcurrentOptimizes");
    }

    /**
     * Returns the maximum data size before triggering a roll from hot to warm buckets for this index.
     *
     * @return The maximum data size, in MB, or "auto" (which means 750MB), or
     * "auto_high_volume" (which means 10GB on a 64-bit system, or 1GB on a 32-bit system).
     */
    public String getMaxDataSize() {
        return getString("maxDataSize");
    }

    /**
     * Returns the maximum number of hot buckets that can exist for this index.
     *
     * @return The maximum number of hot buckets.
     */
    public int getMaxHotBuckets() {
        return getInteger("maxHotBuckets");
    }

    /**
     * Returns the maximum lifetime of a hot bucket for this index. 
     * If a hot bucket exceeds this value, Splunk rolls it to warm. 
     * A value of 0 means an infinite lifetime.
     *
     * @return The hot bucket's maximum lifetime, in seconds.
     */
    public int getMaxHotIdleSecs() {
        return getInteger("maxHotIdleSecs");
    }

    /**
     * Returns the upper bound of the target maximum timespan of 
     * hot and warm buckets for this index.
     *
     * @return The upper bound of the target maximum timespan, in seconds.
     */
    public int getMaxHotSpanSecs() {
        return getInteger("maxHotSpanSecs");
    }

    /**
     * Returns the amount of memory to allocate for buffering 
     * a single tsidx file into memory before flushing to disk. 
     *
     * @return The amount of memory, in MB.
     */
    public int getMaxMemMB() {
        return getInteger("maxMemMB");
    }

    /**
     * Returns the maximum number of unique lines that are allowed 
     * in a bucket's .data files for this index. A value of 0 means infinite lines.
     *
     * @return The maximum number of unique lines.
     */
    public int getMaxMetaEntries() {
        return getInteger("maxMetaEntries");
    }

    /**
     * Returns the maximum number of concurrent helper processes for this index.
     *
     * @return The maximum number of concurrent helper processes.
     */
    public int getMaxRunningProcessGroups() {
        return getInteger("maxRunningProcessGroups");
    }

    /**
     * Returns the maximum time attribute for this index.
     *
     * @return The maximum time attribute, or {@code null} if not specified.
     */
    public Date getMaxTime() {
        return getDate("maxTime", null);
    }

    /**
     * Returns the maximum size for this index. If an index
     * grows larger than this value, the oldest data is frozen.
     *
     * @return The maximum index size, in MB.
     */
    public int getMaxTotalDataSizeMB() {
        return getInteger("maxTotalDataSizeMB");
    }

    /**
     * Returns the maximum number of warm buckets for this index. If this 
     * value is exceeded, the warm buckets with the lowest value for their 
     * latest times are moved to cold.
     *
     * @return The maximum number of warm buckets.
     */
    public int getMaxWarmDBCount() {
        return getInteger("maxWarmDBCount");
    }

    /**
     * Returns the memory pool for this index.
     *
     * @return The memory pool, in MB or "auto".
     */
    public String getMemPoolMB() {
        return getString("memPoolMB");
    }

    /**
     * Returns the frequency at which Splunkd forces a filesystem sync while 
     * compressing journal slices for this index.
     * A value of "disable" disables this feature completely, while a value of 0
     * forces a file-system sync after completing compression of every journal slice.
     *
     * @return The file-system sync frequency, as an integer or "disable".
     */
    public String getMinRawFileSyncSecs() {
        return getString("minRawFileSyncSecs");
    }

    /**
     * Returns the minimum time attribute for this index.
     *
     * @return The minimum time attribute, or {@code null} if not specified.
     */
    public Date getMinTime() {
        return getDate("minTime", null);
    }

    /**
     * Returns the frequency at which metadata is for partially synced (synced in-place)
     * for this index. A value of 0 disables partial syncing, so metadata is 
     * only synced on the ServiceMetaPeriod interval. 
     * @see #getServiceMetaPeriod getServiceMetaPeriod
     *
     * @return The metadata sync interval, in seconds.
     */
    public int getPartialServiceMetaPeriod() {
        return getInteger("partialServiceMetaPeriod");
    }

    /**
     * Returns the future event-time quarantine for this index. Events
     * that are newer than now plus this value are quarantined.
     *
     * @return The future event-time quarantine, in seconds.
     */
    public int getQuarantineFutureSecs() {
        return getInteger("quarantineFutureSecs");
    }

    /**
     * Returns the past event-time quarantine for this index. Events
     * that are older than now minus this value are quarantined.
     *
     * @return The past event-time quarantine, in seconds.
     */
    public int getQuarantinePastSecs() {
        return getInteger("quarantinePastSecs");
    }

    /**
     * Returns the target uncompressed size of individual raw slices in the
     * rawdata journal for this index.
     *
     * @return The target uncompressed size, in bytes.
     */
    public int getRawChunkSizeBytes() {
        return getInteger("rawChunkSizeBytes");
    }

    /**
     * Returns the frequency to check for the need to create a new hot bucket and
     * the need to roll or freeze any warm or cold buckets for this index.
     *
     * @return The check frequency, in seconds.
     */
    public int getRotatePeriodInSecs() {
        return getInteger("rotatePeriodInSecs");
    }

    /**
     * Returns the frequency at which metadata is synced to disk for this index.
     *
     * @return The meta data sync frequency, in seconds.
     */
    public int getServiceMetaPeriod() {
        return getInteger("serviceMetaPeriod");
    }

    /**
     * Returns a list of indexes that suppress "index missing" messages.
     *
     * @return A comma-separated list of indexes.
     */
    public String getSuppressBannerList() {
        return getString("suppressBannerList", null);
    }

    /**
     * Returns the sync attribute for this index.
     *
     * @return The sync attribute.
     */
    public boolean getSync() {
        return getBoolean("sync");
    }

    /**
     * Indicates whether the sync operation is invoked before the file descriptor is 
     * closed on metadata updates. 
     *
     * @return {@code true} if the sync operation is invoked before the file descriptor 
     * is closed on metadata updates, {@code false} if not.
     */
    public boolean getSyncMeta() {
        return getBoolean("syncMeta");
    }

    /**
     * Returns the absolute path to the thawed index for this index. This value may 
     * contain shell expansion terms.
     *
     * @return The absolute path to the thawed index, or {@code null} if not specified.
     */
    public String getThawedPath() {
        return getString("thawedPath", null);
    }

    /**
     * Returns the expanded absolute path to the thawed index for this index.
     *
     * @return The expanded absolute path to the thawed index, or {@code null} if not specified.
     */
    public String getThawedPathExpanded() {
        return getString("thawedPath_expanded", null);
    }

    /**
     * Returns the frequency at which Splunk checks for an index throttling condition.
     *
     * @return The frequency of the throttling check, in seconds.
     */
    public int getThrottleCheckPeriod() {
        return getInteger("throttleCheckPeriod");
    }

    /**
     * Returns the total event count for this index.
     *
     * @return The total event count.
     */
    public int getTotalEventCount() {
        return getInteger("totalEventCount");
    }

    /**
     * Indicates whether this index is an internal index.
     *
     * @return {@code true} if this index is an internal index, {@code false} if not.
     */
    public boolean isInternal() {
        return getBoolean("isInternal");
    }

    /**
     * Performs rolling hot buckets for this index.
     */
    public void rollHotBuckets() {
        ResponseMessage response = service.post(path + "/roll-hot-buckets");
        assert(response.getStatus() == 200);
    }

    /**
     * Submits an event to this index through an HTTP POST request.
     *
     * @param data The event data that was posted.
     */
    public void submit(String data) {
        Receiver receiver = service.getReceiver();
        receiver.submit(getName(), data);
    }

    /**
     * Submits an event to this index through HTTP POST.
     *
     * @param data Event data posted.
     * @param args optional arguments for the simple receivers endpoint.
     */
    public void submit(String data, Args args) {
        Receiver receiver = service.getReceiver();
        receiver.submit(getName(), data, args);
    }

    /**
     * Uploads a file to this index as an event stream. 
     * <br/>Note: This file must be directly accessible by the Splunk server.
     *
     * @param filename The uploaded file.
     */
    public void upload(String filename) {
        EntityCollection<Upload> uploads = service.getUploads();
        Args args = new Args("index", getName());
        uploads.create(filename, args);
    }
}

