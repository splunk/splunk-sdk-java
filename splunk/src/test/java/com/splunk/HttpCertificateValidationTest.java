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
import org.junit.BeforeClass;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class HttpCertificateValidationTest extends SDKTestCase {
    private HttpService httpService;

    /**
     * This method will be executed when class is loaded.
     */
    @BeforeClass
    public static void preClassLoadActions() {
        // Bypass the certification validation here.
        HttpService.setValidateCertificates(false);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        httpService = new HttpService(
                (String) command.opts.get("host"),
                (Integer) command.opts.get("port"),
                (String) command.opts.get("scheme")
        );
    }

    @Test
    public void testSSLSocketUsingCertificateFlag() throws Exception {

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] byPassTrustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }
                    }
            };
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
            SSLSocketFactory TLSOnlySSLFactory = sslContext.getSocketFactory();
            Service.setSSLSocketFactory(TLSOnlySSLFactory);

            validateSSLSocketFactory(Service.getSSLSocketFactory());
        } catch (Exception e) {
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
}
