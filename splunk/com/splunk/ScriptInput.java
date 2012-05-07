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

import java.util.Date;

/**
 * The {@code ScriptInput} class represents a script input.
 */
public class ScriptInput extends Input {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The script input endpoint.
     */
    ScriptInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the end-time.
     *
     * @return The end-time, or {@code null if not specified}
     */
    public Date getEndTime() {
        return getDate("endtime", null);
    }

    /**
     * Returns the group for this script input.
     *
     * @return The group, or {@code null} if not specified.
     */
    public String getGroup() {
        return getString("group", null);
    }

    /**
     * Returns the source host for this script input.
     *
     * @return The source host, or {@code null} if not specified.
     */
    public String getHost() {
        return getString("host", null);
    }

    /**
     * Returns the index name for this script input.
     *
     * @return The index name.
     */
    public String getIndex() {
        return getString("index");
    }

    /**
     * Returns the execution frequency for this script input.
     *
     * @return The execution frequency in seconds or a cron schedule.
     */
    public String getInterval() {
        return getString("interval");
    }

    /**
     * Returns the input type for this script input.
     * @see InputKind
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.Script;
    }

    /**
     * Returns the value of the user this script runs under.
     *
     * @return The user that executes this script.
     */
    public String getPassAuth() {
        return getString("passAuth", null);
    }

    /**
     * Returns the value of the {@code _rcvbuf} attribute for this script input.
     *
     * @return The {@code _rcvbuf} value.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }

    /**
     * Returns the source.
     *
     * @return The source.
     */
    public String getSource() {
        return getString("source", null);
    }

    /**
     * Returns the source type.
     *
     * @return The source type.
     */
    public String getSourceType() {
        return getString("sourcetype", null);
    }

    /**
     * Returns the start-time.
     *
     * @return The start-time, or {@code null if not specified}
     */
    public Date getStartTime() {
        return getDate("starttime", null);
    }

    /**
     * Sets whether the script input is enabled or disabled. Note that the
     * supported disabled mechanism, is to use the @{code disable} action.
     *
     * @param disabled {@code true} to disabled to script input,
     * {@code false} to enable.
     */
    public void setDisabled(boolean disabled) {
        setCacheValue("disabled", disabled);
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
     * Sets which index events from this input should be stored in.
     *
     * @param index The index name.
     */
    public void setIndex(String index) {
        setCacheValue("index", index);
    }

    /**
     * Sets either an interval, in seconds, or cron schedule for how often to
     * execute the specified script. If a cron schedule is set, the script is
     * not ececuted on start-up.
     *
     * @param interval an interval, in seconds, or cron schedule.
     */
    public void setInterval(String interval) {
        setCacheValue("interval", interval);
    }

    /**
     * Sets the script to run as this user.
     *
     * @param passAuth the user.
     */
    public void setPassAuth(String passAuth) {
        setCacheValue("passAuth", passAuth);
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
     * Sets the source key/field for events from this input.
     *
     * The key is used during parsing/indexing, in particular to set the source
     * field during indexing. It is also the source field used at search time.
     * As a convenience, the chosen string is prepended with 'source::'.
     *
     * Note: Overriding the source key is generally not recommended. Typically,
     * the input layer provides a more accurate string to aid in problem
     * analysis and investigation, accurately recording the file from which
     * the data was retreived. Consider use of source types, tagging, and search
     * wildcards before overriding this value.
     *
     * @param source The source key/field for events from this input.
     */
    public void setSource(String source) {
        setCacheValue("source", source);
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
}
