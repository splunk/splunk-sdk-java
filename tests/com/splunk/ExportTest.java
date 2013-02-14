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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Test loading of data from the export endpoints.
 *
 * All the relevant data is in the data/export/ directory (for raw XML to be parsed)
 * and the data/export_test_data.json file (for the expected results of parsing).
 */
@RunWith(Parameterized.class)
public class ExportTest extends SDKTestCase {
    private static Gson reader = new Gson();
    private static Map<String, Object> expectedData = reader.fromJson(
            streamToString(openResource("data/export_test_data.json")),
            Map.class
    );

    private String version;

    public ExportTest(String version) {
        this.version = version;
        Map<String, Object> versionData =
                (Map<String, Object>)expectedData.get(version);
    }

    @Parameterized.Parameters(name="from version {0}")
    public static Collection<Object[]> testCases() {
        Collection<Object[]> cases = new ArrayList<Object[]>();
        for (String version : (Set<String>)expectedData.keySet()) {
            cases.add(new Object[] {version});
        }
        return cases;
    }

    @Test
    public void testExportWithoutPreview() throws IOException {
        Map<String, Object> thisVersion = (Map<String, Object>)expectedData.get(this.version);
        if (!thisVersion.containsKey("without_preview")) {
            return; // No test case
        }
        Map<String, Object> expectedResultsSet = (Map<String, Object>)thisVersion.get("without_preview");
        List<Map<String, Object>> expectedEvents = (List<Map<String, Object>>)expectedResultsSet.get("results");

        InputStream xmlStream = openResource("data/export/" + this.version + "/export_results.xml");
        ResultsReaderXml resultsReader = new ResultsReaderXml(xmlStream);

        for (int i = 0; i < expectedEvents.size(); i++) {
            Map<String, Object> expectedEvent = expectedEvents.get(i);
            Event foundEvent = resultsReader.getNextEvent();

            assertEquals(expectedEvent.keySet(), foundEvent.keySet());
            for (String key : expectedEvent.keySet()) {
                assertTrue(foundEvent.containsKey(key));
                if (expectedEvent.get(key) instanceof List) {
                    assertEquals(expectedEvent.get(key), Arrays.asList(foundEvent.getArray(key)));
                } else {
                    assertEquals(expectedEvent.get(key), foundEvent.get(key));
                }
            }
        }
    }

    @Test
    public void testExportWithPreview() throws IOException {
        Map<String, Object> thisVersion = (Map<String, Object>)expectedData.get(this.version);
        if (!thisVersion.containsKey("with_preview")) {
            return; // No test case
        }
        List<Map<String, Object>> expectedResultsSets = (List<Map<String, Object>>)thisVersion.get("with_preview");

        InputStream xmlStream = openResource("data/export/" + this.version + "/export_results.xml");
        // Some kind of results reader here...

        for (Map<String, Object> expectedResultsSet : expectedResultsSets) {
            assertNotNull(expectedResultsSet);
            // check that this results set matches the corresponding part of the reader
        }
    }

    @Test
    public void testExportNonreporting() throws IOException {
        Map<String, Object> thisVersion = (Map<String, Object>)expectedData.get(this.version);
        if (!thisVersion.containsKey("nonreporting")) {
            return; // No test case
        }
        Map<String, Object> expectedResultsSet = (Map<String, Object>)thisVersion.get("nonreporting");
        List<Map<String, Object>> expectedEvents = (List<Map<String, Object>>)expectedResultsSet.get("results");

        InputStream xmlStream = openResource("data/export/" + this.version + "/nonreporting.xml");
        ResultsReaderXml resultsReader = new ResultsReaderXml(xmlStream);

        for (Map<String, Object> expectedEvent : expectedEvents) {
            Event foundEvent = resultsReader.getNextEvent();
            assertNotNull("Did not parse as many events from the XML as expected.", foundEvent);
            assertEquals(expectedEvent.keySet(), foundEvent.keySet());
            for (String key : expectedEvent.keySet()) {
                assertTrue(foundEvent.containsKey(key));
                if (expectedEvent.get(key) instanceof List) {
                    assertEquals(
                            expectedEvent.get(key),
                            Arrays.asList(foundEvent.getArray(key))
                    );
                } else {
                    assertEquals(expectedEvent.get(key), foundEvent.get(key));
                }
            }
        }
    }
}
