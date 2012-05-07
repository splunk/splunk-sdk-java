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

    // returns -1, 0, 1 comparing left version string to right
    // for less than, equal to or greater than
    int versionCompare(String left, String right) {

        // short cut for equality.
        if (left.equals(right)) return 0;

        // if not the same, break down into individual digits for comparison.
        String[] leftDigits = left.split(".");
        String[] rightDigits = right.split(".");
        int i=0;

        for (; i<leftDigits.length; i++) {
            // No more right side, left side is bigger
            if (i == rightDigits.length) return 1;
            // left side smaller>?
            if (Integer.parseInt(leftDigits[i]) <
                Integer.parseInt(leftDigits[1])) {
                return -1;
            }
            // left side bigger?
            if (Integer.parseInt(leftDigits[i]) >
                    Integer.parseInt(leftDigits[1])) {
                return 1;
            }
        }
        // we got to the end of the left side, and not equal, right side
        // most be larger by having more digits.
        return -1;
    }

    // Wait for the given job to complete
    Job wait(Job job) {

        while (true) {
            try {
                if (!job.isDone()) {
                    try { Thread.sleep(2000); }
                    catch (InterruptedException e) {}
                    job.refresh();
                } else {
                    return job;
                }
            }
            catch (SplunkException splunkException) {
                if (splunkException.getCode() == SplunkException.JOB_NOTREADY) {
                    try { Thread.sleep(500); }
                    catch (Exception e) {}
                } else {
                    throw splunkException;
                }
            }
        }
    }
}
