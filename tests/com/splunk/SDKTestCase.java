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

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Base test case for SDK test suite.
 *
 * TestCase does the following actions in the various test contexts:
 *
 * @@Before:
 *   - Read ~/.splunkrc to get host, port, user, and password to connect with.
 *   - Create a Service object connected to the Splunk instance.
 *
 * @@After:
 *   - Logout the Service object.
 */
public abstract class SDKTestCase extends TestCase {
    protected static final boolean WORKAROUND_KNOWN_BUGS = true;
    
    protected static Service service;
    protected List<String> installedApps;

    protected Command command;

    public void connect() {
        if (service != null) {
            service.login(service.username, service.password);
        } else {
            service = Service.connect(command.opts);
        }
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        command = Command.splunk();
        connect();
        if (restartRequired()) {
            System.out.println(
                    "WARNING: Splunk was already in a state requiring a " +
                    "restart prior to running this test. Trying to recover...");
            splunkRestart();
        }
        installedApps = new ArrayList<String>();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        if (restartRequired()) {
            fail("Test left Splunk in a state that required restart.");
        }

        // Cannot delete applications in Splunk 4.2.
        if (service.versionIsAtLeast("4.3")) {
            // We delete the apps we installed for capabilities after checking
            // for restarts that might be required, since deleting an app
            // triggers a restart (but one that we can safely ignore).
            for (String applicationName : installedApps) {
                service.getApplications().remove(applicationName);
                clearRestartMessage();
            }
        }

        super.tearDown();
    }
    
    // === Temporary Names ===

    protected static String createTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        return dateFormat.format(new Date());
    }

    protected static String createTemporaryName() {
        return "delete-me-" + UUID.randomUUID().toString();
    }
    
    // === Asserts ===

    public static abstract class EventuallyTrueBehavior {
        public int tries;
        public int pauseTime;

        {
            tries = 10;
            pauseTime = 1000;
        }

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
        fail(behavior.timeoutMessage);
        return false;
    }
    
    // === Test Data Installation ===

    public boolean hasTestData() {
        String collectionName = "sdk-app-collection";
        return service.getApplications().containsKey("sdk-app-collection");
    }

    public void installApplicationFromTestData(String applicationName) {
        String collectionName = "sdk-app-collection";
        if (!service.getApplications().containsKey("sdk-app-collection")) {
            throw new TestDataNotInstalledException();
        }

        String splunkHome = service.getSettings().getSplunkHome();

        // This is the filename separator sequence for splunkd, not
        // the Splunk SDK. Therefore we can't just use File.separator here.
        String separator;
        if (splunkHome.contains("/") && splunkHome.contains("\\")) {
            // Windows - allows mixed paths
            separator = "\\";
        } else if (splunkHome.contains("/")) {
            // Unix or Mac OS X
            separator = "/";
        } else if (splunkHome.contains("\\")) {
            // Windows
            separator = "\\";
        } else {
            throw new RuntimeException(
                    "Couldn't determine what the path separator was " +
                    "for splunkd.");
        }

        String[] pathComponents = {splunkHome, "etc", "apps",
                collectionName, "build", applicationName + ".tar"};
        String appPath = Util.join(separator, pathComponents);

        Args args = new Args();
        args.put("name", appPath);
        args.put("update", "1");
        service.post("apps/appinstall", args);
        
        installedApps.add(applicationName);
    }
    
    // === Restarts ===

    public void clearRestartMessage() {
        final MessageCollection messages = service.getMessages();
        if (messages.containsKey("restart_required")) {
            messages.remove("restart_required");
            assertEventuallyTrue(new EventuallyTrueBehavior() {
                @Override public boolean predicate() {
                    messages.refresh();
                    return !messages.containsKey("restart_required");
                }
            });
        }
    }

    public boolean restartRequired() {
        for (Message message : service.getMessages().values()) {
            if (message.containsKey("restart_required")) {
                return true;
            }
        }
        return false;
    }
    
    private void markAsRestartRequired() {
        service.getMessages().create(
                "restart_required",
                "Unit test called markAsRestartRequired().");
    }

    public void splunkRestart() {
        if (!restartRequired()) {
            fail("Asked to restart Splunk when no restart was required.");
        }
        uncheckedSplunkRestart();
    }

    public void uncheckedSplunkRestart() {
        ResponseMessage response = service.restart();
        if (response.getStatus() != 200) {
            fail("Restart command failed: " + response.getContent());
        }
        
        // (This status will be cleared when Splunk actually restarts.)
        markAsRestartRequired();

        // Wait for splunkd to come back up fresh.
        // (And update 'service'.)
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            {
                tries = 20;
                pauseTime = 1000;
            }
            
            @Override
            public boolean predicate() {
                try {
                    connect();
                    return !restartRequired();
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }
    
    // === Misc ===
    
    protected static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e) {}
    }

    protected int findNextUnusedPort(int startingPort) {
        InputCollection inputs = service.getInputs();
        
        int port = startingPort;
        while (inputs.containsKey(String.valueOf(port))) {
            port++;
        }
        return port;
    }
    
    protected static boolean contains(String[] array, String value) {
        return Arrays.asList(array).contains(value);
    }
    
    protected static void assertEquals(String[] a1, String[] a2) {
        assertEquals(Arrays.asList(a1), Arrays.asList(a2));
    }

    protected static String locateSystemLog() {
        final String filename;
        String osName = service.getInfo().getOsName();
        if (osName.equals("Windows"))
            filename = "C:\\Windows\\WindowsUpdate.log";
        else if (osName.equals("Linux"))
            filename = "/var/log/syslog";
        else if (osName.equals("Darwin")) {
            filename = "/var/log/system.log";
        } else {
            throw new RuntimeException("OS " + osName + " not recognized");
        }
        return filename;
    }
    
    protected static String joinServerPath(String[] components) {
        String separator;
        String osName = service.getInfo().getOsName();
        if (osName.equals("Windows"))
            separator = "\\";
        else if (osName.equals("Linux"))
            separator = "/";
        else if (osName.equals("Darwin")) {
            separator = "/";
        } else {
            throw new RuntimeException("OS " + osName + " not recognized");
        }
        
        return Util.join(separator, components);
    }
}
