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
}
