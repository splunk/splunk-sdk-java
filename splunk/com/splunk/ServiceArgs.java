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

/**
 * A collection of arguments used to initialize a Splunk service instance.
 */
public class ServiceArgs {
    /** The application context for the service */
    public String app = null;

    /** The host name of the service */
    public String host = null;

    /** The owner contet for the service */
    public String owner = null;

    /** The port number for the service */
    public Integer port = null;

    /** The scheme to use for accessing the service */
    public String scheme = null;

    /** A Splunk auth token to use for the session */
    public String token = null;
}
