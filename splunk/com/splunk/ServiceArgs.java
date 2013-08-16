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

import java.net.URLStreamHandler;

/**
 * The {@code ServiceArgs} class contains a collection of arguments that are 
 * used to initialize a Splunk {@code Service} instance.
 */
public class ServiceArgs extends Args {
    /**
     * The application context of the service.
     * 
     * @deprecated
     *      Use {@link #setApp(String)} instead.
     */
    public String app = null;

    /**
     * The host name of the service.
     * 
     * @deprecated
     *      Use {@link #setHost(String)} instead.
     */
    public String host = null;

    /**
     * The owner context of the service.
     * 
     * @deprecated
     *      Use {@link #setOwner(String)} instead.
     */
    public String owner = null;

    /**
     * The port number of the service.
     * 
     * @deprecated
     *      Use {@link #setPort(int)} instead.
     */
    public Integer port = null;

    /**
     * The scheme to use for accessing the service.
     * 
     * @deprecated
     *      Use {@link #setScheme(String)} instead.
     */
    public String scheme = null;

    /**
     * A Splunk authentication token to use for the session.
     * 
     * @deprecated
     *      Use {@link #setToken(String)} instead.
     */
    public String token = null;
    
    /**
     * @param app
     *      The application context of the service.
     */
    public void setApp(String app) {
        this.app = app; // for backward compatibility
        this.put("app", app);
    }
    
    /**
     * @param host
     *      The host name of the service.
     */
    public void setHost(String host) {
        this.host = host; // for backward compatibility
        this.put("host", host);
    }
    
    /**
     * @param handler
     *      A URLStreamHandler to handle HTTPS requests for the service.
     */
    public void setHTTPSHandler(URLStreamHandler handler) {
    	this.put("httpsHandler", handler);
    }
    
    /**
     * @param owner
     *      The owner context of the service.
     */
    public void setOwner(String owner) {
        this.owner = owner; // for backward compatibility
        this.put("owner", owner);
    }
    
    /**
     * @param password
     *      The password to use when logging in.
     */
    public void setPassword(String password) {
        this.put("password", password);
    }
    
    /**
     * @param port
     *      The port number of the service.
     */
    public void setPort(int port) {
        this.port = port; // for backward compatibility
        this.put("port", port);
    }
    
    /**
     * @param scheme
     *      The scheme to use for accessing the service.
     */
    public void setScheme(String scheme) {
        this.scheme = scheme; // for backward compatibility
        this.put("scheme", scheme);
    }
    
    /**
     * @param token
     *      A Splunk authentication token to use for the session.
     */
    public void setToken(String token) {
        this.token = token; // for backward compatibility
        this.put("token", token);
    }
    
    /**
     * @param username
     *     The username to use when logging in.
     */
    public void setUsername(String username) {
        this.put("username", username);
    }
}
