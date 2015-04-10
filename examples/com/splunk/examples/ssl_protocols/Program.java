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
 * protocol to connect to Splunk.
 * Additionally, there's a small code sample showing how to
 * use a custom SSLSocketFactory to connect to Splunk.
 */

package com.splunk.examples.ssl_protocols;

import com.splunk.Command;
import com.splunk.SSLSecurityProtocol;
import com.splunk.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.security.SecureRandom;

public class Program {

    public static Integer getJavaVersion() {
        String ver = System.getProperty("java.version");
        return Integer.parseInt(ver.substring(2, 3));
    }

    public static void main(String[] args) {
        Command command = Command.splunk("info").parse(args);

        int version = getJavaVersion();
        System.out.println("Your Java version is: " + version);

        // At this point, the default protocol is SSLv3.
        // Possible values are TLSv1.2, TLSv1.1, TLSv1 & SSLv3
        // These are defined by the SSLSecurityProtocol enum
        // Java 8 disables SSLv3 by default
        System.out.println("Now trying to connect to Splunk using SSLv3");
        try {
            Service.setSslSecurityProtocol(SSLSecurityProtocol.SSLv3);
            Service serviceSSLv3 = Service.connect(command.opts);
            serviceSSLv3.login();
            System.out.println("\t Success!");
        } catch (RuntimeException e) {
            System.out.println("\t Failure! ");
        }

        // TLSv1 is available by default in every modern version of Java
        System.out.println("Now trying to connect to Splunk using TLSv1");
        try {
            Service.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1);
            Service serviceTLSv1 = Service.connect(command.opts);
            serviceTLSv1.login();
            System.out.println("\t Success!");
        } catch (RuntimeException e) {
            System.out.println("\t Failure! ");
        }


        // TLSv1.1 is available by default in Java 7 and up
        System.out.println("Now trying to connect to Splunk using TLSv1.1");
        try {
            Service.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_1);
            Service serviceTLSv1_1 = Service.connect(command.opts);
            serviceTLSv1_1.login();
            System.out.println("\t Success!");
        } catch (RuntimeException e) {
            System.out.println("\t Failure! ");
        }

        // TLSv1.2 is available by default in Java 7 and up
        System.out.println("Now trying to connect to Splunk using TLSv1.2");
        try {
            Service.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);
            Service serviceTLSv1_2 = Service.connect(command.opts);
            serviceTLSv1_2.login();
            System.out.println("\t Success!");
        } catch (RuntimeException e) {
            System.out.println("\t Failure! ");
        }

        // You can also specify your own SSLSocketFactory, in this case any version of SSL
        System.out.println("Now trying to connect to Splunk using a custom SSL only SSLSocketFactory");
        try {
            // Create an SSLSocketFactory configured to use SSL only
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, null, new SecureRandom());
            SSLSocketFactory TLSOnlySSLFactory = sslContext.getSocketFactory();
            Service.setSSLSocketFactory(TLSOnlySSLFactory);

            Service serviceCustomSSLFactory = Service.connect(command.opts);
            serviceCustomSSLFactory.login();
            System.out.println("\t Success!");
        } catch (Exception e) {
            System.out.println("\t Failure!");
        }

        // You can also specify your own SSLSocketFactory, in this case any version of TLS
        System.out.println("Now trying to connect to Splunk using a custom TLS only SSLSocketFactory");
        try {
            // Create an SSLSocketFactory configured to use TLS only
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, new SecureRandom());
            SSLSocketFactory TLSOnlySSLFactory = sslContext.getSocketFactory();
            Service.setSSLSocketFactory(TLSOnlySSLFactory);

            Service serviceCustomSSLFactory = Service.connect(command.opts);
            serviceCustomSSLFactory.login();
            System.out.println("\t Success!");
        } catch (Exception e) {
            System.out.println("\t Failure!");
        }
    }
}