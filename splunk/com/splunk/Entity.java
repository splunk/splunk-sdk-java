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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Entity extends Resource implements Map<String, Object> {
    private Map<String, Object> content;

    Entity(Service service, String path) {
        super(service, path);
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
        return validate().content.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return validate().content.containsValue(value);
    }

    public void disable() {
        service.post(actionPath("disable"));
        invalidate();
    }

    public void enable() {
        service.post(actionPath("enable"));
        invalidate();
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return validate().content.entrySet();
    }

    public Object get(Object key) {
        return validate().content.get(key);
    }

    private Map<String, Object> getContent() {
        return validate().content;
    }

    boolean getBoolean(String key) {
        return Value.getBoolean(getContent(), key);
    }

    boolean getBoolean(String key, boolean defaultValue) {
        return Value.getBoolean(getContent(), key, defaultValue);
    }

    long getByteCount(String key) {
        return Value.getByteCount(getContent(), key);
    }

    long getByteCount(String key, long defaultValue) {
        return Value.getByteCount(getContent(), key, defaultValue);
    }

    Date getDate(String key) {
        return Value.getDate(getContent(), key);
    }

    Date getDate(String key, Date defaultValue) {
        return Value.getDate(getContent(), key, defaultValue);
    }

    Date getDateFromEpoch(String key) {
        return Value.getDateFromEpoch(getContent(), key);
    }

    Date getDateFromEpoch(String key, Date defaultValue) {
        return Value.getDateFromEpoch(getContent(), key, defaultValue);
    }

    float getFloat(String key) {
        return Value.getFloat(getContent(), key);
    }

    int getInteger(String key) {
        return Value.getInteger(getContent(), key);
    }

    int getInteger(String key, int defaultValue) {
        return Value.getInteger(getContent(), key, defaultValue);
    }

    long getLong(String key) {
        return Value.getLong(getContent(), key);
    }

    long getLong(String key, int defaultValue) {
        return Value.getLong(getContent(), key, defaultValue);
    }

    public EntityMetadata getMetadata() {
        // CONSIDER: For entities that dont have an eai:acl field, which is
        // uncommon, but does happen at least in the case of a DeploymentClient
        // that is not enabled, we return null. A slightly friendlier option
        // would be to return a metadata instance that defaults all values?
        if (!getContent().containsKey("eai:acl")) return null;
        return new EntityMetadata(this);
    }

    String getString(String key) {
        return Value.getString(getContent(), key);
    }

    String getString(String key, String defaultValue) {
        return Value.getString(getContent(), key, defaultValue);
    }

    String[] getStringArray(String key) {
        return Value.getStringArray(getContent(), key);
    }

    String[] getStringArray(String key, String[] defaultValue) {
        return Value.getStringArray(getContent(), key, defaultValue);
    }

    // UNDONE: Type parameter?
    public <T> T getValue(String key) {
        return (T)validate().get(key);
    }

    public <T> T getValue(String key, T defaultValue) {
        validate();
        if (!containsKey(key)) return defaultValue;
        return (T)get(key);
    }

    public boolean isEmpty() {
        return validate().content.isEmpty();
    }

    public boolean isDisabled() {
        return getBoolean("disabled", false);
    }

    public Set<String> keySet() {
        return validate().content.keySet();
    }

    @Override Entity load(AtomObject value) {
        super.load(value);
        AtomEntry entry = (AtomEntry)value;
        if (entry == null) {
            content = new HashMap<String, Object>();
        }
        else {
            content = entry.content;
        }
        return this;
    }

    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map<? extends String, ? extends Object> map) {
        throw new UnsupportedOperationException();
    }

    // Refresh the current (singleton) entity instance.
    @Override public Entity refresh() {
        ResponseMessage response = service.get(path);
        assert(response.getStatus() == 200); // UNDONE
        AtomFeed feed = AtomFeed.parse(response.getContent());
        int count = feed.entries.size();
        assert(count == 0 || count == 1);
        AtomEntry entry = count == 0 ? null : feed.entries.get(0);
        load(entry);
        return this;
    }

    public void reload() {
        service.get(actionPath("reload"));
        invalidate();
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return validate().content.size();
    }

    public void update(Map args) {
        service.post(actionPath("edit"), args);
        invalidate();
    }

    public void remove() {
        service.delete(actionPath("remove"));
    }

    @Override public Entity validate() { 
        super.validate(); 
        return this;
    }

    public Collection<Object> values() {
        return validate().content.values();
    }
}

