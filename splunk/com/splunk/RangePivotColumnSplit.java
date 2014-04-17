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

/**
 * Split values of a field into rows by ranges of a numeric field.
 */
public class RangePivotColumnSplit extends PivotColumnSplit {
    private final Integer start, end, step, limit;

    RangePivotColumnSplit(DataModelObject dataModelObject, String field,
                                 Integer start, Integer end, Integer step, Integer limit) {
        super(dataModelObject, field);
        this.start = start;
        this.end = end;
        this.step = step;
        this.limit = limit;
    }

    /**
     * @return the value of the start of the lowest range, or null if not specified.
     */
    public Integer getStart() { return this.start; }

    /**
     * @return the value of the end of the highest range, or null if not specified.
     */
    public Integer getEnd() { return this.end; }

    /**
     * @return the width of each range, or null if not specified.
     */
    public Integer getStep() { return this.step; }

    /**
     * @return the maximum number of ranges to split into, or null if no limit.
     */

    @Override
    JsonObject toJson() {
        JsonObject root = new JsonObject();

        addCommonFields(root);

        JsonObject ranges = new JsonObject();
        // In Splunk 6.0.1.1, data models incorrectly expect strings for these fields
        // instead of numbers. In 6.1, this is fixed and both are accepted.
        if (start != null) ranges.add("start", new JsonPrimitive(start.toString()));
        if (end != null) ranges.add("end", new JsonPrimitive(end.toString()));
        if (step != null) ranges.add("size", new JsonPrimitive(step.toString()));
        if (limit != null) ranges.add("maxNumberOf", new JsonPrimitive(limit.toString()));
        root.add("ranges", ranges);
        root.add("display", new JsonPrimitive("ranges"));

        return root;
    }
}
