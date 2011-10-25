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

import com.splunk.atom.*;
import com.splunk.http.ResponseMessage;
import com.splunk.Args;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class Entity extends Resource {
    private Map<String, Object> content;
    private String title;

    public Entity(Service service, String path) {
        super(service, path);
    }

    public ResponseMessage get() throws IOException {
        return service.get(path);
    }

    public void disable() {
        invoke("disable");
        invalidate();
    }

    public void enable() {
        invoke("enable");
        invalidate();
    }

    public Map<String, Object> getContent() {
        validate();
        return this.content;
    }

    boolean getBoolean(String key) {
        return Value.getBoolean(getContent(), key);
    }

    boolean getBoolean(String key, boolean defaultValue) {
        return Value.getBoolean(getContent(), key, defaultValue);
    }

    Date getDate(String key) {
        return Value.getDate(getContent(), key);
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

    String getString(String key) {
        return getContent().get(key).toString();
    }

    String getString(String key, String defaultValue) {
        return Value.getString(getContent(), key, defaultValue);
    }

    Object getValue(String key) {
        return getContent().get(key);
    }

    Object getValue(String key, Object defaultValue) {
        Map<String, Object> map = getContent();
        if (!map.containsKey(key)) return defaultValue;
        return map.get(key);
    }

    public String getTitle() {
        validate();
        return this.title;
    }

    public boolean isDisabled() {
        return Value.getBoolean(getContent(), "disabled", false);
    }

    void load(AtomEntry entry) {
        super.load(entry);
        this.content = entry.content;
        this.title = entry.title;
    }

    public void update(Args args) {
        invoke("edit", args);
        invalidate();
    }

    //
    // Read and construct a singleton Entity instance. Returns null if no
    // entity exists at this resource (indicated by a valid Atom response
    // that does not contain an entry element).
    //
    // Note that singleton Entity "reads" are immediate not deferred like
    // collections because we need to figure out up front if they actually 
    // exist or not and return null if they do not exist, as can be the case
    // in eg: deployment/client
    //
    // CONSIDER:
    //   It's possible we could hoist the existence check into a separate 
    //   code path and only use in the cases (few) where its possible for 
    //   the entity to not exist and allow the other cases to be deferred.
    //
    // UNDONE: How should this singleton factory method interact with typed
    // entity objects?
    //
    static Entity read(Service service, String path) {
        ResponseMessage response;
        try {
            response = service.get(path);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        assert(response.getStatus() == 200); // UNDONE
        AtomFeed feed = AtomFeed.create(response.getContent());
        int count = feed.entries.size();
        if (count == 0) return null;
        assert(count == 1);
        AtomEntry entry = feed.entries.get(0);
        Entity result = new Entity(service, path);
        result.load(entry);
        return result;
    }

    // Refresh the current (singleton) entity instance.
    public void refresh() {
        ResponseMessage response;
        try {
            response = get();
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        assert(response.getStatus() == 200); // UNDONE
        AtomFeed feed = AtomFeed.create(response.getContent());
        assert(feed.entries.size() == 1);
        AtomEntry entry = feed.entries.get(0);
        load(entry);
    }

    public void remove() {
        invoke("remove");
        // UNDONE: would like to set maybe = false on container
    }
}

