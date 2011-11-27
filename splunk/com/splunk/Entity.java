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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Entity extends Resource implements Map<String, Object> {
    private Record content;

    Entity(Service service, String path) {
        super(service, path);
    }

    // Returns the path corresponding to the given action.
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

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
        return getContent().containsKey(key);
    }

    public boolean containsValue(Object value) {
        return getContent().containsValue(value);
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
        return getContent().entrySet();
    }

    public Object get(Object key) {
        return getContent().get(key);
    }

    boolean getBoolean(String key) {
        return getContent().getBoolean(key);
    }

    boolean getBoolean(String key, boolean defaultValue) {
        return getContent().getBoolean(key, defaultValue);
    }

    long getByteCount(String key) {
        return getContent().getByteCount(key);
    }

    long getByteCount(String key, long defaultValue) {
        return getContent().getByteCount(key, defaultValue);
    }

    private Record getContent() {
        return validate().content;
    }

    Date getDate(String key) {
        return getContent().getDate(key);
    }

    Date getDate(String key, Date defaultValue) {
        return getContent().getDate(key, defaultValue);
    }

    Date getDateFromEpoch(String key) {
        return getContent().getDateFromEpoch(key);
    }

    Date getDateFromEpoch(String key, Date defaultValue) {
        return getContent().getDateFromEpoch(key, defaultValue);
    }

    float getFloat(String key) {
        return getContent().getFloat(key);
    }

    int getInteger(String key) {
        return getContent().getInteger(key);
    }

    int getInteger(String key, int defaultValue) {
        return getContent().getInteger(key, defaultValue);
    }

    long getLong(String key) {
        return getContent().getLong(key);
    }

    long getLong(String key, int defaultValue) {
        return getContent().getLong(key, defaultValue);
    }

    public EntityMetadata getMetadata() {
        // CONSIDER: For entities that dont have an eai:acl field, which is
        // uncommon, but does happen at least in the case of a DeploymentClient
        // that is not enabled, we return null. A slightly friendlier option
        // would be to return a metadata instance that defaults all values?
        if (!containsKey("eai:acl")) return null;
        return new EntityMetadata(this);
    }

    String getString(String key) {
        return getContent().getString(key);
    }

    String getString(String key, String defaultValue) {
        return getContent().getString(key, defaultValue);
    }

    String[] getStringArray(String key) {
        return getContent().getStringArray(key);
    }

    String[] getStringArray(String key, String[] defaultValue) {
        return getContent().getStringArray(key, defaultValue);
    }

    public boolean isEmpty() {
        return getContent().isEmpty();
    }

    public boolean isDisabled() {
        return getBoolean("disabled", false);
    }

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
        return getContent().size();
    }

    public void update(Map<String, Object> args) {
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
        return getContent().values();
    }
}

