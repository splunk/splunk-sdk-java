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

public class TcpInputTest extends SplunkTestCase {
    protected Service service;
    protected int tcpPort;
    protected TcpInput tcpInput = null;
    protected String indexName;
    protected Index index = null;

    public int findNextUnusedTcpPort(int startingPort) {
        int port = startingPort;
        while (this.service.getInputs().containsKey(String.valueOf(port))) {
            port += 1;
        }
        return port;
    }

    @Before
    public void setUp() {
        super.setUp();
        this.service = connect();

        this.indexName = temporaryName();
        this.index = service.getIndexes().create(indexName);

        this.tcpPort = findNextUnusedTcpPort(10000);
        Args args = new Args();
        args.add("index", indexName);
        this.tcpInput = service.getInputs().create(String.valueOf(this.tcpPort), InputKind.Tcp, args);
    }

    @After
    public void tearDown() {
        if (this.index != null && this.service.versionCompare("5.0") >= 0) {
            this.index.remove();
        }
        if (this.tcpInput != null) {
            this.tcpInput.remove();
        }
    }

    @Test
    public void testAttachAndWrite() {
        final int nEvents = index.getTotalEventCount();
        final Index index = this.index;

        try {
            Socket socket = this.tcpInput.attach();
            PrintStream output = new PrintStream(socket.getOutputStream());
            output.print(createTimestamp() + " Boris the mad baboon!\r\n");
            output.flush();
            socket.close();
        } catch (IOException e) {
            SplunkTestCase.fail("Got an IO exception in attaching.");
        }


        SplunkTestCase.assertEventuallyTrue(new EventuallyTrueBehavior() {
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == nEvents + 1;
            }
        });
    }

    @Test
    public void testSubmit() {
        final int nEvents = index.getTotalEventCount();
        final Index index = this.index;

        try {
            this.tcpInput.submit(createTimestamp() + " Boris the mad baboon!\r\n");
        } catch (IOException e) {
            SplunkTestCase.fail("Got an IO exception in submit.");
        }

        SplunkTestCase.assertEventuallyTrue(new EventuallyTrueBehavior() {
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
            this.tcpInput.attachWith(new TcpInput.ReceiverBehavior() {
                public void run(OutputStream stream) throws IOException {
                    String s = createTimestamp() + " Boris the mad baboon!\r\n";
                    stream.write(s.getBytes("UTF8"));
                }
            });
        } catch (IOException e) {
            SplunkTestCase.fail("IOException in attachWith.");
        }
    }
}
