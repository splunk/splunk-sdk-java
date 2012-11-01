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

/**
 * The {@code StormService} class represents a Splunk service instance at a
 * given address (host:port), accessed using the {@code http} or {@code https}
 * protocol scheme.
 * <p>
 * Using the {@code StormService} class, you can get a {@code Receiver} object
 * and log events to it. For more information, see 
 * <a href="http://dev.splunk.com/view/java-sdk/SP-CAAAECN" 
 * target="_blank">Overview of the Splunk Java SDK</a> on the Developer Portal. 
 */
public class StormService extends Service {

    /**
     * Creates a new {@code StormService} instance.
     */
    public StormService() {
        super("api.splunkstorm.com", 443, "https");
        this.simpleReceiverEndPoint = "/1/inputs/http";
    }

    /**
     * Establishes a connection to a Splunk Storm service using a map of 
     * arguments. This member creates a new {@code StormService} instance and 
     * authenticates the session using credentials passed in from the 
     * {@code args} map.
     *
     * @param args The {@code args} map.
     * @return A new {@code StormService} instance.
     */
    public static StormService connect(Map<String, Object> args) {
        StormService service = new StormService();
        if (args.containsKey("StormToken")) {
            String username = (String)args.get("StormToken");
            String preToken = username + ":x";
            service.token = "Basic " + Base64.encode(preToken);
        }
        return service;
    }
}
