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


import org.junit.Test;

import java.util.HashMap;

public class InputCrudTest extends InputTest {
    @Test
    public void testGetters() {
        assertFalse("No default inputs to test.", inputs.isEmpty());
        
        Input splunkInput = inputs.create(findNextUnusedPort(12911) +"", InputKind.TcpSplunk);
        inputs.refresh();
        
        // Test getters on all default inputs
        for (Input input : inputs.values()) {
            input.getName();
            input.getTitle();
            input.getPath();
            input.getKind();
            testSpecializedGetters(input);
        }
        
        splunkInput.remove();
    }
    
    @SuppressWarnings("deprecation")
    private void testSpecializedGetters(Input input) {
        InputKind inputKind = input.getKind();
        TcpConnections tcpConnections = null;
        UdpConnections udpConnections = null;

        if (inputKind == InputKind.Monitor) {
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
        } else if (inputKind == InputKind.Script) {
            ScriptInput scriptInput = (ScriptInput) input;
            scriptInput.getEndTime();
            scriptInput.getGroup();
            scriptInput.getHost();
            scriptInput.getIndex();
            scriptInput.getInterval();
            scriptInput.getRcvBuf();
            scriptInput.getStartTime();
        } else if (inputKind == InputKind.Tcp) {
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
        } else if (inputKind == InputKind.TcpSplunk) {
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
        } else if (inputKind == InputKind.Udp) {
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
        } else if (inputKind == InputKind.WindowsActiveDirectory) {
            WindowsActiveDirectoryInput windowsActiveDirectoryInput =
                    (WindowsActiveDirectoryInput) input;
            windowsActiveDirectoryInput.getIndex();
            windowsActiveDirectoryInput.getMonitorSubtree();
            windowsActiveDirectoryInput.getStartingNode();
            windowsActiveDirectoryInput.getTargetDc();
        } else if (inputKind == InputKind.WindowsEventLog) {
            WindowsEventLogInput windowsEventLogInput =
                    (WindowsEventLogInput) input;
            windowsEventLogInput.getHosts();
            windowsEventLogInput.getIndex();
            windowsEventLogInput.getLocalName();
            windowsEventLogInput.getLogs();
            windowsEventLogInput.getLookupHost();
        } else if (inputKind == InputKind.WindowsPerfmon) {
            WindowsPerfmonInput windowsPerfmonInput =
                    (WindowsPerfmonInput) input;
            windowsPerfmonInput.getCounters();
            windowsPerfmonInput.getIndex();
            windowsPerfmonInput.getInstances();
            windowsPerfmonInput.getInterval();
            windowsPerfmonInput.getObject();
        } else if (inputKind == InputKind.WindowsRegistry) {
            WindowsRegistryInput windowsRegistryInput =
                    (WindowsRegistryInput) input;
            windowsRegistryInput.getBaseline();
            windowsRegistryInput.getHive();
            windowsRegistryInput.getIndex();
            windowsRegistryInput.getMonitorSubnodes();
            windowsRegistryInput.getProc();
            windowsRegistryInput.getType();
        } else if (inputKind == InputKind.WindowsWmi) {
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
        }
    }
    
