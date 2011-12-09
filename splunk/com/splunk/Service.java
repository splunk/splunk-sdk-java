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

import java.io.InputStream;
import java.util.Map;

public class Service extends HttpService {
    protected String app = null;
    protected String token = null;
    protected String owner = null;
    protected String username = null;
    protected String password = null;

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
        this.app = args.app;
        this.host = args.host == null ? DEFAULT_HOST : args.host;
        this.owner = args.owner;
        this.port = args.port == null ? DEFAULT_PORT : args.port;
        this.scheme = args.scheme == null ? DEFAULT_SCHEME : args.scheme;
        this.token = args.token;
    }

    public Service(Map<String, Object> args) {
        super();
        this.app = Args.<String>get(args, "app", null);
        this.host = Args.<String>get(args, "host", DEFAULT_HOST);
        this.owner = Args.<String>get(args, "owner", null);
        this.port = Args.<Integer>get(args, "port", DEFAULT_PORT);
        this.scheme = Args.<String>get(args, "scheme", DEFAULT_SCHEME);
        this.token = Args.<String>get(args, "token", null);
    }

    public static Service connect(Map<String, Object> args) {
        Service service = new Service(args);
        if (args.containsKey("username")) {
            String username = Args.get(args, "username", null);
            String password = Args.get(args, "password", null);
            service.login(username, password);
        }
        return service;
    }

    // Execute a search using the export endpoint, streaming results back
    // in the response of the search request.
    public InputStream export(String search) {
        return export(search, null);
    }

    public InputStream export(String search, Map args) {
        args = Args.create(args).add("search", search);
        ResponseMessage response = get("search/jobs/export", args);
        return response.getContent();
    }

    // Ensures that the given path is fully qualified, prepending a
    // path prefix as necessarry.
    String fullpath(String path) {
        if (path.startsWith("/"))
            return path;
        if (owner == null && app == null)
            return "/services/" + path;
        return String.format("/servicesNS/%s/%s/%s", 
            owner == null ? "-" : owner, app == null ? "-" : app, path);
    }
    
    // Return the app name used to scope this service instance.
    public String getApp() {
        return this.app;
    }

    public EntityCollection<Application> getApplications() {
        return new EntityCollection<Application>(
            this, "apps/local", Application.class);
    }

    public ConfCollection getConfs() {
        return new ConfCollection(this);
    }

    public String[] getCapabilities() {
        Entity caps = new Entity(this, "authorization/capabilities");
        return caps.getStringArray("capabilities");
    }

    public DeploymentClient getDeploymentClient() {
        return new DeploymentClient(this);
    }

    public EntityCollection<DeploymentServer> getDeploymentServers() {
        return new EntityCollection<DeploymentServer>(
            this, "deployment/server", DeploymentServer.class);
    }

    public EntityCollection<DeploymentServerClass> getDeploymentServerClasses(){
        return new EntityCollection<DeploymentServerClass>(
            this, "deployment/serverclass", DeploymentServerClass.class);
    }

    public EntityCollection<DeploymentTenant> getDeploymentTenants() {
        return new EntityCollection<DeploymentTenant>(
            this, "deployment/tenants", DeploymentTenant.class);
    }

    public DistributedConfiguration getDistributedConfiguration() {
        return new DistributedConfiguration(this);
    }

    public EntityCollection<DistributedPeer> getDistributedPeers() {
        return new EntityCollection<DistributedPeer>(
            this, "search/distributed/peers", DistributedPeer.class);
    }

    public EventTypeCollection getEventTypes() {
        return new EventTypeCollection(this);
    }

    public EntityCollection<FiredAlert> getFiredAlerts() {
        return new EntityCollection<FiredAlert>(
            this, "alerts/fired_alerts", FiredAlert.class);
    }

    public EntityCollection<Index> getIndexes() {
        return new EntityCollection<Index>(this, "data/indexes", Index.class);
    }

    public ServiceInfo getInfo() {
        return new ServiceInfo(this);
    }

    public InputCollection getInputs() {
        return new InputCollection(this);
    }

    public JobCollection getJobs() {
        return new JobCollection(this);
    }

    public EntityCollection<LicenseGroup> getLicenseGroups() {
        return new EntityCollection<LicenseGroup>(
            this, "licenser/groups", LicenseGroup.class);
    }

    public EntityCollection<LicenseMessage> getLicenseMessages() {
        return new EntityCollection<LicenseMessage>(
            this, "licenser/messages", LicenseMessage.class);
    }

    // Return the user name used to scope this service instance.
    public String getOwner() {
        return this.owner;
    }

    public LicensePoolCollection getLicensePools() {
        return new LicensePoolCollection(this);
    }

    public EntityCollection<LicenseSlave> getLicenseSlaves() {
        return new EntityCollection<LicenseSlave>(
            this, "licenser/slaves", LicenseSlave.class);
    }

    public EntityCollection<LicenseStack> getLicenseStacks() {
        return new EntityCollection<LicenseStack>(
            this, "licenser/stacks", LicenseStack.class);
    }

    public EntityCollection<License> getLicenses() {
        return new EntityCollection<License>(
            this, "licenser/licenses", License.class);
    }

    public EntityCollection<Logger> getLoggers() {
        return new EntityCollection<Logger>(
            this, "server/logger", Logger.class);
    }

    public MessageCollection getMessages() {
        return new MessageCollection(this);
    }

    public OutputDefault getOutputDefault() {
        return new OutputDefault(this);
    }

    public EntityCollection<OutputGroup> getOutputGroups() {
        return new EntityCollection<OutputGroup>(
            this, "data/outputs/tcp/group", OutputGroup.class);
    }

    public EntityCollection<OutputServer> getOutputServers() {
        return new EntityCollection<OutputServer>(
            this, "data/outputs/tcp/server", OutputServer.class);
    }

    public EntityCollection<OutputSyslog> getOutputSyslogs() {
        return new EntityCollection<OutputSyslog>(
            this, "data/outputs/tcp/syslog", OutputSyslog.class);
    }

    public String getPassword() {
        return this.password;
    }

    public PasswordCollection getPasswords() {
        return new PasswordCollection(this);
    }

    public EntityCollection<Role> getRoles() {
        return new EntityCollection<Role>(
            this, "authentication/roles", Role.class);
    }

    public SavedSearchCollection getSavedSearches() {
        return new SavedSearchCollection(this);
    }

    public Settings getSettings() {
        return new Settings(this);
    }

    // Returns the current session's auth token.
    public String getToken() {
        return this.token;
    }

    public String getUsername() {
        return this.username;
    }

    public UserCollection getUsers() {
        return new UserCollection(this);
    }

    public Service login(String username, String password) {
        this.username = username;
        this.password = password;

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

    public ResponseMessage parse(String query) {
        return parse(query, null);
    }

    public ResponseMessage parse(String query, Map args) {
        args = Args.create(args).add("q", query);
        return get("search/parser", args);
    }

    public ResponseMessage restart() {
        return get("server/control/restart");
    }

    public ResponseMessage send(String path, RequestMessage request) {
        request.getHeader().put("Authorization", token);
        return super.send(fullpath(path), request);
    }

    // Provides a session token for use by this service instance.
    public void setToken(String value) {
        this.token = value;
    }
}
