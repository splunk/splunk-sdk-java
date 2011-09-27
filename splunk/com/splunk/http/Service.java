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

//
// HTTP headers are case *in*sensitive
// URL query arguments are case *in*sensitive
// HTML Form POST arguments are case sensitive
//

package com.splunk.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
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

    static Map<String, String> defaultHeader = new HashMap<String, String>() {{
        put("User-Agent", "splunk-sdk-java/0.1");
        put("Accept", "*/*");
    }};

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

    public String encode(String value) {
        if (value == null) return "";
        String result = null;
        try {
            result = URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e) { assert false; }
        return result;
    }

    public String encode(Map<String, String> args) {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, String> entry : args.entrySet()) {
            if (builder.length() > 0)
                builder.append('&');
            builder.append(encode(entry.getKey()));
            builder.append('=');
            builder.append(encode(entry.getValue()));
        }
        return builder.toString();
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

    public ResponseMessage post(String path, Map<String, String> args)
        throws IOException
    {
        RequestMessage request = new RequestMessage("POST", path);
        request.getHeader().put(
            "Content-Type", "application/x-www-form-urlencoded");
        request.setContent(encode(args));
        return send(request);
    }

    public ResponseMessage send(RequestMessage request) throws IOException {
        String prefix = String.format("%s://%s:%d",
            this.scheme, this.host, this.port);
        URL url = new URL(prefix + request.getPath());

        HttpURLConnection cn = (HttpURLConnection)url.openConnection();
        cn.setUseCaches(false);
        cn.setAllowUserInteraction(false);

        String method = request.getMethod();
        cn.setRequestMethod(method);

        Map<String, String> header = request.getHeader();
        for (Entry<String, String> entry : header.entrySet())
            cn.setRequestProperty(entry.getKey(), entry.getValue());

        // Add default headers that were absent from the request message
        for (Entry<String, String> entry : defaultHeader.entrySet()) {
            String key = entry.getKey();
            if (header.containsKey(key)) continue;
            cn.setRequestProperty(key, entry.getValue());
        }

        Object content = request.getContent();
        if (content != null) {
            cn.setDoOutput(true);
            OutputStream stream = cn.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            // UNDONE: Figure out how to support streaming request content
            writer.write((String)content);
            writer.close();
        }

        // System.out.format("%s %s => ", method, url.toString());

        // Execute the request
        cn.connect();

        // UNDONE: Populate response header
        ResponseMessage response = new ResponseMessage(
            cn.getResponseCode(),
            cn.getInputStream());

        // System.out.format("%d\n", response.getStatus());

        return response;
    }
}

