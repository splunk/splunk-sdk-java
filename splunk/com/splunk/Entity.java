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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Entity extends Resource {
    private Map<String, Object> content;

    Entity(Service service, String path) {
        super(service, path);
    }

    public void disable() {
        service.post(actionPath("disable"));
        invalidate();
    }

    public void enable() {
        service.post(actionPath("enable"));
        invalidate();
    }

    public void reload() {
        service.get(actionPath("reload"));
        invalidate();
    }

    public Map<String, Object> getContent() {
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

    Object getValue(String key) {
        return getContent().get(key);
    }

    Object getValue(String key, Object defaultValue) {
        Map<String, Object> map = getContent();
        if (!map.containsKey(key)) return defaultValue;
        return map.get(key);
    }

    public boolean isDisabled() {
        return getBoolean("disabled", false);
    }

    @Override Entity load(AtomObject value) {
        super.load(value);
        AtomEntry entry = (AtomEntry)value;
        if (entry == null) {
            this.content = new HashMap<String, Object>();
        }
        else {
            this.content = entry.content;
        }
        return this;
    }

    public void update(Map args) {
        service.post(actionPath("edit"), args);
        invalidate();
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

    public void remove() {
        service.delete(actionPath("remove"));
    }

    @Override public Entity validate() { 
        super.validate(); 
        return this;
    }
}

