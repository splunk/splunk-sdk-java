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
 * Representation of a collection of messages.
 */
public class MessageCollection extends EntityCollection<Message> {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     */
    MessageCollection(Service service) {
        super(service, "messages", Message.class);
    }

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param args Arguments use at instantiation, such as count and offset.
     */
    MessageCollection(Service service, Args args) {
        super(service, "messages", Message.class, args);
    }

    /**
     * Create a new message.
     *
     * @param name The name of the new message.
     * @param value The value of the message.
     * @return The created message.
     */
    public Message create(String name, String value) {
        Args args = new Args("value", value);
        return create(name, args);
    }
}
