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

public class SettingsTest extends SplunkTestCase {
    final static String assertRoot = "Settings assert: ";

    @Test public void testSettings() throws Exception {
        Service service = connect();
        Settings settings = service.getSettings();
        settings.getSplunkDB();
        settings.getSplunkHome();
        settings.getEnableSplunkWebSSL();
        settings.getHost();
        settings.getHttpPort();
        settings.getMgmtPort();
        settings.getMinFreeSpace();
        settings.getPass4SymmKey();
        settings.getServerName();
        settings.getSessionTimeout();
        settings.getStartWebServer();
        settings.getTrustedIP();

        // set aside original settings
        String originalTimeout = settings.getSessionTimeout();
        boolean originalSSL = settings.getEnableSplunkWebSSL();
        String originalHost = settings.getHost();
        int originalHttpPort = settings.getHttpPort();
        int originalMinSpace = settings.getMinFreeSpace();
        //int originalMgmtPort = settings.getMgmtPort();
        String originalServerName = settings.getServerName();
        boolean originalStartWeb = settings.getStartWebServer();

        // test update
        settings.setEnableSplunkWebSSL(!originalSSL);
        settings.setHost("sdk-host");
        settings.setHttpPort(8001);
        settings.setMinimumFreeSpace(originalMinSpace-100);
        //settings.setMgmtHostPort(originalMgmtPort+1);
        settings.setServerName("sdk-test-name");
        settings.setSessionTimeout("2h");
        //settings.setStartWebServer(!originalStartWeb);
        settings.update();

        // changing ports require a restart
        splunkRestart();
        service = connect();
        settings = service.getSettings();

        assertEquals(assertRoot + "#1", !originalSSL,
            settings.getEnableSplunkWebSSL());
        assertEquals(assertRoot + "#2", "sdk-host", settings.getHost());
        assertEquals(assertRoot + "#3", 8001, settings.getHttpPort());
        assertEquals(assertRoot + "#4", originalMinSpace-100,
            settings.getMinFreeSpace());
        assertEquals(assertRoot + "#5", "sdk-test-name",
            settings.getServerName());
        assertEquals(assertRoot + "#6", "2h", settings.getSessionTimeout());
        //assertEquals(settings.getStartWebServer(), !originalStartWeb);

        // restore original
        settings.setEnableSplunkWebSSL(originalSSL);
        settings.setHost(originalHost);
        settings.setHttpPort(originalHttpPort);
        settings.setMinimumFreeSpace(originalMinSpace);
        settings.setServerName(originalServerName);
        settings.setSessionTimeout(originalTimeout);
        settings.setStartWebServer(originalStartWeb);
        settings.update();

        // changing ports require a restart
        splunkRestart();
        service = connect();
        settings = service.getSettings();

        assertEquals(assertRoot + "#7", originalSSL,
            settings.getEnableSplunkWebSSL());
        assertEquals(assertRoot + "#8", originalHost, settings.getHost());
        assertEquals(assertRoot + "#9", originalHttpPort,
            settings.getHttpPort());
        assertEquals(assertRoot + "#10", originalMinSpace,
            settings.getMinFreeSpace());
        assertEquals(assertRoot + "#11", originalServerName,
            settings.getServerName());
        assertEquals(assertRoot + "#12", originalTimeout,
            settings.getSessionTimeout());
        assertEquals(assertRoot + "#13", originalStartWeb,
            settings.getStartWebServer());

    }
}
