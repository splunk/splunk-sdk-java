package com.splunk;

import java.util.Map;

/**
 * Created by fross on 2/14/14.
 */
public class DataModelCollection extends EntityCollection<DataModel> {
    public DataModelCollection(Service service) {
        super(service, "datamodel/model", DataModel.class);
    }

    /**
     * Creates an entity in this collection.
     *
     * @param name The name of the entity.
     * @return The entity.
     */
    @Override
    public DataModel create(String name) {
        return create(name, (Map)null);
    }

    /**
     * Creates an entity in this collection.
     *
     * @param name The name of the entity.
     * @param args Arguments for creating the entity.
     * @return The entity.
     */
    @Override
    public DataModel create(String name, Map args) {
        Args revisedArgs = Args.create(args).add("name", name);
        // concise=0 forces the server to return all details of the newly
        // created data model.
        if (!args.containsKey("concise")) {
            revisedArgs = revisedArgs.add("concise", "0");
        }
        DataModel model = super.create(name, revisedArgs);
        model.parseDescription(model.getContent().getString("description"));
        model.parseAcceleration(model.getContent().getString("acceleration"));
        return model;
    }
}
