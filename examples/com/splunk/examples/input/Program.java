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

package com.splunk.examples.input;

import com.splunk.*;

/**
 * Generate events into an index using either stream, submit or raw tcp
 * methods.
 */

public class Program {

    public static void main(String[] args) {
        try {
            run(args);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

   static void DisplaySpecificInput(Input input) {
        InputKind inputKind = input.getKind();

        System.out.println("    **type specific settings");

        if (inputKind == InputKind.Monitor) {
            MonitorInput monitorInput = (MonitorInput) input;
            System.out.println(
                    "      file count:      " + monitorInput.getFileCount());
            System.out.println(
                    "      host:            " + monitorInput.getHost());
            System.out.println(
                    "      index:           " + monitorInput.getIndex());
            System.out.println(
                    "      receive buffer:  " + monitorInput.getRcvBuf());
        } else if (inputKind == InputKind.Script) {
            ScriptInput scriptInput = (ScriptInput) input;
            System.out.println(
                    "      group:           " + scriptInput.getGroup());
            System.out.println(
                    "      host:            " + scriptInput.getHost());
            System.out.println(
                    "      index:           " + scriptInput.getIndex());
            System.out.println(
                    "      interval:        " + scriptInput.getInterval());
            System.out.println(
                    "      receive buffer:  " + scriptInput.getRcvBuf());
        } else if (inputKind == InputKind.Tcp) {
            TcpInput tcpInput = (TcpInput) input;
            System.out.println(
                    "      connection host: " + tcpInput.getConnectionHost());
            System.out.println(
                    "      group:           " + tcpInput.getGroup());
            System.out.println(
                    "      host:            " + tcpInput.getHost());
            System.out.println(
                    "      index:           " + tcpInput.getIndex());
            System.out.println(
                    "      queue:           " + tcpInput.getQueue());
            System.out.println(
                    "      receive buffer:  " + tcpInput.getRcvBuf());
            System.out.println(
                    "      restrict to host:" + tcpInput.getRestrictToHost());
            System.out.println(
                    "      source:          " + tcpInput.getSource());
            System.out.println(
                    "      source type:     " + tcpInput.getSourceType());
            System.out.println(
                    "      SSL:             " + tcpInput.getSSL());
        } else if (inputKind == InputKind.TcpSplunk) {
            TcpSplunkInput tcpSplunkInput = (TcpSplunkInput) input;
            System.out.println(
                    "      connection host: " + tcpSplunkInput.getConnectionHost());
            System.out.println(
                    "      group:           " + tcpSplunkInput.getGroup());
            System.out.println(
                    "      host:            " + tcpSplunkInput.getHost());
            System.out.println(
                    "      index:           " + tcpSplunkInput.getIndex());
            System.out.println(
                    "      queue:           " + tcpSplunkInput.getQueue());
            System.out.println(
                    "      receive buffer:  " + tcpSplunkInput.getRcvBuf());
            System.out.println(
                    "      source:          " + tcpSplunkInput.getSource());
            System.out.println(
                    "      source type:     " + tcpSplunkInput.getSourceType());
            System.out.println(
                    "      SSL:             " + tcpSplunkInput.getSSL());
        } else if (inputKind == InputKind.Udp) {
            UdpInput udpInput = (UdpInput) input;
            System.out.println(
                    "      connection host: " + udpInput.getConnectionHost());
            System.out.println(
                    "      group:           " + udpInput.getGroup());
            System.out.println(
                    "      host:            " + udpInput.getHost());
            System.out.println(
                    "      index:           " + udpInput.getIndex());
            System.out.println(
                    "      queue:           " + udpInput.getQueue());
            System.out.println(
                    "      receive buffer:  " + udpInput.getRcvBuf());
            System.out.println(
                    "      source:          " + udpInput.getSource());
            System.out.println(
                    "      source type:     " + udpInput.getSourceType());
            System.out.println(
                    "      no timestamp append:" +
                            udpInput.getNoAppendingTimeStamp());
            System.out.println(
                    "      no priority stripping:" +
                            udpInput.getNoPriorityStripping());
        } else if (inputKind == InputKind.WindowsActiveDirectory) {
            WindowsActiveDirectoryInput windowsActiveDirectoryInput =
                    (WindowsActiveDirectoryInput) input;
            System.out.println(
                    "      index:           " +
                            windowsActiveDirectoryInput.getIndex());
            System.out.println(
                    "      monitor subtree: " +
                            windowsActiveDirectoryInput.getMonitorSubtree());
            System.out.println(
                    "      starting node:   " +
                            windowsActiveDirectoryInput.getStartingNode());
            System.out.println(
                    "      target DC:       " +
                            windowsActiveDirectoryInput.getTargetDc());
        } else if (inputKind == InputKind.WindowsEventLog) {
            WindowsEventLogInput windowsEventLogInput =
                    (WindowsEventLogInput) input;
            System.out.println(
                    "      hosts:           " +
                            windowsEventLogInput.getHosts());
            System.out.println(
                    "      index:           " +
                            windowsEventLogInput.getIndex());
            System.out.println(
                    "      local name:      " +
                            windowsEventLogInput.getLocalName());
            String[] logs = windowsEventLogInput.getLogs();
            System.out.println("      logs:");
            if (logs != null)
                    for (String log: logs) {
                        System.out.println("            " + log);
                    }
            System.out.println(
                    "      lookup host:     " +
                            windowsEventLogInput.getLookupHost());
        } else if (inputKind == InputKind.WindowsPerfmon) {
            WindowsPerfmonInput windowsPerfmonInput =
                    (WindowsPerfmonInput) input;
            System.out.println(
                    "      counters:        " +
                            windowsPerfmonInput.getCounters());
            System.out.println(
                    "      index:           " +
                            windowsPerfmonInput.getIndex());
            System.out.println(
                    "      instances:       " +
                            windowsPerfmonInput.getInstances());
            System.out.println(
                    "      interval:        " +
                            windowsPerfmonInput.getInterval());
            System.out.println(
                    "      object:          " +
                            windowsPerfmonInput.getObject());
        } else if (inputKind == InputKind.WindowsRegistry) {
            WindowsRegistryInput windowsRegistryInput =
                    (WindowsRegistryInput) input;
            System.out.println(
                    "      baseline:        " +
                            windowsRegistryInput.getBaseline());
            System.out.println(
                    "      hive:            " +
                            windowsRegistryInput.getHive());
            System.out.println(
                    "      index:           " +
                            windowsRegistryInput.getIndex());
            System.out.println(
                    "      monitor subnodes:" +
                            windowsRegistryInput.getMonitorSubnodes());
            System.out.println(
                    "      process:         " +
                            windowsRegistryInput.getProc());
            System.out.println(
                    "      type:            " +
                            windowsRegistryInput.getType());
        } else if (inputKind == InputKind.WindowsWmi) {
            WindowsWmiInput windowsWmiInput = (WindowsWmiInput) input;
            System.out.println(
                    "      WMI input:       " +
                            windowsWmiInput.getClasses());
            String[] fields = windowsWmiInput.getFields();
            System.out.println("      fields:");
            for (String field: fields) {
                System.out.println("            " + field);
            }
            System.out.println(
                    "      index:           " +
                            windowsWmiInput.getIndex());

            String[] instances = windowsWmiInput.getInstances();
            System.out.println("      instances:");
            for (String instance: instances) {
                System.out.println("            " + instance);
            }
            System.out.println(
                    "      interval:        " +
                            windowsWmiInput.getInterval());
            System.out.println(
                    "      local name:      " +
                            windowsWmiInput.getLocalName());
            System.out.println(
                    "      lookup host:     " +
                            windowsWmiInput.getLookupHost());
            System.out.println(
                    "      server:          " +
                            windowsWmiInput.getServers());
            System.out.println(
                    "      WQL:             " + windowsWmiInput.getWql());
        }
    }

   static void run(String[] argsIn) throws Exception {

        Command command;
        Service service;

        command = Command.splunk("input");
        service = Service.connect(command.opts);

        InputCollection inputs = service.getInputs();

        // Iterate inputs and make sure we can read them.
        for (Input input : inputs.values()) {
            System.out.println("Input name:  " + input.getName());
            System.out.println("      title: " + input.getTitle());
            System.out.println("      path:  " + input.getPath());
            System.out.println("      type:  " + input.getKind());
            DisplaySpecificInput(input);
            System.out.println("\n\n");
        }
    }
}
