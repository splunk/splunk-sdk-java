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

/**
 * Contains arguments used to create a {@link Job}.
 */
public class JobArgs extends Args {

    /**
     * Class constructor.
     */
    public JobArgs() { super(); }
    
    /**
     * @param auto_cancel
     *      If specified, the job automatically cancels after this many seconds of inactivity. (0 means never auto-cancel)
     */
    public void setAutoCancel(int auto_cancel) {
        this.put("auto_cancel", String.valueOf(auto_cancel));
    }
    
    /**
     * @param auto_finalize_ec
     *      Auto-finalize the search after at least this many events have been processed.
     *      
     *      Specify 0 to indicate no limit.
     */
    public void setAutoFinalizeEventCount(int auto_finalize_ec) {
        this.put("auto_finalize_ec", String.valueOf(auto_finalize_ec));
    }
    
    /**
     * @param auto_pause
     *      If specified, the job automatically cancels after this many seconds of inactivity. (0 means never auto-pause) 
     */
    public void setAutoPause(int auto_pause) {
        this.put("auto_pause", String.valueOf(auto_pause));
    }
    
    /**
     * @param earliest_time
     *      Specify a time string. Sets the earliest (inclusive), respectively, time bounds for the search.
     *      
     *      The time string can be either a UTC time (with fractional seconds), a relative time specifier (to now) or a formatted time string. (Also see comment for the search_mode variable.)
     */
    public void setEarliestTime(String earliest_time) {
        this.put("earliest_time", String.valueOf(earliest_time));
    }
    
    /**
     * @param enable_lookups
     *      Indicates whether lookups should be applied to events.
     *      
     *      Specifying true (the default) may slow searches significantly depending on the nature of the lookups.
     */
    public void setEnableLookups(boolean enable_lookups) {
        this.put("enable_lookups", String.valueOf(enable_lookups));
    }
    
    /**
     * @param exec_mode
     *      If set to normal, runs an asynchronous search.
     *      
     *      If set to blocking, returns the sid when the job is complete.
     *      
     *      If set to oneshot, returns results in the same call. 
     */
    public void setExecutionMode(ExecutionMode exec_mode) {
        this.put("exec_mode", String.valueOf(exec_mode));
    }
    
    /**
     * @param force_bundle_replication
     *      Specifies whether this search should cause (and wait depending on the value of sync_bundle_replication) for bundle synchronization with all search peers.
     */
    public void setForceBundleReplication(boolean force_bundle_replication) {
        this.put("force_bundle_replication", String.valueOf(force_bundle_replication));
    }
    
    /**
     * @param id
     *      Optional string to specify the search ID (&lt:sid>). If unspecified, a random ID is generated.
     */
    public void setId(String id) {
        this.put("id", String.valueOf(id));
    }
    
    /**
     * @param latest_time
     *      Specify a time string. Sets the latest (exclusive), respectively, time bounds for the search.
     *      
     *      The time string can be either a UTC time (with fractional seconds), a relative time specifier (to now) or a formatted time string. (Also see comment for the search_mode variable.)
     */
    public void setLatestTime(String latest_time) {
        this.put("latest_time", String.valueOf(latest_time));
    }
    
    /**
     * @param max_count
     *      The number of events that can be accessible in any given status bucket.
     *      
     *      Also, in transforming mode, the maximum number of results to store. Specifically, in all calls, codeoffset+count <= max_count.
     */
    public void setMaximumCount(int max_count) {
        this.put("max_count", String.valueOf(max_count));
    }
    
    /**
     * @param max_time
     *      The number of seconds to run this search before finalizing. Specify 0 to never finalize.
     */
    public void setMaximumTime(int max_time) {
        this.put("max_time", String.valueOf(max_time));
    }
    
    /**
     * @param namespace
     *      The application namespace in which to restrict searches.
     *      
     *      The namespace corresponds to the identifier recognized in the /services/apps/local endpoint.
     */
    public void setNamespace(String namespace) {
        this.put("namespace", String.valueOf(namespace));
    }
    
    /**
     * @param now
     *      Specify a time string to set the absolute time used for any relative time specifier in the search. Defaults to the current system time.
     *      
     *      You can specify a relative time modifier for this parameter. For example, specify +2d to specify the current time plus two days.
     *      
     *      If you specify a relative time modifier both in this parameter and in the search string, the search string modifier takes precedence.
     *      
     *      Refer to <a href="http://docs.splunk.com/Documentation/Splunk/4.3.4/SearchReference/SearchTimeModifiers">Time modifiers for search</a> for details on specifying relative time modifiers. 
     */
    public void setNow(String now) {
        this.put("now", String.valueOf(now));
    }
    
    /**
     * @param reduce_freq
     *      Determines how frequently to run the MapReduce reduce phase on accumulated map values. 
     */
    public void setReduceFrequency(int reduce_freq) {
        this.put("reduce_freq", String.valueOf(reduce_freq));
    }
    
    /**
     * @param reload_macros
     *      Specifies whether to reload macro definitions from macros.conf.
     *      
     *      Default is true.
     */
    public void setReloadMacros(boolean reload_macros) {
        this.put("reload_macros", String.valueOf(reload_macros));
    }
    
    /**
     * @param remote_server_list
     *      List of (possibly wildcarded) servers from which raw events should be pulled. This same server list is to be used in subsearches.
     */
    public void setRemoteServerList(String[] remote_server_list) {
        StringBuilder csv = new StringBuilder();
        for (int i = 0, n = remote_server_list.length; i < n; i++) {
            if (i != 0) {
                csv.append(",");
            }
            csv.append(remote_server_list[i]);
        }
        
        this.put("remote_server_list", String.valueOf(csv));
    }
    
