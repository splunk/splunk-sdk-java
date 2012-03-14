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

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of a collection of event types.
 */
public class EventTypeCollection extends EntityCollection<EventType> {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     */
    EventTypeCollection(Service service) {
        super(service, "saved/eventtypes", EventType.class);
    }

    /**
     * Creates an event type.
     *
     * @param name The name of this new event type.
     * @param search The search string of this new event type.
     *
     * @return The event type.
     */
    public EventType create(String name, String search) {
        return create(name, search, null);
    }

    /**
     * Create an event type.
     *
     * @param name The name of this new event type.
     * @param search The search string of this new event type.
     * @param args Optional arguments.
     * @return the event type.
     */
    public EventType create(String name, String search, Map args) {
        args = Args.create(args).add("search", search);
        return create(name, args);
    }

    /**
     * Creates an event type.
     *
     * @param name The name of this new event type.
     * @param search The search string of this new event type.
     * @param namespace The namespace.
     * @return The event type.
     */
    public EventType
    create(String name, String search, HashMap<String, String>namespace) {
        return create(name, search, null, namespace);
    }

    /**
     * Create an event type.
     *
     * @param name The name of this new event type.
     * @param search The search string of this new event type.
     * @param args Optional arguments.
     * @param namespace The namespace.
     * @return the event type.
     */
    public EventType
    create(String name, String search,
           Map args, HashMap<String, String>namespace) {
        args = Args.create(args).add("search", search);
        return create(name, args, namespace);
    }
}
