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
import com.splunk.http.ResponseMessage;
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
 * Export.java: export an splunk entire index in XML. The return data is in
 * strict descending time order.
 */

// UNDONE: In recovery mode, we assume we can find a viable start time in the
// last 'bufferSize' of the index export. Either make a larger buffer
// (not perfect), or chunk through the data backwards until we find one, and
// then read from there to the end of the file.
//
// also: in recover mode, we will duplicate messages and meta data; however,
// this is not necessarily incorrect, just redundant information.

public class Program {

    static public void main(String[] args) {
        try {
            run(args);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static int getLastEventOffset(int indexTimeOffset, byte [] buffer) {
        String str = new String(buffer);
        String pattern = "<result offset=";
        int curr = str.indexOf(pattern);
        int last = 0;
        while ((curr < indexTimeOffset) && curr > 0)  {
            last = curr;
            curr = curr + pattern.length();
            curr = str.indexOf(pattern, curr);
        }
        return last;
    }

    static int getLastKnownGoodEventTime(
        File file, byte [] buffer) throws Exception {

        final int bufferSize = (64*1024);
        int bytesToRead = (int)Math.min(file.length(), bufferSize);
        RandomAccessFile raf = new RandomAccessFile(file, "r");

        // read end of file
        raf.seek(Math.max(0, file.length() - bufferSize));
        raf.read(buffer, 0, bytesToRead);
        String str = new String(buffer);

        int count = -1;
        int start = 0;
        int lastTime = 0;
        int [] times = new int[128];
        int [] offsets = new int[128];
        String opattern = "<field k='_indextime'>";
        String ipattern = "<value><text>";

        int index = str.indexOf(opattern, start);
        while (index > 0) {
            int vstart = str.indexOf(
                ipattern, index + opattern.length()) + ipattern.length();
            int vend = str.indexOf("<", vstart);
            if ((vstart == -1) || (vend == -1)) break;
            times[++count] = Integer.parseInt(str.substring(vstart, vend));
            offsets[count] = vstart;
            lastTime = times[count];
            start = vend;
            index = str.indexOf(opattern, start);
        }

        for (;count > 0; count--) {
            if (times[count] != lastTime) {
                return offsets[count+1];
            }
        }

        throw new Error("failed to find adequate recovery time");
    }

    static String getStartTime(int startTimeBufferOffset, byte [] buffer) {
        String str = new String(buffer);
        return str.substring(
            startTimeBufferOffset, str.indexOf("<", startTimeBufferOffset));
    }

    static void run(String[] argv) throws Exception {
        Command command = Command.splunk("export").parse(argv);
        Service service = Service.connect(command.opts);

        Args args = new Args();
        final String outFilename = "export.out";
        boolean recover = false;
        final int bufferSize = (64*1024);
        byte [] buffer = new byte[bufferSize];

        // This example takes optional arguments:
        //
        // index-name [recover]

        if (command.args.length == 0)
            throw new Error("Index-name required");

        if (command.args.length > 1) {
            for (int index=1; index < command.args.length; index++) {
                if (command.args[index].equals("recover"))
                    recover = true;
                else
                    throw new Error("Unknown option: " + command.args[index]);
            }
        }

        File file = new File(outFilename);
        if (file.exists() && file.isFile() && !recover)
            throw new Error("Export file exists, and no recover option");

        if (recover && file.exists() && file.isFile()) {
            // get last known good event time, truncate file at end of event
            int startTimeBufOffset = getLastKnownGoodEventTime(file, buffer);
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            FileChannel fc = raf.getChannel();
            long eof = Math.max(0, file.length() - bufferSize)
                       + getLastEventOffset(startTimeBufOffset, buffer);
            fc.truncate(eof);
            args.put("latest_time", getStartTime(startTimeBufOffset, buffer));
        } else {
            if (!file.createNewFile())
                throw new Error("Failed to create output file");
        }

        // search args
        args.put("search", String.format("search index=%s *", command.args[0]));
        args.put("earliest_time", "0"); // all the way to the beginning
        args.put("timeout", "60");      // don't keep search around

        ResponseMessage resp =
            service.get("/servicesNS/admin/search/search/jobs/export", args);
        InputStream is = resp.getContent();

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