    @Test
    public void testMonitorInputCrud() {
        final String filename = locateSystemLog();
        
        // Create variants
        try {
            inputs.create(filename);
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Good
        }
        try {
            inputs.create(filename, new HashMap<String, Object>());
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Good
        }
        
        // Create
        if (inputs.containsKey(filename)) {
            inputs.remove(filename);
        }
        inputs.create(filename, InputKind.Monitor);
        assertTrue(inputs.containsKey(filename));
        MonitorInput monitorInput = (MonitorInput)inputs.get(filename);
        
        // Get variant
        Args namespace = new Args();
        namespace.put("owner", monitorInput.getMetadata().getOwner());
        namespace.put("app", monitorInput.getMetadata().getApp());
        inputs.get(filename, namespace);    // throws no exception

        // Probe
        {
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
            
            // NOTE: Some attributes are write only. Check what we can.
            assertEquals("phonyregex*1", monitorInput.getBlacklist());
            assertFalse(monitorInput.getFollowTail());
            assertEquals("three.four.com", monitorInput.getHost());
            assertEquals("host*regex*", monitorInput.getHostRegex());
            if (service.versionCompare("4.2.1") >= 0) {
                assertEquals("1d", monitorInput.getIgnoreOlderThan());
                assertEquals(120, monitorInput.getTimeBeforeClose());
            }
            assertEquals("main", monitorInput.getIndex());
            assertFalse(monitorInput.getRecursive());
            assertEquals("renamedSource", monitorInput.getSource());
            assertEquals("monitor", monitorInput.getSourceType());
            assertEquals("phonyregex*2", monitorInput.getWhitelist());
        }

        // Remove
        monitorInput.remove();
        assertEventuallyTrue(new EventuallyTrueBehavior() {
            @Override
            public boolean predicate() {
                return !inputs.refresh().containsKey(filename);
            }
        });
    }
    
    @Test
    public void testScriptInputCrud() {
        if (!hasTestData()) {
            System.out.println("WARNING: sdk-app-collection not installed in Splunk; skipping test.");
            return;
        }

        installApplicationFromTestData("modular-inputs");

        // Determine what script to use for the input
        String filename;
        if (service.getInfo().getOsName().equals("Windows")) {
            // Windows
            filename = "etc\\apps\\modular-inputs\\bin\\echo.bat";
        } else {
            // Linux or Mac OS X
            filename = "etc/apps/modular-inputs/bin/echo.sh";
        }

        // Create
        if (inputs.get(filename) != null) {
            inputs.remove(filename);
        }
        inputs.create(
                filename, InputKind.Script, new Args("interval", "60"));
        assertTrue(inputs.containsKey(filename));
        ScriptInput scriptInput = (ScriptInput)inputs.get(filename);

        // Probe
        {
            scriptInput.setHost("three.four.com");
            scriptInput.setIndex("main");
            scriptInput.setInterval("120");
            if (service.versionCompare("4.2.4") >= 0) {
                scriptInput.setPassAuth("admin");
            }
            scriptInput.setRenameSource("renamedSource");
            scriptInput.setSource("renamedSource2");
            scriptInput.setSourcetype("script");
            scriptInput.update();

            assertEquals("three.four.com", scriptInput.getHost());
            assertEquals("main", scriptInput.getIndex());
            assertEquals("120", scriptInput.getInterval());
            if (service.versionCompare("4.2.4") >= 0) {
                assertEquals("admin", scriptInput.getPassAuth());
            }
            if (!WORKAROUND_KNOWN_BUGS) {   // SPL-57223
                assertEquals("renamedSource", scriptInput.getSource());
            }
            assertEquals("script", scriptInput.getSourceType());
        }

        // Remove
        scriptInput.remove();
        assertFalse(inputs.refresh().containsKey(filename));
    }
    
    @Test
    public void testTcpInputCrud() {
        String port = "9999";   // test port
        
        deleteInputIfExists(port);
        
        // Create
        inputs.create(port, InputKind.Tcp);
        assertTrue(inputs.containsKey(port));
        TcpInput tcpInput = (TcpInput)inputs.get(port);

        // Probe
        {
            assertFalse("ip".equals(tcpInput.getConnectionHost()));
            tcpInput.setConnectionHost("ip");
            tcpInput.setHost("myhost");
            tcpInput.setIndex("main");
            tcpInput.setQueue("indexQueue");
            if (service.versionCompare("4.3") >= 0) {
                // Behavioral difference between 4.3 and earlier versions
                tcpInput.setRawTcpDoneTimeout(120);
            }
            tcpInput.setSource("tcp");
            tcpInput.setSourceType("sdk-tests");
            tcpInput.setSSL(false);
            tcpInput.update();

            assertEquals("ip", tcpInput.getConnectionHost());
            assertEquals("myhost", tcpInput.getHost());
            assertEquals("main", tcpInput.getIndex());
            assertEquals("indexQueue", tcpInput.getQueue());
            assertEquals("tcp", tcpInput.getSource());
            assertEquals("sdk-tests", tcpInput.getSourceType());
            assertFalse(tcpInput.getSSL());
        }
        
        // Remove
        assertTrue(inputs.refresh().containsKey(port));
        tcpInput.remove();
        assertFalse(inputs.refresh().containsKey(port));
    }
    
