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

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    
    protected static ConnectionArgs connectionArgs;
    protected static Service service;
    protected List<String> installedApps;

    /**
     * @return The path to .splunkrc in the user's home directory.
     */
    private static String getSplunkrcPath() {
        String homePath = System.getProperty("user.home");
        return homePath + File.separator + ".splunkrc";
    }

    /**
     * Read a stream attached to a .splunkrc file into a {@code ConnectionArgs}
     * (which is a subclass of {@code Map<String, Object>}).
     *
     * @param stream Stream attached to .splunkrc (usually a {@code FileReader}).
     * @return A ConnectionArgs object with the keys and values set from .splunkrc.
     */
    private static ConnectionArgs readSplunkrc(InputStreamReader stream) {
        BufferedReader bufferedStream = new BufferedReader(stream);
        ConnectionArgs args = new ConnectionArgs();
        try {
            String line = bufferedStream.readLine();
            while (line != null) {
                args.handleLine(line);
                line = bufferedStream.readLine();
            }
        } catch (Exception e) {
            fail(e.toString());
        } finally {
            try {
                stream.close();
            } catch (IOException e) {}
        }
        return args;
    }

    public void connect() {
        if (service != null) {
            service.login(service.username, service.password);
        } else {
            String splunkrcPath = getSplunkrcPath();
            FileReader splunkrcReader;
            try {
                splunkrcReader = new FileReader(splunkrcPath);
            } catch (FileNotFoundException e) {
                fail("Could not find .splunkrc at " + splunkrcPath);
                return;
            }
            connectionArgs = readSplunkrc(splunkrcReader);

            service = Service.connect(connectionArgs);
        }
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        connect();
        if (restartRequired()) {
            fail("Splunk was in a state requiring restart. Cowardly refusing to start.");
        }
        installedApps = new ArrayList<String>();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        if (restartRequired()) {
            fail("Test left Splunk in a state that required restart.");
        }
        // We delete the apps we installed for capabilities after checking
        // for restarts that might be required, since deleting an app
        // triggers a restart (but one that we can safely ignore).
        for (String applicationName : installedApps) {
            service.getApplications().remove(applicationName);
            clearRestartMessage();
        }

        super.tearDown();
    }

    protected static String createTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    protected static String createTemporaryName() {
        UUID u = UUID.randomUUID();
        String name = "delete-me-" + u.toString();
        return name;
    }

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

    public boolean hasApplicationCollection() {
        return !service.getApplications().containsKey("sdk-app-collection");
    }

    public void installApplicationFromCollection(String applicationName) {
        String collectionName = "sdk-app-collection";
        if (hasApplicationCollection()) {
            throw new MissingAppCollectionException();
        }

        String splunkHome = service.getSettings().getSplunkHome();

        String separator;
        if (splunkHome.contains("/") && splunkHome.contains("\\")) {
            throw new NoSeparatorFindableInPathException();
        } else if (splunkHome.contains("/")) {
            separator = "/";
        } else if (splunkHome.contains("\\")) {
            separator = "\\";;
        } else {
            throw new NoSeparatorFindableInPathException();
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

    protected static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e) {}
    }

    public boolean restartRequired() {
        for (Message message : service.getMessages().values()) {
            if (message.containsKey("restart_required")) {
                return true;
            }
        }
        return false;
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

        // Wait for splunkd to go down.
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                try {
                    new Socket(service.getHost(), service.getPort()).close();
                    return false;
                } catch (Exception e) {
                    return true;
                }
            }
        });

        // Wait for splunkd to come back up.
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            {
                tries = 20;
                pauseTime = 1000;
            }
            
            @Override
            public boolean predicate() {
                try {
                    Service.connect(connectionArgs);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        });

        connect();
    }

    protected int findNextUnusedPort(int startingPort) {
        InputCollection inputs = service.getInputs();
        
        int port = startingPort;
        while (inputs.containsKey(String.valueOf(port))) {
            port++;
        }
        return port;
    }
}
