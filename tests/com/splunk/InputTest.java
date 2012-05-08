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

import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class InputTest extends SplunkTestCase {

    private boolean contains(String[] array, String value) {
        for (int i = 0; i < array.length; ++i)
            if (array[i].equals(value)) return true;
        return false;
    }

    private void touchSpecificInput(Input input) {
        InputKind inputKind = input.getKind();
        TcpConnections tcpConnections = null;
        UdpConnections udpConnections = null;

        switch (inputKind) {
            case Monitor:
                MonitorInput monitorInput = (MonitorInput) input;
                monitorInput.getBlacklist();
                monitorInput.getCrcSalt();
                monitorInput.getFileCount();
                monitorInput.getFollowTail();
                monitorInput.getHost();
                monitorInput.getHostRegex();
                monitorInput.getIgnoreOlderThan();
                monitorInput.getIndex();
                monitorInput.getQueue();
                monitorInput.getRcvBuf();
                monitorInput.getRecursive();
                monitorInput.getSource();
                monitorInput.getSourceType();
                monitorInput.getTimeBeforeClose();
                monitorInput.getWhitelist();
                break;
            case Script:
                ScriptInput scriptInput = (ScriptInput) input;
                scriptInput.getEndTime();
                scriptInput.getGroup();
                scriptInput.getHost();
                scriptInput.getIndex();
                scriptInput.getInterval();
                scriptInput.getRcvBuf();
                scriptInput.getStartTime();
                break;
            case Tcp:
                TcpInput tcpInput = (TcpInput) input;
                tcpInput.getConnectionHost();
                tcpInput.getGroup();
                tcpInput.getHost();
                tcpInput.getIndex();
                tcpInput.getQueue();
                tcpInput.getRcvBuf();
                tcpInput.getRestrictToHost();
                tcpInput.getSource();
                tcpInput.getSourceType();
                tcpInput.getSSL();
                tcpConnections = tcpInput.connections();
                tcpConnections.getConnection();
                tcpConnections.getServername();
                break;
            case TcpSplunk:
                TcpSplunkInput tcpSplunkInput = (TcpSplunkInput) input;
                tcpSplunkInput.getConnectionHost();
                tcpSplunkInput.getGroup();
                tcpSplunkInput.getHost();
                tcpSplunkInput.getIndex();
                tcpSplunkInput.getQueue();
                tcpSplunkInput.getRcvBuf();
                tcpSplunkInput.getSource();
                tcpSplunkInput.getSourceType();
                tcpSplunkInput.getSSL();
                tcpConnections = tcpSplunkInput.connections();
                tcpConnections.getConnection();
                tcpConnections.getServername();
                break;
            case Udp:
                UdpInput udpInput = (UdpInput) input;
                udpInput.getConnectionHost();
                udpInput.getGroup();
                udpInput.getHost();
                udpInput.getIndex();
                udpInput.getQueue();
                udpInput.getRcvBuf();
                udpInput.getSource();
                udpInput.getSourceType();
                udpInput.getNoAppendingTimeStamp();
                udpInput.getNoPriorityStripping();
                udpConnections = udpInput.connections();
                udpConnections.getGroup();
                break;
            case WindowsActiveDirectory:
                WindowsActiveDirectoryInput windowsActiveDirectoryInput =
                        (WindowsActiveDirectoryInput) input;
                windowsActiveDirectoryInput.getIndex();
                windowsActiveDirectoryInput.getMonitorSubtree();
                windowsActiveDirectoryInput.getStartingNode();
                windowsActiveDirectoryInput.getTargetDc();
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
                windowsPerfmonInput.getCounters();
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
                windowsWmiInput.getServers();
                windowsWmiInput.getWql();
                break;
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

    @Test public void testMonitorInputCrud() {
        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        ServiceInfo info = service.getInfo();

        // CRUD Monitor input
        String filename;
        if (info.getOsName().equals("Windows"))
            filename = "C:\\Windows\\WindowsUpdate.log";
        else if (info.getOsName().equals("Linux"))
            filename = "/var/log/messages";
        else if (info.getOsName().equals("Darwin")) {
            filename = "/var/log/system.log";
        } else {
            throw new Error("OS: " + info.getOsName() + " not supported");
        }
        if (inputCollection.containsKey(filename)) {
            inputCollection.remove(filename);
        }

        inputCollection.create(filename, InputKind.Monitor);
        assertTrue(inputCollection.containsKey(filename));
        MonitorInput monitorInput = (MonitorInput)inputCollection.get(filename);

        monitorInput.setBlacklist("phonyregex*1");
        monitorInput.setCheckIndex(true);
        monitorInput.setCheckPath(true);
        if (versionCompare(info.getVersion(), "4.2") > 0) {
            monitorInput.setCrcSalt("ThisIsSalt");
        }
        monitorInput.setFollowTail(false);
        monitorInput.setHost("three.four.com");
        monitorInput.setHostRegex("host*regex*");
        monitorInput.setHostSegment("");
        monitorInput.setIgnoreOlderThan("1d");
        monitorInput.setIndex("main");
        monitorInput.setRecursive(false);
        monitorInput.setRenameSource("renamedSource");
        monitorInput.setSourcetype("monitor");
        monitorInput.setTimeBeforeClose(120);
        monitorInput.setWhitelist("phonyregex*2");
        monitorInput.update();

        monitorInput.disable();
        // some attributes are write only; check what we can.
        assertEquals(monitorInput.getBlacklist(), "phonyregex*1");
        assertEquals(monitorInput.getFollowTail(), false);
        assertEquals(monitorInput.getHost(), "three.four.com");
        assertEquals(monitorInput.getHostRegex(), "host*regex*");
        assertEquals(monitorInput.getIgnoreOlderThan(), "1d");
        assertEquals(monitorInput.getIndex(), "main");
        assertEquals(monitorInput.getRecursive(), false);
        assertEquals(monitorInput.getSource(), "renamedSource");
        assertEquals(monitorInput.getSourceType(), "monitor");
        assertEquals(monitorInput.getTimeBeforeClose(), 120);
        assertEquals(monitorInput.getWhitelist(), "phonyregex*2");

        monitorInput.remove();
        inputCollection.refresh();
        assertFalse(inputCollection.containsKey(filename));
    }

    @Test public void testScriptInputCrud() {
        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        ServiceInfo info = service.getInfo();

        // CRUD Script input
        String filename;
        if (info.getOsName().equals("Windows"))
            filename = "echo.bat";
        else
            filename = "echo.sh";

        Args args = new Args();
        args.clear();
        args.put("interval", "60");
        if (inputCollection.get(filename) != null) {
            inputCollection.remove(filename);
        }
        inputCollection.create(filename, InputKind.Script, args);
        assertTrue(inputCollection.containsKey(filename));
        ScriptInput scriptInput = (ScriptInput)inputCollection.get(filename);

        scriptInput.setHost("three.four.com");
        scriptInput.setIndex("main");
        scriptInput.setInterval("120");
        if (versionCompare(info.getVersion(), "4.2.3") > 0) {
            scriptInput.setPassAuth("admin");
        }
        scriptInput.setRenameSource("renamedSource");
        scriptInput.setSource("source");
        scriptInput.setSourcetype("script");
        scriptInput.update();

        assertEquals(scriptInput.getHost(), "three.four.com");
        assertEquals(scriptInput.getIndex(), "main");
        assertEquals(scriptInput.getInterval(), "120");
        if (versionCompare(info.getVersion(), "4.2.3") > 0) {
            assertEquals(scriptInput.getPassAuth(), "admin");
        }
        assertEquals(scriptInput.getSource(), "renamedSource");
        assertEquals(scriptInput.getSourceType(), "script");

        scriptInput.remove();
        inputCollection.refresh();
        assertFalse(inputCollection.containsKey(filename));

    }

    @Test public void testTcpInputCrud() {
        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        ServiceInfo info = service.getInfo();
        String port = "9999"; // test port

        // CRUD TCP (raw) input
        if (inputCollection.containsKey(port)) {
            inputCollection.remove(port);
            inputCollection.refresh();
        }
        assertFalse(inputCollection.containsKey(port));

        inputCollection.create(port, InputKind.Tcp);
        assertTrue(inputCollection.containsKey(port));
        TcpInput tcpInput = (TcpInput)inputCollection.get(port);

        tcpInput.setConnectionHost("one.two.three");
        tcpInput.setHost("myhost");
        tcpInput.setIndex("main");
        tcpInput.setQueue("indexQueue");
        if (versionCompare(info.getVersion(), "4.3") > 0) {
            tcpInput.setRawTcpDoneTimeout(120);
            // Behavioral difference between 4.3 and earlier versions
            tcpInput.setRestrictToHost("four.five.com");
        }
        tcpInput.setSource("tcp");
        tcpInput.setSourceType("sdk-tests");
        tcpInput.setSSL(false);
        tcpInput.update();

        assertEquals(tcpInput.getConnectionHost(), "one.two.three");
        assertEquals(tcpInput.getHost(), "myhost");
        assertEquals(tcpInput.getIndex(), "main");
        assertEquals(tcpInput.getQueue(), "indexQueue");
        assertEquals(tcpInput.getSource(), "tcp");
        assertEquals(tcpInput.getSourceType(), "sdk-tests");
        assertFalse(tcpInput.getSSL());
        assertEquals(tcpInput.getSource(), "tcp");

        tcpInput.remove();
        inputCollection.refresh();
        assertFalse(inputCollection.containsKey(port));
    }

    @Test public void testTcpSplunkInputCrud() {
        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        ServiceInfo info = service.getInfo();
        String port = "9998"; // test port

        // CRUD TCP (cooked) input
        if (inputCollection.containsKey(port)) {
            inputCollection.remove(port);
            inputCollection.refresh();
        }
        assertFalse(inputCollection.containsKey(port));

        inputCollection.create(port, InputKind.TcpSplunk);
        assertTrue(inputCollection.containsKey(port));
        TcpSplunkInput tcpSplunkInput =
                (TcpSplunkInput)inputCollection.get(port);

        tcpSplunkInput.setConnectionHost("one.two.three");
        tcpSplunkInput.setHost("myhost");
        if (versionCompare(info.getVersion(), "4.3") > 0) {
            // Behavioral difference between 4.3 and earlier versions
            tcpSplunkInput.setRestrictToHost("four.five.com");
        }
        tcpSplunkInput.setSSL(false);
        tcpSplunkInput.update();

        assertEquals(tcpSplunkInput.getConnectionHost(), "one.two.three");
        assertEquals(tcpSplunkInput.getHost(), "myhost");
        assertFalse(tcpSplunkInput.getSSL());

        tcpSplunkInput.remove();
        inputCollection.refresh();
        assertFalse(inputCollection.containsKey(port));
    }

    @Test public void testUdpInputCrud() {
        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        ServiceInfo info = service.getInfo();
        String port = "9997"; // test port

        // CRUD UDP input
        if (inputCollection.containsKey(port)) {
            inputCollection.remove(port);
            inputCollection.refresh();
        }
        assertFalse(inputCollection.containsKey(port));

        inputCollection.create(port, InputKind.Udp);
        assertTrue(inputCollection.containsKey(port));
        UdpInput udpInput =(UdpInput)inputCollection.get(port);

        udpInput.setConnectionHost("connectionHost.com");
        udpInput.setHost("myhost");
        udpInput.setIndex("main");
        udpInput.setNoAppendingTimeStamp(true);
        udpInput.setNoPriorityStripping(true);
        udpInput.setQueue("indexQueue");
        udpInput.setSource("mysource");
        udpInput.setSourceType("mysourcetype");
        udpInput.update();

        assertEquals(udpInput.getConnectionHost(), "connectionHost.com");
        assertEquals(udpInput.getHost(), "myhost");
        assertEquals(udpInput.getIndex(), "main");
        assertTrue(udpInput.getNoAppendingTimeStamp());
        assertTrue(udpInput.getNoPriorityStripping());
        assertEquals(udpInput.getQueue(), "indexQueue");
        assertEquals(udpInput.getSource(), "mysource");
        assertEquals(udpInput.getSourceType(), "mysourcetype");

        udpInput.remove();
        inputCollection.refresh();
        assertFalse(inputCollection.containsKey(port));

    }

    @Test public void testWindowsActiveDirectoryInputCrud() {
    /*
     * Need an active directory domain controller for Windows Active Directory
     *

        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        ServiceInfo info = service.getInfo();

        if (info.getOsName().equals("Windows")) {
            String name = "sdk-input-wad";
            Args args = new Args();

            if (inputCollection.containsKey(name)) {
                inputCollection.remove(name);
                inputCollection.refresh();
            }
            assertFalse(inputCollection.containsKey(name));

            args.put("monitorSubtree", false);
            inputCollection.create(
                    name, InputKind.WindowsActiveDirectory, args);
            assertTrue(inputCollection.containsKey(name));
            WindowsActiveDirectoryInput windowsActiveDirectoryInput =
                    (WindowsActiveDirectoryInput)inputCollection.get(name);

            windowsActiveDirectoryInput.setStartingNode("startnode");
            windowsActiveDirectoryInput.setIndex("main");
            windowsActiveDirectoryInput.setMonitorSubtree(false);
            windowsActiveDirectoryInput.setTargetDc("otherDC");
            windowsActiveDirectoryInput.update();

            assertEquals(windowsActiveDirectoryInput.getIndex(), "main");
            assertEquals(
                    windowsActiveDirectoryInput.getMonitorSubtree(), false);
            assertEquals(
                    windowsActiveDirectoryInput.getStartingNode(), "startnode");
            assertEquals(windowsActiveDirectoryInput.getIndex(), "main");

            windowsActiveDirectoryInput.remove();
            inputCollection.refresh();
            assertFalse(inputCollection.containsKey(name));
    */
    }

    @Test public void testWindowsEventLogInputCrud() {
        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        ServiceInfo info = service.getInfo();

        if (info.getOsName().equals("Windows")) {
            String name = "sdk-input-wel";
            Args args = new Args();

            if (inputCollection.containsKey(name)) {
                inputCollection.remove(name);
                inputCollection.refresh();
            }
            assertFalse(inputCollection.containsKey(name));

            // CRUD Windows Event Log Input
            args.put("lookup_host", "127.0.0.1");
            inputCollection.create(name, InputKind.WindowsEventLog, args);
            assertTrue(inputCollection.containsKey(name));
            WindowsEventLogInput windowsEventLogInput =
                     (WindowsEventLogInput)inputCollection.get(name);

            windowsEventLogInput.setIndex("main");
            windowsEventLogInput.setLookupHost("127.0.0.1");
            windowsEventLogInput.setHosts("one.two.three,four.five.six");
            windowsEventLogInput.update();

            assertEquals(windowsEventLogInput.getLookupHost(), "127.0.0.1");
            assertEquals(
                windowsEventLogInput.getHosts(), "one.two.three,four.five.six");
            assertEquals(windowsEventLogInput.getIndex(), "main");

            windowsEventLogInput.remove();
            inputCollection.refresh();
            assertFalse(inputCollection.containsKey(name));
        }
    }


    @Test public void testWindowsPerfmonInputCrud() {
        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        ServiceInfo info = service.getInfo();

        if (info.getOsName().equals("Windows")) {
            String name = "sdk-input-wp";
            Args args = new Args();

            if (inputCollection.containsKey(name)) {
                inputCollection.remove(name);
                inputCollection.refresh();
            }
            assertFalse(inputCollection.containsKey(name));

            // CRUD Windows Perfmon Input
            args.put("interval", 600);
            args.put("object", "Server");
            inputCollection.create(name, InputKind.WindowsPerfmon, args);
            assertTrue(inputCollection.containsKey(name));
            WindowsPerfmonInput windowsPerfmonInput =
                    (WindowsPerfmonInput)inputCollection.get(name);

            windowsPerfmonInput.setIndex("main");
            windowsPerfmonInput.setCounters("% Privileged Time");
            windowsPerfmonInput.setInstances("wininit");
            windowsPerfmonInput.setObject("Process");
            windowsPerfmonInput.setInterval(1200);
            windowsPerfmonInput.update();

            assertEquals(
                    windowsPerfmonInput.getCounters(), "[% Privileged Time]");
            assertEquals(windowsPerfmonInput.getIndex(), "main");
            assertEquals(windowsPerfmonInput.getInstances(), "[wininit]");
            assertEquals(windowsPerfmonInput.getInterval(), 1200);
            assertEquals(windowsPerfmonInput.getObject(), "Process");

            // set multi-series values and update.
            windowsPerfmonInput.setCounters(
                    new String[] {"% Privileged Time","% User Time"});
            windowsPerfmonInput.setInstances(new String[] {"smss","csrss"});
            windowsPerfmonInput.update();

            assertEquals(
                    windowsPerfmonInput.getCounters(),
                    "[% Privileged Time, % User Time]");
            assertEquals(windowsPerfmonInput.getInstances(), "[smss, csrss]");

            windowsPerfmonInput.remove();
            inputCollection.refresh();
            assertFalse(inputCollection.containsKey(name));

        }
    }

    @Test public void testWindowsRegistryInputCrud() {
        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        ServiceInfo info = service.getInfo();

        if (info.getOsName().equals("Windows")) {
            String name = "sdk-input-wr";
            Args args = new Args();

            if (inputCollection.containsKey(name)) {
                inputCollection.remove(name);
                inputCollection.refresh();
            }
            assertFalse(inputCollection.containsKey(name));

            // CRUD Windows Registry Input
            args.put("disabled", true);
            args.put("baseline", false);
            args.put("hive", "HKEY_USERS");
            args.put("proc", "*");
            args.put("type", "*");
            inputCollection.create(name, InputKind.WindowsRegistry, args);
            assertTrue(inputCollection.containsKey(name));
            WindowsRegistryInput windowsRegistryInput =
                    (WindowsRegistryInput)inputCollection.get(name);

            windowsRegistryInput.setIndex("main");
            windowsRegistryInput.setMonitorSubnodes(true);
            windowsRegistryInput.update();

            assertEquals(windowsRegistryInput.getBaseline(), false);
            assertEquals(windowsRegistryInput.getIndex(), "main");

            // set some of the rrequired fields directly
            windowsRegistryInput.setHive("HKEY_CURRENT_CONFIG");
            windowsRegistryInput.setProc("s*");
            windowsRegistryInput.setType("create,delete");
            windowsRegistryInput.setBaseline(true);
            windowsRegistryInput.update();

            assertEquals(windowsRegistryInput.getHive(), "HKEY_CURRENT_CONFIG");
            assertEquals(windowsRegistryInput.getProc(), "s*");
            assertEquals(windowsRegistryInput.getType(), "[create,delete]");
            assertEquals(windowsRegistryInput.getBaseline(), true);

            windowsRegistryInput.remove();
            inputCollection.refresh();
            assertFalse(inputCollection.containsKey(name));
        }
    }


    @Test public void testWmiInputCrud() {
        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        ServiceInfo info = service.getInfo();

        if (info.getOsName().equals("Windows")) {
            String name = "sdk-input-wmi";
            Args args = new Args();

            if (inputCollection.containsKey(name)) {
                inputCollection.remove(name);
                inputCollection.refresh();
            }
            assertFalse(inputCollection.containsKey(name));

            // CRUD Windows Wmi Input
            args.put("classes", "PerfOS_Processor");
            args.put("interval", 600);
            args.put("lookup_host", "127.0.0.1");
            inputCollection.create(name, InputKind.WindowsWmi, args);
            assertTrue(inputCollection.containsKey(name));
            WindowsWmiInput windowsWmiInput =
                    (WindowsWmiInput)inputCollection.get(name);

            assertEquals(windowsWmiInput.getClasses(),
                    "Win32_PerfFormattedData_PerfOS_Processor");
            assertEquals(windowsWmiInput.getInterval(), 600);
            assertEquals(windowsWmiInput.getLookupHost(), "127.0.0.1");

            windowsWmiInput.setClasses("PerfDisk_LogicalDisk");
            windowsWmiInput.setFields("Caption");
            windowsWmiInput.setIndex("main");
            windowsWmiInput.setInterval(1200);
            windowsWmiInput.setInstances("_Total");
            windowsWmiInput.setServers("host1.splunk.com,host2.splunk.com");
            windowsWmiInput.update();

            assertEquals(windowsWmiInput.getClasses(),
                    "Win32_PerfFormattedData_PerfDisk_LogicalDisk");
            assertTrue(windowsWmiInput.getFields().length == 1);
            assertTrue(contains(windowsWmiInput.getFields(), "Caption"));
            assertEquals(windowsWmiInput.getIndex(), "main");
            assertEquals(windowsWmiInput.getInterval(), 1200);
            assertTrue(windowsWmiInput.getInstances().length == 1);
            assertTrue(contains(windowsWmiInput.getInstances(), "_Total"));
            assertEquals(windowsWmiInput.getServers(),
                    "host1.splunk.com,host2.splunk.com");

            // set list fields
            windowsWmiInput.setFields(new String[]{"Caption", "Description"});
            windowsWmiInput.update();

            assertTrue(windowsWmiInput.getFields().length == 2);
            assertTrue(contains(windowsWmiInput.getFields(), "Caption"));
            assertTrue(contains(windowsWmiInput.getFields(), "Description"));

            windowsWmiInput.remove();
            inputCollection.refresh();
            assertFalse(inputCollection.containsKey(name));
        }
    }
}
