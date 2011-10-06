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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeploymentServerclass extends Client {

    private final String path = "/services/deployment/serverclass/";

    public DeploymentServerclass(Service service) {
        super(service);
    }

    public Entity get(String name) throws Exception {
        return super.get(path + name);
    }

    public Entity get() throws Exception {
        return super.get(path);
    }

    public Entity create(String name) throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("name", name);
        return super.create(path, args);
    }

    public Entity create(String name, Map<String,String> args) throws Exception {
        if (args.containsKey("name")) {
            throw new Exception("name not allowed in argument map if explicitly requested");
        }
        args.put("name", name);
        return super.create(path, args);
    }

    public Entity create(Map<String,String> args) throws Exception {
        if (!args.containsKey("name")) {
            throw new Exception("name must be in argument map");
        }
        return super.create(path, args);
    }

    public Entity delete(String name) throws Exception {
        return super.get(path + name);
    }

    public List<String> nameList(String name) throws Exception {
        return super.nameList(path + name);
    }

    public List<String> nameList() throws Exception {
        return nameList("");
    }
}
