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
* The {@code MonitorInput} class represents a monitor input, which is a file,
* directory, script, or network port that is monitored for new data.
 */
public class MonitorInput extends Input {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The monitor input endpoint.
     */
    MonitorInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the file count of this monitor input.
     *
     * @return The file count.
     */
    public int getFileCount() {
        return getInteger("filecount", -1);
    }

    /**
     * Returns the host for this monitor input.
     *
     * @return The host, or {@code null} if not specified.
     */
    public String getHost() {
        return getString("host", null);
    }

    /**
     * Returns the index name for this monitor input.
     *
     * @return The index name.
     */
    public String getIndex() {
        return getString("index");
    }

    /**
     * Returns the type of monitor input.
     * @see InputKind
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.Monitor;
    }

    /**
     * Returns value of the {@code _rcvbuf} attribute for this monitor input.
     *
     * @return The {@code _rcvbuf} value.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }

    /**
     * Sets a regular expression for a file path. The file path that matches
     * this regular expression is not indexed.
     *
     * @param blacklist The path regular expression for exclusion.
     */
    public void setBlacklist(String blacklist) {
        setCacheValue("blacklist", blacklist);
    }

    /**
     * Sets whether ot not the {@code index} value will be checked to ensure
     * that it is the name of a valid index.
     *
     * @param index Whether ot not the {@code index} value will be checked to
     * ensure that it is the name of a valid index.
     */
    public void setCheckIndex(boolean index) {
        setCacheValue("check-index", index);
    }

    /**
     * Sets whether ot not the {@code name} value will be checked to ensure its
     * existence.
     *
     * @param path  whether ot not the {@code name} value will be checked to
     * ensure its existence.
     */
    public void setCheckPath(boolean path) {
        setCacheValue("check-path", path);
    }

    /**
     * Sets a string that modifies the file tracking identity for files in
     * this input. The magic value @{code <SOURCE>} invokes special behavior
     * (see admin documentation).
     *
     * @param salt The string that modifies the file tracking identity for files
     * in this input
     */
    public void setCrcSalt(String salt) {
        setCacheValue("crc-salt", salt);
    }

    /**
     * Sets whether files that are seen for the first time will be read from
     * the end.
     *
     * @param followTail whether files that are seen for the first time will
     * be read from the end.
     */
    public void setFollowTail(boolean followTail) {
        setCacheValue("followTail", followTail);
    }

    /**
     * Sets the value to populate in the host field for events from this data
     * input.
     *
     * @param host the value to populate in the host field for events from this
     * data input.
     */
    public void setHost(String host) {
        setCacheValue("host", host);
    }

    /**
     * Sets the regular expression for a file path. If the path for a file
     * matches this regular expression, the captured value is used to populate
     * the host field for events from this data input. The regular expression
     * must have one capture group.
     *
     * @param regex The regular expression for a file path.
     */
    public void setHostRegex(String regex) {
        setCacheValue("host_regex", regex);
    }

    /**
     * Sets the specified slash-separate segment of the filepath as the host
     * field value.
     *
     * @param segment the specified slash-separate segment of the filepath as
     * the host field value.
     */
    public void setHostSegment(String segment) {
        setCacheValue("host_segment", segment);
    }

    /**
     * Sets a time value. If the modification time of a file being monitored
     * falls outside of this rolling time window, the file is no longer being
     * monitored.
     *
     * @param time The time value.
     */
    public void setIgnoreOlderThan(String time) {
        setCacheValue("ignore-older-than", time);
    }

    /**
     * Sets which index events from this input should be stored in.
     *
     * @param index The index name.
     */
    public void setIndex(String index) {
        setCacheValue("index", index);
    }

    /**
     * Sets whether or not to monitor any sub-directories
     * encountered within this data input.
     *
     * @param recursive whether or not
     */
    public void setRecursive(boolean recursive) {
        setCacheValue("recursive", recursive);
    }

    /**
     * Sets the source name to populate in the source field for events from
     * this data input. The same source should not be used for multiple data
     * inputs.
     *
     * @param name the source name to populate in the source field.
     */
    public void setRenameSource(String name) {
        setCacheValue("rename-source", name);
    }

    /**
     * Sets the source type to populate in the source type for events from
     * this data input.
     *
     * @param sourcetype the source name to populate in the source type field.
     */
    public void setSourcetype(String sourcetype) {
        setCacheValue("sourcetype", sourcetype);
    }

    /**
     * Sets the period, in seconds, to keep a file open.
     *
     * @param period The time, in seconds, to keep a file open.
     */
    public void setTimeBeforeClose(int period) {
        setCacheValue("time-before-close", period);
    }

    /**
     * Sets a regular expression for a file path. The file path that matches
     * this regular expression is indexed.
     *
     * @param whitelist The path regular expression for inclusion.
     */
    public void setWhitelist(String whitelist) {
        setCacheValue("whitelist", whitelist);
    }
}
