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

import java.util.Map;

public class DataModelCollection extends EntityCollection<DataModel> {
    DataModelCollection(Service service) {
        super(service, "datamodel/model", DataModel.class);
        this.refreshArgs.put("concise", "0");
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
        Args revisedArgs = Args.create(args);
        // concise=0 forces the server to return all details of the newly
        // created data model.
        if (!args.containsKey("concise")) {
            revisedArgs = revisedArgs.add("concise", "0");
        }
        return super.create(name, revisedArgs);
    }
}
