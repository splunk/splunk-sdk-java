/*
 * Copyright 2012 Splunk, Inc.
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

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

/**
 * The {@code Service} class represents a Splunk service instance at a given
 * address (host:port), accessed using the {@code http} or {@code https}
 * protocol scheme.
 * <p>
 * A {@code Service} instance also captures an optional namespace context
 * consisting of an optional owner name (or "-" wildcard) and optional app name
 * (or "-" wildcard).
 * <p>
 * To access {@code Service} members, the {@code Service} instance must be
 * authenticated by presenting credentials using the {@code login} method, or
 * by constructing the {@code Service} instance using the {@code connect}
 * method, which both creates and authenticates the instance.
 */
public class Service extends HttpService {
    /** The current app context. */
    protected String app = null;

    /** The current session token. */
    protected String token = null;

    /** The current owner context. A value of "nobody" means that all users
     * have access to the resource.
     */
    protected String owner = null;

    /** The Splunk account username, which is used to authenticate the Splunk
     * instance. */
    protected String username = null;

    /** The password, which is used to authenticate the Splunk instance. */
    protected String password = null;

    /** The default simple receiver endpoint. */
    protected String simpleReceiverEndPoint = "receivers/simple";

    /** The default password endpoint, can change over Splunk versions. */
    protected String passwordEndPoint = "admin/passwords";

    /** The version of this Splunk instance, once logged in. */
    public String version = null;

    /** The default host name, which is used when a host name is not provided.*/
    public static String DEFAULT_HOST = "localhost";

    /** The default port number, which is used when a port number is not
     * provided. */
    public static int DEFAULT_PORT = 8089;

    /** The default scheme, which is used when a scheme is not provided. */
    public static String DEFAULT_SCHEME = "https";

    /**
     * Creates a new {@code Service} instance using a host.
     *
     * @param host The host name.
     */
    public Service(String host) {
        super(host);
    }

    /**
     * Creates a new {@code Service} instance using a host and port.
     *
     * @param host The host name.
     * @param port The port number.
     */
    public Service(String host, int port) {
        super(host, port);
    }

    /**
     * Creates a new {@code Service} instance using a host, port, and
     * scheme for accessing the service ({@code http} or {@code https}).
     *
     * @param host The host name.
     * @param port The port number.
     * @param scheme The scheme ({@code http} or {@code https}).
     */
    public Service(String host, int port, String scheme) {
        super(host, port, scheme);
    }

    /**
     * Creates a new {@code Service} instance using a collection of arguments.
     *
     * @param args The {@code ServiceArgs} to initialize the service.
     */
    public Service(ServiceArgs args) {
        super();
        this.app = args.app;
        this.host = args.host == null ? DEFAULT_HOST : args.host;
        this.owner = args.owner;
        this.port = args.port == null ? DEFAULT_PORT : args.port;
        this.scheme = args.scheme == null ? DEFAULT_SCHEME : args.scheme;
        this.token = args.token;
    }

    /**
     * Creates a new {@code Service} instance using a map of arguments.
     *
     * @param args A {@code Map} of arguments to initialize the service.
     */
    public Service(Map<String, Object> args) {
        super();
        this.app = Args.<String>get(args, "app", null);
        this.host = Args.<String>get(args, "host", DEFAULT_HOST);
        this.owner = Args.<String>get(args, "owner", null);
        this.port = Args.<Integer>get(args, "port", DEFAULT_PORT);
        this.scheme = Args.<String>get(args, "scheme", DEFAULT_SCHEME);
        this.token = Args.<String>get(args, "token", null);
    }

    /**
     * Establishes a connection to a Splunk service using a map of arguments. 
     * This member creates a new {@code Service} instance and authenticates 
     * the session using credentials passed in from the {@code args} map.
     *
     * @param args The {@code args} map.
     * @return A new {@code Service} instance.
     */
    public static Service connect(Map<String, Object> args) {
        Service service = new Service(args);
        if (args.containsKey("username")) {
            String username = Args.get(args, "username", null);
            String password = Args.get(args, "password", null);
            service.login(username, password);
        }
        return service;
    }

