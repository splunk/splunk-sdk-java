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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class PivotTest extends SDKTestCase {
    DataModelObject dataModelObject;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        EntityCollection<DataModel> dataModels = service.getDataModels();

        DataModelArgs args = new DataModelArgs();
        args.setRawDescription(streamToString(openResource("data/datamodels/data_model_for_pivot.json")));
        DataModel model = dataModels.create(createTemporaryName(), args);

        this.dataModelObject = model.getObject("test_data");
        Assert.assertNotNull(dataModelObject);
    }

    @After
    public void tearDown() throws Exception {
        for (DataModel d : service.getDataModels().values()) {
            if (d.getName().startsWith("delete-me")) {
                d.remove();
            }
        }

        super.tearDown();
    }

    @Test
    public void testConstructorArgs() {

        PivotSpecification pivotArgs = new PivotSpecification(dataModelObject);

        JsonElement root = pivotArgs.getDescription();
        Assert.assertTrue(root instanceof JsonObject);
        JsonObject rootAsObject = root.getAsJsonObject();

        Assert.assertTrue(rootAsObject.has("dataModel"));
        Assert.assertEquals(new JsonPrimitive(dataModelObject.getDataModel().getName()), rootAsObject.get("dataModel"));

        Assert.assertTrue(rootAsObject.has("baseClass"));
        Assert.assertEquals(new JsonPrimitive(dataModelObject.getName()), rootAsObject.get("baseClass"));
    }

    @Test
    public void testAccelerationWorks() {
        PivotSpecification pivotArgs = new PivotSpecification(dataModelObject);

        Assert.assertEquals(dataModelObject.getDataModel().getName(), pivotArgs.getNamespace());

        String sid = createTemporaryName();
        pivotArgs.setAccelerationJob(sid);
        Assert.assertEquals("sid="+sid, pivotArgs.getNamespace());

        String namespace = createTemporaryName();
        pivotArgs.setAccelerationNamespace(namespace);
        Assert.assertEquals(namespace, pivotArgs.getNamespace());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddFilterOnNonexistantField() {
        PivotSpecification pivotArgs = new PivotSpecification(dataModelObject);
        pivotArgs.addFilter(createTemporaryName(), BooleanComparison.EQUALS, true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddBooleanFilterOnWrongType() {
        PivotSpecification pivotSpec = new PivotSpecification(dataModelObject);
        pivotSpec.addFilter("_time", BooleanComparison.EQUALS, true);
    }

    @Test
    public void testAddBooleanFilter() {
        PivotSpecification pivotSpec = new PivotSpecification(dataModelObject);
        pivotSpec.addFilter("has_boris", BooleanComparison.EQUALS, true);

        Assert.assertEquals(1, pivotSpec.getFilters().size());
        for (PivotFilter pf : pivotSpec.getFilters()) {
            Assert.assertTrue(pf instanceof BooleanPivotFilter);
            JsonElement obj = pf.toJson();
            Assert.assertTrue(obj instanceof JsonObject);
            JsonObject o = (JsonObject)obj;

            Assert.assertTrue(o.has("fieldName"));
            Assert.assertEquals(new JsonPrimitive("has_boris"), o.get("fieldName"));

            Assert.assertTrue(o.has("type"));
            Assert.assertEquals(new JsonPrimitive("boolean"), o.get("type"));

            Assert.assertTrue(o.has("comparator"));
            Assert.assertEquals(new JsonPrimitive("="), o.get("comparator"));

            Assert.assertTrue(o.has("compareTo"));
            Assert.assertEquals(new JsonPrimitive(true), o.get("compareTo"));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddStringFilterOnWrongType() {
        PivotSpecification pivotSpecification = new PivotSpecification(dataModelObject);
        pivotSpecification.addFilter("has_boris", StringComparison.CONTAINS, "abc");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddStringFilteronNonexistantField() {
        PivotSpecification pivotSpecification = new PivotSpecification(dataModelObject);
        pivotSpecification.addFilter(createTemporaryName(), StringComparison.CONTAINS, "abc");
    }

    @Test
    public void testAddStringFilter() {
        PivotSpecification pivotSpec = new PivotSpecification(dataModelObject);
        pivotSpec.addFilter("host", StringComparison.CONTAINS, "abc");

        Assert.assertEquals(1, pivotSpec.getFilters().size());
        for (PivotFilter pf : pivotSpec.getFilters()) {
            Assert.assertTrue(pf instanceof StringPivotFilter);
            JsonElement obj = pf.toJson();
            Assert.assertTrue(obj instanceof JsonObject);
            JsonObject o = (JsonObject)obj;

            Assert.assertTrue(o.has("fieldName"));
            Assert.assertEquals(new JsonPrimitive("host"), o.get("fieldName"));

            Assert.assertTrue(o.has("type"));
            Assert.assertEquals(new JsonPrimitive("string"), o.get("type"));

            Assert.assertTrue(o.has("comparator"));
            Assert.assertEquals(new JsonPrimitive("contains"), o.get("comparator"));

            Assert.assertTrue(o.has("compareTo"));
            Assert.assertEquals(new JsonPrimitive("abc"), o.get("compareTo"));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddIpv4FilterOnWrongType() {
        PivotSpecification pivotSpecification = new PivotSpecification(dataModelObject);
        pivotSpecification.addFilter("has_boris", IPv4Comparison.STARTS_WITH, "192.168");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIpv4FilterOnNonexistantField() {
        PivotSpecification pivotSpecification = new PivotSpecification(dataModelObject);
        pivotSpecification.addFilter(createTemporaryName(), IPv4Comparison.STARTS_WITH, "192.168");
    }

    @Test
    public void testAddIpv4Filter() {
        PivotSpecification pivotSpec = new PivotSpecification(dataModelObject);
        pivotSpec.addFilter("hostip", IPv4Comparison.STARTS_WITH, "192.168");

        Assert.assertEquals(1, pivotSpec.getFilters().size());
        for (PivotFilter pf : pivotSpec.getFilters()) {
            Assert.assertTrue(pf instanceof IPv4PivotFilter);
            JsonElement obj = pf.toJson();
            Assert.assertTrue(obj instanceof JsonObject);
            JsonObject o = (JsonObject)obj;

            Assert.assertTrue(o.has("fieldName"));
            Assert.assertEquals(new JsonPrimitive("hostip"), o.get("fieldName"));

            Assert.assertTrue(o.has("type"));
            Assert.assertEquals(new JsonPrimitive("ipv4"), o.get("type"));

            Assert.assertTrue(o.has("comparator"));
            Assert.assertEquals(new JsonPrimitive("startsWith"), o.get("comparator"));

            Assert.assertTrue(o.has("compareTo"));
            Assert.assertEquals(new JsonPrimitive("192.168"), o.get("compareTo"));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddNumberFilterOnWrongType() {
        PivotSpecification pivotSpecification = new PivotSpecification(dataModelObject);
        pivotSpecification.addFilter("has_boris", NumberComparison.AT_LEAST, 2.3);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNumberFilterOnNonexistantField() {
        PivotSpecification pivotSpecification = new PivotSpecification(dataModelObject);
        pivotSpecification.addFilter(createTemporaryName(), NumberComparison.AT_LEAST, 2.3);
    }

    @Test
    public void testAddNumberFilter() {
        PivotSpecification pivotSpec = new PivotSpecification(dataModelObject);
        pivotSpec.addFilter("epsilon", NumberComparison.AT_LEAST, 2.3);

        Assert.assertEquals(1, pivotSpec.getFilters().size());
        for (PivotFilter pf : pivotSpec.getFilters()) {
            Assert.assertTrue(pf instanceof NumberPivotFilter);
            JsonElement obj = pf.toJson();
            Assert.assertTrue(obj instanceof JsonObject);
            JsonObject o = (JsonObject)obj;

            Assert.assertTrue(o.has("fieldName"));
            Assert.assertEquals(new JsonPrimitive("epsilon"), o.get("fieldName"));

            Assert.assertTrue(o.has("type"));
            Assert.assertEquals(new JsonPrimitive("number"), o.get("type"));

            Assert.assertTrue(o.has("comparator"));
            Assert.assertEquals(new JsonPrimitive(">="), o.get("comparator"));

            Assert.assertTrue(o.has("compareTo"));
            Assert.assertEquals(new JsonPrimitive((double)2.3), o.get("compareTo"));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddLimitFilterOnNonexistentField() {
        PivotSpecification pivotSpecification = new PivotSpecification(dataModelObject);
        pivotSpecification.addFilter("has_boris", "host",
                SortDirection.DEFAULT, 50, StatsFunction.COUNT);
    }

    @Test
    public void testAddLimitFilter() {
        PivotSpecification pivotSpecification = new PivotSpecification(dataModelObject);
        pivotSpecification.addFilter("epsilon", "hostip", SortDirection.ASCENDING, 500, StatsFunction.AVERAGE);

        Assert.assertEquals(1, pivotSpecification.getFilters().size());
        for (PivotFilter pf : pivotSpecification.getFilters()) {
            Assert.assertTrue(pf instanceof LimitPivotFilter);
            JsonElement obj = pf.toJson();

            Assert.assertTrue(obj instanceof JsonObject);
            JsonObject o = (JsonObject)obj;

            Assert.assertTrue(o.has("fieldName"));
            Assert.assertEquals(new JsonPrimitive("epsilon"), o.get("fieldName"));

            Assert.assertTrue(o.has("owner"));
            Assert.assertEquals(new JsonPrimitive("test_data"), o.get("owner"));

            Assert.assertTrue(o.has("type"));
            Assert.assertEquals(new JsonPrimitive("number"), o.get("type"));

            Assert.assertTrue(o.has("attributeName"));
            Assert.assertEquals(new JsonPrimitive("hostip"), o.get("attributeName"));

            Assert.assertTrue(o.has("attributeOwner"));
            Assert.assertEquals(new JsonPrimitive("test_data"), o.get("attributeOwner"));

            Assert.assertTrue(o.has("limitType"));
            Assert.assertEquals(new JsonPrimitive("lowest"), o.get("limitType"));

            Assert.assertTrue(o.has("limitAmount"));
            Assert.assertEquals(new JsonPrimitive(500), o.get("limitAmount"));

            Assert.assertTrue(o.has("statsFn"));
            Assert.assertEquals(new JsonPrimitive("average"), o.get("statsFn"));
        }
    }
}
