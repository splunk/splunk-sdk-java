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
import java.util.*;

/**
 * Representation of a collection of Splunk resources.
 *
 * @param <T> The type of members of the collection.
 */
public class ResourceCollection<T extends Resource> 
    extends Resource implements Map<String, T>
{
    protected Map<String, LinkedList<T>>
                litems = new HashMap<String, LinkedList<T>>();
    protected Class itemClass;

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     * @param path The target endpoint.
     * @param itemClass The class of this resource item.
     */
    ResourceCollection(Service service, String path, Class itemClass) {
        super(service, path);
        this.itemClass = itemClass;
    }

    /**
     * Class constructor.
     *
     * @param service The connected service instance.
     * @param path The target endpoint.
     * @param itemClass The class of this resource item.
     * @param namespace The namespace of this collection.
     */
    ResourceCollection(Service service, String path,
                       Class itemClass, HashMap<String, String> namespace) {
        super(service, service.fullpath(path, namespace));
        this.itemClass = itemClass;
    }

    /** {@inheritDoc} */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public boolean containsKey(Object key) {
        return validate().litems.containsKey(key);
    }

    /** {@inheritDoc} */
    public boolean containsValue(Object value) {
        // value should be a non-linked-list value; values are stored as linked
        // lists inside our container.
        LinkedList<Object> linkedList = new LinkedList<Object>();
        linkedList.add(value);
        return validate().litems.containsValue(linkedList);
    }

    static Class[] itemSig = new Class[] { Service.class, String.class };

    /**
     * Creates a collection member (aka item).
     *
     * @param itemClass Class of the member to create.
     * @param path Path to the member resource.
     * @param namespace The namespace.
     * @return The created member.
     */
    protected T createItem(Class itemClass, String path,
                           HashMap<String, String> namespace) {
        Constructor ctor;
        try {
            ctor = itemClass.getDeclaredConstructor(itemSig);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        T item;
        try {
            item =
               (T)ctor.newInstance(service, service.fullpath(path, namespace));
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
        return createItem(itemClass, itemPath(entry), namespace(entry));
    }

    /** {@inheritDoc} */
    public Set<Map.Entry<String, T>> entrySet() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        // value should be a non-linked-list value; values are stored as linked
        // lists inside our container.
        LinkedList<Object> linkedList = new LinkedList<Object>();
        linkedList.add(o);
        return validate().litems.equals(linkedList);
    }

    /** {@inheritDoc} */
    public T get(Object key) {
        validate();
        LinkedList<T> entities = litems.get(key);
        if (entities != null && entities.size() > 1) {
            throw new SplunkException(SplunkException.AMBIGUOUS,
                    "Key has multiple values, specify a namespace");
        }
        if (entities == null || entities.size() == 0) return null;
        return entities.get(0);
    }

    /** {@inheritDoc} */
    public T get(Object key, HashMap<String, String> namespace) {
        validate();
        LinkedList<T> entities = litems.get(key);
        String pathMatcher = service.fullpath("", namespace);
        if (entities == null || entities.size() == 0) return null;
        for (T entity: entities) {
            if (entity.path.startsWith(pathMatcher)) {
                return entity;
            }
        }
        return null;
    }

    @Override public int hashCode() {
        return validate().litems.hashCode();
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return validate().litems.isEmpty();
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
     * Returns the value to use as the item path from the given Atom entry.
     * Subclasses may override this to support alternative methods of
     * determining a members path.
     *
     * @param entry The Atom entry corresponding to the collection member.
     * @return The value to use as the members path.
     */
    protected String itemPath(AtomEntry entry) {
        return entry.links.get("alternate");
    }

    private HashMap<String, String> namespace(AtomEntry entry) {
        HashMap<String, String>namespace = new HashMap<String, String>();

        // no content? return an empty namespace.
        if (entry.content == null)
            return namespace;

        HashMap<String, String> entityMetadata =
                (HashMap<String, String>)entry.content.get("eai:acl");
        if (entityMetadata.containsKey("owner"))
            namespace.put("owner", entityMetadata.get("owner"));
        if (entityMetadata.containsKey("app"))
            namespace.put("app", entityMetadata.get("app"));
        if (entityMetadata.containsKey("sharing"))
            namespace.put("sharing", entityMetadata.get("sharing"));
        return namespace;
    }

    /** {@inheritDoc} */
    public Set<String> keySet() {
        return validate().litems.keySet();
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
            if (litems.containsKey(key)) {
                LinkedList<T> list = litems.get(key);
                list.add(item);
            } else {
                LinkedList<T> list = new LinkedList<T>();
                list.add(item);
                litems.put(key, list);
            }
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
        litems.clear();
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
        return validate().litems.size();
    }

    /** {@inheritDoc} */
    @Override public ResourceCollection<T> validate() {
        super.validate();
        return this;
    }

    /** {@inheritDoc} */
    public Collection<T> values() {
        LinkedList<T> collection = new LinkedList<T>();
        validate();
        Set<String> keySet = litems.keySet();
        for (String key: keySet) {
            LinkedList<T> list = litems.get(key);
            for (T item: list) {
                collection.add(item);
            }
        }
        return collection;
    }

    /**
     * Returns the number of values a specific key represents.
     *
     * @param key The key to lookup.
     * @returns The number of entity values represented by the key.
     */
    public int valueSize(Object key) {
        validate();
        LinkedList<T> entities = litems.get(key);
        if (entities == null || entities.size() == 0) return 0;
        return entities.size();
    }
}