    /**
     * Runs a search using the {@code search/jobs/export} endpoint, which
     * streams results back in an input stream.
     *
     * @param search The search query to run.
     * @return The {@code InputStream} object that contains the search results.
     */
    public InputStream export(String search) {
        return export(search, null);
    }

    /**
     * Runs a search with arguments using the {@code search/jobs/export}
     * endpoint, which streams results back in an input stream.
     *
     * @param search The search query to run.
     * @param args Additional search arguments.
     * @return The {@code InputStream} object that contains the search results.
     */
    public InputStream export(String search, Map args) {
        args = Args.create(args).add("search", search);
        ResponseMessage response = get("search/jobs/export", args);
        return response.getContent();
    }

    /**
     * Ensures that the given path is fully qualified, prepending a path
     * prefix if necessary. The path prefix is constructed using the current 
     * owner and app context when available.
     *
     * @param path The path to verify.
     * @return A fully-qualified resource path.
     */
    String fullpath(String path) {
        return fullpath(path, null);
    }

    /**
     * Ensures that the given path is fully qualified, prepending a path
     * prefix if necessarry. The path prefix is constructed using the
     * current owner and app context when available.
     *
     * @param path The path to verify.
     * @param namespace The name space dictionary (<i>app, owner, sharing</i>).
     * @return A fully-qualified resource path.
     */
    public String fullpath(String path, Args namespace) {

        // if already fully qualified (i.e. root begins with /) then return
        // the already qualified path.
        if (path.startsWith("/"))
            return path;

        // if no namespace at all, and no service instance of app, and no
        // sharing, return base service endpoint + path.
        if (namespace == null && app == null) {
            return "/services/" + path;
        }

        // base namespace values
        String localApp = app;
        String localOwner = owner;
        String localSharing = "";

        // override with invocation namespace if set.
        if (namespace != null) {
            if (namespace.containsKey("app"))
                localApp = (String)namespace.get("app");
            if (namespace.containsKey("owner"))
                localOwner = (String)namespace.get("owner");
            if (namespace.containsKey("sharing"))
                localSharing = (String)namespace.get("sharing");
        }

        // sharing, if set calls for special mapping, override here.
        // "user"    --> {user}/{app}
        // "app"     --> nobody/{app}
        // "global"  --> nobody/{app}
        // "system"  --> nobody/system
        if (localSharing.equals("app") || localSharing.equals("global"))
            localOwner = "nobody";
        else if (localSharing.equals("system")) {
            localApp = "system";
            localOwner = "nobody";
        }

        return String.format("/servicesNS/%s/%s/%s",
                localOwner == null ? "-" : localOwner,
                localApp   == null ? "-" : localApp,
                path);
    }

    /**
     * Returns the app context for this {@code Service} instance. 
     * A {@code null} value indicates no app context, and a value of 
     * {@code "-"} indicates an app wildcard.
     *
     * @return The app context.
     */
    public String getApp() {
        return this.app;
    }

    /**
     * Returns the collection of applications.
     *
     * @return The application collection.
     */
    public EntityCollection<Application> getApplications() {
        return new EntityCollection<Application>(
            this, "/services/apps/local", Application.class);
    }

    /**
     * Returns the collection of configurations.
     *
     * @return The configurations collection.
     */
    public ConfCollection getConfs() {
        return new ConfCollection(this);
    }

    /**
     * Returns the collection of configurations.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return The configurations collection.
     */
    public ConfCollection getConfs(Args args) {
        return new ConfCollection(this, args);
    }

    /**
     * Returns an array of system capabilities.
     *
     * @return An array of capabilities.
     */
    public String[] getCapabilities() {
        Entity caps = new Entity(this, "authorization/capabilities");
        return caps.getStringArray("capabilities");
    }

    /**
     * Returns the configuration and status of a deployment client.
     *
     * @return The configuration and status.
     */
    public DeploymentClient getDeploymentClient() {
        return new DeploymentClient(this);
    }

