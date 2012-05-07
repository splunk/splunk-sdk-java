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

import java.net.Socket;

public class ApplicationTest extends SplunkTestCase {

    private Service cleanApp(String appName, Service service) throws Exception {
        splunkRestart();
        service = connect();
        EntityCollection<Application> apps = service.getApplications();
        apps.remove(appName);
        splunkRestart();
        return connect();
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
        for (Application app: apps.values()) {
            try {
                ApplicationSetup applicationSetup = app.setup();
                applicationSetup.getSetupXml();
            } catch (Exception e) {
                // silent exception, if setup doesn't exist, exception occurs
            }
            app.archive();
            app.getAuthor();
            app.getCheckForUpdates();
            app.getDescription();
            app.getLabel();
            app.getVersion();
            app.isConfigured();
            app.isManageable();
            app.isVisible();
            app.stateChangeRequiresRestart();
            ApplicationUpdate applicationUpdate = app.getUpdate();
            applicationUpdate.getChecksum();
            applicationUpdate.getChecksumType();
            applicationUpdate.getHomepage();
            applicationUpdate.getSize();
            applicationUpdate.getUpdateName();
            applicationUpdate.getAppUrl();
            applicationUpdate.getVersion();
            applicationUpdate.isImplicitIdRequired();
        }

        if (apps.containsKey("sdk-tests")) {
            service = cleanApp("sdk-tests", service);
        }

        apps = service.getApplications();
        assertEquals(false, apps.containsKey("sdk-tests"));

        Args createArgs = new Args();
        createArgs.put("author", "me");
        // 4.2.4+ only
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

        ApplicationUpdate appUpdate = app.getUpdate();
        assertTrue(appUpdate.containsKey("eai:acl"));

        service = cleanApp("sdk-tests", service);
        apps = service.getApplications();
        assertEquals(false, apps.containsKey("sdk-tests"));
    }
}
