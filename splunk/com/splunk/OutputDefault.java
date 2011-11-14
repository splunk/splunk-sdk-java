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

public class OutputDefault extends Entity {
    public OutputDefault(Service service) {
        super(service, "data/outputs/tcp/default");
    }

    public boolean autoLb() {
        return getBoolean("autoLB");
    }

    public boolean blockOnCloning() {
        return getBoolean("blockOnCloning", true); // UNDONE: default?
    }

    public boolean blockOnQueueFull() {
        return getBoolean("blockOnQueueFull", true); // UNDONE: default?
    }

    public int getAutoLbFrequency() {
        return getInteger("autoLBFrequency", 0); // UNDONE: default?
    }

    public int getConnectionTimeout() {
        return getInteger("connectionTimeout", 0); // UNDONE: default?
    }

    public String getDefaultGroup() {
        return getString("defaultGroup", null);
    }

    public int getDropClonedEventsOnQueueFull() {
        return getInteger("dropClonedEventsOnQueueFull", 0); // UNDONE: default?
    }

    public int getDropEventsOnQueueFull() {
        return getInteger("dropEventsOnQueueFull", 0); //  UNDONE: default?
    }

    public String getForwardedIndex0Whitelist() {
        return getString("forwardedindex.0.whitelist");
    }

    public String getForwardedIndex1Blacklist() {
        return getString("forwardedindex.1.blacklist");
    }

    public String getForwardedIndex2Whitelist() {
        return getString("forwardedindex.2.whitelist");
    }

    public int getHeartbeatFrequency() {
        return getInteger("heartbeatFrequency", 0); // UNDONE: default?
    }

    public int getMaxConnectionsPerIndexer() {
        return getInteger("maxConnectionsPerIndexer", 0); // UNDONE: default?
    }

    public int getMaxFailuresPerInterval() {
        return getInteger("maxFailuresPerInterval", 0); // UNDONE: default?
    }

    public long getMaxQueueSize() {
        return getByteCount("maxQueueSize");
    }

    public int getReadTimeout() {
        return getInteger("readTimeout", 0); // UNDONE: default?
    }

    public int getSecsInFailureInterval() {
        return getInteger("secsInFailureInterval", 0); // UNDONE: default?
    }

    public int getWriteTimeout() {
       return getInteger("writeTimeout", 0); // UNDONE: default?
    }

    public boolean indexAndForward() {
        return getBoolean("indexAndForward");
    }

    public boolean isCompressed() {
        return getBoolean("compressed", false); // UNDONE: default?
    }

    public boolean isDisabled() {
        return getBoolean("disabled");
    }

    public boolean isForwardedIndexFilterDisable() {
        return getBoolean("forwardedindex.filter.disable");
    }

    public boolean isIndexAndForward() {
        return getBoolean("indexAndForward");
    }

    public boolean sendCookedData() {
        return getBoolean("sendCookedData", false); // UNDONE: default?
    }

    public boolean useAck() {
        return getBoolean("useACK", false); // UNDONE: default?
    }
}

