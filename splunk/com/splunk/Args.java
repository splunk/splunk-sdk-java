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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The {@code Args} class is a helper class for working with Splunk REST API
 * arguments.
 *
 * This extension is used mainly for encoding arguments for UTF8 transmission
 * to a Splunk instance in a key=value pairing for a string, or
 * key=value1&key=value2 (and so on) for an array of strings.
 */
public class Args extends LinkedHashMap<String, Object> {

    /**
     * Class constructor.
     */
    public Args() { super(); }

    /**
     * Class constructor. Initializes a single key-value pair.
     *
     * @param key The key name.
     * @param value The value, as a {@code String:String} or
     * {@code String:String[]}.
     */
    public Args(String key, Object value) {
        super();
        put(key, value);
    }

    /**
     * Class constructor. Initializes a pre-existing hash map.
     *
     * @param values A set of key-value pairs.
     */
    public Args(Map<String, Object> values) {
        super(values);
    }

    /**
     * Adds an argument to an {@code Args} object.
     *
     * @param key The key name.
     * @param value The value, as a {@code String:String} or
     * {@code String:String[]}.
     * @return This {@code Args} set.
     */
    public Args add(String key, Object value) {
        put(key, value);
        return this;
    }

    /**
     * Creates a new empty instance of {@code Args}.
     *
     * @return The {@code Args} instance.
     */
    public static Args create() {
        return new Args();
    }

    /**
     * Creates a new {@code Args} instance and initializes it with a single
     * key-value pair.
     *
     * @param key The key name.
     * @param value The value, as a {@code String:String} or
     * {@code String:String[]}.
     * @return The {@code Args} instance.
     */
    public static Args create(String key, Object value) {
        return new Args(key, value);
    }

    /**
     * Creates a new {@code Args} instance and initializes it with a
     * pre-existing hash map.
     *
     * @param values The pre-existing hash map.
     * @return The {@code Args} instance.
     */
    public static Args create(Map<String, Object> values) {
        return values == null ? new Args() : new Args(values);
    }

    /**
     * Encodes a single string with UTF8 encoding.
     *
     * @param value The string.
     * @return The encoded string.
     */
    public static String encode(String value) {
        if (value == null) return "";
        String result = null;
        try {
            result = URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e) { assert false; }
        return result;
    }

    /**
     * Encodes a hash map of {@code String:String} or {@code String:String[]}
     * into a single UTF8-encoded string.
     *
     * @param args The hash map.
     * @return The string.
     */
    public static String encode(Map<String, Object> args) {
        return Args.create(args).encode();
    }

    // Encodes an argument with a list-valued argument.
    private void 
    encodeValues(StringBuilder builder, String key, String[] values) {
        key = encode(key);
        for (String value : values) {
            if (builder.length() > 0) builder.append('&');
            builder.append(key);
            builder.append('=');
            builder.append(encode(value));
        }
    }

    /**
     * Encodes an {@code Args} instance into a UTF8-encoded string.
     *
     * @return The UTF8-encoded string.
     */
    public String encode() {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, Object> entry : entrySet()) {
            if (builder.length() > 0) builder.append('&');
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String[]) {
                encodeValues(builder, key, (String[])value);
            }
            else {
                builder.append(encode(key));
                builder.append('=');
                builder.append(encode(value.toString()));
            }
        }
        return builder.toString();
    }

    /**
     * Returns the hash-map value of a specific key, or the default value if
     * the key is not found.
     *
     * @param args The hash map.
     * @param key The key to look for.
     * @param defaultValue The default value, if the key is not found.
     * @param <T> The class type.
     * @return The value.
     */
    public static <T> T 
    get(Map<String, Object> args, String key, T defaultValue) {
        if (!args.containsKey(key)) return defaultValue;
        return (T)args.get(key);
    }
}

