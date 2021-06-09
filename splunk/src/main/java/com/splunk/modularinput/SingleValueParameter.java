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

/**
 * The {@code SingleValueParameter} class represents a parameter as part of a modular input instance that
 * contains only a single value. This corresponds to XML fragments of the form:
 *
 * <pre>
 * {@code
 * <param name="param1">value11</param>
 * }
 * </pre>
 */
public class SingleValueParameter extends Parameter {
    private final String name;
    private final String value;

    // Package private by design.
    SingleValueParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name of this parameter.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the parameter as found (as a String), without trying to coerce it to another type.
     *
     * If your field is Boolean or numeric, use {@code getBoolean} or one of {@code getInt}, {@code getLong}, 
     * {@code getFloat}, and {@code getDouble} instead.
     *
     * @return The value of this parameter as a String.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Tries to coerce the value of this parameter to a Boolean. A range of values (true, t, on, 1, y, yes) are
     * interpreted as {@code true}, and a similar range (false, f, off, 0, no, n) as {@code false}. Everything
     * else, including null, results in a {@code MalformedDataException}.
     *
     * @return The value of this parameter coerced to a Boolean. 
     * @throws MalformedDataException If the value cannot be coerced to a boolean.
     */
    public boolean getBoolean() throws MalformedDataException {
        return XmlUtil.normalizeBoolean(getValue());
    }

    /**
     * Coerces the value of this field to an int.
     *
     * @return An int parsed from this parameter's value.
     */
    public int getInt() {
        return Integer.parseInt(getValue());
    }

    /**
     * Coerces the value of this field to a long.
     *
     * @return A long parsed from this parameter's value.
     */
    public long getLong() {
        return Long.parseLong(getValue());
    }

    /**
     * Coerces the value of this field to a float.
     *
     * @return A float parsed from this parameter's value.
     */
    public float getFloat() {
        return Float.parseFloat(getValue());
    }

    /**
     * Coerces the value of this field to a double.
     *
     * @return A double parsed from this parameter's value.
     */
    public double getDouble() {
        return Double.parseDouble(getValue());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SingleValueParameter)) {
            return false;
        } else {
            SingleValueParameter that = (SingleValueParameter)other;
            return this.getValue().equals(that.getValue()) && this.getName().equals(that.getName());
        }
    }

    @Override
    public int hashCode() {
        return (this.name == null ? 0 : this.name.hashCode()) ^
                (this.value == null ? 0 : this.value.hashCode());
    }
}
