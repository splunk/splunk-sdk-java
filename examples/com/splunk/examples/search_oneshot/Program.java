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

package com.splunk.examples.search_oneshot;

import com.splunk.Args;
import com.splunk.HttpException;
import com.splunk.ResultsReaderXml;
import com.splunk.Service;
import com.splunk.Command;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

// Note: not all search parameters are exposed to the CLI for this example.
public class Program {

    static String earliestTimeText = "Search earliest time";
    static String fieldListText =
         "A comma-separated list of the fields to return";
    static String latestTimeText = "Search latest time";
    static String outputModeText =
        "Search output format {csv, raw, json, xml} (default: xml)";
    static String rawText = "Set to 1 if raw events are displayed";
    static String statusBucketsText =
        "Number of status buckets to use for search (default: 0)";

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
        command.addRule("earliest_time", String.class, earliestTimeText);
        command.addRule("field_list", String.class, fieldListText);
        command.addRule("latest_time", String.class, latestTimeText);
        command.addRule("output_mode", String.class, outputModeText);
        command.addRule("raw", Integer.class, rawText);
        command.addRule("status_buckets", Integer.class, statusBucketsText);
        command.parse(args);

        if (command.args.length != 1)
            Command.error("Search expression required");
        String query = command.args[0];

        String earliestTime = null;
        if (command.opts.containsKey("earliest_time"))
            earliestTime = (String)command.opts.get("earliest_time");

        String fieldList = null;
        if (command.opts.containsKey("field_list"))
            fieldList = (String)command.opts.get("field_list");

        String latestTime = null;
        if (command.opts.containsKey("latest_time"))
            latestTime = (String)command.opts.get("latest_time");

        int statusBuckets = 0;
        if (command.opts.containsKey("status_buckets"))
            statusBuckets = (Integer)command.opts.get("status_buckets");

        String outputMode = "xml";
        if (command.opts.containsKey("output_mode"))
            outputMode = (String)command.opts.get("output_mode");

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

        // Create the oneshot search query & query arguments.
        Args queryArgs = new Args();
        if (earliestTime != null)
            queryArgs.put("earliest_time", earliestTime);
        if (fieldList != null)
            queryArgs.put("field_list", fieldList);
        if (latestTime != null)
            queryArgs.put("latest_time", latestTime);
        if (statusBuckets > 0)
            queryArgs.put("status_buckets", statusBuckets);
        queryArgs.put("output_mode", outputMode);

        // Execute the oneshot query, which returns the stream (i.e. there is
        // no search job created, just a one time search)
        InputStream stream = service.oneshot(query, queryArgs);

        boolean rawData = true;
        if (command.opts.containsKey("raw")) {
            int tmp  = (Integer)command.opts.get("raw");
            if (tmp == 0 ) rawData = false;
        }

        if (!rawData) {
            HashMap<String, String> map;
            try {
                ResultsReaderXml resultsReader = new ResultsReaderXml(stream);
                while ((map = resultsReader.getNextEvent()) != null) {
                    System.out.println("EVENT:********");
                    System.out.println("   " + map);
                }
                resultsReader.close();
            } catch (IOException e) {
                System.out.println("I/O exception: " + e);
            }
        }
        else {
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
}
