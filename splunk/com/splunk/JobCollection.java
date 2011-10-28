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

import com.splunk.atom.Xml;
import com.splunk.http.ResponseMessage;

public class JobCollection extends EntityCollection<Job> {
    public JobCollection(Service service, String path) {
        super(service, path, Job.class);
    }
    
    public Job create(String query) {
        return create(query, null);
    }

    // UNDONE: Check for exec_mode=oneshot and throw, that request
    // returns the results directly in the response body and can not
    // be invoked via create - we will need to add a special oneshot(...)
    // method that returns search results instead of a Job entity.
    public Job create(String query, Args extra) {
        Args args = new Args();
        args.put("search", query);
        if (extra != null) args.putAll(extra);
        ResponseMessage response = service.post(path, args);
        assert(response.getStatus() == 201);
        invalidate();
        String sid = Xml.parse(response.getContent())
            .getElementsByTagName("sid").item(0).getTextContent();
        return get(sid);
    }

    public ResponseMessage list() {
        return service.get(path + "?count=0");
    }
}
