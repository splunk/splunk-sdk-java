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

import java.util.List;

public class DeploymentServerclass extends Client {

    private final String PATH_DEP_SERV_CL = "/services/deployment/serverclass/";

    public DeploymentServerclass(Service service) {
        super(service);
    }

    public Entity get(String name) throws Exception {
        return super.get(PATH_DEP_SERV_CL + name);
    }

    public Entity get() throws Exception {
        return super.get(PATH_DEP_SERV_CL);
    }

    public Entity delete(String name) throws Exception {
        return super.get(PATH_DEP_SERV_CL + name);
    }

    public List<String> list(String name) throws Exception {
        return super.list(PATH_DEP_SERV_CL + name);
    }

    public List<String> list() throws Exception {
        return super.list(PATH_DEP_SERV_CL);
    }
}
