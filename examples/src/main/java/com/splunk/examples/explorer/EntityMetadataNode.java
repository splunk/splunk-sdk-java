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

import com.splunk.EntityMetadata;

import java.util.Map;

class EntityMetadataNode extends ExplorerNode {
    EntityMetadataNode(EntityMetadata value) {
        super(value);
        setDisplayName("Metadata");
    }

    @Override protected PropertyList getMetadata() {
        PropertyList list = super.getMetadata();
        list.add(boolean.class, "canChangePermissions");
        list.add(boolean.class, "canShareApp");
        list.add(boolean.class, "canShareGlobal");
        list.add(boolean.class, "canShareUser");
        list.add(boolean.class, "canWrite");
        list.add(String.class, "getApp");
        list.add(String.class, "getOwner");
        list.add(Map.class, "getPermissions");
        list.add(String.class, "getSharing");
        list.add(boolean.class, "isModifiable");
        return list;
    }
}

