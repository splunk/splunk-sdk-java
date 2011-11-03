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

public class DeploymentServerClass extends Entity {
    public DeploymentServerClass(Service service, String path) {
        super(service, path);
    }

    public String getBlackList() {
        return getString("blacklist", null);
    }

    public String getBlackListDot() {
        return getString("blacklist.", null);
    }

    public String getBlackList0() {
        return getString("blacklist.0", null);
    }

    public String getBlackList1() {
        return getString("blacklist.1", null);
    }

    public String getBlackList2() {
        return getString("blacklist.2", null);
    }

    public String getBlackList3() {
        return getString("blacklist.3", null);
    }

    public String getBlackList4() {
        return getString("blacklist.4", null);
    }

    public String getBlackList5() {
        return getString("blacklist.5", null);
    }

    public String getBlackList6() {
        return getString("blacklist.6", null);
    }

    public String getBlackList7() {
        return getString("blacklist.7", null);
    }

    public String getBlackList8() {
        return getString("blacklist.8", null);
    }

    public String getBlackList9() {
        return getString("blacklist.9", null);
    }

    public boolean getContinueMatching() {
        return getBoolean("continueMatching");
    }

    public String getEndpoint() {
        return getString("endpoint", null);
    }

    public String getFilterType() {
        return getString("filterType");
    }

    public String getRepositoryLocation() {
        return getString("repositoryLocation");
    }

    public String getTargetRepositoryLocation() {
        return getString("targetRepositoryLocation", null);
    }

    public String getTmpFolder() {
        return getString("tmpFolder", null);
    }

    public String getWhiteList() {
        return getString("whitelist", null);
    }

    public String getWhiteListDot() {
        return getString("whitelist.", null);
    }

    public String getWhiteList0() {
        return getString("whitelist.0", null);
    }

    public String getWhiteList1() {
        return getString("whitelist.1", null);
    }

    public String getWhiteList2() {
        return getString("whitelist.2", null);
    }

    public String getWhiteList3() {
        return getString("whitelist.3", null);
    }

    public String getWhiteList4() {
        return getString("whitelist.4", null);
    }

    public String getWhiteList5() {
        return getString("whitelist.5", null);
    }

    public String getWhiteList6() {
        return getString("whitelist.6", null);
    }

    public String getWhiteList7() {
        return getString("whitelist.7", null);
    }

    public String getWhiteList8() {
        return getString("whitelist.8", null);
    }

    public String getWhiteList9() {
        return getString("whitelist.9", null);
    }
}