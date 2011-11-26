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

import com.splunk.Service;

import org.openide.nodes.Children;
import org.openide.nodes.Node;

class ServiceKids extends Children.Keys<String> {
    Service service;

    ServiceKids(Service service) {
        this.service = service;
    }

    @Override protected void addNotify() {
        String[] kinds = new String[] {
            "settings",
            "loggers",
            "messages",
            "distributedconfig",
            "distributedpeers",
            "deploymentclient",
            "deploymentservers",
            "deploymentserverclasses",
            "deploymenttenants",
            "licenses",
            "licenseGroups",
            "licensePools",
            "licenseSlaves",
            "licenseStacks",
            "roles",
            "users",
            "passwords",
            "apps",
            "confs",
            "searches",
            "eventtypes",
            "indexes",
            "inputs",
            "outputDefault",
            "outputGroups",
            "outputServers",
            "outputSyslogs",
            "jobs",
        };
        setKeys(kinds);
    }

    private Node createNode(String kind) {
        if (kind.equals("apps"))
            return new EntityCollectionNode(
                "Apps", 
                service.getApplications(), 
                AppNode.class);

        if (kind.equals("confs"))
            return new ConfCollectionNode(service.getConfs());

        if (kind.equals("distributedconfig"))
            return new DistributedConfigurationNode(
                service.getDistributedConfiguration());

        if (kind.equals("distributedpeers"))
            return new EntityCollectionNode(
                "Distributed Peers", 
                service.getDistributedPeers(), 
                DistributedPeerNode.class);

        if (kind.equals("deploymentclient"))
            return new DeploymentClientNode(
                service.getDeploymentClient());

        if (kind.equals("deploymentservers"))
            return new EntityCollectionNode(
                "Deployment Servers",
                service.getDeploymentServers(),
                DeploymentServerNode.class);

        if (kind.equals("deploymentserverclasses"))
            return new EntityCollectionNode(
                "Deployment Server Classes",
                service.getDeploymentServerClasses(),
                DeploymentServerClassNode.class);

        if (kind.equals("deploymenttenants"))
            return new EntityCollectionNode(
                "Deployment Tenants",
                service.getDeploymentTenants(),
                DeploymentTenantNode.class);

        if (kind.equals("eventtypes"))
            return new EntityCollectionNode(
                "EventTypes", 
                service.getEventTypes(), 
                EventTypeNode.class);

        if (kind.equals("indexes"))
            return new EntityCollectionNode(
                "Indexes", 
                service.getIndexes(), 
                IndexNode.class);

        if (kind.equals("inputs"))
            return new EntityCollectionNode(
                "Inputs", 
                service.getInputs(), 
                InputNode.class);

        if (kind.equals("jobs"))
            return new EntityCollectionNode(
                "Jobs", 
                service.getJobs(), 
                JobNode.class);

        if (kind.equals("licenses"))
            return new EntityCollectionNode(
                "Licenses", 
                service.getLicenses(), 
                LicenseNode.class);

        if (kind.equals("licensePools"))
            return new EntityCollectionNode(
                "License Pools", 
                service.getLicensePools(), 
                LicensePoolNode.class);

        if (kind.equals("licenseGroups"))
            return new EntityCollectionNode(
                "License Groups", 
                service.getLicenseGroups(), 
                LicenseGroupNode.class);

        if (kind.equals("licenseSlaves"))
            return new EntityCollectionNode(
                "License Slaves",
                service.getLicenseSlaves(),
                LicenseSlaveNode.class);

        if (kind.equals("licenseStacks"))
            return new EntityCollectionNode(
                "License Stacks", 
                service.getLicenseStacks(), 
                LicenseStackNode.class);

        if (kind.equals("loggers"))
            return new EntityCollectionNode(
                "Loggers", 
                service.getLoggers(), 
                LoggerNode.class);

        if (kind.equals("messages"))
            return new EntityCollectionNode(
                "Messages", 
                service.getMessages(), 
                MessageNode.class);

        if (kind.equals("outputDefault"))
            return new  OutputDefaultNode(
                service.getOutputDefault());

        if (kind.equals("outputGroups"))
            return new EntityCollectionNode(
                "Output Groups", 
                service.getOutputGroups(), 
                OutputGroupNode.class);

        if (kind.equals("outputServers"))
            return new EntityCollectionNode(
                "Output Servers", 
                service.getOutputServers(), 
                OutputServerNode.class);

        if (kind.equals("outputSyslogs"))
            return new EntityCollectionNode(
                "Output Syslogs", 
                service.getOutputSyslogs(), 
                OutputSyslogNode.class);

        if (kind.equals("passwords"))
            return new EntityCollectionNode(
                "Passwords",
                service.getPasswords(),
                PasswordNode.class);

        if (kind.equals("searches"))
            return new EntityCollectionNode(
                "Saved Searches", 
                service.getSavedSearches(), 
                SavedSearchNode.class);

        if (kind.equals("settings"))
            return new SettingsNode(service.getSettings());

        if (kind.equals("roles"))
            return new EntityCollectionNode(
                "Roles", 
                service.getRoles(), 
                RoleNode.class);

        if (kind.equals("users"))
            return new EntityCollectionNode(
                "Users", 
                service.getUsers(), 
                UserNode.class);

        return null;
    }

    @Override protected Node[] createNodes(String kind) {
        return new Node[] { createNode(kind) };
    }
}
