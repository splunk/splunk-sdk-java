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

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Test aspects of results readers not covered by the atom, results, and export test data.
 *
 * Note: some of these tests predate the introduction of the atom, results, and export test
 * data, and may overlap with tests in that set.
 */
public class ResultsReaderTest extends SDKTestCase {
    @Test
    public void testReadCsv() throws Exception {
        InputStream input = openResource("results.csv");
        ResultsReaderCsv reader = new ResultsReaderCsv(input);
        Map<String, String> expected = new HashMap<String, String>();

        expected.put("series", "twitter");
        expected.put("sum(kb)", "14372242.758775");
        assertNextEventEquals(expected, reader);

        expected.put("series", "splunkd");
        expected.put("sum(kb)", "267802.333926");
        assertNextEventEquals(expected, reader);

        expected.put("series", "splunkd_access");
        expected.put("sum(kb)", "5979.036338");
        assertNextEventEquals(expected, reader);

        assertNull(reader.getNextEvent());
        reader.close();
    }

    @Test
    public void testReadCsvFromOneshot() throws Exception {
        InputStream input = service.oneshotSearch(
                "search index=_internal | head 1 | stats count",
                Args.create("output_mode", "csv"));
        ResultsReaderCsv reader = new ResultsReaderCsv(input);
        Map<String, String> expected = new HashMap<String, String>();

        expected.put("count", "1");
        assertNextEventEquals(expected, reader);

        assertNull(reader.getNextEvent());
        reader.close();
    }

    @Test
    public void testReadJsonOnSplunk4() throws Exception {
        InputStream input = openResource("results4.json");
        ResultsReaderJson reader = new ResultsReaderJson(input);
        Map<String, String> expected = new HashMap<String, String>();

        expected.put("series", "twitter");
        expected.put("sum(kb)", "14372242.758775");
        assertNextEventEquals(expected, reader);

        expected.put("series", "splunkd");
        expected.put("sum(kb)", "267802.333926");
        assertNextEventEquals(expected, reader);

        expected.put("series", "splunkd_access");
        expected.put("sum(kb)", "5979.036338");
        assertNextEventEquals(expected, reader);

        assertNull(reader.getNextEvent());
        reader.close();
    }

    @Test
    public void testReadJsonOnSplunk5() throws Exception {
        // Splunk 5.0 uses a different format for JSON results
        // from Splunk 4.3.
        InputStream input = openResource("results5.json");
        ResultsReaderJson reader = new ResultsReaderJson(input);
        Map<String, String> expected = new HashMap<String, String>();

        expected.put("series", "twitter");
        expected.put("sum(kb)", "14372242.758775");
        assertNextEventEquals(expected, reader);

        expected.put("series", "splunkd");
        expected.put("sum(kb)", "267802.333926");
        assertNextEventEquals(expected, reader);

        expected.put("series", "splunkd_access");
        expected.put("sum(kb)", "5979.036338");
        assertNextEventEquals(expected, reader);

        assertNull(reader.getNextEvent());
        reader.close();
    }
    
    public void testReadMultivalueCsvJson() throws IOException {
        // These results were generated from "search index=_internal | head 1",
        // with the output formats {xml, csv, json}.
        String search = "search index=_internal | head 1";
        
        testReadMultivalue(
                ResultsReaderXml.class, "resultsMV.xml");
        testReadMultivalue(
                ResultsReaderCsv.class, "resultsMV.csv");
        testReadMultivalue(
                ResultsReaderJson.class, "resultsMV4.json");
        testReadMultivalue(
                ResultsReaderJson.class, "resultsMV5.json");
        testReadMultivalue(
                ResultsReaderJson.class, "resultsMVFuture.json", ",");
        
        testReadMultivalue(
                ResultsReaderXml.class, "resultsMVOneshot.xml");
        testReadMultivalue(
                ResultsReaderCsv.class, "resultsMVOneshot.csv");
        testReadMultivalue(
                ResultsReaderJson.class, "resultsMVOneshot4.json");
        testReadMultivalue(
                ResultsReaderJson.class, "resultsMVOneshot5.json");
        testReadMultivalue(
                ResultsReaderJson.class, "resultsMVOneshotFuture.json", ",");
        
        testReadMultivalue(
                ResultsReaderXml.class, "splunk_search:blocking/xml/" + search);
        testReadMultivalue(
                ResultsReaderCsv.class, "splunk_search:blocking/csv/" + search);
        testReadMultivalue(
                ResultsReaderJson.class, "splunk_search:blocking/json/" + search);
        
        testReadMultivalue(
                ResultsReaderXml.class, "splunk_search:oneshot/xml/" + search);
        testReadMultivalue(
                ResultsReaderCsv.class, "splunk_search:oneshot/csv/" + search);
        testReadMultivalue(
                ResultsReaderJson.class, "splunk_search:oneshot/json/" + search);
    }
    
