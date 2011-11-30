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

import java.util.Map;
import org.junit.*;

public class InputTest extends SplunkTestCase {

    private void touchSpecificInput(Input input) {
        InputKind kind = input.getKind();

        switch (kind) {
            case Monitor:
                MonitorInput monitorInput = (MonitorInput) input;
                monitorInput.getFileCount();
                monitorInput.getHost();
                monitorInput.getIndex();
                monitorInput.getRcvBuf();
                break;
            case Script:
                ScriptInput scriptInput = (ScriptInput) input;
                scriptInput.getGroup();
                scriptInput.getHost();
                scriptInput.getIndex();
                scriptInput.getInterval();
                scriptInput.getRcvBuf();
                break;
            case Tcp:
                TcpInput tcpInput = (TcpInput) input;
                tcpInput.getGroup();
                tcpInput.getHost();
                tcpInput.getIndex();
                tcpInput.getRcvBuf();
                tcpInput.getRestrictToHost();
                break;
            case TcpSplunk:
                TcpSplunkInput tcpSplunkInput = (TcpSplunkInput) input;
                tcpSplunkInput.getGroup();
                tcpSplunkInput.getHost();
                tcpSplunkInput.getIndex();
                tcpSplunkInput.getRcvBuf();
            case Udp:
                UdpInput udpInput = (UdpInput) input;
                udpInput.getGroup();
                udpInput.getHost();
                udpInput.getIndex();
                udpInput.getRcvBuf();
            case WindowsActiveDirectory:
                WindowsActiveDirectoryInput windowsActiveDirectoryInput =
                        (WindowsActiveDirectoryInput) input;
                windowsActiveDirectoryInput.getIndex();
                windowsActiveDirectoryInput.getMonitorSubtree();
                break;
            case WindowsEventLog:
                WindowsEventLogInput windowsEventLogInput =
                        (WindowsEventLogInput) input;
                windowsEventLogInput.getHosts();
                windowsEventLogInput.getIndex();
                windowsEventLogInput.getLocalName();
                windowsEventLogInput.getLogs();
                windowsEventLogInput.getLookupHost();
                break;
            case WindowsPerfmon:
                WindowsPerfmonInput windowsPerfmonInput =
                        (WindowsPerfmonInput) input;
                windowsPerfmonInput.getIndex();
                windowsPerfmonInput.getInstances();
                windowsPerfmonInput.getInterval();
                windowsPerfmonInput.getObject();
                break;
            case WindowsRegistry:
                WindowsRegistryInput windowsRegistryInput =
                        (WindowsRegistryInput) input;
                windowsRegistryInput.getBaseline();
                windowsRegistryInput.getHive();
                windowsRegistryInput.getIndex();
                windowsRegistryInput.getMonitorSubnodes();
                windowsRegistryInput.getProc();
                windowsRegistryInput.getType();
                break;
            case WindowsWmi:
                WindowsWmiInput windowsWmiInput = (WindowsWmiInput) input;
                windowsWmiInput.getClasses();
                windowsWmiInput.getFields();
                windowsWmiInput.getIndex();
                windowsWmiInput.getInstances();
                windowsWmiInput.getInterval();
                windowsWmiInput.getLocalName();
                windowsWmiInput.getLookupHost();
                windowsWmiInput.getServer();
                windowsWmiInput.getWq1();
        }
    }

    @Test public void testInputs() {
        Service service = connect();

        InputCollection inputs = service.getInputs();

        // Iterate inputs and make sure we can read them.
        for (Input input : inputs.values()) {
            input.getName();
            input.getTitle();
            input.getPath();
            input.getKind();
            touchSpecificInput(input);
        }
    }

    // UNDONE: Currently only do a rudimentary test on TcpInput, need to cover
    // all other input kinds.
    @Test public void testInputCrud() {
        Service service = connect();

        InputCollection inputs = service.getInputs();

        // Tcp inputs require the name to be the input's port number.
        String name = "9999"; // test port

        if (inputs.containsKey(name))
            fail("Input test port already exists: " + name);

        assertFalse(inputs.containsKey(name));

        inputs.create(name, InputKind.Tcp);
        assertTrue(inputs.containsKey(name));

        TcpInput tcpInput = (TcpInput)inputs.get(name);

        Args args = new Args();
        args.put("sourcetype", "sdk-tests");
        tcpInput.update(args);
        assertEquals(tcpInput.get("sourcetype"), "sdk-tests");

        tcpInput.remove();
        inputs.refresh();
        assertFalse(inputs.containsKey(name));
    }
}