    @Test
    public void testTcpSplunkInputCrud() {
        String port = "9998";   // test port

        deleteInputIfExists(port);

        // Create
        inputs.create(port, InputKind.TcpSplunk);
        assertTrue(inputs.containsKey(port));
        TcpSplunkInput tcpSplunkInput =
                (TcpSplunkInput)inputs.get(port);

        // Probe
        {
            assertFalse("dns".equals(tcpSplunkInput.getConnectionHost()));
            tcpSplunkInput.setConnectionHost("dns");
            tcpSplunkInput.setHost("myhost");
            tcpSplunkInput.setSSL(false);
            tcpSplunkInput.update();

            assertEquals("dns", tcpSplunkInput.getConnectionHost());
            assertEquals("myhost", tcpSplunkInput.getHost());
            assertFalse(tcpSplunkInput.getSSL());
        }

        // Remove
        tcpSplunkInput.remove();
        assertFalse(inputs.refresh().containsKey(port));
    }
    
    @Test
    public void testUdpInputCrud() {
        String port = "9997";   // test port

        deleteInputIfExists(port);

        // Create
        inputs.create(port, InputKind.Udp);
        assertTrue(inputs.containsKey(port));
        UdpInput udpInput = (UdpInput)inputs.get(port);

        // Probe
        {
            assertFalse("dns".equals(udpInput.getConnectionHost()));
            udpInput.setConnectionHost("dns");
            udpInput.setHost("myhost");
            udpInput.setIndex("main");
            udpInput.setNoAppendingTimeStamp(true);
            udpInput.setNoPriorityStripping(true);
            udpInput.setQueue("indexQueue");
            udpInput.setSource("mysource");
            udpInput.setSourceType("mysourcetype");
            udpInput.update();
    
            assertEquals("dns", udpInput.getConnectionHost());
            assertEquals("myhost", udpInput.getHost());
            assertEquals("main", udpInput.getIndex());
            assertTrue(udpInput.getNoAppendingTimeStamp());
            assertTrue(udpInput.getNoPriorityStripping());
            assertEquals("indexQueue", udpInput.getQueue());
            assertEquals("mysource",udpInput.getSource());
            assertEquals("mysourcetype", udpInput.getSourceType());
        }

        // Remove
        udpInput.remove();
        assertFalse(inputs.refresh().containsKey(port));
    }
    
    // Need an active directory domain controller to run this test
    //@Test
    public void _testWindowsActiveDirectoryInputCrud() {
        if (!service.getInfo().getOsName().equals("Windows")) {
            return;
        }
        
        String name = "sdk-input-wad";
        
        deleteInputIfExists(name);

        // Create
        inputs.create(
                name, InputKind.WindowsActiveDirectory,
                new Args("monitorSubtree", false));
        assertTrue(inputs.containsKey(name));
        WindowsActiveDirectoryInput windowsActiveDirectoryInput =
                (WindowsActiveDirectoryInput)inputs.get(name);

        // Probe
        {
            windowsActiveDirectoryInput.setStartingNode("startnode");
            windowsActiveDirectoryInput.setIndex("main");
            windowsActiveDirectoryInput.setMonitorSubtree(false);
            windowsActiveDirectoryInput.setTargetDc("otherDC");
            windowsActiveDirectoryInput.update();

            assertEquals("main", windowsActiveDirectoryInput.getIndex());
            assertFalse(windowsActiveDirectoryInput.getMonitorSubtree());
            assertEquals("startnode", windowsActiveDirectoryInput.getStartingNode());
            assertEquals("main", windowsActiveDirectoryInput.getIndex());
        }

        // Remove
        windowsActiveDirectoryInput.remove();
        assertFalse(inputs.refresh().containsKey(name));
    }
    
