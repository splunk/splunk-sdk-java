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

import com.google.gson.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * PivotSpecification specifies
 */
public class PivotSpecification {
    private static GsonBuilder gson = new GsonBuilder();

    private DataModelObject dataModelObject;
    String namespace = null;

    List<PivotColumnSplit> columns = new ArrayList<PivotColumnSplit>();
    List<PivotFilter> filters = new ArrayList<PivotFilter>();
    List<PivotCell> cells = new ArrayList<PivotCell>();
    List<PivotRowSplit> rows = new ArrayList<PivotRowSplit>();

    public PivotSpecification(DataModelObject dataModelObject) {
        this.dataModelObject = dataModelObject;
        this.namespace = dataModelObject.getDataModel().getName();
    }

    public void setAccelerationNamespace(String namespace) {
        if (namespace == null) {
            throw new IllegalArgumentException("Namespace to use for acceleration must not be null.");
        } else {
            this.namespace = namespace;
        }
    }

    public void setAccelerationJob(String sid) {
        if (sid == null) {
            throw new IllegalArgumentException("Sid to use for acceleration must not be null.");
        } else {
            this.namespace = "sid=" + sid;
        }
    }

    public void setAccelerationJob(Job job) {
        setAccelerationJob(job.getSid());
    }

    public String getNamespace() {
        return this.namespace;
    }

    public PivotSpecification addFilter(String field, BooleanComparison comparison, boolean compareTo) {
        if (!dataModelObject.containsField(field)) {
            throw new IllegalArgumentException("No such field " + field);
        }
        if (dataModelObject.getField(field).getType() != FieldType.BOOLEAN) {
            throw new IllegalArgumentException("Expected a field of type boolean, found "
                    + dataModelObject.getField(field).getType().toString());
        }
        BooleanPivotFilter filter = new BooleanPivotFilter(this.dataModelObject, field, comparison, compareTo);

        filters.add(filter);

        return this;
    }

    public PivotSpecification addFilter(String field, StringComparison comparison, String comparisonValue) {
        if (!dataModelObject.containsField(field)) {
            throw new IllegalArgumentException("No such field " + field);
        }
        if (dataModelObject.getField(field).getType() != FieldType.STRING) {
            throw new IllegalArgumentException("Expected a field of type string, found "
                    + dataModelObject.getField(field).getType().toString());
        }
        StringPivotFilter filter = new StringPivotFilter(this.dataModelObject, field, comparison, comparisonValue);
        filters.add(filter);

        return this;
    }

    public PivotSpecification addFilter(String field, IPv4Comparison comparison, String comparisonValue) {
        if (!dataModelObject.containsField(field)) {
            throw new IllegalArgumentException("No such field " + field);
        }
        if (dataModelObject.getField(field).getType() != FieldType.IPV4) {
            throw new IllegalArgumentException("Expected a field of type ipv4, found "
                    + dataModelObject.getField(field).getType().toString());
        }
        IPv4PivotFilter filter = new IPv4PivotFilter(this.dataModelObject, field, comparison, comparisonValue);
        filters.add(filter);

        return this;
    }

    public PivotSpecification addFilter(String field, NumberComparison comparison, double comparisonValue) {
        if (!dataModelObject.containsField(field)) {
            throw new IllegalArgumentException("No such field " + field);
        }
        if (dataModelObject.getField(field).getType() != FieldType.NUMBER) {
            throw new IllegalArgumentException("Expected a field of type number, found "
                    + dataModelObject.getField(field).getType().toString());
        }
        NumberPivotFilter filter = new NumberPivotFilter(this.dataModelObject, field, comparison, comparisonValue);
        filters.add(filter);

        return this;
    }

    public PivotSpecification addFilter(String field, String sortAttribute,
                                        SortDirection sortDirection, int limit, StatsFunction statsFunction) {
        if (!dataModelObject.containsField(field)) {
            throw new IllegalArgumentException("No such field " + field);
        }
        if (dataModelObject.getField(field).getType() != FieldType.NUMBER) {
            throw new IllegalArgumentException("Expected a field of type number, found "
                    + dataModelObject.getField(field).getType().toString());
        }

        if (!dataModelObject.containsField(sortAttribute)) {
            throw new IllegalArgumentException("No such field " + sortAttribute);
        }

        LimitPivotFilter filter = new LimitPivotFilter(this.dataModelObject, field, sortAttribute,
                sortDirection, limit, statsFunction);
        filters.add(filter);

        return this;
    }

    /*
   * These methods add row splits. In all cases, if the type of field the method applies
   * to as specified in its comments doesn't match the type of the field specified by 'field',
   * the method throws an IllegalArgumentException.
   */

