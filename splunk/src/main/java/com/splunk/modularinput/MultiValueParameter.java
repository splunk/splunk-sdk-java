/*
 * Copyright 2013 Splunk, Inc.
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

package com.splunk.modularinput;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code MultiValueParameter} class represents a parameter containing multiple values that is passed as part of a definition
 * of a modular input instance. {@code MultiValueParameter} objects correspond to XML fragments of the form:
 *
 * <pre>
 * {@code
 * <param_list name="multiValue">
 *     <value>value1</value>
 *     <value>value2</value>
 * </param_list>
 * }
 * </pre>
 */
public class MultiValueParameter extends Parameter {
    private final String name;
    private final List<String> values;

    // Note: package private constructor by design so parameters cannot be instantiated by the user.
    MultiValueParameter(String name) {
        this.name = name;
        this.values = new ArrayList<String>();
    }

    /**
     * Gets the name of this parameter.
     *
     * @return The name of this parameter.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets a list of all values of this parameter.
     *
     * @return A list of all values of this parameter.
     */
    public List<String> getValues() {
        return this.values;
    }

    // Package private by design.
    void appendValue(String value) {
        this.values.add(value);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MultiValueParameter)) {
            return false;
        } else {
            MultiValueParameter that = (MultiValueParameter)other;
            return this.values.equals(that.values) && this.name.equals(that.name);
        }
    }

    @Override
    public int hashCode() {
        return (this.name == null ? 0 : this.name.hashCode()) ^
                (this.values == null ? 0 : this.values.hashCode());
    }
}
