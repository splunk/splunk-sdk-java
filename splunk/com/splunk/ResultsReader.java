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

public abstract  class ResultsReader {
    InputStreamReader inputStreamReader = null;
    Reader reader = null;

    /**
     * Class constructor.
     *
     * @param inputStream The input stream (unread) return stream from a splunk
     * query or export.
     * @throws IOException If an IO exception occurs.
     */
    public ResultsReader(InputStream inputStream) throws Exception {
        try {
            inputStreamReader = new
                    InputStreamReader(inputStream, "UTF-8");
        }
        catch (UnsupportedEncodingException e) { assert false; }
    }

    public HashMap<String, String> getNextEvent() throws Exception {
        return null;
    }

    public void close() throws IOException {
    }
}
