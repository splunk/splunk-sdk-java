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

// UNDONE: Support for pluggable trust managers.

package com.splunk.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;

public class Service {
    String scheme = "https";
    String host = "localhost";
    int port = 8089;

    public Service() {
        setTrustPolicy();
    }

    public Service(String host) {
        this.host = host;
        setTrustPolicy();
    }

    public Service(String host, int port) {
        this.host = host;
        this.port = port;
        setTrustPolicy();
    }

    public Service(String host, int port, String scheme) {
        this.host = host;
        this.port = port;
        this.scheme = scheme;
        setTrustPolicy();
    }

    public String getHost() { 
        return this.host; 
    }

    public void setHost(String value) {
        this.host = value;
    }

    public int getPort() {
        return this.port;
    }

    // Set trust policy to be used by this instance.
    void setTrustPolicy() {
        TrustManager[] trustAll = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(
                    X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(
                    X509Certificate[] certs, String authType) { }
            }
        };
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustAll, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(
                new HostnameVerifier() {
                    public boolean verify(
                        String urlHostName, SSLSession session) { return true; }
                });
        }
        catch (Exception e) {
            throw new RuntimeException("Error installing trust manager.");
        }
    }

    public void setPort(int value) {
        this.port = value; 
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String value) {
        this.scheme = value;
    }

    public ResponseMessage send(RequestMessage request)
        throws
            IOException,
            MalformedURLException,
            ProtocolException
    {
        String method = request.getMethod();

        String prefix = String.format("%s://%s:%d",
            this.scheme, this.host, this.port);
        URL url = new URL(prefix + request.getPath());

        HttpURLConnection cn = (HttpURLConnection)url.openConnection();
        cn.setRequestMethod(method);

        // UNDONE: Process request.header

        // UNDONE: The following should be defaults if not already passed
        // in the messages header.
        cn.setRequestProperty("User-Agent", "splunk-sdk-java/0.1");
        cn.setRequestProperty("Accept", "*/*");

        // Explicitly disable some features we don't want
        cn.setUseCaches(false);
        cn.setAllowUserInteraction(false);

        // UNDONE: Handle various request content types (stream, string, etc)
        // and set Content-Length accordingly.

        if (method == "GET") {
            cn.setRequestProperty("Content-Length", "0");
            cn.connect();
            return new ResponseMessage(
                cn.getResponseCode(),
                cn.getInputStream());
        }

        if (method == "POST") {
            cn.setDoOutput(true); 
        }

        if (method == "DELETE") {
        }

        throw new IllegalArgumentException(
            String.format("Unsupported method: '%s'", method));
    }
}

