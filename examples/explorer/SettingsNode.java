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

class SettingsNode extends EntityNode {
    SettingsNode(Entity entity) {
        super(entity);
        setDisplayName("Settings");
    }

    @Override protected PropertyList getMetadata() {
        return new PropertyList() {{
            add(String.class, "getSplunkDB");
            add(String.class, "getSplunkHome");
            add(boolean.class, "getEnableSplunkWebSSL");
            add(String.class, "getHost");
            add(int.class, "getHttpPort");
            add(int.class, "getMgmtPort");
            add(int.class, "getMinFreeSpace");
            add(String.class, "getPass4SymmKey");
            add(String.class, "getServerName");
            add(String.class, "getSessionTimeout");
            add(boolean.class, "getStartWebServer");
            add(String.class, "getTrustedIP");
        }};
    }
}
