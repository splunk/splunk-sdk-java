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

public class RangePivotRowSplit extends PivotRowSplit {
    final int start, end, step, limit;

    public RangePivotRowSplit(DataModelObject dataModelObject, String field, String label,
                              int start, int end, int step, int limit) {
        super(dataModelObject, field, label);
        this.start = start;
        this.end = end;
        this.step = step;
        this.limit = limit;
    }

    public int getStart() { return this.start; }
    public int getEnd() { return this.end; }
    public int getStep() { return this.step; }
    public int getLimit() { return this.limit; }

    @Override
    public JsonElement toJson() {
        JsonObject root = new JsonObject();

        addCommonFields(root);

        JsonObject ranges = new JsonObject();
        ranges.add("start", new JsonPrimitive(start));
        ranges.add("end", new JsonPrimitive(end));
        ranges.add("size", new JsonPrimitive(step));
        ranges.add("maxNumberOf", new JsonPrimitive(limit));
        root.add("ranges", ranges);
        root.add("display", new JsonPrimitive("ranges"));

        return root;
    }
}
