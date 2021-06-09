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
 * The {@code Logger} class represents a specific Splunkd logging category.
 */
public class Logger extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The logger endpoint.
     */
    Logger(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the logging level of this logger. Valid values are: "FATAL",
     * "CRIT", "WARN", "INFO", and "DEBUG".
     *
     * @return The logging level.
     */
    public String getLevel() {
        return getString("level");
    }

    /**
     * Sets the logging level of this logger. Valid values are: "FATAL",
     * "CRIT", "WARN", "INFO", and "DEBUG".
     *
     * @param level The logging level.
     */
    public void setLevel(String level) {
        setCacheValue("level", level);
    }
}

