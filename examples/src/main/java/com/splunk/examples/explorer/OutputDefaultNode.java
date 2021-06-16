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

class OutputDefaultNode extends EntityNode {
    OutputDefaultNode(Entity value) {
        super(value);
        setDisplayName("Output Default");
    }

    @Override protected PropertyList getMetadata() {
        PropertyList list = super.getMetadata();
        list.add(boolean.class, "autoLb");
        list.add(boolean.class, "blockOnCloning");
        list.add(boolean.class, "blockOnQueueFull");
        list.add(int.class, "getAutoLbFrequency");
        list.add(int.class, "getConnectionTimeout");
        list.add(String.class, "getDefaultGroup");
        list.add(int.class, "getDropClonedEventsOnQueueFull");
        list.add(int.class, "getDropEventsOnQueueFull");
        list.add(String.class, "getForwardedIndex0Whitelist");
        list.add(String.class, "getForwardedIndex1Blacklist");
        list.add(String.class, "getForwardedIndex2Whitelist");
        list.add(int.class, "getHeartbeatFrequency");
        list.add(int.class, "getMaxConnectionsPerIndexer");
        list.add(int.class, "getMaxFailuresPerInterval");
        list.add(long.class, "getMaxQueueSize");
        list.add(int.class, "getReadTimeout");
        list.add(int.class, "getSecsInFailureInterval");
        list.add(int.class, "getWriteTimeout");
        list.add(boolean.class, "indexAndForward");
        list.add(boolean.class, "isCompressed");
        list.add(boolean.class, "isDisabled");
        list.add(boolean.class, "isForwardedIndexFilterDisable");
        list.add(boolean.class, "isIndexAndForward");
        list.add(boolean.class, "sendCookedData");
        list.add(boolean.class, "useAck");
        return list;
    }
}
