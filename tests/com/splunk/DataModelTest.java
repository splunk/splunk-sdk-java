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
    }

    @Test
    public void testDataModelWithOneObject() {
        EntityCollection<DataModel> dataModels = service.getDataModels();

        DataModelArgs args = new DataModelArgs();
        args.setRawDescription(streamToString(openResource("data/datamodels/object_with_one_search.json")));
        DataModel model = dataModels.create(createTemporaryName(), args);

        Assert.assertEquals(1, model.getObjects().size());
    }

    @Test
    public void testDataModelWithTwoObjects() {
        EntityCollection<DataModel> dataModels = service.getDataModels();

        DataModelArgs args = new DataModelArgs();
        args.setRawDescription(streamToString(openResource("data/datamodels/object_with_two_searches.json")));
        DataModel model = dataModels.create(createTemporaryName(), args);

        Assert.assertEquals(2, model.getObjects().size());
    }
}
