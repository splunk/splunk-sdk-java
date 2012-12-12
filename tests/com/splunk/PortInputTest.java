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

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PortInputTest extends InputTest {
    private int tcpPort = -1;
    private TcpInput tcpInput = null;

    protected int findNextUnusedPort(int startingPort) {
        InputCollection inputs = service.getInputs();

        int port = startingPort;
        while (inputs.containsKey(String.valueOf(port))) {
            port++;
        }
        return port;
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        tcpPort = findNextUnusedPort(10000);
        while (true) {
            try {
                tcpInput = service.getInputs().create(
                        String.valueOf(tcpPort),
                        InputKind.Tcp,
                        new Args("index", "main"));
                break;
            } catch (HttpException he) {
                if (he.getStatus() == 400) {
                    tcpPort += 1;
                }
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        if (tcpInput != null) {
            tcpInput.remove();
        }

        super.tearDown();
    }

    @Test
    public void testRestrictToHostCausesError() {
        try {
            tcpInput.update(new Args("restrictToHost", "boris"));
            fail("Expected an UnsupportedOperationException.");
        }
        catch (UnsupportedOperationException e) {}
    }

    @Test
    public void testGetPort() {
        assertEquals(tcpPort, tcpInput.getPort());
    }
}
