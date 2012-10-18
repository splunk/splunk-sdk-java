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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class ResultsReaderTest extends TestCase {
    public InputStream openResource(String path) {
        try {
            return this.getClass().getResourceAsStream(path);
        } catch (Exception e) {
            fail(e.toString());
            return null;
        }
    }

    @Test public void testAtomFeed() {
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

    @Test public void test200Results() {
        InputStream input = openResource("results200.xml");
        try {
            ResultsReaderXml reader = new ResultsReaderXml(input);
            Map<String, String> event = reader.getNextEvent();
            assertEquals("0:39987", event.get("_cd"));
            assertEquals("blovering.local", event.get("host"));
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test public void test404Results() {

    }
}
