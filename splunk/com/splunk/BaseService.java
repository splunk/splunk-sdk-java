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
 * Contains functionality common to Splunk Enterprise and Splunk Storm.
 * 
 * This class is an implementation detail and is therefore SDK-private.
 */
abstract class BaseService extends HttpService {
    protected BaseService() {
        super();
    }
    
    protected BaseService(String host) {
        super(host);
    }

    protected BaseService(String host, int port) {
        super(host, port);
    }

    protected BaseService(String host, int port, String scheme) {
        super(host, port, scheme);
    }
}
