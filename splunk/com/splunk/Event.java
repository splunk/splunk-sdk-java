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

/**
 * Wraps an individual event or result returned by
 * {@link ResultsReader#getNextEvent()}.
 * 
 * An event maps each field name to a list of zero of more values.
 * These values can be accessed as either an array (via {@link #getArray} or
 * as a delimited string (via {@link #get}). It is recommended to access as an
 * array when possible.
 * 
 * The delimiter for field values depends on the underlying result format.
 * If the underlying format does not specify a delimiter, such as with
 * {@link ResultsReaderXml}, the delimiter is comma (,).
 */
public class Event extends HashMap<String, String> {
    private Map<String, String[]> arrayValues = new HashMap<String, String[]>();
    
    // Prevent non-SDK instantiation.
    Event() {
        // nothing
    }
    
    /**
     * Sets the single value or delimited set of values for the specified
     * field name.
     * 
     * When setting a multi-valued field, {@link #putArray(String, String[])}
     * is recommended instead.
     */
    String putDelimited(String key, String valueOrDelimitedValues) {
        return super.put(key, valueOrDelimitedValues);
    }
    
    /**
     * Sets the value(s) for the specified field name.
     * 
     * The value delimiter is assumed to be comma (,).
     */
    void putArray(String key, String[] values) {
        arrayValues.put(key, values);
        
        // For backward compatibility with the Map interface
        super.put(key, Util.join(",", values));
    }
    
    /**
     * Returns the single value or delimited set of values for the specified
     * field name, or {@code null} if the specified field is not present.
     * 
     * When getting a multi-valued field, {@link #getArray(String)} or
     * {@link #getArray(String, String)} is recommended instead.
     */
    public String get(String key) {
        return super.get(key);
    }
    
    /**
     * Gets the value(s) for the specified field name.
     * 
     * <b>Caution:</b> This variant of {@link #getArray(String, String)} is
     * unsafe for {@link ResultsReader} implementations that require a
     * delimiter. Therefore this method should only be used for results
     * returned by {@link ResultsReaderXml}. For other readers, use
     * {@link #getArray(String, String)} instead.
     */
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
    
    /**
     * Gets the value(s) for the specified field name.
     * 
     * The delimiter must be determined empirically based on the search
     * string and the data format of the index. The delimiter can differ
     * between fields in the same {@link Event}.
     * 
     * The delimiter is ignored for {@link ResultsReader} implementations
     * that do not require a delimiter, such as {@link ResultsReaderXml}.
     */
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
    
    // === Read Only ===
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object clone() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String put(String key, String value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String remove(Object key) {
        throw new UnsupportedOperationException();
    }
}
