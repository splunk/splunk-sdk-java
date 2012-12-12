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

package com.splunk;

import java.io.BufferedReader;
import java.io.File;
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
    private String appName;
    private Options rules = new Options();

    // The parsed command line arguments
    public String[] args = new String[0];

    // The parsed command line options (flags)
    public HashMap<String, Object> opts = new HashMap<String, Object>();

    // Whether or not this is a help request
    public Boolean help = false;

    public static final HashMap<String, Object> defaultValues = new HashMap<String, Object>();
    {
        defaultValues.put("scheme", "https");
        defaultValues.put("host", "localhost");
        defaultValues.put("port", 8089);
    }
    
    Command(String appName) { 
        this.appName = appName;
    }

    public static Command create() {
        return create(null);
    }

    public static Command create(String appName) {
        return new Command(appName);
    }

    public static void error(String message, Object... args) {
        System.err.format("Error: %s\n", String.format(message, args));
        System.exit(2);
    }

    public Options getRules() {
        return this.rules;
    }

    // Initialize with default Splunk command options.
    @SuppressWarnings("static-access")  // OptionBuilder API requires this
    public Command init() {
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
        rules.addOption(null, "app", true, "App/namespace context");
        rules.addOption(null, "owner", true, "Owner/user context");
        // This is here only for compatibility with the JavaScript SDK's .splunkrc.
        rules.addOption(null, "version", true, "Version (irrelevant for Java)");
        return this;
    }

    public Command addRule(String name, String description) {
        rules.addOption(null, name, false, description);
        return this;
    }

    @SuppressWarnings("static-access")  // OptionBuilder API requires this
    public Command addRule(String name, Class argType, String description) {
        rules.addOption(
            OptionBuilder
                .withLongOpt(name)
                .hasArg(true)
                .withType(argType)
                .withDescription(description)
                .create());
        return this;
    }

    // Load a file of options and arguments
    public Command load(String path) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(path);
        }
        catch (FileNotFoundException e) { return this; }

        ArrayList<String> argList = new ArrayList<String>(4);
        BufferedReader reader = new BufferedReader(fileReader);
        while (true) {
            String line;
            try {
                line = reader.readLine();
            }
            catch (IOException e) { return this; }
            if (line == null) break;
            if (line.startsWith("#")) continue;
            line = line.trim();
            if (line.length() == 0) continue;
            if (!line.startsWith("-"))
                line = "--" + line;
            argList.add(line);
        }
        parse(argList.toArray(new String[argList.size()]));
        return this;
    }

    // Parse the given argument vector
    public Command parse(String[] argv) {
        CommandLineParser parser = new PosixParser();

        CommandLine cmdline = null;
        try {
            cmdline = parser.parse(this.rules, argv);
        }
        catch (ParseException e) {
            error(e.getMessage());
        }

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
                } 
                else if (type == Integer.class) {
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
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        String[] orig = this.args;
        String[] more = cmdline.getArgs();
        this.args = new String[orig.length + more.length];
        System.arraycopy(orig, 0, this.args, 0, orig.length);
        System.arraycopy(more, 0, this.args, orig.length, more.length);

        if (this.help) {
            printHelp();
            System.exit(0);
        }

        return this;
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        String appName = this.appName == null ? "App" : this.appName;
        formatter.printHelp(appName, this.rules);
    }

    public static Command splunk() {
        return splunk(null);
    }

    // Creates a command instance, initializes with the default Splunk
    // command line rules and attempts to load the default options file.
    public static Command splunk(String appName) {
        return Command.create(appName).init().splunkrc();
    }

    // Load the default options file (.splunkrc) if it exists
    public Command splunkrc() {
        this.opts.putAll(defaultValues);
        load(System.getProperty("user.home") + File.separator + ".splunkrc");
        return this;
    }
}

