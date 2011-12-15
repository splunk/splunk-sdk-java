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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.net.Socket;

/**
 * Representation of an index.
 */
public class Index extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The index endpoint.
     */
    Index(Service service, String path) {
        super(service, path);
    }

    /**
     * Creates a writable socket to this index.
     *
     * @return The Socket.
     * @throws IOException
     */
    public Socket attach() throws IOException {
        Socket socket = service.streamConnect();
        OutputStream ostream = socket.getOutputStream();
        Writer out = new OutputStreamWriter(ostream, "UTF8");

        String header = String.format(
            "POST /services/receivers/stream?index=%s HTTP/1.1\r\n" +
            "Host: %s:%d\r\n" +
            "Accept-Encoding: identity\r\n" +
            "Authorization: %s\r\n" +
            "X-Splunk-Input-Mode: Streaming\r\n\r\n",
            getName(),
            service.getHost(), service.getPort(),
            service.token);
        out.write(header);
        out.flush();
        return socket;
    }

    /**
     * Cleans this index, removing all events.
     *
     * @return This index.
     */
    public Index clean() {
        Args saved = new Args();
        saved.put("maxTotalDataSizeMB",
                  Integer.toString(this.getMaxTotalDataSizeMB()));
        saved.put("frozenTimePeriodInSecs",
                  Integer.toString(this.getFrozenTimePeriodInSecs()));

        Args reset = new Args();
        reset.put("maxTotalDataSizeMB", "1");
        reset.put("frozenTimePeriodInSecs", "1");
        update(reset);
        rollHotBuckets();

        while (true) {
            try {
                Thread.sleep(1000); // 1000ms (1 second sleep)
            }
            catch (InterruptedException e) {
                return this; // eat
            }
            if (this.getTotalEventCount() == 0)
                break;
            refresh();
        }
        update(saved);
        return this;
    }

    /**
     * Returns whether or not data retrieved from this index is in UTF8
     * encoding.
     *
     * @return Whether or not data retrieved from this index is in UTF8.
     */
    public boolean getAssureUTF8() {
        return getBoolean("assureUTF8");
    }

    /**
     * Returns this index's block signature database.
     *
     * @return This index's block signature database.
     */
    public String getBlockSignatureDatabase() {
        return getString("blockSignatureDatabase");
    }

    /**
     * Returns this index's block sign size. This value defines  the number of
     * events that make up a block for block signatures. A value of 0 means
     * block signing is disabled.
     *
     * @return This index's block sign size.
     */
    public int getBlockSignSize() {
        return getInteger("blockSignSize");
    }

    /**
     * Returns this index's colddbs's absolute file path, or null if not
     * specified. This value may contain shell expansion terms.
     *
     * @return this index's colddbs's absolute file path.
     */
    public String getColdPath() {
        return getString("coldPath", null);
    }

    /**
     * Returns this index's colddbs's expanded absolute file path, or null if
     * not specified.
     *
     * @return this index's colddbs's expanded absolute file path.
     */
    public String getColdPathExpanded() {
        return getString("coldPath_expanded", null);
    }

    /**
     * Returns this index's frozen archive destination path, or null if not
     * specified.
     *
     * @return This index's frozen archive destination path.
     */
    public String getColdToFrozenDir() {
        return getString("coldToFrozenDir", null);
    }

    /**
     * Returns this index's archiving script, or null if not specified.
     *
     * @see <a href="http://docs.splunk.com/Documentation/Splunk/latest/RESTAPI/RESTindex#POST_data.2Findexes">Notes on this attribute</a>
     *
     * @return This index's archiving script
     */
    public String getColdToFrozenScript() {
        return getString("coldToFrozenScript", null);
    }

    /**
     * Returns whether or not raw data is compressed.
     *
     * @deprecated Splunk always compresses raw data.
     * @return Whether or not raw data is compressed.
     */
    public boolean getCompressRawdata() {
        return getBoolean("compressRawdata");
    }

    /**
     * Returns this index's current size in MBs.
     *
     * @return This index's current size in MB's
     */
    public int getCurrentDBSizeMB() {
        return getInteger("currentDBSizeMB");
    }

    /**
     * Return the splunk instances default index name.
     *
     * @return The splunk instances default index name.
     */
    public String getDefaultDatabase() {
        return getString("defaultDatabase");
    }

    /**
     * Returns whether or not this index has real-time-search enabled.
     *
     * @return Whether or not this index has real-time-search enabled.
     */
    public boolean getEnableRealtimeSearch() {
        return getBoolean("enableRealtimeSearch");
    }

    /**
     * Returns how long, in seconds, that this index's data rolls to frozen. If
     * archiving is necessary for frozen data, see coldToFrozen attributes.
     *
     * @return How long, in seconds, that this index's data rolls to frozen.
     */
    public int getFrozenTimePeriodInSecs() {
        return getInteger("frozenTimePeriodInSecs");
    }

    /**
     * Returns this index's absolute path to both hot and warm buckets, or null
     * if not specified. This value may contain shell expansion terms.
     *
     * @return This index's absolute path to both hot and warm buckets.
     */
    public String getHomePath() {
        return getString("homePath", null);
    }

    /**
     * Returns this index's expanded absolute path to both hot and warm buckets,
     * or null if not specified.
     *
     * @return This index's expanded absolute path to both hot and warm buckets.
     */
    public String getHomePathExpanded() {
        return getString("homePath_expanded", null);
    }

    /**
     * Returns this index's index thread.
     *
     * @return This index's index thread.
     */
    public String getIndexThreads() {
        return getString("indexThreads");
    }

    /**
     * Returns this index's last initialization time, or null if not specified.
     *
     * @return This index's last initialization time.
     */
    public String getLastInitTime() {
        return getString("lastInitTime", null);
    }

    /**
     * Returns this index's maximum number of concurrent optimize processes per
     * hot bucket.
     *
     * @return This index's maximum number of concurrent optimize processes per
     * hot bucket.
     */
    public int getMaxConcurrentOptimizes() {
        return getInteger("maxConcurrentOptimizes");
    }

    /**
     * Returns this index's maximum data size, in MB or "auto" or
     * "auto_high_volume", before triggering a roll from hot to warm buckets.
     *
     * @return This index's maximum data size, before triggering a roll from hot
     * to warm buckets.
     */
    public String getMaxDataSize() {
        return getString("maxDataSize");
    }

    /**
     * Returns this index's maximum number of hot buckets.
     *
     * @return this index's maximum number of hot buckets.
     */
    public int getMaxHotBuckets() {
        return getInteger("maxHotBuckets");
    }

    /**
     * Returns this index's hot bucket's maximum life, in seconds. A value of
     * zero means infinite.
     *
     * @return This index's hot bucket's maximum life, in seconds.
     */
    public int getMaxHotIdleSecs() {
        return getInteger("maxHotIdleSecs");
    }

    /**
     * Returns this index's hot/warm bucket upper bound timespan, in seconds.
     *
     * @return This index's hot/warm bucket upper bound timespan, in seconds.
     */
    public int getMaxHotSpanSecs() {
        return getInteger("maxHotSpanSecs");
    }

    /**
     * Returns this index's single tsidx file maximum size, in MB.
     *
     * @return This index's single tsidx file maximum size, in MB.
     */
    public int getMaxMemMB() {
        return getInteger("maxMemMB");
    }

    /**
     * Returns this index's maximum number of lines permissible in the .data
     * file. A value of zero means infinite.
     *
     * @return This index's maximum number of lines permissible in the .data
     * file.
     */
    public int getMaxMetaEntries() {
        return getInteger("maxMetaEntries");
    }

    /**
     * Returns this index's maximum concurrent helper processes.
     *
     * @return This index's maximum concurrent helper processes.
     */
    public int getMaxRunningProcessGroups() {
        return getInteger("maxRunningProcessGroups");
    }

    /**
     * Returns this index's max time attribute, or null if not specified.
     *
     * @return This index's max time attribute.
     */
    public Date getMaxTime() {
        return getDate("maxTime", null);
    }

    /**
     * Returns this index's maximum allowable size, in MB. If the index grows
     * larger, the oldest data is frozen.
     *
     * @return This index's maximum allowable size, in MB.
     */
    public int getMaxTotalDataSizeMB() {
        return getInteger("maxTotalDataSizeMB");
    }

    /**
     * Returns this index's warm bucket count maximum. If this value is
     * exceeded, the warm buckets with oldest latest times are moved to cold.
     *
     * @return This index's warm bucket count maximum.
     */
    public int getMaxWarmDBCount() {
        return getInteger("maxWarmDBCount");
    }

    /**
     * Returns this index's memory pool, in MB or "auto".
     *
     * @return This index's memory pool, in MB.
     */
    public String getMemPoolMB() {
        return getString("memPoolMB");
    }

    /**
     * Returns this index's file system sync frequency while compressing
     * journal slices. Note that a value of "disable" disables this feature
     * completely, while a value of 0 forces a filesystem sync after
     * every journal slice.
     *
     * @return This index's file system sync frequency while compressing
     * journal slices.
     */
    public String getMinRawFileSyncSecs() {
        return getString("minRawFileSyncSecs");
    }

    /**
     * Returns this index's minimum time attribute, or null if not specified.
     *
     * @return This index's minimum time attribute.
     */
    public Date getMinTime() {
        return getDate("minTime", null);
    }

    /**
     * Returns this index's meta data sync interval, in seconds. A value of zero
     * disables sync until a requested metadata sync.
     *
     * @return This index's meta data sync interval, in seconds.
     */
    public int getPartialServiceMetaPeriod() {
        return getInteger("partialServiceMetaPeriod");
    }

    /**
     * Returns this index's future event time quarantine, in seconds. Events
     * that are newer than now plus this value are quarantined.
     *
     * @return This index's future event time quarantine, in seconds.
     */
    public int getQuarantineFutureSecs() {
        return getInteger("quarantineFutureSecs");
    }

    /**
     * Returns this index's past event time quarantine, in seconds. Events
     * that are older than now minus this value are quarantined.
     *
     * @return This index's past event time quarantine, in seconds.
     */
    public int getQuarantinePastSecs() {
        return getInteger("quarantinePastSecs");
    }

    /**
     * Returns this index's raw slice uncompressed size, in bytes.
     *
     * @return This index's raw slice uncompressed size, in bytes.
     */
    public int getRawChunkSizeBytes() {
        return getInteger("rawChunkSizeBytes");
    }

    /**
     * Returns this index's hot bucket creation check frequency. This value
     * also indicates warm to cold and cold to frozen check frequency.
     *
     * @return This index's hot bucket creation check frequency.
     */
    public int getRotatePeriodInSecs() {
        return getInteger("rotatePeriodInSecs");
    }

    /**
     * Returns this index's meta data sync frequency.
     *
     * @return This index's meta data sync frequency.
     */
    public int getServiceMetaPeriod() {
        return getInteger("serviceMetaPeriod");
    }

    /**
     * Returns a comma separated list of indexes that suppress index missing
     * messages.
     *
     * @return A comma separated list of indexes that suppress index missing
     * messages.
     */
    public String getSuppressBannerList() {
        return getString("suppressBannerList", null);
    }

    /**
     * Returns this index's sync attribute.
     *
     * @return This index's sync attribute.
     */
    public boolean getSync() {
        return getBoolean("sync");
    }

    /**
     * Returns this index's syncMeta attribute. When true, a sync operation is
     * invoked before the file descriptor is closed on metadata updates.
     *
     * @return This index's syncMeta attribute.
     */
    public boolean getSyncMeta() {
        return getBoolean("syncMeta");
    }

    /**
     * Returns this index's absolute path to the thawed index, or null if not
     * present. This value may contain shell expansion terms.
     *
     * @return This index's absolute path to the thawed index.
     */
    public String getThawedPath() {
        return getString("thawedPath", null);
    }

    /**
     * Returns this index's expanded absolute path to the thawed index, or null
     * if not present.
     *
     * @return This index's expanded absolute path to the thawed index.
     */
    public String getThawedPathExpanded() {
        return getString("thawedPath_expanded", null);
    }

    /**
     * Returns this index's throttling frequency check, in seconds.
     *
     * @return This index's throttling frequency check, in seconds.
     */
    public int getThrottleCheckPeriod() {
        return getInteger("throttleCheckPeriod");
    }

    /**
     * Returns this index's total event count.
     *
     * @return This index's total event count.
     */
    public int getTotalEventCount() {
        return getInteger("totalEventCount");
    }

    /**
     * Returns whether or not this index is an internal index.
     *
     * @return Whether ot not this index is an internal index.
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
     * Submits an event to this index through HTTP POST.
     *
     * @param data Event data posted.
     */
    public void submit(String data) {
        RequestMessage request = new RequestMessage("POST");
        request.setContent(data);
        service.send("receivers/simple?index=" + getName(), request);
    }

    /**
     * Uploads a file to this index as an event stream. Note: this file must
     * be accessible to the splunk instance as a local file or through NAS.
     *
     * @param filename The file uploaded.
     */
    public void upload(String filename) {
        Args args = new Args();
        args.put("name", filename);
        args.put("index", getName());
        service.post("/services/data/inputs/oneshot", args);
    }
}

