/*
 * Copyright 2012 Splunk, Inc.
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


import java.util.HashMap;

/**
 * Class to contain the values extracted from .splunkrc file.
 */
public class ConnectionArgs extends HashMap<String, Object> {
    public String scheme = "https";
    public String host = "localhost";
    public int port = 8089;
    public String username = null;
    public String password = null;
    public String app = null;
    public String owner = null;

    public void setField(String key, String value)
            throws UnknownSplunkrcKeyException, InvalidUrlSchemeException {
        if (key.equals("scheme")) {
            if (value.equalsIgnoreCase("http") || value.equalsIgnoreCase("https")) {
                put("scheme", value);
            } else {
                throw new InvalidUrlSchemeException(value);
            }
        } else if (key.equals("host") ||
                   key.equals("username") ||
                   key.equals("password") ||
                   key.equals("app") ||
                   key.equals("owner")) {
            put(key, value);
        } else if (key.equals("port")) {
            try {
                this.port = Integer.parseInt(value);
            } catch (Exception e) {
                throw new IllegalArgumentException("Port must be an integer, found: " + value);
            }
        } else {
            throw new UnknownSplunkrcKeyException(value);
        }
    }

    public void handleLine(String line)
            throws MalformedSplunkrcLineException, UnknownSplunkrcKeyException, InvalidUrlSchemeException {
        assert line != null;
        String trimmedLine = line.trim();
        if (trimmedLine.startsWith("#") || trimmedLine.length() == 0) {
            return; // Skip comments and blank lines
        }
        String[] pair = trimmedLine.split("=", 2);
        if (pair.length != 2) {
            throw new MalformedSplunkrcLineException(line);
        } else {
            String key = pair[0];
            String value = pair[1];
            setField(key, value);
        }
    }

    /**
     * Is the state of the args valid?
     */
    public boolean isValid() {
        return scheme != null &&
                host != null &&
                username != null &&
                password != null &&
                app != null &&
                owner != null;
    }
}