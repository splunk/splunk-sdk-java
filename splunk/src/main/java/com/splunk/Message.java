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
 * The {@code Message} class represents a message. Most messages are created by 
 * Splunkd to inform the user of system problems, such as license quotas,
 * license expirations, misconfigured indexes, and disk space.
 */
public class Message extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The message endpoint.
     */
    Message(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the title of this message.
     *
     * @return The message title.
     */
    public String getKey() {
        return getTitle();
    }

    /**
     * Returns the content of this message.
     *
     * @return The message text.
     */
    public String getValue() {
        return getString(getKey());
    }
}
