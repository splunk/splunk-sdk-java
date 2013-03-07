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
 * The {@code MultiResultsReaderXml} class represents a streaming XML reader for
 * Splunk search results. It supports the export endpoint which may return
 * one of more previews before final results.
 */
public class MultiResultsReaderXml
        extends MultiResultsReader<ResultsReaderXml> {
    /**
     * Class constructor.
     *
     * Constructs a streaming XML reader for the event stream. You should only
     * attempt to parse an XML stream with the XML reader. Unpredictable results
     * may occur if you use a non-XML stream.
     *
     * @param inputStream The stream to parse.
     * @throws IOException
     */
    public MultiResultsReaderXml(InputStream inputStream) throws IOException {
        super(new ResultsReaderXml(inputStream, true));
    }
}
