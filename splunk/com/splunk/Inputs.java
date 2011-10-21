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
import java.util.Map;

public class Inputs extends Collection {

    private Map<String,String> kindMap = new HashMap<String, String>();
    private void initmap() {
        kindMap.put("ad", "ad");
        kindMap.put("monitor", "monitor");
        kindMap.put("registry", "registry");
        kindMap.put("script", "script");
        kindMap.put("ad", "ad");
        kindMap.put("tcp", "tcp/raw");
        kindMap.put("splunktcp", "tcp/cooked");
        kindMap.put("udp", "udp");
        kindMap.put("win-event-log-collections", "win-event-log-collections");
        kindMap.put("win-perfmon", "win-perfmon");
        kindMap.put("win-wmi-collections", "win-wmi-collections");
    }

    public Inputs(Service service) {
        super(service, "/services/data/inputs/");
        initmap();
    }

    public Inputs create(String kind,
                          String name,
                          Map<String,String> args) throws Exception {
        if (!kindMap.containsKey(kind)) {
            throw new Exception("Input creation requires a valid 'kind', from: "
                    + kindMap);
        }
        args.put("name", name);
        super.post(kindMap.get(kind), args);
        return this;
    }

    public Inputs create(String kind, String name) throws Exception {
        Map<String,String> args = new HashMap<String, String>();
        return create(kind, name, args);
    }

    public Inputs delete(String kind, String name) throws Exception {
        if (!kindMap.containsKey(kind)) {
            throw new Exception("Input creation requires a valid 'kind', from: "
                    + kindMap);
        }
        super.delete(kindMap.get(kind) + "/" + name);
        return this;
    }

    public Map<String,String> kinds() {
        return kindMap;
    }

    public String kindpath(String kind) {
        return kindMap.get(kind);
    }
}
