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

public abstract class PivotFilter {
    private final DataModelObject dataModelObject;
    private final String fieldName;

    public PivotFilter(DataModelObject dataModelObject, String fieldName) {
        this.dataModelObject = dataModelObject;
        if (!dataModelObject.containsField(fieldName)) {
            throw new IllegalArgumentException("No such field " + fieldName + " on specified data model object.");
        }
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getOwnerName() {
        return this.dataModelObject.getName();
    }
    public String[] getOwnerLineage() {
        return this.dataModelObject.getLineage();
    }

    public FieldType getType() {
        return this.dataModelObject.getField(fieldName).getType();
    }

    public abstract JsonElement toJson();

    protected void addCommonFields(JsonObject root) {
        root.add("fieldName", new JsonPrimitive(this.fieldName));

        ArrayList<String> lineageList = new ArrayList<String>();
        for (String entry : this.getOwnerLineage()) {
            lineageList.add(entry);
        }
        root.add("owner", new JsonPrimitive(Util.join(".", lineageList)));
        root.add("type", new JsonPrimitive(this.getType().toString()));
    }
}
