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

import java.util.Map.Entry;

/**
 * Represents a field of a data model object.
 */
public class DataModelField {
    private String[] ownerLineage;
    private String name;
    private FieldType type;
    private boolean required;
    private boolean multivalued;
    private boolean hidden;
    private String displayName;
    private String comment;
    private boolean editable;
    private String fieldSearch;

    private DataModelField() {}

    /**
     * @return a search query fragment for this field.
     */
    public String getFieldSearch() { return this.fieldSearch; }

    /**
     * @return The name of this field.
     */
    public String getName() { return this.name; };

    /**
     * Return the name of the data model object on which this field is defined. That need not
     * be the data model object you accessed it from. It can be one of its ancestors.
     *
     * @return The name of the DataModelObject that owns this field.
     */
    public String getOwnerName() { return this.ownerLineage[this.ownerLineage.length-1]; }

    /**
     * Return the lineage of the data model object on which this field is defined. That need not
     * be the data model object you accessed it from. It can be one of its ancestors.
     *
     * @return An array of names of DataModelObjects representing the lineage of this field's owner.
     */
    public String[] getOwnerLineage() { return this.ownerLineage; }

    /**
     * @return The type of this field.
     */
    public FieldType getType() { return this.type; }

    /**
     * Some fields are part of system objects such as BaseEvent or are part
     * of the structure of the object, such as a field with the same name
     * as the object. Those fields cannot be edited. This method returns
     * whether the field is one of these protected fields.
     *
     * @return whether this field can be edited.
     */
    public boolean isEditable() { return editable; }

    /**
     * @return whether this field is required on events in the object.
     */
    public boolean isRequired() { return required; }

    /**
     * @return whether this field is can be multivalued.
     */
    public boolean isMultivalued() { return multivalued; }

    /**
     * @return whether this field should be displayed in a data model UI.
     */
    public boolean isHidden() { return hidden; }

    /**
     * @return a human readable name for this field.
     */
    public String getDisplayName() { return displayName; }

    /**
     * @return a comment on this field (if there is one), or null.
     */
    public String getComment() { return comment; }

    public static DataModelField parse(JsonElement fieldJson) {
        DataModelField field = new DataModelField();

        for (Entry<String, JsonElement> entry : fieldJson.getAsJsonObject().entrySet()) {
            if (entry.getKey().equals("fieldName")) {
                field.name = entry.getValue().getAsString();
            } else if (entry.getKey().equals("owner")) {
                field.ownerLineage = entry.getValue().getAsString().split("\\.");
            } else if (entry.getKey().equals("type")) {
                field.type = FieldType.parseType(entry.getValue().getAsString());
            } else if (entry.getKey().equals("required")) {
                field.required = entry.getValue().getAsBoolean();
            } else if (entry.getKey().equals("multivalue")) {
                field.multivalued = entry.getValue().getAsBoolean();
            } else if (entry.getKey().equals("hidden")) {
                field.hidden = entry.getValue().getAsBoolean();
            } else if (entry.getKey().equals("displayName")) {
                field.displayName = entry.getValue().getAsString();
            } else if (entry.getKey().equals("comment")) {
                field.comment = entry.getValue().getAsString();
            } else if (entry.getKey().equals("editable")) {
                field.editable = entry.getValue().getAsBoolean();
            } else if (entry.getKey().equals("fieldSearch")) {
                field.fieldSearch = entry.getValue().getAsString();
            }
        }

        return field;
    }
}

