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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.*;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Base test case for SDK test suite.
 *
 * TestCase does the following actions in the various test contexts:
 *
 * @@BeforeClass:
 *   - Read ~/.splunkrc to get host, port, user, and password to connect with.
 *   - Create a Service object connected to the Splunk instance.
 *
 * @@AfterClass:
 *   - Logout the Service object.
 */
public abstract class SDKTestCase extends TestCase {
    protected static ConnectionArgs connectionArgs;
    protected static Service connection;

    private static String getSplunkrcPath() {
        String homePath = System.getProperty("user.home");
        return homePath + File.separator + ".splunkrc";
    }

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
        String splunkrcPath = getSplunkrcPath();
        FileReader splunkrcReader;
        try {
            splunkrcReader = new FileReader(splunkrcPath);
        } catch (FileNotFoundException e) {
            fail("Could not find .splunkrc at " + splunkrcPath);
            return;
        }
        connectionArgs = readSplunkrc(splunkrcReader);

        connection = Service.connect(connectionArgs);
    }

    @Before
    @Override
    public void setUp() {
        try {
            super.setUp();
        } catch (Exception e) {
            fail(e.toString());
        }
        connect();
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

}
