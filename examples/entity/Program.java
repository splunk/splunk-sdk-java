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

    private static void dumpEntity(Element element) {
        dumpHeader(element.header);
        dumpEntries(element.entry);
    }

    public void run() throws Exception {
        Service service = new Service(this.host, this.port, this.scheme);
        service.login(this.username, this.password);


        System.out.println("APPLICATIONS redux ****************************************\n");
        Apps apps =  new Apps(service);
        System.out.println("Application list: " + apps.list() + "\n");

        System.out.println("Collection, index one item");
        dumpEntity(apps.get("eaitest"));
        System.out.println("Collection: ");
        dumpEntity(apps.get());

        System.out.println("Entity: (single item)");
        App app =  new App(service, "eaitest");
        dumpEntity(app.read());

        System.out.println("INDEX redux ***********************************************\n");
        Indexes indexes =  new Indexes(service);
        dumpEntity(indexes.get());
        dumpEntity(indexes.get("_internal"));

        System.out.println("Index list: " + indexes.list() + "\n");
        for (String index: indexes.list()) {
            Index idx = new Index(service, index);
            dumpEntity(idx.get());
            System.out.println(idx.readmeta());
        }

        Index idx = new Index(service, "wkc");
        idx.clean();

        System.out.println("CONFS redux ****************************************\n");
        Confs confs =  new Confs(service);
        System.out.println("Conf list: " + confs.list());
        dumpEntity(confs.get());
        dumpEntity(confs.get("app"));

        Conf conf =  new Conf(service, "app");
        dumpEntity(conf.read());

        System.out.println("JOBS redux ****************************************\n");
        Jobs jobs =  new Jobs(service);
        System.out.println("jobs list: " + jobs.list());
    }
}
