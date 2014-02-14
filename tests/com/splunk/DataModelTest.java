package com.splunk;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class DataModelTest extends SDKTestCase {
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
    public void testDataModelCollectionCreateAndDelete() {
        EntityCollection<DataModel> dataModels = service.getDataModels();

        DataModelArgs args = new DataModelArgs();
        args.setRawDescription(streamToString(openResource("data/datamodels/empty_data_model.json")));

        int initialN = dataModels.size();

        DataModel dataModel = dataModels.create(createTemporaryName(), args);
        Assert.assertEquals(initialN+1, dataModels.size());

        dataModel.remove();
        dataModels.refresh();
        Assert.assertEquals(initialN, dataModels.size());
    }

    @Test
    public void testDataModelWithZeroObjects() {
        EntityCollection<DataModel> dataModels = service.getDataModels();

        DataModelArgs args = new DataModelArgs();
        args.setRawDescription(streamToString(openResource("data/datamodels/empty_data_model.json")));
        DataModel model = dataModels.create(createTemporaryName(), args);

        Assert.assertEquals(0, model.getObjects().size());

        // Make sure this works after refresh as well.
        model.refresh();
        Assert.assertEquals(0, model.getObjects().size());
    }

    @Test
    public void testDataModelWithOneObject() {
        EntityCollection<DataModel> dataModels = service.getDataModels();

        DataModelArgs args = new DataModelArgs();
        args.setRawDescription(streamToString(openResource("data/datamodels/object_with_one_search.json")));
        DataModel model = dataModels.create(createTemporaryName(), args);

        Assert.assertEquals(1, model.getObjects().size());

        // Make sure this works after refresh as well.
        model.refresh();
        Assert.assertEquals(1, model.getObjects().size());
    }

    @Test
    public void testDataModelWithTwoObjects() {
        EntityCollection<DataModel> dataModels = service.getDataModels();

        DataModelArgs args = new DataModelArgs();
        args.setRawDescription(streamToString(openResource("data/datamodels/object_with_two_searches.json")));
        DataModel model = dataModels.create(createTemporaryName(), args);

        Assert.assertEquals(2, model.getObjects().size());

        // Make sure this works after refresh as well.
        model.refresh();
        Assert.assertEquals(2, model.getObjects().size());
    }

    @Test
    public void testGetAndContainsObjectWork() {
        EntityCollection<DataModel> dataModels = service.getDataModels();

        DataModelArgs args = new DataModelArgs();
        args.setRawDescription(streamToString(openResource("data/datamodels/object_with_two_searches.json")));
        DataModel model = dataModels.create(createTemporaryName(), args);

        Assert.assertTrue(model.containsObject("search1"));
        Assert.assertTrue(model.containsObject("search2"));

        DataModelObject search1 = model.getObject("search1");
        Assert.assertNotNull(search1);
        Assert.assertEquals("\u0bf5\u0bf1\u0bf0\u0bef - search 1", search1.getDisplayName());

        DataModelObject search2 = model.getObject("search2");
        Assert.assertNotNull(search2);
        Assert.assertEquals("\u0bf5\u0bf1\u0bf0\u0bef - search 2", search2.getDisplayName());
    }

    @Test
    public void testDataModelWithUnicodeAttributes() {
        EntityCollection<DataModel> dataModels = service.getDataModels();

        String modelName = createTemporaryName();

        DataModelArgs args = new DataModelArgs();
        args.setRawDescription(streamToString(openResource("data/datamodels/model_with_unicode_headers.json")));
        DataModel model = dataModels.create(modelName, args);

        Assert.assertEquals(modelName, model.getName());
        Assert.assertEquals("\u1029\u1699\u0bf5", model.getDisplayName());
        Assert.assertEquals("\u0bf5\u0bf1\u0bf0\u0bef", model.getDescription());
    }

    @Test
    public void testDataModelWithEmptyAttributes() {
        EntityCollection<DataModel> dataModels = service.getDataModels();

        String modelName = createTemporaryName();

        DataModelArgs args = new DataModelArgs();
        args.setRawDescription(streamToString(openResource("data/datamodels/model_with_empty_headers.json")));
        DataModel model = dataModels.create(modelName, args);

        Assert.assertEquals(modelName, model.getName());
        Assert.assertEquals("", model.getDisplayName());
        Assert.assertEquals("", model.getDescription());
    }

    @Test
    public void testAccelerationSettings() {
        EntityCollection<DataModel> dataModels = service.getDataModels();

        DataModelArgs args = new DataModelArgs();
        args.setRawDescription(streamToString(openResource("data/datamodels/object_with_two_searches.json")));
        DataModel model = dataModels.create(createTemporaryName(), args);

        model.setAcceleration(true);
        model.setEarliestAcceleratedTime("-2mon");
        model.setAccelerationCronSchedule("5/* * * * *");

        Assert.assertTrue(model.isAccelerated());
        Assert.assertEquals("-2mon", model.getEarliestAcceleratedTime());
        Assert.assertEquals("5/* * * * *", model.getAccelerationCronSchedule());

        model.update();
        model.refresh();

        Assert.assertTrue(model.isAccelerated());
        Assert.assertEquals("-2mon", model.getEarliestAcceleratedTime());
        Assert.assertEquals("5/* * * * *", model.getAccelerationCronSchedule());

        model.setAcceleration(false);
        model.setEarliestAcceleratedTime("-1mon");
        model.setAccelerationCronSchedule("* * * * *");

        Assert.assertFalse(model.isAccelerated());
        Assert.assertEquals("-1w", model.getEarliestAcceleratedTime());
        Assert.assertEquals("* * * * *", model.getAccelerationCronSchedule());

    }
}
