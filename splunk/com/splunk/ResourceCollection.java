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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResourceCollection<T extends Resource> 
    extends Resource implements Map<String, T> 
{
    protected Map<String, T> items = new HashMap<String, T>();
    protected Class itemClass;

    ResourceCollection(Service service, String path, Class itemClass) {
        super(service, path);
        this.itemClass = itemClass;
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
        return validate().items.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return validate().items.containsValue(value);
    }

    static Class[] itemSig = new Class[] { Service.class, String.class };
    protected T createItem(Class itemClass, String path) {
        Constructor ctor;
        try {
            ctor = itemClass.getDeclaredConstructor(itemSig);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        T item;
        try {
            item = (T)ctor.newInstance(service, path);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }

        return item;
    }

    // Instantiate a collection item corresponding to the given AtomEntry.
    // This base implementation uses the class object passed in when the 
    // generic ResourceCollection was created. Subclasses may override this
    // method to provide alternative means of instantiating a collection items.
    protected T createItem(AtomEntry entry) {
        String path = itemPath(entry);
        return createItem(itemClass, itemPath(entry));
    }

    public Set<Map.Entry<String, T>> entrySet() {
        return validate().items.entrySet();
    }

    public boolean equals(Object o) {
        return validate().items.equals(o);
    }

    public T get(Object key) {
        return validate().items.get(key);
    }

    public int hashCode() {
        return validate().items.hashCode();
    }

    public boolean isEmpty() {
        return validate().items.isEmpty();
    }
    
    // Returns the value to use as the item key from the given AtomEntry.
    // Subclasses may override this for collections that use something other
    // than title as the default.
    protected String itemKey(AtomEntry entry) {
        return entry.title;
    }

    // Retrieve the value to use as the item path from the given AtomEntry.
    // Subclasses may override this to support alternative methods of 
    // determining the item's path.
    protected String itemPath(AtomEntry entry) {
        return entry.links.get("alternate");
    }

    public Set<String> keySet() {
        return validate().items.keySet();
    }

    public ResponseMessage list() {
        return service.get(path + "?count=-1");
    }

    ResourceCollection<T> load(AtomFeed value) {
        super.load(value);
        for (AtomEntry entry : value.entries) {
            String key = itemKey(entry);
            T item = createItem(entry);
            items.put(key, item);
        }
        return this;
    }

    public T put(String key, T value) {
    	throw new UnsupportedOperationException();
    }
    		  	
    public void putAll(Map<? extends String, ? extends T> map) {
    	throw new UnsupportedOperationException();
    }

    @Override public ResourceCollection refresh() {
        items.clear();
        ResponseMessage response = list();
        assert(response.getStatus() == 200); // UNDONE
        AtomFeed feed = AtomFeed.parse(response.getContent());
        load(feed);
        return this;
    }

    public T remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return validate().items.size();
    }

    @Override public ResourceCollection<T> validate() {
        super.validate();
        return this;
    }

    public Collection<T> values() {
        return validate().items.values();
    }
}
