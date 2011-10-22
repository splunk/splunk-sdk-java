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

public class Application extends Entity {
    public Application(Service service, String path) {
        super(service, path);
    }

    public Boolean getCheckForUpdates() {
        return Value.getBoolean(getContent(), "check_for_updates");
    }

    public String getLabel() {
        return Value.getString(getContent(), "label", "");
    }

    public String getVersion() {
        return Value.getString(getContent(), "version", null);
    }

    public Boolean isConfigured() {
        return Value.getBoolean(getContent(), "configured");
    }

    public Boolean isManageable() {
        return Value.getBoolean(getContent(), "manageable");
    }

    public Boolean isVisible() {
        return Value.getBoolean(getContent(), "visible");
    }
}

