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

class DeploymentServerClassNode extends EntityNode {
    DeploymentServerClassNode(Entity entity) {
        super(entity);
    }

    @Override protected PropertyList getMetadata() {
        return new PropertyList() {{
            add(String.class, "getBlackList");
            add(String.class, "getBlackListDot");
            add(String.class, "getBlackList0");
            add(String.class, "getBlackList1");
            add(String.class, "getBlackList2");
            add(String.class, "getBlackList3");
            add(String.class, "getBlackList4");
            add(String.class, "getBlackList5");
            add(String.class, "getBlackList6");
            add(String.class, "getBlackList7");
            add(String.class, "getBlackList8");
            add(String.class, "getBlackList9");
            add(boolean.class, "getContinueMatching");
            add(String.class, "getEndpoint");
            add(String.class, "getFilterType");
            add(String.class, "getRepositoryLocation");
            add(String.class, "getTargetRepositoryLocation");
            add(String.class, "getTmpFolder");
            add(String.class, "getWhiteList");
            add(String.class, "getWhiteListDot");
            add(String.class, "getWhiteList0");
            add(String.class, "getWhiteList1");
            add(String.class, "getWhiteList2");
            add(String.class, "getWhiteList3");
            add(String.class, "getWhiteList4");
            add(String.class, "getWhiteList5");
            add(String.class, "getWhiteList6");
            add(String.class, "getWhiteList7");
            add(String.class, "getWhiteList8");
            add(String.class, "getWhiteList9");
        }};
    }
}

