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
    private int tcpPort = -1;
    private TcpInput tcpInput = null;
    private String indexName;
    private Index index = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        indexName = createTemporaryName();
        index = service.getIndexes().create(indexName);

        tcpPort = findNextUnusedPort(10000);
        tcpInput = service.getInputs().create(
                String.valueOf(tcpPort),
                InputKind.Tcp,
                new Args("index", indexName));
    }

    @After
    public void tearDown() throws Exception {
        if (index != null && service.versionCompare("5.0") >= 0) {
            index.remove();
        }
        if (tcpInput != null) {
            tcpInput.remove();
        }
        
        super.tearDown();
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
            @Override
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == nEvents + 1;
            }
        });
    }
    
    // TODO: Move to InputTest once it has been ported over to the new suite.
    @Test
    public void testGetInputKindOfScript() {
        Input scriptInput = new Input(service, "data/inputs/script/$SPLUNK_HOME/etc/apps/myapp/bin/myscript.py");
        assertEquals(InputKind.Script, scriptInput.getKind());
        
        Input tcpRawInput = new Input(service, "data/inputs/tcp/raw/6666");
        assertEquals(InputKind.Tcp, tcpRawInput.getKind());
        
        Input tcpCookedInput = new Input(service, "data/inputs/tcp/cooked/6666");
        assertEquals(InputKind.TcpSplunk, tcpCookedInput.getKind());
        
        Input udpInput = new Input(service, "data/inputs/udp/6666");
        assertEquals(InputKind.Udp, udpInput.getKind());
        
        Input modularInput = new Input(service, "data/inputs/my_modular_input/input_name");
        assertEquals(InputKind.createFromRelativePath("my_modular_input"), modularInput.getKind());
    }
}
