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

import com.splunk.atom.*;
import com.splunk.http.ResponseMessage;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EntityCollection extends Resource implements Map<String, Entity> {
    private Map<String, Entity> entities;

    Class entityClass = Entity.class;

    public EntityCollection(Service service, String path) {
        super(service, path);
    }

    public EntityCollection(Service service, String path, Class entityClass) {
        super(service, path);
        this.entityClass = entityClass;
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
        validate();
        return entities.containsKey(key);
    }

    public boolean containsValue(Object value) {
        validate();
        return entities.containsValue(value);
    }

    public Entity create(String name) {
        return null; // UNDONE
    }

    public Set<Map.Entry<String, Entity>> entrySet() {
        validate();
        return entities.entrySet();
    }

    public boolean equals(Object o) {
        validate();
        return entities.equals(o);
    }

    public Entity get(Object key) {
        validate();
        return entities.get(key);
    }

    public int hashCode() {
        validate();
        return entities.hashCode();
    }

    public boolean isEmpty() {
        validate();
        return entities.isEmpty();
    }

    public Set<String> keySet() {
        validate();
        return entities.keySet();
    }

    void load(AtomFeed value) {
        try {
            super.load(value);
            Constructor ctor = entityClass.getConstructor(
                new Class[] { Service.class, String.class });
            Object[] args = new Object[2];
            args[0] = service;
            this.entities = new HashMap<String, Entity>();
            for (AtomEntry entry : value.entries) {
                args[1] = entry.id; // Entity path
                Entity entity = (Entity)ctor.newInstance(args);
                entity.load(entry);
                this.entities.put(entity.getName(), entity);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Entity put(String key, Entity value) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map<? extends String, ? extends Entity> map) {
        throw new UnsupportedOperationException();
    }

    public void refresh() {
        ResponseMessage response = get();
        assert(response.getStatus() == 200); // UNDONE
        AtomFeed feed = AtomFeed.create(response.getContent());
        load(feed);
    }

    public Entity remove(Object key) {
        validate();
        Entity entity = entities.get(key);
        entity.remove();
        entities.remove(key);
        invalidate();
        return entity;
    }

    public int size() {
        validate();
        return entities.size();
    }

    public Collection<Entity> values() {
        validate();
        return entities.values();
    }
}

