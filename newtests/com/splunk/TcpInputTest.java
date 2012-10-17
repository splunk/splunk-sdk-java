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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class TcpInputTest extends SDKTestCase {
    protected int tcpPort = -1;
    protected TcpInput tcpInput = null;
    protected String indexName;
    protected Index index = null;

    public int findNextUnusedTcpPort(int startingPort) {
        int port = startingPort;
        InputCollection inputs = service.getInputs();
        while (inputs.containsKey(String.valueOf(port))) {
            port += 1;
        }
        return port;
    }

    @Before public void setUp() throws Exception {
        super.setUp();
        indexName = createTemporaryName();
        index = service.getIndexes().create(indexName);

        tcpPort = findNextUnusedTcpPort(10000);
        Args args = new Args();
        args.add("index", indexName);
        tcpInput = service.getInputs().create(String.valueOf(tcpPort), InputKind.Tcp, args);
    }

    @After public void tearDown() throws Exception {
        super.tearDown();
        if (index != null && service.versionCompare("5.0") >= 0) {
            index.remove();
        }
        if (tcpPort != -1) {
            service.getInputs().get(String.valueOf(tcpPort)).remove();
        }
    }

    @Test
    public void testAttachAndWrite() {
        final int nEvents = index.getTotalEventCount();

        try {
            Socket socket = tcpInput.attach();
            PrintStream output = new PrintStream(socket.getOutputStream());
            output.print(createTimestamp() + " Boris the mad baboon!\r\n");
            output.flush();
            socket.close();
        } catch (IOException e) {
            fail("Got an IO exception in attaching.");
        }

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == nEvents + 1;
            }
        });
    }

    @Test
    public void testSubmit() {
        final int nEvents = index.getTotalEventCount();

        try {
            this.tcpInput.submit(createTimestamp() + " Boris the mad baboon!\r\n");
        } catch (IOException e) {
            fail("Got an IO exception in submit.");
        }

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == nEvents + 1;
            }
        });
    }

    @Test
    public void testAttachWith() {
        final int nEvents = index.getTotalEventCount();
        final Index index = this.index;

        try {
            this.tcpInput.attachWith(new ReceiverBehavior() {
                public void run(OutputStream stream) throws IOException {
                    String s = createTimestamp() + " Boris the mad baboon!\r\n";
                    stream.write(s.getBytes("UTF8"));
                }
            });
        } catch (IOException e) {
            fail("IOException in attachWith.");
        }

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == nEvents + 1;
            }
        });
    }
}