    /**
     * Numeric field, display=all
     */
    public PivotSpecification addRowSplit(String field, String label) {
        FieldType t = this.dataModelObject.getField(field).getType();
        PivotRowSplit split;
        if (t == FieldType.NUMBER) {
            split = new NumberPivotRowSplit(this.dataModelObject, field, label);
        } else if (t == FieldType.STRING) {
            split = new StringPivotRowSplit(this.dataModelObject, field, label);
        } else {
            throw new IllegalArgumentException("Expected a field of type number or string; found type " + t.toString());
        }

        rows.add(split);
        return this;
    }

    /**
     * Numeric field, display=ranges, generates bins with edges equivalent to the
     * classic loop 'for i in <start> to <end> by <step>' but with a maximum
     * number of bins <limit>. This dispatches to the stats and xyseries search commands.
     */
    public PivotSpecification addRowSplit(String field, String label, int start, int end, int step, int limit) {
        FieldType t = this.dataModelObject.getField(field).getType();
        if (t != FieldType.NUMBER) {
            throw new IllegalArgumentException("Expected a field of type number; found type " + t.toString());
        }

        PivotRowSplit split = new RangePivotRowSplit(this.dataModelObject, field, label, start, end, step, limit);
        rows.add(split);

        return this;
    }

    /**
     * Boolean field. trueDisplayValue and falseDisplayValue are the strings to be shown in the results
     * when the field is true or false, respectively.
     */
    public PivotSpecification addRowSplit(String field, String label, String trueDisplayValue, String falseDisplayValue) {
        FieldType t = this.dataModelObject.getField(field).getType();
        if (t != FieldType.BOOLEAN) {
            throw new IllegalArgumentException("Expected a field of type boolean; found type " + t.toString());
        }

        PivotRowSplit split = new BooleanPivotRowSplit(this.dataModelObject, field, label,
                trueDisplayValue, falseDisplayValue);
        rows.add(split);

        return this;
    }

    /**
     * Timestamp field. binning is the size of buckets to use.
     */
    public PivotSpecification addRowSplit(String field, String label, TimestampBinning binning) {
        FieldType t = this.dataModelObject.getField(field).getType();
        if (t != FieldType.TIMESTAMP) {
            throw new IllegalArgumentException("Expected a field of type timestamp; found type " + t.toString());
        }

        PivotRowSplit split = new TimestampPivotRowSplit(this.dataModelObject, field, label, binning);
        rows.add(split);

        return this;
    }

    public PivotSpecification addColumnSplit(String field) {
        FieldType t = this.dataModelObject.getField(field).getType();
        PivotColumnSplit split;

        if (t == FieldType.NUMBER) {
            split = new NumericPivotColumnSplit(this.dataModelObject, field);
        } else if (t == FieldType.STRING) {
            split = new StringPivotColumnSplit(this.dataModelObject, field);
        } else {
            throw new IllegalArgumentException("Expected a field of type number or string; found type " + t.toString());
        }

        columns.add(split);
        return this;
    }

    public PivotSpecification addColumnSplit(String field, int start, int end, int step, int limit) {
        FieldType t = this.dataModelObject.getField(field).getType();

        if (t != FieldType.NUMBER) {
            throw new IllegalArgumentException("Expected a field of type number; found type " + t.toString());
        }

        PivotColumnSplit split = new RangePivotColumnSplit(this.dataModelObject, field, start, end, step, limit);

        columns.add(split);
        return this;
    }

    public PivotSpecification addColumnSplit(String field, String trueDisplayValue, String falseDisplayValue) {
        FieldType t = this.dataModelObject.getField(field).getType();

        if (t != FieldType.BOOLEAN) {
            throw new IllegalArgumentException("Expected a field of type boolean; found type " + t.toString());
        }

        PivotColumnSplit split = new BooleanPivotColumnSplit(this.dataModelObject, field,
                trueDisplayValue, falseDisplayValue);

        columns.add(split);
        return this;
    }

    public PivotSpecification addColumnSplit(String field, TimestampBinning binning) {
        FieldType t = this.dataModelObject.getField(field).getType();

        if (t != FieldType.TIMESTAMP) {
            throw new IllegalArgumentException("Expected a field of type timestamp; found type " + t.toString());
        }

        PivotColumnSplit split = new TimestampPivotColumnSplit(this.dataModelObject, field, binning);

        columns.add(split);
        return this;
    }

    public JsonElement getDescription() {
        JsonObject root = new JsonObject();

        root.add("dataModel", new JsonPrimitive(this.dataModelObject.getDataModel().getName()));
        root.add("baseClass", new JsonPrimitive(this.dataModelObject.getName()));

        JsonArray filterArray = new JsonArray();
        for (PivotFilter filter : filters) {
            filterArray.add(filter.toJson());
        }
        root.add("filters", filterArray);

        return root;
    }

    public Collection<PivotFilter> getFilters() { return this.filters; }
    public Collection<PivotRowSplit> getRowSplits() { return this.rows; }
    public Collection<PivotColumnSplit> getColumnSplits() { return this.columns; }

}
