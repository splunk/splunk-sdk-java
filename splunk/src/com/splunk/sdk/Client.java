package com.splunk.sdk;

import java.io.IOException;
import java.util.HashMap;

/**
 * Client
 * version 1.0
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


// UNDONE: need to deal with data representation -- other than straight XML.

public class Client {

    private final String PATH_APPS = "apps/local/";
    private final String PATH_CAPABILITIES = "authorization/capabilities/";
    private final String PATH_CONF = "configs/conf-%s/"; // (config file
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

    public Binding bind = null;

    public Client() {
        // wkcfix do we need a default constructor?
    }

    public void connect() throws IOException {
        this.bind = new Binding();
        this.bind.login();
    }

    public void connect(String host, String port, String username, String password, String scheme) throws IOException {
        this.bind = new Binding();
        this.bind.login(host, port, username, password, scheme);
    }

    public Collection apps() throws IOException {
        return new Collection(bind, PATH_APPS, "apps");
    }

    public class Endpoint {
        Binding binding = null;
        String path = null;

        public Endpoint(Binding bind, String pth) {
            if (!pth.endsWith("/")) pth += "/";
            binding = bind;
            path = pth;
        }

        // overloads for get
        public String get(String relpath, HashMap<String,String> mymap) throws IOException {
            String fullpath = path + relpath;
            return binding.get(path, mymap);
        }
        public String get(HashMap<String,String> mymap) throws IOException {
            return binding.get(path, mymap);
        }
        public String get() throws IOException {
            return binding.get(path);
        }

        // overloads for post
        public String post(String relpath, HashMap<String,String> mymap) throws IOException {
            String fullpath = path + relpath;
            return binding.get(path, mymap);
        }
        public String post(HashMap<String,String> mymap) throws IOException {
            return binding.get(path, mymap);
        }
        public String post() throws IOException {
            return binding.get(path);
        }

        // overloads for post
        public String delete(String relpath, HashMap<String,String> mymap) throws IOException {
            String fullpath = path + relpath;
            return binding.delete(path, mymap);
        }
        public String delete(HashMap<String,String> mymap) throws IOException {
            return binding.delete(path, mymap);
        }
        public String delete() throws IOException {
            return binding.delete(path);
        }

    }

    public class Collection {

        Endpoint endp = null;
        String name = null;

        public Collection(Binding service, String path, String name) {
            this.endp = new Endpoint(service, path);
            this.name = name;
        }

        // wkcfix -- add methods to deal with individual elements of the collection
        // i.e. Entities

        public String get() throws IOException {
            return endp.get();
        }
        public String post() throws IOException {
            return endp.post();
        }
        public String delete() throws IOException {
            return endp.delete();
        }

    }

    public class Entity {

        Endpoint endp = null;
        String name = null;

        public Entity(Binding service, String path, String name) {
            this.endp = new Endpoint(service, path);
            this.name = name;
        }

        // wkcfix -- add methods or cleanup for individual elements, not just XML blobs
        public String get() throws IOException {
            return endp.get();
        }
        public String post() throws IOException {
            return endp.post();
        }
        public String delete() throws IOException {
            return endp.delete();
        }
    }

    public class Conf  {

    }

    public class Index {

    }

    public class Input {

    }

    public class Inputs {

    }

    public class Job {

    }

    public class Jobs {

    }

    public class Message {

    }
}
