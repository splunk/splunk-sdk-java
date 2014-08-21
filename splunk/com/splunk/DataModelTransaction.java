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
 * Represents a datamodel object that inherits directly from BaseTransaction,
 * that is, an object that wraps a Splunk transaction. All children of this
 * object will appears as standard DataModelObject instances.
 */
public class DataModelTransaction extends DataModelObject {
    protected Collection<String> groupByFields;
    protected Collection<String> objectsToGroup;
    protected String maxSpan;
    protected String maxPause;

    DataModelTransaction(DataModel dataModel) {
        super(dataModel);
    }

    /**
     * @return the fields that will be used to group events into transactions.
     *
     * Contiguous events with identical values of the fields named in this collection
     * will be grouped into transactions.
     */
    public Collection<String> getGroupByFields() { return this.groupByFields; }

    /**
     * @return the names of the data model objects that should be unioned and split into transactions.
     */
    public Collection<String> getObjectsToGroup() { return this.objectsToGroup; }

    /**
     * maxSpan is the maximum amount of time (in a Splunk defined format, such as <tt>"1m"</tt>
     * for one minute or <tt>"20s"</tt> for twenty seconds) that a single transaction can span.
     * When a transaction reaches this size, it is automatically ended and a new transaction
     * begun.
     *
     * @return The maximum time that a transaction can span.
     */
    public String getMaxSpan() { return this.maxSpan; }

    /**
     * maxPause is the maximum amount of time between two events in a transaction, specified as a string
     * such as <tt>"1m"</tt> for one minute or <tt>"20s"</tt> for twenty seconds. When a transaction
     * has a gap of at least maxPause since its last even, it is ended and a new transaction begun.
     *
     * @return the maximum pause between events in a transaction.
     */
    public String getMaxPause() { return this.maxPause; }
}
