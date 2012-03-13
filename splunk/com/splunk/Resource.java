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


import java.util.Map;
import java.util.HashMap;

/**
 * Representation of a Splunk resource.
 */
public abstract class Resource {
    protected Map<String, String> actions;
    protected String path;
    protected Service service;
    protected String title;
    private boolean maybeValid = false;

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The target endpoint.
     */
    Resource(Service service, String path) {
        this.path = service.fullpath(path);
        this.service = service;
    }

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     * @param path The target endpoint.
     * @param namespace The namespace of this resource. This namespace will
     *        override the namespace of the service context.
     */
    Resource(Service service, String path, HashMap<String, Object> namespace) {
        this.path = service.fullpath(path, namespace);
        this.service = service;
    }

    /**
     * Returns a map of actions that are enabled for this resource.
     *
     * @return Action map.
     */
    public Map<String, String> getActions() {
        return validate().actions;
    }

    /**
     * Returns the resource name. Every resource has a name, which by default
     * is its title. That name may also be used as the key for the resources
     * if it belongs to a container resource, as in the case of an entity that
     * belongs to an entity collection.
     *
     * @return Resource namne.
     */
    //
    public String getName() {
        return getTitle();
    }

    /**
     * Returns the path to this resource.
     *
     * @return Resource path.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Returns the service instance this resource instance is affiliated with.
     *
     * @return Service instance.
     */
    public Service getService() {
        return this.service;
    }

    /**
     * Return the title of this resource. This corresponds to the Atom title
     * element return in the Splunk REST API.
     *
     * @return Resource title.
     */
    public String getTitle() {
        return validate().title;
    }

    /**
     * Set the title of this resource.
     *
     * @param value Resource title.
     */
    void setTitle(String value) {
        this.title = value;
    }

    /**
     * Mark the local state of this resource instance as no longer current.
     *
     * @return The current resource instance.
     */
    public Resource invalidate() {
        this.maybeValid = false;
        return this;
    }

    /**
     * Load the state of this resource instance from the given Atom object.
     *
     * @param value Atom object to load resources state from.
     * @return The current resource instance.
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
     * Refresh the local state of this resource instance.
     *
     * @return The current resource instance.
     */
    public abstract Resource refresh();

    /**
     * Ensure that the local state of the resource instance is current,
     * possibly invoking {@code refresh} if necessarry.
     *
     * @return The current resource instance.
     */
    public Resource validate() {
        if (!this.maybeValid) refresh();
        return this;
    }
}
