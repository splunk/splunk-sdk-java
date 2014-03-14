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

import java.net.Inet4Address;
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

    List<PivotColumn> columns = new ArrayList<PivotColumn>();
    List<PivotFilter> filters = new ArrayList<PivotFilter>();
    List<PivotCell> cells = new ArrayList<PivotCell>();
    List<PivotRow> rows = new ArrayList<PivotRow>();

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

}
