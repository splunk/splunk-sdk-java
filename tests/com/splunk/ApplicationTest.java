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

package com.splunk.sdk.tests.com.splunk;

import com.splunk.*;
import com.splunk.sdk.Program;
import com.splunk.Service;

import java.io.IOException;

import junit.framework.TestCase;
import junit.framework.Assert;

import org.junit.*;

public class ApplicationTest extends TestCase {
    Program program = new Program();

    public ApplicationTest() {}

    Service connect() throws IOException {
        return new Service(
            program.host, program.port, program.scheme)
                .login(program.username, program.password);
    }

    @Before public void setUp() {
        this.program.init(); // Pick up .splunkrc settings
    }

    // Nota Bene: deleting an app, then creating one requires a splunk reboot in between.
    @Test public void testApps() throws Exception {
        Service service = connect();

        EntityCollection<Application> apps = service.getApplications();

        if (apps.containsKey("sdk-tests")) {
            apps.remove("sdk-tests");
        }

        Assert.assertEquals(false, apps.containsKey("sdk-tests"));

        apps.create("sdk-tests");
        Assert.assertEquals(true, apps.containsKey("sdk-tests"));
        Application app = apps.get("sdk-tests");

        app.getCheckForUpdates();
        app.getLabel();
        app.getVersion();
        app.isConfigured();
        app.isManageable();
        app.isVisible();

        // UNDONE: (llike python tests) are we exposing 'author' in AtomObjects
        //Entity app = apps.get("sdk-tests");
        //Assert.assertFalse(app.getContent().get("author").equals("Splunk"));
        //Args map = new Args();
        //map.put("author", "Splunk");
        //app.update(map);
        //Assert.assertTrue(app.getContent().get("author").equals("Splunk"));

        apps.remove("sdk-tests");
        Assert.assertEquals(false, apps.containsKey("sdk-tests"));
    }
}