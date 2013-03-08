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

import java.io.IOException;
import java.io.InputStream;

/**
 * The {@code MultiResultsReaderJson} class represents a streaming JSON reader
 * for Splunk search results. This reader supports streams from export searches, 
 * which might return one of more previews before returning final results.
 */
public class MultiResultsReaderJson
        extends MultiResultsReader<ResultsReaderJson> {
    /**
     * Class constructor.
     *
     * Constructs a streaming JSON reader for the event stream. You should only
     * attempt to parse a JSON stream with this reader. Unpredictable results
     * may occur if you try to parse a stream with a different format.
     *
     * @param inputStream The JSON stream to parse.
     * @throws IOException
     */
    public MultiResultsReaderJson(InputStream inputStream) throws IOException {
        super(new ResultsReaderJson(inputStream, true));
    }
}