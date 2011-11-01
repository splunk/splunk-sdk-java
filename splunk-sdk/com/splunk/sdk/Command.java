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

// UNDONE: Support for other builtin types?
// UNDONE: Automatically build rules by reflecting over option fields.

package com.splunk.sdk;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

// Processes and capture command options and arguments
public class Command {
    Options rules = new Options();

    public String[] args = new String[0];
    public HashMap<String, Object> opts = new HashMap<String, Object>();

    // Option fields
    public Boolean help = false;
    public String host = "localhost";
    public int port = 8089;
    public String scheme = "https";
    public String username = null;
    public String password = null;
    public String namespace = null;

    public Command() {
        rules.addOption("h",  "help", false, "Display this help message");
        rules.addOption(null, "host", true, "Host name (default localhost)");
        rules.addOption(OptionBuilder
            .withLongOpt("port")
            .hasArg(true)
            .withType(Integer.class)
            .create());
        rules.addOption(null, "scheme", true, "Scheme (default https)");
        rules.addOption(null, "username", true, "Username to login with");
        rules.addOption(null, "password", true, "Password to login with");
        rules.addOption(null, "namespace", true, null);
    }

    public Options getRules() {
        return this.rules;
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
                line = "--" + line.trim();
            argList.add(line);
        }
        parse(argList.toArray(new String[argList.size()]));
    }

    // Parse the given argument vector
    public void parse(String[] argv) throws ParseException {
        CommandLineParser parser = new PosixParser();

        CommandLine cmdline = parser.parse(this.rules, argv);

        // Unpack the cmdline into a simple Map of options and optionally
        // assign values to any corresponding fields found in the Command class.
        for (Option option : cmdline.getOptions()) {
            String name = option.getLongOpt();
            Object value = option.getValue();

            // Figure out the type of the option and convert the value.
            if (!option.hasArg()) {
                // If it has no arg, then its implicitly boolean and presence
                // of the argument indicates truth.
                value = true;
            }
            else {
                Class type = (Class)option.getType();
                if (type == null) {
                    // Null implies String, no conversion necessary
                } else if (type == Integer.class) {
                    value = Integer.parseInt((String)value);
                }
                else {
                    assert false; // Unsupported type
                }
            }

            this.opts.put(name, value);

            // Look for a field of the Command class (or subclass) that
            // matches the long name of the option and, if found, assign the
            // corresponding option value in order to provide simplified
            // access to command options.
            try {
                Field field = this.getClass().getField(name);
                field.set(this, value);
            }
            catch (NoSuchFieldException e) { continue; }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        String[] orig = this.args;
        String[] more = cmdline.getArgs();
        this.args = new String[orig.length + more.length];
        System.arraycopy(orig, 0, this.args, 0, orig.length);
        System.arraycopy(more, 0, this.args, orig.length, more.length);
    }

    public void printHelp(String app) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(app, this.rules);
    }
}

