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

    private void splunkRestart(int newPort) throws Exception {

        boolean restarted = false;

        Service service = connect();

        ResponseMessage response = service.restart();
        assertEquals(200, response.getStatus());

        // port sniff. expect connection ... then no connection ...
        // the connection. Max 3 minutes.

        int totalTime = 0;
        // server up, wait until socket no longer accepted.
        while (totalTime < (3*60*1000)) {
            try {
                Socket ServerSok = new Socket(service.getHost(),newPort);
			    ServerSok.close();
			    Thread.sleep(10); // 10 milliseconds
                totalTime += 10;
    		}
            catch (Exception e) {
                break;
		    }
        }

        // server down, wait until socket accepted.
        while (totalTime < (3*60*1000)) {
            try {
                Socket ServerSok = new Socket(service.getHost(),newPort);
			    ServerSok.close();
                break;

    		}
            catch (Exception e) {
			    Thread.sleep(10); // 10 milliseconds
                totalTime += 10;
		    }
        }

        while (totalTime < (3*60*1000)) {
            try {
                connect();
                restarted = true;
                break;
            }
            catch (Exception e) {
                // server not back yet
                Thread.sleep(100);
                totalTime += 10;
            }
        }
        assertTrue(restarted);
    }

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
        splunkRestart(8001);
        service = connect();
        settings = service.getSettings();

        assertEquals(settings.getEnableSplunkWebSSL(), !originalSSL);
        assertEquals(settings.getHost(), "sdk-host");
        assertEquals(settings.getHttpPort(), 8001);
        assertEquals(settings.getMinFreeSpace(), originalMinSpace-100);
        assertEquals(settings.getServerName(), "sdk-test-name");
        assertEquals(settings.getSessionTimeout(), "2h");
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
        splunkRestart(originalHttpPort);
        service = connect();
        settings = service.getSettings();

        assertEquals(settings.getEnableSplunkWebSSL(), originalSSL);
        assertEquals(settings.getHost(), originalHost);
        assertEquals(settings.getHttpPort(), originalHttpPort);
        assertEquals(settings.getMinFreeSpace(), originalMinSpace);
        assertEquals(settings.getServerName(), originalServerName);
        assertEquals(settings.getSessionTimeout(), originalTimeout);
        assertEquals(settings.getStartWebServer(), originalStartWeb);

    }
}
