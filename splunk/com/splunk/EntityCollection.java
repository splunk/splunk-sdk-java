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
import java.util.LinkedList;
import java.util.Map;

/**
 * Representation of a collection of entities.
 *
 * @param <T> The type of members of the collection.
 */
public class EntityCollection<T extends Entity> extends ResourceCollection<T> {

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     * @param path The entity's endpoint.
     */
    EntityCollection(Service service, String path) {
        super(service, path, Entity.class);
    }

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     * @param path The entity's endpoint.
     * @param args Arguments use at instantiation, such as count and offset.
     */
    EntityCollection(Service service, String path, Args args) {
        super(service, path, Entity.class, args);
    }

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     * @param path The entity's endpoint.
     * @param namespace This collection's namespace.
     */
    EntityCollection(
            Service service, String path, HashMap<String, String> namespace) {
        super(service, path, Entity.class, namespace);
    }

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     * @param path The entity's endpoint.
     * @param args Arguments use at instantiation, such as count and offset.
     * @param namespace This collection's namespace.
     */
    EntityCollection(Service service, String path, Args args,
                     HashMap<String, String> namespace) {
        super(service, path, Entity.class, args, namespace);
    }



    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     * @param path The entity's endpoint.
     * @param itemClass This entity's class.
     */
    EntityCollection(Service service, String path, Class itemClass) {
        super(service, path, itemClass);
    }

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     * @param path The entity's endpoint.
     * @param itemClass This entity's class.
     * @param args Arguments use at instantiation, such as count and offset.
     */
    EntityCollection(Service service, String path, Class itemClass, Args args) {
        super(service, path, itemClass, args);
    }

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     * @param path The entity's endpoint.
     * @param itemClass This entity's class.
     * @param namespace This collection's namespace.
     */
    EntityCollection(Service service, String path, Class itemClass,
            HashMap<String, String> namespace) {
        super(service, path, itemClass, namespace);
    }

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     * @param path The entity's endpoint.
     * @param itemClass This entity's class.
     * @param args Arguments use at instantiation, such as count and offset.
     * @param namespace This collection's namespace.
     */
    EntityCollection(Service service, String path, Class itemClass, Args args,
                     HashMap<String, String> namespace) {
        super(service, path, itemClass, args, namespace);
    }

    /**
     * Creates an entity in this collection.
     *
     * @param name The name of the entity created.
     * @return The entity.
     */
    public T create(String name) {
        return create(name, (Map)null);
    }

    /**
     * Creates an entity in this collection.
     *
     * @param name The name of the entity created.
     * @param args The arguments supplied to the creation.
     * @return The entity.
     */
    public T create(String name, Map args) {
        args = Args.create(args).add("name", name);
        service.post(path, args);
        invalidate();
        return get(name);
    }

    /**
     * Removes an entity from this collection.
     *
     * @param key the name of the entity to remove.
     * @return this collection.
     * @throws SplunkException "AMBIGUOUS" if the collection contains more than
     * one entity with the specified key. Disambiguation is done through the
     * similar method {@code remove(Object key, HashMap<String,String>namespace}
     * which uses the namespace to perform the disambiguation.
     */
    public T remove(String key) {
        validate();
        if (!containsKey(key)) return null;
        LinkedList<T> entities = linkedListItems.get(key);
        if (entities != null && entities.size() > 1) {
            throw new SplunkException(SplunkException.AMBIGUOUS,
                    "Key has multiple values, specify a namespace");
        }
        if (entities == null) return null;
        T entity = entities.get(0);
        entity.remove();
        // by invalidating any access to linkedListItems will get refreshed
        invalidate();
        return entity;
    }

    public T remove(String key, HashMap<String, String> namespace) {
        validate();
        if (!containsKey(key)) return null;
        LinkedList<T> entities = linkedListItems.get(key);
        String pathMatcher = service.fullpath("", namespace);
        if (entities == null || entities.size() == 0) return null;
        for (T entity: entities) {
            if (entity.path.startsWith(pathMatcher)) {
                entity.remove();
                // by invalidating any access to linkedListItems will get
                // refreshed
                invalidate();
                return entity;
            }
        }
        return null;
    }
}
