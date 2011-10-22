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

import java.util.Date;
import java.util.Map;

// Value conversion helpers
public class Value {
    public static boolean getBoolean(Map<String, Object> map, String key) {
        return toBoolean(map.get(key).toString());
    }

    public static boolean 
    getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        if (!map.containsKey(key)) return defaultValue;
        return toBoolean(map.get(key).toString());
    }

    public static Date getDate(Map<String, Object> map, String key) {
        return toDate(map.get(key).toString());
    }

    public static float getFloat(Map<String, Object> map, String key) {
        return toFloat(map.get(key).toString());
    }

    public static int getInteger(Map<String, Object> map, String key) {
        return toInteger(map.get(key).toString());
    }

    public static int 
    getInteger(Map<String, Object> map, String key, int defaultValue) {
        if (!map.containsKey(key)) return defaultValue;
        return toInteger(map.get(key).toString());
    }

    public static String 
    getString(Map<String, Object> map, String key, String defaultValue) {
        if (!map.containsKey(key)) return defaultValue;
        return map.get(key).toString();
    }

    // Convert the given string to a boolean value.
    public static boolean toBoolean(String value) {
        if (value == null) return false;
        if (value.equals("0"))
            return false;
        if (value.equals("1"))
            return true;
        if (value.toLowerCase().equals("false"))
            return false;
        if (value.toLowerCase().equals("true"))
            return true;
        String message = String.format("Value error: '%s'", value);
        throw new RuntimeException(message); // UNDONE
    }

    public static Date toDate(String value) {
        return null; // UNDONE
    }

    public static float toFloat(String value) {
        return Float.parseFloat(value);
    }

    public static int toInteger(String value) {
        return Integer.parseInt(value);
    }
}

