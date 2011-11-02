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
        }

        if (this.args.length != 2)
            throw new Error("Action and index-name required");

        String action = this.args[0];
        String name = this.args[1];

        EntityCollection indexes = service.getIndexes();
        if (action.equals("create")) {
            if (indexes.containsKey(name))
                throw new Error("Index " + name + " already exists");
            indexes.create(name);
            return;
        }

        Index index = (Index)indexes.get(name);
        if (index == null)
            throw new Error("Index '" + name + "' does not exists");

        if (action.equals("clean"))
            index.clean();
        else if (action.equals("disable"))
            index.disable();
        else if (action.equals("enable"))
            index.enable();
        else
            throw new Error("Unknown action '" + action + "'");
    }
}
