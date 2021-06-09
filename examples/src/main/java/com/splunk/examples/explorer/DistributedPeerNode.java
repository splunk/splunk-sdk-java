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

import com.splunk.Entity;

class DistributedPeerNode extends EntityNode {
    DistributedPeerNode(Entity value) { 
        super(value); 
    }

    @Override protected PropertyList getMetadata() {
        PropertyList list = super.getMetadata();
        list.add(String[].class, "getBundleVersions");
        list.add(String.class, "getGuid");
        list.add(String.class, "getLicenseSignature");
        list.add(String.class, "getPeerName");
        list.add(String.class, "getPeerType");
        list.add(String.class, "getReplicationStatus");
        list.add(String.class, "getStatus");
        list.add(String.class, "getVersion");
        list.add(boolean.class, "isDisabled");
        list.add(boolean.class, "isHttps");
        return list;
    }
}

