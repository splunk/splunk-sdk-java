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

package com.splunk;

public class ConfCollection 
    extends ResourceCollection<EntityCollection<Entity>> 
{
    public ConfCollection(Service service) {
        super(service, "properties", EntityCollection.class);
    }

    public EntityCollection<Entity> create(String name) {
        return create(name, null);
    }

    public EntityCollection<Entity> create(String name, Args extra) {
        Args args = new Args("__conf", name);
        if (extra != null) args.putAll(extra);
        service.post(path, args);
        invalidate();
        return get(name);
    }
    
    // Search jobs use the sid value as the key.
    @Override protected String itemPath(AtomEntry entry) {
        return String.format("configs/conf-%s", entry.title);
    }
}
