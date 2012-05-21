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

package com.splunk.external;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.splunk.ResultsReader;
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
        jsonReader = new JsonReader(new InputStreamReader(inputStream));
        jsonReader.beginArray();
    }

    /** {@inheritDoc} */
    @Override public void close() throws Exception {
        super.close();
        jsonReader.close();
        jsonReader = null;
    }

    /** {@inheritDoc} */
    @Override public HashMap<String, String> getNextEvent() throws Exception {
        HashMap<String, String> returnData = null;
        int level = 0;

        while (jsonReader.hasNext()) {
            if (returnData == null) {
                returnData = new HashMap<String, String>();
            }
            if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                jsonReader.beginObject();
                level++;
            }
            returnData.put(jsonReader.nextName(), jsonReader.nextString());
            if (jsonReader.peek() == JsonToken.END_OBJECT) {
                jsonReader.endObject();
                if (--level == 0)
                    break;
            }
            if (jsonReader.peek() == JsonToken.END_ARRAY) {
                jsonReader.endArray();
                break;
            }
        }
        return returnData;
    }
}
