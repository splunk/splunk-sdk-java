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

    protected boolean getBoolean(String key) {
        return Value.getBoolean(getContent(), key);
    }

    protected boolean getBoolean(String key, boolean defaultValue) {
        return Value.getBoolean(getContent(), key, defaultValue);
    }

    protected Date getDate(String key) {
        return Value.getDate(getContent(), key);
    }

    protected float getFloat(String key) {
        return Value.getFloat(getContent(), key);
    }

    protected int getInteger(String key) {
        return Value.getInteger(getContent(), key);
    }

    protected int getInteger(String key, int defaultValue) {
        return Value.getInteger(getContent(), key, defaultValue);
    }

    protected String getString(String key) {
        return getContent().get(key).toString();
    }

    protected String getString(String key, String defaultValue) {
        return Value.getString(getContent(), key, defaultValue);
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

