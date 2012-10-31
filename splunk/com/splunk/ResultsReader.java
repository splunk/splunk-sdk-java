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

import java.io.*;
import java.util.HashMap;
/**
 * The {@code ResultsReader} class represents a streaming reader for Splunk 
 * search results.
 */
public abstract  class ResultsReader {
    InputStreamReader inputStreamReader = null;

    /**
     * Class constructor.
     *
     * @param inputStream The unread return input stream from a Splunk query or 
     * export.
     * @throws IOException If an IO exception occurs.
     */
    public ResultsReader(InputStream inputStream) throws Exception {
        try {
            inputStreamReader = new
                InputStreamReader(inputStream, "UTF-8");
        }
        catch (UnsupportedEncodingException e) { assert false; }
    }

    /**
     * Closes the reader and returns resources.
     *
     * @throws Exception on Exception
     */
    public void close() throws Exception {
        if (inputStreamReader != null)
            inputStreamReader.close();
        inputStreamReader = null;
    }

    /**
     * Returns the next event in the event stream.
     *
     * @return The hash map of key-value pairs for an entire event.
     * @throws Exception on Exception.
     */
    public HashMap<String, String> getNextEvent() throws Exception {
        return null;
    }
}
