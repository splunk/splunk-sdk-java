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

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * The {@code Entity} class is a base class for all Splunk entity resources.
 */
public class Entity extends Resource implements Map<String, Object> {
    private Record content;

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The entity's endpoint.
     */
    Entity(Service service, String path) {
        super(service, path);
    }

    /**
     * Returns the path that corresponds to the requested action.
     *
     * @param action The requested action.
     * @return The return path.
     */
    protected String actionPath(String action) {
        if (action.equals("disable"))
            return path + "/disable";
        if (action.equals("edit"))
            return path;
        if (action.equals("enable"))
            return path + "/enable";
        if (action.equals("reload"))
            return path + "/_reload";
        if (action.equals("remove"))
            return path;
        throw new IllegalArgumentException("Invalid action: " + action);
    }

    /** {@inheritDoc} */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public boolean containsKey(Object key) {
        return getContent().containsKey(key);
    }

    /** {@inheritDoc} */
    public boolean containsValue(Object value) {
        return getContent().containsValue(value);
    }

    /**
     * Disables the entity that is named by this endpoint. This method is 
     * available on almost every endpoint.
     */
    public void disable() {
        service.post(actionPath("disable"));
        invalidate();
    }

    /**
     * Enables the entity that is named by this endpoint. This method is 
     * available on almost every endpoint.
     */
    public void enable() {
        service.post(actionPath("enable"));
        invalidate();
    }

    /** {@inheritDoc} */
    public Set<Map.Entry<String, Object>> entrySet() {
        return getContent().entrySet();
    }

    /** {@inheritDoc} */
    public Object get(Object key) {
        return getContent().get(key);
    }

    /**
     * Returns the Boolean value associated with the specified key. Values
     * can be converted from: 0, 1, true, false.
     *
     * @param key The key to look up.
     * @return The Boolean value associated with the specified key.
     */
    boolean getBoolean(String key) {
        return getContent().getBoolean(key);
    }

    /**
     * Returns the Boolean value associated with the specified key, or the
     * default value if the key does not exist. Boolean values can be converted
     * from: 0, 1, true, false.
     *
     * @param key The key to look up.
     * @param defaultValue The default value.
     * @return The Boolean value associated with the specified key.
     */
    boolean getBoolean(String key, boolean defaultValue) {
        return getContent().getBoolean(key, defaultValue);
    }

    /**
     * Returns the long value associated with the specified key. Long values
     * can be converted from: number, numberMB, numberGB.
     *
     * @param key The key to look up.
     * @return The long value associated with the specified key.
     */
    long getByteCount(String key) {
        return getContent().getByteCount(key);
    }

    /**
     * Returns the long value associated with the specified key, or the default
     * value if the key does not exist. Long values can be converted from: 
     * number, numberMB, numberGB.
     *
     * @param key The key to look up.
     * @param defaultValue The default value.
     * @return The long value associated with the specified key.
     */
    long getByteCount(String key, long defaultValue) {
        return getContent().getByteCount(key, defaultValue);
    }

    private Record getContent() {
        return validate().content;
    }

    /**
     * Returns a date value associated with the specified key. Date values can
     * be converted from standard UTC time formats.
     *
     * @param key The key to look up.
     * @return The date value associated with the specified key.
     */
    Date getDate(String key) {
        return getContent().getDate(key);
    }

    /**
     * Returns a date value associated with the specified key, or the default
     * value if the key does not exist. Date values can be converted from
     * standard UTC time formats.
     *
     * @param key The key to look up.
     * @param defaultValue The default value.
     * @return The date value associated with the specified key.
     */
    Date getDate(String key, Date defaultValue) {
        return getContent().getDate(key, defaultValue);
    }

    /**
     * Returns a date value associated with the specified key. Date values can
     * be converted from epoch time formats.
     *
     * @param key The key to look up.
     * @return The date value associated with the specified. key.
     */
    Date getDateFromEpoch(String key) {
        return getContent().getDateFromEpoch(key);
    }

    /**
     * Returns a date value associated with the specified key, or the default
     * value if the key does not exist. Date values can be converted from epoch
     * time formats.
     *
     * @param key The key to look up.
     * @param defaultValue The default value.
     * @return The date value associated with the specified key.
     */
    Date getDateFromEpoch(String key, Date defaultValue) {
        return getContent().getDateFromEpoch(key, defaultValue);
    }

    /**
     * Returns the floating point value associated with the specified key.
     *
     * @param key The key to look up.
     * @return The floating point value associated with the specified key.
     */
    float getFloat(String key) {
        return getContent().getFloat(key);
    }

