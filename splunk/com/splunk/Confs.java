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

public class Confs extends  Client {

    private final String path = "/services/configs/conf-"; // (config file)
    private final String pathp = "/services/properties/";

   public Confs(Service service) {
        super(service);
    }

    public Entity get(String name) throws Exception {
        return super.get(path + name + "/");
    }

    public Entity get() throws Exception {
        return super.get(path);
    }

    public Entity create(String file, Map<String,String> args) throws Exception {
        if (!args.containsKey("name")) {
            throw new Exception("name (stanza) required in argument map if not explicitly passed");
        }
        return super.create(path + file + "/", args);
    }

    public Entity create(String file, String stanza) throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        args.put("name", stanza);
        return super.create(path + file + "/", args);
    }

    public Entity create(String file, String stanza, Map<String,String> args) throws Exception {
        if (args.containsKey("name")) {
            throw new Exception("name already found in arguments");
        }
        args.put("name", stanza);
        return super.create(path + file + "/", args);
    }

    public Entity delete(String file, String stanza) throws Exception {
        return super.delete(path + file + "/" + stanza);
    }

    public List<String> nameList(String name) throws Exception {
        return super.nameList(pathp + name + "/");
    }

    public List<String> nameList() throws Exception {
        return super.nameList(pathp);
    }
}
