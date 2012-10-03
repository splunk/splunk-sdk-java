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

/**
 * Contains arguments used to list a collection of entities.
 */
public class CollectionArgs extends Args {
    
    /**
     * Class constructor.
     */
    public CollectionArgs() { super(); }
    
    /**
     * @param count
     *      Indicates the maximum number of entries to return. To return all entries, specify 0.
     */
    public void setCount(int count) {
        this.put("count", String.valueOf(count));
    }
    
    /**
     * @param offset
     *      Index for first item to return.
     */
    public void setOffset(int offset) {
        this.put("offset", String.valueOf(offset));
    }
    
    /**
     * @param search
     *      Search expression to filter the response. The response matches field values against the search expression. For example:
     *      
     *      "foo" matches any object that has "foo" as a substring in a field. "field_name=field_value" restricts the match to a single field.
     */
    public void setSearch(String search) {
        this.put("search", String.valueOf(search));
    }
    
    /**
     * @param sort_dir
     *      Indicates whether to sort returned entries in ascending or descending order.
     */
    public void setSortDirection(SortDirection sort_dir) {
        this.put("sort_dir", String.valueOf(sort_dir));
    }
    
    /**
     * @param sort_key
     *      Field to use for sorting.
     */
    public void setSortKey(String sort_key) {
        this.put("sort_key", String.valueOf(sort_key));
    }
    
    /**
     * @param sort_mode
     *      Indicates the collating sequence for sorting the returned entries.
     */
    public void setSortMode(SortMode sort_mode) {
        this.put("sort_mode", String.valueOf(sort_mode));
    }
    
    /**
     * Indicates whether to sort returned entries in ascending or descending order.
     */
    public static enum SortDirection {
        ASC("asc"),
        DESC("desc");
        
        private String value;
        
        private SortDirection(String value) {
            this.value = value;
        }
        
        /**
         * @return The REST API value for this enumerated constant.
         */
        public String toString() {
            return this.value;
        }
    }
    
    /**
     * Indicates the collating sequence for sorting the returned entries.
     */
    public static enum SortMode {
        /** If all values of the field are numbers, collate numerically. Otherwise, collate alphabetically. */
        AUTO("auto"),
        /** Collate alphabetically. */
        ALPHA("alpha"),
        /** Collate alphabetically, case-sensitive. */
        ALPHA_CASE("alpha_case"),
        /** Collate numerically. */
        NUM("num");
        
        private String value;
        
        private SortMode(String value) {
            this.value = value;
        }
        
        /**
         * @return The REST API value for this enumerated constant.
         */
        public String toString() {
            return this.value;
        }
    }
}
