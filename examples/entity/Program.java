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


import com.splunk.Client;
import com.splunk.Service;
import com.splunk.data.Entity;
import com.splunk.data.Entry;
import com.splunk.data.Header;

import java.util.ArrayList;

public class Program {
    public static void main(String[] args) {
        try {
            run();
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

    static void run() throws Exception {
        Service service = new Service("192.168.242.114", 8089, "https");
        service.login("admin", "changed");

        Client client = new Client(service);

        dumpEntity(client.app("eaitest"));
        dumpEntity(client.apps());

    }
}
