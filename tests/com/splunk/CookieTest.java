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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Assume;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

public class CookieTest extends SDKTestCase {

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Cookies were not around before version 6.2
        Assume.assumeTrue(service.versionIsAtLeast("6.2"));
    }

    @Test
    public void testGotCookieOnLogin() {
        Map<String, Object> args = getStandardArgs();
        args.put("scheme", (String) command.opts.get("scheme"));
        args.put("username", (String) command.opts.get("username"));
        args.put("password", (String) command.opts.get("password"));
        Service s = new Service(args);

        s.login();

        Assert.assertNotEquals(s.stringifyCookies().length(), 0);
    }

    @Test
    public void testLoginWithCookie() {
        String validCookie = service.stringifyCookies();

        Map<String, Object> args = getStandardArgs();
        args.put("cookie", validCookie);

        Service s = new Service(args);

        // Ensure we can perform some action.
        // In particular we don't expect an unauthenticated error.
        s.getSettings().refresh();

        // Make sure we're still using the same token.
        // In particular we don't want to trigger auto-login functionality
        // that may get a new cookie.
        Assert.assertEquals(s.stringifyCookies(), validCookie);
    }

    @Test(expected=HttpException.class)
    public void testLoginFailsWithBadCookie() {
        Map<String, Object> args = getStandardArgs();
        args.put("cookie", "bad=cookie");

        Service s  = new Service(args);

        s.getSettings().refresh();
    }

    @Test(expected=HttpException.class)
    public void testAuthenticationFailsWithNoCookieOrLogin() {
        Service s  = new Service(service.getHost(), service.getPort());

        s.getSettings().refresh();
    }

    @Test
    public void testLoginWithMultipleCookies() {
        String validCookie = service.stringifyCookies();

        Map<String, Object> args = getStandardArgs();
        args.put("cookie", validCookie);

        Service s  = new Service(args);

        s.addCookie("bad=cookie");

        s.getSettings().refresh();
    }

    @Test
    public void testLoginWithMultipleCookiesReversed() {
        String validCookie = service.stringifyCookies();

        Map<String, Object> args = getStandardArgs();
        args.put("cookie", "bad=cookie");
        Service s  = new Service(args);

        s.addCookie(validCookie);

        s.getSettings().refresh();
    }

    @Test
    public void testHttpServiceWithValidCookie() {
        String validCookie = service.stringifyCookies();

        HttpService httpService;

        httpService = new HttpService(
                (String)command.opts.get("host"),
                (Integer)command.opts.get("port"),
                (String)command.opts.get("scheme")
        );

        httpService.addCookie(validCookie);

        httpService.get("/services/authentication/users");

        Assert.assertEquals(validCookie, httpService.stringifyCookies());
    }

    @Test(expected=HttpException.class)
    public void testHttpServiceWithInvalidCookie() {
        HttpService httpService;

        httpService = new HttpService(
                (String)command.opts.get("host"),
                (Integer)command.opts.get("port"),
                (String)command.opts.get("scheme")
        );

        httpService.addCookie("bad=cookie");

        httpService.get("/services/authentication/users");
    }

    @Test(expected=HttpException.class)
    public void testHttpServiceWithNoCookie() {
        HttpService httpService;

        httpService = new HttpService(
                (String)command.opts.get("host"),
                (Integer)command.opts.get("port"),
                (String)command.opts.get("scheme")
        );

        httpService.get("/services/authentication/users");
    }

    private Map<String, Object> getStandardArgs() {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("host", (String)command.opts.get("host"));
        args.put("port", (Integer) command.opts.get("port"));

        return args;
    }

}