    /**
     * Returns the integer point value associated with the specified key.
     *
     * @param key The key to look up.
     * @return The integer point value associated with the specified key.
     */
    int getInteger(String key) {
        return getContent().getInteger(key);
    }

    /**
     * Returns the integer value associated with the specified key.
     *
     * @param key The key to look up.
     * @param defaultValue The default value.
     * @return The integer value associated with the specified key.
     */
    int getInteger(String key, int defaultValue) {
        return getContent().getInteger(key, defaultValue);
    }

    /**
     * Returns the long value associated with the specified key.
     *
     * @param key The key to look up.
     * @return The long value associated with the specified key.
     */
    long getLong(String key) {
        return getContent().getLong(key);
    }

    /**
     * Returns the long value associated with the specified key.
     *
     * @param key The key to look up.
     * @param defaultValue The default value.
     * @return The long value associated with the specified key.
     */
    long getLong(String key, int defaultValue) {
        return getContent().getLong(key, defaultValue);
    }

    /**
     * Returns the meta data (eai:acl) of this entity.
     *
     * @return The meta data of this entity, or {@code null} if none exist.
     */
    public EntityMetadata getMetadata() {
        // CONSIDER: For entities that don't have an eai:acl field, which is
        // uncommon but does happen at least in the case of a DeploymentClient
        // that is not enabled, we return null. A slightly friendlier option
        // would be to return a metadata instance that defaults all values?
        if (!containsKey("eai:acl")) return null;
        return new EntityMetadata(this);
    }

    /**
     * Returns the string value associated with the specified key.
     *
     * @param key The key to look up.
     * @return The string value associated with the specified key.
     */
    String getString(String key) {
        return getContent().getString(key);
    }

    /**
     * Returns the string value associated with the specified key, or the
     * default value if the key does not exist.
     *
     * @param key The key to look up.
     * @param defaultValue The default value.
     * @return The string value associated with the specified key.
     */
    String getString(String key, String defaultValue) {
        return getContent().getString(key, defaultValue);
    }

    /**
     * Returns the string array value associated with the specified key.
     *
     * @param key The key to look up.
     * @return The string array value associated with the specified key.
     */
    String[] getStringArray(String key) {
        return getContent().getStringArray(key);
    }

    /**
     * Returns the string array value associated with the specified key, or the
     * default value if the key does not exist.
     *
     * @param key The key to look up.
     * @param defaultValue The default value.
     * @return The string array value associated with the specified key.
     */
    String[] getStringArray(String key, String[] defaultValue) {
        return getContent().getStringArray(key, defaultValue);
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return getContent().isEmpty();
    }

    /**
     * Indicates whether this entity is disabled. This method is 
     * available on almost every endpoint.
     *
     * @return {@code true} if this entity is disabled, {@code false} if 
     * enabled.
     */
    public boolean isDisabled() {
        return getBoolean("disabled", false);
    }

    /** {@inheritDoc} */
    public Set<String> keySet() {
        return getContent().keySet();
    }

    @Override Entity load(AtomObject value) {
        super.load(value);
        AtomEntry entry = (AtomEntry)value;
        if (entry == null) {
            content = new Record();
        }
        else {
            content = entry.content;
        }
        return this;
    }

    /** {@inheritDoc} */
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends String, ? extends Object> map) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public Entity refresh() {
        ResponseMessage response = service.get(path);
        assert(response.getStatus() == 200);
        AtomFeed feed = AtomFeed.parse(response.getContent());
        int count = feed.entries.size();
        assert(count == 0 || count == 1);
        AtomEntry entry = count == 0 ? null : feed.entries.get(0);
        load(entry);
        return this;
    }

    /**
     * Performs this entity's reload action.
     */
    public void reload() {
        service.get(actionPath("reload"));
        invalidate();
    }

    /** {@inheritDoc} */
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public int size() {
        return getContent().size();
    }

    /**
     * Updates the entity with the specified arguments.
     *
     * @param args The arguments to update.
     */
    public void update(Map<String, Object> args) {
        service.post(actionPath("edit"), args);
        invalidate();
    }

    /**
     * Removes this entity from its corresponding collection.
     */
    public void remove() {
        service.delete(actionPath("remove"));
    }

    /** {@inheritDoc} */
    @Override public Entity validate() { 
        super.validate(); 
        return this;
    }

    /** {@inheritDoc} */
    public Collection<Object> values() {
        return getContent().values();
    }
}

