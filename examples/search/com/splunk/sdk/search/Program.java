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

// UNDONE: Add additional search args
// UNDONE: Add additional output args (offset, count, field_list, f)

package com.splunk.sdk.search;

import com.splunk.Args;
import com.splunk.HttpException;
import com.splunk.Job;
import com.splunk.sdk.Command;
import com.splunk.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class Program {
    static String[] outputChoices = new String[] {
        "events", "results", "preview", "searchlog", "summary", "timeline"
    };

    static String fieldListText = 
        "A comma-separated list of the fields to return";

    static String outputText = 
        "Which search results to output {events, results, preview, searchlog, summary, timeline} (default: results)";

    static String outputModeText = 
        "Search output format {csv, raw, json, xml} (default: xml)";

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
        command.addRule("field_list", String.class, fieldListText);
        command.addRule("output", String.class, outputText);
        command.addRule("output_mode", String.class, outputModeText);
        command.addRule("status_buckets", Integer.class, statusBucketsText);
        command.addRule("verbose", "Display search progress");
        command.parse(args);

        if (command.args.length != 1)
            Command.error("Search expression required");
        String query = command.args[0];

        String fieldList = null;
        if (command.opts.containsKey("field_list"))
            fieldList = (String)command.opts.get("field_list");

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
        if (fieldList != null)
            queryArgs.put("field_list", fieldList);
        if (statusBuckets > 0)
            queryArgs.put("status_buckets", statusBuckets);
        Job job = service.getJobs().create(query, queryArgs);

        // Wait until results are available.
        boolean status = false;
        while (true) {
            if (job.isDone())
                break;

            // If no outputs are available, optionally print status and wait.
            if (verbose) {
                float progress = job.getDoneProgress() * 100.0f;
                int scanned = job.getScanCount();
                int matched = job.getEventCount();
                int results = job.getResultCount();
                System.out.format(
                    "\r%03.1f%% done -- %d scanned -- %d matched -- %d results",
                    progress, scanned, matched, results);
                status = true;
            }

            try { Thread.sleep(2000); }
            catch (InterruptedException e) {}

            job.refresh();
        }
        if (status) System.out.println("");

        InputStream stream = null;

        Args outputArgs = new Args();
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

        job.cancel();
    }
}
