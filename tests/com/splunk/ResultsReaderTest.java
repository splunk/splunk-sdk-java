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

import junit.framework.TestCase;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ResultsReaderTest extends TestCase {
    private InputStream openResource(String path) {
        return this.getClass().getResourceAsStream(path);
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
    public void testResults() {
        InputStream input = openResource("results.xml");
        try {
            ResultsReaderXml reader = new ResultsReaderXml(input);

            Map<String, String> expected, found;
            expected = new HashMap<String, String>();

            expected.clear();
            expected.put("series", "twitter");
            expected.put("sum(kb)", "14372242.758775");
            found = reader.getNextEvent();
            assertEquals(expected, found);

            expected.clear();
            expected.put("series", "splunkd");
            expected.put("sum(kb)", "267802.333926");
            found = reader.getNextEvent();
            assertEquals(expected, found);

            expected.clear();
            expected.put("series", "flurry");
            expected.put("sum(kb)", "12576.454102");
            found = reader.getNextEvent();
            assertEquals(expected, found);

            expected.clear();
            expected.put("series", "splunkd_access");
            expected.put("sum(kb)", "5979.036338");
            found = reader.getNextEvent();
            assertEquals(expected, found);

            expected.clear();
            expected.put("series", "splunk_web_access");
            expected.put("sum(kb)", "5838.935649");
            found = reader.getNextEvent();
            assertEquals(expected, found);

        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testReadRawField() {
        InputStream input = openResource("raw_field.xml");
        try {
            ResultsReaderXml reader = new ResultsReaderXml(input);

            Map<String, String> expected, found;
            expected = new HashMap<String, String>();

            expected.clear();
            expected.put(
                    "_raw",
                    "07-13-2012 09:27:27.307 -0700 INFO  Metrics - group=search_concurrency, system total, active_hist_searches=0, active_realtime_searches=0"
            );
            found = reader.getNextEvent();
            assertEquals(expected, found);
        } catch (Exception e) {
            fail(e.toString());
        }
    }
}