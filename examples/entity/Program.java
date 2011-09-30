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

import com.splunk.Entity;
import com.splunk.Entry;
import com.splunk.Header;

import java.util.ArrayList;

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
        System.out.println("link:         " + header.link);
        System.out.println("itemsperpage: " + header.itemsPerPage);
        System.out.println("startindex:   " + header.startIndex);
        System.out.println("totalresults: " + header.totalResults);
        System.out.println("messages:     " + header.messages + "\n");
    }

    private static void dumpEntries(ArrayList<Entry> entries) {
        // collections is just an entity with more than one entry
         for (int idx = 0; idx < entries.size(); idx++ ) {
             Entry entry = entries.get(idx);

             System.out.println("    ID:           " + entry.id);
             System.out.println("    updated:      " + entry.updated);
             System.out.println("    title:        " + entry.title);
             System.out.println("    content:      " + entry.content + "\n");
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
        System.out.println("APP List:" + application.list() + "\n");
        dumpEntity(application.get("eaitest"));
        dumpEntity(application.get());


        System.out.println("INDEXES ***************************************************\n");
        Indexes indexes =  new Indexes(service);
        System.out.println("Indexes List:" + indexes.list() + "\n");
        dumpEntity(indexes.get("_internal"));
        dumpEntity(indexes.get());

        System.out.println("INPUTS ****************************************************\n");
        Inputs inputs =  new Inputs(service);
        System.out.println("Inputs List:" + inputs.list() + "\n");
        dumpEntity(inputs.get("tcp"));
        dumpEntity(inputs.get("tcp/ssl"));
        dumpEntity(inputs.get());
    }
}
