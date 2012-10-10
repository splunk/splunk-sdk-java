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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;



public class InputTest extends SplunkTestCase {
    final static String assertRoot = "Input assert: ";

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
        if (service.versionCompare("4.2.1") >= 0) {
            monitorInput.setCrcSalt("ThisIsSalt");
            monitorInput.setIgnoreOlderThan("1d");
            monitorInput.setTimeBeforeClose(120);
        }
        monitorInput.setFollowTail(false);
        monitorInput.setHost("three.four.com");
        monitorInput.setHostRegex("host*regex*");
        monitorInput.setHostSegment("");
        monitorInput.setIndex("main");
        monitorInput.setRecursive(false);
        monitorInput.setRenameSource("renamedSource");
        monitorInput.setSourcetype("monitor");
        monitorInput.setWhitelist("phonyregex*2");
        monitorInput.update();

        monitorInput.disable();
        // some attributes are write only; check what we can.
        assertEquals(assertRoot + "#1", "phonyregex*1",
            monitorInput.getBlacklist());
        assertFalse(assertRoot + "#2", monitorInput.getFollowTail());
        assertEquals(assertRoot + "#3", "three.four.com",
            monitorInput.getHost());
        assertEquals(assertRoot + "#4", "host*regex*",
            monitorInput.getHostRegex());
        if (service.versionCompare("4.2.1") >= 0) {
            assertEquals(assertRoot + "#4", "1d",
                monitorInput.getIgnoreOlderThan());
            assertEquals(assertRoot + "#4", 120,
                monitorInput.getTimeBeforeClose());
        }
        assertEquals(assertRoot + "#5", "main", monitorInput.getIndex());
        assertFalse(assertRoot + "#6", monitorInput.getRecursive());
        assertEquals(assertRoot + "#7", "renamedSource",
            monitorInput.getSource());
        assertEquals(assertRoot + "#8", "monitor",
            monitorInput.getSourceType());
        assertEquals(assertRoot + "#9", "phonyregex*2",
            monitorInput.getWhitelist());

        monitorInput.remove();
        inputCollection.refresh();
        inputCollection.refresh();
        assertFalse(assertRoot + "#10", inputCollection.containsKey(filename));
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
        assertTrue(assertRoot + "#11", inputCollection.containsKey(filename));
        ScriptInput scriptInput = (ScriptInput)inputCollection.get(filename);

        scriptInput.setHost("three.four.com");
        scriptInput.setIndex("main");
        scriptInput.setInterval("120");
        if (service.versionCompare("4.2.4") >= 0) {
            scriptInput.setPassAuth("admin");
        }
        scriptInput.setRenameSource("renamedSource");
        scriptInput.setSource("source");
        scriptInput.setSourcetype("script");
        scriptInput.update();

        assertEquals(assertRoot + "#12", "three.four.com",
            scriptInput.getHost());
        assertEquals(assertRoot + "#13", "main", scriptInput.getIndex());
        assertEquals(assertRoot + "#14", "120", scriptInput.getInterval());
        if (service.versionCompare("4.2.4") >= 0) {
            assertEquals(assertRoot + "#15", "admin",
                scriptInput.getPassAuth());
        }
        assertEquals(assertRoot + "#16", "renamedSource",
            scriptInput.getSource());
        assertEquals(assertRoot + "#17", "script", scriptInput.getSourceType());

