/*
 * Copyright 2012 Splunk, Inc.
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

import java.util.HashMap;
import java.util.Map;

public class ModularInputKindArgument extends HashMap<String,String> {
    public enum ModularInputKindArgumentType { Number, String, Boolean };

    ModularInputKindArgument(Map<String,String> template) {
        for (String key : template.keySet()) {
            put(key, template.get(key));
        }
    }

    public String getDescription() {
        String description = get("description");
        if (description != null) {
            return description;
        } else {
            return "";
        }
    }

    public boolean getRequiredOnCreate() {
        String r = get("required_on_create");
        if (r.equals("1")) {
            return true;
        } else if (r.equals("0")) {
            return false;
        } else {
            throw new IllegalArgumentException("Expected 1 or 0; found: " + r);
        }
    }

    public boolean getRequiredOnEdit() {
        String r = get("required_on_edit");
        if (r.equals("1")) {
            return true;
        } else if (r.equals("0")) {
            return false;
        } else {
            throw new IllegalArgumentException("Expected 1 or 0; found: " + r);
        }
    }

    public ModularInputKindArgumentType getType() {
        String type = get("data_type");
        if (type.equals("number")) {
            return ModularInputKindArgumentType.Number;
        } else if (type.equals("boolean")) {
            return ModularInputKindArgumentType.Boolean;
        } else if (type.equals("string")) {
            return ModularInputKindArgumentType.String;
        } else {
            throw new IllegalArgumentException("Invalid data_type value: " + type);
        }
    }
}
