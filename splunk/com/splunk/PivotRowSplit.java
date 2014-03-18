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
import com.google.gson.JsonSerializer;

public abstract class PivotRowSplit {
    private final String fieldName;
    private final DataModelObject owner;
    private final String label;

    public PivotRowSplit(DataModelObject owner, String fieldName, String label) {
        this.fieldName = fieldName;
        this.owner = owner;
        this.label = label;
    }

    public String getFieldName() { return this.fieldName; }

    public DataModelObject getOwner() { return this.owner; }

    public String getLabel() { return this.label; }

    protected void addCommonFields(JsonObject obj) {
        Field field = this.owner.getField(this.fieldName);

        obj.add("fieldName", new JsonPrimitive(this.fieldName));
        obj.add("owner", new JsonPrimitive(Util.join(".", field.getOwnerLineage())));
        obj.add("type", new JsonPrimitive(field.getType().toString()));
        obj.add("label", new JsonPrimitive(this.label));
    }

    public abstract JsonElement toJson();
}
