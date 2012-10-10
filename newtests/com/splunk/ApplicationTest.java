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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.Collection;

public class ApplicationTest extends SDKTestCase {
    String applicationName;
    Application application;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        for (Application app : service.getApplications().values()) {
            if (app.getName().startsWith("delete-me")) {
                app.remove();
            }
        }

        applicationName = createTemporaryName();
        EntityCollection<Application> applications = service.getApplications();
        application = applications.create(applicationName);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        application.remove();
    }

    @Test
    public void testForEmptySetup() {
        // Newly created applications have no setup.
        ApplicationSetup applicationSetup = application.setup();
        assertNull(applicationSetup.getSetupXml());
    }

    @Test
    public void testForSetupPresent() {
        if (!hasApplicationCollection()) {
           return;
        }
        try {
            installApplicationFromCollection("has_setup_xml");
        } catch (Exception e) {
            fail(e.toString());
        }
        assertTrue(service.getApplications().containsKey("has_setup_xml"));
        Application applicationWithSetupXml = service.getApplications().get("has_setup_xml");
        ApplicationSetup applicationSetup = applicationWithSetupXml.setup();
        assertEquals("has_setup_xml", applicationSetup.getName());
        String xml = applicationSetup.getSetupXml();
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        Document parsedXml = null;
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
            parsedXml = docBuilder.parse(xmlStream);
        } catch (Exception e) {
            fail(e.toString());
        }
        parsedXml.getDocumentElement().normalize();
        assertEquals(parsedXml.getDocumentElement().getNodeName(), "SetupInfo");
        NodeList blocks = parsedXml.getDocumentElement().getElementsByTagName("block");
        assertEquals(1, blocks.getLength());
        Node block = blocks.item(0);
        assertEquals("block", block.getNodeName());
    }

    @Test
    public void testArchive() {
        ApplicationArchive archive = application.archive();
        assertEquals(applicationName, archive.getAppName());
        String path = archive.getFilePath();
        File archiveFile = new File(path);
        assertTrue(archiveFile.exists());
    }

    @Test
    public void testFields() {
        // Initially, should be empty.
        assertEquals(null, application.getAuthor());
        assertTrue(application.getCheckForUpdates());
        assertFalse(application.isConfigured());
        assertFalse(application.isVisible());
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
        application.setVisible(true);

        application.update();
        application.refresh();

        assertEquals(authorString, application.getAuthor());
        assertFalse(application.getCheckForUpdates());
        assertEquals(descriptionString, application.getDescription());
        assertEquals(labelString, application.getLabel());
        assertEquals(versionString, application.getVersion());
        assertTrue(application.isConfigured());
        assertTrue(application.isVisible());
    }

    @Test
    public void testUpdate() {
        ApplicationUpdate update = application.getUpdate();

    }



}
