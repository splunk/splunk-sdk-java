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

/**
 * Represents a row split on a boolean valued field in a pivot.
 */
public class BooleanPivotRowSplit extends PivotRowSplit {
    private final String falseDisplayValue;
    private final String trueDisplayValue;

    BooleanPivotRowSplit(DataModelObject dataModelObject, String field, String label,
                                String trueDisplayValue, String falseDisplayValue) {
        super(dataModelObject, field, label);
        this.trueDisplayValue = trueDisplayValue;
        this.falseDisplayValue = falseDisplayValue;
    }

    public String getTrueDisplayValue() { return this.trueDisplayValue; }
    public String getFalseDisplayValue() { return this.falseDisplayValue; }

    @Override
    JsonElement toJson() {
        JsonObject root = new JsonObject();

        addCommonFields(root);

        root.add("trueLabel", new JsonPrimitive(this.trueDisplayValue));
        root.add("falseLabel", new JsonPrimitive(this.falseDisplayValue));

        return root;
    }
}
