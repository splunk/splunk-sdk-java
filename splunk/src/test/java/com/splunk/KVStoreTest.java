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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KVStoreTest extends SDKTestCase {

    private KVStore kvStore;
    private String kvStoreName;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        service.owner = "nobody";
        service.app = "search";
        kvStoreName = createTemporaryName();
        kvStore = service.getKVStores().create(kvStoreName);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        KVStoreCollection kvStores = service.getKVStores();
        for(KVStore kvStoreEntity : kvStores.values()){
            if (kvStoreEntity.getName().startsWith("delete-me")) {
                kvStores.remove(kvStoreEntity.getName());
            }
        }

        super.tearDown();
    }

    @Test
    public void testKVStore() {
        KVStoreCollection kvStoreCollection = service.getKVStores();
        boolean kvStoreExists = false;
        for (KVStore kvStoreObj : kvStoreCollection.values()) {
            if (kvStoreObj.getName().equals(kvStoreName)) {
                kvStoreExists = true;
                break;
            }
        }
        Assert.assertTrue(kvStoreExists);
    }

    @Test
    public void testKVStoreAttributes() {
        Assert.assertFalse(kvStore.getDisabled());
        Assert.assertFalse(kvStore.getProfilingEnabled());
        int profilingThresholdMs = kvStore.getProfilingThresholdMs();
        Assert.assertEquals(1000, profilingThresholdMs);
        Assert.assertFalse(kvStore.getReplicate());
        int replicationDumpMaximumFileSize = kvStore.getReplicationDumpMaximumFileSize();
        Assert.assertEquals(10240, replicationDumpMaximumFileSize);
        Assert.assertEquals("auto", kvStore.getReplicationDumpStrategy());
        Args args = new Args();
        args.put("enforceTypes", true);
        kvStore.update(args);
        Assert.assertTrue(kvStore.getEnforceTypes());
    }

    @Test
    public void testKVStoreWithArgs() {
        KVStoreCollection kvStores = service.getKVStores();
        String kvStoreWithArgsName = createTemporaryName();
        Args args = new Args();
        args.put("profilingEnabled", true);
        args.put("profilingThresholdMs", 15000);
        KVStore kvStoreWithArgs = kvStores.create(kvStoreWithArgsName, args);
        Assert.assertEquals(true, kvStoreWithArgs.getProfilingEnabled());
        Assert.assertEquals("15000", kvStoreWithArgs.getProfilingThresholdMs().toString());
    }

    @Test
    public void testKVStoreAcceleratedFields() {
        Map<String, JsonObject> acceleratedFields = new HashMap<>();
        JsonObject field1 = new JsonObject();
        field1.addProperty("test-1", -1);
        acceleratedFields.put("accelerated_fields.a", field1);
        JsonObject field2 = new JsonObject();
        field2.addProperty("test-2", 1);
        acceleratedFields.put("accelerated_fields.b", field1);
        kvStore.updateAcceleratedFields(acceleratedFields);
        Map<String, String> returnedValues = kvStore.getAcceleratedFields();
        Assert.assertEquals(2, returnedValues.size());
        Assert.assertEquals(acceleratedFields.get("accelerated_fields.a").toString(), returnedValues.get("accelerated_fields.a"));
        Assert.assertEquals(acceleratedFields.get("accelerated_fields.b").toString(), returnedValues.get("accelerated_fields.b"));
    }

    @Test
    public void testKVStoreFields() {
        Args fields = new Args();
        fields.put("field.x", "array");
        fields.put("field.y", "number");
        fields.put("field.z", "bool");
        fields.put("field.l", "string");
        fields.put("field.m", "cidr");
        fields.put("field.n", "time");
        kvStore.updateFields(fields);
        Map<String, String> returnedValues = kvStore.getFields();
        Assert.assertEquals(6, returnedValues.size());
        Assert.assertEquals(fields.get("field.x"), returnedValues.get("field.x"));
        Assert.assertEquals(fields.get("field.y"), returnedValues.get("field.y"));
        Assert.assertEquals(fields.get("field.z"), returnedValues.get("field.z"));
        Assert.assertEquals(fields.get("field.l"), returnedValues.get("field.l"));
        Assert.assertEquals(fields.get("field.m"), returnedValues.get("field.m"));
        Assert.assertEquals(fields.get("field.n"), returnedValues.get("field.n"));
    }

    @Test
    public void testKVStoreData() {
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("test-key-1", "test-val-1");
        jsonObject1.addProperty("test-key-2", "test-val-2");
        jsonObject1.addProperty("test-key-3", "test-val-3");
        jsonObject1.addProperty("_key", "test-data-1");
        jsonObject1.addProperty("_user", "nobody");
        kvStore.addKVStoreData(jsonObject1);
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("test-key-a", "test-val-a");
        jsonObject2.addProperty("test-key-b", "test-val-b");
        jsonObject2.addProperty("test-key-c", "test-val-c");
        jsonObject2.addProperty("_key", "test-data-2");
        jsonObject2.addProperty("_user", "nobody");
        kvStore.addKVStoreData(jsonObject2);
        Assert.assertEquals(2, kvStore.getKVStoreData().size());

        Args args = new Args();
        JsonObject query = new JsonObject();
        query.addProperty("test-key-1", "test-val-1");
        args.put("query", query);
        JsonArray returnedJsonArray = kvStore.getKVStoreData(args);
        Assert.assertEquals(1, returnedJsonArray.size());
        Assert.assertTrue(returnedJsonArray.contains(jsonObject1));

        query.remove("test-key-1");
        query.addProperty("test-key-a", "test-val-a");
        args.put("query", query);
        returnedJsonArray = kvStore.getKVStoreData(args);
        Assert.assertEquals(1, returnedJsonArray.size());
        Assert.assertTrue(returnedJsonArray.contains(jsonObject2));

        // Checking delete with args KV Store collection data
        kvStore.deleteKVStoreData(args);
        returnedJsonArray = kvStore.getKVStoreData();
        Assert.assertEquals(1, returnedJsonArray.size());
        Assert.assertTrue(returnedJsonArray.contains(jsonObject1) && !returnedJsonArray.contains(jsonObject2));

        // Checking if delete KV Store collection data deletes every collection data
        kvStore.addKVStoreData(jsonObject2);
        Assert.assertEquals(2, kvStore.getKVStoreData().size());
        kvStore.deleteKVStoreData();
        Assert.assertEquals(0, kvStore.getKVStoreData().size());
    }

    @Test
    public void testKVStoreUpdateDeleteDataByKey() {
        JsonArray jsonArray = addDataUsingBatchSave();
        Assert.assertEquals(4, kvStore.getKVStoreData().size());
        JsonObject jsonObject1 = jsonArray.get(0).getAsJsonObject();
        JsonObject jsonObject2 = jsonArray.get(1).getAsJsonObject();
        JsonObject jsonObject3 = jsonArray.get(2).getAsJsonObject();
        JsonObject jsonObject4 = jsonArray.get(3).getAsJsonObject();
        JsonObject returnedJsonObject1 = kvStore.getKVStoreDataByKey("first-batch-data");
        JsonObject returnedJsonObject2 = kvStore.getKVStoreDataByKey("second-batch-data");
        JsonObject returnedJsonObject3 = kvStore.getKVStoreDataByKey("third-batch-data");
        JsonObject returnedJsonObject4 = kvStore.getKVStoreDataByKey("fourth-batch-data");
        Assert.assertEquals(jsonObject1, returnedJsonObject1);
        Assert.assertEquals(jsonObject2, returnedJsonObject2);
        Assert.assertEquals(jsonObject3, returnedJsonObject3);
        Assert.assertEquals(jsonObject4, returnedJsonObject4);

        jsonObject2.remove("test-key-batch-b");
        jsonObject2.addProperty("test-key-batch-x", "test-val-batch-b");
        kvStore.updateKVStoreDataByKey("second-batch-data", jsonObject2);
        Assert.assertEquals(4, kvStore.getKVStoreData().size());
        returnedJsonObject1 = kvStore.getKVStoreDataByKey("first-batch-data");
        returnedJsonObject2 = kvStore.getKVStoreDataByKey("second-batch-data");
        returnedJsonObject3 = kvStore.getKVStoreDataByKey("third-batch-data");
        returnedJsonObject4 = kvStore.getKVStoreDataByKey("fourth-batch-data");
        Assert.assertEquals(jsonObject1, returnedJsonObject1);
        Assert.assertEquals(jsonObject2, returnedJsonObject2);
        Assert.assertEquals(jsonObject3, returnedJsonObject3);
        Assert.assertEquals(jsonObject4, returnedJsonObject4);

        kvStore.deleteKVStoreDataByKey("first-batch-data");
        Assert.assertEquals(3, kvStore.getKVStoreData().size());
        returnedJsonObject2 = kvStore.getKVStoreDataByKey("second-batch-data");
        returnedJsonObject3 = kvStore.getKVStoreDataByKey("third-batch-data");
        returnedJsonObject4 = kvStore.getKVStoreDataByKey("fourth-batch-data");
        Assert.assertEquals(jsonObject2, returnedJsonObject2);
        Assert.assertEquals(jsonObject3, returnedJsonObject3);
        Assert.assertEquals(jsonObject4, returnedJsonObject4);
        kvStore.deleteKVStoreDataByKey("second-batch-data");
        kvStore.deleteKVStoreDataByKey("third-batch-data");
        kvStore.deleteKVStoreDataByKey("fourth-batch-data");
        Assert.assertEquals(0, kvStore.getKVStoreData().size());
    }

    @Test
    public void testKVStoreBatchSaveData() {
        JsonArray jsonArray = addDataUsingBatchSave();
        Assert.assertEquals(4, kvStore.getKVStoreData().size());
        JsonObject jsonObject1 = jsonArray.get(0).getAsJsonObject();
        JsonObject jsonObject2 = jsonArray.get(1).getAsJsonObject();
        JsonObject jsonObject3 = jsonArray.get(2).getAsJsonObject();
        JsonObject jsonObject4 = jsonArray.get(3).getAsJsonObject();

        JsonArray returnedJsonArray = kvStore.getKVStoreData();
        Assert.assertTrue(returnedJsonArray.contains(jsonObject1));
        Assert.assertTrue(returnedJsonArray.contains(jsonObject2));
        Assert.assertTrue(returnedJsonArray.contains(jsonObject3));
        Assert.assertTrue(returnedJsonArray.contains(jsonObject4));
    }

    @Test
    public void testKVStoreBatchFindData() {

        JsonArray jsonArray = addDataUsingBatchSave();
        Assert.assertEquals(4, kvStore.getKVStoreData().size());
        JsonObject jsonObject1 = jsonArray.get(0).getAsJsonObject();
        JsonObject jsonObject2 = jsonArray.get(1).getAsJsonObject();
        JsonObject jsonObject3 = jsonArray.get(2).getAsJsonObject();
        JsonObject jsonObject4 = jsonArray.get(3).getAsJsonObject();

        List<Args> listArgs = new ArrayList<>();
        Args args1 = new Args();
        JsonObject queryJsonObject1 = new JsonObject();
        queryJsonObject1.addProperty("test-key-batch-1","test-val-batch-1");
        args1.put("query", queryJsonObject1);
        listArgs.add(args1);
        Args args2 = new Args();
        JsonObject queryJsonObject2 = new JsonObject();
        queryJsonObject2.addProperty("test-key-batch-a","test-val-batch-a");
        args2.put("query", queryJsonObject2);
        listArgs.add(args2);
        Args args3 = new Args();
        JsonObject queryJsonObject3 = new JsonObject();
        queryJsonObject3.addProperty("test-key-batch-z","test-val-batch-z");
        args3.put("query", queryJsonObject3);
        listArgs.add(args3);

        JsonArray returnedJsonArray = kvStore.batchFindKVStoreData(listArgs);
        Assert.assertEquals(3, returnedJsonArray.size());
        JsonArray returnedJsonArrayQuery1 = returnedJsonArray.get(0).getAsJsonArray();
        JsonArray returnedJsonArrayQuery2 = returnedJsonArray.get(1).getAsJsonArray();
        JsonArray returnedJsonArrayQuery3 = returnedJsonArray.get(2).getAsJsonArray();
        Assert.assertEquals(3, returnedJsonArrayQuery1.size());
        Assert.assertTrue(returnedJsonArrayQuery1.contains(jsonObject1) && returnedJsonArrayQuery1.contains(jsonObject2) && returnedJsonArrayQuery1.contains(jsonObject3));
        Assert.assertFalse(returnedJsonArrayQuery1.contains(jsonObject4));
        Assert.assertEquals(2, returnedJsonArrayQuery2.size());
        Assert.assertTrue(returnedJsonArrayQuery2.contains(jsonObject2) && returnedJsonArrayQuery2.contains(jsonObject4));
        Assert.assertFalse(returnedJsonArrayQuery2.contains(jsonObject1) || returnedJsonArrayQuery2.contains(jsonObject3));
        Assert.assertEquals(0, returnedJsonArrayQuery3.size());
    }

    public JsonArray addDataUsingBatchSave() {
        JsonArray jsonArray = new JsonArray();

        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("test-key-batch-1", "test-val-batch-1");
        jsonObject1.addProperty("test-key-batch-2", "test-val-batch-2");
        jsonObject1.addProperty("test-key-batch-3", "test-val-batch-3");
        jsonObject1.addProperty("test-key-batch-4", "test-val-batch-4");
        jsonObject1.addProperty("test-key-batch-5", "test-val-batch-5");
        jsonObject1.addProperty("_key", "first-batch-data");
        jsonObject1.addProperty("_user", "nobody");
        jsonArray.add(jsonObject1);

        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("test-key-batch-1", "test-val-batch-1");
        jsonObject2.addProperty("test-key-batch-2", "test-val-batch-2");
        jsonObject2.addProperty("test-key-batch-a", "test-val-batch-a");
        jsonObject2.addProperty("test-key-batch-b", "test-val-batch-b");
        jsonObject2.addProperty("_key", "second-batch-data");
        jsonObject2.addProperty("_user", "nobody");
        jsonArray.add(jsonObject2);

        JsonObject jsonObject3 = new JsonObject();
        jsonObject3.addProperty("test-key-batch-x", "test-val-batch-x");
        jsonObject3.addProperty("test-key-batch-y", "test-val-batch-y");
        jsonObject3.addProperty("test-key-batch-1", "test-val-batch-1");
        jsonObject3.addProperty("_key", "third-batch-data");
        jsonObject3.addProperty("_user", "nobody");
        jsonArray.add(jsonObject3);

        JsonObject jsonObject4 = new JsonObject();
        jsonObject4.addProperty("test-key-batch-x", "test-val-batch-x");
        jsonObject4.addProperty("test-key-batch-a", "test-val-batch-a");
        jsonObject4.addProperty("_key", "fourth-batch-data");
        jsonObject4.addProperty("_user", "nobody");
        jsonArray.add(jsonObject4);

        kvStore.batchSaveKVStoreData(jsonArray);
        return jsonArray;
    }
}
