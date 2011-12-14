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

/**
 * Represents a collection of Splunk resources.
 *
 * @param <T> The type of members of the collection.
 */
public class ResourceCollection<T extends Resource> 
    extends Resource implements Map<String, T> 
{
    protected Map<String, T> items = new HashMap<String, T>();
    protected Class itemClass;

    ResourceCollection(Service service, String path, Class itemClass) {
        super(service, path);
        this.itemClass = itemClass;
    }

    /** {@inheritDoc} */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public boolean containsKey(Object key) {
        return validate().items.containsKey(key);
    }

    /** {@inheritDoc} */
    public boolean containsValue(Object value) {
        return validate().items.containsValue(value);
    }

    static Class[] itemSig = new Class[] { Service.class, String.class };

    /**
     * Creates a collection member (aka item).
     *
     * @param itemClass Class of the member to create.
     * @param path Path to the member resource.
     * @return The created member.
     */
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

    /**
     * Creates a collection member corresponding to the given Atom entry.
     * This base implementation uses the class object pass in when the generic
     * ResourceCollection was created. Subclasses may override this method
     * to provide alternative means of instantiating collection items.
     *
     * @param entry Atom entry corresponding to the member to instantiate.
     * @return The newly created member.
     */
    protected T createItem(AtomEntry entry) {
        return createItem(itemClass, itemPath(entry));
    }

    /** {@inheritDoc} */
    public Set<Map.Entry<String, T>> entrySet() {
        return validate().items.entrySet();
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        return validate().items.equals(o);
    }

    /** {@inheritDoc} */
    public T get(Object key) {
        return validate().items.get(key);
    }

    @Override public int hashCode() {
        return validate().items.hashCode();
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return validate().items.isEmpty();
    }
    
    /**
     * Returns the value to use as the item key from the given Atom entry.
     * Subclasses may override this for collections that use something other
     * than title as the key.
     *
     * @param entry The Atom entry corresponding to the collection member.
     * @return The value to use as the member's key.
     */
    protected String itemKey(AtomEntry entry) {
        return entry.title;
    }

    /**
     * Returns the vlaue to use as the item path from the given Atom entry.
     * Subclasses may override this to support alternative methods of
     * determining a members path.
     *
     * @param entry The Atom entry corresponding to the collection member.
     * @return The value to use as the members path.
     */
    protected String itemPath(AtomEntry entry) {
        return entry.links.get("alternate");
    }

    /** {@inheritDoc} */
    public Set<String> keySet() {
        return validate().items.keySet();
    }

    /**
     * Issues an HTTP request to list the contents of the collection resource.
     *
     * @return List response message.
     */
    public ResponseMessage list() {
        return service.get(path + "?count=-1");
    }

    /**
     * Loads the collection resource from the given {@code AtomFeed}.
     *
     * @param value The {@code AtomFeed} instance to load the collection from.
     * @return The current resource collection instance.
     */
    ResourceCollection<T> load(AtomFeed value) {
        super.load(value);
        for (AtomEntry entry : value.entries) {
            String key = itemKey(entry);
            T item = createItem(entry);
            items.put(key, item);
        }
        return this;
    }

    /** {@inheritDoc} */
    public T put(String key, T value) {
    	throw new UnsupportedOperationException();
    }

    /**
     * Copies all mappings from the given map to this map (unsupported).
     *
     * @param map The set of mappings to copy into this map.
     */
    public void putAll(Map<? extends String, ? extends T> map) {
    	throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public ResourceCollection refresh() {
        items.clear();
        ResponseMessage response = list();
        assert(response.getStatus() == 200);
        AtomFeed feed = AtomFeed.parse(response.getContent());
        load(feed);
        return this;
    }

    /** {@inheritDoc} */
    public T remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public int size() {
        return validate().items.size();
    }

    /** {@inheritDoc} */
    @Override public ResourceCollection<T> validate() {
        super.validate();
        return this;
    }

    /** {@inheritDoc} */
    public Collection<T> values() {
        return validate().items.values();
    }
}
