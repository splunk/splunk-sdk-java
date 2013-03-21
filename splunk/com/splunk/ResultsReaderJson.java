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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The {@code ResultsReaderJson} class represents a streaming JSON reader for
 * Splunk search results. This class requires the gson-2.1.jar file in your 
 * build path. If you want to access the preview events, use the 
 * {@link MultiResultsReaderJson} class.
 */
public class ResultsReaderJson extends ResultsReader {
    private JsonReader jsonReader;
    // Helper object that will only be constructed if the reader is handling
    // json format used by export.
    private ExportHelper exportHelper;
    // Whether the 'preview' flag is read
    private boolean previewFlagRead;

    /**
     * Class constructor.
     *
     * Constructs a streaming JSON reader for the event stream. You should only
     * attempt to parse a JSON stream with this reader. If you attempt to parse 
     * a different type of stream, unpredictable results may occur. 
     *
     * @param inputStream The JSON stream to parse.
     * @throws IOException
     */
    public ResultsReaderJson(InputStream inputStream) throws IOException {
        this(inputStream, false);
    }

    ResultsReaderJson(InputStream inputStream, boolean isInMultiReader)
            throws IOException {
        super(inputStream, isInMultiReader);
        jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF8"));
        // if stream is empty, return a null reader.
        jsonReader.setLenient(true);
        if (isExportStream || isInMultiReader)
            exportHelper = new ExportHelper();
        finishInitialization();
    }

