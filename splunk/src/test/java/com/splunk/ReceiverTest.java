
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
import org.junit.Test;
import org.junit.Assume;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ReceiverTest extends SDKTestCase {

    @Test
    public void testReceiverWithoutCookie() {
        service.removeAllCookies();
        testReceiver(service);
    }

    @Test
    public void testReceiverWithCookie() {
        Assume.assumeTrue(service.versionIsAtLeast("6.2"));
        Assert.assertTrue(service.hasSplunkAuthCookies());
        testReceiver(service);
    }

    @Test
    public void testReceiverWithoutSplunkCookie() {
        String validToken = service.getToken();
        Assert.assertTrue(validToken.startsWith("Splunk "));
        String otherCookies = "load=balancer;";
        Map<String, Object> args = new HashMap<>();
        args.put("cookie", otherCookies);
        args.put("host",service.getHost());
        args.put("port", service.getPort());
        Service s = new Service(args);
        s.setToken(validToken);
        s.version = s.getInfo().getVersion();
        System.out.println(s.version);
        Assume.assumeTrue(s.versionIsAtLeast("6.2"));
        Assert.assertTrue(!s.cookieStore.isEmpty());
        testReceiver(s);
    }

    // Make a few simple requests and make sure the results look ok.
    public void testReceiver(Service passedService) {
        Receiver receiver = passedService.getReceiver();

        final String indexName = passedService.getIndexes().get("_internal").getDefaultDatabase();
        final Index index = passedService.getIndexes().get(indexName);
        final int originalEventCount = index.getTotalEventCount();
        final int versionCompare = passedService.versionCompare("6.0.0");
        final String osName = passedService.getInfo().getOsName();

        try {
            Socket socket1 = receiver.attach();
            OutputStream stream = socket1.getOutputStream();

            String s = createTimestamp() + " Boris the mad baboon1!\r\n";
            stream.write(s.getBytes("UTF-8"));
            // Splunk won't deterministically index these events until the socket is closed or greater than 1MB
            // has been written.
            stream.close();
            socket1.close();
        } catch (IOException e) {
            Assert.fail("Exception on attach");
        }

        try {
            Socket socket1 = receiver.attach(Args.create("sourcetype", "mysourcetype"));
            OutputStream stream = socket1.getOutputStream();

            String s = createTimestamp() + " Boris the mad baboon2!\r\n";
            stream.write(s.getBytes("UTF-8"));
            // Splunk won't deterministically index these events until the socket is closed or greater than 1MB
            // has been written.
            stream.close();
            socket1.close();
        } catch (IOException e) {
            Assert.fail("Exception on attach");
        }

        receiver.submit("Boris the mad baboon3!\r\n");
        receiver.submit(Args.create("sourcetype", "mysourcetype"), "Boris the mad baboon4!\r\n");
        receiver.log("Boris the mad baboon5!\r\n");
        receiver.log("main", "Boris the mad baboon6!\r\n");
        receiver.log(Args.create("sourcetype", "mysourcetype"), "Boris the mad baboon7!\r\n");
        receiver.log(indexName, Args.create("sourcetype", "mysourcetype"), "Boris the mad baboon8!\r\n");

        assertEventuallyTrue(new EventuallyTrueBehavior() {
            {
                tries = 200;
            }

            @Override
            public boolean predicate() {
                index.refresh();
                int eventCount = index.getTotalEventCount();
                // WORKAROUND (SPL-75109): Splunk 6.0 on Windows doesn't record events submitted to the streaming
                // HTTP input without a sourcetype.
                //
                if (versionCompare == 0 && osName.equals("Windows")) {
                    return eventCount == originalEventCount + 7;
                } else {
                    return eventCount == originalEventCount + 8;
                }
            }
        });
    }
}
