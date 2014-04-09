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
 * Represents an aggregate value to appear in the cells of a pivot.
 */
public class PivotCellValue {
    private final String fieldName;
    private final DataModelObject dataModelObject;
    private final String label;
    private final StatsFunction statsFunction;

    PivotCellValue(DataModelObject dataModelObject, String fieldName, String label, StatsFunction statsFunction) {
        this.fieldName = fieldName;
        this.dataModelObject = dataModelObject;
        this.label = label;
        this.statsFunction = statsFunction;

        if (!dataModelObject.containsField(fieldName)) {
            throw new IllegalArgumentException("No such field named " + fieldName + " on data model object.");
        }

        FieldType t = dataModelObject.getField(fieldName).getType();

        if ((t == FieldType.STRING || t == FieldType.IPV4) &&
                statsFunction != StatsFunction.LIST &&
                statsFunction != StatsFunction.DISTINCT_VALUES &&
                statsFunction != StatsFunction.FIRST &&
                statsFunction != StatsFunction.LAST &&
                statsFunction != StatsFunction.COUNT &&
                statsFunction != StatsFunction.DISTINCT_COUNT) {
            throw new IllegalArgumentException("Stats function on string and IPv4 field must be one of " +
                    "list, distinct_values, first, last, count, or distinct_count; found "
                    + statsFunction.toString()
            );
        } else if (t == FieldType.NUMBER &&
                statsFunction != StatsFunction.SUM &&
                statsFunction != StatsFunction.COUNT &&
                statsFunction != StatsFunction.AVERAGE &&
                statsFunction != StatsFunction.MAX &&
                statsFunction != StatsFunction.MIN &&
                statsFunction != StatsFunction.STDEV &&
                statsFunction != StatsFunction.LIST &&
                statsFunction != StatsFunction.DISTINCT_VALUES) {
            throw new IllegalArgumentException("Stats function on number field must be one of " +
                    "sum, count, average, max, min, stdev, list, or distinct_values; found "
                    + statsFunction.toString()
            );
        } else if (t == FieldType.TIMESTAMP &&
                statsFunction != StatsFunction.DURATION &&
                statsFunction != StatsFunction.EARLIEST &&
                statsFunction != StatsFunction.LATEST &&
                statsFunction != StatsFunction.LIST &&
                statsFunction != StatsFunction.DISTINCT_VALUES) {
            throw new IllegalArgumentException("Stats function on timestamp field must be one of " +
                    "duration, earliest, latest, list, or distinct_values; found "
                    + statsFunction.toString()
            );
        } else if ((t == FieldType.CHILDCOUNT || t == FieldType.OBJECTCOUNT) &&
                statsFunction != StatsFunction.COUNT) {
            throw new IllegalArgumentException("Stats function on childcount and objectcount fields must be count; " +
                    "; found " + statsFunction.toString()
            );
        } else if (t == FieldType.BOOLEAN) {
            throw new IllegalArgumentException("Cannot use boolean valued fields as cell values.");
        }
    }

    /**
     * @return the name of the field to aggregate on.
     */
    public String getFieldName() { return this.fieldName; }

    /**
     * @return the data model object this pivot is operating on.
     */
    public DataModelObject getOwner() { return this.dataModelObject; }

    /**
     * @return a human readable name for this aggregate.
     */
    public String getLabel() { return this.label; }

    /**
     * @return the function used for aggregation.
     */
    public StatsFunction getStatsFunction() { return this.statsFunction; }

    /**
     * @return a JSON serialization of this object.
     */
    JsonObject toJson() {
        JsonObject root = new JsonObject();

        DataModelField f = this.dataModelObject.getField(fieldName);

        root.addProperty("fieldName", this.fieldName);
        root.addProperty("owner", Util.join(".", f.getOwnerLineage()));
        root.addProperty("type", f.getType().toString());
        root.addProperty("label", this.label);
        root.addProperty("sparkline", false); // Not properly implemented in core yet.
        root.addProperty("value", this.statsFunction.toString());

        return root;
    }
}
