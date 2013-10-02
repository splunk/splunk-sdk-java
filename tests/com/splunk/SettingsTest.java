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

public class SettingsTest extends SDKTestCase {

    @Test
    public void testGettersThrowNoExceptions() {
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
    }
    
    @Test
    public void testHttpPortSetter() throws Exception {
        Settings settings = service.getSettings();

        int originalHttpPort = settings.getHttpPort();
        if (!isPortInUse(originalHttpPort)) {
            // Try to clean up from weird state where splunkweb isn't running
            System.out.println(
                    "WARNING: splunkweb seems to be down. " +
                    "Trying to recover...");
            uncheckedSplunkRestart();
            waitForSplunkwebUp(originalHttpPort);
        }
        assertTrue(isPortInUse(originalHttpPort));

        int newPort = originalHttpPort + 1;
        while (isPortInUse(newPort)) {
            newPort++;
        }
        changeHttpPort(newPort);
        assertTrue(isPortInUse(newPort));
        assertFalse(isPortInUse(originalHttpPort));
        
        changeHttpPort(originalHttpPort);
        assertTrue(isPortInUse(originalHttpPort));
        assertFalse(isPortInUse(newPort));
    }

    private void changeHttpPort(int newHttpPort) {
        Settings settings = service.getSettings();
        settings.setHttpPort(newHttpPort);
        settings.update();
        
        // If you change the splunkweb port, a new splunkweb instance
        // will be created on the new port. However the old splunkweb instance
        // will remain until you do a full restart.
        splunkRestart();
        waitForSplunkwebUp(newHttpPort);
        
        settings = service.getSettings();
        assertEquals(newHttpPort, settings.getHttpPort());
    }
    
    private void waitForSplunkwebUp(final int httpPort) {
        // Wait for splunkweb to come back up
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            {
                tries = 20;
                pauseTime = 1000;
            }
            
            public boolean predicate() {
                return isPortInUse(httpPort);
            }
        });
    }

    @Test
    public void testMangementPort() throws Exception {
        Settings settings = service.getSettings();
        int managementPort = settings.getMgmtPort();
        
        settings.setMgmtPort(29111);
        settings.update();
        settings.refresh();
        
        assertEquals(29111, settings.getMgmtPort());
        
        settings.setMgmtPort(managementPort);
        settings.update();
        splunkRestart();
    }
    
    @Test
    public void testSymmKey() throws Exception {
        Settings settings = service.getSettings();
        String key = settings.getPass4SymmKey();
        
        settings.setPasswordSymmKey(key + "_foo");
        settings.update();
        settings.refresh();
        
        assertEquals(key + "_foo", settings.getPass4SymmKey());
        
        settings.setPasswordSymmKey(key);
        settings.update();
        splunkRestart();
    }
    
    @Test
    public void testOtherSetters() throws Exception {
        Settings settings = service.getSettings();

        // Save original settings
        String originalTimeout = settings.getSessionTimeout();
        boolean originalSSL = settings.getEnableSplunkWebSSL();
        String originalHost = settings.getHost();
        int originalMinSpace = settings.getMinFreeSpace();
        //int originalMgmtPort = settings.getMgmtPort();
        String originalServerName = settings.getServerName();
        boolean originalStartWeb = settings.getStartWebServer();

        // Update
        settings.setEnableSplunkWebSSL(!originalSSL);
        settings.setHost("sdk-host");
        settings.setMinimumFreeSpace(originalMinSpace-100);
        //settings.setMgmtHostPort(originalMgmtPort+1);
        settings.setServerName("sdk-test-name");
        settings.setSessionTimeout("2h");
        //settings.setStartWebServer(!originalStartWeb);
        settings.update();
        
        clearRestartMessage();

        assertEquals(!originalSSL, settings.getEnableSplunkWebSSL());
        assertEquals("sdk-host", settings.getHost());
        assertEquals(originalMinSpace-100, settings.getMinFreeSpace());
        assertEquals("sdk-test-name", settings.getServerName());
        assertEquals("2h", settings.getSessionTimeout());
        //assertEquals(settings.getStartWebServer(), !originalStartWeb);

        // Restore
        settings.setEnableSplunkWebSSL(originalSSL);
        settings.setHost(originalHost);
        settings.setMinimumFreeSpace(originalMinSpace);
        settings.setServerName(originalServerName);
        settings.setSessionTimeout(originalTimeout);
        settings.setStartWebServer(originalStartWeb);
        settings.update();

        clearRestartMessage();

        assertEquals(originalSSL, settings.getEnableSplunkWebSSL());
        assertEquals(originalHost, settings.getHost());
        assertEquals(originalMinSpace, settings.getMinFreeSpace());
        assertEquals(originalServerName, settings.getServerName());
        assertEquals(originalTimeout, settings.getSessionTimeout());
        assertEquals(originalStartWeb, settings.getStartWebServer());
    }
}