    /**
     * Returns the configuration of all deployment servers.
     *
     * @return The configuration of deployment servers.
     */
    public EntityCollection<DeploymentServer> getDeploymentServers() {
        return new EntityCollection<DeploymentServer>(
            this, "deployment/server", DeploymentServer.class);
    }

    /**
     * Returns the configuration of all deployment servers.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return The configuration of deployment servers.
     */
    public EntityCollection<DeploymentServer> getDeploymentServers(Args args) {
        return new EntityCollection<DeploymentServer>(
            this, "deployment/server", DeploymentServer.class, args);
    }

    /**
     * Returns a collection of class configurations for a deployment server.
     *
     * @return A collection of class configurations.
     */
    public EntityCollection<DeploymentServerClass> getDeploymentServerClasses(){
        return new EntityCollection<DeploymentServerClass>(
            this, "deployment/serverclass", DeploymentServerClass.class);
    }

    /**
     * Returns a collection of class configurations for a deployment server.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of server class configurations.
     */
    public EntityCollection<DeploymentServerClass> getDeploymentServerClasses(
            Args args){
        return new EntityCollection<DeploymentServerClass>(
            this, "deployment/serverclass", DeploymentServerClass.class, args);
    }

    /**
     * Returns a collection of multi-tenant configurations.
     *
     * @return A collection of multi-tenant configurations.
     */
    public EntityCollection<DeploymentTenant> getDeploymentTenants() {
        return new EntityCollection<DeploymentTenant>(
            this, "deployment/tenants", DeploymentTenant.class);
    }

    /**
     * Returns a collection of multi-tenant configurations.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of multi-tenant configurations.
     */
    public EntityCollection<DeploymentTenant> getDeploymentTenants(Args args) {
        return new EntityCollection<DeploymentTenant>(
            this, "deployment/tenants", DeploymentTenant.class, args);
    }

    /**
     * Returns information about distributed search options.
     *
     * @return Distributed search information.
     */
    public DistributedConfiguration getDistributedConfiguration() {
        return new DistributedConfiguration(this);
    }

    /**
     * Returns a collection of distributed search peers. A <i>search peer</i>
     * is a Splunk server to which another Splunk server distributes searches.
     * The Splunk server where the search originates is referred to as the
     * <i>search head</i>.
     *
     * @return A collection of search peers.
     */
    public EntityCollection<DistributedPeer> getDistributedPeers() {
        return new EntityCollection<DistributedPeer>(
            this, "search/distributed/peers", DistributedPeer.class);
    }

    /**
     * Returns a collection of distributed search peers. A <i>search peer</i>
     * is a Splunk server to which another Splunk server distributes searches.
     * The Splunk server where the search originates is referred to as the
     * <i>search head</i>.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of search peers.
     */
    public EntityCollection<DistributedPeer> getDistributedPeers(Args args) {
        return new EntityCollection<DistributedPeer>(
            this, "search/distributed/peers", DistributedPeer.class, args);
    }


    /**
     * Returns a collection of saved event types.
     *
     * @return A collection of saved event types.
     */
    public EventTypeCollection getEventTypes() {
        return new EventTypeCollection(this);
    }

    /**
     * Returns a collection of saved event types.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of saved event types.
     */
    public EventTypeCollection getEventTypes(Args args) {
        return new EventTypeCollection(this, args);
    }

    /**
     * Returns a collection of alerts that have been fired by the service.
     *
     * @return A collection of fired alerts.
     */
    public FiredAlertGroupCollection getFiredAlertGroups() {
        return new FiredAlertGroupCollection(this);
    }

    /**
     * Returns a collection of alerts that have been fired by the service.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of fired alerts.
     */
    public FiredAlertGroupCollection getFiredAlerts(Args args) {
        return new FiredAlertGroupCollection(this, args);
    }

    /**
     * Returns a collection of Splunk indexes.
     *
     * @return A collection of indexes.
     */
    public IndexCollection getIndexes() {
        return new IndexCollection(this);
    }

    /**
     * Returns a collection of Splunk indexes.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of indexes.
     */
    public EntityCollection<Index> getIndexes(Args args) {
        return new EntityCollection<Index>(
            this, "data/indexes", Index.class, args);
    }

