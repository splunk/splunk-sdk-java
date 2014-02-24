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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DataModelObject {
    private String comment;
    private DataModel model;
    private String name;
    private String[] lineage;
    private String displayName;
    private String parentName;
    private Collection<String> children;

    private Map<String, Field> fields;
    private Collection<Constraint> constraints;
    private Map<String, Calculation> calculations;

    DataModelObject(DataModel model) {
        this.model = model;
        this.fields = new HashMap<String, Field>();
        this.children = new ArrayList<String>();
        this.constraints = new ArrayList<Constraint>();
        this.calculations = new HashMap<String, Calculation>();
    }

    /**
     * Checks whether there is a field with the given name in this
     * data model object.
     *
     * @param fieldName name of the field to check for.
     * @return true if there is such a field; false otherwise.
     */
    public boolean containsField(String fieldName) {
        return fields.containsKey(fieldName);
    }

    /**
     * Local acceleration is tsidx acceleration of a data model object that is handled
     * manually by a user. You create a job which generates an index, and then use that
     * index in your pivots on the data model object.
     *
     * The namespace to this job is where the tsidx index will be stored. It should be
     * unique, or multiple jobs may clobber each other.
     *
     * When you have created a job using this method, then you can generate an accelerated
     * pivot by passing the same namespace to the pivot method.
     *
     * It is the user's responsibility to manage this job, including cancelling it.
     *
     * @param namespace The tsidx namespace to create.
     * @return a Job writing a tsidx index.
     */
    public Job createLocalAccelerationJob(String namespace) {
        return createLocalAccelerationJob(namespace, null);
    }

    /**
     * Local acceleration is tsidx acceleration of a data model object that is handled
     * manually by a user. You create a job which generates an index, and then use that
     * index in your pivots on the data model object.
     *
     * The namespace to this job is where the tsidx index will be stored. It should be
     * unique, or multiple jobs may clobber each other.
     *
     * When you have created a job using this method, then you can generate an accelerated
     * pivot by passing the same namespace to the pivot method.
     *
     * It is the user's responsibility to manage this job, including cancelling it.
     *
     * @param namespace The tsidx namespace to create.
     * @param earliestTime A time modifier (e.g., "-2w") setting the earliest time to index.
     * @return a Job writing a tsidx index.
     */
    public Job createLocalAccelerationJob(String namespace, String earliestTime) {
        String query = "| datamodel " + this.model.getName() + " " +
                this.getName() + " search | tscollect namespace=" + namespace;
        JobArgs args = new JobArgs();
        if (earliestTime != null) {
            args.setEarliestTime(earliestTime);
        }
        return this.model.getService().getJobs().create(query, args);
    }

    /**
     * Get the tsidx namespace that this object's containing data model uses for
     * global acceleration. Even if there is no acceleration on the model, this method
     * will still return a tsidx namespace.
     *
     * @return the global acceleration namespace of the data model containing this object.
     */
    public String getAccelerationNamespace() {
        return getDataModel().getAccelerationNamespace();
    }

    /**
     * Return a Splunk query that will produce the events in this data model object, using
     * global acceleration if possible.
     *
     * @return a String containing a search query.
     */
    public String getAcceleratedQuery() {
        if (this.getDataModel().isAccelerated()) {
            return "| tstats namspace=" + this.getAccelerationNamespace();
        } else {
            return "| datamodel " + this.model.getName() + " " + this.getName() + " search";
        }
    }

    /**
     * Return a Splunk query that will produce events in this data model from the specified
     * tsidx namespace (which is assumed to be generated by a job created by the
     * createLocalAccelerationJob method).
     *
     * @param namespace The tsidx namespace to generate a query on.
     * @return A String containing a search query.
     */
    public String getAcceleratedQuery(String namespace) {
        return "| tstats namespace=" + namespace;
    }

    public Map<String, Calculation> getCalculations() {
        return this.calculations;
    }

    public Calculation getCalcualtion(String calculationId) {
        return this.calculations.get(calculationId);
    }

    public Collection<DataModelObject> getChildren() {
        Collection<DataModelObject> children = new ArrayList<DataModelObject>();

        DataModel model = this.getDataModel();
        for (String childName : getChildrenNames()) {
            children.add(model.getObject(childName));
        }

        return children;
    }

    /**
     * @return a collection of string giving the names of all children of this object in the data model.
     */
    public Collection<String> getChildrenNames() {
        return children;
    }

    /**
     * @return a human readable comment on this object.
     */
    public String getComment() {
        return this.comment;
    }

    public Collection<Constraint> getConstraints() {
        return this.constraints;
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
     * @return A Field object, or null if there is no field of the given name.
     */
    public Field getField(String fieldName) {
        return fields.get(fieldName);
    }

    /**
     * Get a collection of objects specifying all the fields that defined on events
     * produced by this data model object.
     *
     * @return a collection of Field objects.
     */
    public Collection<Field> getFields() {
        return fields.values();
    }

    public String getQuery() {
        return "| datamodel " + this.model.getName() + " " + this.getName() + " search";
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

    public Job runQuery() {
        return runQuery("", null);
    }

    public Job runQuery(JobArgs args) {
        return runQuery("", args);
    }

    public Job runQuery(String querySuffix) {
        return runQuery(querySuffix, null);
    }

    public Job runQuery(String querySuffix, JobArgs args) {
        return getDataModel().getService().getJobs().create(getAcceleratedQuery() + querySuffix, args);
    }

    public Pivot pivot(PivotArgs pivotArgs) {
        // TODO implement me
        return null;
    }

    public static DataModelObject parse(DataModel dataModel, JsonElement object) {
        String name = null;
        String displayName = null;
        String comment = null;
        String[] lineage = new String[0];
        String parentName = null;
        Map<String, Field> fields = new HashMap<String, Field>();
        Collection<String> children = new ArrayList<String>();
        Collection<Constraint> constraints = new ArrayList<Constraint>();
        Map<String, Calculation> calculations = new HashMap<String, Calculation>();

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
            } else if (entry.getKey().equals("comment")) {
                comment = entry.getValue().getAsString();
            } else if (entry.getKey().equals("lineage")) {
                lineage = entry.getValue().getAsString().split("\\.");
            } else if (entry.getKey().equals("parentName")) {
                parentName = entry.getValue().getAsString();
            } else if (entry.getKey().equals("fields")) {
                JsonArray fieldsJson = entry.getValue().getAsJsonArray();
                fields.clear();

                for (JsonElement fieldJson : fieldsJson) {
                    Field field = Field.parse(fieldJson);
                    fields.put(field.getName(), field);
                }
            } else if (entry.getKey().equals("children")) {
                JsonArray childrenJson = entry.getValue().getAsJsonArray();

                for (JsonElement childJson : childrenJson) {
                    children.add(childJson.getAsString());
                }

            } else if (entry.getKey().equals("constraints")) {
                JsonArray constraintsJson = entry.getValue().getAsJsonArray();

                for (JsonElement constraintJson : constraintsJson) {
                    Constraint constraint = Constraint.parse(constraintJson);
                    constraints.add(constraint);
                }
            } else if (entry.getKey().equals("calculations")) {
                calculations.clear();
                for (JsonElement cjson : entry.getValue().getAsJsonArray()) {
                    Calculation c = Calculation.parse(cjson);
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
        if (parentName.equals("BaseSearch")) {
            dmo = new DataModelSearch(dataModel);
        } else if (parentName.equals("BaseTransaction")) {
            dmo = new DataModelTransaction(dataModel);
        } else {
            dmo = new DataModelObject(dataModel);
        }

        // Set the fields common to all data model objects
        dmo.name = name;
        dmo.displayName = displayName;
        dmo.comment = comment;
        dmo.lineage = lineage;
        dmo.parentName = parentName;
        dmo.fields = fields;
        dmo.children = children;
        dmo.constraints = constraints;
        dmo.calculations = calculations;

        // Set the fields of particular types
        if (parentName.equals("BaseSearch")) {
            ((DataModelSearch)dmo).baseSearch = baseSearch;
        } else if (parentName.equals("BaseTransaction")) {
            ((DataModelTransaction)dmo).groupByFields = groupByFields;
            ((DataModelTransaction)dmo).objectsToGroup = objectsToGroup;
            ((DataModelTransaction)dmo).maxPause = transactionMaxPause;
            ((DataModelTransaction)dmo).maxSpan = transactionMaxTimeSpan;
        }

        return dmo;
    }
}
