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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// An extension of map with a collection of value converting accessors.
public class Record extends HashMap<String, Object> {
    boolean getBoolean(String key) {
        return Value.toBoolean(getString(key));
    }

    boolean getBoolean(String key, boolean defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return Value.toBoolean(getString(key));
    }

    long getByteCount(String key) {
        return Value.toByteCount(getString(key));
    }

    long getByteCount(String key, long defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return Value.toByteCount(getString(key));
    }

    Date getDate(String key) {
        return Value.toDate(getString(key));
    }

    Date getDate(String key, Date defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return Value.toDate(getString(key));
    }

    Date getDateFromEpoch(String key) {
        return Value.toDateFromEpoch(getString(key));
    }

    Date getDateFromEpoch(String key, Date defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return Value.toDateFromEpoch(getString(key));
    }

    float getFloat(String key) {
        return Value.toFloat(getString(key));
    }

    int getInteger(String key) {
        return Value.toInteger(getString(key));
    }

    int getInteger(String key, int defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return Value.toInteger(getString(key));
    }

    long getLong(String key) {
        return Value.toLong(getString(key));
    }

    long getLong(String key, int defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return Value.toLong(getString(key));
    }

    String getString(String key) {
        return get(key).toString();
    }

    String getString(String key, String defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return get(key).toString();
    }

    String[] getStringArray(String key) {
        List<String> value = (List<String>)get(key);
        return value.toArray(new String[value.size()]);
    }

    String[] getStringArray(String key, String[] defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return getStringArray(key);
    }

    <T> T getValue(String key) {
        return (T)get(key);
    }

    <T> T getValue(String key, T defaultValue) {
        if (!containsKey(key)) return defaultValue;
        return (T)get(key);
    }
}
