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
import java.io.OutputStream;
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
     * Writes events to this index, reusing the connection. 
     * This method passes an output stream connected to the index to the 
     * {@code run} method of the {@code ReceiverBehavior} object, then handles 
     * setting up and tearing down the socket.
     * <p>
     * For an example of how to use this method, see 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ2" target="_blank">How to 
     * get data into Splunk</a> on 
     * <a href="http://dev.splunk.com/view/SP-CAAAEJ2" 
     * target="_blank">dev.splunk.com</a>.
     * 
     * @param behavior The body of a {@code try} block as an anonymous 
     * implementation of the {@code ReceiverBehavior} interface.
     */
    public void attachWith(ReceiverBehavior behavior) throws IOException {
        Socket socket = null;
        OutputStream output = null;
        try {
            socket = attach();
            output = socket.getOutputStream();
            behavior.run(output);
            output.flush();
        } finally {
            if (output != null) { output.close(); }
            if (socket != null) { socket.close(); }
        }
    }

    /**
     * Creates a writable socket to this index.
     *
     * @param args Optional arguments for this stream. Valid parameters are: 
     * "host", "host_regex", "source", and "sourcetype".
     * @return The socket.
     * @throws IOException
     */
    public Socket attach(Args args) throws IOException {
        Receiver receiver = service.getReceiver();
        return receiver.attach(getName(), args);
    }

    /**
     * Cleans this index, which removes all events from it.
     *
     * @param maxSeconds The maximum number of seconds to wait before returning.
     * A value of -1 means to wait forever.
     * @throws SplunkException If cleaning timed out or
     * if the thread was interrupted.
     * @return This index.
     */
    public Index clean(int maxSeconds) {
        Args saved = new Args();
        saved.put("maxTotalDataSizeMB", getMaxTotalDataSizeMB());
        saved.put("frozenTimePeriodInSecs", getFrozenTimePeriodInSecs());
        try {
            Args reset = new Args();
            reset.put("maxTotalDataSizeMB", "1");
            reset.put("frozenTimePeriodInSecs", "1");
            update(reset);
            rollHotBuckets();
            
            long startTime = System.currentTimeMillis();
            long endTime = startTime + (maxSeconds * 1000);
            while (true) {
                long timeLeft = endTime - System.currentTimeMillis();
                if (timeLeft <= 0) {
                    break;
                }
                Thread.sleep(Math.min(1000, timeLeft));
                
                if (this.getTotalEventCount() == 0) {
                    return this;
                }
                refresh();
            }
            
            throw new SplunkException(SplunkException.TIMEOUT,
                                      "Index cleaning timed out");
        }
        catch (InterruptedException e)
        {
            SplunkException f = new SplunkException(
                    SplunkException.INTERRUPTED,
                    "Index cleaning interrupted.");
            f.initCause(e);
            throw f;
        }
        finally {
            update(saved);
        }
    }

    /**
     * Indicates whether the data retrieved from this index has been
     * UTF8-encoded.
     *
     * @return {@code true} if the retrieved data is in UTF8, {@code false} if
     * not.
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
     * Returns the number of events that make up a block for block signatures. 
     *
     * @return The block sign size. A value of 0 means block signing has been 
     * disabled.
     */
    public int getBlockSignSize() {
        return getInteger("blockSignSize");
    }

    /**
     * Returns the total size of all bloom filter files.
     *
     * @return The total size of all bloom filter files, in KB.
     */
    public int getBloomfilterTotalSizeKB() {
        return getInteger("bloomfilterTotalSizeKB", 0);
    }

    /**
     * Returns the suggested size of the .tsidx file for the bucket rebuild 
     * process. 
     * Valid values are: "auto", a positive integer, or a positive 
     * integer followed by "KB", "MB", or "GB". 
     *
     * @return The suggested size of the .tsidx file for the bucket rebuild 
     * process.
     */
    public String getBucketRebuildMemoryHint() {
        return getString("bucketRebuildMemoryHint");
    }

    /**
     * Returns the absolute file path to the cold database for this index.
     * This value may contain shell expansion terms.
     *
     * @return The absolute file path to the cold database, or {@code null} if
     * not specified.
     */
    public String getColdPath() {
        return getString("coldPath", null);
    }

    /**
     * Returns the expanded absolute file path to the cold database for this
     * index.
     *
     * @return The expanded absolute file path to the cold database, or 
     * {@code null} if not specified.
     */
    public String getColdPathExpanded() {
        return getString("coldPath_expanded", null);
    }

    /**
     * Returns the frozen archive destination path for this index.
     *
     * @return The frozen archive destination path, or {@code null} if not
     * specified.
     */
    public String getColdToFrozenDir() {
        return getString("coldToFrozenDir", null);
    }

    /**
     * Returns the path to the archiving script.
     * <p>For more info about archiving scripts, see the
     * <a href="http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTindex#POST_data.2Findexes"
     * target="_blank">POST data/indexes endpoint</a> in the REST API
     * documentation.
     * @see #getColdToFrozenDir
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
     * Returns whether asynchronous "online fsck" bucket repair is enabled.
     * <p>
     * When this feature is enabled, you don't have to wait for buckets to be
     * repaired before starting Splunk, but you might notice a slight
     * degradation in performance as a result.
     * @return {@code true} if bucket repair is enabled, {@code false} if
     * not.
     */
    public boolean getEnableOnlineBucketRepair() {

        return getBoolean("enableOnlineBucketRepair");
    }

    /**
     * Indicates whether real-time search is enabled for this index.
     *
     * @return {@code true} if real-time search is enabled, {@code false} if
     * not.
     */
    public boolean getEnableRealtimeSearch() {
        return getBoolean("enableRealtimeSearch");
    }

    /**
     * Returns the maximum age for a bucket, after which the data in this index
     * rolls to frozen. If archiving is necessary for frozen data, see the
     * {@code coldToFrozen} attributes.
     *
     * @return The maximum age, in seconds, after which data rolls to frozen.
     */
    public int getFrozenTimePeriodInSecs() {
        return getInteger("frozenTimePeriodInSecs");
    }

    /**
     * Returns the absolute path to both hot and warm buckets for this index.
     * This value may contain shell expansion terms.
     *
     * @return This index's absolute path to both hot and warm buckets, or
     * {@code null} if not specified.
     */
    public String getHomePath() {
        return getString("homePath", null);
    }

    /**
     * Returns the expanded absolute path to both hot and warm buckets for this
     * index.
     *
     * @return The expanded absolute path to both hot and warm buckets, or
     * {@code null} if not specified.
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
     * Returns the time that indicates a bucket age. When a warm or cold bucket 
     * is older than this, Splunk does not create or rebuild its bloomfilter.  
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d"). For example, "30d" for 30 days.
     */
    public String getMaxBloomBackfillBucketAge() {
        return getString("maxBloomBackfillBucketAge", null);
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
     * Returns the maximum data size before triggering a roll from hot to warm
     * buckets for this index.
     *
     * @return The maximum data size, in MB, or "auto" (which means 750MB), or
     * "auto_high_volume" (which means 10GB on a 64-bit system, or 1GB on a
     * 32-bit system).
     * @see #setMaxDataSize
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
     * a single .tsidx file into memory before flushing to disk.
     *
     * @return The amount of memory, in MB.
     */
    public int getMaxMemMB() {
        return getInteger("maxMemMB");
    }

    /**
     * Returns the maximum number of unique lines that are allowed
     * in a bucket's .data files for this index. A value of 0 means infinite
     * lines.
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
        return getInteger("maxRunningProcessGroups", 0);
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
     * Returns the maximum size of this index. If an index
     * grows larger than this value, the oldest data is frozen.
     *
     * @return The maximum index size, in MB.
     */
    public int getMaxTotalDataSizeMB() {
        return getInteger("maxTotalDataSizeMB");
    }

    /**
     * Returns the upper limit, in seconds, for how long an event can sit in a
     * raw slice. This value applies only when replication is enabled for this 
     * index, and is ignored otherwise.<br>
     * If there are any acknowledged events sharing this raw slice, the 
     * {@code MaxTimeUnreplicatedWithAcksparamater} applies instead.
     * @see #getMaxTimeUnreplicatedWithAcks
     */
    public int getMaxTimeUnreplicatedNoAcks() {
        return getInteger("maxTimeUnreplicatedNoAcks");
    }

    /**
     * Returns the upper limit, in seconds, for how long an event can sit 
     * unacknowledged in a raw slice. This value only applies when indexer 
     * acknowledgement is enabled on forwarders and replication is enabled with 
     * clustering.
     */
    public int getMaxTimeUnreplicatedWithAcks() {
        return getInteger("maxTimeUnreplicatedWithAcks");
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
     * <p>
     * A value of "disable" disables this feature completely, while a value of 0
     * forces a file-system sync after completing compression of every journal
     * slice.
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
     * Returns the number of hot buckets that were created for this index.
     *
     * @return The number of hot buckets.
     */
    public int getNumHotBuckets() {
        return getInteger("numHotBuckets", 0);
    }

    /**
     * Returns the number of warm buckets created for this index.
     *
     * @return The number of warm buckets.
     */
    public int getNumWarmBuckets() {
        return getInteger("numWarmBuckets", 0);
    }

    /**
     * Returns the number of bloom filters created for this index.
     *
     * @return The number of bloom filters.
     */
    public int getNumBloomfilters() {
        return getInteger("numBloomfilters", 0);
    }

    /**
     * Returns the frequency at which metadata is for partially synced (synced
     * in-place) for this index. A value of 0 disables partial syncing, so
     * metadata is only synced on the {@code ServiceMetaPeriod} interval.
     * @see #getServiceMetaPeriod
     * @see #setServiceMetaPeriod
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
     * Returns the frequency to check for the need to create a new hot bucket
     * and the need to roll or freeze any warm or cold buckets for this index.
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
     * Returns the number of events that trigger the indexer to sync events. 
     * This value is global, not a per-index value.
     *
     * @return The number of events that trigger the indexer to sync events.
     */
    public int getSync() {
        return getInteger("sync");
    }

    /**
     * Indicates whether the sync operation is called before the file
     * descriptor is closed on metadata updates.
     *
     * @return {@code true} if the sync operation is called before the file
     * descriptor is closed on metadata updates, {@code false} if not.
     */
    public boolean getSyncMeta() {
        return getBoolean("syncMeta");
    }

    /**
     * Returns the absolute path to the thawed index for this index. This value
     * may contain shell expansion terms.
     *
     * @return The absolute path to the thawed index, or {@code null} if not
     * specified.
     */
    public String getThawedPath() {
        return getString("thawedPath", null);
    }

    /**
     * Returns the expanded absolute path to the thawed index for this index.
     *
     * @return The expanded absolute path to the thawed index, or {@code null}
     * if not specified.
     */
    public String getThawedPathExpanded() {
        return getString("thawedPath_expanded", null);
    }

    /**
     * Returns the frequency at which Splunk checks for an index throttling
     * condition.
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
     * @return {@code true} if this index is an internal index, {@code false}
     * if not.
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
     * Sets whether the data retrieved from this index is UTF8-encoded.
     * <p>
     * <b>Note:</b> Indexing performance degrades when this parameter is set to
     * {@code true}.
     *
     * In Splunk 5.0 and later, this is a global property and cannot be set on
     * a per-index basis.
     *
     * @param assure {@code true} to ensure UTF8 encoding, {@code false} if not.
     */
    public void setAssureUTF8(boolean assure) {
        setCacheValue("assureUTF8", assure);
    }

    /**
     * Sets the number of events that make up a block for block signatures. A 
     * value of 100 is recommended. A value of 0 disables block signing for this
     * index.
     *
     * @param value The event count for block signing. 
     */
    public void setBlockSignSize(int value) {
        setCacheValue("blockSignSize", value);
    }


    /**
     * Sets the suggested size of the .tsidx file for the bucket rebuild 
     * process.
     *
     * Valid values are: "auto", a positive integer, or a positive 
     * integer followed by "KB", "MB", or "GB". 
     *
     * @param value The suggested size of the .tsidx file for the bucket rebuild 
     * process.
     */
    public void setBucketRebuildMemoryHint(String value) {
        setCacheValue("bucketRebuildMemoryHint", value);
    }

    /**
     * Sets the destination path for the frozen archive, where Splunk
     * automatically puts frozen buckets. The bucket freezing policy is as
     *follows:
     * <p>
     * <ul><li><b>New-style buckets (4.2 and later):</b> All files are removed
     * except the raw data. To thaw frozen buckets, run {@code Splunk rebuild
     * <bucket dir>} on the bucket, then move the buckets to the thawed
     * directory.</li>
     * <li><b>Old-style buckets (4.1 and earlier):</b> gzip all the .data and
     * .tsidx files. To thaw frozen buckets, gunzip the zipped files and move
     * the buckets to  the thawed directory.</li></ul>
     * If both {@code coldToFrozenDir} and {@code coldToFrozenScript} are
     * specified, {@code coldToFrozenDir} takes precedence.
     * @see #setColdToFrozenScript
     * @see #getColdToFrozenScript
     *
     * @param destination The destination path for the frozen archive.
     */
    public void setColdToFrozenDir(String destination) {
        setCacheValue("coldToFrozenDir", destination);
    }

    /**
     * Sets the path to the archiving script.
     * <p>For more info about archiving scripts, see the
     * <a href="http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTindex#POST_data.2Findexes"
     * target="_blank">POST data/indexes endpoint</a> in the REST API
     * documentation.
     * @see #setColdToFrozenDir
     * @see #getColdToFrozenDir
     *
     * @param script The path to the archiving script.
     */
    public void setColdToFrozenScript(String script) {
        setCacheValue("coldToFrozenScript", script);
    }

    /**
     * Sets whether asynchronous "online fsck" bucket repair is enabled.
     * <p>
     * When this feature is enabled, you don't have to wait for buckets to be
     * repaired before starting Splunk, but you might notice a slight
     * degradation in performance as a result.
     *
     * @param value {@code true} to enable online bucket repair, {@code false}
     * if not.
     */
    public void setEnableOnlineBucketRepair(boolean value) {
        setCacheValue("enableOnlineBucketRepair", value);
    }

    /**
     * Sets the maximum age for a bucket, after which the data in this index
     * rolls to frozen. Freezing data removes it from the index. To archive
     * data, see {@code coldToFrozenDir} and {@code coldToFrozenScript}.
     * @see #setColdToFrozenDir
     * @see #setColdToFrozenScript
     *
     * @param seconds The time, in seconds, after which indexed data rolls to
     * frozen.
     */
    public void setFrozenTimePeriodInSecs(int seconds) {
        setCacheValue("frozenTimePeriodInSecs", seconds);
    }

    /**
     * Sets the time that indicates a bucket age. When a warm or cold bucket 
     * is older than this, Splunk does not create or rebuild its bloomfilter.  
     * The valid format is <i>number</i> followed by a time unit ("s", "m", "h",
     * or "d"). For example, "30d" for 30 days.
     * @param time The time that indicates a bucket age.
     */
    public void setMaxBloomBackfillBucketAge(String time) {
        setCacheValue("maxBloomBackfillBucketAge", time);
    }

    /**
     * Sets the number of concurrent optimize processes that can run against
     * a hot bucket for this index.
     *
     * @param processes The number of concurrent optimize processes.
     */
    public void setMaxConcurrentOptimizes(int processes) {
        setCacheValue("maxConcurrentOptimizes", processes);
    }

    /**
     * Sets the maximum data size before triggering a roll from hot to warm
     * buckets for this index. You can also specify a value to let Splunk
     * autotune this parameter: use "auto_high_volume" for high-volume indexes
     * (such as the main index, or one that gets over 10GB of data per day);
     * otherwise, use "auto".
     * @see #getMaxDataSize
     *
     * @param size The size in MB, or an autotune string.
     */
    public void setMaxDataSize(String size) {
        setCacheValue("maxDataSize", size);
    }

    /**
     * Sets the maximum number of hot buckets that can exist per index.
     * <p>
     * When {@code maxHotBuckets} is exceeded, Splunk rolls the least recently
     * used (LRU) hot bucket to warm. Both normal hot buckets and quarantined
     * hot buckets count towards this total. This setting operates independently
     * of {@code MaxHotIdleSecs}, which can also cause hot buckets to roll.
     * @see #setMaxHotIdleSecs
     * @see #getMaxHotIdleSecs
     *
     * @param size The maximum number of hot buckets per index.
     */
    public void setMaxHotBuckets(int size) {
        setCacheValue("maxHotBuckets", size);
    }

    /**
     * Sets the maximum lifetime of a hot bucket for this index.
     * <p>
     * If a hot bucket exceeds this value, Splunk rolls it to warm.
     * This setting operates independently of {@code MaxHotBuckets}, which can
     * also cause hot buckets to roll.
     * @see #setMaxHotBuckets
     * @see #getMaxHotBuckets
     *
     * @param seconds The hot bucket's maximum lifetime, in seconds. A value of
     * 0 means an infinite lifetime.
     */
    public void setMaxHotIdleSecs(int seconds) {
        setCacheValue("maxHotIdleSecs", seconds);
    }

    /**
     * Sets the upper bound of the target maximum timespan of hot and warm
     * buckets for this index.
     * <p>
     * <b>Note:</b> If you set this too small, you can get an explosion of
     * hot and warm buckets in the file system. The system sets a lower bound
     * implicitly for this parameter at 3600, but this advanced parameter should
     * be set with care and understanding of the characteristics of your data.
     *
     * @param seconds The upper bound of the target maximum timespan, in
     * seconds.
     */
    public void setMaxHotSpanSecs(int seconds) {
        setCacheValue("maxHotSpanSecs", seconds);
    }

    /**
     * Sets the amount of memory allocated for buffering a single .tsidx
     * file before flushing to disk.
     *
     * @param memory The amount of memory, in MB.
     */
    public void setMaxMemMB(int memory) {
        setCacheValue("maxMemMB", memory);
    }

    /**
     * Sets the maximum number of unique lines in .data files in a bucket, which
     * may help to reduce memory consumption.
     * <p>
     * If this value is exceeded, a hot bucket is rolled to prevent a further
     * increase. If your buckets are rolling due to Strings.data hitting this
     * limit, the culprit might be the "punct" field in your data. If you don't
     * use that field, it might be better to just disable this (see the
     * props.conf.spec in $SPLUNK_HOME/etc/system/README).
     *
     * @param entries The maximum number of unique lines. A value of 0 means
     * infinite lines.
     */
    public void setMaxMetaEntries(int entries) {
        setCacheValue("maxMetaEntries", entries);
    }


    /**
     * Sets the upper limit for how long an event can sit in a
     * raw slice. This value applies only when replication is enabled for this 
     * index, and is ignored otherwise.<br>
     * If there are any acknowledged events sharing this raw slice, the 
     * {@code MaxTimeUnreplicatedWithAcksparamater} applies instead.
     * 
     * @param value The upper limit, in seconds. A value of 0 disables this 
     * setting.
     */
    public void setMaxTimeUnreplicatedNoAcks(int value) {
        setCacheValue("maxTimeUnreplicatedNoAcks", value);
    }

    /**
     * Sets the upper limit for how long an event can sit unacknowledged in a 
     * raw slice. This value only applies when indexer acknowledgement is 
     * enabled on forwarders and replication is enabled with clustering.
     * <p>
     * This number should not exceed the acknowledgement timeout configured on 
     * any forwarder. 
     * 
     * @param value The upper limit, in seconds. A value of 0 disables this 
     * setting (not recommended).
     */
    public void setMaxTimeUnreplicatedWithAcks(int value) {
        setCacheValue("maxTimeUnreplicatedWithAcks", value);
    }

    /**
     * Sets the maximum size for this index. If an index grows larger than this
     * value, the oldest data is frozen.
     *
     * @param size The maximum index size, in MB.
     */
    public void setMaxTotalDataSizeMB(int size) {
        setCacheValue("maxTotalDataSizeMB", size);
    }

    /**
     * Sets the maximum number of warm buckets. If this number is exceeded,
     * the warm buckets with the lowest value for their latest times will be
     * moved to cold.
     *
     * @param buckets The maximum number of warm buckets.
     */
    public void setMaxWarmDBCount(int buckets) {
        setCacheValue("maxWarmDBCount", buckets);
    }

    /**
     * Sets the frequency at which Splunkd forces a file system sync while
     * compressing journal slices for this index. A value of "disable" disables
     * this feature completely, while a value of 0 forces a file-system sync
     * after completing compression of every journal slice.
     *
     * @param frequency The file-system sync frequency, as an integer or
     * "disable".
     */
    public void setMinRawFileSyncSecs(String frequency) {
        setCacheValue("minRawFileSyncSecs", frequency);
    }

    /**
     * Sets the frequency at which metadata is for partially synced (synced
     * in-place) for this index. A value of 0 disables partial syncing, so
     * metadata is only synced on the {@code ServiceMetaPeriod} interval.
     * @see #setServiceMetaPeriod
     * @see #getServiceMetaPeriod
     *
     * @param frequency The metadata sync interval, in seconds.
     */
    public void setPartialServiceMetaPeriod(int frequency) {
        setCacheValue("partialServiceMetaPeriod", frequency);
    }

    /**
     * Sets a quarantine for events that are timestamped in the future to help
     * prevent main hot buckets from being polluted with fringe events. Events
     * that are newer than "now" plus this value are quarantined.
     *
     * @param window The future event-time quarantine, in seconds.
     */
    public void setQuarantineFutureSecs(int window) {
        setCacheValue("quarantineFutureSecs", window);
    }

    /**
     * Sets a quarantine for events that are timestamped in the past to help
     * prevent main hot buckets from being polluted with fringe events. Events
     * that are older than "now" plus this value are quarantined.
     *
     * @param window The past event-time quarantine, in seconds.
     */
    public void setQuarantinePastSecs(int window) {
        setCacheValue("quarantinePastSecs", window);
    }

    /**
     * Sets the target uncompressed size of individual raw slices in the rawdata
     * journal for this index.
     * <p>
     * This parameter only specifies a target chunk size. The actual chunk size
     * might be slightly larger by an amount proportional to an individual event
     * size.
     * <blockquote>
     * <b>WARNING:</b> This is an advanced parameter. Only change it if you are
     * instructed to do so by Splunk Support.
     * </blockquote>
     * @param size The target uncompressed size, in bytes. (0 is not a valid
     * value--if 0 is used, this parameter is set to the default value.)
     */
    public void setRawChunkSizeBytes(int size) {
        setCacheValue("rawChunkSizeBytes", size);
    }

    /**
     * Sets the frequency to check for the need to create a new hot bucket and
     * the need to roll or freeze any warm or cold buckets for this index.
     *
     * @param frequency The check frequency, in seconds.
     */
    public void setRotatePeriodInSecs(int frequency) {
        setCacheValue("rotatePeriodInSecs", frequency);
    }

    /**
     * Sets the frequency at which metadata is synced to disk for this index.
     *
     * @param frequency The meta data sync frequency, in seconds.
     */
    public void setServiceMetaPeriod(int frequency) {
        setCacheValue("serviceMetaPeriod", frequency);
    }

    /**
     * Sets whether the sync operation is called before the file descriptor is
     * closed on metadata updates.
     * <p>
     * This functionality improves the integrity of metadata files, especially
     * with regard to operating system crashes and machine failures.
     * <blockquote>
     * <b>WARNING:</b> This is an advanced parameter. Only change it if you are
     * instructed to do so by Splunk Support.
     * </blockquote>
     * @param sync {@code true} to call the sync operation before the file
     * descriptor is closed on metadata updates, {@code false} if not.
     */
    public void setSyncMeta(boolean sync) {
        setCacheValue("syncMeta", sync);
    }

    /**
     * Sets the frequency at which Splunk checks for an index throttling
     * condition.
     *
     * @param frequency The frequency of the throttling check, in seconds.
     */
    public void setThrottleCheckPeriod(int frequency) {
        setCacheValue("throttleCheckPeriod", frequency);
    }

    /**
     * Submits an event to this index through an HTTP POST request.
     *
     * @param data The event data to post.
     */
    public void submit(String data) {
        Receiver receiver = service.getReceiver();
        receiver.submit(getName(), data);
    }

    /**
     * Submits an event to this index through an HTTP POST request.
     *
     * @param args Optional arguments for this request. Valid parameters are: 
     * "host", "host_regex", "source", and "sourcetype".
     * @param data The event data to post.
     */
    public void submit(Args args, String data) {
        Receiver receiver = service.getReceiver();
        receiver.submit(getName(), args, data);
    }

    /**
     * Uploads a file to this index as an event stream.
     * <p>
     * <b>Note:</b> This file must be directly accessible by the Splunk server.
     *
     * @param filename The path and filename.
     */
    public void upload(String filename) {
        EntityCollection<Upload> uploads = service.getUploads();
        Args args = new Args("index", getName());
        uploads.create(filename, args);
    }
    
    /**
     * Uploads a file to this index as an event stream.
     * <p>
     * <b>Note:</b> This file must be directly accessible by the Splunk server.
     *
     * @param filename The path and filename.
     * 
     * @param args Optional arguments for this request. Valid parameters are:
     * "host", "sourcetype", "rename-source". More found at:
     * http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTinput#data.2Finputs.2Foneshot
     * 
     */
    public void upload(String filename, Args args) {
        EntityCollection<Upload> uploads = service.getUploads();
        if(args.containsKey("index")){
        	throw new IllegalArgumentException("The 'index' parameter cannot be passed to an index's oneshot upload.");
        }
        args.add("index", getName());
        uploads.create(filename, args);
    }
}

