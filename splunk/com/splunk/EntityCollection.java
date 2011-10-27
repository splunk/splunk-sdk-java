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
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EntityCollection<T extends Entity> extends Resource implements Map<String, T> {
    private Map<String, T> entities;
    private Class<? extends Entity> entityClass;

    public EntityCollection(Service service, String path) {
        super(service, path);
        entityClass = Entity.class;
    }
    
    public EntityCollection(Service service, String path, Class<T> cls) {
        super(service, path);
        entityClass = cls;
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

    public T create(String name) {
        return create(name, null);
    }

    public T create(String name, Args extra) {
        Args args = new Args();
        args.put("name", name);
        if (extra != null) args.putAll(extra);
        service.post(path, args);
        invalidate();
        return get(name);
    }

    public Set<Map.Entry<String, T>> entrySet() {
        validate();
        return entities.entrySet();
    }

    public boolean equals(Object o) {
        validate();
        return entities.equals(o);
    }

    public T get(Object key) {
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
            this.entities = new HashMap<String, T>();
            for (AtomEntry entry : value.entries) {
                // UNDONE: Unfortunate to have to instantiate an URL object
                // just to retrieve the path. Should probably also assert that
                // the scheme://host:port match the service.
                URL url = new URL(entry.id);
                args[1] = url.getPath();
                T entity = (T)ctor.newInstance(args);
                entity.load(entry);
                this.entities.put(entity.getName(), entity);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }    
    
    public T put(String key, T value) {
    	throw new UnsupportedOperationException();
    }
    	  	
    		  	
    public void putAll(Map<? extends String, ? extends T> map) {
    	throw new UnsupportedOperationException();
    }

    public void refresh() {
        ResponseMessage response = service.get(path);
        assert(response.getStatus() == 200); // UNDONE
        AtomFeed feed = AtomFeed.parse(response.getContent());
        load(feed);
    }

    public T remove(Object key) {
        validate();
        T entity = entities.get(key);
        entity.remove();
        entities.remove(key);
        invalidate();
        return entity;
    }

    public int size() {
        validate();
        return entities.size();
    }

    public Collection<T> values() {
        validate();
        return entities.values();
    }
}
