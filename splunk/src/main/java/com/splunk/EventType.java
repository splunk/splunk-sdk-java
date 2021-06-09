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

import java.util.Map;

/**
 * The {@code EventType} class represents an event type.
 */
public class EventType extends Entity {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
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
     * being the highest priority.
     *
     * @return The priority of this event type.
     */
    public int getPriority() {
        return getInteger("priority", -1);
    }

    /**
     * Returns the search terms for this event type.
     *
     * @return The search terms for this event type.
     */
    public String getSearch() {
        return getString("search", null);
    }

    /**
     * @deprecated Use tags.conf.spec file to assign tags to groups of events
     * with related field values.
     *
     * Returns the list of tags for this event type.
     *
     * @return The list of tags for this event type.
     */
    public String [] getTags() {
        return getStringArray("tags", null);
    }

    /**
     * Sets the description of the event type.
     *
     * @param description The description of the event type.
     */
    public void setDescription(String description) {
        setCacheValue("description", description);
    }

    /**
     * Sets whether the event type is enabled or disabled. 
     * <p>
     * <b>Note:</b> Using this method requires you to restart Splunk before this 
     * setting takes effect. To avoid restarting Splunk, use the 
     * {@code Entity.disable} and {@code Entity.enable} methods instead, which 
     * take effect immediately. 
     * @see Entity#disable
     * @see Entity#enable
     */
    public void setDisabled(boolean disabled) {
        setCacheValue("disabled", disabled);
    }

    /**
     * Sets the priority of the event type. Valid values are from 1 to 10, with
     * 1 being the highest priority.
     *
     * @param priority The priority of the event type.
     */
    public void setPriority(int priority) {
        setCacheValue("priority", priority);
    }

    /**
     * Sets the search string.
     *
     * @param search The search string.
     */
    public void setSearch(String search) {
        setCacheValue("search", search);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update(Map<String, Object> args) {
        // Add required arguments if not already present
        if (!args.containsKey("search")) {
            args = Args.create(args).add("search", getSearch());
        }
        super.update(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void update() {
        // If not present in the update keys, add required attribute as long
        // as one pre-existing update pair exists
        if (toUpdate.size() > 0 && !toUpdate.containsKey("search")) {
            setCacheValue("search", getSearch());
        }
        super.update();
    }
}

