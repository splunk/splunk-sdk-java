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
import com.splunk.ResultsReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.HashMap;

public class ResultsReaderCsv extends ResultsReader {

    private CSVReader csvReader = null;
    private String[] keys;

    /**
     * Class Constructor.
     *
     * Construct a streaming CSV reader for the event stream. One should only
     * attempt to parse an CSV stream with the CSV reader. Using a non-CSV
     * stream will yield unpredictable results.
     *
     * @param inputStream The stream to be parsed.
     * @throws Exception On exception.
     */
    public ResultsReaderCsv(InputStream inputStream) throws IOException {
        super(inputStream);
        csvReader = new CSVReader(new InputStreamReader(inputStream, "UTF8"));
        // initial line contains the keys, except for oneshot -- which contains
        // a blank line, and then the key list.
        keys = csvReader.readNext();
        if (keys.length == 1 && keys[0].trim().equals("")) {
            keys = csvReader.readNext();
        }
    }

    /** {@inheritDoc} */
    @Override public void close() throws IOException {
        super.close();
        if (csvReader != null)
            csvReader.close();
        csvReader = null;
    }

    /** {@inheritDoc} */
    @Override public HashMap<String, String> getNextEvent() throws IOException {
        HashMap<String, String> returnData = null;
        String[] line;

        if ((line = csvReader.readNext()) != null) {
        	if (line.length == 1 && line[0].equals("")) {
        		line = csvReader.readNext();
        		if (line == null) {
        			return returnData;
        		}
        	}
        	
            returnData = new HashMap<String, String>();
            int count = 0;
            for (String key: keys) {
                returnData.put(key, line[count++]);
            }
        }

        return returnData;
    }
}
