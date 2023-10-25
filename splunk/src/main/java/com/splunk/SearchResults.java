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

import java.util.Collection;

/**
 * The {@code SearchResults} interface represents Splunk search results.
 */
public interface SearchResults extends Iterable<Event> {
    /**
     * Indicates whether the results are a preview from an unfinished search.
     * @return {@code true} if the results are a preview, {@code false} if not.
     */
    public boolean isPreview();

    /**
     * Returns a collection of field names from the results.
     * @return A collection of field names.
     * <p>
     * Note that any given result will contain a subset of these fields.
     */
    public Collection<String> getFields();
}
