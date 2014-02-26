package com.splunk;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Created by fross on 2/13/14.
 */
public class PivotArgs {
    private static GsonBuilder gson = new GsonBuilder();

    String dataModel = null;
    String baseClass = null;

    public PivotArgs(String dataModelName, String objectName) {
        this.dataModel = dataModelName;
        this.baseClass = objectName;
    }

    public PivotArgs(DataModelObject object) {
        this.dataModel = object.getDataModel().getName();
        this.baseClass = object.getName();
    }


}
