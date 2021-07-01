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

package com.splunk.examples.explorer;

import com.splunk.Service;

class ServiceNode extends ExplorerNode {
    ServiceNode(Service service) {
        super(service.getInfo(), new ServiceKids(service));
        setDisplayName(service.getInfo().getServerName());
    }

    @Override protected PropertyList getMetadata() {
        return new PropertyList() {{
            add(int.class, "getBuild");
            add(String.class, "getCpuArch");
            add(String[].class, "getLicenseKeys");
            add(String.class, "getLicenseSignature");
            add(String.class, "getLicenseState");
            add(String.class, "getMasterGuid");
            add(String.class, "getMode");
            add(String.class, "getOsBuild");
            add(String.class, "getOsVersion");
            add(String.class, "getServerName");
            add(String.class, "getVersion");
            add(boolean.class, "isFree");
            add(boolean.class, "isTrial");
        }};
    }
}
