package com.splunk;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    private Map<String, Field> fields;

    private DataModelObject() {
        this.fields = new HashMap<String, Field>();
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

    public Job createLocalAccelerationJob(String namespace) {
        return null;
    }

    public Job createLocalAccelerationJob(String namespace, String earliestTime) {
        return null;
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

    /**
     * @return a human readable comment on this object.
     */
    public String getComment() {
        return this.comment;
    }

    public Collection<Constraint> getConstraints() {
        return null;
    }

    public DataModel getDataModel() {
        return null;
    }

    /**
     * @return the human readable name of this data model object.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    public Field getField(String fieldName) {
        return fields.get(fieldName);
    }
    public Collection<Field> getFields() {
        return fields.values();
    }

    public String getQuery() {
        return null;
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
        DataModelObject dmo = new DataModelObject();

        for (Entry<String, JsonElement> entry : object.getAsJsonObject().entrySet()) {
            if (entry.getKey().equals("objectName")) {
                dmo.name = entry.getValue().getAsString();
            } else if (entry.getKey().equals("displayName")) {
                dmo.displayName = entry.getValue().getAsString();
            } else if (entry.getKey().equals("comment")) {
                dmo.comment = entry.getValue().getAsString();
            } else if (entry.getKey().equals("lineage")) {
                dmo.lineage = entry.getValue().getAsString().split("\\.");
            } else if (entry.getKey().equals("displayName")) {
                dmo.displayName = entry.getValue().getAsString();
            } else if (entry.getKey().equals("fields")) {
                JsonArray fieldsJson = entry.getValue().getAsJsonArray();
                dmo.fields.clear();

                for (JsonElement fieldJson : fieldsJson) {
                    Field field = Field.parse(fieldJson);
                    dmo.fields.put(field.getName(), field);
                }
            } else if (entry.getKey().equals("constraints")) {
                // TODO: parse contraints
            }
        }

        return dmo;
    }
}
