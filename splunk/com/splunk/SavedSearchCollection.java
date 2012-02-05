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

/**
 * Representation of a collection of saved searches.
 */
public class SavedSearchCollection extends EntityCollection<SavedSearch> {

    /**
     * Constructs an instance of the {@code SavedSearchCollection}.
     *
     * @param service The service the entity is affiliated with.
     */
    SavedSearchCollection(Service service) {
        super(service, "saved/searches", SavedSearch.class);
    }

    /**
     * Constructs an instance of the {@code SavedSearchCollection}.
     *
     * @param service The service the entity is affiliated with.
     * @param args Arguments use at instantiation, such as count and offset.
     */
    SavedSearchCollection(Service service, Args args) {
        super(service, "saved/searches", SavedSearch.class, args);
    }

    /** {@inheritDoc} */
    @Override public SavedSearch create(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Create a saved search with the given name and search expression.
     *
     * @param name The name of the new saved search.
     * @param search The search expression for the new saved search.
     * @return The newly created saved search.
     */
    public SavedSearch create(String name, String search) {
        Args args = new Args("search", search);
        return create(name, args);
    }

    /**
     * Create a saved search with the given name, search expression and
     * additional saved search arguments.
     *
     * @param name The name of the new saved search.
     * @param search The search expression for the new saved search.
     * @param args Additional saved search arguments.
     * @return The newly created saved search.
     */
    public SavedSearch create(String name, String search, Map args) {
        args = Args.create(args).add("search", search);
        return create(name, args);
    }
}