    /**
     * @param rf
     *      Required fields for the search.
     *      
     *      These fields, even if not referenced or used directly by the search, are still included by the events and summary endpoints. Splunk Web uses these fields to prepopulate panels in the Search view.
     */
    public void setRequiredFieldList(String[] rf) {
        this.put("rf", rf);
    }
    
    /**
     * @param rt_blocking
     *      For a realtime search, indicates if the indexer blocks if the queue for this search is full.
     */
    public void setRealtimeBlocking(boolean rt_blocking) {
        this.put("rt_blocking", String.valueOf(rt_blocking));
    }
    
    /**
     * @param rt_indexfilter
     *      For a realtime search, indicates if the indexer prefilters events.
     */
    public void setRealtimeIndexFilter(boolean rt_indexfilter) {
        this.put("rt_indexfilter", String.valueOf(rt_indexfilter));
    }
    
    /**
     * @param rt_maxblocksecs
     *      For a realtime search with rt_blocking set to true, the maximum time to block.
     *      
     *      Specify 0 to indicate no limit.
     */
    public void setRealtimeMaximumBlockSeconds(int rt_maxblocksecs) {
        this.put("rt_maxblocksecs", String.valueOf(rt_maxblocksecs));
    }
    
    /**
     * @param rt_queue_size
     *      For a realtime search, the queue size (in events) that the indexer should use for this search.
     */
    public void setRealtimeQueueSize(int rt_queue_size) {
        this.put("rt_queue_size", String.valueOf(rt_queue_size));
    }
    
    /**
     * @param search_listener
     *      Registers a search state listener with the search.
     *      
     *      Use the format:
     *      
     *      search_state;results_condition;http_method;uri;
     *      
     *      For example:
     *      
     *      search_listener=onResults;true;POST;/servicesNS/admin/search/saved/search/foobar/notify;
     */
    public void setSearchListener(String search_listener) {
        this.put("search_listener", String.valueOf(search_listener));
    }
    
    /**
     * @param search_mode
     *      If set to realtime, search runs over live data. A realtime search may also be indicated by earliest_time and latest_time variables starting with 'rt' even if the search_mode is set to normal or is unset. For a real-time search, if both earliest_time and latest_time are both exactly 'rt', the search represents all appropriate live data received since the start of the search.
     *      
     *      Additionally, if earliest_time and/or latest_time are 'rt' followed by a relative time specifiers then a sliding window is used where the time bounds of the window are determined by the relative time specifiers and are continuously updated based on the wall-clock time.
     */
    public void setSearchMode(SearchMode search_mode) {
        this.put("search_mode", String.valueOf(search_mode));
    }
    
    /**
     * @param spawn_process
     *      Specifies whether the search should run in a separate spawned process. Default is true.
     *      
     *      Searches against indexes must run in a separate process.
     */
    public void setSpawnProcess(boolean spawn_process) {
        this.put("spawn_process", String.valueOf(spawn_process));
    }
    
    /**
     * @param status_buckets
     *      The most status buckets to generate.
     *      
     *      0 indicates to not generate timeline information.
     */
    public void setStatusBuckets(int status_buckets) {
        this.put("status_buckets", String.valueOf(status_buckets));
    }
    
    /**
     * @param sync_bundle_replication
     *      Specifies whether this search should wait for bundle replication to complete.
     */
    public void setSynchronizeBundleReplication(boolean sync_bundle_replication) {
        this.put("sync_bundle_replication", String.valueOf(sync_bundle_replication));
    }
    
    /**
     * @param time_format
     *      Used to convert a formatted time string from {start,end}_time into UTC seconds. It defaults to ISO-8601.
     */
    public void setTimeFormat(String time_format) {
        this.put("time_format", String.valueOf(time_format));
    }
    
    /**
     * @param timeout
     *      The number of seconds to keep this search after processing has stopped.
     */
    public void setTimeout(int timeout) {
        this.put("timeout", String.valueOf(timeout));
    }
    
    /**
     * Affects how a call to {@link JobCollection#create} operates.
     */
    public static enum ExecutionMode {
        /** Runs an asynchronous search. */
        NORMAL("normal"),
        /** Returns the sid when the job is complete. */
        BLOCKING("blocking"),
        /** Returns results in the same call. */
        ONESHOT("oneshot");
        
        private String value;
        
        private ExecutionMode(String value) {
            this.value = value;
        }
        
        /**
         * @return The REST API value for this enumerated constant.
         */
        public String toString() {
            return this.value;
        }
    }
    
    /**
     * Affects how a call to {@link JobCollection#create} operates.
     */
    public static enum SearchMode {
        /**
         * Search runs over historical data.
         */
        NORMAL("normal"),
        /**
         * Search runs over live data. A realtime search may also be indicated by earliest_time and latest_time variables starting with 'rt' even if the search_mode is set to normal or is unset. For a real-time search, if both earliest_time and latest_time are both exactly 'rt', the search represents all appropriate live data received since the start of the search.
         * 
         * Additionally, if earliest_time and/or latest_time are 'rt' followed by a relative time specifiers then a sliding window is used where the time bounds of the window are determined by the relative time specifiers and are continuously updated based on the wall-clock time.
         */
        REALTIME("realtime");
        
        private String value;
        
        private SearchMode(String value) {
            this.value = value;
        }
        
        /**
         * @return The REST API value for this enumerated constant.
         */
        public String toString() {
            return this.value;
        }
    }
}
