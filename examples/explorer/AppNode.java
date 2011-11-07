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

import com.splunk.Application;
import com.splunk.Entity;

class AppNode extends EntityNode {
    public AppNode(Entity entity) {
        super(entity);
        Application app = (Application)entity;
        String displayName = app.getLabel();
        if (displayName == null) displayName = app.getName();
        setDisplayName(displayName);
    }
    
    @Override protected PropertyList getMetadata() {
        return new PropertyList() {{
            add(boolean.class, "getCheckForUpdates");
            add(String.class, "getLabel");
            add(String.class, "getVersion");
            add(boolean.class, "isConfigured");
            add(boolean.class, "isManageable");
            add(boolean.class, "isVisible");
        }};
    }
}
