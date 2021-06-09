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

package com.splunk.examples.explorer;

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
            "system",
            "indexes",
            "inputs",
            "outputs",
            "namespaces",
        };
        setKeys(kinds);
    }

    private Node createNode(String kind) {
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

        if (kind.equals("namespaces"))
            return new NamespacesNode(service);

        if (kind.equals("outputs"))
            return new GroupNode(
                "Outputs",
                new OutputDefaultNode(service.getOutputDefault()),
                new EntityCollectionNode(
                    "Output Groups", 
                    service.getOutputGroups(), 
                    OutputGroupNode.class),
                new EntityCollectionNode(
                    "Output Servers", 
                    service.getOutputServers(), 
                    OutputServerNode.class),
                new EntityCollectionNode(
                    "Output Syslogs", 
                    service.getOutputSyslogs(), 
                    OutputSyslogNode.class));
                    
        if (kind.equals("system"))
            return new GroupNode(
                "System",
                new SettingsNode(service.getSettings()),
                new EntityCollectionNode(
                    "Loggers", service.getLoggers(), LoggerNode.class),
                new EntityCollectionNode(
                    "Messages", service.getMessages(), MessageNode.class),
                new GroupNode(
                    "Deployment",
                    new DistributedConfigurationNode(
                        service.getDistributedConfiguration()),
                    new EntityCollectionNode(
                        "Distributed Peers", 
                        service.getDistributedPeers(), 
                        DistributedPeerNode.class),
                    new DeploymentClientNode(
                        service.getDeploymentClient()),
                    new EntityCollectionNode(
                        "Deployment Servers",
                        service.getDeploymentServers(),
                        DeploymentServerNode.class),
                    new EntityCollectionNode(
                        "Deployment Server Classes",
                        service.getDeploymentServerClasses(),
                        DeploymentServerClassNode.class),
                    new EntityCollectionNode(
                        "Deployment Tenants",
                        service.getDeploymentTenants(),
                        DeploymentTenantNode.class)),
                new GroupNode(
                    "Licensing",
                    new EntityCollectionNode(
                        "Licenses", 
                        service.getLicenses(), 
                        LicenseNode.class),
                    new EntityCollectionNode(
                        "License Pools", 
                        service.getLicensePools(), 
                        LicensePoolNode.class),
                    new EntityCollectionNode(
                        "License Groups", 
                        service.getLicenseGroups(), 
                        LicenseGroupNode.class),
                    new EntityCollectionNode(
                        "License Slaves",
                        service.getLicenseSlaves(),
                        LicenseSlaveNode.class),
                    new EntityCollectionNode(
                        "License Stacks", 
                        service.getLicenseStacks(), 
                        LicenseStackNode.class)),
                new EntityCollectionNode(
                    "Roles", service.getRoles(), RoleNode.class),
                new EntityCollectionNode(
                    "Users", service.getUsers(), UserNode.class));

        return null;
    }

    @Override protected Node[] createNodes(String kind) {
        return new Node[] { createNode(kind) };
    }
}
