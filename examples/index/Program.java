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

import com.splunk.EntityCollection;
import com.splunk.Index;
import com.splunk.Service;

public class Program extends com.splunk.sdk.Program {
    private void listAllIndexes(Service service) {
        EntityCollection<Index> indexes = service.getIndexes();
        for (Index entity: indexes.values()) {
            System.out.println(
                entity.getTitle() +
                " (" + entity.getContent().get("totalEventCount") + ")");
        }
    }

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

    public void run() throws Exception {
        Service service = new Service(this.host, this.port, this.scheme);
        service.login(this.username, this.password);

        // This example takes optional arguments:
        // [action index-name]
        //
        // without cli arguments, all indexes and their totalEventCount
        // is displayed

        if (this.args.length == 0) {
            listAllIndexes(service);
            return;
        } else {
            if (this.args.length != 2) {
                System.out.println("You must provide action and index name");
                return;
            }
        }

        String action = this.args[0];
        String name = this.args[1];
        EntityCollection indexes = service.getIndexes();

        if (action.equals("create")) {
            if (indexes.containsKey(name)) {
                System.out.println("Index " + name + " already exists");
                return;
            }
            service.getIndexes().create(name);
        } else {
            if (!indexes.containsKey(name)) {
                System.out.println("Index " + name + " does not exists");
                return;
            }
            if (action.equals("clean")) {
                service.getIndexes().get(name).clean();
            } else if (action.equals("disable")) {
                service.getIndexes().get(name).disable();
            } else if (action.equals("enable")) {
                service.getIndexes().get(name).disable();
            } else {
                System.out.println("Unknown action: " + action);
            }
        }
    }
}
