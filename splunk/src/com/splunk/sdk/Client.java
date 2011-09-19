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

package com.splunk.sdk;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;

// UNDONE: need to deal with data representation -- other than straight XML.

/**
 * Mid level splunk sdk API; uses lower level Binding sdk API.
 */

public class Client {

    private final String PATH_APPS = "apps/local/";
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

    public Binding bind = null;

    public Client() {
        // TODO: wkcfix do we need a default constructor?
    }

    /**
     * Connect to a splunkd instance using default login arguments
     *
     * @throws IOException
     */
    public void connect() throws IOException {
        bind = new Binding();
        bind.login();
    }

    /**
     * Connect to a splunkd instance using supplied arguments. NB: all need to be supplied.
     *
     * @param host     hostname (DNS or IPaddress) of splunkd server
     * @param port     port that splunkd listens on
     * @param username username to login as
     * @param password password to login with
     * @param scheme   either http or https
     * @throws IOException
     */
    public void connect(String host, String port, String username, String password, String scheme) throws IOException {
        bind = new Binding();
        bind.login(host, port, username, password, scheme);
    }

    /**
     * Endpoint class: Represents the class all accesses use. Get/post/delete.
     */

    public class Endpoint extends Binding {
        String path = null;

        public Endpoint() {
            // nothing
        }

        public Endpoint(Binding bind, String pth) {
            if (!pth.endsWith("/")) pth += "/";
            path = pth;
        }
    }

    /**
     * Collection class: Represents a collection of splunkd objects.
     */

    public class Entity  extends Endpoint {

        Endpoint endp = null;

        public Entity() {
            // nothing
        }

        public Entity (Binding service, String pth) {
            endp = new Endpoint(service, pth);
        }

        public HttpURLConnection get() throws IOException {
            // TODO: wkcfix, correct way to reach into extended classes?
            return bind.get(endp.path);
        }
    }

    /**
     * Entity class: Represents a single splunkd object.
     */

    public class Collection extends Entity {

        Entity ent = null;

        public Collection() {
            // nothing
        }

        public Collection(Binding service, String  path) {
            ent = new Entity(service, path)  ;
        }

        public HttpURLConnection get() throws IOException {
            // TODO: wkcfix, correct way to reach into extended classes?
            return bind.get(ent.endp.path);
        }

        // TODO: in addition to get/post/delete, add semantical routines like create, delete, list, etc.
    }

    /**
     * apps class: Represents the application splunkd objects
     */

    public Entity app(String name) {
        return new Entity(bind, PATH_APPS + name);
    }

    public Collection apps() {
        return new Collection(bind, PATH_APPS);
    }

    /**
     * apps class: Represents the configuration splunkd objects
     */

    public class Conf {

    }

    /**
     * apps class: Represents the Index splunkd objects
     */

    public class Index {

    }

    /**
     * apps class: Represents the Input splunkd objects
     */

    public class Input {

    }

    /**
     * apps class: Represents the Inputs splunkd objects
     */

    public class Inputs {

    }

    /**
     * apps class: Represents the JOB splunkd objects
     */

    public class Job {

    }

    /**
     * apps class: Represents the Jobs splunkd objects
     */

    public class Jobs {

    }

    /**
     * apps class: Represents the Message splunkd objects
     */

    public class Message {

    }
}
