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

        if (kind.equals(InputKind.Monitor)) {
            MonitorInput mi = (MonitorInput) input;
            mi.getFileCount();
            mi.getHost();
            mi.getIndex();
            mi.getRcvBuf();
        }
        else if (kind.equals(InputKind.Script)) {
            ScriptInput si = (ScriptInput) input;
            si.getGroup();
            si.getHost();
            si.getIndex();
            si.getInteval();
            si.getRcvBuf();
        }
        else if (kind.equals(InputKind.Tcp)) {
            TcpInput ti = (TcpInput) input;
            ti.getGroup();
            ti.getIndex();
            ti.getRcvBuf();
            ti.getRestrictToHost();
        }
        else if (kind.equals(InputKind.TcpSplunk)) {
            TcpSplunkInput tsi = (TcpSplunkInput) input;
            tsi.getGroup();
            tsi.getHost();
            tsi.getIndex();
            tsi.getRcvBuf();
        }
        else if (kind.equals(InputKind.Udp)) {
            UdpInput ui = (UdpInput) input;
            ui.getGroup();
            ui.getHost();
            ui.getIndex();
            ui.getRcvBuf();
        }
        else if (kind.equals(InputKind.WindowsActiveDirectory)) {
            WindowsActiveDirectoryInput wadi =
                    (WindowsActiveDirectoryInput) input;
            wadi.getIndex();
            wadi.getMonitorSubtree();
        }
        else if (kind.equals(InputKind.WindowsEventLog)) {
            WindowsEventLogInput weli = (WindowsEventLogInput) input;
            weli.getHosts();
            weli.getIndex();
            weli.getLocalName();
            weli.getLogs();
            weli.getLookupHost();
        }
        else if (kind.equals(InputKind.WindowsPerfmon)) {
            WindowsPerfmonInput wpi = (WindowsPerfmonInput) input;
            wpi.getIndex();
            wpi.getInstances();
            wpi.getInterval();
            wpi.getObject();
        }
        else if (kind.equals(InputKind.WindowsRegistry)) {
            WindowsRegistryInput wri = (WindowsRegistryInput) input;
            wri.getBaseline();
            wri.getHive();
            wri.getIndex();
            wri.getMonitorSubnodes();
            wri.getProc();
            wri.getType();
        }
        else if (kind.equals(InputKind.WindowsWmi)) {
            WindowsWmiInput wwi = (WindowsWmiInput) input;
            wwi.getClasses();
            wwi.getFields();
            wwi.getIndex();
            wwi.getInstances();
            wwi.getInterval();
            wwi.getLocalName();
            wwi.getLookupHost();
            wwi.getServer();
            wwi.getWq1();
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
