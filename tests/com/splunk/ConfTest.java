/*
 * Copyright 2011 Splunk, Inc.
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

public class ConfTest extends SplunkTestCase {
    final static String APP = "sdk-tests";
    final static String OWNER = "nobody";
    final static String assertRoot = "Conf assert: ";

    @Test public void testConfs() {
        Service service = connect();

        ConfCollection confs = service.getConfs();

        // Make sure the collection contains some of the expected entries.
        assertTrue(assertRoot + "#1", confs.containsKey("eventtypes"));
        assertTrue(assertRoot + "#2", confs.containsKey("searchbnf"));
        assertTrue(assertRoot + "#3", confs.containsKey("indexes"));
        assertTrue(assertRoot + "#4", confs.containsKey("inputs"));
        assertTrue(assertRoot + "#5", confs.containsKey("props"));
        assertTrue(assertRoot + "#6", confs.containsKey("transforms"));
        assertTrue(assertRoot + "#7", confs.containsKey("savedsearches"));

        // Iterate over the confs just to make sure we can read them
        for (EntityCollection<Entity> conf : confs.values()) {
            conf.getName();
            conf.getTitle();
            conf.getPath();
            for (Entity stanza : conf.values()) {
                try {
                    stanza.getName();
                    stanza.getTitle();
                    stanza.getPath();
                } catch (Exception e) {
                    // IF the application is disabled, trying to get info
                    // on it will in fact give us a 404 exception.
                }
            }
        }
    }

    // Test creating, reading, updating and deleting (where possible) confs,
    // stanzas and properties.
    @Test public void testConfCrud() {
        Service service;

        // Create a fresh app to use as the container for confs that we will
        // create in this test. There is no way to delete a conf once it's
        // created so we make sure to create in the context of this test app
        // and then we delete the app when we are done to make everything go
        // away.
        createApp(APP);
        service = connect();
        assertTrue(
            assertRoot + "#8", service.getApplications().containsKey(APP));

        // Create an app specific service instance
        Args args = new Args(command.opts);
        args.put("app", APP);
        args.put("owner", OWNER);
        service = Service.connect(args);

        ConfCollection confs = service.getConfs();
        assertFalse(assertRoot + "#9", confs.containsKey("testconf"));
        confs.create("testconf");
        assertTrue(assertRoot + "#10", confs.containsKey("testconf"));

        EntityCollection<Entity> stanzas = confs.get("testconf");
        assertEquals(assertRoot + "#11", 0, stanzas.size());

        stanzas.create("stanza1");
        stanzas.create("stanza2");
        stanzas.create("stanza3");
        assertEquals(assertRoot + "#12", 3, stanzas.size());
        assertTrue(assertRoot + "#13", stanzas.containsKey("stanza1"));
        assertTrue(assertRoot + "#14", stanzas.containsKey("stanza2"));
        assertTrue(assertRoot + "#15", stanzas.containsKey("stanza3"));

        // Grab the new stanza and check its content
        Entity stanza1 = stanzas.get("stanza1");
        assertEquals(assertRoot + "#16", "nobody", stanza1.get("eai:userName"));
        assertEquals(assertRoot + "#17", APP, stanza1.get("eai:appName"));
        assertFalse(assertRoot + "#18", stanza1.containsKey("key1"));
        assertFalse(assertRoot + "#19", stanza1.containsKey("key2"));
        assertFalse(assertRoot + "#20", stanza1.containsKey("key3"));

        // Add a couple of properties
        args = new Args();
        args.put("key1", "value1");
        args.put("key2", 42);
        stanza1.update(args);

        // Make sure the properties showed up
        assertEquals(assertRoot + "#21", "value1", stanza1.get("key1"));
        assertEquals(assertRoot + "#22", "42", stanza1.get("key2"));
        assertFalse(assertRoot + "#23", stanza1.containsKey("key3"));

        // Update an existing property
        args = new Args();
        args.put("key1", "value2");
        stanza1.update(args);

        // Make sure the updated property shows up (and no other changes).
        assertEquals(assertRoot + "#24", "value2", stanza1.get("key1"));
        assertEquals(assertRoot + "#25", "42", stanza1.get("key2"));
        assertFalse(assertRoot + "#26", stanza1.containsKey("key3"));

        // Delete the stanzas
        stanzas.remove("stanza3");
        assertEquals(assertRoot + "#27", 2, stanzas.size());
        assertTrue(assertRoot + "#28", stanzas.containsKey("stanza1"));
        assertTrue(assertRoot + "#29", stanzas.containsKey("stanza2"));
        assertFalse(assertRoot + "#30", stanzas.containsKey("stanza3"));

        stanzas.remove("stanza2");
        assertEquals(assertRoot + "#31", 1, stanzas.size());
        assertTrue(assertRoot + "#32", stanzas.containsKey("stanza1"));
        assertFalse(assertRoot + "#33", stanzas.containsKey("stanza2"));
        assertFalse(assertRoot + "#34", stanzas.containsKey("stanza3"));

        stanzas.remove("stanza1");
        assertEquals(assertRoot + "#35", 0, stanzas.size());
        assertFalse(assertRoot + "#36", stanzas.containsKey("stanza1"));
        assertFalse(assertRoot + "#37", stanzas.containsKey("stanza2"));
        assertFalse(assertRoot + "#38", stanzas.containsKey("stanza3"));

        // Cleanup after ourselves
        removeApp(APP);
    }
}
