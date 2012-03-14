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
 * Representation of a collection of configurations.
 */
public class ConfCollection 
    extends ResourceCollection<EntityCollection<Entity>> 
{
    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     */
    ConfCollection(Service service) {
        super(service, "properties", EntityCollection.class);
    }

    /**
     * Creates a config.
     *
     * @param name The new config name.
     * @return The new config.
     */
    public EntityCollection<Entity> create(String name) {
        return create(name, null);
    }

    /**
     * Creates a new config.
     *
     * @param name The new config name.
     * @param args Optional arguments.
     * @return the new config.
     */
    public EntityCollection<Entity> create(String name, Map args) {
        args = Args.create(args).add("__conf", name);
        service.post(path, args);
        invalidate();
        return get(name);
    }

    /**
     * Creates a config.
     *
     * @param name The new config name.
     * @param namespace The namespace.
     * @return The new config.
     */
    public EntityCollection<Entity>
    create(String name, HashMap<String, String>namespace) {
        return create(name, null, namespace);
    }

    /**
     * Creates a new config.
     *
     * @param name The new config name.
     * @param args Optional arguments.
     * @param namespace The namespace.
     * @return the new config.
     */
    public EntityCollection<Entity>
    create(String name, Map args, HashMap<String, String>namespace) {
        args = Args.create(args).add("__conf", name);
        service.post(service.fullpath(partialPath, namespace), args);
        invalidate();
        return get(name);
    }

    /**
     * Returns This config's endpoint path.
     *
     * @param entry The atom representation of this entry.
     * @return This config's endpoint path.
     */
    @Override protected String itemPath(AtomEntry entry) {
        return String.format("configs/conf-%s", entry.title);
    }
}
