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

public class User extends Entity {
    User(Service service, String path) {
        super(service, path);
    }

    public String getDefaultApp() {
        return getString("defaultApp", null);
    }

    public boolean getDefaultAppIsUserOverride() {
        return getBoolean("defaultAppIsUserOverride");
    }

    public String getDefaultAppSourceRole() {
        return getString("defaultAppSourceRole");
    }

    public String getEmail() {
        return getString("email", null);
    }

    public String getPassword() {
        return getString("password", null);
    }

    public String getRealName() {
        return getString("realname", null);
    }

    public String[] getRoles() {
        return getStringArray("roles", null);
    }

    public String getType() {
        return getString("type", null);
    }

    public String getTz() {
        return getString("tz", null);
    }
}
