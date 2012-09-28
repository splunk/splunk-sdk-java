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
 * {@code ModularInputKind} represents a particular modular input. The actual inputs of this kind can be
 * accessed from the InputCollection.
 */
public class ModularInputKind extends Entity {
    Map<String, Map<String,String>> args;

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     * @param path The entity's endpoint.
     */
    public ModularInputKind(Service service, String path) {
        super(service, path);
        Map<String, Map<String, Map<String,String>>> endpoint = (Map<String, Map<String, Map<String,String>>>)get("endpoint");
        this.args = endpoint.get("args");
    }

    /**
     * Returns a {@code Map} with all the argument names as keys, and the {@code ModularInputKindArgument} as the
     * corresponding value.
     */
    public Map<String, ModularInputKindArgument> getArguments() {
        Map<String, ModularInputKindArgument> arguments = new HashMap<String, ModularInputKindArgument>();
        for (String argumentName : args.keySet()) {
            arguments.put(argumentName, getArgument(argumentName));
        }
        return arguments;
    }

    /**
     * Return the streaming mode of this modular input kind.
     *
     * @return {@code "xml"} or {@code "simple"}
     */
    public String getStreamingMode() {
        String mode = getString("streaming_mode");
        return mode;
    }

    /**
     * Get a {@code Map}-like object representing a particular argument of this modular input kind.
     *
     * @param argumentName Name of the argument to fetch.
     * @return a {@code ModularInputKindArgument} representing the argument, or {@code null}
     *         if the argument does not exist.
     */
    public ModularInputKindArgument getArgument(String argumentName) {
        if (this.args.get(argumentName) != null) {
            return new ModularInputKindArgument(this.args.get(argumentName));
        } else {
            return null;
        }
    }

    /**
     * Return the description of this modular input kind.
     *
     * @return A string giving the description.
     */
    public String getDescription() {
        if (containsKey("description")) {
            return getString("description");
        } else {
            return "";
        }
    }

    /**
     * Return the title of thhis modular input kind.
     *
     * The title is the human readable string displayed in splunkweb rather than the name
     * you access it under in the REST API.
     *
     * @return A string containing the title.
     */
    public String getTitle() {
        if (containsKey("title")) {
            return getString("title");
        } else {
            return super.getTitle();
        }
    }

    /**
     * Check whether this {@code ModularInputKind} has an argument of the given name.
     *
     * @param argumentName The argument to look up.
     * @return {@code true} if the argument exists; {@code false} otherwise.
     */
    public boolean hasArgument(String argumentName) {
        return this.args.containsKey(argumentName);
    }
}