    private void testReadMultivalue(
            Class<? extends ResultsReader> type,
            String filename) throws IOException {
        
        // For our particular test data, the multi-value delimiter
        // for the "_si" field (which is being checked) is a newline
        // for those ResultsReaders that care about delimiters.
        String delimiter = (type == ResultsReaderXml.class) ? "," : "\n";
        
        testReadMultivalue(type, filename, delimiter);
    }
    
    private void testReadMultivalue(
            Class<? extends ResultsReader> type,
            String filename,
            String delimiter) throws IOException {
        
        // Test legacy getNextEvent() interface on 2-valued and 1-valued fields
        {
            ResultsReader reader = createResultsReader(type, openResource(filename));
            
            HashMap<String, String> firstResult = reader.getNextEvent();
            {
                String siDelimited = firstResult.get("_si");
                String[] siArray = siDelimited.split(Pattern.quote(delimiter));
                assertEquals(2, siArray.length);
                // (siArray[0] should be the locally-determined hostname of
                //  splunkd, but there is no good way to test this
                //  consistently.)
                assertEquals("_internal", siArray[1]);
            }
            assertEquals("_internal", firstResult.get("index"));
            
            assertNull("Expected exactly one result.", reader.getNextEvent());
            reader.close();
        }
        
        // Test new getNextEvent() interface on 2-valued and 1-valued fields
        {
            ResultsReader reader = createResultsReader(type, openResource(filename));
            
            Event firstResult = reader.getNextEvent();
            {
                String[] siArray = firstResult.getArray("_si", delimiter);
                assertEquals(2, siArray.length);
                // (siArray[0] should be the locally-determined hostname of
                //  splunkd, but there is no good way to test this
                //  consistently.)
                assertEquals("_internal", siArray[1]);
            }
            assertEquals(
                    new String[] {"_internal"},
                    firstResult.getArray("index", delimiter));
            
            assertNull("Expected exactly one result.", reader.getNextEvent());
            reader.close();
        }
    }
    
    private static ResultsReader createResultsReader(
            Class<? extends ResultsReader> type, InputStream input) {
        
        try {
            return type.getConstructor(InputStream.class).newInstance(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void testEventIsReadOnly() {
        Event event = new Event();
        
        try {
            event.clear();
            fail("Expected UnsupportedOperationException.");
        }
        catch (UnsupportedOperationException e) {
            // Good
        }
        
        try {
            event.clone();
            fail("Expected UnsupportedOperationException.");
        }
        catch (UnsupportedOperationException e) {
            // Good
        }
        
        try {
            event.put(null, null);
            fail("Expected UnsupportedOperationException.");
        }
        catch (UnsupportedOperationException e) {
            // Good
        }
        
        try {
            event.putAll(null);
            fail("Expected UnsupportedOperationException.");
        }
        catch (UnsupportedOperationException e) {
            // Good
        }
        
        try {
            event.remove(null);
            fail("Expected UnsupportedOperationException.");
        }
        catch (UnsupportedOperationException e) {
            // Good
        }
    }
    
    // === Utility ===
    
    private void assertNextEventEquals(
            Map<String, String> expected,
            ResultsReader reader) throws IOException {
        
        assertEquals(expected, reader.getNextEvent());
        expected.clear();
    }
}
