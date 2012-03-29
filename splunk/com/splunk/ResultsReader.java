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

import au.com.bytecode.opencsv.CSVReader;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;

public class ResultsReader {
    String streamType;
    String csvKeys[];
    InputStreamReader inputStreamReader = null;
    CSVReader csvReader = null;
    JsonReader jsonReader = null;

    /**
     * Class constructor.
     *
     * @param inputStream The input stream (unread) return stream from a splunk
     * query or export.
     * @param streamType Specifies the input stream's format. Valid values are
     * {@code csv, xml,} or {@code json}.
     *
     * @throws IOException If an IO exception occurs.
     */
    public ResultsReader(InputStream inputStream, String streamType)
            throws IOException {
        try {
            inputStreamReader = new
                    InputStreamReader(inputStream, "UTF-8");
        }
        catch (UnsupportedEncodingException e) { assert false; }

        if (streamType.equals("csv")) {
            csvReader = new CSVReader(inputStreamReader);
            csvKeys = csvReader.readNext();
        } else if (streamType.equals("json")) {
            jsonReader = new JsonReader(inputStreamReader);
            jsonReader.beginArray();
        } else if (streamType.equals("xml")) {

        } else {
            throw new RuntimeException("streamType must be xml, json or xml");
        }
        this.streamType = streamType;
    }

    public HashMap<String, String> getNextEvent() throws IOException {
        HashMap<String, String> data = new HashMap<String, String>();

        if (streamType.equals("csv")) {
            int index = 0;

            String [] nextLine = csvReader.readNext();
            if (nextLine == null) return null;

            for (String key: csvKeys) {
                data.put(key, nextLine[index++]);
            }
        } else if (streamType.equals("json")) {
            if (jsonReader.peek() == JsonToken.END_ARRAY) {
                return null;
            }
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                data.put(jsonReader.nextName(), jsonReader.nextString());
            }
            if (jsonReader.peek() == JsonToken.END_OBJECT) {
                jsonReader.endObject();
            }
        } else {

        }

        return data;
    }

    public void close() throws IOException {
        if (streamType.equals("csv")) {
            csvReader.close();
            csvReader = null;
        } else if (streamType.equals("json")) {
            jsonReader.close();
            jsonReader = null;
        } else {

        }

        inputStreamReader.close();
        inputStreamReader = null;
    }
}
