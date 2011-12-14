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

package com.splunk.sdk.tail;

import com.splunk.*;
import com.splunk.sdk.Command;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Tail an index
 */

public class Program {

    static String outputModeText =
        "Search output format {csv, raw, json, xml} (default: xml)";

    public static void main(String[] args) {
        try {
            run(args);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static void run(String[] argsIn) throws Exception {

        Command command = Command.splunk("tail");
        command.addRule("format", Integer.class, outputModeText);
        command.parse(argsIn);

        if (command.args.length != 1)
            Command.error("Search expression required");
        String query = command.args[0];

        Service service = Service.connect(command.opts);

        String outputMode = "csv";
        if (command.opts.containsKey("output_mode"))
            outputMode = (String)command.opts.get("output_mode");
        Args args = new Args();

        // search args
        args.put("timeout", "60");              // don't keep search around
        args.put("output_mode", outputMode);    // output in specific format
        args.put("earliest_time", "rt");        // realtime
        args.put("latest_time", "rt");          // realtime
        args.put("search_mode", "realtime");    // realtime

        InputStream is = service.export(query, args);

        // use UTF8 sensitive reader/writers
        InputStreamReader reader = new InputStreamReader(is, "UTF8");
        OutputStreamWriter writer = new OutputStreamWriter(System.out);

        int size = 1024;
        char[] buffer = new char[size];
        while (true) {
            int count = reader.read(buffer);
            if (count == -1) break;
            writer.write(buffer, 0, count);
            writer.flush();
        }
    }
}
