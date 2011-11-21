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
import org.junit.Before;

// Shared base class for Splunk SDK unit tests, contains common unit setup
// and a collection of helper functions.
public class SplunkTestCase extends TestCase {
    Command command;

    public SplunkTestCase() {}

    Service connect() {
        return Service.connect(command.opts);
    }

    // Create a fresh test app with the given name, delete the existing
    // test app and reboot Splunk if needed.
    void createApp(String name) {
        EntityCollection<Application> apps;
        
        Service service = connect();

        apps = service.getApplications();
        if (apps.containsKey(name)) {
            apps.remove(name);
            service = restart();
        }

        apps = service.getApplications();
        assertFalse(apps.containsKey(name));

        apps.create(name);
        service = restart();

        apps = service.getApplications();
        assertTrue(apps.containsKey(name));
    }

    // Remove the given app and reboot Splunk if needed.
    void removeApp(String name) {
        EntityCollection<Application> apps;

        Service service = connect();

        apps = service.getApplications();
        if (apps.containsKey(name)) {
            apps.remove(name);
            service = restart();
        }

        apps = service.getApplications();
        assertFalse(apps.containsKey(name));
    }

    // Restart Splunk and return an updated service instance.
    Service restart() {
        Service service = connect();
        service.restart();
        sleep(5000);
        int retry = 15;
        while (retry > 0) {
            try {
                return connect();
            }
            catch (Exception e) {
                sleep(2000);
            }
        }
        fail("Splunk service failed to restart");
        return null;
    }

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
    }

    void sleep(int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {}
    }
}
