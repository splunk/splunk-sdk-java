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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * The {@code Record} class represents an extension of {@code HashMap} that 
 * contains a variety of value-converting access methods.
 */
public class Record extends HashMap<String, Object> {

    /**
     * Returns the {@code Boolean} value associated with the given key.
     *
     * @param key The key of the value being retrieved.
     * @return The value associated with the given key, or {@code null} if the
     *         key does not exist.
     */
    boolean getBoolean(String key) {
        return Value.toBoolean(getString(key));
    }

    /**
     * Returns the {@code Boolean} value associated with the given key, or the
     * {@code defaultValue} if the key does not exist.
     *
     * @param key The key of the value being retrieved.
     * @param defaultValue The value to return if the key does not exist.
     * @return The value associated with the given key, or {@code defaultValue}
     *         if the key does not exist.
     */
    boolean getBoolean(String key, boolean defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return Value.toBoolean(getString(key));
    }

    /**
     * Returns the {@code long} byte count value associated with the given key.
     *
     * @param key The key of the value being retrieved.
     * @return The value associated with the given key, or {@code null} if the
     *         key does not exist.
     */
    long getByteCount(String key) {
        return Value.toByteCount(getString(key));
    }

    /**
     * Returns the {@code long} byte count value associated with the given key,
     * or {@code defaultValue} if the key does not exist.
     *
     * @param key The key of the value being retrieved.
     * @param defaultValue The value to return if the key does not exist.
     * @return The value associated with the given key, or {@code defaultValue}
     *         if the key does not exist.
     */
    long getByteCount(String key, long defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return Value.toByteCount(getString(key));
    }

    /**
     * Returns the {@code Date} value associated with the given key.
     *
     * @param key The key of the value being retrieved.
     * @return The value associated with the given key, or {@code null} if the
     *         key does not exist.
     */
    Date getDate(String key) {
        return Value.toDate(getString(key));
    }

    /**
     * Returns the {@code Date} value associated with the given key, or
     * {@code defaultValue} if the key does not exist.
     *
     * @param key The key of the value being retrieved.
     * @param defaultValue The value to return if the key does not exist.
     * @return The value associated with the given key, or {@code defaultValue}
     *         if the key does not exist.
     */
    Date getDate(String key, Date defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return Value.toDate(getString(key));
    }

    /**
     * Returns the {@code float} value associated with the given key.
     *
     * @param key The key of the value being retrieved.
     * @return The value associated with the given key.
     */
    float getFloat(String key) {
        return Value.toFloat(getString(key));
    }

    /**
     * Returns the {@code int} value associated with the given key.
     *
     * @param key The key of the value being retrieved.
     * @return The value associated with the given key.
     */
    int getInteger(String key) {
        return Value.toInteger(getString(key));
    }

    /**
     * Returns the {@code int} value associated with the given key, or
     * {@code defaultValue} if the key does not exist.
     *
     * @param key The key of the value being retrieved.
     * @param defaultValue The value to return if the key does not exist.
     * @return The value associated with the given key, or {@code defaultValue}
     *         if the key does not exist.
     */
    int getInteger(String key, int defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return Value.toInteger(getString(key));
    }

    /**
     * Returns the {@code long} value associated with the given key.
     *
     * @param key The key of the value being retrieved.
     * @return The value associated with the given key.
     */
    long getLong(String key) {
        return Value.toLong(getString(key));
    }

    /**
     * Returns the {@code long} value associated with the given key, or
     * {@code defaultValue} if the key does not exist.
     *
     * @param key The key of the value being retrieved.
     * @param defaultValue The value to return if the key does not exist.
     * @return The value associated with the given key, or {@code defaultValue}
     *         if the key does not exist.
     */
    long getLong(String key, int defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return Value.toLong(getString(key));
    }

    /**
     * Returns the {@code String} value associated with the given key.
     *
     * @param key The key of the value being retrieved.
     * @return The value associated with the given key.
     */
    String getString(String key) {
        return get(key).toString();
    }

    /**
     * Returns the {@code String} value associated with the given key, or
     * {@code defaultValue} if the key does not exist.
     *
     * @param key The key of the value being retrieved.
     * @param defaultValue The value to return if the key does not exist.
     * @return The value associated with the given key, or {@code defaultValue}
     *         if the key does not exist.
     */
    String getString(String key, String defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return get(key).toString();
    }

    /**
     * Returns the {@code String[]} value associated with the given key.
     *
     * @param key The key of the value being retrieved.
     * @return The value associated with the given key.
     */
    String[] getStringArray(String key) {
        List<String> value = (List<String>)get(key);
        return value.toArray(new String[value.size()]);
    }

    /**
     * Returns the {@code String[]} value associated with the given key, or
     * {@code defaultValue} if the key does not exist.
     *
     * @param key The key of the value being retrieved.
     * @param defaultValue The value to return if the key does not exist.
     * @return The value associated with the given key, or {@code defaultValue}
     *         if the key does not exist.
     */
    String[] getStringArray(String key, String[] defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return getStringArray(key);
    }

    /**
     * Returns the value associated with the given key, cast to the given type
     * parameter.
     *
     * @param key The key of the value being retrieved.
     * @param <T> The type to cast the return value to.
     * @return The value associated with the given key, cast to the given type.
     */
    <T> T getValue(String key) {
        return (T)get(key);
    }

    /**
     * Returns the value associated with the given key, or {@code defaultValue}
     * if the key does not exist, cast to the given type parameter.
     *
     * @param key The key of the value being retrieved.
     * @param defaultValue The value to return if the key does not exist.
     * @param <T> The type to cast the return value to.
     * @return The value associated with the given key, or {@code defautlValue}
     *         if the key does not exist.
     */
    <T> T getValue(String key, T defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return (T)get(key);
    }
}
