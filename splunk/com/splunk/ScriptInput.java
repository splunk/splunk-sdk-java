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
     * Returns the value of the {@code _rcvbuf} attribute for this script input.
     *
     * @return The {@code _rcvbuf} value.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }
}
