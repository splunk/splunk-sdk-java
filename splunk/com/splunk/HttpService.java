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

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The {@code HttpService} class represents a generic HTTP service at a given
 * address ({@code host:port}), accessed using a given protocol scheme
 * ({@code http} or {@code https}).
 */
public class HttpService {
    // For debugging purposes
    private static final boolean VERBOSE_REQUESTS = false;

    private static final SSLSocketFactory SSL_SOCKET_FACTORY = createSSLFactory();
    
    private static String HTTPS_SCHEME = "https";
    private static String HTTP_SCHEME = "http";

    private static final HostnameVerifier HOSTNAME_VERIFIER = new HostnameVerifier() {
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };
    
    /** A variable to hold an optional custom HTTPS handler */
    protected URLStreamHandler httpsHandler = null;

    /** The scheme used to access the service. */
    protected String scheme = "https";

    /** The host name of the service. */
    protected String host = "localhost";

    /** The port number of the service. */
    protected int port = 8089;

    private String prefix = null;

    static Map<String, String> defaultHeader = new HashMap<String, String>() {{
        put("User-Agent", "splunk-sdk-java/1.3.2");
        put("Accept", "*/*");
    }};

    /** Constructs a new {@code HttpService} instance. */
    public HttpService() {
    }

    /**
     * Constructs a new {@code HttpService} instance at the given host.
     *
     * @param host The host name of the service.
     */
    public HttpService(String host) {
        this.host = host;
    }

    /**
     * Constructs a new {@code HttpService} instance at the given host and port.
     *
     * @param host The host name of the service.
     * @param port The port number of the service.
     */
    public HttpService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Constructs a new {@code HttpService} instance using the given host,
     * port, and scheme.
     *
     * @param host The host name of the service.
     * @param port The port number of the service.
     * @param scheme Scheme for accessing the service ({@code http} or 
     * {@code https}).
     */
    public HttpService(String host, int port, String scheme) {
        this.host = host;
        this.port = port;
        this.scheme = scheme;
    }
    
    /**
     * Constructs a new {@code HttpService} instance using the given host,
     * port, and scheme, and instructing it to use the specified HTTPS handler.
     *
     * @param host The host name of the service.
     * @param port The port number of the service.
     * @param scheme Scheme for accessing the service ({@code http} or 
     * {@code https}).
     */
    public HttpService(String host, int port, String scheme, 
    		URLStreamHandler httpsHandler) {
        this.host = host;
        this.port = port;
        this.scheme = scheme;
        this.httpsHandler = httpsHandler;
    }

    // Returns the count of arguments in the given {@code args} map.
    private static int count(Map<String, Object> args) {
        if (args == null) return 0;
        return args.size();
    }

    /**
     * Issues an HTTP GET request against the service using a given path.
     *
     * @param path The request path.
     * @return The HTTP response.
     */
    public ResponseMessage get(String path) {
        return send(path, new RequestMessage("GET"));
    }

    /**
     * Issues an HTTP GET request against the service using a given path and
     * query arguments.
     *
     * @param path The request path.
     * @param args The query arguments.
     * @return The HTTP response.
     */
    public ResponseMessage get(String path, Map<String, Object> args) {
        if (count(args) > 0)
            path = path + "?" + Args.encode(args);
        RequestMessage request = new RequestMessage("GET");
        return send(path, request);
    }

    /**
     * Returns the host name of this service.
     *
     * @return The host name.
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Returns the port number of this service.
     *
     * @return The port number.
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Returns the URL prefix of this service, consisting of
     * {@code scheme://host[:port]}.
     *
     * @return The URL prefix.
     */
    public String getPrefix() {
        if (this.prefix == null)
            this.prefix = String.format("%s://%s:%s",
                this.scheme, this.host, this.port);
        return this.prefix;
    }

    /**
     * Returns the scheme used by this service.
     *
     * @return The scheme.
     */
    public String getScheme() {
        return this.scheme;
    }

