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
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

import java.io.InputStream;
import java.util.*;

/**
 * Test the parsing of Atom feeds.
 *
 * All the raw XML to parse is in the data/atom/ directory, and the expected
 * behavior is specified in the data/atom_test_data.json file.
 */
@RunWith(Parameterized.class)
public class AtomFeedTest {
    private static Gson reader = new Gson();
    private static Map<String, Object> expectedData = reader.fromJson(
            SDKTestCase.streamToString(
                SDKTestCase.openResource(
                    "data/atom_test_data.json")),
            Map.class);

    private Map<String, Object> expectedFeed;
    private String testName;
    private InputStream xmlStream;

    public AtomFeedTest(String testName) {
        this.testName = testName;
        this.expectedFeed = (Map<String, Object>)expectedData.get(testName);
        this.xmlStream = SDKTestCase.openResource("data/atom/" + testName + ".xml");
    }

    @Test
    public void testAtomFeed() {
        Map<String, Object> expectedMetadata = (Map<String, Object>)expectedFeed.get("metadata");
        AtomFeed actualFeed = AtomFeed.parseStream(this.xmlStream);

        assertEquals(expectedMetadata.get("itemsPerPage"), actualFeed.itemsPerPage);
        assertEquals(expectedMetadata.get("startIndex"), actualFeed.startIndex);
        assertEquals(expectedMetadata.get("totalResults"), actualFeed.totalResults);

        // The generator header is ignored by AtomFeed, so we don't test it.

        List<Map<String, Object>> expectedEntries = (List<Map<String, Object>>)expectedFeed.get("entries");
        List<AtomEntry> actualEntries = (List<AtomEntry>)actualFeed.entries;
        for (int i = 0; i < actualEntries.size(); i++) {
            AtomEntry actualEntry = actualEntries.get(i);
            Map<String, Object> expectedEntry = expectedEntries.get(i);
            assertEquals(expectedEntry.get("id"), actualEntry.id);
            assertEquals(expectedEntry.get("title"), actualEntry.title);
            assertEquals(expectedEntry.get("updated"), actualEntry.updated);
            // "author" is not parsed.
            Map<String, String> expectedLinks = (Map<String, String>)expectedEntry.get("links");
            for (String linkName : actualEntry.links.keySet()) {
                assertEquals(expectedLinks.get(linkName), actualEntry.links.get(linkName));
            }
            if (expectedEntry.containsKey("content")) {
                Map<String, Object> expectedContent = (Map<String, Object>)expectedEntry.get("content");
                for (String key : actualEntry.content.keySet()) {
                    if (!key.startsWith("eai:")) {
                        assertEquals("Mismatch on " + key, expectedContent.get(key), actualEntry.content.get(key));
                    }
                }
            } else {
                assertNull(actualEntry.content);
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
