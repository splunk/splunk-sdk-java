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

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.NoSuchAlgorithmException;

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
    public void testHttpServiceWithHostIP(){
        HttpService service = new HttpService("127.0.0.1");
        ResponseMessage response = service.get("/");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(firstLineIsXmlDtd(response.getContent()));
    }

    @Test
    public void testHttpServiceWithHostIPv6(){
        // IPv6 Host without the [] brackets
        HttpService service = new HttpService("::1");
        ResponseMessage response = service.get("/");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(firstLineIsXmlDtd(response.getContent()));
        
        // IPv6 Host with the [] brackets
        HttpService newService = new HttpService("[::1]");
        ResponseMessage resp = newService.get("/");
        Assert.assertEquals(200, resp.getStatus());
        Assert.assertTrue(firstLineIsXmlDtd(resp.getContent()));
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
        Assert.assertEquals("POST", request.getMethod());
        
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
        Assert.assertEquals(200, response.getStatus());
        Assert.assertNotNull(response.getHeader());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSSLSocketFactorySetNull(){
        HttpService.setSSLSocketFactory(null);
    }



    @Test
    public void testSSLSocketFactory() {
        try {
            SSLSocketFactory factory = Service.getSSLSocketFactory();
            try (SSLSocket socket = (SSLSocket) factory.createSocket((String)command.opts.get("host"), 8089)) {
                String[] protocols = socket.getEnabledProtocols();
                Assert.assertTrue(protocols.length > 0);
            }
        }
        catch (Exception e) {
            Assert.assertNull(e);
        }
    }

    public void validateSSLSocketFactory(SSLSocketFactory factory) {
        // Backup the old value
        SSLSocketFactory old = Service.getSSLSocketFactory();

        Service.setSSLSocketFactory(factory);
        Service s = new Service(service.getHost());
        s.login(service.getUsername(), service.getPassword());
        Assert.assertEquals(service.getUsername(), s.getUsername());
        Assert.assertEquals(service.getPassword(), s.getPassword());
        Assert.assertEquals(service.getInfo().keySet(), s.getInfo().keySet());
        Assert.assertEquals(service.getInfo().getVersion(), s.getInfo().getVersion());

        // Restore the old value
        Service.setSSLSocketFactory(old);
    }

    @Test
    public void testCustomSSLSocketFactories() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] byPassTrustManagers = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                }
            };
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
            SSLSocketFactory TLSOnlySSLFactory = sslContext.getSocketFactory();
            Service.setSSLSocketFactory(TLSOnlySSLFactory);

            validateSSLSocketFactory(TLSOnlySSLFactory);
        }
        catch (Exception e) {
            Assert.assertNull(e);
        }

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] byPassTrustManagers = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                }
            };
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
            SSLSocketFactory SSLOnlySSLFactory = sslContext.getSocketFactory();
            Service.setSSLSocketFactory(SSLOnlySSLFactory);

            validateSSLSocketFactory(SSLOnlySSLFactory);
        }
        catch (Exception e) {
            // Swallow exceptions for Java 8, since we know SSLv3 is disabled
            // by default
            if (getJavaVersion() < 8) {
                Assert.assertNull(e);
            }
        }
    }

    public void validateSSLProtocol(Service s, SSLSecurityProtocol securityProtocol) {
        // Backup the old value
        SSLSecurityProtocol old = Service.getSslSecurityProtocol();

        Service.setSslSecurityProtocol(securityProtocol);

        s.login(service.getUsername(), service.getPassword());
        Assert.assertEquals(service.getUsername(), s.getUsername());
        Assert.assertEquals(service.getPassword(), s.getPassword());
        Assert.assertEquals(service.getInfo().keySet(), s.getInfo().keySet());
        Assert.assertEquals(service.getInfo().getVersion(), s.getInfo().getVersion());

        // Restore the value
        Service.setSslSecurityProtocol(old);
    }

    @Test
    public void testSSLSecurityProtocols() {
        Service s = new Service(service.getHost());

        Integer javaVersion = getJavaVersion();
        Assert.assertNotNull(javaVersion);

        // TLSv1.1 and TLSv1.2 were added in Java 7
        if (javaVersion >= 7) {
            String[] supportedProtos = new String[0];
            try {
                supportedProtos = SSLContext.getDefault().getSupportedSSLParameters().getProtocols();
            } catch (NoSuchAlgorithmException e) {
            }

           for (String proto : supportedProtos) {
               if (proto.equals(SSLSecurityProtocol.TLSv1_2.toString())) {
                   validateSSLProtocol(s, SSLSecurityProtocol.TLSv1_2);
               } else if (proto.equals(SSLSecurityProtocol.TLSv1_1.toString())) {
                   validateSSLProtocol(s, SSLSecurityProtocol.TLSv1_1);
               }
           }
        }

        // TLSv1 is supported in Java 6-8, always check
        validateSSLProtocol(s, SSLSecurityProtocol.TLSv1);

        // SSLv3 is disabled by default in Java 8
        if (javaVersion < 8) {
            validateSSLProtocol(s, SSLSecurityProtocol.SSLv3);
        }
    }
}
