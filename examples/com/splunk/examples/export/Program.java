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

package com.splunk.examples.export;

import com.splunk.*;

import java.nio.channels.FileChannel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Export.java: export an splunk entire index in XML, CSV or JSON (4.3+). The
 * return data is in strict descending time order.
 */

// In recover mode, we will duplicate messages and meta data; however,
// this is not necessarily incorrect, just redundant information.

public class Program {

    static String lastTime;
    static int nextEventOffset;

    static public void main(String[] args) {
        try {
            run(args);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static Map<String, Integer> getStartNextCSVEvent(int location, String str) {

        Map<String, Integer> pair = new HashMap<String, Integer>();
        pair.put("start", -1);
        pair.put("end", -1);

        int eventStart = str.indexOf("\n", location) + 1;
        int eventEnd = str.indexOf("\"\n", eventStart + 1);

        while (eventEnd > 0) {
            String substring = str.substring(eventStart, eventEnd);
            String [] parts = substring.split(",");
            // Test parts 0 and 1 of the CSV, for <number> and time.qqq stamp
            try {
                Integer.parseInt(parts[0]);
                String timestamp = parts[1].replace("\"","");
                String [] timeparts = timestamp.split("\\.");
                Integer.parseInt(timeparts[0]);
                Integer.parseInt(timeparts[1]);
                pair.put("start", eventStart);
                pair.put("end", eventEnd);
                return pair;
            }
            catch (Exception e) {
                // If any of the fields accessed caused an exception, then
                // we didn't have a valid start of event, so try again.
                eventStart = str.indexOf("\n", eventEnd + 2);
                eventEnd = str.indexOf("\"\n", eventStart + 1);
            }
        }
        return pair;
    }

    static int getCsvEventStart(String str) {

        Map<String, Integer>pair = getStartNextCSVEvent(0, str);
        if (pair.get("start")< 0)
            return -1;

        lastTime = str.substring(pair.get("start"))
                                     .split(",")[1]
                                     .replace("\"","");
        nextEventOffset = pair.get("end");

        // Walk through events until time changes.
        while (pair.get("end") > 0) {
            pair = getStartNextCSVEvent(pair.get("start"), str);
            if (pair.get("end") < 0)
                return -1;
            String time = str.substring(pair.get("start"), pair.get("end"))
                    .split(",")[1]
                    .replace("\"", "");
            if (!time.equals(lastTime)) {
                return pair.get("start");
            }
            nextEventOffset = pair.get("end") + 1;
        }

        return -1;
    }

    static int getXmlEventStart(String str) {
        String resultPattern = "<result offset='";
        String timeKeyPattern = "<field k='_time'>";
        String timeStartPattern = "<value><text>";
        String timeEndPattern = "<";
        String eventEndPattern = "</result>";

        // Get first event in this buffer. If no event end kick back
        int eventStart = str.indexOf(resultPattern);
        int eventEnd = str.indexOf(eventEndPattern, eventStart)
            + eventEndPattern.length();
        if (eventEnd < 0)
            return -1;
        int timeKeyStart = str.indexOf(timeKeyPattern, eventStart);
        int timeStart = str.indexOf(timeStartPattern, timeKeyStart)
                + timeStartPattern.length();
        int timeEnd = str.indexOf(timeEndPattern, timeStart+1);
        lastTime = str.substring(timeStart, timeEnd);

        nextEventOffset = eventEnd;

        // Walk through events until time changes
        eventStart = eventEnd;
        while (eventEnd > 0) {
            eventStart = str.indexOf(resultPattern, eventStart+1);
            eventEnd = str.indexOf(eventEndPattern, eventStart)
                    + eventEndPattern.length();
            if (eventEnd < 0)
                return -1;
            timeKeyStart = str.indexOf(timeKeyPattern, eventStart);
            timeStart = str.indexOf(timeStartPattern, timeKeyStart);
            timeEnd = str.indexOf(timeEndPattern, timeStart);
            String time = str.substring(timeStart, timeEnd);
            if (!time.equals(lastTime)) {
                return eventStart;
            }
            nextEventOffset = eventEnd;
            eventStart = eventEnd;
        }

        return -1;
    }

    static int getJsonEventStart(String str) {

        String timeKeyPattern = "\"_time\":\"";
        String timeEndPattern = "\"";
        String eventEndPattern = "\"},\n";
        String eventEndPattern2 = "\"}[]"; // Old json output format bug

        // Get first event in this buffer. If no event end kick back.
        int eventStart = str.indexOf("{\"_cd\":\"");
        int eventEnd = str.indexOf(eventEndPattern, eventStart)
                + eventEndPattern.length();
        if (eventEnd < 0)
            eventEnd = str.indexOf(eventEndPattern2, eventStart)
                    + eventEndPattern2.length();
        if (eventEnd < 0)
            return -1;

        int timeStart = str.indexOf(timeKeyPattern, eventStart)
                + timeKeyPattern.length();
        int timeEnd = str.indexOf(timeEndPattern, timeStart+1);
        lastTime = str.substring(timeStart, timeEnd);
        nextEventOffset = eventEnd;

        // Walk through events until time changes.
        eventStart = eventEnd;
        while (eventEnd > 0) {
            eventStart = str.indexOf("{\"_cd\":\"", eventStart+1);
            eventEnd = str.indexOf(eventEndPattern, eventStart)
                    + eventEndPattern.length();
            if (eventEnd < 0)
                eventEnd = str.indexOf(eventEndPattern2, eventStart)
                        + eventEndPattern2.length();
            if (eventEnd < 0)
                return -1;

            timeStart = str.indexOf(timeKeyPattern, eventStart)
                    + timeKeyPattern.length();
            timeEnd = str.indexOf(timeEndPattern, timeStart+1);
            String time = str.substring(timeStart, timeEnd);
            if (!time.equals(lastTime)) {
                return eventStart;
            }
            nextEventOffset = eventEnd-2;
            eventStart = eventEnd;
        }

        return -1;
    }

    static int getEventStart(byte[] buffer, String format)
            throws Exception {

        String str = new String(buffer);
        if (format.equals("csv"))
            return getCsvEventStart(str);
        else if (format.equals("xml"))
            return getXmlEventStart(str);
        else
            return getJsonEventStart(str);
    }

    static void cleanupTail(Writer out, String format) throws Exception {
        if (format.equals("csv"))
            out.write("\n");
        else if (format.equals("xml"))
            out.write("\n</results>\n");
        else
            out.write("\n]\n");
    }

    static void run(String[] argv) throws Exception {
        Command command = Command.splunk("export");
        command.addRule("search", String.class, "Search string to export");

        command.parse(argv);
        Service service = Service.connect(command.opts);

        Args args = new Args();
        final String outFilename = "export.out";
        boolean recover = false;
        boolean addEndOfLine = false;
        String format = "csv"; // default to csv

        // This example takes optional arguments:
        //
        // index-name [recover] [csv|xml|json]
        //
        // N.B. json output only valid with 4.3+

        String indexName = null;
        if (command.args.length == 0) {
            System.out.println("Exporting main index");
            indexName = "main";
        } else
            indexName = command.args[0];

        if (command.args.length > 1) {
            for (int index=1; index < command.args.length; index++) {
                if (command.args[index].equals("recover"))
                    recover = true;
                else if (command.args[index].equals("csv"))
                    format = "csv";
                else if (command.args[index].equals("xml"))
                    format = "xml";
                else if (command.args[index].equals("json"))
                    format = "json";
                else
                    throw new Error("Unknown option: " + command.args[index]);
            }
        }

        File file = new File(outFilename);
        if (file.exists() && file.isFile() && !recover)
            throw new Error("Export file exists, and no recover option");

        if (recover && file.exists() && file.isFile()) {
            // Chunk backwards through the file until we find valid
            // start time. If we can't find one just start over.
            final int bufferSize = (64*1024);
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            long fptr = Math.max(file.length() - bufferSize, 0);
            long fptrEof = 0;

            while (fptr > 0) {
                byte [] buffer = new byte[bufferSize];
                raf.seek(fptr);
                raf.read(buffer, 0, bufferSize);
                int eventStart = getEventStart(buffer, format);
                if (eventStart != -1) {
                    fptrEof = nextEventOffset + fptr;
                    break;
                }
                fptr = fptr - bufferSize;
            }

            if (fptr < 0)
                fptrEof = 0; // We didn't find a valid event, so start over.
            else
                args.put("latest_time", lastTime);
                addEndOfLine = true;

            FileChannel fc = raf.getChannel();
            fc.truncate(fptrEof);
        } else
        if (!file.createNewFile())
            throw new Error("Failed to create output file");

        // Search args
        args.put("timeout", "60");          // Don't keep search around
        args.put("output_mode", format);    // Output in specific format
        args.put("earliest_time", "0.000"); // Always to beginning of index
        args.put("time_format", "%s.%Q");   // Epoch time plus fraction
        String search = null;

        if (command.opts.containsKey("search")) {
            search = (String)command.opts.get("search");
        }
        else {
            search = String.format("search index=%s *", indexName);
        }

        InputStream is = service.export(search, args);

        // Use UTF8 sensitive reader/writers
        InputStreamReader isr = new InputStreamReader(is, "UTF8");
        FileOutputStream os = new FileOutputStream(file, true);
        Writer out = new OutputStreamWriter(os, "UTF8");

        // Read/write 8k at a time if possible
        char [] xferBuffer = new char[8192];
        boolean once = true;

        // If superfluous meta-data is not needed, or specifically
        // wants to be removed, one would clean up the first read
        // buffer on a format by format basis,
        while (true) {
            if (addEndOfLine && once) {
                cleanupTail(out, format);
                once = false;
            }
            int bytesRead = isr.read(xferBuffer);
            if (bytesRead == -1) break;
            out.write(xferBuffer, 0, bytesRead);
        }

        isr.close();
        out.close();
    }
}