    /**
     * Returns information about the Splunk service.
     *
     * @return Splunk service information.
     */
    public ServiceInfo getInfo() {
        return new ServiceInfo(this);
    }

    /**
     * Returns a collection of configured inputs.
     *
     * @return A collection of inputs.
     */
    public InputCollection getInputs() {
        return new InputCollection(this);
    }

    /**
     * Returns a collection of configured inputs.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of inputs.
     */
    public InputCollection getInputs(Args args) {
        return new InputCollection(this, args);
    }

    /**
     * Returns a collection of current search jobs.
     *
     * @return A collection of search jobs.
     */
    public JobCollection getJobs() {
        return new JobCollection(this);
    }

    /**
     * Returns a collection of current search jobs.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of search jobs.
     */
    public JobCollection getJobs(Args args) {
        return new JobCollection(this, args);
    }

    /**
     * Returns a collection of license group configurations.
     *
     * @return A collection of license group configurations.
     */
    public EntityCollection<LicenseGroup> getLicenseGroups() {
        return new EntityCollection<LicenseGroup>(
            this, "licenser/groups", LicenseGroup.class);
    }

    /**
     * Returns a collection of license group configurations.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of license group configurations.
     */
    public EntityCollection<LicenseGroup> getLicenseGroups(Args args) {
        return new EntityCollection<LicenseGroup>(
            this, "licenser/groups", LicenseGroup.class, args);
    }

    /**
     * Returns a collection of messages from the licenser.
     *
     * @return A collection of licenser messages.
     */
    public EntityCollection<LicenseMessage> getLicenseMessages() {
        return new EntityCollection<LicenseMessage>(
            this, "licenser/messages", LicenseMessage.class);
    }

    /**
     * Returns a collection of messages from the licenser.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of licenser messages.
     */
    public EntityCollection<LicenseMessage> getLicenseMessages(Args args) {
        return new EntityCollection<LicenseMessage>(
            this, "licenser/messages", LicenseMessage.class, args);
    }

    /**
     * Returns the current owner context for this {@code Service} instance. 
     * A value of {@code "-"} indicates a wildcard, and a {@code null} value 
     * indicates no owner context.
     *
     * @return The current owner context.
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * Returns a collection of licenser pool configurations.
     *
     * @return A collection of licenser pool configurations.
     */
    public LicensePoolCollection getLicensePools() {
        return new LicensePoolCollection(this);
    }

    /**
     * Returns a collection of licenser pool configurations.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of licenser pool configurations.
     */
    public LicensePoolCollection getLicensePools(Args args) {
        return new LicensePoolCollection(this, args);
    }

    /**
     * Returns a collection of slaves reporting to this license master.
     *
     * @return A collection of licenser slaves.
     */
    public EntityCollection<LicenseSlave> getLicenseSlaves() {
        return new EntityCollection<LicenseSlave>(
            this, "licenser/slaves", LicenseSlave.class);
    }

    /**
     * Returns a collection of slaves reporting to this license master.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of licenser slaves.
     */
    public EntityCollection<LicenseSlave> getLicenseSlaves(Args args) {
        return new EntityCollection<LicenseSlave>(
            this, "licenser/slaves", LicenseSlave.class, args);
    }

    /**
     * Returns a collection of license stack configurations.
     *
     * @return A collection of license stack configurations.
     */
    public EntityCollection<LicenseStack> getLicenseStacks() {
        return new EntityCollection<LicenseStack>(
            this, "licenser/stacks", LicenseStack.class);
    }

    /**
     * Returns a collection of license stack configurations.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of license stack configurations.
     */
    public EntityCollection<LicenseStack> getLicenseStacks(Args args) {
        return new EntityCollection<LicenseStack>(
            this, "licenser/stacks", LicenseStack.class, args);
    }

    /**
     * Returns a collection of licenses for this service.
     *
     * @return A collection of licenses.
     */
    public EntityCollection<License> getLicenses() {
        return new EntityCollection<License>(
            this, "licenser/licenses", License.class);
    }

