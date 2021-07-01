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
 * The {@code Event} class wraps an individual event or result that was returned
 * by the {@link ResultsReader#getNextEvent} method.
 * <p>
 * An event maps each field name to a list of zero of more values.
 * These values can be accessed as either an array (using the {@link #getArray} 
 * method) or as a delimited string (using the {@link #get} method). We 
 * recommend accessing values as an array when possible.
 * <p>
 * The delimiter for field values depends on the underlying result format.
 * If the underlying format does not specify a delimiter, such as with the
 * {@link ResultsReaderXml} class, the delimiter is a comma (,).
 */
public class Event extends HashMap<String, String> {
    private Map<String, String[]> arrayValues = new HashMap<String, String[]>();
    private String segmentedRaw;
    
    // Prevent non-SDK instantiation.
    Event() {
        // nothing
    }
    
    /**
     * Sets the single value or delimited set of values for the specified
     * field name.
     * 
     * When setting a multi-valued field, use the 
     * {@link #putArray(String, String[])} method instead.
     * 
     * @param key The field name.
     * @param valueOrDelimitedValues The single values or delimited set of 
     * values.
     */
    String putSingleOrDelimited(String key, String valueOrDelimitedValues) {
        return super.put(key, valueOrDelimitedValues);
    }
    
    /**
     * Sets the values for the specified field name, with the assumption that 
     * the value delimiter is a comma (,).
     *
     * @param key The field name.
     * @param values The delimited set of values.
     */
    void putArray(String key, String[] values) {
        arrayValues.put(key, values);
        
        // For backward compatibility with the Map interface
        super.put(key, Util.join(",", values));
    }

    /**
     * Sets the value for the XML element for the {@code _raw} field. This value
     * is only used by the {@link ResultsReaderXml} class.
     * @param value The text of the XML element.
     */
    void putSegmentedRaw(String value) {
        segmentedRaw = value;
    }

    /**
     * Returns the single value or delimited set of values for the specified
     * field name, or {@code null} if the specified field is not present.
     * 
     * When getting a multi-valued field, use the {@link #getArray(String)} or
     * {@link #getArray(String, String)} method instead.
     *
     * @param key The field name.
     * @return The single value or delimited set of values.
     */
    public String get(String key) {
        return super.get(key);
    }
    
    /**
     * Gets the values for the specified field name.
     * <br><br>
     * <b>Caution:</b> This variant of {@link #getArray(String, String)} is
     * unsafe for {@link ResultsReader} implementations that require a
     * delimiter. Therefore, this method should only be used for results that
     * are returned by {@link ResultsReaderXml}. For other readers, use the 
     * {@link #getArray(String, String)} method instead.
     * <br><br>
     * If the underlying {@link ResultsReader} object has no delimiter, the 
     * original array of values is returned. If the object <i>does</i> have a 
     * delimiter, the single/delimited value is assumed to be a single value and
     * is returned as a single-valued array.
     *
     * @param key The field name.
     * @return The original array of values if there is no delimiter, or the 
     * single-valued array.
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
     * Gets the values for the specified field name.
     * 
     * The delimiter must be determined empirically based on the search
     * string and the data format of the index. The delimiter can differ
     * between fields in the same {@link Event} object.
     * 
     * The delimiter is ignored for {@link ResultsReader} implementations
     * that do not require a delimiter, such as {@link ResultsReaderXml}.
     * 
     * If the underlying {@link ResultsReader} object has no delimiter, the 
     * original array of values is returned (and the specified delimiter is 
     * ignored). If the object <i>does</i> have a delimiter, the 
     * single/delimited value is split based on the specified delimiter and is
     * returned as an array.
     *
     * @param key The field name.
     * @param delimiter The delimiter.
     * @return The original array of values if there is no delimiter, or the 
     * array of values split by delimiter.
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

    /**
     * Gets the XML markup for the {@code "_raw"} field value. This value
     * is only used by the {@link ResultsReaderXml} class.
     * <p>
     * The return value is different than that of {@code get("_raw")}
     * in that this segmented raw value is an XML fragment that includes all 
     * markup such as XML tags and escaped characters.
     * <p>
     * For example, {@code get("_raw")} returns this:
     * <p>
     * {@code "http://localhost:8000/en-US/app/search/flashtimeline?q=search%20search%20index%3D_internal%20%7C%20head%2010&earliest=rt-1h&latest=rt"}
     * <p>
     * The {@code getSegmentedRaw} method returns this:
     * <p>
     * {@code <v xml:space="preserve" trunc="0">"http://localhost:8000/en-US/app/<sg h=\"1\">search</sg>/flashtimeline?q=<sg h=\"1\">search</sg>%20<sg h=\"1\">search</sg>%20index%3D_internal%20%7C%20head%2010&amp;earliest=rt-1h&amp;latest=rt"</v>}
     * @return the segmented raw xml including tags and escaped characters.
     */
    public String getSegmentedRaw() {
       if (segmentedRaw == null) {
           // ResultsReaderXml will always set this to not null. Using this
           // method for other result reader is not supported.
           throw new UnsupportedOperationException(
               "The value is not available. Use ResultsReaderXml instead.");
       }
       return segmentedRaw;
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
