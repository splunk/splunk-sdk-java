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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Abstract class specifying a calculation on a data model object.
 */
public abstract class Calculation {
    private final String[] ownerLineage;
    private final String calculationID;
    private final Map<String, Field> generatedFields;
    private final String comment;
    private final boolean editable;

    Calculation(String[] ownerLineage, String calculationID, Map<String, Field> generatedFields, String comment, boolean editable) {
        this.ownerLineage = ownerLineage;
        this.calculationID = calculationID;
        this.generatedFields = generatedFields;
        this.comment = comment;
        this.editable = editable;
    }

    /**
     * @return the ID of this calculation.
     */
    public String getCalculationID() { return this.calculationID; }

    /**
     * @return whether this calculation generated a field of the given name.
     */
    public boolean containsGeneratedField(String fieldName) {
        return this.generatedFields.containsKey(fieldName);
    }

    /**
     * @return a collection of the fields this calculation generates.
     */
    public Collection<Field> getGeneratedFields() { return this.generatedFields.values(); }

    /**
     * @param fieldName Name of the field to fetch.
     * @return a Field object.
     */
    public Field getGeneratedField(String fieldName) { return this.generatedFields.get(fieldName); }

    /**
     * @return the comment on this calculation (if one is specified) or null.
     */
    public String getComment() { return this.comment; }

    /**
     * Returns the name of the object on which this calculation is defined.
     * That need not be the one you accessed it from, as it may be inherited from
     * another data model object.
     *
     * @return The name of the object on which this calculation is defined.
     */
    public String getOwner() { return this.ownerLineage[this.ownerLineage.length-1]; }

    /**
     * Return the lineage of the data model object on which this calculation is
     * defined, starting with the most remote ancestor and ending with the data model object
     * on which this calculation is defined.
     *
     * @return an array of the names of data model objects.
     */
    public String[] getLineage() { return this.ownerLineage; }

    /**
     * @return whether this calculation can be edited, or it is a system defined calculation.
     */
    public boolean isEditable() { return this.editable; }


    public static Calculation parse(JsonElement json) {
        Calculation c;
        String type = null;
        String calculationId = null;
        String inputField = null;
        String comment = null;
        String expression = null;
        String lookupName = null;
        String lookupField = null;
        String[] owner = new String[0];
        boolean editable = false;
        Map<String, Field> outputFields = new HashMap<String, Field>();

        String key;
        for (Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            key = entry.getKey();
            if (key.equals("calculationType")) {
                type = entry.getValue().getAsString().toLowerCase();
            } else if (key.equals("calculationID")) {
                calculationId = entry.getValue().getAsString();
            } else if (key.equals("outputFields")) {
                for (JsonElement e : entry.getValue().getAsJsonArray()) {
                    Field f = Field.parse(e.getAsJsonObject());
                    outputFields.put(f.getName(), f);
                }
            } else if (key.equals("inputField")) {
                inputField = entry.getValue().getAsString();
            } else if (key.equals("comment")) {
                comment = entry.getValue().getAsString();
            } else if (key.equals("expression")) {
                expression = entry.getValue().getAsString();
            } else if (key.equals("lookupName")) {
                lookupName = entry.getValue().getAsString();
            } else if (key.equals("lookupField")) {
                lookupField = entry.getValue().getAsString();
            } else if (key.equals("owner")) {
                owner = entry.getValue().getAsString().split("\\.");
            } else if (key.equals("editable")) {
                editable = entry.getValue().getAsBoolean();
            }
        }

        if (type.equals("lookup")) {
            c = new LookupCalculation(owner, calculationId, outputFields, comment, editable, lookupName, lookupField, inputField);
        } else if (type.equals("geoip")) {
            c = new GeoIPCalculation(owner, calculationId, outputFields, comment, editable, inputField);
        } else if (type.equals("eval")) {
            c = new EvalCalculation(owner, calculationId, outputFields, comment, editable, expression);
        } else if (type.equals("rex")) {
            c = new RegexpCalculation(owner, calculationId, outputFields, comment, editable, inputField, expression);
        } else {
            throw new IllegalStateException("Unknown calculation type: " + type);
        }

        return c;
    }
}
