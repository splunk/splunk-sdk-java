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
 * The {@code MessageCollection} class represents a collection of messages, 
 * providing access to Splunk system messages. Most messages are created by 
 * Splunkd to inform the user of system problems.
 */
public class MessageCollection extends EntityCollection<Message> {

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     */
    MessageCollection(Service service) {
        super(service, "messages", Message.class);
    }

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
     */
    MessageCollection(Service service, Args args) {
        super(service, "messages", Message.class, args);
    }

    /**
     * Creates a new message.
     *
     * @param name The name (primary key) of the new message.
     * @param value The message text.
     * @return The new message.
     */
    public Message create(String name, String value) {
        Args args = new Args("value", value);
        return create(name, args);
    }
}