    // Advance in the json stream, reading meta data if available, and
    // get ready for readEvent method.
    // Return false if end of stream is encountered.
    boolean advanceIntoNextSetBeforeEvent() throws IOException {
        // jsonReader will be set to null once the end is reached.
        if (jsonReader == null)
            return false;

        // In Splunk 5.0 from the export endpoint,
        // each result is in its own top level object.
        // In Splunk 5.0 not from the export endpoint, the results are
        // an array at that object's key "results".
        // In Splunk 4.3, the
        // array was the top level returned. So if we find an object
        // at top level, we step into it until we find the right key,
        // then leave it in that state to iterate over.
        try {
            // Json single-reader depends on 'isExport' flag to function.
            // It does not support a stream from a file saved from
            // a stream from an export endpoint.
            // Json multi-reader assumes export format thus does not support
            // a stream from none export endpoints.
            if (exportHelper != null) {
                if (jsonReader.peek() == JsonToken.BEGIN_ARRAY)
                    throw new UnsupportedOperationException(
                        "A stream from an export endpoint of " +
                        "a Splunk 4.x server in the JSON output format " +
                        "is not supported by this class. " +
                        "Use the XML search output format, " +
                        "and an XML result reader instead.");
                /*
                 * We're on a stream from an export endpoint
                 * Below is an example of an input stream.
                 *      {"preview":true,"offset":0,"lastrow":true,"result":{"host":"Andy-PC","count":"62"}}
                 *      {"preview":true,"offset":0,"result":{"host":"Andy-PC","count":"1682"}}
                 */
                // Read into first result object of the next set.
                while (true) {
                    boolean endPassed = exportHelper.lastRow;
                    exportHelper.skipRestOfRow();
                    if (!exportHelper.readIntoRow())
                        return false;
                    if (endPassed)
                        break;
                }
                return true;
            }
            // Single-reader not from an export endpoint
            if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                 /*
                  * We're on Splunk 5 with a single-reader not from
                  * an export endpoint
                  * Below is an example of an input stream.
                  *     {"preview":false,"init_offset":0,"messages":[{"type":"DEBUG","text":"base lispy: [ AND index::_internal ]"},{"type":"DEBUG","text":"search context: user=\"admin\", app=\"search\", bs-pathname=\"/Users/fross/splunks/splunk-5.0/etc\""}],"results":[{"sum(kb)":"14372242.758775","series":"twitter"},{"sum(kb)":"267802.333926","series":"splunkd"},{"sum(kb)":"5979.036338","series":"splunkd_access"}]}
                  */
                jsonReader.beginObject();
                String key;
                while (true) {
                    key = jsonReader.nextName();
                    if (key.equals("preview"))
                        readPreviewFlag();
                    else if (key.equals("results")) {
                        jsonReader.beginArray();
                        return true;
                    } else {
                        skipEntity();
                    }
                }
            } else { // We're on Splunk 4.x, and we just need to start the array.
                /*
                 * Below is an example of an input stream
                 *   [
                 *       {
                 *           "sum(kb)":"14372242.758775",
                 *               "series":"twitter"
                 *       },
                 *       {
                 *           "sum(kb)":"267802.333926",
                 *               "series":"splunkd"
                 *       },
                 *       {
                 *           "sum(kb)":"5979.036338",
                 *               "series":"splunkd_access"
                 *       }
                 *   ]
                 */
                jsonReader.beginArray();
                return true;
            }
        } catch (EOFException e) {
            return false;
        }
    }

    private void readPreviewFlag() throws IOException {
        isPreview = jsonReader.nextBoolean();
        previewFlagRead = true;
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
    @Override public void close() throws IOException {
        super.close();
        if (jsonReader != null)
            jsonReader.close();
        jsonReader = null;
    }

    /** {@inheritDoc} */
    public boolean isPreview(){
        if (!previewFlagRead)
            throw new UnsupportedOperationException(
                "isPreview() is not supported " +
                "with a stream from a Splunk 4.x server by this class. " +
                "Use the XML format and an XML result reader instead.");
        return isPreview;
    }

    /**
     * This method is not supported.
     * @return Not applicable.
     */
    public Collection<String> getFields(){
        throw new UnsupportedOperationException(
                "getFields() is not supported by this subclass.");
    }

    @Override Event getNextEventInCurrentSet() throws IOException {
        if (exportHelper != null) {
            // If the last row has been passed and moveToNextStreamPosition
            // has not been called, end the current set.
            if (exportHelper.lastRow && !exportHelper.inRow ) {
                return null;
            }
            exportHelper.readIntoRow();
        }

        Event returnData = readEvent();

        if (exportHelper != null) {
            exportHelper.skipRestOfRow();
            return returnData;
        }
        // Single reader not from export
        if (returnData == null)
            close();
        return returnData;
    }

    private Event readEvent() throws IOException {
        Event returnData = null;
        String name = null;
        List<String> values = new ArrayList<String>();

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
                returnData = new Event();
            }
            if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                jsonReader.beginObject();
            }
            if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                // The Gson parser is a little unintuitive here. Nested objects,
                // have their own relative notion of hasNext; when hasNext()
                // is done, it is only for this array.
                while (jsonReader.hasNext()) {
                    JsonToken jsonToken2 = jsonReader.peek();
                    if (jsonToken2 == JsonToken.STRING) {
                        values.add(jsonReader.nextString());
                    }
                }
                jsonReader.endArray();

                String[] valuesArray =
                    values.toArray(new String[values.size()]);
                returnData.putArray(name, valuesArray);

                values.clear();
            }
            if (jsonReader.peek() == JsonToken.NAME) {
                name = jsonReader.nextName();
            }
            if (jsonReader.peek() == JsonToken.STRING) {
                String delimitedValues = jsonReader.nextString();
                returnData.putSingleOrDelimited(name, delimitedValues);
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

    @Override boolean advanceStreamToNextSet() throws IOException{
        return advanceIntoNextSetBeforeEvent();
    }

    /**
     * Contains code only used for streams from the export endpoint.
     */
    private class ExportHelper {
        // Initial value must be true so that
        // the first row is treated as the start of a new set.
        boolean lastRow = true;
        boolean inRow;

        ExportHelper() { }

        // Return false if end of stream is encountered.
        private boolean readIntoRow() throws IOException {
            if (inRow)
                return true;
            if (jsonReader.peek() == JsonToken.END_DOCUMENT)
                return false;
            inRow = true;
            jsonReader.beginObject();
            // lastrow name and value pair does not appear if the row
            // is not the last in the set.
            lastRow = false;
            while (jsonReader.hasNext()) {
                String key = jsonReader.nextName();
                if (key.equals("preview")) {
                    readPreviewFlag();
                } else if (key.equals("lastrow")) {
                    lastRow = jsonReader.nextBoolean();
                } else if (key.equals("result")) {
                    return true;
                } else {
                    skipEntity();
                }
            }
            return false;
        }
                           
        private void skipRestOfRow() throws IOException {
            if (!inRow)
                return;
            inRow = false;
            while (jsonReader.peek() != JsonToken.END_OBJECT) {
                skipEntity();
            }
            jsonReader.endObject();
        }
    }
}