    /**
     * Returns a collection of licenses for this service.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of licenses.
     */
    public EntityCollection<License> getLicenses(Args args) {
        return new EntityCollection<License>(
            this, "licenser/licenses", License.class, args);
    }

    /**
     * Returns a collection of service logging categories and their status.
     *
     * @return A collection of logging categories.
     */
    public EntityCollection<Logger> getLoggers() {
        return new EntityCollection<Logger>(
            this, "server/logger", Logger.class);
    }

    /**
     * Returns a collection of service logging categories and their status.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of logging categories.
     */
    public EntityCollection<Logger> getLoggers(Args args) {
        return new EntityCollection<Logger>(
            this, "server/logger", Logger.class, args);
    }

    /**
     * Returns a collection of system messages.
     *
     * @return A collection of system messages.
     */
    public MessageCollection getMessages() {
        return new MessageCollection(this);
    }

    /**
     * Returns a collection of system messages.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of system messages.
     */
    public MessageCollection getMessages(Args args) {
        return new MessageCollection(this, args);
    }

    /**
     * Returns a collection of modular inputs.
     *
     * @param args Optional arguments, such as "count" and "offset" for pagination.
     * @return A collection of modular inputs.
     */
    public ResourceCollection<ModularInputKind> getModularInputKinds(Args args) {
        return new ResourceCollection<ModularInputKind>(this, "data/modular-inputs", ModularInputKind.class);
    }

    /**
     * Returns global TCP output properties.
     *
     * @return Global TCP output properties.
     */
    public OutputDefault getOutputDefault() {
        return new OutputDefault(this);
    }

    /**
     * Returns a collection of output group configurations.
     *
     * @return A collection of output group configurations.
     */
    public EntityCollection<OutputGroup> getOutputGroups() {
        return new EntityCollection<OutputGroup>(
            this, "data/outputs/tcp/group", OutputGroup.class);
    }

    /**
     * Returns a collection of output group configurations.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of output group configurations.
     */
    public EntityCollection<OutputGroup> getOutputGroups(Args args) {
        return new EntityCollection<OutputGroup>(
            this, "data/outputs/tcp/group", OutputGroup.class, args);
    }

    /**
     * Returns a collection of data-forwarding configurations.
     *
     * @return A collection of data-forwarding configurations.
     */
    public EntityCollection<OutputServer> getOutputServers() {
        return new EntityCollection<OutputServer>(
            this, "data/outputs/tcp/server", OutputServer.class);
    }

    /**
     * Returns a collection of data-forwarding configurations.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of data-forwarding configurations.
     */
    public EntityCollection<OutputServer> getOutputServers(Args args) {
        return new EntityCollection<OutputServer>(
            this, "data/outputs/tcp/server", OutputServer.class, args);
    }

    /**
     * Returns a collection of configurations for forwarding data in standard
     * syslog format.
     *
     * @return A collection of syslog forwarders.
     */
    public EntityCollection<OutputSyslog> getOutputSyslogs() {
        return new EntityCollection<OutputSyslog>(
            this, "data/outputs/tcp/syslog", OutputSyslog.class);
    }

    /**
     * Returns a collection of configurations for forwarding data in standard
     * syslog format.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of syslog forwarders.
     */
    public EntityCollection<OutputSyslog> getOutputSyslogs(Args args) {
        return new EntityCollection<OutputSyslog>(
            this, "data/outputs/tcp/syslog", OutputSyslog.class, args);
    }

    /**
     * Returns the current password that was used to authenticate the session.
     *
     * @return The current password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns a collection of passwords. This collection is used for managing
     * secure credentials.
     *
     * @return A collection of passwords.
     */
    public PasswordCollection getPasswords() {
        return new PasswordCollection(this);
    }

    /**
     * Returns a collection of passwords. This collection is used for managing
     * secure credentials.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of passwords.
     */
    public PasswordCollection getPasswords(Args args) {
        return new PasswordCollection(this, args);
    }

    /**
     * Returns the receiver object for the Splunk service.
     *
     * @return A Splunk receiver object.
     */
    public Receiver getReceiver() {
        return new Receiver(this);
    }