        scriptInput.remove();
        inputCollection.refresh();
        assertFalse(assertRoot + "#18", inputCollection.containsKey(filename));

    }

    @Test public void testTcpInputCrud() {
        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        String port = "9999"; // test port

        // CRUD TCP (raw) input
        if (inputCollection.containsKey(port)) {
            inputCollection.remove(port);
            inputCollection.refresh();
        }
        assertFalse(assertRoot + "#19", inputCollection.containsKey(port));

        inputCollection.create(port, InputKind.Tcp);
        assertTrue(assertRoot + "#20", inputCollection.containsKey(port));
        TcpInput tcpInput = (TcpInput)inputCollection.get(port);

        tcpInput.setConnectionHost("one.two.three");
        tcpInput.setHost("myhost");
        tcpInput.setIndex("main");
        tcpInput.setQueue("indexQueue");
        if (service.versionCompare("4.3") >= 0) {
            // Behavioral difference between 4.3 and earlier versions
            tcpInput.setRawTcpDoneTimeout(120);
            tcpInput.setRestrictToHost("four.five.com");
        }
        tcpInput.setSource("tcp");
        tcpInput.setSourceType("sdk-tests");
        tcpInput.setSSL(false);
        tcpInput.update();

        assertEquals(assertRoot + "#21", "one.two.three",
            tcpInput.getConnectionHost());
        assertEquals(assertRoot + "#22", "myhost", tcpInput.getHost());
        assertEquals(assertRoot + "#23", "main", tcpInput.getIndex());
        assertEquals(assertRoot + "#24", "indexQueue", tcpInput.getQueue());
        assertEquals(assertRoot + "#25", "tcp", tcpInput.getSource());
        assertEquals(assertRoot + "#26", "sdk-tests", tcpInput.getSourceType());
        assertFalse(assertRoot + "#27", tcpInput.getSSL());

        tcpInput.remove();
        inputCollection.refresh();
        assertFalse(assertRoot + "#28", inputCollection.containsKey(port));
    }

    @Test public void testTcpSplunkInputCrud() {
        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        String port = "9998"; // test port

        // CRUD TCP (cooked) input
        if (inputCollection.containsKey(port)) {
            inputCollection.remove(port);
            inputCollection.refresh();
        }
        assertFalse(assertRoot + "#29", inputCollection.containsKey(port));

        inputCollection.create(port, InputKind.TcpSplunk);
        assertTrue(assertRoot + "#30", inputCollection.containsKey(port));
        TcpSplunkInput tcpSplunkInput =
                (TcpSplunkInput)inputCollection.get(port);

        tcpSplunkInput.setConnectionHost("one.two.three");
        tcpSplunkInput.setHost("myhost");
        if (service.versionCompare("4.3") >= 0) {
            // Behavioral difference between 4.3 and earlier versions
            tcpSplunkInput.setRestrictToHost("four.five.com");
        }
        tcpSplunkInput.setSSL(false);
        tcpSplunkInput.update();

        assertEquals(assertRoot + "#31", "one.two.three",
            tcpSplunkInput.getConnectionHost());
        assertEquals(assertRoot + "#32", "myhost", tcpSplunkInput.getHost());
        assertFalse(tcpSplunkInput.getSSL());

        tcpSplunkInput.remove();
        inputCollection.refresh();
        assertFalse(assertRoot + "#33", inputCollection.containsKey(port));
    }

    @Test public void testUdpInputCrud() {
        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        String port = "9997"; // test port

        // CRUD UDP input
        if (inputCollection.containsKey(port)) {
            inputCollection.remove(port);
            inputCollection.refresh();
        }
        assertFalse(assertRoot + "#34", inputCollection.containsKey(port));

        inputCollection.create(port, InputKind.Udp);
        assertTrue(assertRoot + "#35", inputCollection.containsKey(port));
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

        assertEquals(assertRoot + "#36", "connectionHost.com",
            udpInput.getConnectionHost());
        assertEquals(assertRoot + "#37", "myhost", udpInput.getHost());
        assertEquals(assertRoot + "#38", "main", udpInput.getIndex());
        assertTrue(assertRoot + "#39", udpInput.getNoAppendingTimeStamp());
        assertTrue(assertRoot + "#40", udpInput.getNoPriorityStripping());
        assertEquals(assertRoot + "#41", "indexQueue", udpInput.getQueue());
        assertEquals(assertRoot + "#42", "mysource",udpInput.getSource());
        assertEquals(assertRoot + "#43", "mysourcetype",
            udpInput.getSourceType());

        udpInput.remove();
        inputCollection.refresh();
        assertFalse(assertRoot + "#44", inputCollection.containsKey(port));

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
            assertFalse(assertRoot + "#45", inputCollection.containsKey(name));

            args.put("monitorSubtree", false);
            inputCollection.create(
                    name, InputKind.WindowsActiveDirectory, args);
            assertTrue(assertRoot + "#46", inputCollection.containsKey(name));
            WindowsActiveDirectoryInput windowsActiveDirectoryInput =
                    (WindowsActiveDirectoryInput)inputCollection.get(name);

            windowsActiveDirectoryInput.setStartingNode("startnode");
            windowsActiveDirectoryInput.setIndex("main");
            windowsActiveDirectoryInput.setMonitorSubtree(false);
            windowsActiveDirectoryInput.setTargetDc("otherDC");
            windowsActiveDirectoryInput.update();

            assertEquals(assertRoot + "#47", "main",
                windowsActiveDirectoryInput.getIndex());
            assertFalse(assertRoot + "#48",
                windowsActiveDirectoryInput.getMonitorSubtree());
            assertEquals(assertRoot + "#49", "startnode",
                windowsActiveDirectoryInput.getStartingNode());
            assertEquals(assertRoot + "#50", "main",
                windowsActiveDirectoryInput.getIndex());

            windowsActiveDirectoryInput.remove();
            inputCollection.refresh();
            assertFalse(assertRoot + "#51", inputCollection.containsKey(name));
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
            assertFalse(assertRoot + "#52", inputCollection.containsKey(name));

            // CRUD Windows Event Log Input
            args.put("lookup_host", "127.0.0.1");
            inputCollection.create(name, InputKind.WindowsEventLog, args);
            assertTrue(assertRoot + "#53", inputCollection.containsKey(name));
            WindowsEventLogInput windowsEventLogInput =
                     (WindowsEventLogInput)inputCollection.get(name);

            windowsEventLogInput.setIndex("main");
            windowsEventLogInput.setLookupHost("127.0.0.1");
            windowsEventLogInput.setHosts("one.two.three,four.five.six");
            windowsEventLogInput.update();

            assertEquals(assertRoot + "#54", "127.0.0.1",
                windowsEventLogInput.getLookupHost());
            assertEquals(assertRoot + "#55", "one.two.three,four.five.six",
                windowsEventLogInput.getHosts());
            assertEquals(assertRoot + "#55", "main",
                windowsEventLogInput.getIndex());

            windowsEventLogInput.remove();
            inputCollection.refresh();
            assertFalse(assertRoot + "#56", inputCollection.containsKey(name));
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
            assertFalse(assertRoot + "#57", inputCollection.containsKey(name));

            // CRUD Windows Perfmon Input
            args.put("interval", 600);
            args.put("object", "Server");
            inputCollection.create(name, InputKind.WindowsPerfmon, args);
            assertTrue(assertRoot + "#58", inputCollection.containsKey(name));
            WindowsPerfmonInput windowsPerfmonInput =
                    (WindowsPerfmonInput)inputCollection.get(name);

            windowsPerfmonInput.setIndex("main");
            windowsPerfmonInput.setCounters("% Privileged Time");
            windowsPerfmonInput.setInstances("wininit");
            windowsPerfmonInput.setObject("Process");
            windowsPerfmonInput.setInterval(1200);
            windowsPerfmonInput.update();

            assertEquals(assertRoot + "#59", 1,
                windowsPerfmonInput.getCounters().length);
            assertTrue(assertRoot + "#60",
                contains(windowsPerfmonInput.getCounters(),
                "% Privileged Time"));
            assertEquals(windowsPerfmonInput.getIndex(), "main");
            assertTrue(assertRoot + "#61",
                contains(windowsPerfmonInput.getInstances(), "wininit"));
            assertEquals(assertRoot + "#62", 1200,
                windowsPerfmonInput.getInterval());
            assertEquals(assertRoot + "#63", "Process",
                windowsPerfmonInput.getObject());

            // set multi-series values and update.
            windowsPerfmonInput.setCounters(
                new String[] {"% Privileged Time","% User Time"});
            windowsPerfmonInput.setInstances(new String[] {"smss","csrss"});
            windowsPerfmonInput.update();

            assertEquals(assertRoot + "#64", 2,
                windowsPerfmonInput.getCounters().length);
            assertTrue(assertRoot + "#65",
                contains(windowsPerfmonInput.getCounters(),
                "% Privileged Time"));
            assertTrue(assertRoot + "#66",
                contains(windowsPerfmonInput.getCounters(),  "% User Time"));

            assertEquals(assertRoot + "#67", 2,
                windowsPerfmonInput.getInstances().length);
            assertTrue(assertRoot + "#68",
                contains(windowsPerfmonInput.getInstances(), "smss"));
            assertTrue(assertRoot + "#69",
                contains(windowsPerfmonInput.getInstances(), "csrss"));

            windowsPerfmonInput.remove();
            inputCollection.refresh();
            assertFalse(assertRoot + "#70", inputCollection.containsKey(name));

        }
    }

    @Test public void testWindowsRegistryInputCrud() {
        Service service = connect();
        InputCollection inputCollection = service.getInputs();
        ServiceInfo info = service.getInfo();

        if (info.getOsName().equals("Windows")) {
            String name = "sdk-input-wr";
            Args args = new Args();

            if (service.versionCompare("4.3") < 0)
                return;

            if (inputCollection.containsKey(name)) {
                inputCollection.remove(name);
                inputCollection.refresh();
            }
            assertFalse(assertRoot + "#71", inputCollection.containsKey(name));

            // CRUD Windows Registry Input
            args.put("disabled", true);
            args.put("baseline", false);
            args.put("hive", "HKEY_USERS");
            args.put("proc", "*");
            args.put("type", "*");
            inputCollection.create(name, InputKind.WindowsRegistry, args);
            assertTrue(assertRoot + "#72", inputCollection.containsKey(name));
            WindowsRegistryInput windowsRegistryInput =
                    (WindowsRegistryInput)inputCollection.get(name);

            windowsRegistryInput.setIndex("main");
            windowsRegistryInput.setMonitorSubnodes(true);
            windowsRegistryInput.update();

            assertFalse(assertRoot + "#73", windowsRegistryInput.getBaseline());
            assertEquals(assertRoot + "#74", "main",
                windowsRegistryInput.getIndex());

            // adjust a few of the arguments
            windowsRegistryInput.setType("create,delete");
            windowsRegistryInput.setBaseline(false);
            windowsRegistryInput.update();

            assertEquals(assertRoot + "#75", "*",
                windowsRegistryInput.getProc());
            assertTrue(assertRoot + "#76",
                windowsRegistryInput.getType().contains("create"));
            assertTrue(assertRoot + "#77",
                windowsRegistryInput.getType().contains("delete"));
            assertFalse(assertRoot + "#78", windowsRegistryInput.getBaseline());

            windowsRegistryInput.remove();
            inputCollection.refresh();
            assertFalse(assertRoot + "#79", inputCollection.containsKey(name));
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
            assertFalse(assertRoot + "#80", inputCollection.containsKey(name));

            // CRUD Windows Wmi Input
            args.put("classes", "PerfOS_Processor");
            args.put("interval", 600);
            args.put("lookup_host", "127.0.0.1");
            inputCollection.create(name, InputKind.WindowsWmi, args);
            assertTrue(assertRoot + "#81", inputCollection.containsKey(name));
            WindowsWmiInput windowsWmiInput =
                    (WindowsWmiInput)inputCollection.get(name);

            assertEquals(assertRoot + "#82",
                "Win32_PerfFormattedData_PerfOS_Processor",
                windowsWmiInput.getClasses());
            assertEquals(assertRoot + "#83", 600,
                windowsWmiInput.getInterval());
            assertEquals(assertRoot + "#84", "127.0.0.1",
                windowsWmiInput.getLookupHost());

            windowsWmiInput.setClasses("PerfDisk_LogicalDisk");
            windowsWmiInput.setFields("Caption");
            windowsWmiInput.setIndex("main");
            windowsWmiInput.setInterval(1200);
            windowsWmiInput.setInstances("_Total");
            windowsWmiInput.setServers("host1.splunk.com,host2.splunk.com");
            windowsWmiInput.update();

            assertEquals(assertRoot + "#85",
                "Win32_PerfFormattedData_PerfDisk_LogicalDisk",
                windowsWmiInput.getClasses());
            assertEquals(assertRoot + "#86", 1,
                windowsWmiInput.getFields().length);
            assertTrue(assertRoot + "#87",
                contains(windowsWmiInput.getFields(), "Caption"));
            assertEquals(assertRoot + "#88", "main",
                windowsWmiInput.getIndex());
            assertEquals(assertRoot + "#89", 1200,
                windowsWmiInput.getInterval());
            assertEquals(assertRoot + "#90", 1,
                windowsWmiInput.getInstances().length);
            assertTrue(assertRoot + "#91",
                contains(windowsWmiInput.getInstances(), "_Total"));
            assertEquals(assertRoot + "#92",
                "host1.splunk.com,host2.splunk.com",
                windowsWmiInput.getServers());

            // set list fields
            windowsWmiInput.setFields(new String[]{"Caption", "Description"});
            windowsWmiInput.update();

            assertEquals(assertRoot + "#93", 2,
                windowsWmiInput.getFields().length);
            assertTrue(assertRoot + "#94",
                contains(windowsWmiInput.getFields(), "Caption"));
            assertTrue(assertRoot + "#95",
                contains(windowsWmiInput.getFields(), "Description"));

            windowsWmiInput.remove();
            inputCollection.refresh();
            assertFalse(assertRoot + "#96", inputCollection.containsKey(name));
        }
    }
}
