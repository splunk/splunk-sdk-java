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
 * Represents a filter on a boolean valued field in a pivot.
 */
public class BooleanPivotFilter extends PivotFilter {
    private final BooleanComparison comparison;
    private final boolean comparisonValue;

    BooleanPivotFilter(DataModelObject dataModelObject, String fieldName,
                              BooleanComparison comparison, boolean comparisonValue) {
        super(dataModelObject, fieldName);
        if (dataModelObject.getField(fieldName).getType() != FieldType.BOOLEAN) {
            throw new IllegalArgumentException("Field " + fieldName + " on the data model object was of type "
                    + dataModelObject.getField(fieldName).getType().toString() + ", expected boolean.");
        }
        this.comparison = comparison;
        this.comparisonValue = comparisonValue;
    }

    @Override
    JsonElement toJson() {
        JsonObject root = new JsonObject();

        addCommonFields(root);

        root.add("comparator", new JsonPrimitive(this.comparison.toString()));
        root.add("compareTo", new JsonPrimitive(this.comparisonValue));

        return root;
    }
}