    @Test
    public void testWindowsEventLogInputCrud() {
        if (!service.getInfo().getOsName().equals("Windows")) {
            return;
        }
        
        String name = "sdk-input-wel";
        
        deleteInputIfExists(name);
        assertFalse(inputs.refresh().containsKey(name));

        // Create
        inputs.create(
                name, InputKind.WindowsEventLog,
                new Args("lookup_host", "127.0.0.1"));
        assertTrue(inputs.containsKey(name));
        WindowsEventLogInput windowsEventLogInput =
                 (WindowsEventLogInput)inputs.get(name);

        // Probe
        {
            windowsEventLogInput.setIndex("main");
            windowsEventLogInput.setLookupHost("127.0.0.1");
            windowsEventLogInput.setHosts("one.two.three,four.five.six");
            windowsEventLogInput.update();
    
            assertEquals("127.0.0.1", windowsEventLogInput.getLookupHost());
            assertEquals("one.two.three,four.five.six", windowsEventLogInput.getHosts());
            assertEquals("main", windowsEventLogInput.getIndex());
        }

        // Remove
        windowsEventLogInput.remove();
        assertFalse(inputs.refresh().containsKey(name));
    }
    
    @Test
    public void testWindowsPerfmonInputCrud() {
        if (!service.getInfo().getOsName().equals("Windows")) {
            return;
        }
            
        String name = "sdk-input-wp";
        
        deleteInputIfExists(name);

        // Create
        Args args = new Args();
        args.put("interval", 600);
        args.put("object", "Server");
        inputs.create(name, InputKind.WindowsPerfmon, args);
        assertTrue(inputs.containsKey(name));
        WindowsPerfmonInput windowsPerfmonInput =
                (WindowsPerfmonInput)inputs.get(name);

        // Probe
        {
            windowsPerfmonInput.setIndex("main");
            windowsPerfmonInput.setCounters("% Privileged Time");
            windowsPerfmonInput.setInstances("wininit");
            windowsPerfmonInput.setObject("Process");
            windowsPerfmonInput.setInterval(1200);
            windowsPerfmonInput.update();
    
            assertEquals(1, windowsPerfmonInput.getCounters().length);
            assertTrue(contains(windowsPerfmonInput.getCounters(), "% Privileged Time"));
            assertEquals(windowsPerfmonInput.getIndex(), "main");
            assertTrue(contains(windowsPerfmonInput.getInstances(), "wininit"));
            assertEquals(1200, windowsPerfmonInput.getInterval());
            assertEquals("Process", windowsPerfmonInput.getObject());
            
            // set multi-series values and update.
            windowsPerfmonInput.setCounters(new String[] {"% Privileged Time","% User Time"});
            windowsPerfmonInput.setInstances(new String[] {"smss","csrss"});
            windowsPerfmonInput.update();
    
            assertEquals(2, windowsPerfmonInput.getCounters().length);
            assertTrue(contains(windowsPerfmonInput.getCounters(), "% Privileged Time"));
            assertTrue(contains(windowsPerfmonInput.getCounters(),  "% User Time"));
    
            assertEquals(2, windowsPerfmonInput.getInstances().length);
            assertTrue(contains(windowsPerfmonInput.getInstances(), "smss"));
            assertTrue(contains(windowsPerfmonInput.getInstances(), "csrss"));
        }

        // Remove
        windowsPerfmonInput.remove();
        assertFalse(inputs.refresh().containsKey(name));
    }
    
