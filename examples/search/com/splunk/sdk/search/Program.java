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

package com.splunk.sdk.search;

import com.splunk.*;
import com.splunk.sdk.Command;
import com.splunk.external.ResultsReaderCsv;
import com.splunk.external.ResultsReaderJson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;

// Note: not all search parameters are exposed to the CLI for this example.
public class Program {
    static String[] outputChoices = new String[] {
        "events", "results", "preview", "searchlog", "summary", "timeline"
    };

    static String earliestTimeText = "Search earliest time";
    static String fieldListText =
        "A comma-separated list of the fields to return";
    static String latestTimeText = "Search latest time";
    static String offset =
        "The first result (inclusive) from which to begin returning data. " +
        "(default: 0)";
    static String outputText =
        "Which search results to output {events, results, preview, searchlog," +
        " summary, timeline} (default: results)";
    static String outputModeText =
        "Search output format {csv, raw, json, xml} (default: xml)";
    static String resultsCount =
        "The maximum number of results to return (default: 100)";
    static String readerText = "Use ResultsReader";
    static String statusBucketsText =
        "Number of status buckets to use for search (default: 0)";
    static String verboseString = "Display search progress";

    public static void main(String[] args) {
        try {
            run(args);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static void run(String[] args) throws Exception  {
        Command command = Command.splunk("search");
        command.addRule("count", Integer.class, resultsCount);
        command.addRule("earliest_time", String.class, earliestTimeText);
        command.addRule("field_list", String.class, fieldListText);
        command.addRule("latest_time", String.class, latestTimeText);
        command.addRule("offset", Integer.class, offset);
        command.addRule("output", String.class, outputText);
        command.addRule("output_mode", String.class, outputModeText);
        command.addRule("reader", readerText);
        command.addRule("status_buckets", Integer.class, statusBucketsText);
        command.addRule("verbose",  verboseString);
        command.parse(args);

        if (command.args.length != 1)
            Command.error("Search expression required");
        String query = command.args[0];

        int resultsCount = 100;
        if (command.opts.containsKey("count"))
            resultsCount = (Integer)command.opts.get("count");

        String earliestTime = null;
        if (command.opts.containsKey("earliest_time"))
            earliestTime = (String)command.opts.get("earliest_time");

        String fieldList = null;
        if (command.opts.containsKey("field_list"))
            fieldList = (String)command.opts.get("field_list");

        String latestTime = null;
        if (command.opts.containsKey("latest_time"))
            latestTime = (String)command.opts.get("latest_time");

        int offset = 0;
        if (command.opts.containsKey("offset"))
            offset = (Integer)command.opts.get("offset");

        String output = "results";
        if (command.opts.containsKey("output")) {
            output = (String)command.opts.get("output");
            if (!Arrays.asList(outputChoices).contains(output))
                Command.error("Unsupported output: '%s'", output);
        }

        String outputMode = "xml";
        if (command.opts.containsKey("output_mode"))
            outputMode = (String)command.opts.get("output_mode");

        int statusBuckets = 0;
        if (command.opts.containsKey("status_buckets"))
            statusBuckets = (Integer)command.opts.get("status_buckets");

        boolean verbose = command.opts.containsKey("verbose");

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

        // Create a search job for the given query & query arguments.
        Args queryArgs = new Args();
        if (earliestTime != null)
            queryArgs.put("earliest_time", earliestTime);
        if (fieldList != null)
            queryArgs.put("field_list", fieldList);
        if (latestTime != null)
            queryArgs.put("latest_time", latestTime);
        if (statusBuckets > 0)
            queryArgs.put("status_buckets", statusBuckets);

        Job job = service.getJobs().create(query, queryArgs);

        // Wait until results are available.
        boolean status = false;
        while (!job.isDone()) {

            // If no outputs are available, optionally print status
            if (verbose && job.isReady()) {
                float progress = job.getDoneProgress() * 100.0f;
                int scanned = job.getScanCount();
                int matched = job.getEventCount();
                int results = job.getResultCount();
                System.out.format(
                    "\r%03.1f%% done -- %d scanned -- %d matched -- %d results",
                    progress, scanned, matched, results);
                status = true;
            }

            Thread.sleep(1000);
        }

        if (status) System.out.println("");

        InputStream stream = null;

        Args outputArgs = new Args();
        outputArgs.put("count", resultsCount);
        outputArgs.put("offset", offset);
        outputArgs.put("output_mode", outputMode);

        if (output.equals("results"))
            stream = job.getResults(outputArgs);
        else if (output.equals("events"))
            stream = job.getEvents(outputArgs);
        else if (output.equals("preview"))
            stream = job.getResultsPreview(outputArgs);
        else if (output.equals("searchlog"))
            stream = job.getSearchLog(outputArgs);
        else if (output.equals("summary"))
            stream = job.getSummary(outputArgs);
        else if (output.equals("timeline"))
            stream = job.getTimeline(outputArgs);
        else assert(false);

        boolean useReader = false;
        if (command.opts.containsKey("reader")) {
            useReader = true;
        }

        if (useReader) {
            HashMap<String, String> map;
            try {
                // NOTE: The JSON and CSV results readers requires an external
                // jar (gson-2.1.jar, opencsv-2.3.jar) for json and csv parsing
                // and is not part of the base Splunk Java SDK. These readers
                // are found in the "com.splunk.external" module. If you include
                // the splunk-external.jar and the both gson-2.1.jar and
                // opencsv-2.3.jar, you can use any -- just like this sample.
                ResultsReader resultsReader;
                if (outputMode.equals("xml"))
                    resultsReader = new ResultsReaderXml(stream);
                else if (outputMode.equals("json")) {
                    resultsReader = new ResultsReaderJson(stream);
                } else {
                    resultsReader = new ResultsReaderCsv(stream);
                }
                while ((map = resultsReader.getNextEvent()) != null) {
                    System.out.println("EVENT:********");
                    for (String key: map.keySet())
                        System.out.println("  " + key + " --> " + map.get(key));
                }
                resultsReader.close();
            } catch (Exception e) {
                System.out.println("exception: " + e);
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

        job.cancel();
    }
}