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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * DataModel represents a data model on the server. Data models contain
 * data model objects, which specify structured views on Splunk data.
 */
public class DataModel extends Entity {
    private final static JsonParser jsonParser = new JsonParser();
    private final static Gson gson = new Gson();

    private final String modelNameLabel = "modelName";
    private final String displayNameLabel = "displayName";
    private final String rawJsonLabel = "description";

    private String description = null;

    private Map<String, DataModelObject> objects;
    private boolean accelerationEnabled;
    private String earliestAcceleratedTime;
    private String accelerationCronSchedule;

    public DataModel(Service service, String path) {
        super(service, path);
        objects = new HashMap<String, DataModelObject>();
    }

    /**
     * Returns whether there is an object of the given name in this data model.
     *
     * @param name Name of the object to check for.
     * @return true if there is an object with that name; false otherwise.
     */
    public boolean containsObject(String name) {
        return this.objects.containsKey(name);
    }

    /**
     * Retrieve an object by name from this data model.
     *
     * @param name Name of the object to retrieve.
     * @return a DataModelObject if there is such an object; null otherwise.
     */
    public DataModelObject getObject(String name) {
        return this.objects.get(name);
    }

    /**
     * @return a collection of all objects in this data model.
     */
    public Collection<DataModelObject> getObjects() {
        return objects.values();
    }

    /**
     * Returns the tsidx namespace which holds global acceleration events for this
     * data model. The namespace will be returned whether acceleration is enabled
     * or not.
     *
     * @return The tsidx namespace for global acceleration of this data model.
     */
    public String getAccelerationNamespace() {
        // For the moment, the acceleration namespace for global acceleration of
        // data models is the name of the data model.
        return getName();
    }

    /**
     * @return whether global acceleration is enabled for this data model.
     */
    public boolean isAccelerated() {
        return this.accelerationEnabled;
    }

    /**
     * @return A human readable description of this data model.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return The raw JSON describing this data model and its objects.
     */
    public String getRawJson() {
        return getString(rawJsonLabel);
    }

    /**
     * @return the human readable name of this data model.
     */
    public String getDisplayName() {
        return getString(displayNameLabel);
    }

    @Override
    public Entity load(AtomObject value) {
        Entity result = super.load(value);
        parseDescription(getString(rawJsonLabel));
        parseAcceleration(getString("acceleration"));
        return result;
    }

    void parseDescription(String input) {
        objects = new HashMap<String, DataModelObject>();

        JsonElement rootElement = jsonParser.parse(input);

        for (Entry<String, JsonElement> entry : rootElement.getAsJsonObject().entrySet()) {
            if (entry.getKey().equals(modelNameLabel)) {
                content.put(modelNameLabel, entry.getValue().getAsString());
            } else if (entry.getKey().equals(displayNameLabel)) {
                content.put(displayNameLabel, entry.getValue().getAsString());
            } else if (entry.getKey().equals(rawJsonLabel)) {
                description = entry.getValue().getAsString();
            } else if (entry.getKey().equals("objects")) {
                JsonArray objectArray = entry.getValue().getAsJsonArray();
                for (JsonElement object : objectArray) {
                    DataModelObject dmo = DataModelObject.parse(this, object);
                    objects.put(dmo.getName(), dmo);
                }
            }
        }
    }

    public void parseAcceleration(String input) {
         JsonElement rootElement = jsonParser.parse(input);

        for (Entry<String, JsonElement> entry : rootElement. getAsJsonObject().entrySet()) {
            if (entry.getKey().equals("enabled")) {
                accelerationEnabled = entry.getValue().getAsBoolean();
            } else if (entry.getKey().equals("earliest_time")) {
                earliestAcceleratedTime = entry.getValue().getAsString();
            } else if (entry.getKey().equals("cron_schedule")) {
                accelerationCronSchedule = entry.getValue().getAsString();
            }
        }
    }

    /**
     * Enable or disable global acceleration on this data model.
     *
     * @param enabled true enabled, false disables.
     */
    public void setAcceleration(boolean enabled) {
        setAccelerationObject(
                enabled,
                getEarliestAcceleratedTime(),
                getAccelerationCronSchedule()
        );
    }

    private void setAccelerationObject(boolean isAccelerated, String earliestTime, String cronSchedule) {
        Map toSend = new HashMap();

        toSend.put("enabled", isAccelerated);
        toSend.put("earliest_time", earliestTime);
        toSend.put("cron_schedule", cronSchedule);

        final String json = gson.toJson(toSend);
        System.out.println(json);

        ResponseMessage message = service.post(path, new Args() {{
            put("acceleration", json);
            //put("concise", "0");
        }});

        AtomFeed feed = AtomFeed.parseStream(message.getContent());
        load(feed);
    }

    public String getEarliestAcceleratedTime() {
        return earliestAcceleratedTime;
    }

    public void setEarliestAcceleratedTime(String earliestAcceleratedTime) {
        setAccelerationObject(
                isAccelerated(),
                earliestAcceleratedTime,
                getAccelerationCronSchedule()
        );
    }

    public String getAccelerationCronSchedule() {
        return accelerationCronSchedule;
    }

    public void setAccelerationCronSchedule(String accelerationCronSchedule) {
        setAccelerationObject(
                isAccelerated(),
                getEarliestAcceleratedTime(),
                accelerationCronSchedule
        );
    }
}
