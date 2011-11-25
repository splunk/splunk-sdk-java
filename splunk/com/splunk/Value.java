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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Value conversion helpers
class Value {
    static boolean getBoolean(Map<String, Object> map, String key) {
        return toBoolean(map.get(key).toString());
    }

    static boolean 
    getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        if (!map.containsKey(key)) return defaultValue;
        return toBoolean(map.get(key).toString());
    }

    static long getByteCount(Map<String, Object>map, String key) {
        return toByteCount(map.get(key).toString());
    }

    static long
    getByteCount(Map<String, Object>map, String key, long defaultValue) {
        if (!map.containsKey(key)) return defaultValue;
        return toByteCount(map.get(key).toString());
    }

    static Date getDate(Map<String, Object> map, String key) {
        return toDate(map.get(key).toString());
    }

    static Date 
    getDate(Map<String, Object> map, String key, Date defaultValue) {
        if (!map.containsKey(key)) return defaultValue;
        return toDate(map.get(key).toString());
    }

    static Date getDateFromEpoch(Map<String, Object> map, String key) {
        return toDateFromEpoch(map.get(key).toString());
    }

    static Date 
    getDateFromEpoch(Map<String, Object> map, String key, Date defaultValue) 
    {
        if (!map.containsKey(key)) return defaultValue;
        return toDateFromEpoch(map.get(key).toString());
    }

    static float getFloat(Map<String, Object> map, String key) {
        return toFloat(map.get(key).toString());
    }

    static int getInteger(Map<String, Object> map, String key) {
        return toInteger(map.get(key).toString());
    }

    static int 
    getInteger(Map<String, Object> map, String key, int defaultValue) {
        if (!map.containsKey(key)) return defaultValue;
        return toInteger(map.get(key).toString());
    }

    static long getLong(Map<String, Object> map, String key) {
        return toLong(map.get(key).toString());
    }

    static long getLong(Map<String, Object> map, String key, int defaultValue) {
        if (!map.containsKey(key)) return defaultValue;
        return toLong(map.get(key).toString());
    }

    static String getString(Map<String, Object> map, String key) {
        return map.get(key).toString();
    }

    static String 
    getString(Map<String, Object> map, String key, String defaultValue) {
        if (!map.containsKey(key)) return defaultValue;
        return map.get(key).toString();
    }

    static String[] getStringArray(Map<String, Object> map, String key) {
        List<String> value = (List<String>)map.get(key);
        return value.toArray(new String[value.size()]);
    }

    static String[]
    getStringArray(Map<String, Object> map, String key, String[] defaultValue) {
        if (!map.containsKey(key)) return defaultValue;
        return getStringArray(map, key);
    }

    static <T> T getValue(Map<String, Object> map, String key) {
        return (T)map.get(key);
    }

    static <T> T getValue(Map<String, Object> map, String key, T defaultValue) {
        if (!map.containsKey(key)) return defaultValue;
        return (T)map.get(key);
    }

    // Convert the given string to a boolean value.
    static boolean toBoolean(String value) {
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

    static long toByteCount(String value) {
        long multiplier = 1;
        if (value.endsWith("B")) {
            if (value.endsWith("KB"))
                multiplier = 1024;
            else if (value.endsWith("MB"))
                multiplier = 1024*1024;
            else if (value.endsWith("GB"))
                multiplier = 1024*1024*1024;
            else {
                String message = String.format("Value error: '%s'", value);
                throw new RuntimeException(message); // UNDONE
            }
            value = value.substring(0, value.length()-2);
        }
        return Long.parseLong(value) * multiplier;
    }

    private static SimpleDateFormat dateFormat = null;
    private static Pattern datePattern = null;
    static Date toDate(String value) {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            dateFormat.setLenient(true);
        }
        if (datePattern == null) {
            String pattern = "(.*)\\.\\d+([\\-+]\\d+):(\\d+)";
            datePattern = Pattern.compile(pattern);
        }
        Date result;
        try {
            // Must first remove the colon (':') from the timezone
            // field, or SimpleDataFormat will not parse correctly.
            // Eg: 2010-01-01T12:00:00+01:00 => 2010-01-01T12:00:00+0100
            Matcher matcher = datePattern.matcher(value);
            value = matcher.replaceAll("$1$2$3");
            result = dateFormat.parse(value);
        }
        catch (ParseException e) {
            throw new RuntimeException(e.getMessage()); // UNDONE
        }
        return result;
    }

    static Date toDateFromEpoch(String value) {
        return new Date(Long.parseLong(value)*1000);
    }

    static float toFloat(String value) {
        return Float.parseFloat(value);
    }

    static int toInteger(String value) {
        return Integer.parseInt(value);
    }

    static long toLong(String value) {
        return Long.parseLong(value);
    }
}

