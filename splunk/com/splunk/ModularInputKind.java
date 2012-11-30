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
import java.util.List;

/**
 * The {@code ModularInputKind} class represents a particular modular input. 
 * The actual inputs of this kind can be accessed from the 
 * {@code InputCollection} object.
 */
public class ModularInputKind extends Entity {
    protected Map<String, Map<String,String>> args;

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The entity's endpoint.
     */
    ModularInputKind(Service service, String path) {
        super(service, path);
        Map<String, Map<String, Map<String,String>>> endpoint =
                (Map<String, Map<String, Map<String,String>>>)get("endpoint");
        this.args = endpoint.get("args");
    }

    /**
     * Returns an argument map that contains the argument names as keys, and the 
     * {@code ModularInputKindArgument}s as corresponding values.
     *
     * @return A {@code Map} containing the argument key-value pairs.
     */
    public Map<String, ModularInputKindArgument> getArguments() {
        Map<String, ModularInputKindArgument> arguments = new HashMap<String, ModularInputKindArgument>();
        for (String argumentName : args.keySet()) {
            arguments.put(argumentName, getArgument(argumentName));
        }
        return arguments;
    }

    /**
     * Returns the streaming mode of this modular input kind.
     *
     * @return The streaming mode ("xml" or "simple"). 
     */
    public String getStreamingMode() {
        String mode = getString("streaming_mode");
        return mode;
    }

    /**
     * Returns a map-like object representing a particular argument of this 
     * modular input kind.
     *
     * @param argumentName The name of the argument to retrieve.
     * @return A {@code ModularInputKindArgument} object representing the given
     * argument, or {@code null} if the argument does not exist.
     */
    public ModularInputKindArgument getArgument(String argumentName) {
        if (this.args.get(argumentName) != null) {
            return new ModularInputKindArgument(this.args.get(argumentName));
        } else {
            return null;
        }
    }

    /**
     * Returns the description of this modular input kind.
     *
     * @return A string containing the description.
     */
    public String getDescription() {
        return getString("description", null);
    }

    /**
     * Returns the title of this modular input kind, which is also displayed in
     * Splunk Web (rather than the name used in the REST API). 
     *
     * @return A string containing the title.
     */
    public String getTitle() {
        return getString("title", null);
    }

    /**
     * Indicates whether this modular input kind has a given argument.
     *
     * @param argumentName The argument to look up.
     * @return {@code true} if the argument exists, {@code false} if not.
     */
    public boolean hasArgument(String argumentName) {
        return this.args.containsKey(argumentName);
    }
}
