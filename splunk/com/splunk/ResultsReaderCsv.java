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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * The {@code ResultsReaderCsv} class represents a streaming CSV reader for
 * Splunk search results. This class requires the opencsv-2.3.jar file in your 
 * build path.
 */
public class ResultsReaderCsv extends ResultsReader {

    private CSVReader csvReader = null;
    private List<String> keys;

    /**
     * Class constructor.
     *
     * Constructs a streaming CSV reader for the event stream. You should only
     * attempt to parse a CSV stream with this reader. If you attempt to parse 
     * a different type of stream, unpredictable results may occur. 
     *
     * @param inputStream The CSV stream to parse.
     * @throws IOException
     */
    public ResultsReaderCsv(InputStream inputStream) throws IOException {
        super(inputStream, false);
        if (isExportStream)
            throw new UnsupportedOperationException(
                "A stream from an export endpoint is not supported " +
                "by a CSV result reader. Use XML or JSON search output "+
                "format and matching reader instead."
            );
        csvReader = new CSVReader(new InputStreamReader(inputStream, "UTF8"));
        // initial line contains the keyArray, except for oneshot -- which 
        // contains a blank line, and then the key list.
        String[] keyArray = csvReader.readNext();
        if (keyArray.length == 1 && keyArray[0].trim().equals("")) {
            keyArray = csvReader.readNext();
        }
        keys = Arrays.asList(keyArray);
    }

    /** {@inheritDoc} */
    @Override public void close() throws IOException {
        super.close();
        if (csvReader != null)
            csvReader.close();
        csvReader = null;
    }

    /**
     * This method is not supported.
     * @return Not applicable.
     */
    public boolean isPreview(){
        throw new UnsupportedOperationException(
                "isPreview() is not supported by this subclass.");
    }

    /** {@inheritDoc} */
    public Collection<String> getFields(){
       return keys;
    }

    /*
     * Multiple result sets are not supported by this reader.
     * This function reads the entire stream.
     * An application won't reach here with a stream from
     * an /export endpoint. The constructor will throw an error in that case.
     */
    @Override Event getNextEventInCurrentSet() throws IOException {
        Event returnData = null;
        String[] line;

        if ((line = csvReader.readNext()) != null) {
            if (line.length == 1 && line[0].equals("")) {
                line = csvReader.readNext();
                if (line == null) {
                    return returnData;
                }
            }
            
            returnData = new Event();
            int count = 0;
            for (String key : keys) {
                String delimitedValues = line[count++];
                returnData.putSingleOrDelimited(key, delimitedValues);
            }
        }

        return returnData;
    }
}
