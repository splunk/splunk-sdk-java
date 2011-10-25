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

// UNDONE: Implement get/post/delete

import com.splunk.http.ResponseMessage;

import java.util.Map;

public class Endpoint {
    String path;
    Service service;

    public Endpoint(Service service, String path) {
        this.path = path;
        this.service = service;
    }

    public ResponseMessage get() {
        return service.get(path);
    }

    public ResponseMessage get(Map<String, String> args) {
        return service.get(this.path, args);
    }

    public String getPath() {
        return this.path;
    }

    public ResponseMessage post(Map<String, String> args) {
        return service.post(this.path, args);
    }

    public Service getService() {
        return this.service;
    }
}

