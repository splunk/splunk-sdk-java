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

import com.splunk.Entity;

import java.util.Date;

class DistributedPeerNode extends EntityNode {
    DistributedPeerNode(Entity entity) { 
        super(entity); 
    }

    @Override protected PropertyList getMetadata() {
        return new PropertyList() {{
            // UNDONE: add(String[].class, "getBundleVersions");
            add(String.class, "getGuid");
            add(String.class, "getLicenseSignature");
            add(String.class, "getPeerName");
            add(String.class, "getPeerType");
            add(String.class, "getRemotePassword");
            add(String.class, "getRemoteUsername");
            add(String.class, "getReplicationStatus");
            add(String.class, "getStatus");
            add(String.class, "getVersion");
            add(boolean.class, "isDisabled");
            add(boolean.class, "isHttps");
        }};
    }
}

