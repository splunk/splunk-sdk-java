package com.splunk.sdk;

/**
 * Context
 * version 1.0
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Maintains a local splunk Context for communicating to and from the splunkd server
 */
public class Context {

    // contextData is the local store of information that defines the splunk context
    private final HashMap<String, String> contextData = new HashMap<String, String>();

    /**
     * processes a line of the form: key=value or key value primarily for
     * the splunkrc file -- which contains the splunk Context information for initial
     * connections and subsequent communications.
     *
     * @param str String to be processed
     * @return 0 for success, -1 via setContextValue if key not allowed in set, -2 for bad tokenization in file
     */
    private int process(String str) {
        StringTokenizer st = new StringTokenizer(str, " =", false); // spaces and equal signs, skip tokens themselves
        if (st.countTokens() != 2)
            return -2; // bad tokenization

        String key = st.nextToken();
        String value = st.nextToken();

        // update the Context ontext key/value pair overwriting whatever is there
        return setContextValue(key, value);
    }

    /**
     * establish default key/value pairs for splunks' Context
     */
    public void contextDefaults() {
        /*
         * defaults that can be overwritten by the .splunkrc file, or through explicit
         * parameters to the login() method in Binding.java
         */
        setContextValue("host", "127.0.0.1");
        setContextValue("port", "8089");
        setContextValue("username", "admin");
        setContextValue("password", "none");
        setContextValue("scheme", "https");

        // these are defaults that will get filled in at runtime
        setContextValue("sessionKey", "");
        setContextValue("namespace", "");
    }

    /**
     * initialize splunk Context
     *
     * @throws SplunkException
     */
    public void initSplunkContext() {
        BufferedReader in = null;
        contextDefaults();

        try {
            /*
             * try SPLUNK_SDK, HOME or current directory for .splunkrc
             * first found file gets used, whether it parses or not
             */

            int count = 0;
            while (count < 3) {
                try {
                    String directory;
                    switch (count) {
                        case 0:
                            directory = System.getenv("SPLUNK_SDK");
                            break;
                        case 1:
                            directory = System.getenv("HOME");
                            break;
                        case 2:
                            directory = System.getProperty("user.dir");
                            break;
                        default:
                            throw new SplunkException("Failed to find .splunkrc");
                    }
                    count = count + 1;
                    if (directory != null) {
                        if (!directory.endsWith(System.getProperty("file.separator")))
                            directory = directory + System.getProperty("file.separator");

                        in = new BufferedReader(new FileReader(directory + ".splunkrc"));

                        // found
                        break;
                    }
                } catch (IOException e) {
                    // failed to open try next
                    count = count + 1;
                }
            }

            String str;
            if (in != null) {
                while ((str = in.readLine()) != null) {
                    int retval = process(str);
                    if (retval == -1) {
                        throw new SplunkException("No such predefined key for splunk splunkContext found in .splunkrc");
                    }
                    if (retval == -2) {
                        throw new SplunkException("Bad tokenization found in .splunkrc");
                    }
                }
                in.close();
                return;
            }
        } catch (IOException e) {
            throw new SplunkException("IO splunkException reading .splunkrc");
        }

        throw new SplunkException("Credentials not loaded, .splunkrc file not found (looks in SPLUNK_SDK, HOME and current working directory)");
    }

    /**
     * extract a value from the local splunkContext given a specific key.
     *
     * @param key Key string to look up
     * @return String of value in splunkContext, or a string with ERROR NO SUCH KEY: [key request]
     */
    public String getContextValue(String key) {
        if (contextData.containsKey(key))
            return contextData.get(key);

        return "<ERROR NO SUCH KEY: " + key + ">";
    }

    /**
     * set a value from the local splunkContext given a specific key.
     *
     * @param key   Key string to set
     * @param value Value to set
     * @return 0 on success, -1 if key not in allowed set of keys to set
     */
    public int setContextValue(String key, String value) {
        // restrict the keys to the following list (as opposed to an open k/v pairing)
        String KEYS = "host port username password namespace scheme sessionKey";

        if (KEYS.contains(key)) {
            contextData.put(key, value);
            return 0;
        } else
            return -1; // key not found in our list of KEYS
    }

}
