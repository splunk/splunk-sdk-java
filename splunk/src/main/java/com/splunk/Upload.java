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
 * The {@code Upload} class represents an in-progress oneshot upload. Use this 
 * class to query the state of the upload. 
 * @see Index#upload 
 */
public class Upload extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The oneshot input endpoint.
     */
    Upload(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the number of bytes that are currently indexed.
     *
     * @return The number of bytes.
     */
    public int getBytesIndexed() {
        return getInteger("Bytes Indexed");
    }

    /**
     * Returns the current offset.
     *
     * @return The offset.
     */
    public int getOffset() {
        return getInteger("Offset");
    }

    /**
     * Returns the current size.
     *
     * @return The size.
     */
    public int getSize() {
        return getInteger("Size");
    }

    /**
     * Returns the number of sources that are indexed.
     *
     * @return The number of sources.
     */
    public int getSourcesIndexed() {
        return getInteger("Sources Indexed");
    }

    /**
     * Returns the start time of the upload that is being indexed.
     *
     * @return The start time.
     */
    public Date getSpoolTime() {
        return getDate("Spool Time", null);
    }
}

