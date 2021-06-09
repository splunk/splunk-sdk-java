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

package com.splunk.examples.search_saved;

import com.splunk.*;

public class Program {

    static String countString = "How many saved searches to return";
    static String offsetString = "The offset into the collection";

    private static void list(Service service, Args window) {

        EntityCollection<SavedSearch> searches;

        if (window == null)
            searches = service.getSavedSearches();
        else
            searches = service.getSavedSearches(window);

        for (SavedSearch entity: searches.values()) {
            System.out.println(
                entity.getTitle() + "\n" +
                "    (" + entity.getSearch()+ ")");
        }
    }

    public static void main(String[] args) {
        Command command = Command.splunk("search saved").parse(args);
        command.addRule("count", String.class, countString);
        command.addRule("offset", String.class, offsetString);
        Service service = Service.connect(command.opts);

        if (command.args.length == 0) {
            list(service, null);
            return;
        }

        Args window = new Args();
        for (String value: args) {
            String [] parts  = value.split("=");
            if (parts.length != 2) {
                Command.error("Arguments are of the form: name=value");
            }
            if (!parts[0].equals("count") && !parts[0].equals("offset")) {
                Command.error("Unknown key: " + parts[0]);
            }
            window.put(parts[0], parts[1]);
        }

        list(service, window);
    }
}
