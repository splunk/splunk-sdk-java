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

/**
 * A {@code Map}-like object specialized to represent arguments to modular input kinds.
 */
public class ModularInputKindArgument extends HashMap<String,String> {
    public enum ModularInputKindArgumentType { Number, String, Boolean };

    /**
     * Class constructor.
     *
     * @param template A {@code Map&lt;String,String&gt;} which will be copied into the
     *                 new object.
     */
    ModularInputKindArgument(Map<String,String> template) {
        super();
        putAll(template);
    }

    /**
     * Return the description of this field.
     */
    public String getDescription() {
        String description = get("description");
        if (description != null) {
            return description;
        } else {
            return "";
        }
    }

    /**
     * Return a boolean extracted from the specified field.
     */
    protected boolean getBoolean(String fieldName) {
        String r = get(fieldName);
        if (r.equals("1")) {
            return true;
        } else if (r.equals("0")) {
            return false;
        } else {
            throw new IllegalArgumentException("Expected 1 or 0; found: " + r);
        }
    }

    /**
     * Return whether this argument is required when creating a modular input of this kind.
     */
    public boolean getRequiredOnCreate() {
        return getBoolean("required_on_create");
    }

    /**
     * Return whether this argument is required when editing a modular input of this kind.
     */
    public boolean getRequiredOnEdit() {
        return getBoolean("required_on_edit");
    }

    /**
     * Return the type of this argument to the modular input.
     *
     * @return One of the elements of the {@code ModularInputKindArgumentType}
     *         enumeration ({@code Number}, {@code Boolean}, or {@code String}).
     */
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