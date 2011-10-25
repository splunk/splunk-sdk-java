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

// UNDONE: storage/passwords

import java.util.List;

public class Service extends com.splunk.Service {
    public Service(String host, int port, String scheme) {
        super(host, port, scheme);
    }

    public EntityCollection getApplications() {
        return new EntityCollection(
            this, "/services/apps/local", Application.class);
    }

    public EntityCollection getConfigurations() {
        return null; // UNDONE
    }

    public List<String> getCapabilities() {
        Entity caps = Entity.read(this, "/services/authorization/capabilities");
        return (List<String>)caps.getValue("capabilities");
    }

    public Entity getDeploymentClient() {
        return Entity.read(this, "/services/deployment/client");
    }

    public EntityCollection getDeploymentServers() {
        return new EntityCollection(this, "/services/deployment/server");
    }

    public EntityCollection getDeploymentServerClasses() {
        return new EntityCollection(this, "/services/deployment/serverclass");
    }

    public EntityCollection getDeploymentTenants() {
        return new EntityCollection(this, "/services/deployment/tenants");
    }

    public EntityCollection getEventTypes() {
        return new EntityCollection(this, "/services/saved/eventtypes");
    }

    // UNDONE: getFiredAlerts?

    public EntityCollection getIndexes() {
        return new EntityCollection(this, "/services/data/indexes");
    }

    public Entity getInfo() {
        return Entity.read(this, "/services/server/info");
    }

    public EntityCollection getInputs() {
        return null; //  UNDONE: flatten?
    }

    public EntityCollection getJobs() {
        return new EntityCollection(this, "/services/search/jobs", Job.class);
    }

    public EntityCollection getLicenseGroups() {
        return new EntityCollection(this, "/services/licenser/groups");
    }

    public EntityCollection getLicenseMessages() {
        return new EntityCollection(this, "/services/licenser/messages");
    }

    public EntityCollection getLicensePools() {
        return new EntityCollection(this, "/services/licenser/pools");
    }

    public EntityCollection getLicenseSlaves() {
        return new EntityCollection(this, "/services/licenser/slaves");
    }

    public EntityCollection getLicenseStacks() {
        return new EntityCollection(this, "/services/licenser/stacks");
    }

    public EntityCollection getLicenses() {
        return new EntityCollection(this, "/services/licenser/licenses");
    }

    public EntityCollection getLoggers() {
        return new EntityCollection(this, "/services/server/logger");
    }

    public Object getMessages() {
        return null; // UNDONE
    }

    // UNDONE: getOutputs

    public EntityCollection getRoles() {
        return new EntityCollection(this, "/services/authentication/roles");
    }

    public EntityCollection getSearches() {
        return new EntityCollection(this, "/services/saved/searches");
    }

    public Object getSettings() {
        return null; // UNDONE
    }

    public EntityCollection getUsers() {
        return new EntityCollection(this, "/services/authentication/users");
    }

    // public Object parse(String query) {}
    // public void restart() {}
}

