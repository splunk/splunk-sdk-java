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
import java.net.Socket;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

// Shared base class for Splunk SDK unit tests, contains common unit setup
// and a collection of helper functions.
public class SplunkTestCase extends TestCase {
    protected String createTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    protected String createTemporaryName() {
        UUID u = UUID.randomUUID();
        String name = "delete-me-" + u.toString();
        return name;
    }

    Command command;

    public SplunkTestCase() {}

    Service connect() {
        return Service.connect(command.opts);
    }

    boolean contains(String[] array, String value) {
        for (int i = 0; i < array.length; ++i)
            if (array[i].equals(value)) return true;
        return false;
    }

    // Create a fresh test app with the given name, delete the existing
    // test app and reboot Splunk if needed.
    void createApp(String name) {
        EntityCollection<Application> apps;
        
        Service service = connect();

        apps = service.getApplications();
        if (apps.containsKey(name)) {
            apps.remove(name);
            splunkRestart();
            service = connect();
        }

        apps = service.getApplications();
        assertFalse(assertRoot + "#1", apps.containsKey(name));

        apps.create(name);
        splunkRestart();
        service = connect();

        apps = service.getApplications();
        assertTrue(assertRoot + "#2", apps.containsKey(name));
    }

    // Remove the given app and reboot Splunk if needed.
    void removeApp(String name) {
        EntityCollection<Application> apps;

        Service service = connect();

        apps = service.getApplications();
        if (apps.containsKey(name)) {
            apps.remove(name);
            splunkRestart();
            service = connect();
        }

        apps = service.getApplications();
        assertFalse(assertRoot + "#3", apps.containsKey(name));
    }

    @Before public void setUp() {
        command = Command.splunk(); // Pick up .splunkrc settings
    }

    void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e) {}
    }

    public void splunkRestart() {
        // If not specified, use 3 minutes (in milliseconds) as default
        // restart timeout.
        splunkRestart(3*60*1000);
    }

    public void splunkRestart(int millisecondTimeout) {

        boolean restarted = false;

        Service service = connect();

        ResponseMessage response = service.restart();
        assertEquals(assertRoot + "#4", 200, response.getStatus());

        // Sniff the management port. We expect the port to be up for a short
        // while, and then no conection

        int totalTime = 0;
        // Server is back up, wait until socket no longer accepted.
        while (totalTime < millisecondTimeout) {
            try {
                Socket socket = new Socket(service.getHost(),service.getPort());
			    socket.close();
			    sleep(10);
                totalTime += 10;
    		}
            catch (Exception e) {
                break;
		    }
        }

        // server down, wait until socket accepted.
        while (totalTime < millisecondTimeout) {
            try {
                Socket socket = new Socket(service.getHost(),service.getPort());
			    socket.close();
                break;

    		}
            catch (Exception e) {
                sleep(10);
                totalTime += 10;
		    }
        }

        while (totalTime < millisecondTimeout) {
            try {
                connect();
                restarted = true;
                break;
            }
            catch (Exception e) {
                sleep(100);
                totalTime += 100;
            }
        }
        assertTrue(assertRoot + "#5", restarted);
    }

    // Wait for the given job to be ready
    Job ready(Job job) {
        while (!job.isReady()) {
            sleep(10);
        }
        return job;
    }

    // Wait for the given job to complete
    Job wait(Job job) {

        while (!job.isDone()) {
            sleep(500);
        }
        return job;
    }

    public static abstract class EventuallyTrueBehavior {
        public int tries = 10;
        public int pauseTime = 1000;

        public String timeoutMessage = "Test timed out before true.";
        public abstract boolean predicate();
    }

    public static boolean assertEventuallyTrue(EventuallyTrueBehavior behavior) {
        int remainingTries = behavior.tries;
        while (remainingTries > 0) {
            boolean succeeded = behavior.predicate();
            if (succeeded) {
                return true;
            } else {
                remainingTries -= 1;
                try {
                    Thread.sleep(behavior.pauseTime);
                } catch (InterruptedException e) {}
            }
        }
        SplunkTestCase.fail(behavior.timeoutMessage);
        return false;
    }
}
