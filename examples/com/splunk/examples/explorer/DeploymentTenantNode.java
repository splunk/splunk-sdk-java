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

class DeploymentTenantNode extends EntityNode {
    DeploymentTenantNode(Entity value) {
        super(value);
    }

    @Override protected PropertyList getMetadata() {
        return new PropertyList() {{
            add(boolean.class, "getCheckNew");
            add(String.class, "getWhiteList0");
        }};
    }
}

