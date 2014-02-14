package com.splunk;

import com.google.gson.JsonElement;

import java.util.Collection;
import java.util.Map.Entry;

public class DataModelObject {
    private DataModel model;
    private String name;
    private String[] lineage;
    private String displayName;

    public DataModelObject(DataModel model, String name, String[] lineage,
                           String displayName) {
        this.model = model;
        this.name = name;
        this.lineage = lineage;
        this.displayName = displayName;
    }

    public boolean containsField(String fieldName) {
        return false;
    }

    public Job createLocalAccelerationJob(String namespace) {
        return null;
    }

    public Job createLocalAccelerationJob(String namespace, String earliestTime) {
        return null;
    }

    public String getAccelerationNamespace() {
        return getDataModel().getAccelerationNamespace();
    }

    public String getAcceleratedQuery() {
        return null;
    }

    public Collection<Calculation> getCalculations() {
        return null;
    }

    public Collection<DataModelObject> getChildren() {
        return null;
    }

    public Collection<String> getChildrenNames() {
        return null;
    }

    public String getComment() {
        return null;
    }

    public Collection<Constraint> getConstraints() {
        return null;
    }

    public DataModel getDataModel() {
        return null;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Field getField(String fieldName) {
        return null;
    }
    public Collection<Field> getFields() {
        return null;
    }

    public String getQuery() {
        return null;
    }

    public String getName() { return this.name; }

    public String[] getLineage() { return this.lineage; }

    public DataModelObject getParent() {
        return null;
    }
    public String getParentName() {
        return null;
    }

    public Job runQuery() {
        return null;
    }

    public Job runQuery(JobArgs args) {
        return null;
    }

    public Job runQuery(String querySuffix) {
        return null;
    }

    public Job runQuery(String querySuffix, JobArgs args) {
        return null;
    }

    public Pivot pivot(PivotArgs pivotArgs) {
        return null;
    }

    public static DataModelObject parse(DataModel dataModel, JsonElement object) {
        String name = null;
        String[] lineage = new String[0];
        String displayName = null;
        for (Entry<String, JsonElement> entry : object.getAsJsonObject().entrySet()) {
            if (entry.getKey().equals("objectName")) {
                name = entry.getValue().getAsString();
            } else if (entry.getKey().equals("lineage")) {
                lineage = entry.getValue().getAsString().split("\\.");
            } else if (entry.getKey().equals("displayName")) {
                displayName = entry.getValue().getAsString();
            }
        }


        return new DataModelObject(
                dataModel,
                name,
                lineage,
                displayName
        );
    }
}
