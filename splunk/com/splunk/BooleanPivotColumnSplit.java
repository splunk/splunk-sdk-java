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

import com.google.gson.JsonObject;

/**
 * Represents a column split on a boolean valued field in a pivot.
 */
public class BooleanPivotColumnSplit extends PivotColumnSplit {
    private final String trueLabel, falseLabel;

    BooleanPivotColumnSplit(DataModelObject owner, String fieldName, String trueLabel, String falseLabel) {
        super(owner, fieldName);
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
    }

    @Override
    JsonObject toJson() {
        JsonObject root = new JsonObject();
        addCommonFields(root);

        root.addProperty("trueLabel", trueLabel);
        root.addProperty("falseLabel", falseLabel);

        return root;
    }
}
