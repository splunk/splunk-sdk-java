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
 * The {@code ConfCollection} class represents a collection of configuration
 * files.
 */
public class ConfCollection 
    extends ResourceCollection<EntityCollection<Entity>> 
{
    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     */
    ConfCollection(Service service) {
        super(service, "properties", EntityCollection.class);
    }

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
     */
    ConfCollection(Service service, Args args) {
        super(service, "properties", EntityCollection.class, args);
    }

    /**
     * Creates a new stanza in the current configuration file.
     *
     * @param name The name of the stanza to create.
     * @return The name of the new stanza.
     */
    public EntityCollection<Entity> create(String name) {
        return create(name, null);
    }

    /**
     * Creates a new stanza in the current configuration file with attributes.
     *
     * @param name The name of the stanza to create.
     * @param args Optional. A set of attributes as key-value pairs to put in
     * the new stanza.
     * @return The name of the new stanza.
     */
    public EntityCollection<Entity> create(String name, Map args) {
        args = Args.create(args).add("__conf", name);
        service.post(path, args);
        invalidate();
        return get(name);
    }

    /**
     * Returns the endpoint path for this configuration stanza.
     *
     * @param entry The {@code AtomEntry} representation of this entry.
     * @return This stanza's endpoint path in the format 
     * "/servicesNS/{user}/{app}/configs/conf-{file}/{stanza}".
     */
    @Override protected String itemPath(AtomEntry entry) {
        return String.format("configs/conf-%s", entry.title);
    }
}
