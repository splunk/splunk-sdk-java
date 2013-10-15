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
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.URLStreamHandler;
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
public class Service extends BaseService {
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
     * Constructs a new {@code Service} instance using the given host,
     * port, and scheme, and instructing it to use the specified HTTPS handler.
     *
     * @param host The host name of the service.
     * @param port The port number of the service.
     * @param scheme Scheme for accessing the service ({@code http} or 
     * {@code https}).
     */
    public Service(String host, int port, String scheme, 
    		URLStreamHandler httpsHandler) {
        this.host = host;
        this.port = port;
        this.scheme = scheme;
        this.httpsHandler = httpsHandler;
    }

    /**
     * Creates a new {@code Service} instance using a collection of arguments.
     *
     * @param args The {@code ServiceArgs} to initialize the service.
     */
    // NOTE: This overload exists primarily to provide better documentation
    //       for the "args" parameter.
    @SuppressWarnings("deprecation")
    public Service(ServiceArgs args) {
        super();
        // NOTE: Must read the deprecated fields for backward compatibility.
        //       (Consider the case where the fields are initialized directly,
        //        rather than using the new setters.)
        // NOTE: Must also read the underlying dictionary for forward compatibility.
        //       (Consider the case where the user calls Map.put() directly,
        //        rather than using the new setters.)
        this.app = Args.<String>get(args,    "app",    args.app != null    ? args.app    : null);
        this.host = Args.<String>get(args,   "host",   args.host != null   ? args.host   : DEFAULT_HOST);
        this.owner = Args.<String>get(args,  "owner",  args.owner != null  ? args.owner  : null);
        this.port = Args.<Integer>get(args,  "port",   args.port != null   ? args.port   : DEFAULT_PORT);
        this.scheme = Args.<String>get(args, "scheme", args.scheme != null ? args.scheme : DEFAULT_SCHEME);
        this.token = Args.<String>get(args,  "token",  args.token != null  ? args.token  : null);
        this.username = (String)args.get("username");
        this.password = (String)args.get("password");
        this.httpsHandler = Args.<URLStreamHandler>get(args, "httpsHandler", null);
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
        this.username = (String)args.get("username");
        this.password = (String)args.get("password");
        this.httpsHandler = Args.<URLStreamHandler>get(args, "httpsHandler", null);
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
            service.login();
        }
        return service;
    }

    /**
     * Runs an export search (using the {@code search/jobs/export} endpoint), 
     * and streams results back in an input stream.
     *
     * @param search The search query to run.
     * @return The {@code InputStream} object that contains the search results.
     */
    public InputStream export(String search) {
        return export(search, null);
    }

    /**
     * Runs an export search with arguments (using the {@code search/jobs/export}
     * endpoint), and streams results back in an input stream.
     *
     * @param search The search query to run.
     * @param args Additional search arguments. 
     * For a list of possible parameters, see
     * <a href="http://dev.splunk.com/view/SP-CAAAEHQ#savedsearchparams" 
     * target="_blank">Saved search parameters</a> on 
     * <a href="http://dev.splunk.com/view/SP-CAAAEHQ" 
     * target="_blank">dev.splunk.com</a>. 
     * @return The {@code InputStream} object that contains the search results.
     */
    public InputStream export(String search, Map args) {
        args = Args.create(args).add("search", search);
        // By default don't highlight search terms in the output.
        if (!args.containsKey("segmentation")) {
            args.put("segmentation", "none");
        }
        ResponseMessage response = get("search/jobs/export", args);
        return new ExportResultsStream(response.getContent());
    }
    
    /**
     * Runs an export search with arguments (using the {@code search/jobs/export}
     * endpoint), and streams results back in an input stream.
     *
     * @param search The search query to run.
     * @param args Additional search arguments (see {@code JobExportArgs}).
     * @return The {@code InputStream} object that contains the search results.
     */
    // NOTE: This overload exists primarily to provide better documentation
    //       for the "args" parameter.
    public InputStream export(String search, JobExportArgs args) {
        return export(search, (Map<String, Object>) args);
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
     * Ensures that a given path is fully qualified, prepending a path
     * prefix if necessary. The path prefix is constructed using the
     * current owner and app context when available.
     *
     * @param path The path to verify.
     * @param namespace The namespace dictionary (<i>app, owner, sharing</i>).
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
            // URL encode the owner and app.
            if (namespace.containsKey("app")) {
                try {
                    localApp = URLEncoder.encode((String)namespace.get("app"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // This is unreachable, since UTF-8 is always supported.
                    assert false;
                }
            }
            if (namespace.containsKey("owner")) {
                try {
                    localOwner = URLEncoder.encode((String)namespace.get("owner"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // This is unreachable, since UTF-8 is always supported.
                    assert false;
                }
            }
            if (namespace.containsKey("sharing")) {
                localSharing = (String)namespace.get("sharing");
            }
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
        return getConfs(null);
    }

    /**
     * Returns the collection of configurations.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getDeploymentServers(null);
    }

    /**
     * Returns the collection of deployment servers.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
     * @return The configuration of deployment servers.
     */
    public EntityCollection<DeploymentServer> getDeploymentServers(Args args) {
        String path;
        if (versionIsEarlierThan("6.0.0")) {
            path = "deployment/server";
        } else {
            path = ""; // TODO: Find out what this should be and fix it.
        }
        return new EntityCollection<DeploymentServer>(
            this, "deployment/server", DeploymentServer.class, args);
    }

    /**
     * Returns a collection of class configurations for a deployment server.
     *
     * @return A collection of class configurations.
     */
    public EntityCollection<DeploymentServerClass> getDeploymentServerClasses(){
        return getDeploymentServerClasses(null);
    }

    /**
     * Returns a collection of class configurations for a deployment server.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
     * @return A collection of server class configurations.
     */
    public EntityCollection<DeploymentServerClass> getDeploymentServerClasses(
            Args args) {
        String path;
        if (versionIsEarlierThan("6.0.0")) {
            path = "deployment/serverclass";
        } else {
            path = "deployment/server/serverclasses";
        }
        return new EntityCollection<DeploymentServerClass>(
            this, path, DeploymentServerClass.class, args);
    }

    /**
     * Returns a collection of multi-tenant configurations.
     *
     * @return A collection of multi-tenant configurations.
     */
    public EntityCollection<DeploymentTenant> getDeploymentTenants() {
        return getDeploymentTenants(null);
    }

    /**
     * Returns a collection of multi-tenant configurations.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getDistributedPeers(null);
    }

    /**
     * Returns a collection of distributed search peers. A <i>search peer</i>
     * is a Splunk server to which another Splunk server distributes searches.
     * The Splunk server where the search originates is referred to as the
     * <i>search head</i>.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getEventTypes(null);
    }

    /**
     * Returns a collection of saved event types.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getFiredAlertsGroups(null);
    }

    /**
     * Returns a collection of alerts that have been fired by the service.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
     * @return A collection of fired alerts.
     */
    public FiredAlertGroupCollection getFiredAlertsGroups(Args args) {
        return new FiredAlertGroupCollection(this, args);
    }

    /**
     * Returns a collection of Splunk indexes.
     *
     * @return A collection of indexes.
     */
    public IndexCollection getIndexes() {
        return getIndexes((IndexCollectionArgs)null);
    }
    
    /**
     * Returns a collection of Splunk indexes.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link IndexCollectionArgs}.
     * @return A collection of indexes.
     */
    // NOTE: This overload exists primarily to provide better documentation
    //       for the "args" parameter.
    public IndexCollection getIndexes(IndexCollectionArgs args) {
        return getIndexes((Args)args);
    }

    /**
     * Returns a collection of Splunk indexes.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link IndexCollectionArgs}.
     * @return A collection of indexes.
     */
    public IndexCollection getIndexes(Args args) {
        return new IndexCollection(this, args);
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
        return getInputs(null);
    }

    /**
     * Returns a collection of configured inputs.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getJobs((CollectionArgs)null);
    }
    
    /**
     * Returns a collection of current search jobs.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
     * @return A collection of search jobs.
     */
    // NOTE: This overload exists primarily to provide better documentation
    //       for the "args" parameter.
    public JobCollection getJobs(CollectionArgs args) {
        return getJobs((Args)args);
    }

    /**
     * Returns a collection of current search jobs.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getLicenseGroups(null);
    }

    /**
     * Returns a collection of license group configurations.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getLicenseMessages(null);
    }

    /**
     * Returns a collection of messages from the licenser.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getLicensePools(null);
    }

    /**
     * Returns a collection of licenser pool configurations.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getLicenseSlaves(null);
    }

    /**
     * Returns a collection of slaves reporting to this license master.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getLicenseStacks(null);
    }

    /**
     * Returns a collection of license stack configurations.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getLicenses(null);
    }

    /**
     * Returns a collection of licenses for this service.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getLoggers(null);
    }

    /**
     * Returns a collection of service logging categories and their status.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getMessages(null);
    }

    /**
     * Returns a collection of system messages.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
     * @return A collection of system messages.
     */
    public MessageCollection getMessages(Args args) {
        return new MessageCollection(this, args);
    }
    
    /**
     * Returns a collection of modular inputs.
     *
     * @return A collection of modular inputs.
     */
    public ResourceCollection<ModularInputKind> getModularInputKinds() {
        return getModularInputKinds(null);
    }

    /**
     * Returns a collection of modular inputs.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
     * @return A collection of modular inputs.
     */
    public ResourceCollection<ModularInputKind> getModularInputKinds(Args args) {
        return new ResourceCollection<ModularInputKind>(
                this, "data/modular-inputs", ModularInputKind.class, args);
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
        return getOutputGroups(null);
    }

    /**
     * Returns a collection of output group configurations.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getOutputServers(null);
    }

    /**
     * Returns a collection of data-forwarding configurations.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getOutputSyslogs(null);
    }

    /**
     * Returns a collection of configurations for forwarding data in standard
     * syslog format.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getPasswords(null);
    }

    /**
     * Returns a collection of passwords. This collection is used for managing
     * secure credentials.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
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
        return getRoles(null);
    }

    /**
     * Returns a collection of Splunk user roles.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
     * @return A collection of user roles.
     */
    public EntityCollection<Role> getRoles(Args args) {
        return new EntityCollection<Role>(
            this, "authorization/roles", Role.class, args);
    }

    /**
     * Returns a collection of saved searches.
     *
     * @return A collection of saved searches.
     */
    public SavedSearchCollection getSavedSearches() {
        return getSavedSearches((SavedSearchCollectionArgs)null);
    }
    
    /**
     * Returns a collection of saved searches.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link SavedSearchCollectionArgs}.
     * @return A collection of saved searches.
     */
    // NOTE: This overload exists primarily to provide better documentation
    //       for the "args" parameter.
    public SavedSearchCollection getSavedSearches(SavedSearchCollectionArgs args) {
        return getSavedSearches((Args)args);
    }

    /**
     * Returns a collection of saved searches.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
     * @return A collection of saved searches.
     */
    public SavedSearchCollection getSavedSearches(Args args) {
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
        return getUploads(null);
    }

    /**
     * Returns a collection of in-progress oneshot uploads.
     *
     * @param namespace This collection's namespace; there are no other
     * optional arguments for this endpoint.
     * @return A collection of in-progress oneshot uploads
     */
    public EntityCollection<Upload> getUploads(Args namespace) {
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
        return getUsers(null);
    }

    /**
     * Returns a collection of Splunk users.
     *
     * @param args Collection arguments that specify the number of entities to 
     * return and how to sort them. See {@link CollectionArgs}.
     * @return A collection of users.
     */
    public UserCollection getUsers(Args args) {
        return new UserCollection(this, args);
    }

    /**
     * Authenticates the {@code Service} instance with the username and password
     * that were specified when the instance was created.
     * 
     * @return The current {@code Service} instance.
     */
    public Service login() {
        if (this.username == null || this.password == null) {
            throw new IllegalStateException("Missing username or password.");
        }
        else {
            return login(this.username, this.password);
        }
    }
    
    /**
     * Authenticates the {@code Service} instance with a specified username and 
     * password. Note that these values override any previously-set values for 
     * username and password.
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
    public InputStream oneshotSearch(String query) {
        return oneshotSearch(query, null);
    }

    /**
     * Creates a oneshot synchronous search using search arguments.
     *
     * @param query The search query.
     * @param args The search arguments:<ul>
     * <li>"output_mode": Specifies the output format of the results (XML, JSON,
     * or CSV).</li>
     * <li>"earliest_time": Specifies the earliest time in the time range to 
     * search. The time string can be a UTC time (with fractional seconds), a 
     * relative time specifier (to now), or a formatted time string.</li>
     * <li>"latest_time": Specifies the latest time in the time range to search.
     * The time string can be a UTC time (with fractional seconds), a relative 
     * time specifier (to now), or a formatted time string.</li>
     * <li>"rf": Specifies one or more fields to add to the search.</li></ul>
     * @return The search results.
     */
    public InputStream oneshotSearch(String query, Map args) {
        args = Args.create(args);
        args.put("search", query);
        args.put("exec_mode", "oneshot");

        // By default, don't highlight search terms in the search output.
        if (!args.containsKey("segmentation")) {
            args.put("segmentation", "none");
        }

        ResponseMessage response = post("search/jobs", args);
        return response.getContent();
    }

    /**
     * Creates a oneshot synchronous search using search arguments.
     *
     * @param query The search query.
     * @param args The search arguments:<ul>
     * <li>"output_mode": Specifies the output format of the results (XML, JSON,
     * or CSV).</li>
     * <li>"earliest_time": Specifies the earliest time in the time range to 
     * search. The time string can be a UTC time (with fractional seconds), a 
     * relative time specifier (to now), or a formatted time string.</li>
     * <li>"latest_time": Specifies the latest time in the time range to search.
     * The time string can be a UTC time (with fractional seconds), a relative 
     * time specifier (to now), or a formatted time string.</li>
     * <li>"rf": Specifies one or more fields to add to the search.</li></ul>
     * @return The search results.
     */
    public InputStream oneshotSearch(String query, Args args) {
        return oneshotSearch(query, (Map<String, Object>)args);
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
        return post("server/control/restart");
    }

    /**
     * Creates an asynchronous search using the given query. Use this
     * method for simple searches.
     *
     * @param query The search query.
     * @return The search job.
     */
    public Job search(String query) {
        return search(query, null);
    }

    /**
     * Creates an asynchronous search job using the given query and
     * search arguments.
     *
     * @param query The search query.
     * @param args The search arguments.
     * @return The search job.
     */
    public Job search(String query, Map<String, Object> args) {
        args = Args.create(args);

        return this.getJobs().create(query, args);
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
        if (token != null) {
            request.getHeader().put("Authorization", token);
        }
        return super.send(fullpath(path), request);
    }

    /**
     * Provides a session token for use by this {@code Service} instance. 
     * Session tokens can be shared across multiple {@code Service} instances.
     *
     * @param value The session token, which is a basic authorization header in 
     * the format "Basic <i>sessiontoken</i>", where <i>sessiontoken</i> is the 
     * Base64-encoded "username:password" string.
     */
    public void setToken(String value) {
        this.token = value;
    }

    /**
     * Returns true if this Splunk instance's version is no earlier than
     * the version specified in {@code version}.
     *
     * So when called on a Splunk 4.3.2 instance:
     *   * {@code versionIsAtLeast("4.3.2")} is {@code true}.
     *   * {@code versionIsAtLeast("4.1.0")} is {@code true}.
     *   * {@code versionIsAtLeast("5.0.0")} is {@code false}.
     *
     * @param version The version to compare this Splunk instance's version against.
     * @return {@code true} if this Splunk instance's version is equal or
     *         greater than {@code version}; {@code false} otherwise.
     */
    boolean versionIsAtLeast(String version) {
        return versionCompare(version) >= 0;
    }

    /**
     * Returns true if this Splunk instance's version is earlier than
     * the version specified in {@code version}.
     *
     * So when called on a Splunk 4.3.2 instance:
     *   * {@code versionIsEarlierThan("4.3.2")} is {@code false}.
     *   * {@code versionIsEarlierThan("4.1.0")} is {@code false}.
     *   * {@code versionIsEarlierThan("5.0.0")} is {@code true}.
     *
     * @param version The version to compare this Splunk instance's version against.
     * @return {@code true} if this Splunk instance's version is less
     *         than {@code version}; {@code false} otherwise.
     */
    boolean versionIsEarlierThan(String version) {
        return versionCompare(version) < 0;
    }

    /**
     * Returns a value indicating how the version of this Splunk instance 
     * compares to a given version: 
     * <ul>
     * <li>{@code -1} if this version < the given version</li>
     * <li>{@code  0} if this version = the given version</li>
     * <li>{@code  1} if this version > the given version</li>
     * </ul>
     * 
     * @param otherVersion The other version to compare to. 
     * @return -1 if this version is less than, 0 if this version is equal to, 
     *         or 1 if this version is greater than the given version.
     */
    public int versionCompare(String otherVersion) {
        String[] components1 = this.version.split("\\.");
        String[] components2 = otherVersion.split("\\.");
        int numComponents = Math.max(components1.length, components2.length);
        
        for (int i = 0; i < numComponents; i++) {
            int c1 = (i < components1.length)
                    ? Integer.parseInt(components1[i], 10) : 0;
            int c2 = (i < components2.length)
                    ? Integer.parseInt(components2[i], 10) : 0;
            if (c1 < c2) {
                return -1;
            } else if (c1 > c2) {
                return 1;
            }
        }
        return 0;
    }
}
