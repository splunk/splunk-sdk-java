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
 * Representation of Event Types.
 */
public class EventType extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The event type endpoint.
     */
    EventType(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the description of this event type.
     *
     * @return The description of this event type.
     */
    public String getDescription() {
        return getString("description", null);
    }

    /**
     * Returns the priority of this event type. The range is 1 to 10, with 1
     * beging the highest priority.
     *
     * @return The priority of this event type.
     */
    public int getPriority() {
        return getInteger("priority", -1);
    }

    /**
     * Returns this event type's search terms.
     *
     * @return This event types' search terms.
     */
    public String getSearch() {
        return getString("search", null);
    }

    /**
     * @deprecated Use tags.conf.spec file to assign tags to groups of events
     * with related field values.
     *
     * Returns this event types list of tags.
     *
     * @return This event types list of tags.
     */
    public String [] getTags() {
        return getStringArray("tags", null);
    }
}

