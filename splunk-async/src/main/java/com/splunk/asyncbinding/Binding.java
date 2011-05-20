package com.splunk.asyncbinding;

/**
 * Binding
 * version 1.0
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

import com.ning.http.client.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.net.ssl.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Low level asychronous splunk binding and communication layer between java client and splunkd.
 */
public class Binding {

    private final Context context = new Context();

    /**
     * Set security to trust all servers and initialize the default splunk Context
     */
    public Binding() {
        // trust all servers
        blindTrust();
    }

    /**
     * Allow all certificates by overriding x509 certificates
     */
    private void blindTrust() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]
                {
                        new X509TrustManager() {

                            /**
                             * @return null always
                             */
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            /**
                             */
                            public void checkClientTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }

                            /**
                             */
                            public void checkServerTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }
                        }
                };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (Exception e) {
            System.out.println("SplunkException setting up SSL blind trust: " + e);
        }
    }

    /**
     */
    private final HostnameVerifier hv = new HostnameVerifier() {
        /**
         * @param urlHostName name of host
         * @param session SSL session
         * @return true always
         */
        public boolean verify(String urlHostName, SSLSession session) {
            return true;
        }
    };

    /**
     * turn a url path into a non-relative and proper namespace
     *
     * @param path String of path
     * @return string of fully qualified path
     */
    private String url(String path) {

        // if already prefixed with scheme, return as is
        if (path.startsWith(this.context.getContextValue("scheme")))
            return path;
        // if already absolute, return as is
        if (path.startsWith("/"))
            return path;
        // if there is no namespace pre-pend splunk root and append path
        if (this.context.getContextValue("namespace").length() == 0) {
            return "/services/" + path;
        }

        /*
         * Since there is a namespace, pull apart, converting wildcards to
         * splunk notation and NameSpace root path.
         */
        String[] parts = this.context.getContextValue("namespace").split(":");
        String username = (parts[0].equals("*")) ? "-" : parts[0];
        String appname = (parts[1].equals("*")) ? "-" : parts[1];
        return "/servicesNS/" + username + "/" + appname + "/" + path;
    }

    /**
     * turn a url path into a fully qualified url, no arguments
     *
     * @param path String of path
     * @return string of fully qualified path
     */
    private String splunkURL(String path) {

        // fully qualify the URL into <scheme>://<host>:<port>/<url(root)>
        if (path.startsWith(this.context.getContextValue("scheme")))
            return url(path);

        return this.context.getContextValue("scheme") + "://" +
                this.context.getContextValue("host") + ":" +
                this.context.getContextValue("port") + url(path);

    }

    /**
     * turn a url path into a fully qualified url, add key/value pair hashmap for args
     *
     * @param path String of path
     * @param args hashmap of query arguments
     * @return string of fully qualified path
     * @throws UnsupportedEncodingException on bad encoding
     */
    private String splunkURL(String path, HashMap<String, String> args) throws UnsupportedEncodingException {

        // fully qualify the URL into <scheme>://<host>:<port>/<url(root)>
        if (path.startsWith(this.context.getContextValue("scheme")))
            return url(path) + "?" + encodeArgs(args);

        return this.context.getContextValue("scheme") + "://" +
                this.context.getContextValue("host") + ":" +
                this.context.getContextValue("port") + url(path) + "?" +
                encodeArgs(args);
    }

    /**
     * turn a url path into a fully qualified url, add string for args
     *
     * @param path String of path
     * @param args String of query arguments
     * @return string of fully qualified path
     */
    private String splunkURL(String path, String args) {

        // fully qualify the URL into <scheme>://<host>:<port>/<url(root)>
        if (path.startsWith(this.context.getContextValue("scheme")))
            return url(path) + "?" + args;

        return this.context.getContextValue("scheme") + "://" +
                this.context.getContextValue("host") + ":" +
                this.context.getContextValue("port") + url(path) + "?" + args;
    }

    /**
     * private/common synchronous HTTP login for multiple/overloaded logins
     *
     * @throws IOException     percolates IOException from lower level HTTP access
     * @throws SplunkException Splunk contextual exceptions
     */
    private void commonLogin() throws IOException, SplunkException {

        // build args from splunkContext
        HashMap<String, String> arguments = new HashMap<String, String>();
        arguments.put("username", this.context.getContextValue("username"));
        arguments.put("password", this.context.getContextValue("password"));

        try {
            // POST using un-encoded username and password key/value pairs, use synchronous post
            String returnXML = post("/services/auth/login", arguments);

            // extract sid from XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            String sid = "";
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource inStream = new InputSource();
                inStream.setCharacterStream(new java.io.StringReader(returnXML));
                Document dom = db.parse(inStream);
                Element elements = dom.getDocumentElement();
                NodeList nl = elements.getElementsByTagName("sessionKey");
                if (nl != null) {
                    // should only be ONE sessionKey in the login
                    for (int iIndex = 0; iIndex < nl.getLength(); iIndex++) {
                        if (nl.item(iIndex).getNodeType() == Node.ELEMENT_NODE) {
                            Element nameElement = (Element) nl.item(iIndex);
                            sid = nameElement.getFirstChild().getNodeValue().trim();
                        }
                    }
                }

                // save sessionkKey in splunkContext
                this.context.setContextValue("sessionKey", sid);
            } catch (ParserConfigurationException e) {
                throw new SplunkException("Login XML parse failed to find security token");
            } catch (SAXException e) {
                throw new SplunkException("Login XML parse failed");
            }
        } catch (InterruptedException e) {
            throw new SplunkException("Interrupted IO");

        } catch (ExecutionException e) {
            throw new SplunkException("Execution Exception");

        } catch (UnsupportedEncodingException e) {
            // shouldn't happen, since we hard-code UTF-8
        }
    }

    /**
     * encode key/value pairs into URL safe, UTF-8 encoded string
     *
     * @param args hashmap of key/value pairs
     * @return encoded argument in a sinlgle string
     * @throws UnsupportedEncodingException but shouldn't happen, encoding is hard-coded to UTF-8
     */
    private String encodeArgs(HashMap<String, String> args) throws UnsupportedEncodingException {

        String encodedArgs = "";
        Set<Map.Entry<String, String>> set = args.entrySet();

        Iterator<Map.Entry<String, String>> it = set.iterator();

        while (it.hasNext()) {
            Map.Entry<String, String> kv = it.next();
            encodedArgs = encodedArgs + URLEncoder.encode(kv.getKey(), "UTF-8")
                    + "="
                    + URLEncoder.encode(args.get(kv.getValue()), "UTF-8");
            if (it.hasNext()) {
                encodedArgs = encodedArgs + "&";
            }
        }

        return encodedArgs;
    }

    // Public API -------------------------------------------------------------------

    /**
     * private/common asynchronous HTTP POST for multiple/overloaded POSTs
     *
     * @param url partial or fully qualified URL to post to
     * @param data string encoded POST data
     * @param callback for asychronous HTTP operations
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     */
    public Future<String> post(String url, HashMap<String, String> data, AsyncHandler<String> callback) throws IOException {

        // fully qualify the URL, idempotent
        url = splunkURL(url);

        // connect to the endpoint
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestBuilder request = new RequestBuilder("POST");

        request.setUrl(url);
        request.addHeader("Authorization", "Splunk " + this.context.getContextValue("sessionKey"))
                .addHeader("Content-type", "application/x-www-form-urlencoded");

        Set<Map.Entry<String, String>> set = data.entrySet();

        for (Map.Entry<String, String> kv : set) {
            request.addParameter(kv.getKey(), kv.getValue());
        }

        return asyncHttpClient.executeRequest(request.build(), callback);

    }

    /**
     * perform an asynchronous HTTP POST with dual arguments (standard query + standard post arguments)
     *
     * @param url  partial or fully qualified URL to POST to
     * @param query standard query arguments encoded into URL
     * @param args un-encoded hashmap key/value pair arguments to POST
     * @param callback for asychronous HTTP operations
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     */
    public Future<String> post(String url, HashMap<String, String> query, HashMap<String, String> args, AsyncHandler<String> callback) throws IOException {

        String queryURL = encodeArgs(query);
        url = url + "?" + queryURL;

        return post(url, args, callback);
    }

    /**
     * private/common synchronous HTTP POST for multiple/overloaded POSTs
     *
     * @param url partial or fully qualified URL to post to
     * @param data string encoded POST data
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     * @throws InterruptedException percolates InterruptedException from lower level HTTP access
     * @throws ExecutionException percolates ExecutionException from lower level HTTP access
     */
    public String post(String url, HashMap<String, String> data) throws IOException, ExecutionException, InterruptedException {

        // fully qualify the URL, idempotent
        url = splunkURL(url);

        Future<String> future = post(url, data, new AsyncHandler<String>() {
            private final StringBuilder builder = new StringBuilder();

            //@Override
            public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                //int statusCode = status.getStatusCode();
                // The Status have been read
                // If you don't want to read the headers,body or stop processing the response
                return STATE.CONTINUE;
            }

            //@Override
            public STATE onHeadersReceived(HttpResponseHeaders h) throws Exception {
                //FluentCaseInsensitiveStringsMap headers = h.getHeaders();
                // The headers have been read
                // If you don't want to read the body, or stop processing the response
                return STATE.CONTINUE;
            }

            //@Override
            public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                builder.append(new String(bodyPart.getBodyPartBytes()));
                return STATE.CONTINUE;
            }

            //@Override
            public String onCompleted() throws Exception {
                // Will be invoked once the response has been fully read or a ResponseComplete exception
                // has been thrown.
                return builder.toString();
            }

            //@Override
            public void onThrowable(Throwable t) {
            }
        });

        return future.get();
    }

    /**
     * perform an synchronous HTTP POST with dual arguments (standard query + standard post arguments)
     *
     * @param url  partial or fully qualified URL to POST to
     * @param query standard query arguments encoded into URL
     * @param args un-encoded hashmap key/value pair arguments to POST
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     * @throws InterruptedException percolates InterruptedException from lower level HTTP access
     * @throws ExecutionException percolates ExecutionException from lower level HTTP access
     */
    public String post(String url, HashMap<String, String> query, HashMap<String, String> args) throws IOException, InterruptedException, ExecutionException {

        String queryURL = encodeArgs(query);
        url = url + "?" + queryURL;

        Future<String> future = post(url, args, new AsyncHandler<String>() {
            private final StringBuilder builder = new StringBuilder();

            //@Override
            public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                //int statusCode = status.getStatusCode();
                // The Status have been read
                // If you don't want to read the headers,body or stop processing the response
                return STATE.CONTINUE;
            }

            //@Override
            public STATE onHeadersReceived(HttpResponseHeaders h) throws Exception {
                //FluentCaseInsensitiveStringsMap headers = h.getHeaders();
                // The headers have been read
                // If you don't want to read the body, or stop processing the response
                return STATE.CONTINUE;
            }

            //@Override
            public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                builder.append(new String(bodyPart.getBodyPartBytes()));
                return STATE.CONTINUE;
            }

            //@Override
            public String onCompleted() throws Exception {
                // Will be invoked once the response has been fully read or a ResponseComplete exception
                // has been thrown.
                return builder.toString();
            }

            //@Override
            public void onThrowable(Throwable t) {
            }
        });

        return future.get();
    }

    /**
     * private/common asynchronous HTTP GET for multiple/overloaded GETs
     *
     * @param url fully or partially qualified URL to get from
     * @return XML string from splunkd server
     * @param callback for asychronous HTTP operations
     * @throws IOException percolates IOException from lower level HTTP access
     */
    public Future<String> get(String url, AsyncHandler<String> callback) throws IOException {

        // Fully qualify the URL, idempotent
        String fullUrl = splunkURL(url);

        // connect to the endpoint, using asnych callback to caller.
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        return asyncHttpClient.prepareGet(fullUrl)
                .addHeader("Authorization", "Splunk " + this.context.getContextValue("sessionKey"))
                .execute(callback);
    }


    /**
     * perform an asynchronous HTTP GET with un-encoded hashmap key/value pair args
     *
     * @param url partial or fully qualified URL to GET from
     * @param args un-encoded hashmap key/value pair arguments to GET
     * @param callback for asychronous HTTP operations
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     */
    public Future<String> get(String url, HashMap<String, String> args, AsyncHandler<String> callback) throws IOException {

        // fully qualify the URL, idempotent
        url = splunkURL(url, args);

        // return common get
        return get(url, callback);
    }

    /**
     * perform a synchronous HTTP GET without query arguments
     *
     * @param url partial or fully qualified URL to GET from
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     * @throws InterruptedException percolates InterruptedException from lower level HTTP access
     * @throws ExecutionException percolates ExecutionException from lower level HTTP access
     */
    public String get(String url) throws IOException, InterruptedException, ExecutionException {

        // return common get, but make synchronous
        Future<String> future = get(url, new AsyncHandler<String>() {
            private final StringBuilder builder = new StringBuilder();

            //@Override
            public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                //int statusCode = status.getStatusCode();
                // The Status have been read
                // If you don't want to read the headers,body or stop processing the response
                return STATE.CONTINUE;
            }

            //@Override
            public STATE onHeadersReceived(HttpResponseHeaders h) throws Exception {
                //FluentCaseInsensitiveStringsMap headers = h.getHeaders();
                // The headers have been read
                // If you don't want to read the body, or stop processing the response
                return STATE.CONTINUE;
            }

            //@Override
            public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                builder.append(new String(bodyPart.getBodyPartBytes()));
                return STATE.CONTINUE;
            }

            //@Override
            public String onCompleted() throws Exception {
                // Will be invoked once the response has been fully read or a ResponseComplete exception
                // has been thrown.
                return builder.toString();
            }

            //@Override
            public void onThrowable(Throwable t) {
            }
        });

        return future.get();
    }

    /**
     * perform asynchronous HTTP DELETE
     *
     * @param url partial or fully qualified URL to get from
     * @param callback for asychronous HTTP operations
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     */
    public Future<String> delete(String url, AsyncHandler<String> callback) throws IOException {
        // fully qualify the URL, idempotent
        url = splunkURL(url);

        // connect to the endpoint
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestBuilder request = new RequestBuilder("DELETE");

        request.setUrl(url);
        request.addHeader("Authorization", "Splunk " + this.context.getContextValue("sessionKey"));

        return asyncHttpClient.executeRequest(request.build(), callback);

    }

    /**
     * perform asynchronous HTTP DELETE with un-encoded hashmap key/value pair args
     *
     * @param url partial or fully qualified URL delete
     * @param args un-encoded hashmap key/value pair arguments to GET
     * @param callback for asychronous HTTP operations
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     */
    public Future<String> delete(String url, HashMap<String, String> args, AsyncHandler<String> callback) throws IOException {

        // fully qualify the URL, idempotent
        url = splunkURL(url, args);

        // return common get
        return delete(url, callback);
    }

    /**
     * perform synchronous HTTP DELETE
     *
     * @param url partial or fully qualified URL to delete
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     * @throws InterruptedException percolates InterruptedException from lower level HTTP access
     * @throws ExecutionException percolates ExecutionException from lower level HTTP access
     */
    public String delete(String url) throws IOException, InterruptedException, ExecutionException {
        // return common get, but make synchronous
        Future<String> future = delete(url, new AsyncHandler<String>() {
            private final StringBuilder builder = new StringBuilder();

            //@Override
            public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                //int statusCode = status.getStatusCode();
                // The Status have been read
                // If you don't want to read the headers,body or stop processing the response
                return STATE.CONTINUE;
            }

            //@Override
            public STATE onHeadersReceived(HttpResponseHeaders h) throws Exception {
                //FluentCaseInsensitiveStringsMap headers = h.getHeaders();
                // The headers have been read
                // If you don't want to read the body, or stop processing the response
                return STATE.CONTINUE;
            }

            //@Override
            public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                builder.append(new String(bodyPart.getBodyPartBytes()));
                return STATE.CONTINUE;
            }

            //@Override
            public String onCompleted() throws Exception {
                // Will be invoked once the response has been fully read or a ResponseComplete exception
                // has been thrown.
                return builder.toString();
            }

            //@Override
            public void onThrowable(Throwable t) {
            }
        });

        return future.get();
    }

    /**
     * perform synchronous HTTP DELETE with un-encoded hashmap key/value pair arguments
     *
     * @param url  partial or fully qualified URL to GET from
     * @param args un-encoded hashmap key/value pair arguments to GET
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     * @throws InterruptedException percolates InterruptedException from lower level HTTP access
     * @throws ExecutionException percolates ExecutionException from lower level HTTP access
     */
    public String delete(String url, HashMap<String, String> args) throws IOException, InterruptedException, ExecutionException {

        // fully qualify the URL, idempotent
        url = splunkURL(url, args);

        // return common get
        return delete(url);
    }

    /**
     * login to splunk, using credentials from .splunkrc
     *
     * @throws IOException     percolates IOException from lower level HTTP access
     * @throws SplunkException Splunk contextual exceptions
     */
    public void login() throws IOException, SplunkException {
        try {
            // initialize Context from .splunkrc file
            this.context.initSplunkContext();
        } catch (SplunkException e) {
            System.out.println("WARNING: could not initialize splunk splunkContext: " + e);
        }

        commonLogin();
    }

    /**
     * login to splunk, overriding/bypassing credentials from .splunkrc with specified arguments
     *
     * @param host     hostname (DNS or IPaddress) of splunkd server
     * @param port     port that splunkd listens on
     * @param username username to login as
     * @param password password to login with
     * @param scheme   either http or https
     * @throws IOException     percolates IOException from lower level HTTP access
     * @throws SplunkException Splunk contextual exceptions
     */
    public void login(String host, String port, String username, String password, String scheme) throws IOException, SplunkException {

        // seed Context with passed in values
        this.context.setContextValue("host", host);
        this.context.setContextValue("port", port);
        this.context.setContextValue("username", username);
        this.context.setContextValue("password", password);
        this.context.setContextValue("scheme", scheme);

        commonLogin();
    }
}