    /**
     * Returns a collection of Splunk user roles.
     *
     * @return A collection of user roles.
     */
    public EntityCollection<Role> getRoles() {
        return new EntityCollection<Role>(
            this, "authentication/roles", Role.class);
    }

    /**
     * Returns a collection of Splunk user roles.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of user roles.
     */
    public EntityCollection<Role> getRoles(Args args) {
        return new EntityCollection<Role>(
            this, "authentication/roles", Role.class, args);
    }

    /**
     * Returns a collection of saved searches.
     *
     * @return A collection of saved searches.
     */
    public SavedSearchCollection getSavedSearches() {
        return new SavedSearchCollection(this);
    }

    /**
     * Returns a collection of saved searches.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of saved searches.
     */
    public SavedSearchCollection getSavedSearches(Args args) {
        return new SavedSearchCollection(this, args);
    }

    /**
     * Returns a collection of saved searches.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of saved searches.
     */
    public SavedSearchCollection getSavedSearches(SavedSearchCollectionArgs args) {
        return new SavedSearchCollection(this, args);
    }

    /**
     * Returns service configuration information for an instance of Splunk.
     *
     * @return Service configuration information.
     */
    public Settings getSettings() {
        return new Settings(this);
    }

    /**
     * Returns the current session token. Session tokens can be shared across
     * multiple {@code Service} instances.
     *
     * @return The session token.
     */
    public String getToken() {
        return this.token;
    }

    /**
     * Returns a collection of in-progress oneshot uploads.
     *
     * @return A collection of in-progress oneshot uploads
     */
    public EntityCollection<Upload> getUploads() {
        return new EntityCollection<Upload>(
            this, "data/inputs/oneshot", Upload.class);
    }

    /**
     * Returns a collection of in-progress oneshot uploads.
     *
     * @param namespace This collection's namespace; there are no other
     * optional arguments for this endpoint.
     * @return A collection of in-progress oneshot uploads
     */
    public EntityCollection<Upload>
    getUploads(Args namespace) {
        return new EntityCollection<Upload>(
            this, "data/inputs/oneshot", Upload.class, namespace);
    }

    /**
     * Returns the Splunk account username that was used to authenticate the
     * current session.
     *
     * @return The current username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns a collection of Splunk users.
     *
     * @return A collection of users.
     */
    public UserCollection getUsers() {
        return new UserCollection(this);
    }

    /**
     * Returns a collection of Splunk users.
     *
     * @param args Optional arguments, such as "count" and "offset" for 
     * pagination.
     * @return A collection of users.
     */
    public UserCollection getUsers(Args args) {
        return new UserCollection(this, args);
    }

    /**
     * Authenticates the {@code Service} instance with a username and password.
     *
     * @param username The Splunk account username.
     * @param password The password for the username.
     * @return The current {@code Service} instance.
     */
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
        this.version = this.getInfo().getVersion();
        if (versionCompare("4.3") >= 0)
            this.passwordEndPoint = "storage/passwords";

