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

public class SavedSearchCollection extends EntityCollection<SavedSearch> {
    SavedSearchCollection(Service service) {
        super(service, "saved/searches", SavedSearch.class);
    }

    public SavedSearch create(String name) {
        throw new UnsupportedOperationException();
    }

    public SavedSearch create(String name, String search) {
        Args args = new Args("search", search);
        return create(name, args);
    }

    public SavedSearch create(String name, String search, Map args) {
        args = Args.create(args).add("search", search);
        return create(name, args);
    }
}
