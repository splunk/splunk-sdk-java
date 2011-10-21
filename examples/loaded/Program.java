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

import com.splunk.atom.*;
import com.splunk.http.ResponseMessage;
import com.splunk.Service;

import java.io.IOException;
import java.util.Map;

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

    public Service connect() throws IOException {
        Service service = new Service(this.host, this.port, this.scheme);
        service.login(this.username, this.password);
        return service;
    }

    static void printAtomObject(AtomObject item) {
        System.out.format("Id = '%s'\n", item.Id);
        System.out.format("Title = '%s'\n", item.Title);
        System.out.format("Updated = '%s'\n", item.Updated);
        for (Map.Entry<String, String> entry : item.Links.entrySet()) {
            // System.out.format("Link %s => %s\n", entry.getKey(), entry.getValue());
            System.out.format("Link %s => ...\n", entry.getKey());
        }
    }

    static void printAtomFeed(AtomFeed feed) {
        printAtomObject(feed);
        System.out.format("ItemsPerPage = %d\n", feed.ItemsPerPage);
        System.out.format("StartIndex = %d\n", feed.StartIndex);
        System.out.format("TotalResults = %d\n", feed.TotalResults);
        for (AtomEntry entry : feed.Entries.values()) {
            System.out.format("**********\n");
            printAtomEntry(entry);
        }
    }

    static void printAtomEntry(AtomEntry entry) {
        printAtomObject(entry);
        if (entry.Published != null)
            System.out.format("Published = '%s'\n", entry.Published);
        if (entry.Content != null)
            System.out.format("Content = '%s'\n", entry.Content.toString());
    }

    public void run() throws Exception {
        Service service = connect();

        String path = this.args.length > 0 ? this.args[0] : "/";
        ResponseMessage response = service.get(path);

        int status = response.getStatus();
        if (status != 200)
            throw new RuntimeException(String.format("HTTP Error: %d", status));

        System.out.format("Loading %s ..\n", path);
        AtomFeed feed = AtomFeed.create(response.getContent());

        printAtomFeed(feed);
    }
}