        return this;
    }

    /**
     * Forgets the current session token.
     *
     * @return The current {@code Service} instance.
     */
    public Service logout() {
        this.token = null;
        return this;
    }

    /**
     * Creates a oneshot synchronous search.
     *
     * @param query The search query.
     * @return The search results.
     */
    public InputStream oneshot(String query) {
       return oneshot(query, null, null);
    }

    /**
     * Creates a oneshot synchronous search using search arguments.
     *
     * @param query The search query.
     * @param inputArgs The search arguments.
     * @return The search results.
     */
    public InputStream oneshot(String query, Map inputArgs) {
        return oneshot(query, inputArgs, null);
    }

    /**
     * Creates a oneshot synchronous search using search arguments.
     *
     * @param query The search query.
     * @param inputArgs The search arguments.
     * @param outputArgs The output qualifier arguments.
     * @return The search results.
     */
    public InputStream oneshot(String query, Map inputArgs, Map outputArgs) {
        inputArgs = Args.create(inputArgs);
        inputArgs.put("search", query);
        inputArgs.put("exec_mode", "oneshot");
        ResponseMessage response = post("search/jobs", inputArgs);
        return response.getContent();
    }

    /**
     * Opens a raw socket to this service.
     *
     * @param port The port to open. This port must already have been
     * created as an allowable TCP input to the service.
     * @return The socket.
     * @throws java.io.IOException
     */
    public Socket open(int port) throws IOException {
        return new Socket(this.host, port);
    }

    /**
     * Parses a search query and returns a semantic map for the search in JSON 
     * format.
     *
     * @param query The search query.
     * @return The parse response message.
     */
    public ResponseMessage parse(String query) {
        return parse(query, null);
    }

    /**
     * Parses a search query with additional arguments and returns a semantic
     * map for the search in JSON format.
     *
     * @param query The search query.
     * @param args Additional parse arguments.
     * @return The parse response message.
     */
    public ResponseMessage parse(String query, Map args) {
        args = Args.create(args).add("q", query);
        return get("search/parser", args);
    }

    /**
     * Restarts the service. The service will be unavailable until it has
     * sucessfully restarted.
     *
     * @return The restart response message.
     */
    public ResponseMessage restart() {
        return get("server/control/restart");
    }

    /**
     * Creates a simplified synchronous search using search arguments. Use this
     * method for simple searches. For output control arguments, use jobs.
     *
     * @param query The search query.
     * @return The search results.
     */
    public InputStream search(String query) {
       return search(query, null, null);
    }

    /**
     * Creates a simplified synchronous search using search arguments. Use this
     * method for simple searches. For output control arguments, use jobs.
     *
     * @param query The search query.
     * @param inputArgs The search arguments.
     * @return The search results.
     */
    public InputStream search(String query, Map inputArgs) {
        return search(query, inputArgs, null);
    }

    /**
     * Creates a simplified synchronous search using search arguments. Use this
     * method for simple searches. For output control arguments, use jobs.
     *
     * @param query The search query.
     * @param inputArgs The search arguments.
     * @param outputArgs The output qualifier arguments.
     * @return The search results.
     */
    public InputStream search(String query, Map inputArgs, Map outputArgs) {
        inputArgs = Args.create(inputArgs);
        inputArgs.put("search", query);
        // always block until results are ready.
        inputArgs.put("exec_mode", "blocking");
        Job job = this.getJobs().create(query, inputArgs);
        return job.getResults(outputArgs);
    }

    /**
     * Issues an HTTP request against the service using a request path and 
     * message. 
     * This method overrides the base {@code HttpService.send} method
     * and applies the Splunk authorization header, which is required for 
     * authenticated interactions with the Splunk service.
     *
     * @param path The request path.
     * @param request The request message.
     * @return The HTTP response.
     */
    @Override public ResponseMessage send(String path, RequestMessage request) {
        request.getHeader().put("Authorization", token);
        return super.send(fullpath(path), request);
    }

    /**
     * Provides a session token for use by this {@code Service} instance. 
     * Session tokens can be shared across multiple {@code Service} instances.
     *
     * @param value The session token.
     */
    public void setToken(String value) {
        this.token = value;
    }

    // returns -1, 0, 1 comparing current Splunk version string to right version
    // string for less than, equal to or greater than
    public int versionCompare(String right) {

        // short cut for equality.
        if (this.version.equals(right)) return 0;

        // if not the same, break down into individual digits for comparison.
        String[] leftDigits = this.version.split(".");
        String[] rightDigits = right.split(".");
        int i=0;

        for (; i<leftDigits.length; i++) {
            // No more right side, left side is bigger
            if (i == rightDigits.length) return 1;
            // left side smaller>?
            if (Integer.parseInt(leftDigits[i]) <
                Integer.parseInt(leftDigits[1])) {
                return -1;
            }
            // left side bigger?
            if (Integer.parseInt(leftDigits[i]) >
                    Integer.parseInt(leftDigits[1])) {
                return 1;
            }
        }
        // we got to the end of the left side, and not equal, right side
        // most be larger by having more digits.
        return -1;
    }
}
