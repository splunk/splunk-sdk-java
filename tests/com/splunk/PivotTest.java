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

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;


public class PivotTest extends SDKTestCase {
    DataModelObject dataModelObject;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        EntityCollection<DataModel> dataModels = service.getDataModels();

        DataModelArgs args = new DataModelArgs();
        args.setRawJsonDescription(streamToString(openResource("data/datamodels/data_model_for_pivot.json")));
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

        PivotSpecification pivotArgs = dataModelObject.createPivotSpecification();

        JsonObject root = pivotArgs.toJson();

        Assert.assertTrue(root.has("dataModel"));
        Assert.assertEquals(new JsonPrimitive(dataModelObject.getDataModel().getName()), root.get("dataModel"));

        Assert.assertTrue(root.has("baseClass"));
        Assert.assertEquals(new JsonPrimitive(dataModelObject.getName()), root.get("baseClass"));
    }

    @Test
    public void testAccelerationWorks() {
        Args serviceArgs = new Args();
        serviceArgs.put("host", service.getHost());
        serviceArgs.put("port", service.getPort());
        serviceArgs.put("scheme", service.getScheme());
        serviceArgs.put("token", service.getToken());

        serviceArgs.put("owner", "nobody");
        serviceArgs.put("app", "search");
        Service nonprivateService = new Service(serviceArgs);

        EntityCollection<DataModel> dataModels = nonprivateService.getDataModels();

        DataModelArgs args = new DataModelArgs();
        args.setRawJsonDescription(streamToString(openResource("data/datamodels/data_model_for_pivot.json")));
        DataModel model = dataModels.create(createTemporaryName(), args);

        dataModelObject = model.getObject("test_data");
        Assert.assertNotNull(dataModelObject);

        model.setAcceleration(true);
        model.setEarliestAcceleratedTime("-2mon");
        model.setAccelerationCronSchedule("0 */12 * * *");
        model.update();

        PivotSpecification pivotArgs = dataModelObject.createPivotSpecification();

        Assert.assertEquals(dataModelObject.getDataModel().getName(), pivotArgs.getAccelerationNamespace());

        String sid = createTemporaryName();
        pivotArgs.setAccelerationJob(sid);
        Assert.assertEquals("sid="+sid, pivotArgs.getAccelerationNamespace());

        String namespace = createTemporaryName();
        pivotArgs.setAccelerationNamespace(namespace);
        Assert.assertEquals(namespace, pivotArgs.getAccelerationNamespace());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddFilterOnNonexistantField() {
        PivotSpecification pivotArgs = dataModelObject.createPivotSpecification();
        pivotArgs.addFilter(createTemporaryName(), BooleanComparison.EQUALS, true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddBooleanFilterOnWrongType() {
        PivotSpecification pivotSpec = dataModelObject.createPivotSpecification();
        pivotSpec.addFilter("_time", BooleanComparison.EQUALS, true);
    }

    @Test
    public void testAddBooleanFilter() {
        PivotSpecification pivotSpec = dataModelObject.createPivotSpecification();
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

            Assert.assertTrue(o.has("rule"));
            JsonObject rule = o.getAsJsonObject("rule");

            Assert.assertTrue(rule.has("comparator"));
            Assert.assertEquals(new JsonPrimitive("="), rule.get("comparator"));

            Assert.assertTrue(rule.has("compareTo"));
            Assert.assertEquals(new JsonPrimitive(true), rule.get("compareTo"));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddStringFilterOnWrongType() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addFilter("has_boris", StringComparison.CONTAINS, "abc");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddStringFilteronNonexistantField() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addFilter(createTemporaryName(), StringComparison.CONTAINS, "abc");
    }

    @Test
    public void testAddStringFilter() {
        PivotSpecification pivotSpec = dataModelObject.createPivotSpecification();
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
            
            Assert.assertTrue(o.has("rule"));
            JsonObject rule = o.getAsJsonObject("rule");

            Assert.assertTrue(rule.has("comparator"));
            Assert.assertEquals(new JsonPrimitive("contains"), rule.get("comparator"));

            Assert.assertTrue(rule.has("compareTo"));
            Assert.assertEquals(new JsonPrimitive("abc"), rule.get("compareTo"));
        }
    }
    
    @Test
    public void testAddStringFilterAgainstSplunk() {
    	DataModel dm = (DataModel)service.getDataModels().get("internal_audit_logs");
    	DataModelObject dmo = dm.getObject("searches");
        PivotSpecification pivotSpec = dmo.createPivotSpecification();
        
        pivotSpec.addCellValue("host", "host_count", StatsFunction.COUNT);
        
        pivotSpec.addFilter("host", StringComparison.DOES_NOT_CONTAIN, "<$@^*%>");
        pivotSpec.addFilter("host", StringComparison.CONTAINS, ".");
        
        Pivot p =  pivotSpec.pivot();
        final Job j = p.run();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
			
			@Override
			public boolean predicate() {
				if (!j.isDone()) {
					j.refresh();
				}
				return j.isDone();
			}
		});
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddIpv4FilterOnWrongType() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addFilter("has_boris", IPv4Comparison.STARTS_WITH, "192.168");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIpv4FilterOnNonexistantField() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addFilter(createTemporaryName(), IPv4Comparison.STARTS_WITH, "192.168");
    }

    @Test
    public void testAddIpv4Filter() {
        PivotSpecification pivotSpec = dataModelObject.createPivotSpecification();
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

            Assert.assertTrue(o.has("rule"));
            JsonObject rule = o.getAsJsonObject("rule");

            Assert.assertTrue(rule.has("comparator"));
            Assert.assertEquals(new JsonPrimitive("startsWith"), rule.get("comparator"));

            Assert.assertTrue(rule.has("compareTo"));
            Assert.assertEquals(new JsonPrimitive("192.168"), rule.get("compareTo"));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddNumberFilterOnWrongType() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addFilter("has_boris", NumberComparison.AT_LEAST, 2.3);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNumberFilterOnNonexistantField() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addFilter(createTemporaryName(), NumberComparison.AT_LEAST, 2.3);
    }

    @Test
    public void testAddNumberFilter() {
        PivotSpecification pivotSpec = dataModelObject.createPivotSpecification();
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

            Assert.assertTrue(o.has("rule"));
            JsonObject rule = o.getAsJsonObject("rule");

            Assert.assertTrue(rule.has("comparator"));
            Assert.assertEquals(new JsonPrimitive(">="), rule.get("comparator"));

            Assert.assertTrue(rule.has("compareTo"));
            Assert.assertEquals(new JsonPrimitive((double)2.3), rule.get("compareTo"));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddLimitFilterOnNonexistentField() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addFilter("has_boris", "host",
                SortDirection.DEFAULT, 50, StatsFunction.COUNT);
    }

    @Test
    public void testAddLimitFilter() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addFilter("epsilon", "host", SortDirection.ASCENDING, 500, StatsFunction.AVERAGE);

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
            Assert.assertEquals(new JsonPrimitive("host"), o.get("attributeName"));

            Assert.assertTrue(o.has("attributeOwner"));
            Assert.assertEquals(new JsonPrimitive("BaseEvent"), o.get("attributeOwner"));

            Assert.assertTrue(o.has("limitType"));
            Assert.assertEquals(new JsonPrimitive("lowest"), o.get("limitType"));

            Assert.assertTrue(o.has("limitAmount"));
            Assert.assertEquals(new JsonPrimitive(500), o.get("limitAmount"));

            Assert.assertTrue(o.has("statsFn"));
            Assert.assertEquals(new JsonPrimitive("average"), o.get("statsFn"));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddNumericRowSplitOnWrongType() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addRowSplit("has_boris", "My Label");
    }

    @Test
    public void testAddNumericRowSplit() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addRowSplit("epsilon", "My Label");

        Assert.assertEquals(1, pivotSpecification.getRowSplits().size());
        for (PivotRowSplit prs : pivotSpecification.getRowSplits()) {
            Assert.assertTrue(prs instanceof NumberPivotRowSplit);
            JsonElement obj = prs.toJson();

            Assert.assertTrue(obj instanceof JsonObject);
            JsonObject o = (JsonObject)obj;

            Assert.assertTrue(o.has("fieldName"));
            Assert.assertEquals(new JsonPrimitive("epsilon"), o.get("fieldName"));

            Assert.assertTrue(o.has("owner"));
            Assert.assertEquals(new JsonPrimitive("test_data"), o.get("owner"));

            Assert.assertTrue(o.has("type"));
            Assert.assertEquals(new JsonPrimitive("number"), o.get("type"));

            Assert.assertTrue(o.has("label"));
            Assert.assertEquals(new JsonPrimitive("My Label"), o.get("label"));

            Assert.assertTrue(o.has("display"));
            Assert.assertEquals(new JsonPrimitive("all"), o.get("display"));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddRangeRowSplitOnWrongType() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addRowSplit("has_boris", "My Label", 0, 100, 20, 5);
    }

    @Test
    public void testAddRangeRowSplit() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addRowSplit("epsilon", "My Label", 0, 100, 20, 5);

        Assert.assertEquals(1, pivotSpecification.getRowSplits().size());
        for (PivotRowSplit prs : pivotSpecification.getRowSplits()) {
            Assert.assertTrue(prs instanceof RangePivotRowSplit);
            JsonElement obj = prs.toJson();

            Assert.assertTrue(obj instanceof JsonObject);
            JsonObject o = (JsonObject)obj;

            Assert.assertTrue(o.has("fieldName"));
            Assert.assertEquals(new JsonPrimitive("epsilon"), o.get("fieldName"));

            Assert.assertTrue(o.has("owner"));
            Assert.assertEquals(new JsonPrimitive("test_data"), o.get("owner"));

            Assert.assertTrue(o.has("type"));
            Assert.assertEquals(new JsonPrimitive("number"), o.get("type"));

            Assert.assertTrue(o.has("label"));
            Assert.assertEquals(new JsonPrimitive("My Label"), o.get("label"));

            Assert.assertTrue(o.has("display"));
            Assert.assertEquals(new JsonPrimitive("ranges"), o.get("display"));

            JsonObject ranges = new JsonObject();
            ranges.add("start", new JsonPrimitive(0));
            ranges.add("end", new JsonPrimitive(100));
            ranges.add("size", new JsonPrimitive(20));
            ranges.add("maxNumberOf", new JsonPrimitive(5));
            Assert.assertTrue(o.has("ranges"));
            Assert.assertEquals(ranges, o.get("ranges"));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddBooleanRowSplitOnWrongType() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addRowSplit("epsilon", "My Label", "true", "false");
    }

    @Test
    public void testAddBooleanRowSplit() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addRowSplit("has_boris", "My Label", "is_true", "is_false");

        Assert.assertEquals(1, pivotSpecification.getRowSplits().size());
        for (PivotRowSplit prs : pivotSpecification.getRowSplits()) {
            Assert.assertTrue(prs instanceof BooleanPivotRowSplit);
            JsonElement obj = prs.toJson();

            Assert.assertTrue(obj instanceof JsonObject);
            JsonObject o = (JsonObject)obj;

            JsonObject expected = new JsonObject();
            expected.add("fieldName", new JsonPrimitive("has_boris"));
            expected.add("label", new JsonPrimitive("My Label"));
            expected.add("owner", new JsonPrimitive("test_data"));
            expected.addProperty("type", "boolean");
            expected.addProperty("trueLabel", "is_true");
            expected.addProperty("falseLabel", "is_false");

            Assert.assertEquals(expected, o);
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddTimestampRowSplitOnWrongType() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addRowSplit("epsilon", "My Label", "true", "false");
    }

    @Test
    public void testAddTimestampRowSplit() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addRowSplit("_time", "My Label", TimestampBinning.DAY);

        Assert.assertEquals(1, pivotSpecification.getRowSplits().size());
        for (PivotRowSplit prs : pivotSpecification.getRowSplits()) {
            Assert.assertTrue(prs instanceof TimestampPivotRowSplit);
            JsonElement obj = prs.toJson();

            Assert.assertTrue(obj instanceof JsonObject);
            JsonObject o = (JsonObject)obj;

            JsonObject expected = new JsonObject();
            expected.add("fieldName", new JsonPrimitive("_time"));
            expected.add("label", new JsonPrimitive("My Label"));
            expected.add("owner", new JsonPrimitive("BaseEvent"));
            expected.addProperty("type", "timestamp");
            expected.addProperty("period", "day");

            Assert.assertEquals(expected, o);
        }
    }

    @Test
    public void testAddStringRowSplit() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addRowSplit("host", "My Label");

        Assert.assertEquals(1, pivotSpecification.getRowSplits().size());
        for (PivotRowSplit prs : pivotSpecification.getRowSplits()) {
            Assert.assertTrue(prs instanceof StringPivotRowSplit);
            JsonElement found = prs.toJson();

            JsonObject expected = new JsonObject();
            expected.addProperty("fieldName", "host");
            expected.addProperty("label", "My Label");
            expected.addProperty("owner", "BaseEvent");
            expected.addProperty("type", "string");

            Assert.assertEquals(expected, found);
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddNumericColumnSplitOnWrongType() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addColumnSplit("has_boris");
    }

    @Test
    public void testAddNumericColumnSplit() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addColumnSplit("epsilon");

        Assert.assertEquals(1, pivotSpecification.getColumnSplits().size());
        for (PivotColumnSplit prs : pivotSpecification.getColumnSplits()) {
            Assert.assertTrue(prs instanceof NumericPivotColumnSplit);
            JsonElement obj = prs.toJson();

            Assert.assertTrue(obj instanceof JsonObject);
            JsonObject o = (JsonObject)obj;

            Assert.assertTrue(o.has("fieldName"));
            Assert.assertEquals(new JsonPrimitive("epsilon"), o.get("fieldName"));

            Assert.assertTrue(o.has("owner"));
            Assert.assertEquals(new JsonPrimitive("test_data"), o.get("owner"));

            Assert.assertTrue(o.has("type"));
            Assert.assertEquals(new JsonPrimitive("number"), o.get("type"));

            Assert.assertTrue(o.has("display"));
            Assert.assertEquals(new JsonPrimitive("all"), o.get("display"));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddRangeColumnSplitOnWrongType() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addColumnSplit("has_boris", 0, 100, 20, 5);
    }

    @Test
    public void testAddRangeColumnSplit() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addColumnSplit("epsilon", 0, 100, 20, 5);

        Assert.assertEquals(1, pivotSpecification.getColumnSplits().size());
        for (PivotColumnSplit prs : pivotSpecification.getColumnSplits()) {
            Assert.assertTrue(prs instanceof RangePivotColumnSplit);
            JsonElement obj = prs.toJson();

            Assert.assertTrue(obj instanceof JsonObject);
            JsonObject o = (JsonObject)obj;

            Assert.assertTrue(o.has("fieldName"));
            Assert.assertEquals(new JsonPrimitive("epsilon"), o.get("fieldName"));

            Assert.assertTrue(o.has("owner"));
            Assert.assertEquals(new JsonPrimitive("test_data"), o.get("owner"));

            Assert.assertTrue(o.has("type"));
            Assert.assertEquals(new JsonPrimitive("number"), o.get("type"));

            Assert.assertTrue(o.has("display"));
            Assert.assertEquals(new JsonPrimitive("ranges"), o.get("display"));

            JsonObject ranges = new JsonObject();
            ranges.add("start", new JsonPrimitive("0"));
            ranges.add("end", new JsonPrimitive("100"));
            ranges.add("size", new JsonPrimitive("20"));
            ranges.add("maxNumberOf", new JsonPrimitive("5"));
            Assert.assertTrue(o.has("ranges"));
            Assert.assertEquals(ranges, o.get("ranges"));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddBooleanColumnSplitOnWrongType() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addColumnSplit("epsilon", "true", "false");
    }

    @Test
    public void testAddBooleanColumnSplit() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addColumnSplit("has_boris", "is_true", "is_false");

        Assert.assertEquals(1, pivotSpecification.getColumnSplits().size());
        for (PivotColumnSplit prs : pivotSpecification.getColumnSplits()) {
            Assert.assertTrue(prs instanceof BooleanPivotColumnSplit);
            JsonElement obj = prs.toJson();

            Assert.assertTrue(obj instanceof JsonObject);
            JsonObject o = (JsonObject)obj;

            JsonObject expected = new JsonObject();
            expected.add("fieldName", new JsonPrimitive("has_boris"));
            expected.add("owner", new JsonPrimitive("test_data"));
            expected.addProperty("type", "boolean");
            expected.addProperty("trueLabel", "is_true");
            expected.addProperty("falseLabel", "is_false");

            Assert.assertEquals(expected, o);
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddTimestampColumnSplitOnWrongType() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addColumnSplit("epsilon", "true", "false");
    }

    @Test
    public void testAddTimestampColumnSplit() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addColumnSplit("_time", TimestampBinning.DAY);

        Assert.assertEquals(1, pivotSpecification.getColumnSplits().size());
        for (PivotColumnSplit prs : pivotSpecification.getColumnSplits()) {
            Assert.assertTrue(prs instanceof TimestampPivotColumnSplit);
            JsonObject found = prs.toJson();

            JsonObject expected = new JsonObject();
            expected.add("fieldName", new JsonPrimitive("_time"));
            expected.add("owner", new JsonPrimitive("BaseEvent"));
            expected.addProperty("type", "timestamp");
            expected.addProperty("period", "day");

            Assert.assertEquals(expected, found);
        }
    }

    @Test
    public void testAddStringColumnSplit() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addColumnSplit("host");

        Assert.assertEquals(1, pivotSpecification.getColumnSplits().size());
        for (PivotColumnSplit prs : pivotSpecification.getColumnSplits()) {
            Assert.assertTrue(prs instanceof StringPivotColumnSplit);
            JsonObject found = prs.toJson();

            JsonObject expected = new JsonObject();
            expected.addProperty("fieldName", "host");
            expected.addProperty("owner", "BaseEvent");
            expected.addProperty("type", "string");

            Assert.assertEquals(expected, found);
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNonexistantFieldToCellValue() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addCellValue("nonexistant", "my_label", StatsFunction.COUNT);
    }

    @Test
    public void testAddStringCellValue() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addCellValue("source", "Source Value", StatsFunction.DISTINCT_COUNT);

        Assert.assertEquals(1, pivotSpecification.getCellValues().size());
        for (PivotCellValue pcv : pivotSpecification.getCellValues()) {
            JsonObject found = pcv.toJson();
            JsonObject expected = new JsonObject();
            expected.addProperty("fieldName", "source");
            expected.addProperty("owner", "BaseEvent");
            expected.addProperty("type", "string");
            expected.addProperty("label", "Source Value");
            expected.addProperty("value", "dc");
            expected.addProperty("sparkline", false);

            Assert.assertEquals(expected, found);
        }
    }

    @Test
    public void testAddIpv4CellValue() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addCellValue("hostip", "Source Value", StatsFunction.DISTINCT_COUNT);

        Assert.assertEquals(1, pivotSpecification.getCellValues().size());
        for (PivotCellValue pcv : pivotSpecification.getCellValues()) {
            JsonObject found = pcv.toJson();
            JsonObject expected = new JsonObject();
            expected.addProperty("fieldName", "hostip");
            expected.addProperty("owner", "test_data");
            expected.addProperty("type", "ipv4");
            expected.addProperty("label", "Source Value");
            expected.addProperty("value", "dc");
            expected.addProperty("sparkline", false);

            Assert.assertEquals(expected, found);
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIllegalStatsFunction() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addCellValue("source", "Source Value", StatsFunction.SUM);

    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoBooleanCellValues() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addCellValue("has_boris", "Source Value", StatsFunction.DISTINCT_VALUES);
    }

    @Test(expected= HttpException.class)
    public void testEmptyPivotGivesError() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        Pivot pivot = pivotSpecification.pivot();
    }

    @Test
    public void testSimplePivotWithoutNamespace() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addRowSplit("has_boris", "Has Boris", "meep", "hilda");
        pivotSpecification.addCellValue("hostip", "Distinct IPs", StatsFunction.DISTINCT_COUNT);


        Pivot pivot = pivotSpecification.pivot();
        Assert.assertNull(pivot.getAcceleratedQuery());
        Assert.assertTrue(pivot.getPivotQuery().startsWith("| pivot"));
        final Job job = pivot.run();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return job.isReady();
            }
        });
        Assert.assertTrue(job.getSearch().startsWith("| pivot"));
    }

    @Test
    public void testSimplePivotWithSparkline() {
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addRowSplit("has_boris", "Has Boris", "meep", "hilda");
        pivotSpecification.addCellValue("hostip", "Distinct IPs", StatsFunction.DISTINCT_COUNT);


        Pivot pivot = pivotSpecification.pivot();
        Assert.assertNull(pivot.getAcceleratedQuery());
        Assert.assertTrue(pivot.getPivotQuery().startsWith("| pivot"));
        final Job job = pivot.run();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return job.isReady();
            }
        });
        Assert.assertTrue(job.getSearch().startsWith("| pivot"));
    }

    @Test
    public void testSimplePivotWithNamespace() {
        Job adhocJob = dataModelObject.createLocalAccelerationJob();
        PivotSpecification pivotSpecification = dataModelObject.createPivotSpecification();
        pivotSpecification.addRowSplit("has_boris", "Has Boris", "meep", "hilda");
        pivotSpecification.addCellValue("hostip", "Distinct IPs", StatsFunction.DISTINCT_COUNT);
        pivotSpecification.setAccelerationJob(adhocJob);

        Pivot pivot = pivotSpecification.pivot();
        Assert.assertNotNull(pivot.getAcceleratedQuery());

        final Job job = pivot.run();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return job.isReady();
            }
        });

        Assert.assertTrue(job.getSearch().startsWith("| tstats"));
        adhocJob.cancel();
    }

    @Test
    public void testColumnRangeSplit() {
        // This test is here because we had a problem with fields that were supposed to be
        // numbers being expected as strings in Splunk 6.0. This was fixed in Splunk 6.1, and accepts
        // either strings or numbers.
        DataModelObject searches = service.getDataModels().get("internal_audit_logs").getObject("searches");
        PivotSpecification pivotSpecification = new PivotSpecification(searches);
        pivotSpecification.addRowSplit("user", "Executing user");
        pivotSpecification.addColumnSplit("exec_time", 0, 12, 5, 4);
        pivotSpecification.addCellValue("search", "Search Query", StatsFunction.DISTINCT_VALUES);

        Pivot pivot = pivotSpecification.pivot();
        final Job job = pivot.run();

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return job.isDone();
            }
        });

        job.cancel();

    }
}