    @Test
    public void testWindowsRegistryInputCrud() {
        if (!service.getInfo().getOsName().equals("Windows")) {
            return;
        }
        if (service.versionCompare("4.3") < 0) {
            return;
        }
        
        String name = "sdk-input-wr";

        deleteInputIfExists(name);

        // Create
        Args args = new Args();
        args.put("disabled", true);
        args.put("baseline", false);
        args.put("hive", "HKEY_USERS");
        args.put("proc", "*");
        args.put("type", "*");
        inputs.create(name, InputKind.WindowsRegistry, args);
        assertTrue(inputs.containsKey(name));
        WindowsRegistryInput windowsRegistryInput =
                (WindowsRegistryInput)inputs.get(name);

        // Probe
        {
            windowsRegistryInput.setIndex("main");
            windowsRegistryInput.setMonitorSubnodes(true);
            windowsRegistryInput.update();
    
            assertFalse(windowsRegistryInput.getBaseline());
            assertEquals("main",
                windowsRegistryInput.getIndex());
    
            // adjust a few of the arguments
            String[] wriType = {"create", "delete"};
            windowsRegistryInput.setType(wriType);
            windowsRegistryInput.setBaseline(false);
            windowsRegistryInput.update();
    
            assertEquals("*",
                windowsRegistryInput.getProc());
            assertTrue(
                    windowsRegistryInput.getType()[0].equals("create"));
            assertTrue(
                    windowsRegistryInput.getType()[1].equals("delete"));
            assertFalse(windowsRegistryInput.getBaseline());
        }

        // Remove
        windowsRegistryInput.remove();
        assertFalse(inputs.refresh().containsKey(name));
    }

    @Test
    public void testWmiInputCrud() {
        if (!service.getInfo().getOsName().equals("Windows")) {
            return;
        }
        
        String name = "sdk-input-wmi";
        
        deleteInputIfExists(name);

        // Create
        Args args = new Args();
        args.put("classes", "PerfOS_Processor");
        args.put("interval", 600);
        args.put("lookup_host", service.getHost());
        inputs.create(name, InputKind.WindowsWmi, args);
        assertTrue(inputs.containsKey(name));
        WindowsWmiInput windowsWmiInput =
                (WindowsWmiInput)inputs.get(name);

        // Probe
        {
            assertEquals(
                "Win32_PerfFormattedData_PerfOS_Processor",
                windowsWmiInput.getClasses());
            assertEquals(600,
                windowsWmiInput.getInterval());
            assertEquals(service.getHost(),
                windowsWmiInput.getLookupHost());
    
            windowsWmiInput.setClasses("PerfDisk_LogicalDisk");
            windowsWmiInput.setFields("Caption");
            windowsWmiInput.setIndex("main");
            windowsWmiInput.setInterval(1200);
            windowsWmiInput.setInstances("_Total");
            windowsWmiInput.setServers("host1.splunk.com,host2.splunk.com");
            windowsWmiInput.update();
    
            assertEquals(
                "Win32_PerfFormattedData_PerfDisk_LogicalDisk",
                windowsWmiInput.getClasses());
            assertEquals(1,
                windowsWmiInput.getFields().length);
            assertTrue(
                contains(windowsWmiInput.getFields(), "Caption"));
            assertEquals("main",
                windowsWmiInput.getIndex());
            assertEquals(1200,
                windowsWmiInput.getInterval());
            assertEquals(1,
                windowsWmiInput.getInstances().length);
            assertTrue(
                contains(windowsWmiInput.getInstances(), "_Total"));
            assertEquals(
                "host1.splunk.com,host2.splunk.com",
                windowsWmiInput.getServers());
    
            // set list fields
            windowsWmiInput.setFields(new String[]{"Caption", "Description"});
            windowsWmiInput.update();
    
            assertEquals(2,
                windowsWmiInput.getFields().length);
            assertTrue(
                contains(windowsWmiInput.getFields(), "Caption"));
            assertTrue(
                contains(windowsWmiInput.getFields(), "Description"));
        }

        // Remove
        windowsWmiInput.remove();
        assertFalse(inputs.refresh().containsKey(name));
    }
}
