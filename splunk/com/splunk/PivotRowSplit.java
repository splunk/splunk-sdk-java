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

/**
 * Base class representing row splits in a pivot.
 */
public abstract class PivotRowSplit {
    private final String fieldName;
    private final DataModelObject dataModelObject;
    private final String label;

    PivotRowSplit(DataModelObject dataModelObject, String fieldName, String label) {
        this.fieldName = fieldName;
        this.dataModelObject = dataModelObject;
        this.label = label;
    }

    /**
     * @return the name of the field to split on.
     */
    public String getFieldName() { return this.fieldName; }

    /**
     * @return the name of the data model object on which this split's field is defined.
     */
    public String getOwnerName() { return this.dataModelObject.getField(fieldName).getOwnerName(); }

    /**
     * @return the lineage, most remote ancestor first, on which the split's field is defined.
     */
    public String[] getOwnerLineage() { return this.dataModelObject.getField(fieldName).getOwnerLineage(); }

    /**
     * @return a human readable label for this split.
     */
    public String getLabel() { return this.label; }

    /**
     * Add keys common to all row splits to a JSON serialization.
     *
     * @param obj JSON serialization to modify.
     */
    protected void addCommonFields(JsonObject obj) {
        DataModelField field = this.dataModelObject.getField(this.fieldName);

        obj.addProperty("fieldName", this.fieldName);
        obj.addProperty("owner", Util.join(".", field.getOwnerLineage()));
        obj.addProperty("type", field.getType().toString());
        obj.addProperty("label", this.label);
    }

    /**
     * @return a JSON serialization of this object.
     */
    abstract JsonElement toJson();
}
