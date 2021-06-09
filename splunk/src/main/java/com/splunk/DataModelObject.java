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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.*;
import java.util.Map.Entry;

/**
 * DataModelObject represents one of the structured views in a data model.
 */
public class DataModelObject {
    private DataModel model;
    private String name;
    private String[] lineage;
    private String displayName;
    private String parentName;

    private Map<String, DataModelField> autoextractedFields;
    private Collection<DataModelConstraint> constraints;
    private Map<String, DataModelCalculation> calculations;

    protected DataModelObject(DataModel model) {
        this.model = model;
    }

    /**
     * Checks whether there is a field with the given name in this
     * data model object.
     *
     * @param fieldName name of the field to check for.
     * @return true if there is such a field; false otherwise.
     */
    public boolean containsField(String fieldName) {
        if (autoextractedFields.containsKey(fieldName)) {
            return true;
        }
        for (DataModelCalculation c : calculations.values()) {
            if (c.containsGeneratedField(fieldName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Local acceleration is tsidx acceleration of a data model object that is handled
     * manually by a user. You create a job which generates an index, and then use that
     * index in your pivots on the data model object.
     *
     * The namespace created by the job is 'sid={sid}' where {sid} is the job's sid. You
     * would use it in another job by starting your search query with
     *
     *     | tstats ... from sid={sid} | ...
     *
     * The tsidx index created by this job is deleted when the job is garbage collected
     * by Splunk.
     *
     * It is the user's responsibility to manage this job, including cancelling it.
     *
     * @return a Job writing a tsidx index.
     */
    public Job createLocalAccelerationJob() {
        return createLocalAccelerationJob(null);
    }

    /**
     * Local acceleration is tsidx acceleration of a data model object that is handled
     * manually by a user. You create a job which generates an index, and then use that
     * index in your pivots on the data model object.
     *
     * The namespace created by the job is 'sid={sid}' where {sid} is the job's sid. You
     * would use it in another job by starting your search query with
     *
     *     | tstats ... from sid={sid} | ...
     *
     * The tsidx index created by this job is deleted when the job is garbage collected
     * by Splunk.
     *
     * It is the user's responsibility to manage this job, including cancelling it.
     *
     * @param earliestTime A time modifier (e.g., "-2w") setting the earliest time to index.
     * @return a Job writing a tsidx index.
     */
    public Job createLocalAccelerationJob(String earliestTime) {
        String query = "| datamodel " + this.model.getName() + " " +
                this.getName() + " search | tscollect";
        JobArgs args = new JobArgs();
        if (earliestTime != null) {
            args.setEarliestTime(earliestTime);
        }
        return this.model.getService().search(query, args);
    }

    /**
     * Return the calculations done by this data model object to produce fields.
     *
     * Each calculation has a unique ID assigned to it by splunkd, which is the key
     * in the returned map. For most purposes you will probably only want the values.
     *
     * @return a map of calculation IDs to DataModelCalculation objects.
     */
    public Map<String, DataModelCalculation> getCalculations() {
        return Collections.unmodifiableMap(this.calculations);
    }

    /**
     * Fetch a calculation by its unique ID.
     *
     * @param calculationId a splunkd assigned unique ID for this calculation.
     * @return a DataModelCalculation object.
     */
    public DataModelCalculation getCalculation(String calculationId) {
        return this.calculations.get(calculationId);
    }

    /**
     * @return a collection of the constraints limiting events that will appear in this data model object.
     */
    public Collection<DataModelConstraint> getConstraints() {
        return Collections.unmodifiableCollection(this.constraints);
    }

    /**
     * Fetch the data model on which this object is defined.
     *
     * @return A DataModel instance containing this object.
     */
    public DataModel getDataModel() {
        return this.model;
    }

    /**
     * @return the human readable name of this data model object.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Fetch a single field of a given name from this data model object.
     *
     * @param fieldName Name of the field to fetch.
     * @return A DataModelField object, or null if there is no field of the given name.
     */
    public DataModelField getField(String fieldName) {
        if (autoextractedFields.containsKey(fieldName)) {
            return autoextractedFields.get(fieldName);
        }
        for (DataModelCalculation c : this.calculations.values()) {
            if (c.containsGeneratedField(fieldName)) {
                return c.getGeneratedField(fieldName);
            }
        }
        return null;
    }

    /**
     * Get a collection of objects specifying all the fields that were automatically extracted
     * from events (as opposed to generated by calculations in a data model).
     *
     * @return a collection of DataModelField objects.
     */
    public Collection<DataModelField> getAutoExtractedFields() {
        return Collections.unmodifiableCollection(autoextractedFields.values());
    }

    /**
     * Return all the fields, whether input or created by calculations.
     * @return a collection of DataModelField objects.
     */
    public Collection<DataModelField> getFields() {
        Collection<DataModelField> fields = new ArrayList<DataModelField>();
        fields.addAll(this.autoextractedFields.values());
        for (DataModelCalculation c : this.calculations.values()) {
            fields.addAll(c.getGeneratedFields());
        }
        return fields;
    }

    public String getQuery() {
        return "| datamodel " + this.getDataModel().getName() + " " + this.getName() + " search";
    }

    /**
     * @return Splunk's identifier for this data model object.
     */
    public String getName() { return this.name; }

    /**
     * Data model objects can inherit from other data model objects
     * in the same data model (or from a couple of global base objects
     * such as BaseEvent and BaseTransaction). The lineage is a list of
     * data model object names tracing this inheritance, starting with the
     * most remote ancestor and ending with this object.
     *
     * @return An array of names, starting with this object's name, followed by
     * the names up the hierarchy.
     */
    public String[] getLineage() { return this.lineage; }

    /**
     * Returns the name of the parent of this object.
     *
     * @return a String giving the name.
     */
    public String getParentName() {
        return this.parentName;
    }

    /**
     * @return the data model object this one inherits from if it is a user defined data model object
     * in the same data model; otherwise returns null (for example if the data model object inherits from BaseEvent
     * or BaseTransaction).
     */
    public DataModelObject getParent() {
        return this.getDataModel().getObject(this.parentName);
    }

    /**
     * Create a PivotSpecification on this data model object.
     *
     * @return a PivotSpecification instance.
     */
    public PivotSpecification createPivotSpecification() {
        return new PivotSpecification(this);
    }

    /**
     * Start a job that fetches all the events of this data model object.
     *
     * @return a Job object.
     */
    public Job runQuery() {
        return runQuery("", null);
    }

    /**
     * Start a job that fetches all the events of this data model object.
     *
     * @param args arguments specifying behavior of the job.
     * @return a Job object.
     */
    public Job runQuery(JobArgs args) {
        return runQuery("", args);
    }

    /**
     * Start a job that applies querySuffix to all the events in this data model object.
     *
     * @param querySuffix a search query, starting with a '|' that will be appended to the command to fetch
     *                    the contents of this data model object (e.g., "| head 3").
     * @return a Job object.
     */
    public Job runQuery(String querySuffix) {
        return runQuery(querySuffix, null);
    }

    /**
     * Start a job that applies querySuffix to all the events in this data model object.
     *
     * @param querySuffix a search query, starting with a '|' that will be appended to the command to fetch
     *                    the contents of this data model object (e.g., "| head 3").
     * @param args arguments to control the job.
     * @return a Job object.
     */
    public Job runQuery(String querySuffix, JobArgs args) {
        return getDataModel().getService().search(getQuery() + querySuffix, args);
    }

    /**
     * Produce a data model object from a JSON dictionary specifying it plus a data model that contains it.

     * @param dataModel a DataModel instance that contains this data model object.
     * @param object a JsonElement (as produced by Gson) specifying this data model object (usually one of
     *               the entries in the array of objects in the JSON description of the data model).
     * @return a DataModelObject instance.
     */
    static DataModelObject parse(DataModel dataModel, JsonElement object) {
        String name = null;
        String displayName = null;
        String comment = null;
        String[] lineage = new String[0];
        String parentName = null;
        Map<String, DataModelField> fields = new HashMap<String, DataModelField>();
        Collection<String> children = new ArrayList<String>();
        Collection<DataModelConstraint> constraints = new ArrayList<DataModelConstraint>();
        Map<String, DataModelCalculation> calculations = new HashMap<String, DataModelCalculation>();

        // Fields specific to objects inheriting directly from BaseSearch.
        String baseSearch = null;
        // Fields specific to objects inheriting directly from BaseTransaction
        String transactionMaxPause = null;
        String transactionMaxTimeSpan = null;
        Collection<String> groupByFields = new ArrayList<String>();
        Collection<String> objectsToGroup = new ArrayList<String>();

        for (Entry<String, JsonElement> entry : object.getAsJsonObject().entrySet()) {
            if (entry.getKey().equals("objectName")) {
                name = entry.getValue().getAsString();
            } else if (entry.getKey().equals("displayName")) {
                displayName = entry.getValue().getAsString();
            } else if (entry.getKey().equals("lineage")) {
                lineage = entry.getValue().getAsString().split("\\.");
            } else if (entry.getKey().equals("parentName")) {
                parentName = entry.getValue().getAsString();
            } else if (entry.getKey().equals("fields")) {
                JsonArray fieldsJson = entry.getValue().getAsJsonArray();
                fields.clear();

                for (JsonElement fieldJson : fieldsJson) {
                    DataModelField field = DataModelField.parse(fieldJson);
                    fields.put(field.getName(), field);
                }
            } else if (entry.getKey().equals("constraints")) {
                JsonArray constraintsJson = entry.getValue().getAsJsonArray();

                for (JsonElement constraintJson : constraintsJson) {
                    DataModelConstraint constraint = DataModelConstraint.parse(constraintJson);
                    constraints.add(constraint);
                }
            } else if (entry.getKey().equals("calculations")) {
                calculations.clear();
                for (JsonElement cjson : entry.getValue().getAsJsonArray()) {
                    DataModelCalculation c = DataModelCalculation.parse(cjson);
                    String cid = c.getCalculationID();
                    calculations.put(cid, c);
                }
            } else if (entry.getKey().equals("baseSearch")) {
                baseSearch = entry.getValue().getAsString();
            } else if (entry.getKey().equals("transactionMaxPause")) {
                transactionMaxPause = entry.getValue().getAsString();
            } else if (entry.getKey().equals("transactionMaxTimeSpan")) {
                transactionMaxTimeSpan = entry.getValue().getAsString();
            } else if (entry.getKey().equals("groupByFields")) {
                for (JsonElement e : entry.getValue().getAsJsonArray()) {
                    groupByFields.add(e.getAsString());
                }
            } else if (entry.getKey().equals("objectsToGroup")) {
                for (JsonElement e : entry.getValue().getAsJsonArray()) {
                    objectsToGroup.add(e.getAsString());
                }
            }
        }

        DataModelObject dmo;
        // Create the right subclass of DataModelObject.
        if (baseSearch != null) {
            dmo = new DataModelSearch(dataModel);
        } else if (transactionMaxPause != null) {
            dmo = new DataModelTransaction(dataModel);
        } else {
            dmo = new DataModelObject(dataModel);
        }

        // Set the fields common to all data model objects
        dmo.name = name;
        dmo.displayName = displayName;
        dmo.lineage = lineage;
        dmo.parentName = parentName;
        dmo.autoextractedFields = fields;
        dmo.constraints = constraints;
        dmo.calculations = calculations;

        // Set the fields of particular types
        if (baseSearch != null) {
            ((DataModelSearch)dmo).baseSearch = baseSearch;
        } else if (transactionMaxPause != null) {
            ((DataModelTransaction)dmo).groupByFields = groupByFields;
            ((DataModelTransaction)dmo).objectsToGroup = objectsToGroup;
            ((DataModelTransaction)dmo).maxPause = transactionMaxPause;
            ((DataModelTransaction)dmo).maxSpan = transactionMaxTimeSpan;
        } else {
            // Has no additional fields
        }

        return dmo;
    }
}
