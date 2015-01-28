/*
 * Copyright 2015 Splunk, Inc.
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

package com.splunk.examples.endpoint_instantiation;

import com.splunk.*;

/**
 * This example shows how to access any Splunk REST API endpoint.
 * Here, we are just getting an EntityCollection of Entity objects representing
 * apps on the Splunk server.
 *
 * You can also write a class which inherits from the Entity class.
 * A minimal example of this is:
 *
 *     public class MyEntity extends Entity {
 *         MyEntity(Service service, String path) {
 *            super(service, path);
 *         }
 *     }
 *
 * Then, you can write a class which inherits from the EntityCollection class.
 * A minimal example of this is:
 *
 *     public class MyEntityCollection extends EntityCollection<MyEntity> {
 *         MyEntityCollection(Service service) {
 *             super(service, "path/hardcoded", MyEntity.class, new Args());
 *         }
 *     }
 */

public class Program {
    public static void main(String[] args) {
        Command command = Command.splunk("info").parse(args);
        Service service = Service.connect(command.opts);
        
        String mySplunkRESTPath = "apps/local";
        
        EntityCollection myCollection = new EntityCollection(service, mySplunkRESTPath, Entity.class, new Args());
        
        System.out.println("Found " + myCollection.size() + " Splunk apps:");

        for (Object myEntity : myCollection.values()) {
            Entity entity = (Entity) myEntity;
            System.out.println("\t" + entity.getName());
        }
    }
}