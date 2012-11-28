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
import java.util.HashMap;
import java.util.Map;

public class ResultsReaderTest extends SDKTestCase {

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }
	
    private InputStream openResource(String path) {
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

        expected.clear();
        expected.put("series", "twitter");
        expected.put("sum(kb)", "14372242.758775");
        assertEquals(expected, reader.getNextEvent());

        expected.clear();
        expected.put("series", "splunkd");
        expected.put("sum(kb)", "267802.333926");
        assertEquals(expected, reader.getNextEvent());

        expected.clear();
        expected.put("series", "flurry");
        expected.put("sum(kb)", "12576.454102");
        assertEquals(expected, reader.getNextEvent());

        expected.clear();
        expected.put("series", "splunkd_access");
        expected.put("sum(kb)", "5979.036338");
        assertEquals(expected, reader.getNextEvent());

        expected.clear();
        expected.put("series", "splunk_web_access");
        expected.put("sum(kb)", "5838.935649");
        assertEquals(expected, reader.getNextEvent());

        assertNull(reader.getNextEvent());
        
        reader.close();
    }

    @Test
    public void testReadRawField() throws IOException {
        InputStream input = openResource("raw_field.xml");
        ResultsReaderXml reader = new ResultsReaderXml(input);
        Map<String, String> expected = new HashMap<String, String>();

        expected.clear();
        expected.put(
                "_raw",
                "07-13-2012 09:27:27.307 -0700 INFO  Metrics - group=search_concurrency, system total, active_hist_searches=0, active_realtime_searches=0"
        );
        assertEquals(expected, reader.getNextEvent());

        assertNull(reader.getNextEvent());
        
        reader.close();
    }

    @Test
    public void testReadCsv() throws Exception {
        InputStream input = openResource("results.csv");
        ResultsReaderCsv reader = new ResultsReaderCsv(input);
        Map<String, String> expected = new HashMap<String, String>();

        expected.clear();
        expected.put("series", "twitter");
        expected.put("sum(kb)", "14372242.758775");
        assertEquals(expected, reader.getNextEvent());

        expected.clear();
        expected.put("series", "splunkd");
        expected.put("sum(kb)", "267802.333926");
        assertEquals(expected, reader.getNextEvent());

        expected.clear();
        expected.put("series", "splunkd_access");
        expected.put("sum(kb)", "5979.036338");
        assertEquals(expected, reader.getNextEvent());

        assertNull(reader.getNextEvent());
        
        reader.close();
    }

    @Test
    public void testReadCsvFromOneshot() throws Exception {
        InputStream input = service.oneshot("search index=_internal | head 1 | stats count", Args.create("output_mode", "csv"));
        assertNotNull("Failed to find results.csv", input);
        
        ResultsReaderCsv reader = new ResultsReaderCsv(input);
        Map<String, String> expected = new HashMap<String, String>();

        expected.clear();
        expected.put("count", "1");
        assertEquals(expected, reader.getNextEvent());

        assertNull(reader.getNextEvent());
        
        reader.close();
    }

    @Test
    public void testReadJsonOnSplunk4() throws Exception {
        InputStream input = openResource("results4.json");
        ResultsReaderJson reader = new ResultsReaderJson(input);
        Map<String, String> expected = new HashMap<String, String>();

        expected.clear();
        expected.put("series", "twitter");
        expected.put("sum(kb)", "14372242.758775");
        assertEquals(expected, reader.getNextEvent());

        expected.clear();
        expected.put("series", "splunkd");
        expected.put("sum(kb)", "267802.333926");
        assertEquals(expected, reader.getNextEvent());

        expected.clear();
        expected.put("series", "splunkd_access");
        expected.put("sum(kb)", "5979.036338");
        expected.put("mvfield", "1,2");
        assertEquals(expected, reader.getNextEvent());

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

        expected.clear();
        expected.put("series", "twitter");
        expected.put("sum(kb)", "14372242.758775");
        assertEquals(expected, reader.getNextEvent());

        expected.clear();
        expected.put("series", "splunkd");
        expected.put("sum(kb)", "267802.333926");
        assertEquals(expected, reader.getNextEvent());

        expected.clear();
        expected.put("series", "splunkd_access");
        expected.put("sum(kb)", "5979.036338");
        assertEquals(expected, reader.getNextEvent());

        assertNull(reader.getNextEvent());
        
        reader.close();
    }
}
