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

/**
 * This example will demonstrate how to use a specific SSL/TLS
 * protocol with the Splunk SDK for Java.
 *
 * Additionally, there's a small code sample showing how to
 * use a custom SSLSocketFactory.
 */

package com.splunk.examples.ssl_protocols;

import com.splunk.*;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Program {

    public static Integer getJavaVersion() {
        String ver = System.getProperty("java.version");
        return Integer.parseInt(ver.substring(2, 3));
    }

    public static void main(String[] args) {
        Command command = Command.splunk("info").parse(args);
        Service service = Service.connect(command.opts);

        // At this point, the default protocol is SSLv3.
        // Possible values are TLSv1.2, TLSv1.1, TLSv1 & SSLv3
        // These are defined by the SSLSecurityProtocol enum
        // Java 8 disables SSLv3 by default

        int version = getJavaVersion();
        System.out.println("Your Java version is: " + version);

        // Use TLSv1, should be available in every version of Java
        System.out.println("Now trying to connect to Splunk using TLSv1");
        Service.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1);
        service.login();

        if (version >= 7) {
            // Use TLSv1.1, available in Java 7 and up
            System.out.println("Now trying to connect to Splunk using TLSv1.1");
            Service.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_1);
            service.login();

            // Use TLSv1.2, available in Java 7 and up
            System.out.println("Now trying to connect to Splunk using TLSv1.2");
            Service.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);
            service.login();
        }

        System.out.println("Your Splunk version is " + service.getInfo().getVersion());

        // You can also specify your own SSLSocketFactory by using the following static method.
        Service.setSSLSocketFactory(createSSLSocketFactory());
    }

    public static SSLSocketFactory createSSLSocketFactory() {
        // Implement your custom SSLSocketFactory in a method like this
        return new SSLSocketFactory() {
            @Override
            public String[] getDefaultCipherSuites() {
                return new String[0];
            }

            @Override
            public String[] getSupportedCipherSuites() {
                return new String[0];
            }

            @Override
            public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
                return null;
            }

            @Override
            public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
                return null;
            }

            @Override
            public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
                return null;
            }

            @Override
            public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
                return null;
            }

            @Override
            public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
                return null;
            }
        };
    }
}