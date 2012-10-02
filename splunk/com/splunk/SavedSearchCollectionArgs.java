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
 * Contains arguments used to list a collection of saved searches.
 */
public class SavedSearchCollectionArgs extends CollectionArgs {

    /**
     * Class constructor.
     */
    public SavedSearchCollectionArgs() { super(); }
    
    /**
     * @param earliest_time
     *      For scheduled searches display all the scheduled times starting from this time (not just the next run time) 
     */
    public void setEarliestTime(String earliest_time) {
        this.put("earliest_time", String.valueOf(earliest_time));
    }
    
    /**
     * @param latest_time
     *      For scheduled searches display all the scheduled times until this time (not just the next run time) 
     */
    public void setLatestTime(String latest_time) {
        this.put("latest_time", String.valueOf(latest_time));
    }
}
