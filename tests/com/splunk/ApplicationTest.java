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

import com.splunk.sdk.Command;

import junit.framework.TestCase;
import org.junit.*;

public class ApplicationTest extends TestCase {
    Command command;

    public ApplicationTest() {}

    Service connect() {
        return Service.connect(command.opts);
    }

    private Service waitForSplunk() throws Exception {
        // there is still a race condition here: if the restart takes more
        // than 5 seconds to percolate through splunk, apps will not be
        // reset
        int retry = 10;
        while (retry > 0) {
            Thread.sleep(5000); // 5 seconds
            retry = retry-1;
            try {
                return connect();
            }
            catch (Exception e) {
                // server not back yet
            }
        }
        fail("Splunk service did not restart");
        return null;
    }

    private Service cleanApp(String appName, Service service) throws Exception {
        service.restart();
        service = waitForSplunk();
        EntityCollection<Application> apps = service.getApplications();
        apps.remove(appName);
        return waitForSplunk();
    }

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
    }

    // Nota Bene: Splunk needs to be restarted whenever an app is deleted
    // (more precisely, needs to be restarted after an app is deleted, AND
    // you want to manipulate, or create a new one in its place).
    // This test assumes the worst case, and will restart splunk
    // before and after an application deletion -- to correct for invalid
    // splunk application state.
    @Test public void testApps() throws Exception {
        Service service = connect();

        EntityCollection<Application> apps = service.getApplications();

        if (apps.containsKey("sdk-tests")) {
            service = cleanApp("sdk-tests", service);
        }

        apps = service.getApplications();
        assertEquals(false, apps.containsKey("sdk-tests"));

        Args createArgs = new Args();
        createArgs.put("author", "me");
        // 4.2.4 only
        //createArgs.put("configured", false);
        createArgs.put("description", "this is a description");
        createArgs.put("label", "SDKTEST");
        createArgs.put("manageable", false);
        createArgs.put("template", "barebones");
        createArgs.put("visible", false);
        apps.create("sdk-tests", createArgs);
        assertEquals(true, apps.containsKey("sdk-tests"));
        Application app = apps.get("sdk-tests");

        app.getCheckForUpdates();
        assertEquals(app.getLabel(), "SDKTEST");
        assertEquals(app.getAuthor(), "me");
        assertFalse(app.isConfigured());
        assertFalse(app.isManageable());
        assertFalse(app.isVisible());

        Args updateArgs = new Args();
        updateArgs.put("version", "5.0.0");
        app.update(updateArgs);
        assertEquals(app.getVersion(), "5.0.0");

        // archive (package) the application
        ApplicationArchive appArchive = app.archive();
        assertTrue(appArchive.getAppName().length() > 0);
        assertTrue(appArchive.getFilePath().length() > 0);
        assertTrue(appArchive.getUrl().length() > 0);

        ApplicationSetup appSetup = app.setup();
        try {
            assertTrue(appSetup.getSetupXML().length() > 0);
        } catch (Exception e) {
            // silent exception, we expect a 500 error because the
            // setup.xml file will be missing
        }

        ApplicationUpdate appUpdate = app.update();
        assertTrue(appUpdate.getContent().containsKey("eai:acl"));

        service = cleanApp("sdk-tests", service);
        apps = service.getApplications();
        assertEquals(false, apps.containsKey("sdk-tests"));
    }
}
