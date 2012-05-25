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

    final static String assertRoot = "Application assert: ";

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
            app.getRefresh();
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
        assertFalse(assertRoot + "#1", apps.containsKey("sdk-tests"));

        Args createArgs = new Args();
        createArgs.put("author", "me");
        if (service.versionCompare("4.2.4") >= 0) {
            createArgs.put("configured", false);
        }
        createArgs.put("description", "this is a description");
        createArgs.put("label", "SDKTEST");
        createArgs.put("manageable", false);
        createArgs.put("template", "barebones");
        createArgs.put("visible", false);
        apps.create("sdk-tests", createArgs);
        assertTrue(assertRoot + "#2", apps.containsKey("sdk-tests"));
        Application app = apps.get("sdk-tests");

        app.getCheckForUpdates();
        assertEquals(assertRoot + "#3", "SDKTEST", app.getLabel());
        assertEquals(assertRoot + "#4", "me", app.getAuthor());
        assertFalse(assertRoot + "#5", app.isConfigured());
        assertFalse(assertRoot + "#6", app.isManageable());
        assertFalse(assertRoot + "#7", app.isVisible());

        // update the app
        app.setAuthor("not me");
        app.setDescription("new description");
        app.setLabel("new label");
        app.setVisible(false);
        app.setManageable(false);
        app.setVersion("5.0.0");
        app.update();

        // check to see if args took.
        assertEquals(assertRoot + "#8", "not me", app.getAuthor());
        assertEquals(
            assertRoot + "#9", "new description", app.getDescription());
        assertEquals(assertRoot + "#10", "new label", app.getLabel());
        assertFalse(assertRoot + "#11", app.isVisible());
        assertEquals(assertRoot + "#12", "5.0.0", app.getVersion());

        // archive (package) the application
        ApplicationArchive appArchive = app.archive();
        assertTrue(assertRoot + "#13", appArchive.getAppName().length() > 0);
        assertTrue(assertRoot + "#14", appArchive.getFilePath().length() > 0);
        assertTrue(assertRoot + "#15", appArchive.getUrl().length() > 0);

        ApplicationUpdate appUpdate = app.getUpdate();
        assertTrue(assertRoot + "#16", appUpdate.containsKey("eai:acl"));

        service = cleanApp("sdk-tests", service);
        apps = service.getApplications();
        assertFalse(assertRoot + "#17", apps.containsKey("sdk-tests"));
    }
}
