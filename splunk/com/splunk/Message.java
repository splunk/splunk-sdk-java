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
 * Representation of Message.
 */
public class Message extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The message endpoint.
     */
    Message(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns this message's title.
     *
     * @return This message's title.
     */
    public String getKey() {
        return getTitle();
    }

    /**
     * Returns this message's value.
     *
     * @return This message's value.
     */
    public String getValue() {
        return getString(getKey());
    }
}
