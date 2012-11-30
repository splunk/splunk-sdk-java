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
 * The {@code ModularInputKindArgument} class is a map-like object that is 
 * specialized to represent arguments for modular input kinds.
 */
public class ModularInputKindArgument extends HashMap<String,String> {
    public enum Type { NUMBER, STRING, BOOLEAN };

    /**
     * Class constructor.
     *
     * @param template A {@code Map&lt;String,String&gt;} that is copied into
     * the new object.
     */
    ModularInputKindArgument(Map<String,String> template) {
        super();
        putAll(template);
    }

    /**
     * Returns the description of this field.
     *
     * @return The description.
     */
    public String getDescription() {
        return get("description");
    }

    /**
     * Indicates whether this argument is required when creating a modular input
     * of this kind.
     *
     * @return {@code true} if this argument is required for creating an input,
     * {@code false} if not.
     */
    public boolean getRequiredOnCreate() {
        return Value.toBoolean(get("required_on_create"));
    }

    /**
     * Indicates whether this argument is required when editing a modular input
     * of this kind.
     *
     * @return {@code true} if this argument is required for editing, 
     * {@code false} if not.
     */
    public boolean getRequiredOnEdit() {
        return Value.toBoolean(get("required_on_edit"));
    }

    /**
     * Returns the type of this argument to the modular input.
     *
     * @return A member of the {@code ModularInputKindArgumentType} enumeration 
     * ({@code Number}, {@code Boolean}, or {@code String}).
     */
    public Type getType() {
        String type = get("data_type");
        if (type.equals("number")) {
            return Type.NUMBER;
        } else if (type.equals("boolean")) {
            return Type.BOOLEAN;
        } else if (type.equals("string")) {
            return Type.STRING;
        } else {
            throw new IllegalStateException("Invalid data_type value: " + type);
        }
    }
}
