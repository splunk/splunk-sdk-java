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

import java.util.Map.Entry;

/**
 * Represents a constraint on a data model object or a field on a data model object.
 */
public class DataModelConstraint {
    private String owner;
    private String query;

    private DataModelConstraint() {
    }

    /**
     * @return The Splunk search query this constraint specifies.
     */
    public String getQuery() { return this.query; }


    /**
     * @return The DataModelObject that owns this field.
     */
    public String getOwner() { return this.owner; }

    /**
     * Parse a DataModelConstraint object out of JSON.
     *
     * @param json JsonElement to parse.
     * @return a DataModelConstraint object.
     */
    static DataModelConstraint parse(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();
        DataModelConstraint constraint = new DataModelConstraint();

        for (Entry<String, JsonElement> e : jsonObject.entrySet()) {
            if (e.getKey().equals("owner")) {
                constraint.owner = e.getValue().getAsString();
            } else if (e.getKey().equals("search")) {
                constraint.query = e.getValue().getAsString();
            }
        }

        return constraint;
    }
}
