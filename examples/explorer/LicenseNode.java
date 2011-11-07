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
import com.splunk.License;

import java.util.Date;

class LicenseNode extends ExplorerNode {
    LicenseNode(Entity entity) {
        super(entity);
        License license = (License)entity;
        String displayName = license.getLabel();
        if (displayName == null) displayName = license.getName();
        setDisplayName(displayName);
    }

    @Override protected PropertyList getMetadata() {
        return new PropertyList() {{
            add(Date.class, "getCreationTime");
            add(Date.class, "getExpirationTime");
            // UNDONE: add(String[].class, "getFeatures");
            add(String.class, "getGroupId");
            add(String.class, "getLabel");
            add(String.class, "getLicenseHash");
            add(int.class, "getMaxViolations");
            add(long.class, "getQuota");
            // UNDONE: add(String[].class, "getSourceTypes");
            add(String.class, "getStackId");
            add(String.class, "getStatus");
            add(String.class, "getType");
            add(int.class, "getWindowPeriod");
        }};
    }
}
