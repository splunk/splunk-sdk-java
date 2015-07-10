
/*
 * Copyright 2015 Splunk, Inc.
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

import java.util.List;
import java.net.HttpCookie;
import java.util.Map;
import java.util.HashMap;
import java.lang.StringBuilder;

/**
 * The {@code SimpleCookieStore} class stores cookies for authentication.
 */
class SimpleCookieStore {

    private Map<String, String> cookieJar = new HashMap<String, String>();
    /**
     * Adds cookies from a "Set-Cookie" header to the cookie store.
     *
     * @param setCookieHeader The result from a getRequestHeader("Set-Cookie") call
     */
    public void add(String setCookieHeader) {
        if (setCookieHeader != null) {
            List<HttpCookie> cookies = HttpCookie.parse(setCookieHeader);
            for (HttpCookie cookie : cookies) {
                cookieJar.put(cookie.getName(), cookie.getValue());
            }
        }
    }

    /**
     * Returns a string to be set as a "Cookie" header
     *
     * @return Cookie String in the format "Key=Value; Key=Value; ect"
     */
    public String getCookies() {
        StringBuilder cookieString = new StringBuilder();
        //String cookieString = "";
        for (Map.Entry<String, String> cookie : cookieJar.entrySet()) {
            cookieString.append(cookie.getKey() + "=" + cookie.getValue() + "; ");
        }
        return cookieString.toString();
    }

    /**
     * Returns true if the cookie store is empty, false otherwise
     *
     * @return Boolean for whether or not the cookie store is empty
     */
    public Boolean isEmpty() {
        return cookieJar.isEmpty();
    }

    /**
     * Removes all cookies from SimpleCookieStore
     */
    public void removeAll() {
        cookieJar.clear();
    }

}
