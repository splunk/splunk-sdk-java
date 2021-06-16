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
 * The {@code EventTypeCollection} class represents a collection of event types.
 */
public class EventTypeCollection extends EntityCollection<EventType> {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     */
    EventTypeCollection(Service service) {
        super(service, "saved/eventtypes", EventType.class);
    }

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them (see {@link CollectionArgs}).
     */
    EventTypeCollection(Service service, Args args) {
        super(service, "saved/eventtypes", EventType.class, args);
    }

    /**
     * Creates an event type.
     *
     * @param name The name of the event type.
     * @param search The search string of the event type.
     *
     * @return The event type.
     */
    public EventType create(String name, String search) {
        return create(name, search, null);
    }

    /**
     * Creates an event type.
     *
     * @param name The name of the event type.
     * @param search The search string of the event type.
     * @param args Optional arguments: "description", "disabled", and 
     * "priority".
     * @return The event type.
     */
    public EventType create(String name, String search, Map args) {
        args = Args.create(args).add("search", search);
        return create(name, args);
    }
}
