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

import java.util.Map;

/**
 * The {@code Resource} class represents a Splunk resource.
 */
public abstract class Resource {
    protected Map<String, String> actions;
    protected String path;
    protected Service service;
    protected String title;
    private boolean maybeValid = false;

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The target endpoint.
     */
    Resource(Service service, String path) {
        this.path = service.fullpath(path);
        this.service = service;
    }

    /**
     * Returns a map of actions that are enabled for this resource.
     *
     * @return The action map.
     */
    public Map<String, String> getActions() {
        return validate().actions;
    }

    /**
     * Returns the resource name. By default, the name is the resource title. 
     * This name may also be used as the key for the resource if it belongs 
     * to a container resource (for example, an entity that belongs to an 
     * entity collection).
     *
     * @return The resource name.
     */
    //
    public String getName() {
        return getTitle();
    }

    /**
     * Returns the path to this resource.
     *
     * @return The resource path.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Returns the {@code Service} instance this resource is connected to.
     *
     * @return The {@code Service} instance.
     */
    public Service getService() {
        return this.service;
    }

    /**
     * Return the title of this resource, which corresponds to the Atom {@code <title>}
     * element.
     *
     * @return The resource title.
     */
    public String getTitle() {
        return validate().title;
    }

    /**
     * Sets the title of this resource.
     *
     * @param value The resource title.
     */
    void setTitle(String value) {
        this.title = value;
    }

    /**
     * Marks the local state of this resource as no longer current.
     *
     * @return The current {@code Resource} instance.
     */
    public Resource invalidate() {
        this.maybeValid = false;
        return this;
    }

    /**
     * Loads the state of this resource from a given Atom object.
     *
     * @param value The {@code AtomObject} from which to load the resource state.
     * @return The current {@code Resource} instance.
     */
    Resource load(AtomObject value) {
        if (value == null) {
            this.title = "title";
        }
        else {
            this.actions = value.links;
            this.title = value.title;
        }
        this.maybeValid = true;
        return this;
    }

    /**
     * Refreshes the local state of this resource.
     *
     * @return The current {@code Resource} instance.
     */
    public abstract Resource refresh();

    /**
     * Ensures that the local state of the resource is current,
     * invoking {@code refresh} if necessary.
     *
     * @return The current {@code Resource} instance.
     */
    public Resource validate() {
        if (this.maybeValid == false) refresh();
        return this;
    }
}
