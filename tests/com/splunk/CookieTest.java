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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Assume;
import org.junit.Test;

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
        ServiceArgs args = new ServiceArgs();
        args.setHost((String) command.opts.get("host"));
        args.setPort((Integer) command.opts.get("port"));
        args.setScheme((String) command.opts.get("scheme"));
        args.setUsername((String) command.opts.get("username"));
        args.setPassword((String) command.opts.get("password"));
        Service s = new Service(args);

        s.login();

        Assert.assertNotEquals(s.getCookies().length(), 0);
    }

    @Test
    public void testLoginWithCookie() {
        String validCookie = service.getCookies();

        Service s  = new Service(service.getHost(), service.getPort());

        s.addCookie(validCookie);

        // Ensure we can perform some action.
        // In particular we don't expect an unauthenticated error.
        s.getSettings().refresh();

        // Make sure we're still using the same token.
        // In particular we don't want to trigger auto-login functionality
        // that may get a new cookie.
        Assert.assertEquals(s.getCookies(), validCookie);
    }

    @Test(expected=HttpException.class)
    public void testLoginFailsWithBadCookie() {
        Service s  = new Service(service.getHost(), service.getPort());

        s.addCookie("bad=cookie;");

        s.getSettings().refresh();
    }

    @Test(expected=HttpException.class)
    public void testLoginFailsWithNoCookieOrLogin() {
        Service s  = new Service(service.getHost(), service.getPort());

        s.getSettings().refresh();
    }

    @Test
    public void testLoginWithMultipleCookies() {
        String validCookie = service.getCookies();

        Service s  = new Service(service.getHost(), service.getPort());

        s.addCookie(validCookie);
        s.addCookie("bad=cookie");

        s.getSettings().refresh();
    }



}
