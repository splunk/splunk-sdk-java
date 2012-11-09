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

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.splunk.ResultsReader;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ResultsReaderJson extends ResultsReader {
    private JsonReader jsonReader = null;

    /**
     * Class Constructor.
     *
     * Construct a streaming JSON reader for the event stream. One should only
     * attempt to parse a JSON stream with the JSON reader. Using a non-JSON
     * stream will yield unpredictable results.
     *
     * @param inputStream The stream to be parsed.
     * @throws Exception On exception.
     */
    public ResultsReaderJson(InputStream inputStream) throws Exception {
        super(inputStream);
        jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF8"));
        // if stream is empty, return a null reader.
        try {
            if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                // In Splunk 5.0, JSON output is an object, and the results are
                // an array at that object's key "results". In Splunk 4.3, the
                // array was the top level returned. So if we find an object
                // at top level, we step into it until we find the right key,
                // then leave it in that state to iterate over.
                jsonReader.beginObject();

                String key;
                while (true) {
                    key = jsonReader.nextName();
                    if (key.equals("results")) {
                        jsonReader.beginArray();
                        return;
                    } else {
                        skipEntity();
                    }
                }
            } else { // We're on Splunk 4.x, and we just need to start the array.
                jsonReader.beginArray();
                return;
            }
        } catch (EOFException e) {
            jsonReader = null;
            return;
        }
    }

    /**
     * Skip the next value, whether it is atomic or compound, in the JSON
     * stream.
     */
    private void skipEntity() throws IOException {
        if (jsonReader.peek() == JsonToken.STRING) {
            jsonReader.nextString();
        } else if (jsonReader.peek() == JsonToken.BOOLEAN) {
            jsonReader.nextBoolean();
        } else if (jsonReader.peek() == JsonToken.NUMBER) {
            jsonReader.nextDouble();
        } else if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
        } else if (jsonReader.peek() == JsonToken.NAME) {
            jsonReader.nextName();
        } else if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
            jsonReader.beginArray();
            while (jsonReader.peek() != JsonToken.END_ARRAY) {
                skipEntity();
            }
            jsonReader.endArray();
        } else if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
            jsonReader.beginObject();
            while (jsonReader.peek() != JsonToken.END_OBJECT) {
                skipEntity();
            }
            jsonReader.endObject();
        }
    }

    /** {@inheritDoc} */
    @Override public void close() throws Exception {
        super.close();
        if (jsonReader != null)
            jsonReader.close();
        jsonReader = null;
    }

    /** {@inheritDoc} */
    @Override public HashMap<String, String> getNextEvent() throws Exception {
        HashMap<String, String> returnData = null;
        String name = "";

        if (jsonReader == null)
            return null;

        // Events are almost flat, so no need for a true general parser
        // solution. But the Gson parser is a little unintuitive here. Nested
        // objects, have their own relative notion of hasNext. This
        // means that for every object or array start, hasNext() returns false
        // and one must consume the closing (END) object to get back to the
        // previous object.
        while (jsonReader.hasNext()) {
            if (returnData == null) {
                returnData = new HashMap<String, String>();
            }
            if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                jsonReader.beginObject();
            }
            if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                // The Gson parser is a little unintuitive here. Nested objects,
                // have their own relative notion of hasNext; when hasNext()
                // is done, it is only for this array.
                String data = "";
                while (jsonReader.hasNext()) {
                    JsonToken jsonToken2 = jsonReader.peek();
                    if (jsonToken2 == JsonToken.STRING) {
                        data = data + (data.equals("") ? "" : ",") +
                                jsonReader.nextString();
                    }
                }
                jsonReader.endArray();
                returnData.put(name, data);
            }
            if (jsonReader.peek() == JsonToken.NAME) {
                name = jsonReader.nextName();
            }
            if (jsonReader.peek() == JsonToken.STRING) {
                returnData.put(name, jsonReader.nextString());
            }
            if (jsonReader.peek() == JsonToken.END_OBJECT) {
                jsonReader.endObject();
                break;
            }
            if (jsonReader.peek() == JsonToken.END_ARRAY) {
                jsonReader.endArray();
            }
        }
        return returnData;
    }
}
