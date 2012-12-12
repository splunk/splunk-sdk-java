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

package com.splunk.examples.genevents;

import com.splunk.*;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Generate events into an index using either stream, submit or raw tcp
 * methods.
 */

public class Program {

    static String indexName =
        "Name of index to send events to. If unspecified, 'default' is used";
    static int ingestPort = 9002;
    static String ingestMethod =
        "Ingest events via method {stream, submit, tcp} (default: stream)";
    static String tcpPort =
        String.format("Input port for tcp ingest (default: %d)", ingestPort);
    static SimpleDateFormat dateFormat =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static void main(String[] args) {
        try {
            run(args);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static String makeEvent(String stamp, int i, int j) {
        return String.format("%s: event bunch %d, number %d\n", stamp, i, j);
    }

    static void buildRules(Command command, String[] argsIn) {
        command.addRule("index", String.class, indexName);
        command.addRule("itype", String.class, ingestMethod);
        command.addRule("iport", String.class, tcpPort);
        command.parse(argsIn);
    }

    static void run(String[] argsIn) throws Exception {

        Command command;
        int count;
        Index index = null;
        String ingest;
        String iname;
        List ingestTypes = Arrays.asList("submit", "stream", "tcp");
        OutputStream ostream;
        Receiver receiver = null;
        Service service;
        Socket stream = null;
        Writer writerOut = null;

        command = Command.splunk("genevents");
        buildRules(command, argsIn);
        service = Service.connect(command.opts);

        // Determine ingest method and other input arguments.
        iname = null;
        ingest = "stream";
        if (command.opts.containsKey("index")) {
            iname = (String)command.opts.get("index");
        }
        if (command.opts.containsKey("itype"))
            ingest = (String)command.opts.get("itype");
        if (command.opts.containsKey("iport")) {
            ingestPort = Integer.parseInt((String)command.opts.get("iport"));
        }

        // Validate
        if (!ingestTypes.contains(ingest)) {
            Command.error("Method '"+ingest+"' must be in set: "+ingestTypes);
        }

        if (iname != null) {
            index = service.getIndexes().get(iname);
            if (index == null) {
                Command.error("Index '" + iname + "' was not found.");
            }
        } else {
            receiver = service.getReceiver();
        }


        // For stream and tcp, they both require a socket, though setup
        // slightly differently.
        if (ingest.equals("stream") || ingest.equals("tcp")) {
            if (ingest.equals("stream"))
                try {
                    // A specific index or not?
                    if (iname != null)
                        stream = index.attach();
                    else
                        stream = receiver.attach();
                }
                catch (NullPointerException e) {
                    System.out.println("Failed to attach to index.");
                    System.exit(3);
                }
            else {
                // Create a tcp input if one does not already exist.
                String inputName = String.valueOf(ingestPort);
                TcpInput tcpInput = (TcpInput)service.getInputs().get(inputName);
                if (tcpInput == null) {
                    tcpInput = (TcpInput)service.getInputs().create(
                            inputName, InputKind.Tcp);
                }
                stream = tcpInput.attach();
            }
            ostream = stream.getOutputStream();
            writerOut = new OutputStreamWriter(ostream, "UTF8");
        }

        // Generate 10 batches of 5000 events each.
        count = 0;
        for (int i=0; i<10; i++) {
            for (int j=0; j<5000; j++) {
                Date date = new Date();
                String lastEvent = makeEvent(dateFormat.format(date), i, j);
                if (ingest.equals("stream") || ingest.equals("tcp"))
                    writerOut.write(lastEvent);
                else
                    if (iname != null)
                        index.submit(lastEvent);
                    else
                        receiver.submit(lastEvent);
                count++;
            }
            System.out.println("Submitted "+count+" events, using "+ingest);
        }

        // Flush and close stream on completion
        if (ingest.equals("stream") || ingest.equals("tcp")) {
            writerOut.flush();
            writerOut.close();
            stream.close();
        }
    }
}
