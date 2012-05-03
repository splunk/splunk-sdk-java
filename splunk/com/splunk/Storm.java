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

import java.util.Map;

public class Storm extends Service {

    /**
     * Creates a new {@code Storm} service instance.
     */
    public Storm() {
        super("api.splunkstorm.com", 443, "https");
        this.simpleReceiverEndPoint = "/1/inputs/http";
    }

    /**
     * Establishes a connection to a Splunk service using a map of arguments.
     * This member creates a new {@code Service} instance and authenticates
     * the session using credentials passed in from the {@code args} map.
     *
     * @param args The {@code args} map.
     * @return A new {@code Service} instance.
     */
    public static Storm connect(Map<String, Object> args) {
        Storm service = new Storm();
        if (args.containsKey("StormToken")) {
            String username = (String)args.get("StormToken");
            String preToken = username + ":x";
            service.token = "Basic " + Base64.encode(preToken);
        }
        return service;
    }
}
