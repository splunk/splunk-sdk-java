/*
 * Copyright 2011 Splunk, Inc.
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

// UNDONE: Implement get/post/delete args
// UNDONE: Standrad entity methods

import com.splunk.atom.*;
import com.splunk.http.ResponseMessage;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EntityCollection extends Resource implements Iterable<Entity> {
    Map<String, Entity> entities;

    Class entityClass = Entity.class;

    public EntityCollection(Service service, String path) {
        super(service, path);
    }

    public EntityCollection(Service service, String path, Class entityClass) {
        super(service, path);
        this.entityClass = entityClass;
    }

    public Entity create(String name) {
        return null; // UNDONE
    }

    public Map<String, Entity> getEntities() {
        validate();
        return this.entities;
    }

    public Iterator<Entity> iterator() {
        validate();
        return this.entities.values().iterator();
    }

    void load(AtomFeed value) {
        try {
            super.load(value);
            Constructor ctor = entityClass.getConstructor(
                new Class[] { Service.class, String.class });
            Object[] args = new Object[2];
            args[0] = service;
            this.entities = new HashMap<String, Entity>();
            for (AtomEntry entry : value.entries) {
                args[1] = entry.id;
                Entity entity = (Entity)ctor.newInstance(args);
                entity.load(entry);
                this.entities.put(entry.id, entity);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void refresh() {
        try {
            ResponseMessage response = get();
            assert(response.getStatus() == 200); // UNDONE
            AtomFeed feed = AtomFeed.create(response.getContent());
            load(feed);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

