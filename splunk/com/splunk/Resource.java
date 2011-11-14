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

import com.splunk.atom.AtomObject;

import java.util.Map;

public abstract class Resource {
    protected Map<String, String> actions;
    protected String path;
    protected Service service;
    protected String title;
    private boolean maybeValid = false;

    Resource(Service service, String path) {
        this.path = service.fullpath(path);
        this.service = service;
    }

    // Returns the path corresponding to the given action.
    public String actionPath(String action) {
        Map<String, String> actions = getActions();
        if (actions == null) return null;
        return actions.get(action);
    }

    public Map<String, String> getActions() {
        validate();
        return this.actions;
    }

    // Every resource has a name, which by default is its title. That name
    // may also be used as the key for the resources if it belongs to a 
    // container resource, as in the case of an entity that belongs to an 
    // entity collection.
    public String getName() {
        return getTitle();
    }

    public String getPath() {
        return this.path;
    }

    public Service getService() {
        return this.service;
    }

    public String getTitle() {
        validate();
        return this.title;
    }

    void setTitle(String value) {
        this.title = value;
    }

    public void invalidate() {
        this.maybeValid = false;
    }

    void load(AtomObject value) {
        if (value == null) {
            this.title = "title";
        }
        else {
            this.actions = value.links;
            this.title = value.title;
        }
        this.maybeValid = true;
    }

    public abstract void refresh();

    public void validate() {
        if (this.maybeValid == false) refresh();
    }
}

