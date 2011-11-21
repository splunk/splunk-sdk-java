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

import java.util.Map;
import org.junit.*;

public class ConfTest extends SplunkTestCase {
    final static String APP_NAME = "sdk-tests";
    final static String APP_NAMESPACE = "nobody/" + APP_NAME;

    public ConfTest() { super(); }

    @Test public void testConfs() {
        Service service = connect();

        ConfCollection confs = service.getConfs();

        // Make sure the collection contains some of the expected entries.
        assertTrue(confs.containsKey("eventtypes"));
        assertTrue(confs.containsKey("searchbnf"));
        assertTrue(confs.containsKey("indexes"));
        assertTrue(confs.containsKey("inputs"));
        assertTrue(confs.containsKey("props"));
        assertTrue(confs.containsKey("transforms"));
        assertTrue(confs.containsKey("savedsearches"));

        // Iterate over the confs just to make sure we can read them
        for (EntityCollection<Entity> conf : confs.values()) {
            conf.getName();
            conf.getTitle();
            conf.getPath();
            for (Entity stanza : conf.values()) {
                stanza.getName();
                stanza.getTitle();
                stanza.getPath();
                stanza.getContent();
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
        createApp(APP_NAME);
        service = connect();
        assertTrue(service.getApplications().containsKey(APP_NAME));

        // Create an app specific service instance
        Args args = new Args(command.opts);
        args.put("namespace", APP_NAMESPACE);
        service = Service.connect(args);

        ConfCollection confs = service.getConfs();
        assertFalse(confs.containsKey("testconf"));
        confs.create("testconf");
        assertTrue(confs.containsKey("testconf"));

        EntityCollection<Entity> stanzas = confs.get("testconf");
        assertEquals(stanzas.size(), 0);

        stanzas.create("stanza1");
        stanzas.create("stanza2");
        stanzas.create("stanza3");
        assertEquals(stanzas.size(), 3);
        assertTrue(stanzas.containsKey("stanza1"));
        assertTrue(stanzas.containsKey("stanza2"));
        assertTrue(stanzas.containsKey("stanza3"));

        // Grab the new stanza and check its content
        Entity stanza1 = stanzas.get("stanza1");
        Map content = stanza1.getContent();
        assertEquals(content.get("eai:userName"), "nobody");
        assertEquals(content.get("eai:appName"), APP_NAME);
        assertFalse(content.containsKey("key1"));
        assertFalse(content.containsKey("key2"));
        assertFalse(content.containsKey("key3"));

        // Add a couple of properties
        args = new Args();
        args.put("key1", "value1");
        args.put("key2", 42);
        stanza1.update(args);

        // Make sure the properties showed up
        content = stanza1.getContent();
        assertEquals(content.get("key1"), "value1");
        assertEquals(content.get("key2"), "42");
        assertFalse(content.containsKey("key3"));

        // Update an existing property
        args = new Args();
        args.put("key1", "value2");
        stanza1.update(args);

        // Make sure the updated property shows up (and no other changes).
        content = stanza1.getContent();
        assertEquals(content.get("key1"), "value2");
        assertEquals(content.get("key2"), "42");
        assertFalse(content.containsKey("key3"));

        // Delete the stanzas
        stanzas.remove("stanza3");
        assertEquals(stanzas.size(), 2);
        assertTrue(stanzas.containsKey("stanza1"));
        assertTrue(stanzas.containsKey("stanza2"));
        assertFalse(stanzas.containsKey("stanza3"));

        stanzas.remove("stanza2");
        assertEquals(stanzas.size(), 1);
        assertTrue(stanzas.containsKey("stanza1"));
        assertFalse(stanzas.containsKey("stanza2"));
        assertFalse(stanzas.containsKey("stanza3"));

        stanzas.remove("stanza1");
        assertEquals(stanzas.size(), 0);
        assertFalse(stanzas.containsKey("stanza1"));
        assertFalse(stanzas.containsKey("stanza2"));
        assertFalse(stanzas.containsKey("stanza3"));

        // Cleanup after ourselves
        removeApp(APP_NAME);
    }
}
