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

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.InputStream;
import java.util.*;

/**
 * Test the parsing of Atom feeds.
 *
 * All the raw XML to parse is in the data/atom/ directory, and the expected
 * behavior is specified in the data/atom_test_data.json file.
 */
@RunWith(Parameterized.class)
public class AtomFeedTest extends SDKTestCase {
    private static Gson reader = new Gson();
    private static Map<String, Object> expectedData = reader.fromJson(streamToString(openResource("data/atom_test_data.json")), Map.class);

    private Map<String, Object> expectedFeed;
    private String testName;
    private InputStream xmlStream;

    public AtomFeedTest(String testName) {
        this.testName = testName;
        this.expectedFeed = (Map<String, Object>)expectedData.get(testName);
        this.xmlStream = openResource("data/atom/" + testName + ".xml");
    }

    @Test
    public void testAtomFeed() {
        Map<String, Object> metadata = (Map<String, Object>)expectedFeed.get("metadata");
        AtomFeed found = AtomFeed.parseStream(this.xmlStream);

        assertEquals(metadata.get("itemsPerPage"), found.itemsPerPage);
        assertEquals(metadata.get("startIndex"), found.startIndex);
        assertEquals(metadata.get("totalResults"), found.totalResults);
        // generator

        List<Map<String, Object>> expectedEntries = (List<Map<String, Object>>)expectedFeed.get("entries");
        List<AtomEntry> foundEntries = (List<AtomEntry>)found.entries;
        for (int i = 0; i < foundEntries.size(); i++) {
            AtomEntry foundEntry = foundEntries.get(i);
            Map<String, Object> expectedEntry = expectedEntries.get(i);
            assertEquals(expectedEntry.get("id"), foundEntry.id);
            assertEquals(expectedEntry.get("title"), foundEntry.title);
            assertEquals(expectedEntry.get("updated"), foundEntry.updated);
            // "author" is not parsed.
            Map<String, String> expectedLinks = (Map<String, String>)expectedEntry.get("links");
            for (String linkName : foundEntry.links.keySet()) {
                assertEquals(expectedLinks.get(linkName), foundEntry.links.get(linkName));
            }
            if (expectedEntry.containsKey("content")) {
                Map<String, Object> expectedContent = (Map<String, Object>)expectedEntry.get("content");
                for (String key : foundEntry.content.keySet()) {
                    if (!key.startsWith("eai:")) {
                        assertEquals("Mismatch on " + key, expectedContent.get(key), foundEntry.content.get(key));
                    }
                }
            } else {
                assertNull(foundEntry.content);
            }
        }
    }

    @Parameterized.Parameters(name="{0}")
    public static Collection<Object[]> testCases() {
        Collection<Object[]> cases = new ArrayList<Object[]>();
        for (String key : (Set<String>)expectedData.keySet()) {
            cases.add(new Object[] { key });
        }
        return cases;
    }
}
