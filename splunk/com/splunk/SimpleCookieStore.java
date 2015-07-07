
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
import java.util.HashMap;

/**
 * The {@code SimpleCookieStore} class stores cookies for authentication.
 * */
class SimpleCookieStore {

    Map<String, String> cookies = new HashMap<String, String>();
    /**
     * Adds cookies from a "Set-Cookie" header to the cookie store.
     *
     * @param setCookieHeader The result from a getRequestHeader("Set-Cookie") call
     */
    public void add(String setCookieHeader) {
        if (setCookieHeader != null) {
            setCookieHeader = setCookieHeader.split(";")[0];
            String cookieKey = setCookieHeader.split("=")[0];
            String cookieValue = setCookieHeader.split("=")[1];
            cookies.put(cookieKey, cookieValue);
        }
    }

    /**
     * Returns a string to be set as a "Cookie" header
     *
     * @return Cookie String in the format "Key=Value; Key=Value; ect"
     */
    public String getCookies() {
        String cookieString = "";
        for (String key : cookies.keySet()) {
            cookieString = cookieString.concat(key + "=" + cookies.get(key) + "; ");
        }
        return cookieString;
    }
}
