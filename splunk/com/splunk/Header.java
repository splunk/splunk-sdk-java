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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Header {
    // basic elements
    public String id;
    public String title;
    public String updated;
    public int itemsPerPage = -1;
    public String messages;
    public int startIndex = -1;
    public int totalResults = -1;

    // non-scalar
    public Map<String,String> author = new HashMap<String, String>();
    public List<HashMap<String,String>> link = new ArrayList<HashMap<String,String>> ();
    public Map<String,String> generator = new HashMap<String, String>();
}
