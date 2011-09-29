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

package com.splunk.sdk;

import java.io.File;
import org.apache.commons.cli.ParseException;

// Abstract base class that coordinates common program initialization,
// including loading of default options from .splunkrc file and processing
// of command line arguments.
public abstract class Program extends Command {
    // Load default options and process command line.
    public Program init(String[] args) {
        try {
            // Load default options from the .splunkrc file
            String home = System.getProperty("user.home");
            load(home + File.separator + ".splunkrc");

            // Parse command line arguments
            parse(args);
        }
        catch (ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        if (this.help) {
            printHelp("spurl");
            System.exit(0);
        }

        return this;
    }

    public abstract void run() throws Exception;
}
