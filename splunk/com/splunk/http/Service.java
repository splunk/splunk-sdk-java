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

// UNDONE: Support for Splunk namespaces
// UNDONE: Support for pluggable trust managers.
// UNDONE: Timeouts, connection & request.

package com.splunk.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.Socket;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public class Service {
    protected String scheme = "https";
    protected String host = "localhost";
    protected int port = 8089;

    private String prefix = null;

    static Map<String, String> defaultHeader = new HashMap<String, String>() {{
        put("User-Agent", "splunk-sdk-java/0.1");
        put("Accept", "*/*");
    }};

    TrustManager[] trustAll = new TrustManager[] {
        new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(
                X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(
                X509Certificate[] certs, String authType) { }
        }
    };

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

    // Returns the count of args in the given map
    private static int count(Map<String, String> args) {
        if (args == null) return 0;
        return args.size();
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

    public ResponseMessage get(String path) {
        return send(path, new RequestMessage("GET"));
    }

    public ResponseMessage get(String path, Map<String, String> args) {
        if (count(args) > 0) path = path + "?" + encode(args);
        RequestMessage request = new RequestMessage("GET");
        return send(path, request);
    }

    public String getHost() { 
        return this.host; 
    }

    public int getPort() {
        return this.port;
    }

    // Returns the URL prefix for this service.
    public String getPrefix() {
        if (this.prefix == null)
            this.prefix = String.format("%s://%s:%s",
                this.scheme, this.host, this.port);
        return this.prefix;
    }

    public String getScheme() {
        return this.scheme;
    }

    public URL getUrl(String path) {
        try {
            return new URL(getPrefix() + path);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ResponseMessage post(String path) {
        return post(path, null);
    }

    public ResponseMessage post(String path, Map<String, String> args) {
        RequestMessage request = new RequestMessage("POST");
        request.getHeader().put(
            "Content-Type", "application/x-www-form-urlencoded");
        if (args != null && args.size() > 0) request.setContent(encode(args));
        return send(path, request);
    }

    public ResponseMessage delete(String path) {
        RequestMessage request = new RequestMessage("DELETE");
        return send(path, request);
    }

    public ResponseMessage delete(String path, Map<String, String> args) {
        if (count(args) > 0) path = path + "?" + encode(args);
        RequestMessage request = new RequestMessage("DELETE");
        return send(path, request);
    }

    public ResponseMessage send(String path, RequestMessage request) {
        // Construct a full URL to the resource
        URL url = getUrl(path);

        // Create and initialize the connection object
        HttpURLConnection cn;
        try {
            cn = (HttpURLConnection)url.openConnection();
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        cn.setUseCaches(false);
        cn.setAllowUserInteraction(false);

        // Set the reqeust method
        String method = request.getMethod();
        try {
            cn.setRequestMethod(method);
        }
        catch (ProtocolException e) {
            throw new RuntimeException(e.getMessage());
        }

        // Add headers from request message
        Map<String, String> header = request.getHeader();
        for (Entry<String, String> entry : header.entrySet())
            cn.setRequestProperty(entry.getKey(), entry.getValue());

        // Add default headers that were absent from the request message
        for (Entry<String, String> entry : defaultHeader.entrySet()) {
            String key = entry.getKey();
            if (header.containsKey(key)) continue;
            cn.setRequestProperty(key, entry.getValue());
        }

        // Write out request content, if any
        try {
            Object content = request.getContent();
            if (content != null) {
                cn.setDoOutput(true);
                OutputStream stream = cn.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(stream);
                // UNDONE: Figure out how to support streaming request content
                writer.write((String)content);
                writer.close();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        // System.out.format("%s %s => ", method, url.toString());

        // Execute the request
        try {
            cn.connect();
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        int status;
        try {
            status = cn.getResponseCode();
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        InputStream input = null;
        try {
            input = status >= 400 
                ? cn.getErrorStream() 
                : cn.getInputStream();
        }
        catch (IOException e) { assert(false); }

        // UNDONE: Populate response headers
        ResponseMessage response = new ResponseMessage(status, input);

        // System.out.format("%d\n", status);

        if (status >= 400)
            throw HTTPException.create(response);

        return response;
    }

    public Socket streamConnect() throws IOException {
        if (this.scheme.equals("https")) {
            SSLSocketFactory sslsocketfactory;
            try {
                SSLContext context = SSLContext.getInstance("SSL");
                context.init(null, trustAll, new java.security.SecureRandom());
                sslsocketfactory = context.getSocketFactory();
            }
            catch (Exception e) {
                throw new RuntimeException("Error installing trust manager.");
            }
            return sslsocketfactory.createSocket(this.host, this.port);
        } 
        return new Socket(this.host, this.port);
    }

    // Set trust policy to be used by this instance.
    void setTrustPolicy() {
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustAll, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                context.getSocketFactory());
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
}

