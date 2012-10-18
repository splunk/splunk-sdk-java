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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest extends SDKTestCase {

    private String applicationName;
    private Application application;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        // We cannot straightforwardly delete confs,
        // so instead we create a temporary application,
        // reconnect using its namespace, then delete it
        // when we're done.
        applicationName = createTemporaryName();
        service.getApplications().create(applicationName);
        
        service.logout();
        
        ConnectionArgs applicationArgs = new ConnectionArgs();
        applicationArgs.putAll(connectionArgs);
        applicationArgs.put("sharing", "app");
        applicationArgs.put("owner", "nobody");
        applicationArgs.put("app", applicationName);
        service = Service.connect(applicationArgs);
        
        application = service.getApplications().get(applicationName);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        application.remove();
        service.app = null;
        
        super.tearDown();
    }

    @Test
    public void testStandardFilesExist() {
        ConfCollection confs = service.getConfs();
        assertTrue(confs.containsKey("eventtypes"));
        assertTrue(confs.containsKey("indexes"));
        assertTrue(confs.containsKey("inputs"));
        assertTrue(confs.containsKey("props"));
        assertTrue(confs.containsKey("transforms"));
        assertTrue(confs.containsKey("savedsearches"));

        for (Entity stanza : confs.get("indexes").values()) {
            assertNotNull(stanza);
            assertNotNull(stanza.getName());
            assertNotNull(stanza.getPath());
        }
    }

    @Test
    public void testCreateConfWorks() {
        ConfCollection confs = service.getConfs();
        String confName = createTemporaryName();

        assertFalse("New configuration name already used.", confs.containsKey(confName));
        EntityCollection<Entity> conf = confs.create(confName);
        assertTrue("New configuration doesn't show up after creation.", confs.containsKey(confName));
        assertEquals("New configuration is not empty.", 0, conf.size());
    }

    @Test
    public void testCreateAndDeleteStanzaWorks() {
        ConfCollection confs = service.getConfs();
        String confName = createTemporaryName();
        EntityCollection<Entity> conf = confs.create(confName);

        String stanzaName = createTemporaryName();
        assertFalse("Stanza already exists.", conf.containsKey(stanzaName));
        Entity stanza = conf.create(stanzaName);

        assertTrue("Stanza doesn't show up after creation.", conf.containsKey(stanzaName));
        assertEquals("Stanza contains something besides eai: and disabled attributes when first created.", 5, stanza.size());

        stanza.remove();
        conf.refresh();
        assertFalse("Stanza not deleted by remove()", conf.containsKey(stanzaName));
    }

    @Test
    public void testWriteToStanza() {
        ConfCollection confs = service.getConfs();
        String confName = createTemporaryName();
        EntityCollection<Entity> conf = confs.create(confName);
        String stanzaName = createTemporaryName();
        Entity stanza = conf.create(stanzaName);

        assertEquals("Stanza begins with only eai:* and disabled in it.", 5, stanza.size());
        Args args = new Args();
        args.put("test-key", "42");
        stanza.update(args);
        assertEquals("Stanza has right size after write.", 6, stanza.size());
        assertEquals("Read from key gives right value.", "42", stanza.get("test-key"));
    }
}
