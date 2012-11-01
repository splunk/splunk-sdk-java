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
import com.splunk.License;

import java.util.Date;

class LicenseNode extends EntityNode {
    LicenseNode(Entity value) {
        super(value);
        License license = (License)value;
        String displayName = license.getLabel();
        if (displayName == null) displayName = license.getName();
        setDisplayName(displayName);
    }

    @Override protected PropertyList getMetadata() {
        PropertyList list = super.getMetadata();
        list.add(Date.class, "getCreationTime");
        list.add(Date.class, "getExpirationTime");
        list.add(String[].class, "getFeatures");
        list.add(String.class, "getGroupId");
        list.add(String.class, "getLabel");
        list.add(String.class, "getLicenseHash");
        list.add(int.class, "getMaxViolations");
        list.add(long.class, "getQuota");
        list.add(String[].class, "getSourceTypes");
        list.add(String.class, "getStackId");
        list.add(String.class, "getStatus");
        list.add(String.class, "getType");
        list.add(int.class, "getWindowPeriod");
        return list;
    }
}
