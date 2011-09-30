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

import com.splunk.data.Entity;
import com.splunk.http.RequestMessage;
import com.splunk.http.ResponseMessage;

// UNDONE: Need to refactor for more than just get's.

/**
 * Mid level splunk sdk API; uses lower level Binding sdk API.
 */

public class Client {

    Service service = null;

    // UNDONE: need to normalize paths. URL namespace processing needs to be in place
    private final String PATH_APPS = "/services/apps/local/";
    private final String PATH_CAPABILITIES = "authorization/capabilities/";
    private final String PATH_CONF = "configs/conf-%s/"; // (config file)
    private final String PATH_CONFS = "properties/";
    private final String PATH_INDEXES = "data/indexes/";
    private final String PATH_INPUTS = "data/inputs/";
    private final String PATH_JOBS = "search/jobs/";
    private final String PATH_LOGGER = "server/logger/";
    private final String PATH_MESSAGES = "messages/";
    private final String PATH_ROLES = "authentication/roles/";
    private final String PATH_STANZA = "configs/conf-%s/%s"; // (config file, stanza)
    private final String PATH_USERS = "authentication/users/";
    private final String PATH_DEP_SERV = "deployment/server/";
    private final String PATH_DEP_CLI = "deployment/client/";
    private final String PATH_DEP_SERV_CL = "deployment/serverclass/";
    private final String PATH_DEP_TENANT = "deployment/tenants/";
    private final String PATH_DSEARCH_PEER = "search/distributed/peers/";
    private final String PATH_DSEARCH_CONF = "search/distributed/config/";

    private final String XNAME_ENTRY = "{http://www.w3.org/2005/Atom}entry";
    private final String XNAME_CONTENT = "{http://www.w3.org/2005/Atom}content";

    public Client(Service serv) {
        service = serv;
    }

    private ResponseMessage get(String path) throws Exception {
        ResponseMessage response = service.get(path);
        int status = response.getStatus();
        if (status > 201) {
            System.out.println("get failed, status is " + status);
        }
        return response;
    }

    /**
     * app
     */

    public Entity app(String name) throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(get(PATH_APPS + name).getContent());
    }

    public Entity apps() throws Exception {
        Convert converter = new Convert();
        return converter.convertXMLData(get(PATH_APPS).getContent());
    }

    /**
     * apps
     */

/*    public Collection apps(Service service) throws Exception {
        return new Collection(PATH_APPS);
    }

    /**
     * conf
     */
/*
    public com.splunk.Data.Entity conf (Service service, String name) throws Exception {
        return new Entity(PATH_CONF + name);
    }

    /**
     * confs
     */
/*
    public Entity confs (Service service) throws Exception {
        return new Entity(PATH_CONFS);
    }

    /**
     * index
     */
/*
    public Entity index(Service service, String name) throws Exception {
        return new Entity(PATH_INDEXES + name);
    }

    /**
     * inputs
     */
/*
    public Entity inputs(Service service) throws Exception {
        return new Entity(PATH_INPUTS);
    }

    /**
     * job
     */
/*
    public Entity Job (Service service) throws Exception {
        return new Entity(PATH_JOBS);
    }

    /**
     * jobs
     */
/*
    public Entity Jobs (Service service) throws Exception {
        return new Entity(PATH_JOBS);
    }

    /**
     * messagess
     */
/*
    public Entity messages(Service service) throws Exception {
        return new Entity(PATH_MESSAGES);

    }
    */
}
