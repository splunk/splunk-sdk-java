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
 * The {@code ServiceArgs} class contains a collection of arguments that are 
 * used to initialize a Splunk {@code Service} instance.
 */
public class ServiceArgs {
    /** The application context of the service. */
    public String app = null;

    /** The host name of the service. */
    public String host = null;

    /** The owner context of the service. */
    public String owner = null;

    /** The port number of the service. */
    public Integer port = null;

    /** The scheme to use for accessing the service. */
    public String scheme = null;

    /** A Splunk authentication token to use for the session. */
    public String token = null;
}
