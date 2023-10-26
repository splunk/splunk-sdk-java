/*
 * Copyright 2023 Splunk, Inc.
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
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code KVStore} class represents a KV Store Collection.
 */
public class KVStore extends Entity {

    private final KVStoreData kvStoreData;

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The KV Store Collections endpoint.
     */
    KVStore(Service service, String path) {
        super(service, path);
        this.kvStoreData = new KVStoreData();
    }

    /**
     * Insert an item into the KV Store Collection.
     *
     * @param jsonObject The item to be added to KV Store Collection.
     * @return The items present in the KV Store Collection.
     */
    public JsonArray addKVStoreData(JsonObject jsonObject) {
        return kvStoreData.addData(this.getName(), this.service, jsonObject);
    }

    /**
     * Return the items present in the KV Store Collection.
     *
     * @return The items present in the KV Store Collection.
     */
    public JsonArray getKVStoreData() {
        return getKVStoreData(null);
    }

    /**
     * Return the items present in the KV Store Collection using
     * the query arguments.
     *
     * @param args The query arguments.
     * @return The items present in the KV Store Collection.
     */
    public JsonArray getKVStoreData(Args args) {
        return kvStoreData.getData(this.getName(), this.service , args);
    }

    /**
     * Return the item present in the KV Store Collection using
     * the key.
     *
     * @param key The key of the item in the KV Store Collection.
     * @return The item present in the KV Store Collection.
     */
    public JsonObject getKVStoreDataByKey(String key) {
        return kvStoreData.getDataByKey(this.getName(), this.service, key);
    }

    /**
     * Update the item present in the KV Store Collection using
     * the key.
     *
     * @param key The key of the item in the KV Store Collection.
     * @param jsonObject The updated item.
     */
    public void updateKVStoreDataByKey(String key, JsonObject jsonObject) {
        kvStoreData.updateDataByKey(this.getName(), this.service, key, jsonObject);
    }

    /**
     * Delete all the items present in the KV Store Collection.
     */
    public void deleteKVStoreData() {
        kvStoreData.deleteData(this.getName(), this.service, null);
    }

    /**
     * Delete the items present in the KV Store Collection using
     * the query arguments.
     *
     * @param args The query arguments.
     */
    public void deleteKVStoreData(Args args) {
        kvStoreData.deleteData(this.getName(), this.service, args);
    }

    /**
     * Delete the item present in the KV Store Collection using
     * the key.
     *
     * @param key The key of the item in the KV Store Collection.
     */
    public void deleteKVStoreDataByKey(String key) {
        kvStoreData.deleteDataByKey(this.getName(), this.service, key);
    }

    /**
     * Perform multiple save operations in a batch.
     *
     * @param jsonArray The JSON array of items to be saved.
     * @return The items present in the KV Store Collection.
     */
    public JsonArray batchSaveKVStoreData(JsonArray jsonArray) {
        return kvStoreData.batchSave(this.getName(), this.service, jsonArray);
    }

    /**
     * Perform multiple queries in a batch.
     *
     * @param listOfArgs The list of query arguments.
     * @return The JSON array of the results of the queries.
     */
    public JsonArray batchFindKVStoreData(List<Args> listOfArgs) {
        return kvStoreData.batchFind(this.getName(), this.service, listOfArgs);
    }

    /**
     * Update the accelerated fields of the KV Store Collection.
     *
     * @param acceleratedFields The updated accelerated fields.
     */
    public void updateAcceleratedFields(Map<String, JsonObject> acceleratedFields) {
        Args args = new Args();
        for(Map.Entry<String, JsonObject> entry : acceleratedFields.entrySet()){
            args.put(entry.getKey(), entry.getValue().toString());
        }
        super.update(args);
    }

    /**
     * Update the fields of the KV Store Collection.
     *
     * @param fields The updated fields.
     */
    public void updateFields(Args fields) {
        super.update(fields);
    }

    /**
     * Returns the collection state. By default, the value is 0,
     * meaning that the collection is enabled.
     *
     * @return The KV Store collection state.
     */
    public Boolean getDisabled() {
        return getBoolean("disabled");
    }

    /**
     * Returns if data types are enforced when inserting data into
     * the collection. Defaults to false.
     *
     * @return The boolean indicating if data types are enforced
     * when inserting data into the collection.
     */
    public Boolean getEnforceTypes() {
        return getBoolean("enforceTypes");
    }

    /**
     * Returns the profiling status of slow-running operations,
     * affecting profilingThresholdMs. By default, the value is
     * false, meaning that profiling is disabled. If true,
     * profiling is enabled.
     *
     * @return The profiling status of slow-running operations.
     */
    public Boolean getProfilingEnabled() {
        return getBoolean("profilingEnabled");
    }

    /**
     * Returns the threshold for logging slow-running operations,
     * in milliseconds. Applies only if profilingEnabled is true.
     *
     * @return The threshold for logging slow-running operations.
     */
    public Integer getProfilingThresholdMs() {
        return getInteger("profilingThresholdMs");
    }

    /**
     * Returns whether the collection is replicated on indexers.
     * Defaults to false.
     *
     * @return The boolean indicating whether the collection is
     * replicated on indexers.
     */
    public Boolean getReplicate() {
        return getBoolean("replicate");
    }

    /**
     * Returns the maximum file size (in KB) for each dump file
     * when replication_dump_strategy=auto. Defaults to 10240KB.
     *
     * @return The maximum file size (in KB) for each dump file.
     */
    public Integer getReplicationDumpMaximumFileSize() {
        return getInteger("replication_dump_maximum_file_size");
    }

    /**
     * Returns the strategy to dump files. Possible values are
     * auto and one_file.
     *
     * @return The strategy to dump files.
     */
    public String getReplicationDumpStrategy() {
        return getString("replication_dump_strategy");
    }

    /**
     * Returns the field name and its type.
     *
     * @return The field name and its type.
     */
    public Map<String, String> getFields() {
        Map<String, String> fields = new HashMap<>();
        for(Map.Entry<String, Object> entry : getContent().entrySet()) {
            if(entry.getKey().startsWith("field.")) fields.put(entry.getKey(), entry.getValue().toString());
        }
        return fields;
    }

    /**
     * Returns the field acceleration name and JSON definition.
     *
     * @return The field acceleration name and JSON definition.
     */
    public Map<String, String> getAcceleratedFields() {
        Map<String, String> acceleratedFields = new HashMap<>();
        for(Map.Entry<String, Object> entry : getContent().entrySet()) {
            if(entry.getKey().startsWith("accelerated_fields.")) acceleratedFields.put(entry.getKey(), entry.getValue().toString());
        }
        return acceleratedFields;
    }
}
