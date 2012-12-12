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
    private Service appService;
    private String applicationName;

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

        // We cannot easily change the namespace of service,
        // so rather than fight with it, we create a new
        // service with the right namespace and work from it
        // instead. Then we'll return to service in tearDown
        // to clean up.

        // NOTE: If you ever have to put a restart into
        // the configuration tests, then you will need
        // to explicitly re-login appService, since it will
        // not be re-logged in by the call to restartSplunk.
        Args applicationArgs = new Args(command.opts);
        applicationArgs.put("sharing", "app");
        applicationArgs.put("owner", "nobody");
        applicationArgs.put("app", applicationName);
        appService = Service.connect(applicationArgs);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        appService.logout();
        
        final EntityCollection<Application> apps = service.getApplications();
        apps.remove(applicationName);
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return !apps.refresh().containsKey(applicationName);
            }
        });
        clearRestartMessage();
        
        super.tearDown();
    }

    @Test
    public void testStandardFilesExist() {
        ConfCollection confs = appService.getConfs();
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
    public void testCreateConf() {
        ConfCollection confs = appService.getConfs();
        String confName = createTemporaryName();

        assertFalse("New configuration name already used.", confs.containsKey(confName));
        EntityCollection<Entity> conf = confs.create(confName);
        assertTrue("New configuration doesn't show up after creation.", confs.containsKey(confName));
        assertEquals("New configuration is not empty.", 0, conf.size());
    }

    @Test
    public void testCreateAndDeleteStanza() {
        ConfCollection confs = appService.getConfs();
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
        ConfCollection confs = appService.getConfs();
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
