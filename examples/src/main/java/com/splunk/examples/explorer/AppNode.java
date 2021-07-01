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

import com.splunk.Application;
import com.splunk.Entity;

class AppNode extends EntityNode {
    public AppNode(Entity value) {
        super(value);
        Application app = (Application)value;
        String displayName = app.getLabel();
        if (displayName == null) displayName = app.getName();
        setDisplayName(displayName);
    }
    
    @Override protected PropertyList getMetadata() {
        PropertyList list = super.getMetadata();
        list.add(boolean.class, "getCheckForUpdates");
        list.add(String.class, "getLabel");
        list.add(String.class, "getVersion");
        list.add(boolean.class, "isConfigured");
        list.add(boolean.class, "isManageable");
        list.add(boolean.class, "isVisible");
        return list;
    }
}
