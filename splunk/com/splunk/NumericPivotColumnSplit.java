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
import com.google.gson.JsonPrimitive;

public class NumericPivotColumnSplit extends PivotColumnSplit {
    NumericPivotColumnSplit(DataModelObject dataModelObject, String field) {
        super(dataModelObject, field);
    }

    @Override
    JsonObject toJson() {
        JsonObject root = new JsonObject();

        addCommonFields(root);
        root.add("display", new JsonPrimitive("all"));

        return root;
    }
}
