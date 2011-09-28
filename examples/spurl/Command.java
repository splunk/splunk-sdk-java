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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Processes and captures command options and arguments
 public class Command {
    Options options = new Options();

    public ArrayList<String> args = new ArrayList<String>();
    public HashMap<String, String> opts = new HashMap<String, String>();

    public Command() {
        this.options.addOption("h",  "help", false, "Display this help message");
        this.options.addOption(null, "host", true, "Host name (default localhost)");
        this.options.addOption(null, "port", true, "Port number (default 8089)");
        this.options.addOption(null, "scheme", true, "Scheme (default https)");
        this.options.addOption(null, "username", true, "Username to login with");
        this.options.addOption(null, "password", true, "Password to login with");
        this.options.addOption(null, "namespace", true, null);
    }

    public Options getOptions() {
        return this.options;
    }

    // Load a file of options and arguments
    public void load(String path) throws ParseException {
        FileReader fileReader;
        try {
           fileReader = new FileReader(path);
        }
        catch (FileNotFoundException e) { return; }

        ArrayList<String> argList = new ArrayList<String>(4);
        BufferedReader reader = new BufferedReader(fileReader);
        while (true) {
            String line;
            try {
                line = reader.readLine();
            }
            catch (IOException e) { return; }
            if (line == null) break;
            if (line.startsWith("#")) continue;
            if (!line.startsWith("-"))
                line = "--" + line;
            argList.add(line);
        }
        parse(argList.toArray(new String[argList.size()]));
    }

    // Parse the given argument vector
    void parse(String[] args) throws ParseException {
        CommandLineParser parser = new PosixParser();

        CommandLine cmdline = parser.parse(this.options, args);

        // Unpack the cmdline into a simple Map of options
        for (Option option : cmdline.getOptions()) {
            String value = option.getValue();
            if (value == null) continue;
            this.opts.put(option.getLongOpt(), value);
        }

        for (String item : cmdline.getArgs())
            this.args.add(item);
    }
}
