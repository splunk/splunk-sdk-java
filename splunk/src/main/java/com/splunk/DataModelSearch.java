/*
 * Copyright 2014 Splunk, Inc.
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

/**
 * DataModelSearch represents a data model object that directly wraps a Splunk search query.
 *
 * It appears only as a direct subobject of the BaseSearch built-in type. All its children
 * appear as normal DataModelObject instances. DataModelSearch objects have one additional
 * getter, <tt>getBaseSearch</tt>, which returns the underlying search which this object
 * wraps.
 */
public class DataModelSearch extends DataModelObject {
    protected String baseSearch;

    DataModelSearch(DataModel model) {
        super(model);
    }

    /**
     * @return the search query wrapped by this data model object.
     */
    public String getBaseSearch() {
        return baseSearch;
    }
}
