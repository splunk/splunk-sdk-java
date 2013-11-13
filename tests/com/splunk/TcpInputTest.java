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

        // WORKAROUND (SPL-75101): Removing TCP inputs doesn't work on Windows in Splunk 6.0.0. The HTTP call
        // hangs forever, and, though the input vanishes from the REST API, the port is never unbound and cannot be
        // reused until Splunk restarts.
        if (service.versionCompare("6.0.0") != 0 || !service.getInfo().getOsName().equals("Windows")) {
            if (tcpInput != null) {
                tcpInput.remove();
            }
        }

        super.tearDown();
    }

    @Test
    public void testConnectionList() throws IOException {
        // WORKAROUND (SPL-74835): Connections to the TCP input newly created in setUp will not be listed in
        // Splunk 6.0.0 until after the next restart of splunkd.
        if (service.versionCompare("6.0.0") == 0) {
            uncheckedSplunkRestart();
        }

        final Socket socket = tcpInput.attach();
        assertTrue(socket.isConnected());


        assertEventuallyTrue(new EventuallyTrueBehavior() {
            { tries = 15; }

            @Override
            public boolean predicate() {
                TcpConnections connections = tcpInput.connections();
                String connection = connections.getConnection();
                String servername = connections.getServername();
                
                if (connection == null && servername == null) {
                    return false;
                }
                
                try {
                    socket.close();
                } catch (IOException e) {
                    fail("Should not throw!");
                } 
                
                return true;
            }
        });
    }

    @Test
    public void testGetters() {
        assertNotNull(tcpInput.getGroup());
        assertNull(tcpInput.getRestrictToHost());
       
        tcpInput.setDisabled(false);
        tcpInput.update();
    }
    
    @Test
    public void testAttachAndWrite() throws IOException {
        writeEventsTo(tcpInput.attach());
        writeEventsTo(service.open(tcpInput.getPort()));
    }
    
    private void writeEventsTo(Socket socket) throws IOException {
        final int nEvents = index.getTotalEventCount();

        PrintStream output = new PrintStream(socket.getOutputStream());
        output.print(createTimestamp() + " Boris the mad baboon!\r\n");
        output.flush();
        socket.close();

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            { tries = 50; }
            
            @Override
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == nEvents + 1;
            }
        });
    }

    @Test
    public void testSubmit() throws IOException {
        final int nEvents = index.getTotalEventCount();

        this.tcpInput.submit(createTimestamp() + " Boris the mad baboon!\r\n");

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                index.refresh();
                return index.getTotalEventCount() == nEvents + 1;
            }
        });
    }

    @Test
    public void testAttachWith() throws IOException {
        final int nEvents = index.getTotalEventCount();
        final Index index = this.index;

        this.tcpInput.attachWith(new ReceiverBehavior() {
            public void run(OutputStream stream) throws IOException {
                String s = createTimestamp() + " Boris the mad baboon!\r\n";
                stream.write(s.getBytes("UTF8"));
            }
        });

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
        
        Input scriptInputWin = new Input(service, "data/inputs/script/$SPLUNK_HOME\\etc\\apps\\myapp\\bin\\myscript.py");
        assertEquals(InputKind.Script, scriptInputWin.getKind());
        
        Input tcpRawInput = new Input(service, "data/inputs/tcp/raw/6666");
        assertEquals(InputKind.Tcp, tcpRawInput.getKind());
        
        Input tcpCookedInput = new Input(service, "data/inputs/tcp/cooked/6666");
        assertEquals(InputKind.TcpSplunk, tcpCookedInput.getKind());
        
        Input udpInput = new Input(service, "data/inputs/udp/6666");
        assertEquals(InputKind.Udp, udpInput.getKind());
        
        Input modularInput = new Input(service, "data/inputs/my_modular_input/input_name");
        assertEquals(InputKind.create("my_modular_input"), modularInput.getKind());
    }
}
