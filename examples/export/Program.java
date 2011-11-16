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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

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

    static void run(String[] argv) throws Exception {
        Command command = Command.splunk("export").parse(argv);
        Service service = Service.connect(command.opts);

        final String outFilename = "export.out";
        Args args = new Args();
        boolean recover = false;

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

        System.out.println("recover: " + recover);
        File file = new File(outFilename);
        if (file.exists() && file.isFile() && !recover)
            throw new Error("Export file exists, and no recover option");

        if (recover) {
            //UNDONE: when recovering from a halted or interrupted export:
            // 1) read to end of file, find last time stamp.
            // 2) walk backwards until time stamp changes.
            // 3) truncate file, leave filepointer at EOF.
            // 4) args.put("latest_time", <this time>);
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
        FileOutputStream ostream = new FileOutputStream(file);
        Writer out = new OutputStreamWriter(ostream, "UTF8");

        // UNDONE: perhaps a better mechanism to read/write than byte by byte.
        int b;
        while ((b = isr.read()) != -1) {
            out.append((char)b);
        }

        isr.close();
        out.close();
    }
}
