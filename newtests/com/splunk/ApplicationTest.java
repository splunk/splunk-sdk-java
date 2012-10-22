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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class ApplicationTest extends SDKTestCase {
    private String applicationName;
    private Application application;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        removeTestApplications();

        applicationName = createTemporaryName();
        application = service.getApplications().create(applicationName);
    }


    @After
    @Override
    public void tearDown() throws Exception {
        removeTestApplications();
        
        // Clear the restart message that deleting apps causes in splunkd.
        // It's fine to keep going despite it.
        clearRestartMessage();
        
        super.tearDown();
    }
    
    private void removeTestApplications() {
        final EntityCollection<Application> apps = service.getApplications();
        for (Application app : apps.values()) {
            final String appName = app.getName();
            if (appName.startsWith("delete-me")) {
                app.remove();
                assertEventuallyTrue(new EventuallyTrueBehavior() {
                    @Override
                    public boolean predicate() {
                        apps.refresh();
                        return !apps.containsKey(appName);
                    }
                });
            }
        }
    }

    @Test
    public void testForEmptySetup() {
        // Newly created applications have no setup.
        try {
            application.setup().getSetupXml();
            fail("Expected HTTP 500.");
        }
        catch (HttpException e) {
            assertEquals(500, e.getStatus());
            assertTrue(
                    e.getMessage().contains("does not exits") ||    // 4.3.2
                    e.getMessage().contains("does not exist"));     // 5.0rc5
        }
    }

    @Test
    public void testForSetupPresent() throws Exception {
        if (!hasApplicationCollection()) {
            return;
        }
        
        installApplicationFromCollection("has_setup_xml");
        assertTrue(service.getApplications().containsKey("has_setup_xml"));
        Application applicationWithSetupXml = service.getApplications().get("has_setup_xml");
        
        ApplicationSetup applicationSetup = applicationWithSetupXml.setup();
        assertEquals("has_setup_xml", applicationSetup.getName());
        
        String setupXml = applicationSetup.getSetupXml();
        Document parsedSetupXml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
            new ByteArrayInputStream(setupXml.getBytes()));
        parsedSetupXml.getDocumentElement().normalize();
        
        assertEquals(parsedSetupXml.getDocumentElement().getNodeName(), "SetupInfo");
        
        NodeList blocks = parsedSetupXml.getDocumentElement().getElementsByTagName("block");
        assertEquals(1, blocks.getLength());
        Node block = blocks.item(0);
        assertEquals("block", block.getNodeName());
    }

    @Test
    public void testArchive() {
        ApplicationArchive archive = application.archive();
        assertEquals(applicationName, archive.getAppName());
        assertTrue(new File(archive.getFilePath()).exists());
    }

    @Test
    public void testFields() {
        // Initially, should be empty.
        assertEquals(null, application.getAuthor());
        assertTrue(application.getCheckForUpdates());
        assertFalse(application.isConfigured());
        assertTrue(application.isVisible());
        assertFalse(application.stateChangeRequiresRestart());

        String authorString = "Boris the mad baboon";
        application.setAuthor(authorString);
        application.setCheckForUpdates(false);
        String descriptionString = "Meep the nudebranch!";
        application.setDescription(descriptionString);
        String labelString = "Hugga wugga";
        application.setLabel(labelString);
        String versionString = "VII";
        application.setVersion(versionString);
        application.setConfigured(true);
        application.setVisible(false);

        application.update();
        application.refresh();

        assertEquals(authorString, application.getAuthor());
        assertFalse(application.getCheckForUpdates());
        assertEquals(descriptionString, application.getDescription());
        assertEquals(labelString, application.getLabel());
        assertEquals(versionString, application.getVersion());
        assertTrue(application.isConfigured());
        assertFalse(application.isVisible());
    }

    @Test
    public void testUpdate() {
        // Set the version of gettingstarted to something small,
        // then wait for splunkd to pull its update information from splunkbase.
        
        Application gettingStarted = service.getApplications().get("gettingstarted");
        String originalVersion = gettingStarted.getVersion();
        try {
            // Decrease the app's version
            gettingStarted.setVersion("0.1");
            gettingStarted.update();
            
            // TODO: Is this really needed?
            //       If so, an explanation should be provided.
            uncheckedSplunkRestart();
            gettingStarted = service.getApplications().get("gettingstarted");
            
            // Wait until Splunk sees that an update for the app is available
            // NOTE: This typically takes about 15s
            final Application gettingStartedReference = gettingStarted;
            assertEventuallyTrue(new EventuallyTrueBehavior() {
                @Override
                public boolean predicate() {
                    return gettingStartedReference.getUpdate().getChecksum() != null;
                }
            });
            
            // Verify expected properties of the update
            ApplicationUpdate update = gettingStarted.getUpdate();
            assertEquals("f1a30efa896d9a2f7272420c2f999f03", update.getChecksum());
            assertEquals("md5", update.getChecksumType());
            assertEquals("https://splunkbase.splunk.com/apps/Getting+Started", update.getHomepage());
            assertEquals(804665, update.getSize());
            assertEquals("Getting Started", update.getUpdateName());
            assertEquals(
                    "https://splunkbase.splunk.com/api/apps:download/Getting+Started/1.0/gettingstarted.spl",
                    update.getAppUrl()
            );
            assertEquals("1.0", update.getVersion());
            assertTrue(update.isImplicitIdRequired());
        } finally {
            // Restore the app's original version
            gettingStarted.setVersion(originalVersion);
            gettingStarted.update();
        }
    }

    @Test
    public void testEmptyUpdate() {
        ApplicationUpdate update = application.getUpdate();
        assertNull(update.getChecksum());
        assertNull(update.getChecksumType());
        assertNull(update.getHomepage());
        assertEquals(-1, update.getSize());
        assertNull(update.getUpdateName());
        assertNull(update.getAppUrl());
        assertNull(update.getVersion());
        assertFalse(update.isImplicitIdRequired());
    }

    @Test
    public void testListApplications() {
        boolean found = false;
        for (Application app : service.getApplications().values()) {
            if (app.getName().equals(applicationName)) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testContains() {
        assertTrue(service.getApplications().containsKey(applicationName));
    }

}
