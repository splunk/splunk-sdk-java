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

import com.google.gson.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class KVStoreData {

    private static final String DATA_PATH = "storage/collections/data/";

    /**
     * Insert an item into the KV Store Collection.
     *
     * @param name The name of the KV Store Collection.
     * @param service The connected {@code Service} instance.
     * @param jsonObject The item to be added to KV Store Collection.
     * @return The items present in the KV Store Collection.
     */
    protected JsonArray addData(String name, Service service, JsonObject jsonObject){
        service.postWithJsonContent( DATA_PATH + name, jsonObject.toString());
        return getData(name, service, null);
    }

    /**
     * Return the items present in the KV Store Collection using
     * the query arguments.
     *
     * @param name The name of the KV Store Collection.
     * @param service The connected {@code Service} instance.
     * @param args The query arguments.
     * @return The items present in the KV Store Collection.
     */
    protected JsonArray getData(String name, Service service, Args args) {
        JsonElement element = service.getJsonResponse(DATA_PATH + name, args);
        return element.getAsJsonArray();
    }

    /**
     * Return the item present in the KV Store Collection using
     * the key.
     *
     * @param name The name of the KV Store Collection.
     * @param service The connected {@code Service} instance.
     * @param key The key of the item to fetch from the KV Store Collection.
     * @return The item present in the KV Store Collection.
     */
    protected JsonObject getDataByKey(String name, Service service, String key) {
        JsonElement element = service.getJsonResponse(DATA_PATH + name + "/" + key, null);
        return element.getAsJsonObject();
    }

    /**
     * Update the item present in the KV Store Collection using
     * the key.
     *
     * @param name The name of the KV Store Collection.
     * @param service The connected {@code Service} instance.
     * @param key The key of the item to update in the KV Store Collection.
     * @param jsonObject The updated item.
     */
    protected void updateDataByKey(String name, Service service, String key, JsonObject jsonObject){
        service.postWithJsonContent( DATA_PATH + name + "/" + key, jsonObject.toString());
    }

    /**
     * Delete the items present in the KV Store Collection using
     * the query arguments.
     *
     * @param name The name of the KV Store Collection.
     * @param service The connected {@code Service} instance.
     * @param args The query arguments.
     */
    protected void deleteData(String name, Service service, Args args) {
        service.delete(DATA_PATH + name, args);
    }

    /**
     * Delete the item present in the KV Store Collection using
     * the key.
     *
     * @param name The name of the KV Store Collection.
     * @param service The connected {@code Service} instance.
     * @param key The key of the item to delete from the KV Store Collection.
     */
    protected void deleteDataByKey(String name, Service service, String key) {
        service.delete(DATA_PATH + name + "/" + key);
    }

    /**
     * Perform multiple save operations in a batch.
     *
     * @param name The name of the KV Store Collection.
     * @param service The connected {@code Service} instance.
     * @param jsonArray The JSON array of items to be saved.
     * @return The items present in the KV Store Collection.
     */
    protected JsonArray batchSave(String name, Service service, JsonArray jsonArray) {
        service.postWithJsonContent( DATA_PATH + name + "/batch_save", jsonArray.toString());
        return getData(name, service, null);
    }

    /**
     * Perform multiple queries in a batch.
     *
     * @param name The name of the KV Store Collection.
     * @param service The connected {@code Service} instance.
     * @param listOfArgs The list of query arguments.
     * @return The JSON array of the results of the queries.
     */
    protected JsonArray batchFind(String name, Service service, List<Args> listOfArgs) {
        Gson gson = new Gson();
        String jsonArray = gson.toJson(listOfArgs);
        ResponseMessage resp = service.postWithJsonContent(DATA_PATH + name + "/batch_find", jsonArray);
        InputStream is = resp.getContent();
        JsonElement element = JsonParser.parseReader(new InputStreamReader(is));
        return element.getAsJsonArray();
    }
}


