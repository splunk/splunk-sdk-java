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

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ResultsReaderTest extends SDKTestCase {

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }
    
    private InputStream openResource(String path) {
        if (path.startsWith("splunk_search:")) {
            path = path.substring("splunk_search:".length());
            
            String[] pathComponents = path.split("/");
            String searchType = pathComponents[0];
            String outputMode = pathComponents[1];
            String search = pathComponents[2];
            
            Args resultsArgs = new Args("output_mode", outputMode);
            if (searchType.equals("blocking")) {
                Job job = service.getJobs().create(
                        search,
                        new Args("exec_mode", "blocking"));
                return job.getResults(resultsArgs);
            }
            else if (searchType.equals("oneshot")) {
                return service.oneshotSearch(search, resultsArgs);
            }
            else {
                throw new IllegalArgumentException(
                        "Unrecognized search type: " + searchType);
            }
        }
        
        InputStream input = getClass().getResourceAsStream(path);
        assertNotNull("Could not open " + path, input);
        return input;
    }

    @Test
    public void testAtomFeed() {
        InputStream input = openResource("jobs.xml");
        AtomFeed feed = AtomFeed.parseStream(input);
        assertEquals(131, feed.entries.size());
        AtomEntry entry = feed.entries.get(0);
        assertEquals("2012-08-22T20:10:28.000-07:00", entry.updated);
        assertTrue(entry.content.containsKey("cursorTime"));
        assertEquals("1969-12-31T16:00:00.000-08:00", entry.content.getString("cursorTime"));
        assertTrue(entry.content.containsKey("diskUsage"));
        assertEquals(90112, entry.content.getInteger("diskUsage"));
        assertEquals(true, entry.content.getBoolean("isDone"));
    }

    @Test
    public void testResults() throws IOException {
        InputStream input = openResource("results.xml");
        ResultsReaderXml reader = new ResultsReaderXml(input);
        Map<String, String> expected = new HashMap<String, String>();

        expected.put("series", "twitter");
        expected.put("sum(kb)", "14372242.758775");
        assertNextEventEquals(expected, reader);
        
        expected.put("series", "splunkd");
        expected.put("sum(kb)", "267802.333926");
        assertNextEventEquals(expected, reader);

        expected.put("series", "flurry");
        expected.put("sum(kb)", "12576.454102");
        assertNextEventEquals(expected, reader);

        expected.put("series", "splunkd_access");
        expected.put("sum(kb)", "5979.036338");
        assertNextEventEquals(expected, reader);

        expected.put("series", "splunk_web_access");
        expected.put("sum(kb)", "5838.935649");
        assertNextEventEquals(expected, reader);

        assertNull(reader.getNextEvent());
        reader.close();
    }

    @Test
    public void testReadRawField() throws IOException {
        InputStream input = openResource("raw_field.xml");
        ResultsReaderXml reader = new ResultsReaderXml(input);
        Map<String, String> expected = new HashMap<String, String>();

        expected.put(
                "_raw",
                "07-13-2012 09:27:27.307 -0700 INFO  Metrics - group=search_concurrency, system total, active_hist_searches=0, active_realtime_searches=0"
        );
        assertNextEventEquals(expected, reader);

        assertNull(reader.getNextEvent());
        reader.close();
    }

    @Test
    public void testReadCsv() throws Exception {
        InputStream input = openResource("results.csv");
        ResultsReaderCsv reader = new ResultsReaderCsv(input);

        String[] fields = new String[0];
        fields = reader.getFields().toArray(fields);
        assertEquals(2, fields.length);
        assertEquals("sum(kb)", fields[0]);
        assertEquals("series", fields[1]);

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
    public void testReadFromExportJson() throws Exception {
        verifyMultiReader(getExportStreamJson());
    }

    @Test
    public void testReadFromExportXml() throws Exception {
        verifyMultiReader(getExportStreamXml());
    }

    private void verifyMultiReader(
                MultiResultsReader<? extends ResultsReader> reader)
                throws Exception {

        SearchResults singleResults = null;
        for(SearchResults results : reader) {
            singleResults = results;
            break;
        }

        Event singleEvent = null;
        for(Event event : singleResults) {
            singleEvent = event;
        }

       assertEquals("1", singleEvent.get("count"));
    }

    private MultiResultsReader getExportStreamJson() throws IOException {
        return new MultiResultsReaderJson(
            service.export(
                "search index=_internal | head 1 | stats count",
                Args.create("output_mode", "json")));
    }

    private MultiResultsReader getExportStreamXml() throws IOException {
        return new MultiResultsReaderXml(
            service.export(
                "search index=_internal | head 1 | stats count",
                Args.create("output_mode", "xml")));
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
    
    public void testReadMultivalueXmlCsvJson() throws IOException {
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

    @Test
    public void testPreviewSingleReaderXmlIter() throws Exception {
        testPreviewSingleReaderXml(true);
    }

    @Test
    public void testPreviewSingleReaderXmlGetNext() throws Exception {
        testPreviewSingleReaderXml(false);
    }

    private void testPreviewSingleReaderXml(boolean useIter) throws Exception {
        ResultsReaderXml reader = new ResultsReaderXml(
            openResource("results-preview.xml"));

        assertTrue(reader.isPreview());

        String[] fieldNameArray = new String[0];
        fieldNameArray = reader.getFields().toArray(fieldNameArray);
        assertEquals(101, fieldNameArray.length);
        assertEquals(fieldNameArray[99], "useragent");

        int index = 0;
        Event lastEvent = null;
        if (useIter){
            for (Event event : reader) {
                lastEvent = event;
                index ++;
            }
        } else {
            Event event;
            while ((event = reader.getNextEvent()) != null) {
                lastEvent = event;
                index ++;
            }
        }
        assertEquals("1355946614", lastEvent.get("_indextime"));
        assertEquals(10, index);

        reader.close();
    }

    final String resultsExportXml = "resultsExport.xml";

    @Test
    public void testExportSingleReaderXml() throws Exception {
        testExportSingleReader(
                new ResultsReaderXml(
                        getExportResultsStream(resultsExportXml)));
    }

    @Test
    public void testExportMultiReaderXml() throws Exception {
        testExportMultiReader(
            new MultiResultsReaderXml(
                getExportResultsStream(resultsExportXml)),
            18);
    }

    final String resultsExportJson = "resultsExport.json";

    @Test
    public void testExportSingleReaderJson() throws Exception {
        testExportSingleReader(
            new ResultsReaderJson(
                getExportResultsStream(resultsExportJson)));
    }

    @Test
    public void testExportMultiReaderJson() throws Exception {
        ExportResultsStream stream = getExportResultsStream(resultsExportJson);
        MultiResultsReaderJson multiReader = new MultiResultsReaderJson(stream);
        testExportMultiReader(multiReader, 15);
    }

    private ExportResultsStream getExportResultsStream(String fileName) {
        return new ExportResultsStream(
            openResource(fileName));
    }

    private void testExportSingleReader(
            ResultsReader<? extends ResultsReader> reader)
            throws Exception{

        int indexEvent = 0;
        for (Event event : reader){
            if (indexEvent == 0) {
                assertEquals("172.16.35.130", event.get("host"));
                assertEquals("16", event.get("count"));
            }

            if (indexEvent == 4) {
                assertEquals("three.four.com", event.get("host"));
                assertEquals("35994", event.get( "count"));
            }

            indexEvent++;
        }

        assertEquals(5, indexEvent);

        reader.close();
    }

    private void testExportMultiReader(
        MultiResultsReader<? extends ResultsReader> multiReader,
        int countResultSet)
        throws Exception{
        int indexResultSet = 0;
        SearchResults firstResults = null;
        for(SearchResults results : multiReader) {
            if (firstResults == null)
                firstResults = results;

            if (indexResultSet == countResultSet -1) {
                assertFalse(results.isPreview());
            }

            int indexEvent = 0;
            for (Event event : results) {
                if (indexResultSet == 1 && indexEvent == 1) {
                    assertEquals("andy-pc", event.get("host"));
                    assertEquals("3", event.get("count"));
                }

                if (indexResultSet == countResultSet - 2 && indexEvent == 3) {
                    assertEquals("andy-pc", event.get("host"));
                    assertEquals("135", event.get( "count"));
                }

                indexEvent++;
            }

            switch (indexResultSet) {
                case 0:
                    assertEquals(indexEvent, 1);
                    break;
                case 1:
                    assertEquals(indexEvent, 3);
                    break;
                default:
                    assertEquals(indexEvent, 5);
                    break;
            }
            indexResultSet++;
        }

        assertEquals(indexResultSet, countResultSet);

        // firstResults should be empty since the multi-reader has passed it
        // and there should be no exception.
        int count = 0;
        for (Event eventL : firstResults) {
            count++;
        }
        assertEquals(0, count);

        multiReader.close();
    }

    // === Utility ===
    
    private void assertNextEventEquals(
            Map<String, String> expected,
            ResultsReader reader) throws IOException {
        
        assertEquals(expected, reader.getNextEvent());
        expected.clear();
    }
}
