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

package com.splunk.sdk.search_simple;

import com.splunk.Args;
import com.splunk.HttpException;
import com.splunk.sdk.Command;
import com.splunk.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

// Note: not all search parameters are exposed to the CLI for this example.
public class Program {

    public static void main(String[] args) {
        try {
            run(args);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static void run(String[] args) throws IOException {
        Command command = Command.splunk("search");
        command.parse(args);

        if (command.args.length != 1)
            Command.error("Search expression required");
        String query = command.args[0];

        Service service = Service.connect(command.opts);

        // Check the syntax of the query.
        try {
            Args parseArgs = new Args("parse_only", true);
            service.parse(query, parseArgs);
        }
        catch (HttpException e) {
            String detail = e.getDetail();
            Command.error("query '%s' is invalid: %s", query, detail);
        }


        // This is the simplest form of searching splunk. Note that input and
        // output args are allowed, they are not shown in this example.
        InputStream stream = service.search(query);

        InputStreamReader reader = new InputStreamReader(stream);
        OutputStreamWriter writer = new OutputStreamWriter(System.out);

        int size = 1024;
        char[] buffer = new char[size];
        while (true) {
            int count = reader.read(buffer);
            if (count == -1) break;
            writer.write(buffer, 0, count);
        }

        writer.write("\n");
        writer.close();
        reader.close();
    }
}
