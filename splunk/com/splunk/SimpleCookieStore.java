
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
import java.net.CookieStore;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * The {@code SimpleCookieStore} class stores cookies for authentication.
 * */
class SimpleCookieStore {

    private CookieStore cookieJar = new CookieManager(null, CookiePolicy.ACCEPT_ALL).getCookieStore();
    /**
     * Adds cookies from a "Set-Cookie" header to the cookie store.
     *
     * @param setCookieHeader The result from a getRequestHeader("Set-Cookie") call
     */
    public void add(String setCookieHeader) {
        if (setCookieHeader != null) {
            List<HttpCookie> cookies = HttpCookie.parse(setCookieHeader);
            for (HttpCookie cookie : cookies) {
                cookieJar.add(null, cookie);
            }
        }
    }

    /**
     * Returns a string to be set as a "Cookie" header
     *
     * @return Cookie String in the format "Key=Value; Key=Value; ect"
     */
    public String getCookies() {
        String cookieString = "";
        for (HttpCookie cookie : cookieJar.getCookies()) {
            cookieString = cookieString.concat(cookie.toString() + "; ");
        }
        return cookieString;
    }

    /**
     * Returns true if the cookie store is empty, false otherwise
     *
     * @return Boolean for whether or not the cookie store is empty
     */
    public Boolean isEmpty() {
        return cookieJar.getCookies().isEmpty();
    }

}
