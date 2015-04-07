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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;

public class HttpServiceTest extends SDKTestCase {
    private HttpService httpService;
    
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        httpService = new HttpService(
                (String)command.opts.get("host"),
                (Integer)command.opts.get("port"),
                (String)command.opts.get("scheme")
        );
    }

    @Test
    public void testGet() {
        ResponseMessage response = httpService.get("/");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(firstLineIsXmlDtd(response.getContent()));
    }

    @Test
    public void testSend() {
        RequestMessage request = new RequestMessage("GET");
        ResponseMessage response = service.send("/services", request);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(firstLineIsXmlDtd(response.getContent()));
    }

    @Test
    public void testRequestMessage() {
        RequestMessage request = new RequestMessage("GET");
        Assert.assertTrue(request.checkMethod(request.getMethod()));
        request.setMethod("POST");
        Assert.assertTrue(request.checkMethod(request.getMethod()));
        Assert.assertEquals(request.getMethod(), "POST");
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write("HELLO".getBytes("UTF-8"));
        } catch (Exception e) {
            Assert.fail("Exception!");
        }
        
        Assert.assertNull(request.getContent());
        request.setContent(stream);
        Assert.assertNotNull(request.getContent());
    }

    @Test
    public void testResponseMessage() {
        ResponseMessage response = new ResponseMessage(200);
        Assert.assertEquals(response.getStatus(), 200);
        Assert.assertTrue(response.getHeader() != null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSSLSocketFactorySetNull(){
        HttpService.setSSLSocketFactory(null);
    }



    @Test
    public void testSSLSocketFactory() {
        try {
            SSLSocketFactory factory = Service.getSSLSocketFactory();
            SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 8089);
            String[] protocols = socket.getEnabledProtocols();
            Assert.assertTrue(protocols.length > 0);
        }
        catch (Exception e) {
            Assert.assertNull(e);
        }
    }

    public void validateSSLProtocol(Service s, SSLSecurityProtocol securityProtocol) {
        Service.setSslSecurityProtocol(securityProtocol);
        s.login(service.getUsername(), service.getPassword());
        s.getInfo();
    }

    @Test
    public void testSSLSecurityProtocols() {
        Service s = new Service("localhost");

        Integer javaVersion = getJavaVersion();
        Assert.assertNotNull(javaVersion);

        // TLSv1.1 and TLSv1.2 were added in Java 7
        if (javaVersion >= 7) {
            validateSSLProtocol(s, SSLSecurityProtocol.TLSv1_2);
            validateSSLProtocol(s, SSLSecurityProtocol.TLSv1_1);
        }

        // TLSv1 is supported in Java 6-8, always check
        validateSSLProtocol(s, SSLSecurityProtocol.TLSv1);

        // SSLv3 is disabled by default in Java 8
        if (javaVersion < 8) {
            validateSSLProtocol(s, SSLSecurityProtocol.SSLv3);
        }
    }
}
