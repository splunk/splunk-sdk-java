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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Program {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h",  "help", false, "Display this help message");
        options.addOption(null, "host", true, "Host name (default localhost)");
        options.addOption(null, "port", true, "Port number (default 8089)");
        options.addOption(null, "scheme", true, "Scheme (default https)");
        options.addOption(null, "username", true, "Username to login with");
        options.addOption(null, "password", true, "Password to login with");
        options.addOption(null, "namespace", true, null);

        CommandLineParser parser = new PosixParser();

        CommandLine cmdline = null;
        try {
            cmdline = parser.parse(options, args);
        }
        catch (ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        if (cmdline.hasOption('h')) {
            help("spurl [options] {path}", options);
            return;
        }

        try {
            run(cmdline);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static void run(CommandLine cmdline) throws Exception {
        String host = cmdline.getOptionValue("host", "localhost");
        String port = cmdline.getOptionValue("port", "8089");
        String scheme = cmdline.getOptionValue("scheme", "https");

        // UNDONE: Don't default credentials
        String username = cmdline.getOptionValue("username", "admin");
        String password = cmdline.getOptionValue("password", "changeme");

        Service service = new Service(host, Integer.parseInt(port), scheme);
        service.login(username, password);

        String[] args = cmdline.getArgs();
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
