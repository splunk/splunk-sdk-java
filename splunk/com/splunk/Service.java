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

// UNDONE: getAlerts
// UNDONE: getOutputs
// UNDONE: public Object parse(String query) {}
// UNDONE: public void restart() {}

package com.splunk;

import com.splunk.atom.Xml;
import com.splunk.http.*;

import java.util.List;

public class Service extends com.splunk.http.Service {
    protected String token = null;
    protected String namespace = null;
    private String prefix = null;

    public static String DEFAULT_HOST = "localhost";
    public static int DEFAULT_PORT = 8089;
    public static String DEFAULT_SCHEME = "https";

    public Service(String host) {
        super(host);
    }

    public Service(String host, int port) {
        super(host, port);
    }

    public Service(String host, int port, String scheme) {
        super(host, port, scheme);
    }

    public Service(ServiceArgs args) {
        super();
        this.host = args.host == null ? DEFAULT_HOST : args.host;
        this.port = args.port == null ? DEFAULT_PORT : args.port;
        this.scheme = args.scheme == null ? DEFAULT_SCHEME : args.scheme;
        this.namespace = args.namespace;
    }

    // Ensures that the given path is fully qualified, prepending a
    // path prefix as necessarry.
    protected String fullpath(String path) {
        if (path.startsWith("/"))
            return path;
        if (namespace == null)
            return "/services/" + path;
        return String.format("/servicesNS/%s/%s", namespace, path);
    }

    public EntityCollection<Application> getApplications() {
        return new EntityCollection<Application>(this,
            "apps/local", Application.class);
    }

    public EntityCollection<Entity> getConfigurations() {
        return null; // UNDONE
    }

    public List<String> getCapabilities() {
        Entity caps = Entity.read(this, "authorization/capabilities");
        return (List<String>)caps.getValue("capabilities");
    }

    public DeploymentClient getDeploymentClient() {
        return DeploymentClient.read(this, "deployment/client");
    }

    public EntityCollection<Entity> getDeploymentServers() {
        return new EntityCollection<Entity>(this, "deployment/server");
    }

    public EntityCollection<Entity> getDeploymentServerClasses() {
        return new EntityCollection<Entity>(this, "deployment/serverclass");
    }

    public EntityCollection<Entity> getDeploymentTenants() {
        return new EntityCollection<Entity>(this, "deployment/tenants");
    }

    public EntityCollection<Entity> getDistributedPeers() {
        return new EntityCollection<Entity>(this, "search/distributed/peers");
    }

    public EntityCollection<Entity> getEventTypes() {
        return new EntityCollection<Entity>(this, "saved/eventtypes");
    }

    public EntityCollection<Index> getIndexes() {
        return new EntityCollection<Index>(this, "data/indexes", Index.class);
    }

    public ServiceInfo getInfo() {
        // UNDONE: Align the following with singleton protocol (reaad)
        return new ServiceInfo(this);
    }

    public EntityCollection<Entity> getInputs() {
        return null; //  UNDONE: flatten?
    }

    public JobCollection getJobs() {
        return new JobCollection(this, "search/jobs");
    }

    public EntityCollection<Entity> getLicenseGroups() {
        return new EntityCollection<Entity>(this, "licenser/groups");
    }

    public EntityCollection<Entity> getLicenseMessages() {
        return new EntityCollection<Entity>(this, "licenser/messages");
    }

    public EntityCollection<Entity> getLicensePools() {
        return new EntityCollection<Entity>(this, "licenser/pools");
    }

    public EntityCollection<Entity> getLicenseSlaves() {
        return new EntityCollection<Entity>(this, "licenser/slaves");
    }

    public EntityCollection<Entity> getLicenseStacks() {
        return new EntityCollection<Entity>(this, "licenser/stacks");
    }

    public EntityCollection<Entity> getLicenses() {
        return new EntityCollection<Entity>(this, "licenser/licenses");
    }

    public EntityCollection<Entity> getLoggers() {
        return new EntityCollection<Entity>(this, "server/logger");
    }

    public EntityCollection<Message> getMessages() {
        return new EntityCollection<Message>(this, "messages", Message.class);
    }

    public EntityCollection<Entity> getPasswords() {
        // Starting with 4.3 this is available at "storage/passwords"
        return new EntityCollection(this, "admin/passwords");
    }

    public EntityCollection<Entity> getRoles() {
        return new EntityCollection<Entity>(this, "authentication/roles");
    }

    public EntityCollection<Entity> getSearches() {
        return new EntityCollection<Entity>(this, "saved/searches");
    }

    public Settings getSettings() {
        return new Settings(this);
    }

    public UserCollection getUsers() {
        return new UserCollection(this, "authentication/users");
    }

    public Service login(String username, String password) {
        Args args = new Args();
        args.put("username", username);
        args.put("password", password);
        ResponseMessage response = post("/services/auth/login", args);
        String sessionKey = Xml.parse(response.getContent())
            .getElementsByTagName("sessionKey")
            .item(0)
            .getTextContent();
        this.token = "Splunk " + sessionKey;
        return this;
    }

    // Forget the session token
    public Service logout() {
        this.token = null;
        return this;
    }

    public ResponseMessage restart() {
        return this.get("server/control/restart");
    }

    public ResponseMessage send(String path, RequestMessage request) {
        request.getHeader().put("Authorization", token);
        return super.send(fullpath(path), request);
    }
}

