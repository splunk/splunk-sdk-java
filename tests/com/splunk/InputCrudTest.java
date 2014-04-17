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


import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class InputCrudTest extends InputTest {
    @Test
    public void testGetters() {
        Assert.assertFalse("No default inputs to test.", inputs.isEmpty());
        
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
            Assert.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Good
        }
        try {
            inputs.create(filename, new HashMap<String, Object>());
            Assert.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Good
        }
        
        // Create
        if (inputs.containsKey(filename)) {
            inputs.remove(filename);
        }
        inputs.create(filename, InputKind.Monitor);
        Assert.assertTrue(inputs.containsKey(filename));
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
            Assert.assertEquals("phonyregex*1", monitorInput.getBlacklist());
            Assert.assertFalse(monitorInput.getFollowTail());
            Assert.assertEquals("three.four.com", monitorInput.getHost());
            Assert.assertEquals("host*regex*", monitorInput.getHostRegex());
            if (service.versionCompare("4.2.1") >= 0) {
                Assert.assertEquals("1d", monitorInput.getIgnoreOlderThan());
                Assert.assertEquals(120, monitorInput.getTimeBeforeClose());
            }
            Assert.assertEquals("main", monitorInput.getIndex());
            Assert.assertFalse(monitorInput.getRecursive());
            Assert.assertEquals("renamedSource", monitorInput.getSource());
            Assert.assertEquals("monitor", monitorInput.getSourceType());
            Assert.assertEquals("phonyregex*2", monitorInput.getWhitelist());
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
        Assert.assertTrue(inputs.containsKey(filename));
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

            Assert.assertEquals("three.four.com", scriptInput.getHost());
            Assert.assertEquals("main", scriptInput.getIndex());
            Assert.assertEquals("120", scriptInput.getInterval());
            if (service.versionCompare("4.2.4") >= 0) {
                Assert.assertEquals("admin", scriptInput.getPassAuth());
            }
            if (!WORKAROUND_KNOWN_BUGS) {   // SPL-57223
                Assert.assertEquals("renamedSource", scriptInput.getSource());
            }
            Assert.assertEquals("script", scriptInput.getSourceType());
        }

        // Remove
        scriptInput.remove();
        Assert.assertFalse(inputs.refresh().containsKey(filename));
    }
    
    @Test
    public void testTcpInputCrud() {
        String port = "9999";   // test port
        
        deleteInputIfExists(port);
        
        // Create
        inputs.create(port, InputKind.Tcp);
        Assert.assertTrue(inputs.containsKey(port));
        TcpInput tcpInput = (TcpInput)inputs.get(port);

        // Probe
        {
            Assert.assertFalse("ip".equals(tcpInput.getConnectionHost()));
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

            Assert.assertEquals("ip", tcpInput.getConnectionHost());
            Assert.assertEquals("myhost", tcpInput.getHost());
            Assert.assertEquals("main", tcpInput.getIndex());
            Assert.assertEquals("indexQueue", tcpInput.getQueue());
            Assert.assertEquals("tcp", tcpInput.getSource());
            Assert.assertEquals("sdk-tests", tcpInput.getSourceType());
            Assert.assertFalse(tcpInput.getSSL());
        }
        
        // Remove
        Assert.assertTrue(inputs.refresh().containsKey(port));
        tcpInput.remove();
        Assert.assertFalse(inputs.refresh().containsKey(port));
    }
    
    @Test
    public void testTcpSplunkInputCrud() {
        String port = "9998";   // test port

        deleteInputIfExists(port);

        // Create
        inputs.create(port, InputKind.TcpSplunk);
        Assert.assertTrue(inputs.containsKey(port));
        TcpSplunkInput tcpSplunkInput =
                (TcpSplunkInput)inputs.get(port);

        // Probe
        {
            Assert.assertFalse("dns".equals(tcpSplunkInput.getConnectionHost()));
            tcpSplunkInput.setConnectionHost("dns");
            tcpSplunkInput.setHost("myhost");
            tcpSplunkInput.setSSL(false);
            tcpSplunkInput.update();

            Assert.assertEquals("dns", tcpSplunkInput.getConnectionHost());
            Assert.assertEquals("myhost", tcpSplunkInput.getHost());
            Assert.assertFalse(tcpSplunkInput.getSSL());
        }

        // Remove
        tcpSplunkInput.remove();
        Assert.assertFalse(inputs.refresh().containsKey(port));
    }
    
    @Test
    public void testUdpInputCrud() {
        String port = "9997";   // test port

        deleteInputIfExists(port);

        // Create
        inputs.create(port, InputKind.Udp);
        Assert.assertTrue(inputs.containsKey(port));
        UdpInput udpInput = (UdpInput)inputs.get(port);

        // Probe
        {
            Assert.assertFalse("dns".equals(udpInput.getConnectionHost()));
            udpInput.setConnectionHost("dns");
            udpInput.setHost("myhost");
            udpInput.setIndex("main");
            udpInput.setNoAppendingTimeStamp(true);
            udpInput.setNoPriorityStripping(true);
            udpInput.setQueue("indexQueue");
            udpInput.setSource("mysource");
            udpInput.setSourceType("mysourcetype");
            udpInput.update();
    
            Assert.assertEquals("dns", udpInput.getConnectionHost());
            Assert.assertEquals("myhost", udpInput.getHost());
            Assert.assertEquals("main", udpInput.getIndex());
            Assert.assertTrue(udpInput.getNoAppendingTimeStamp());
            Assert.assertTrue(udpInput.getNoPriorityStripping());
            Assert.assertEquals("indexQueue", udpInput.getQueue());
            Assert.assertEquals("mysource", udpInput.getSource());
            Assert.assertEquals("mysourcetype", udpInput.getSourceType());
        }

        // Remove
        udpInput.remove();
        Assert.assertFalse(inputs.refresh().containsKey(port));
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
        Assert.assertTrue(inputs.containsKey(name));
        WindowsActiveDirectoryInput windowsActiveDirectoryInput =
                (WindowsActiveDirectoryInput)inputs.get(name);

        // Probe
        {
            windowsActiveDirectoryInput.setStartingNode("startnode");
            windowsActiveDirectoryInput.setIndex("main");
            windowsActiveDirectoryInput.setMonitorSubtree(false);
            windowsActiveDirectoryInput.setTargetDc("otherDC");
            windowsActiveDirectoryInput.update();

            Assert.assertEquals("main", windowsActiveDirectoryInput.getIndex());
            Assert.assertFalse(windowsActiveDirectoryInput.getMonitorSubtree());
            Assert.assertEquals("startnode", windowsActiveDirectoryInput.getStartingNode());
            Assert.assertEquals("main", windowsActiveDirectoryInput.getIndex());
        }

        // Remove
        windowsActiveDirectoryInput.remove();
        Assert.assertFalse(inputs.refresh().containsKey(name));
    }
    
    @Test
    public void testWindowsEventLogInputCrud() {
        if (!service.getInfo().getOsName().equals("Windows")) {
            return;
        }
        
        String name = "sdk-input-wel";
        
        deleteInputIfExists(name);
        Assert.assertFalse(inputs.refresh().containsKey(name));

        // Create
        inputs.create(
                name, InputKind.WindowsEventLog,
                new Args("lookup_host", "127.0.0.1"));
        Assert.assertTrue(inputs.containsKey(name));
        WindowsEventLogInput windowsEventLogInput =
                 (WindowsEventLogInput)inputs.get(name);

        // Probe
        {
            windowsEventLogInput.setIndex("main");
            windowsEventLogInput.setLookupHost("127.0.0.1");
            windowsEventLogInput.setHosts("one.two.three,four.five.six");
            windowsEventLogInput.update();
    
            Assert.assertEquals("127.0.0.1", windowsEventLogInput.getLookupHost());
            Assert.assertEquals("one.two.three,four.five.six", windowsEventLogInput.getHosts());
            Assert.assertEquals("main", windowsEventLogInput.getIndex());
        }

        // Remove
        windowsEventLogInput.remove();
        Assert.assertFalse(inputs.refresh().containsKey(name));
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
        Assert.assertTrue(inputs.containsKey(name));
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
    
            Assert.assertEquals(1, windowsPerfmonInput.getCounters().length);
            Assert.assertTrue(contains(windowsPerfmonInput.getCounters(), "% Privileged Time"));
            Assert.assertEquals(windowsPerfmonInput.getIndex(), "main");
            Assert.assertTrue(contains(windowsPerfmonInput.getInstances(), "wininit"));
            Assert.assertEquals(1200, windowsPerfmonInput.getInterval());
            Assert.assertEquals("Process", windowsPerfmonInput.getObject());
            
            // set multi-series values and update.
            windowsPerfmonInput.setCounters(new String[] {"% Privileged Time","% User Time"});
            windowsPerfmonInput.setInstances(new String[] {"smss","csrss"});
            windowsPerfmonInput.update();
    
            Assert.assertEquals(2, windowsPerfmonInput.getCounters().length);
            Assert.assertTrue(contains(windowsPerfmonInput.getCounters(), "% Privileged Time"));
            Assert.assertTrue(contains(windowsPerfmonInput.getCounters(), "% User Time"));
    
            Assert.assertEquals(2, windowsPerfmonInput.getInstances().length);
            Assert.assertTrue(contains(windowsPerfmonInput.getInstances(), "smss"));
            Assert.assertTrue(contains(windowsPerfmonInput.getInstances(), "csrss"));
        }

        // Remove
        windowsPerfmonInput.remove();
        Assert.assertFalse(inputs.refresh().containsKey(name));
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
        Assert.assertTrue(inputs.containsKey(name));
        WindowsRegistryInput windowsRegistryInput =
                (WindowsRegistryInput)inputs.get(name);

        // Probe
        {
            windowsRegistryInput.setIndex("main");
            windowsRegistryInput.setMonitorSubnodes(true);
            windowsRegistryInput.update();
    
            Assert.assertFalse(windowsRegistryInput.getBaseline());
            Assert.assertEquals("main",
                    windowsRegistryInput.getIndex());
    
            // adjust a few of the arguments
            String[] wriType = {"create", "delete"};
            windowsRegistryInput.setType(wriType);
            windowsRegistryInput.setBaseline(false);
            windowsRegistryInput.update();
    
            Assert.assertEquals("*",
                    windowsRegistryInput.getProc());
            Assert.assertTrue(
                    windowsRegistryInput.getType()[0].equals("create"));
            Assert.assertTrue(
                    windowsRegistryInput.getType()[1].equals("delete"));
            Assert.assertFalse(windowsRegistryInput.getBaseline());
        }

        // Remove
        windowsRegistryInput.remove();
        Assert.assertFalse(inputs.refresh().containsKey(name));
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
        Assert.assertTrue(inputs.containsKey(name));
        WindowsWmiInput windowsWmiInput =
                (WindowsWmiInput)inputs.get(name);

        // Probe
        {
            Assert.assertEquals(
                    "Win32_PerfFormattedData_PerfOS_Processor",
                    windowsWmiInput.getClasses());
            Assert.assertEquals(600,
                    windowsWmiInput.getInterval());
            Assert.assertEquals(service.getHost(),
                    windowsWmiInput.getLookupHost());
    
            windowsWmiInput.setClasses("PerfDisk_LogicalDisk");
            windowsWmiInput.setFields("Caption");
            windowsWmiInput.setIndex("main");
            windowsWmiInput.setInterval(1200);
            windowsWmiInput.setInstances("_Total");
            windowsWmiInput.setServers("host1.splunk.com,host2.splunk.com");
            windowsWmiInput.update();
    
            Assert.assertEquals(
                    "Win32_PerfFormattedData_PerfDisk_LogicalDisk",
                    windowsWmiInput.getClasses());
            Assert.assertEquals(1,
                    windowsWmiInput.getFields().length);
            Assert.assertTrue(
                    contains(windowsWmiInput.getFields(), "Caption"));
            Assert.assertEquals("main",
                    windowsWmiInput.getIndex());
            Assert.assertEquals(1200,
                    windowsWmiInput.getInterval());
            Assert.assertEquals(1,
                    windowsWmiInput.getInstances().length);
            Assert.assertTrue(
                    contains(windowsWmiInput.getInstances(), "_Total"));
            Assert.assertEquals(
                    "host1.splunk.com,host2.splunk.com",
                    windowsWmiInput.getServers());
    
            // set list fields
            windowsWmiInput.setFields(new String[]{"Caption", "Description"});
            windowsWmiInput.update();
    
            Assert.assertEquals(2,
                    windowsWmiInput.getFields().length);
            Assert.assertTrue(
                    contains(windowsWmiInput.getFields(), "Caption"));
            Assert.assertTrue(
                    contains(windowsWmiInput.getFields(), "Description"));
        }

        // Remove
        windowsWmiInput.remove();
        Assert.assertFalse(inputs.refresh().containsKey(name));
    }
}
