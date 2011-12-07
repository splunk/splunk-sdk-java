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

package com.splunk.sdk.explorer;

import com.splunk.Entity;
import com.splunk.Input;
import com.splunk.InputKind;

class InputNode extends EntityNode {
    InputNode(Entity value) { 
        super(value); 
    }

    @Override protected PropertyList getMetadata() {
        PropertyList list = super.getMetadata();
        list.add(InputKind.class, "getKind");
        Input input = (Input)value;
        switch (input.getKind()) {
        case Monitor:
            list.add(int.class, "getFileCount");
            list.add(String.class, "getHost");
            list.add(String.class, "getIndex");
            list.add(int.class, "getRcvBuf");
            break;
        case Script:
            list.add(String.class, "getGroup");
            list.add(String.class, "getHost");
            list.add(String.class, "getIndex");
            list.add(int.class, "getInterval");
            list.add(int.class, "getRcvBuf");
            break;
        case Tcp:
            list.add(String.class, "getGroup");
            list.add(String.class, "getHost");
            list.add(String.class, "getIndex");
            list.add(int.class, "getRcvBuf");
            list.add(String.class, "getRestrictToHost");
            break;
        case TcpSplunk:
        case Udp:
            list.add(String.class, "getGroup");
            list.add(String.class, "getHost");
            list.add(String.class, "getIndex");
            list.add(int.class, "getRcvBuf");
            break;
        case WindowsActiveDirectory:
            list.add(String.class, "getIndex");
            list.add(boolean.class, "getMonitorSubtree");
            break;
        case WindowsEventLog:
            list.add(String.class, "getHosts");
            list.add(String.class, "getIndex");
            list.add(String[].class, "getLogs");
            list.add(String.class, "getLocalName");
            list.add(String.class, "getLookupHost");
            break;
        case WindowsPerfmon:
            list.add(String.class, "getIndex");
            list.add(String[].class, "getInstances");
            list.add(int.class, "getInterval");
            list.add(String.class, "getObject");
            break;
        case WindowsRegistry:
            list.add(boolean.class, "getBaseline");
            list.add(String.class, "getProc");
            list.add(String.class, "getHive");
            list.add(String.class, "getIndex");
            list.add(boolean.class, "getMonitorSubnoes");
            list.add(String.class, "getType");
            break;
        case WindowsWmi:
            list.add(String.class, "getClasses");
            list.add(String[].class, "getFields");
            list.add(String.class, "getIndex");
            list.add(String[].class, "getInstances");
            list.add(int.class, "getInterval");
            list.add(String.class, "getLookupHost");
            list.add(String.class, "getLocalName");
            list.add(String.class, "getServer");
            list.add(String.class, "getWq1");
            break;
        }
        return list;
    }
}