    /**
     * Constructs a fully-qualified URL for this service using a given path.
     *
     * @param path The path to qualify.
     * @return The fully-qualified URL for the service.
     */
    public URL getUrl(String path) {
        try {
        	if (HTTPS_SCHEME.equals(getScheme()) && httpsHandler != null) {
        		// This branch is not currently covered by unit tests as I 
        		// could not figure out a generic way to get the default
        		// HTTPS handler.
        		return new URL(getScheme(), getHost(), getPort(), path, 
        				httpsHandler);
        	} else {
        		return new URL(getScheme(), getHost(), getPort(), path);
        	}
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Issues a POST request against the service using a given path.
     *
     * @param path The request path.
     * @return The HTTP response.
     */
    public ResponseMessage post(String path) {
        return post(path, null);
    }

    /**
     * Issues a POST request against the service using a given path and
     * form arguments.
     *
     * @param path The request path.
     * @param args The form arguments.
     * @return The HTTP response.
     */
    public ResponseMessage post(String path, Map<String, Object> args) {
        RequestMessage request = new RequestMessage("POST");
        request.getHeader().put(
            "Content-Type", "application/x-www-form-urlencoded");
        if (count(args) > 0)
            request.setContent(Args.encode(args));
        return send(path, request);
    }

    /**
     * Issues a DELETE request against the service using a given path.
     *
     * @param path The request path.
     * @return The HTTP response.
     */
    public ResponseMessage delete(String path) {
        RequestMessage request = new RequestMessage("DELETE");
        return send(path, request);
    }

    /**
     * Issues a DELETE request against the service using a given path
     * and query arguments.
     *
     * @param path The request path.
     * @param args The query arguments.
     * @return The HTTP response.
     */
    public ResponseMessage delete(String path, Map<String, Object> args) {
        if (count(args) > 0)
            path = path + "?" + Args.encode(args);
        RequestMessage request = new RequestMessage("DELETE");
        return send(path, request);
    }

    /**
     * Opens a socket to this service.
     *
     * @return The socket.
     * @throws IOException
     */
    Socket open() throws IOException {
        if (this.scheme.equals("https")) {
            return SSL_SOCKET_FACTORY.createSocket(this.host, this.port);
        }
        return new Socket(this.host, this.port);
    }

    /**
     * Issue an HTTP request against the service using a given path and
     * request message.
     *
     * @param path The request path.
     * @param request The request message.
     * @return The HTTP response.
     */
    public ResponseMessage send(String path, RequestMessage request) {
        // Construct a full URL to the resource
        URL url = getUrl(path);

        // Create and initialize the connection object
        HttpURLConnection cn;
        try {
            cn = (HttpURLConnection)url.openConnection();
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if(cn instanceof HttpsURLConnection) {
            ((HttpsURLConnection)cn).setSSLSocketFactory(SSL_SOCKET_FACTORY);
            ((HttpsURLConnection)cn).setHostnameVerifier(HOSTNAME_VERIFIER);
        }
        cn.setUseCaches(false);
        cn.setAllowUserInteraction(false);

        // Set the request method
        String method = request.getMethod();
        try {
            cn.setRequestMethod(method);
        }
        catch (ProtocolException e) {
            throw new RuntimeException(e.getMessage(), e);
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
                OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
                writer.write((String)content);
                writer.close();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        if (VERBOSE_REQUESTS) {
            System.out.format("%s %s => ", method, url.toString());
        }

        // Execute the request
        try {
            cn.connect();
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        int status;
        try {
            status = cn.getResponseCode();
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        InputStream input = null;
        try {
            input = status >= 400
                ? cn.getErrorStream()
                : cn.getInputStream();
        }
        catch (IOException e) { assert(false); }

        ResponseMessage response = new ResponseMessage(status, input);

        if (VERBOSE_REQUESTS) {
            System.out.format("%d\n", status);
            if (method.equals("POST")) {
                System.out.println("    " + request.getContent());
            }
        }

        if (status >= 400)
            throw HttpException.create(response);

        return response;
    }

    private static SSLSocketFactory createSSLFactory() {
        TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
        };
        try {
            SSLContext context = SSLContext.getInstance("TLSv1.2");
            context.init(null, trustAll, new java.security.SecureRandom());
            return new SSLv3SocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            throw new RuntimeException("Error setting up SSL socket factory: " + e, e);
        }
    }

    private static final class SSLv3SocketFactory extends SSLSocketFactory {
        private final SSLSocketFactory delegate;

        public static final String[] PROTOCOLS = {"TLSv1.2"};

        private SSLv3SocketFactory(SSLSocketFactory delegate) {
            this.delegate = delegate;
        }

        private Socket configure(Socket socket) {
            if (socket instanceof SSLSocket) {
                ((SSLSocket) socket).setEnabledProtocols(PROTOCOLS);
            }
            return socket;
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return delegate.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return delegate.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
            return configure(delegate.createSocket(socket, s, i, b));
        }

        @Override
        public Socket createSocket() throws IOException {
            return configure(delegate.createSocket());
        }

        @Override
        public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
            return configure(delegate.createSocket(s, i));
        }

        @Override
        public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
            return configure(delegate.createSocket(s, i, inetAddress, i1));
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
            return configure(delegate.createSocket(inetAddress, i));
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
            return configure(delegate.createSocket(inetAddress, i, inetAddress1, i1));
        }
    }

}

