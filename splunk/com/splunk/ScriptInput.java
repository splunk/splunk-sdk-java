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
 * The {@code ScriptInput} class represents a scripted data input.
 */
public class ScriptInput extends Input {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The scripted input endpoint.
     */
    ScriptInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the time when the scripted input stopped running.
     *
     * @return The ending time, or {@code null} if not specified.
     */
    public Date getEndTime() {
        return getDate("endtime", null);
    }

    /**
     * Returns the OS group of commands for this scripted input.
     *
     * @return The group of commands, or {@code null} if not specified.
     */
    public String getGroup() {
        return getString("group", null);
    }

    /**
     * Returns the source host for this scripted input.
     *
     * @return The source host, or {@code null} if not specified.
     */
    public String getHost() {
        return getString("host", null);
    }

    /**
     * Returns the index name for this scripted input.
     *
     * @return The index name.
     */
    public String getIndex() {
        return getString("index");
    }

    /**
     * Returns the frequency for running this scripted input.
     *
     * @return The frequency, in seconds or a cron schedule.
     */
    public String getInterval() {
        return getString("interval");
    }

    /**
     * Returns the input kind for this scripted input.
     *
     * @return The input kind.
     */
    public InputKind getKind() {
        return InputKind.Script;
    }

    /**
     * Returns the username that this scripted input runs under.
     *
     * @return The username.
     */
    public String getPassAuth() {
        return getString("passAuth", null);
    }

    /**
     * @deprecated Returns the value of the {@code _rcvbuf} attribute for this 
     * scripted input.
     *
     * @return The {@code _rcvbuf} value.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }

    /**
     * Returns the source of events from this scripted input.
     *
     * @return The source.
     */
    public String getSource() {
        return getString("source", null);
    }

    /**
     * Returns the source type of events from this scripted input.
     *
     * @return The source type.
     */
    public String getSourceType() {
        return getString("sourcetype", null);
    }

    /**
     * Returns the time when the script was started.
     *
     * @return The start time, or {@code null if not specified}.
     */
    public Date getStartTime() {
        return getDate("starttime", null);
    }

    /**
     * Sets whether the scripted input is enabled or disabled. 
     * <p>
     * <b>Note:</b> Using this method requires you to restart Splunk before this
     * setting takes effect. To avoid restarting Splunk, use the 
     * {@code Entity.disable} and {@code Entity.enable} methods instead, which 
     * take effect immediately. 
     *
     * @param disabled {@code true} to disable the input, {@code false} to
     * enable it.
     */
    public void setDisabled(boolean disabled) {
        setCacheValue("disabled", disabled);
    }

    /**
     * Sets the value for the <b>host</b> field for events from this scripted
     * input.
     *
     * @param host The host.
     */
    public void setHost(String host) {
        setCacheValue("host", host);
    }

    /**
     * Sets the index in which to store events from this scripted input.
     *
     * @param index The index name.
     */
    public void setIndex(String index) {
        setCacheValue("index", index);
    }

    /**
     * Sets an interval or a cron schedule that determines when to run the 
     * script. If a cron schedule is used, the script doesn't run at startup.
     *
     * @param interval An interval, in seconds or a cron schedule.
     */
    public void setInterval(String interval) {
        setCacheValue("interval", interval);
    }

    /**
     * Sets a username to run the script under. Splunk generates an 
     * authorization token for the username and passes it to the script. 
     *
     * @param passAuth The username.
     */
    public void setPassAuth(String passAuth) {
        setCacheValue("passAuth", passAuth);
    }

    /**
     * Sets the source name for events from this scripted input. The same source
     * should not be used for multiple data inputs.
     *
     * @param name The source name.
     */
    public void setRenameSource(String name) {
        setCacheValue("rename-source", name);
    }

    /**
     * Sets the initial value for the source key for events from this 
     * input. The source key is used during parsing and indexing. The 
     * <b>source</b> field is used for searches. As a convenience, the source 
     * string is prepended with "source::".
     * <p>
     * <b>Note:</b> Overriding the source key is generally not recommended. 
     * Typically, the input layer provides a more accurate string to aid in 
     * problem analysis and investigation, accurately recording the file from 
     * which the data was retrieved. Consider the use of source types, tagging, 
     * and search wildcards before overriding this value.
     *
     * @param source The source.
     */
    public void setSource(String source) {
        setCacheValue("source", source);
    }

    /**
     * Sets the source type for events from this scripted input.
     *
     * @param sourcetype The source type.
     */
    public void setSourcetype(String sourcetype) {
        setCacheValue("sourcetype", sourcetype);
    }
}
