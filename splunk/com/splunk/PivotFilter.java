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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;

/**
 * Base class representing filters in pivots.
 */
public abstract class PivotFilter {
    protected final DataModelObject dataModelObject;
    protected final String fieldName;

    PivotFilter(DataModelObject dataModelObject, String fieldName) {
        this.dataModelObject = dataModelObject;
        if (!dataModelObject.containsField(fieldName)) {
            throw new IllegalArgumentException("No such field " + fieldName + " on specified data model object.");
        }
        this.fieldName = fieldName;
    }

    /**
     * @return the name of the data model object this filter's field is defined on.
     */
    public String getOwnerName() { return this.dataModelObject.getField(this.fieldName).getOwnerName(); }

    /**
     * @return return the lineage, most remote ancestor first, of the data model object his filter's field is
     * defined on.
     */
    public String[] getOwnerLineage() { return this.dataModelObject.getField(this.fieldName).getOwnerLineage(); }

    /**
     * @return the name of the field to filter on.
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * @return the type of the field we are filtering on.
     */
    public FieldType getType() {
        return this.dataModelObject.getField(fieldName).getType();
    }

    /**
     * @return a JSON serialization of this object.
     */
    abstract JsonElement toJson();

    /**
     * Called by subclasses to add the fields common to all subclasses to JSON serializations.
     *
     * @param root a JsonObject instance representing a serialization of this object.
     */
    protected void addCommonFields(JsonObject root) {
        root.addProperty("fieldName", this.fieldName);
        root.addProperty("owner", Util.join(".", this.dataModelObject.getField(fieldName).getOwnerLineage()));
        root.addProperty("type", this.getType().toString());
    }
}
