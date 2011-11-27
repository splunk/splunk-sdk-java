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

import com.splunk.*;
import com.splunk.sdk.Command;

import java.nio.channels.FileChannel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;

/**
 * Export.java: export an splunk entire index in XML, CSV or JSON (4.3+). The
 * return data is in strict descending time order.
 */

// in recover mode, we will duplicate messages and meta data; however,
// this is not necessarily incorrect, just redundant information.

public class Program {

    static String lastTime;

    static public void main(String[] args) {
        try {
            run(args);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static int getStartNextEvent(
            int indexTimeOffset, String str, String pattern) {
        int curr = str.indexOf(pattern);
        int last = 0;
        while ((curr < indexTimeOffset) && curr > 0)  {
            last = curr;
            curr = curr + pattern.length();
            curr = str.indexOf(pattern, curr);
        }
        return last;
    }

    static int getTruncatePoint(
        int indexTimeOffset, byte[] buffer, String format) {
        String str = new String(buffer);

        if (format.equals("csv"))
            // UNDONE: does this work with line breaks?
            return getStartNextEvent(indexTimeOffset, str, "\n") + 1;
        else if (format.equals("xml"))
            return getStartNextEvent(indexTimeOffset, str, "<result offset=");
        else
            return getStartNextEvent(indexTimeOffset, str, "{\"_cd\":");
    }

    static int getCsvEventTimeOffset(String str) {
        String [] times = new String[1024];
        int [] offsets = new int[1024];
        int count = -1;
        int index = str.indexOf("\n");

        while (index > 0) {
            int next =  str.indexOf("\n", index + 1);
            if (next > 0) {
                String timePart = str.substring(index, next).split(",")[1];
                times[++count] = timePart.substring(1, timePart.length()-1);
                offsets[count] = str.indexOf(timePart, index);
                lastTime = times[count];
            }
            index = next + 1;
        }

        for (;count > 0; count--) {
            if (!times[count].equals(lastTime)) {
                lastTime = times[count];
                return offsets[count+1];
            }
        }
        return -1;
    }

    static int getXmlEventTimeOffset(String str) {
        int count = -1;
        int start = 0;
        String [] times = new String[1024];
        int [] offsets = new int[1024];
        String timePattern = "<field k='_time'>";
        String timeStartPattern = "<value><text>";
        String timeEndPattern = "<";

        int index = str.indexOf(timePattern, start);
        while (index > 0) {
            int timeStart = str.indexOf(
                timeStartPattern, index + timePattern.length())
                + timeStartPattern.length();
            int timeEnd = str.indexOf(timeEndPattern, timeStart);
            if ((timeStart == -1) || (timeEnd == -1)) break;
            times[++count] = str.substring(timeStart, timeEnd);
            offsets[count] = timeStart;
            lastTime = times[count];
            start = timeEnd;
            index = str.indexOf(timePattern, start);
        }

        for (;count > 0; count--) {
            if (!times[count].equals(lastTime)) {
                lastTime = times[count];
                return offsets[count+1];
            }
        }
        return -1;
    }

    static int getJsonEventTimeOffset(String str) {
        int count = -1;
        int start = 0;
        String [] times = new String[1024];
        int [] offsets = new int[1024];
        String timePattern = "\"_time\":\"";
        String timeStartPattern = "\":\"";
        String timeEndPattern = "\",";

        int index = str.indexOf(timePattern, start);
        while (index > 0) {
            int timeStart = str.indexOf(timeStartPattern, index)
                            + timeStartPattern.length();
            int timeEnd = str.indexOf(timeEndPattern, timeStart);
            if ((timeStart == -1) || (timeEnd == -1)) break;
            times[++count] = str.substring(timeStart, timeEnd);
            offsets[count] = timeStart;
            lastTime = times[count];
            start = timeEnd;
            index = str.indexOf(timePattern, start);
        }

        for (;count > 0; count--) {
            if (!times[count].equals(lastTime)) {
                lastTime = times[count];
                return offsets[count+1];
            }
        }
        return -1;
    }

    static int getLastGoodEventOffset(byte[] buffer, String format)
        throws Exception {

        String str = new String(buffer);
        if (format.equals("csv"))
            return getCsvEventTimeOffset(str);
        else if (format.equals("xml"))
            return getXmlEventTimeOffset(str);
        else
            return getJsonEventTimeOffset(str);
    }

    static void run(String[] argv) throws Exception {
        Command command = Command.splunk("export").parse(argv);
        Service service = Service.connect(command.opts);

        Args args = new Args();
        final String outFilename = "export.out";
        boolean recover = false;
        String format = "csv"; // default to csv

        // This example takes optional arguments:
        //
        // index-name [recover] [csv|xml|json]
        //
        // N.B. json output only valid with 4.3+

        if (command.args.length == 0)
            throw new Error("Index-name required");

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
            // chunk backwards through the file until we find valid
            // start time. If we can't find one just start over.
            final int bufferSize = (64*1024);
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            long fptr = Math.max(file.length() - bufferSize, 0);
            long fptrEof = 0;

            while (fptr > 0) {
                byte [] buffer = new byte[bufferSize];
                raf.seek(fptr);
                raf.read(buffer, 0, bufferSize);
                int eventTimeOffset = getLastGoodEventOffset(buffer, format);
                if (eventTimeOffset != -1) {
                    // UNDONE: if we had to crawl back more than one buffer,
                    // AND the end of event is not in this buffer, we
                    // need to walk forward until found.
                    fptrEof = getTruncatePoint(eventTimeOffset, buffer, format)
                        + fptr;
                    break;
                }
                fptr = fptr - bufferSize;
            }

            if (fptr < 0)
                fptrEof = 0; // didn't find a valid event, so start over.
            else
                args.put("latest_time", lastTime);

            FileChannel fc = raf.getChannel();
            fc.truncate(fptrEof);
        } else
            if (!file.createNewFile())
                throw new Error("Failed to create output file");

        // search args
        args.put("timeout", "60");          // don't keep search around
        args.put("output_mode", format);    // output in specific format
        args.put("ealiest_time", "0.000");  // always to beginning of index
        args.put("time_format", "%s.%Q");   // epoch time plus fraction
        String search = String.format("search index=%s *", command.args[0]);

        //System.out.println("search: " + search + ", args: " + args);
        InputStream is = service.export(search, args);

        // use UTF8 sensitive reader/writers
        InputStreamReader isr = new InputStreamReader(is, "UTF8");
        FileOutputStream os = new FileOutputStream(file, true);
        Writer out = new OutputStreamWriter(os, "UTF8");

        // read/write 8k at a time if possible
        char [] xferBuffer = new char[8192];

        while (true) {
            int bytesRead = isr.read(xferBuffer);
            if (bytesRead == -1) break;
            out.write(xferBuffer, 0, bytesRead);
        }

        isr.close();
        out.close();
    }
}
