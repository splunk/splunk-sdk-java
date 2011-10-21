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

import com.splunk.http.RequestMessage;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Index extends Entity {

    private String localname = null;
    HttpURLConnection cn = null;

    public Index(Service service, String relpath) {
        super(service, "/services/data/indexes/" + relpath);
        localname = relpath;
    }

    public void attach() throws IOException {
        RequestMessage request = new RequestMessage("POST",
                        "/services/receivers/stream?index=" + localname);
        request.getHeader().put("X-Splunk-Input-Mode", "Streaming");
        cn = service.streamingConnection(request, service.token);
    }

    public Index clean () throws Exception {
        List<String> list = new ArrayList<String>();
        list.add("maxTotalDataSizeMB");
        list.add("frozenTimePeriodInSecs");
        Map<String,String> saved = super.read(list);

        Map<String,String> reset = new HashMap<String, String>();
        reset.put("maxTotalDataSizeMB", "1");
        reset.put("frozenTimePeriodInSecs", "1");
        super.update(reset);
        super.post("/roll-hot-buckets");

        List<String> count = new ArrayList<String>();
        count.add("totalEventCount");
        Map<String,String> result = new HashMap<String, String>();
        while (true) {
            Thread.sleep(1000); // 1000ms (1 second sleep)
            result = super.read(count);
            String value = result.get("totalEventCount");
            if (value.equals("0")) {
                break;
            }
        }
        super.update(saved);
        return this;
    }

    public void submit(String data) throws Exception {

        if (cn == null) {
            throw new Exception("Connection not established");
        }

        service.stream(cn, data);
    }

    public Index upload(String filename,
                          Map<String, String> args) throws Exception {
        args.put("name", filename);
        args.put("index", localname); // established at class instantiation
        // not a base-relative path
        // need to reach into the endpoints class to post
        Convert converter = new Convert();
        service.post("/services/data/inputs/oneshot", args).getContent();
        return this;
    }
}
