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

import com.splunk.atom.*;
import com.splunk.http.ResponseMessage;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EntityCollection<T extends Entity> extends ResourceCollection<T> {
    EntityCollection(Service service, String path) {
        super(service, path, Entity.class);
    }
    
    public EntityCollection(Service service, String path, Class itemClass) {
        super(service, path, itemClass);
    }

    public T create(String name) {
        return create(name, null);
    }

    public T create(String name, Args extra) {
        Args args = new Args();
        args.put("name", name);
        if (extra != null) args.putAll(extra);
        service.post(path, args);
        invalidate();
        return get(name);
    }

    public T remove(Object key) {
        validate();
        if (!containsKey(key)) return null;
        T entity = items.get(key);
        entity.remove();
        items.remove(key);
        invalidate();
        return entity;
    }
}
