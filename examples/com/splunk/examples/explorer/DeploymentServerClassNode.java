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

class DeploymentServerClassNode extends EntityNode {
    DeploymentServerClassNode(Entity value) {
        super(value);
    }

    @Override protected PropertyList getMetadata() {
        PropertyList list = super.getMetadata();
        list.add(String.class, "getBlackList");
        list.add(String.class, "getBlackListDot");
        list.add(String.class, "getBlackList0");
        list.add(String.class, "getBlackList1");
        list.add(String.class, "getBlackList2");
        list.add(String.class, "getBlackList3");
        list.add(String.class, "getBlackList4");
        list.add(String.class, "getBlackList5");
        list.add(String.class, "getBlackList6");
        list.add(String.class, "getBlackList7");
        list.add(String.class, "getBlackList8");
        list.add(String.class, "getBlackList9");
        list.add(boolean.class, "getContinueMatching");
        list.add(String.class, "getEndpoint");
        list.add(String.class, "getFilterType");
        list.add(String.class, "getRepositoryLocation");
        list.add(String.class, "getTargetRepositoryLocation");
        list.add(String.class, "getTmpFolder");
        list.add(String.class, "getWhiteList");
        list.add(String.class, "getWhiteListDot");
        list.add(String.class, "getWhiteList0");
        list.add(String.class, "getWhiteList1");
        list.add(String.class, "getWhiteList2");
        list.add(String.class, "getWhiteList3");
        list.add(String.class, "getWhiteList4");
        list.add(String.class, "getWhiteList5");
        list.add(String.class, "getWhiteList6");
        list.add(String.class, "getWhiteList7");
        list.add(String.class, "getWhiteList8");
        list.add(String.class, "getWhiteList9");
        return list;
    }
}

