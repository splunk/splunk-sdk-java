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

import com.splunk.Service;
import com.splunk.http.RequestMessage;
import com.splunk.http.ResponseMessage;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

public class Program {
    public static void main(String[] args) {
        Command command = new Command();

        try {
            // Load default options from the .splunkrc file
            String home = System.getProperty("user.home");
            String path = home + File.separator + ".splunkrc";
            command.load(path);

            // Parse command line arguments
            command.parse(args);
        }
        catch (ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        try {
            run(command);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static void run(Command command) throws Exception {
        String host = "localhost";
        String port = "8089";
        String scheme = "https";

        if (command.opts.containsKey("host"))
            host = command.opts.get("host");
        if (command.opts.containsKey("port"))
            port = command.opts.get("port");
        if (command.opts.containsKey("scheme"))
            scheme = command.opts.get("scheme");

        String username = command.opts.get("username");
        String password = command.opts.get("password");

        Service service = new Service(host, Integer.parseInt(port), scheme);
        service.login(username, password);

        String[] args = command.args.toArray(new String[command.args.size()]);
        String path = args.length > 0 ? args[0] : "/";
        ResponseMessage response =
            service.send(new RequestMessage("GET", path));

        int status = response.getStatus();
        System.out.println(String.format("=> %d", status));
        if (status != 200) return;
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(response.getContent()));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            System.out.println(line);
        }
    }

    static void help(String app, Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(app, options);
    }
}
