package com.splunk;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Represents a limit on the events shown in a pivot by sorting them according to some field, then taking
 * the specified number from the beginning or end of the list.
 */
public class LimitPivotFilter extends PivotFilter {
    private final StatsFunction statsFunction;
    private final String sortAttribute;
    private final int limit;
    private final SortDirection sortDirection;

    /**
     * Throws IllegalArgumentException if statsFunction is not one of
     *   - COUNT, DISTINCT_COUNT (for fields of type string)
     *   - COUNT, DISTINCT_COUNT, SUM, AVERAGE (for fields of type number)
     *   - COUNT (for fields of type object count)
     */
    LimitPivotFilter(DataModelObject dataModelObject, String fieldName, String sortAttribute,
                            SortDirection sortDirection, int limit,
                            StatsFunction statsFunction) {
        super(dataModelObject, fieldName);
        FieldType type = dataModelObject.getField(fieldName).getType();
        if (type != FieldType.NUMBER && type != FieldType.STRING && type != FieldType.OBJECTCOUNT) {
            throw new IllegalArgumentException("Field " + fieldName + " on the data model object was of type "
                    + dataModelObject.getField(fieldName).getType().toString() + ", expected number, string, " +
                    "or object count.");
        }
        this.sortAttribute = sortAttribute;
        this.sortDirection = sortDirection;
        this.limit = limit;

        if (type == FieldType.STRING && statsFunction != StatsFunction.COUNT &&
                statsFunction != StatsFunction.DISTINCT_COUNT) {
            throw new IllegalArgumentException("Stats function for fields of type string must be COUNT or " +
                    "DISTINCT_COUNT; found " + statsFunction.toString());
        }
        if (type == FieldType.NUMBER && statsFunction != StatsFunction.COUNT &&
                statsFunction != StatsFunction.DISTINCT_COUNT && statsFunction != StatsFunction.SUM &&
                statsFunction != StatsFunction.AVERAGE) {
            throw new IllegalArgumentException("Stats function for fields of type number must be one of COUNT, " +
            "DISTINCT_COUNT, SUM, or AVERAGE; found " + statsFunction.toString());
        }
        if (type == FieldType.OBJECTCOUNT && statsFunction != StatsFunction.COUNT) {
            throw new IllegalArgumentException("Stats function for fields of type object count must be COUNT; " +
                    "found " + statsFunction.toString());
        }
        this.statsFunction = statsFunction;
    }

    /**
     * Return the name of the field to use for sorting.
     */
    public String getAttributeName() {
        return this.sortAttribute;
    }

    /**
     * @return a list of the names of DataModelObjects from the most remote ancestor that is
     * not a built-in parent to this data model object.
     */
    public String[] getAttributeOwnerLineage() {
        return this.dataModelObject.getField(this.sortAttribute).getOwnerLineage();
    }

    /**
     * @return whether the limit takes the lowest (SortDirection.ASCENDING) or highest (SortDirection.DESCENDING)
     * values from the sorted list.
     */
    public SortDirection getSortDirection() {
        return this.sortDirection;
    }

    /**
     * @return The number of values from the sorted list to allow through this filter.
     */
    public int getLimit() {
        return this.limit;
    }

    /**
     * @return The stats function to use for aggregation before sorting.
     */
    public StatsFunction getStatsFunction() {
        return this.statsFunction;
    }

    @Override
    JsonElement toJson() {
        JsonObject root = new JsonObject();

        addCommonFields(root);

        root.add("attributeName", new JsonPrimitive(this.getAttributeName()));
        root.add("attributeOwner", new JsonPrimitive(Util.join(".", this.getAttributeOwnerLineage())));
        if (sortDirection == SortDirection.ASCENDING) {
            root.add("limitType", new JsonPrimitive("lowest"));
        } else if (sortDirection == SortDirection.DESCENDING) {
            root.add("limitType", new JsonPrimitive("highest"));
        }
        root.add("limitAmount", new JsonPrimitive(this.limit));
        root.add("statsFn", new JsonPrimitive(this.statsFunction.toString()));

        return root;
    }
}
