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
     * Returns a regular expression for a file path that when matched is not 
     * indexed.
     *
     * @return The regex for a file path.
     */
    public String getBlacklist() {
        return getString("blacklist", null);
    }

    /**
     * Returns a string that is used to force Splunk to index files that have a
     * matching cyclic redundancy check (CRC). 
     *
     * When set, this string is added to the CRC. If the string is 
     * "{@code <SOURCE>}", the full source path is added to the CRC, ensuring 
     * that each file being monitored has a unique CRC. For more info, see the 
     * <a href="http://docs.splunk.com/Documentation/Splunk/latest/Data/Editinputs.conf" 
     * target="_blank">Edit inputs.conf</a> 
     * topic in the Getting Data In manual.
     *
     * @return The string that is added to the CRC, or {@code null} if not set.
     */
    public String getCrcSalt() {
        return getString("crcSalt", null);
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
     * Indicates whether files that are seen for the first time will be read
     * from the end.
     *
     * @return {@code true} if new files are read from the end, {@code false} if 
     * not. 
     */
    public boolean getFollowTail() {
        return getBoolean("followTail", false);
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
     * Returns the regular expression for a file path to determine the host. 
     * If the path for a file matches this regular expression, the captured 
     * value is used to populate the <b>host</b> field for events from this 
     * monitor input. The regular expression must have one capture group.
     *
     * @return The regular expression for a file path.
     */
    public String getHostRegex() {
        return getString("host_regex", null);
    }

    /**
     * Returns a time value that defines a rolling time window for monitoring 
     * files. If the modification time of a file being monitored falls outside 
     * of this rolling time window, the file is no longer being monitored.
     *
     * @return The time value.
     */
    public String getIgnoreOlderThan() {
        return getString("ignoreOlderThan", null);
    }

    /**
     * Gets the index where events from this monitor input are stored.
     *
     * @return The index name.
     */
    public String getIndex() {
        return getString("index");
    }

    /**
     * Returns the type of monitor input.
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.Monitor;
    }

    /**
     * Returns the queue for this monitor input. Valid values are "parsingQueue"
     * and "indexQueue".
     *
     * @return The queue, or {@code null} if not specified.
     */
    public String getQueue() {
        return getString("queue", null);
    }

    /**
     * Indicates whether sub-directories are monitored within this monitor 
     * input.
     *
     * @return {@code true} if sub-directories are monitored, {@code false} if 
     * not.
     */
    public boolean getRecursive() {
        return getBoolean("recursive", false);
    }

    /**
     * @deprecated Returns the value of the {@code _rcvbuf} attribute for this 
     * monitor input.
     *
     * @return The {@code _rcvbuf} value.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }

    /**
     * Returns the source of events from this monitor input.
     *
     * @return The source name.
     */
    public String getSource() {
        return getString("source", null);
    }

    /**
     * Returns the source type of events from this monitor input.
     *
     * @return The source type.
     */
    public String getSourceType() {
        return getString("sourcetype", null);
    }

    /**
     * Returns the time period for keeping a file open.
     *
     * @return  The time to keep a file open, in seconds.
     */
    public int getTimeBeforeClose() {
        return getInteger("time_before_close", -1);
    }

    /**
     * Returns a regular expression for a file path that, when matched, is 
     * indexed. 
     *
     * @return  The regular expression for a file path.
     */
    public String getWhitelist() {
        return getString("whitelist", null);
    }

    /**
     * Sets a regular expression for a file path that, when matched, is not 
     * indexed.
     *
     * @param blacklist The regular expression for a file path.
     */
    public void setBlacklist(String blacklist) {
        setCacheValue("blacklist", blacklist);
    }

    /**
     * Sets whether the {@code index} value is checked to ensure that it is the 
     * name of a valid index.
     *
     * @param index {@code true} to verify {@code index}, {@code false} if not.
     */
    public void setCheckIndex(boolean index) {
        setCacheValue("check-index", index);
    }

    /**
     * Sets whether the {@code name} value is checked to ensure that it exists.
     *
     * @param path {@code true} to verify {@code name}, {@code false} if not.
     */
    public void setCheckPath(boolean path) {
        setCacheValue("check-path", path);
    }

    /**
     * Sets a string that is used to force Splunk to index files that have a
     * matching cyclic redundancy check (CRC). 
     *
     * When set, this string is added to the CRC. If the string is 
     * "{@code <SOURCE>}", the full source path is added to the CRC, ensuring 
     * that each file being monitored has a unique CRC. For more info, see the 
     * <a  href="http://docs.splunk.com/Documentation/Splunk/latest/Data/Editinputs.conf" 
     * target="_blank">Edit inputs.conf</a> 
     * topic in the Getting Data In manual.
     *
     * @param salt The string that is added to the CRC.
     */
    public void setCrcSalt(String salt) {
        setCacheValue("crc-salt", salt);
    }

    /**
     * Sets whether files that are seen for the first time will be read from
     * the end.
     *
     * @param followTail {@code true} to read new files from the end, 
     * {@code false} if not. 
     */
    public void setFollowTail(boolean followTail) {
        setCacheValue("followTail", followTail);
    }

    /**
     * Sets the value to populate in the <b>host</b> field for events from this
     * monitor input.
     *
     * @param host The value for the <b>host</b> field.
     */
    public void setHost(String host) {
        setCacheValue("host", host);
    }

    /**
     * Set the regular expression for a file path to determine the host. 
     * If the path for a file matches this regular expression, the captured 
     * value is used to populate the <b>host</b> field for events from this 
     * monitor input. The regular expression must have one capture group.
     *
     * @param regex The regular expression for a file path.
     */
    public void setHostRegex(String regex) {
        setCacheValue("host_regex", regex);
    }

    /**
     * Sets the specified slash-separate segment of the file path as the 
     * <b>host</b> field value.
     *
     * @param segment The slash-separate segment.
     */
    public void setHostSegment(String segment) {
        setCacheValue("host_segment", segment);
    }

    /**
     * Sets a time value that defines a rolling time window for monitoring 
     * files. If the modification time of a file being monitored falls outside 
     * of this rolling time window, the file is no longer being monitored.
     *
     * @param time The time value.
     */
    public void setIgnoreOlderThan(String time) {
        setCacheValue("ignore-older-than", time);
    }

    /**
     * Sets the index where events from this monitor input are stored.
     *
     * @param index The index name.
     */
    public void setIndex(String index) {
        setCacheValue("index", index);
    }

    /**
     * Sets whether to monitor sub-directories within this monitor input.
     *
     * @param recursive {@code true} to monitor sub-directories, {@code false} 
     * if not.
     */
    public void setRecursive(boolean recursive) {
        setCacheValue("recursive", recursive);
    }

    /**
     * Sets the name to populate in the <b>source</b> field for events
     * from this monitor input. The same source name should not be used for 
     * multiple data inputs.
     *
     * @param name The source name.
     */
    public void setRenameSource(String name) {
        setCacheValue("rename-source", name);
    }

    /**
     * Sets the source type to populate in the <b>sourcetype</b> field for 
     * events from this monitor input.
     *
     * @param sourcetype The source type.
     */
    public void setSourcetype(String sourcetype) {
        setCacheValue("sourcetype", sourcetype);
    }

    /**
     * Sets the time period for keeping a file open.
     *
     * @param period The time to keep a file open, in seconds.
     */
    public void setTimeBeforeClose(int period) {
        setCacheValue("time-before-close", period);
    }

    /**
     * Sets a regular expression for a file path that, when matched, is indexed.
     *
     * @param whitelist The regular expression for the file path.
     */
    public void setWhitelist(String whitelist) {
        setCacheValue("whitelist", whitelist);
    }
}
