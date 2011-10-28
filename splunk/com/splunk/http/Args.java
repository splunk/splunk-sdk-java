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

package com.splunk.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

class Args {
    Map<String, Object> values;
    StringBuilder builder = null;

    Args(Map<String, Object> values) {
        this.values = values;
    }

    public static Args create(Map<String, Object> values) {
        return new Args(values);
    }

    // Encode a single string value.
    public static String encode(String value) {
        if (value == null) return "";
        String result = null;
        try {
            result = URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e) { assert false; }
        return result;
    }

    public static String encode(Map<String, Object> args) {
        return Args.create(args).encode();
    }

    // Encode an argument with a list valued argument
    void encodeValues(String key, String[] values) {
        key = encode(key);
        for (String value : values) {
            if (builder.length() > 0) builder.append('&');
            builder.append(key);
            builder.append('=');
            builder.append(encode(value));
        }
    }

    String encode() {
        builder = new StringBuilder();
        for (Entry<String, Object> entry : values.entrySet()) {
            if (builder.length() > 0) builder.append('&');
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String[]) {
                encodeValues(key, (String[])value);
            }
            else {
                builder.append(encode(key));
                builder.append('=');
                builder.append(encode(value.toString()));
            }
        }
        return builder.toString();
    }
}
