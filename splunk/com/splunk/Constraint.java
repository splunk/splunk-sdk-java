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
 * Represents a constraint on a data model object or a field on a data model object.
 */
public class Constraint {
    private final DataModelObject owner;
    private final String query;

    public Constraint(DataModelObject owner, String query) {
        this.owner = owner;
        this.query = query;
    }

    /**
     * @return The Splunk search query this constraint specifies.
     */
    public String getQuery() { return this.query; }


    /**
     * @return The DataModelObject that owns this field.
     */
    public DataModelObject getOwner() { return this.owner; }

    /**
     * @return The name of the DataModelObject that owns this field.
     */
    public String getOwnerName() { return getOwner().getName(); }

    /**
     * @return An array of names of DataModelObjects representing the lineage of this field's owner.
     */
    public String[] getOwnerLineage() { return getOwner().getLineage(); }
}
