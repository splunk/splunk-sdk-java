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
import java.util.regex.Pattern;

// FIXME: document
public class Event extends HashMap<String, String> {
    private Map<String, String[]> arrayValues = new HashMap<String, String[]>();
    
    public void putArray(String key, String[] values) {
        super.put(key, Util.join(",", values)); // for backward compatibility
        arrayValues.put(key, values);
    }
    
    public String[] getArray(String key) {
        String[] arrayValue = arrayValues.get(key);
        if (arrayValue != null) {
            return arrayValue;
        }
        
        String singleValue = super.get(key);
        if (singleValue == null) {
            return null;
        }
        return new String[] { singleValue };
    }
    
    public String[] getArray(String key, String delimiter) {
        String[] arrayValue = arrayValues.get(key);
        if (arrayValue != null) {
            return arrayValue;
        }
        
        String delimitedValues = super.get(key);
        if (delimitedValues == null) {
            return null;
        }
        return delimitedValues.split(Pattern.quote(delimiter));
    }
}
