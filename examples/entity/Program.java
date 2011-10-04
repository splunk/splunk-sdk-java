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


import com.splunk.*;

import java.util.Collection;

public class Program extends com.splunk.sdk.Program {
    public static void main(String[] args) {
        Program program = new Program();
        try {
            program.init(args).run();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void dumpHeader(Header header) {
        System.out.println("ID:           " + header.id);
        System.out.println("generator:    " + header.generator);
        System.out.println("title:        " + header.title);
        System.out.println("updated:      " + header.updated);
        System.out.println("author:       " + header.author);

        // semi-optional
        if (header.link.size() > 0)
            System.out.println("link:         " + header.link);
        if (header.itemsPerPage != -1)
            System.out.println("itemsperpage: " + header.itemsPerPage);
        if (header.startIndex != -1)
            System.out.println("startindex:   " + header.startIndex);
        if (header.totalResults != -1)
            System.out.println("totalresults: " + header.totalResults);
        if (header.messages != null && header.messages.length() > 0)
            System.out.println("messages:     " + header.messages);
        System.out.println("");
    }

    private static void dumpEntry(Entry entry) {
        System.out.println("    ID:           " + entry.id);
        System.out.println("    updated:      " + entry.updated);
        System.out.println("    title:        " + entry.title);

        // semi-optional
        if (entry.published != null)
            System.out.println("    published:    " + entry.published);
        if (!entry.content.isEmpty())
            System.out.println("    content:      " + entry.content);
        System.out.println("");
    }

    private static void dumpEntries(Collection <Entry> entries) {
        // Iterate over entries
         for (Entry entry: entries) {
             dumpEntry(entry);
         }
    }

    private static void dumpEntity(Entity entity) {
        dumpHeader(entity.header);
        dumpEntries(entity.entry);
    }

    public void run() throws Exception {
        Service service = new Service(this.host, this.port, this.scheme);
        service.login(this.username, this.password);

        System.out.println("APPLICATIONS **********************************************\n");
        Apps application =  new Apps(service);
        System.out.println("APP List:" + application.nameList() + "\n");
        dumpEntity(application.get("eaitest"));
        dumpEntity(application.get());

        System.out.println("INDEXES ***************************************************\n");
        Indexes indexes =  new Indexes(service);
        System.out.println("Indexes List:" + indexes.nameList() + "\n");
        dumpEntity(indexes.get("_internal"));
        dumpEntity(indexes.get());

        System.out.println("INPUTS ****************************************************\n");
        Inputs inputs =  new Inputs(service);
        System.out.println("Inputs List:" + inputs.nameList() + "\n");
        dumpEntity(inputs.get("tcp"));
        dumpEntity(inputs.get("tcp/ssl"));
        dumpEntity(inputs.get());

        System.out.println("CAPABILITIES **********************************************\n");
        Capabilities capabilities  =  new Capabilities(service);
        System.out.println("Capabilities List:" + capabilities.nameList() + "\n");
        dumpEntity(capabilities.get());

        System.out.println("CONFS *****************************************************\n");
        Confs confs =  new Confs(service);
        System.out.println("Confs List:" + confs.nameList() + "\n");
        dumpEntity(confs.get("authentication"));
        dumpEntity(confs.get());

        System.out.println("JOBS ******************************************************\n");
        Jobs jobs =  new Jobs(service);
        System.out.println("Jobs List:" + jobs.nameList() + "\n");
        dumpEntity(jobs.get());

        System.out.println("LOGGER ****************************************************\n");
        Logger logger  =  new Logger(service);
        System.out.println("Logger List:" + logger.nameList() + "\n");
        dumpEntity(logger.get("AdminHandler:AuthenticationHandler"));
        dumpEntity(logger.get());

        System.out.println("MESSAGES **************************************************\n");
        Messages messages =  new Messages(service);
        System.out.println("Messages List:" + messages.nameList() + "\n");
        dumpEntity(messages.get());

        System.out.println("ROLES *****************************************************\n");
        Roles roles =  new Roles(service);
        System.out.println("Roles List:" + roles.nameList() + "\n");
        dumpEntity(roles.get("admin"));
        dumpEntity(roles.get());

        System.out.println("USERS *****************************************************\n");
        Users users =  new Users(service);
        System.out.println("Users List:" + users.nameList() + "\n");
        dumpEntity(users.get("admin"));
        dumpEntity(users.get());

        System.out.println("DEPLOYMENT SERVER *****************************************\n");
        DeploymentServer ds =  new DeploymentServer(service);
        System.out.println("Deployment Server List:" + ds.nameList() + "\n");
        dumpEntity(ds.get("default"));
        dumpEntity(ds.get());

        System.out.println("DEPLOYMENT CLIENT *****************************************\n");
        DeploymentClient dc =  new DeploymentClient(service);
        System.out.println("Deployment Client List:" + dc.nameList() + "\n");
        dumpEntity(dc.get());

        System.out.println("DEPLOYMENT SERVER CLASS ***********************************\n");
        DeploymentServerclass dsc =  new DeploymentServerclass(service);
        System.out.println("Deployment Serverclass:" + dsc.nameList() + "\n");
        dumpEntity(dsc.get());

        System.out.println("DEPLOYMENT TENANTS ****************************************\n");
        DeploymentTenants dt =  new DeploymentTenants(service);
        System.out.println("Deployment Tenants:" + dt.nameList() + "\n");
        dumpEntity(dt.get("default"));
        dumpEntity(dt.get());

        System.out.println("DISTRIBUTED SEARCH PEERS **********************************\n");
        DistributedPeers dsp =  new DistributedPeers(service);
        System.out.println("Distributed Search Peers:" + dsp.nameList() + "\n");
        //dumpEntity(dsp.get("default"));
        dumpEntity(dsp.get());

        System.out.println("DISTRIBUTED SEARCH CONFIG *********************************\n");
        DistributedConfig dsconfig =  new DistributedConfig(service);
        System.out.println("Distributed Search Peers:" + dsconfig.nameList() + "\n");
        dumpEntity(dsconfig.get("distributedSearch"));
        dumpEntity(dsconfig.get());
    }
}
