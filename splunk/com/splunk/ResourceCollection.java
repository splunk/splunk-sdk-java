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

public class ResourceCollection<T extends Resource> 
    extends Resource implements Map<String, T> 
{
    protected Map<String, T> items;
    protected Class itemClass;

    ResourceCollection(Service service, String path, Class itemClass) {
        super(service, path);
        this.itemClass = itemClass;
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
        validate();
        return items.containsKey(key);
    }

    public boolean containsValue(Object value) {
        validate();
        return items.containsValue(value);
    }

    public Set<Map.Entry<String, T>> entrySet() {
        validate();
        return items.entrySet();
    }

    public boolean equals(Object o) {
        validate();
        return items.equals(o);
    }

    public T get(Object key) {
        validate();
        return items.get(key);
    }

    public int hashCode() {
        validate();
        return items.hashCode();
    }

    public boolean isEmpty() {
        validate();
        return items.isEmpty();
    }

    protected String itemKey(AtomEntry entry) {
        return entry.title;
    }

    protected String itemPath(AtomEntry entry) {
        return entry.links.get("alternate");
    }

    public Set<String> keySet() {
        validate();
        return items.keySet();
    }

    public ResponseMessage list() {
        return service.get(path + "?count=-1");
    }

    void load(AtomFeed value) {
        try {
            super.load(value);
            Constructor ctor = itemClass.getDeclaredConstructor(
                new Class[] { Service.class, String.class });
            this.items = new HashMap<String, T>();
            for (AtomEntry entry : value.entries) {
                String key = itemKey(entry);
                String path = itemPath(entry);
                T item = (T)ctor.newInstance(service, path);
                this.items.put(key, item);
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
        ResponseMessage response = list();
        assert(response.getStatus() == 200); // UNDONE
        AtomFeed feed = AtomFeed.parse(response.getContent());
        load(feed);
    }

    public T remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        validate();
        return items.size();
    }

    public Collection<T> values() {
        validate();
        return items.values();
    }
}
