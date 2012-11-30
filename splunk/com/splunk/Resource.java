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
 * The {@code Resource} abstract base class represents a Splunk resource.
 */
public abstract class Resource {
    private static final String[] NAMESPACE_COMPONENT_NAMES = {
        "app", "owner", "sharing"
    };
    
    /* Initialized by constructor. */
    protected Service service;
    protected String path;
    protected Args refreshArgs;
    
    /* Initialized by {@link #load()}. */
    protected Map<String, String> actions;
    protected String title;
    private boolean maybeValid = false;

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The target endpoint.
     */
    Resource(Service service, String path) {
        this(service, path, null);
    }

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The target endpoint.
     * @param args Arguments to use when you instantiate the entity.
     */
    Resource(Service service, String path, Args args) {
        // Clone the original argument list, since it will be modified
        args = Args.create(args);
        
        Args namespace = extractNamespaceFrom(args);
        if (!args.containsKey("count")) {
            args.put("count", "-1");
        }
        
        this.service = service;
        this.path = service.fullpath(
            path, namespace.size() == 0 ? null : namespace);
        this.refreshArgs = args;
    }
    
    /**
     * Extracts the namespace components from the specified argument list
     * (modifying the original list in place) and returns the namespace
     * components in its own namespace Args.
     * 
     * @param args  An argument list. Will be modified in place.
     * @return      The namespace arguments from the original argument list.
     */
    private static Args extractNamespaceFrom(Args args) {
        Args namespace = new Args();
        for (String componentName : NAMESPACE_COMPONENT_NAMES) {
            if (args.containsKey(componentName)) {
                namespace.put(componentName, args.get(componentName).toString());
                args.remove(componentName);
            }
        }
        return namespace;
    }

    /**
     * Returns the resource name. By default, the name is the resource title. 
     * This name can also be used as the key for the resource if it belongs 
     * to a container resource (for example, an entity that belongs to an 
     * entity collection).
     *
     * @return The resource name.
     */
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
     * Return the title of this resource, which corresponds to the Atom
     * {@code <title>} element.
     *
     * @return The resource title.
     */
    public String getTitle() {
        return validate().title;
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
     * @param value The {@code AtomObject} from which to load the resource
     * state.
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
     * calling {@code refresh} if necessary.
     *
     * @return The current {@code Resource} instance.
     */
    public Resource validate() {
        if (!this.maybeValid) refresh();
        return this;
    }
}
