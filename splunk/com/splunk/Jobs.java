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
import java.util.List;

public class Jobs extends Collection {

    public Jobs(Service service) {
        super(service, "/services/search/jobs/");
    }

    // Handle lists a little differently than the parent class, which uses title.
    // Here we use the last portion of the id, which is a URL.
    public List<String> list() throws Exception {
        List<String> retList = new ArrayList<String>();
        Element element = super.get();

        for (Entry entry: element.entry) {
            String [] parts = entry.id.split("/");
            if (parts.length > 0) retList.add(parts[parts.length - 1]);
        }
        return retList;
    }
}