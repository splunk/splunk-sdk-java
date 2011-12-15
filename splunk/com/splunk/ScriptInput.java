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

/**
 * Representation of a script input.
 */
public class ScriptInput extends Input {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The Script input endpoint.
     */
    ScriptInput(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this script input's group.
     *
     * @return This script input's group.
     */
    public String getGroup() {
        return getString("group", null);
    }

    /**
     * Returns this script input's source host, or null if not specified.
     *
     * @return This TCP input's source host.
     */
    public String getHost() {
        return getString("host", null);
    }

    /**
     * Returns this script input's index name.
     *
     * @return This script input's index name.
     */
    public String getIndex() {
        return getString("index");
    }

    /**
     * Returns this script input's execution frequency in seconds, or a cron
     * schedule.
     *
     * @return This scripts input's execution frequency in seconds, or a cron
     * schedule.
     */
    public String getInterval() {
        return getString("interval");
    }

    /**
     * Returns the script input kind.
     *
     * @return The script input kind.
     */
    public InputKind getKind() {
        return InputKind.Script;
    }

    /**
     * Returns this script input's _rcvbuf attribute.
     *
     * @return This script input's _rcvbuf attribute.
     */
    public int getRcvBuf() {
        return getInteger("_rcvbuf");
    }
}
