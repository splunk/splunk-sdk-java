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

import java.util.Collection;

/**
 * Represents a field of a data model object.
 */
public class Field {
    private final Collection<Constraint> constraints;
    private final DataModelObject owner;
    private final String name;
    private final FieldType type;
    private final boolean required;
    private final boolean multivalued;
    private final boolean hidden;
    private final String displayName;
    private final String comment;

    public Field(String name, DataModelObject owner, FieldType type,
                 Collection<Constraint> constraints,
                 boolean isRequired, boolean isMultivalued, boolean isHidden,
                 String displayName, String comment) {
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.constraints = constraints;
        this.required = isRequired;
        this.multivalued = isMultivalued;
        this.hidden = isHidden;
        this.displayName = displayName;
        this.comment = comment;
    }

    /**
     * @return The name of this field.
     */
    public String getName() { return this.name; };

    /**
     * @return The DataModelObject that owns this field.
     */
    public DataModelObject getOwner() { return this.owner; }

    /**
     * @return The name of the DataModelObject that owns this field.
     */
    public String getOwnerName() { return getOwner().getName(); }

    /**
     * @return An array of names of DataModelObjects representing the lineage of this field's owner.
     */
    public String[] getOwnerLineage() { return getOwner().getLineage(); }

    /**
     * @return The type of this field.
     */
    public FieldType getType() { return this.type; }

    /**
     * @return Constraints that apply to this field.
     */
    public Collection<Constraint> getConstraints() { return constraints; }

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
}

