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

package com.splunk.examples.index;

import com.splunk.EntityCollection;
import com.splunk.Index;
import com.splunk.Service;
import com.splunk.Command;
import com.splunk.SplunkException;

public class Program {
    private static void list(Service service) {
        EntityCollection<Index> indexes = service.getIndexes();
        for (Index entity: indexes.values()) {
            System.out.println(
                entity.getTitle() +
                " (" + entity.get("totalEventCount") + ")");
        }
    }

    public static void main(String[] args) {
        Command command = Command.splunk("index").parse(args);
        Service service = Service.connect(command.opts);

        // This example takes optional arguments:
        // [action index-name]
        //
        // without cli arguments, all indexes and their totalEventCount
        // is displayed

        if (command.args.length == 0) {
            list(service);
            return;
        }

        if (command.args.length != 2)
            Command.error("Action and index-name required");

        String action = command.args[0];
        String name = command.args[1];

        EntityCollection<Index> indexes = service.getIndexes();
        if (action.equals("create")) {
            if (indexes.containsKey(name))
                Command.error("Index " + name + " already exists");
            indexes.create(name);
            return;
        }

        Index index = indexes.get(name);
        if (index == null)
            Command.error("Index '" + name + "' does not exists");

        if (action.equals("clean")) {
            try {
                index.clean(180);   // Timeout after 3 minutes.
            } catch (SplunkException e) {
                if (e.getCode() == SplunkException.INTERRUPTED) {
                    // User pressed Ctrl-C
                    return;
                } else {
                    throw e;
                }
            } 
        }
        else if (action.equals("disable"))
            index.disable();
        else if (action.equals("enable"))
            index.enable();
        else
            Command.error("Unknown action '" + action + "'");
    }
}
