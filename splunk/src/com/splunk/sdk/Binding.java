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

package com.splunk.sdk;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

/**
 * Low level splunk sdk and communication layer between java client and splunkd.
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
        if (path.startsWith(context.getContextValue("scheme")))
            return path;
        // if already absolute, return as is
        if (path.startsWith("/"))
            return path;
        // if there is no namespace pre-pend splunk root and append path
        if (context.getContextValue("namespace").length() == 0) {
            return "/services/" + path;
        }

        /*
         * Since there is a namespace, pull apart, converting wildcards to
         * splunk notation and NameSpace root path.
         */
        String[] parts = context.getContextValue("namespace").split(":");
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
    private String fullURL(String path) {

        // fully qualify the URL into <scheme>://<host>:<port>/<url(root)>
        if (path.startsWith(context.getContextValue("scheme")))
            return url(path);

        return context.getContextValue("scheme") + "://" +
                context.getContextValue("host") + ":" +
                context.getContextValue("port") + url(path);

    }

    /**
     * turn a url path into a fully qualified url, add key/value pair HashMap for args
     *
     * @param path String of path
     * @param args HashMap of query arguments
     * @return string of fully qualified path
     */
    private String fullURL(String path,
                           HashMap<String, Object> args) {

        // fully qualify the URL into <scheme>://<host>:<port>/<url(root)>
        if (path.startsWith(context.getContextValue("scheme")))
            return url(path) + "?" + encodeArgs(args);

        return context.getContextValue("scheme") + "://" +
                context.getContextValue("host") + ":" +
                context.getContextValue("port") + url(path) + "?" +
                encodeArgs(args);
    }

    /**
     * turn a url path into a fully qualified url, add string for args
     *
     * @param path String of path
     * @param args String of query arguments
     * @return string of fully qualified path
     */
    private String fullURL(String path,
                           String args) {

        // fully qualify the URL into <scheme>://<host>:<port>/<url(root)>
        if (path.startsWith(context.getContextValue("scheme")))
            return url(path) + "?" + args;

        return context.getContextValue("scheme") + "://" +
                context.getContextValue("host") + ":" +
                context.getContextValue("port") + url(path) + "?" + args;
    }

    /**
     * private/common HTTP login for multiple/overloaded logins
     *
     * @throws IOException     percolates IOException from lower level HTTP access
     * @throws SplunkException Splunk contextual exceptions
     */
    private void commonLogin() throws IOException, SplunkException {

        // build args from splunkContext
        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("username", context.getContextValue("username"));
        arguments.put("password", context.getContextValue("password"));

        XMLReader XMLReader = new XMLReader();
        try {
            // POST using un-encoded username and password key/value pairs
            XMLEventReader ereader = XMLReader.Results(post("/services/auth/login", arguments));
            String returnXML = XMLReader.getContentsString(ereader);

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
            } catch (ParserConfigurationException e) {
                throw new SplunkException("Login XML parse failed to find security token");
            } catch (SAXException e) {
                System.out.println(e.getMessage());
                throw new SplunkException("Login XML parse failed");
            }

            // save sessionkKey in splunkContext
            context.setContextValue("sessionKey", sid);
        } catch (XMLStreamException e) {
            throw new SplunkException("Login XML stream exception ");
        }
    }

    private static String urlencode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * encode key/value pairs into URL safe, UTF-8 encoded string
     *
     * @param args HashMap of key/value pairs
     * @return encoded argument in a sinlgle string
     */
    private String encodeArgs(HashMap<String, Object> args) {

        StringBuilder encodedArgs = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Object> kv : args.entrySet()) {

            if (kv.getValue() instanceof String) {
                if (!first) {
                    encodedArgs.append("&");
                }
                first = false;
                encodedArgs.append(urlencode(kv.getKey()));
                encodedArgs.append("=");
                encodedArgs.append(urlencode(kv.getValue().toString()));
            } else if (kv.getValue() instanceof ArrayList) {
                List values = (List) kv.getValue();
                for (Object value : values) {
                    if (!first) {
                        encodedArgs.append("&");
                    }
                    first = false;
                    encodedArgs.append(urlencode(kv.getKey()));
                    encodedArgs.append("=");
                    encodedArgs.append(urlencode(value.toString()));
                }
            } else {
                throw new SplunkException("Argument keys must be String or ArrayList of Strings");
            }

        }
        return encodedArgs.toString();
    }

    private void setUrlConnection(HttpURLConnection urlConnection, String method) {

        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        try {
            urlConnection.setRequestMethod(method);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        if (context.getContextValue("sessionKey").length() > 0) {
            urlConnection.setRequestProperty("Authorization", "Splunk " + context.getContextValue("sessionKey"));
        }
    }

    private HttpURLConnection prequest(String operation,
                                       String url) throws IOException {
        HttpURLConnection urlConnection;
        URL splunkd;

        // fully qualify the URL, idempotent
        url = fullURL(url);

        // connect to the endpoint
        splunkd = new URL(url);
        urlConnection = (HttpURLConnection) splunkd.openConnection();
        setUrlConnection(urlConnection, operation);

        return urlConnection;
    }

    private HttpURLConnection prequest(String operation,
                                       String url,
                                       HashMap<String, Object> args) throws IOException {
        HttpURLConnection urlConnection;
        URL splunkd;

        /*
         * overload get/post/delete with similar arguments. For post,
         * args are written. For get/delete they are part of the url.
         */
        if (operation == "POST") {
            url = fullURL(url);
        } else {
            url = fullURL(url, args);
        }

        // connect to the endpoint
        splunkd = new URL(url);
        urlConnection = (HttpURLConnection) splunkd.openConnection();
        setUrlConnection(urlConnection, operation);

        if (operation == "POST") {
            OutputStreamWriter wr;

            // pack writable data
            String data = encodeArgs(args);


            // write the post, flush and close.
            wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(data);
            wr.flush();
            wr.close();
        }

        return urlConnection;
    }

    private HttpURLConnection prequest(String operation,
                                       String url,
                                       HashMap<String, Object> query,
                                       HashMap<String, Object> args) throws IOException {
        // only POST has query and args
        HttpURLConnection urlConnection;
        OutputStreamWriter wr;
        URL splunkd;

        // connect to the endpoint
        splunkd = new URL(fullURL(url, query));
        urlConnection = (HttpURLConnection) splunkd.openConnection();
        setUrlConnection(urlConnection, operation);

        // pack writable data
        String data = encodeArgs(args);

        // write the post, flush and close.
        wr = new OutputStreamWriter(urlConnection.getOutputStream());
        wr.write(data);
        wr.flush();
        wr.close();

        return urlConnection;
    }

    // Public API -------------------------------------------------------------------

    /**
     * perform an HTTP POST with dual arguments (standard query + standard post arguments)
     *
     * @param url   partial or fully qualified URL to POST to
     * @param args  un-encoded HashMap key/value pair arguments to POST
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     */
    public HttpURLConnection post(String url,
                                  HashMap<String, Object> args) throws IOException {
        // return common request
        return prequest("POST", url, args);
    }

    /**
     * perform an HTTP POST with dual arguments (standard query + standard post arguments)
     *
     * @param url   partial or fully qualified URL to POST to
     * @param query standard query arguments encoded into URL
     * @param args  un-encoded HashMap key/value pair arguments to POST
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     */
    public HttpURLConnection post(String url,
                                  HashMap<String, Object> query,
                                  HashMap<String, Object> args) throws IOException {
        // return common request
        return prequest("POST", url, query, args);
    }

    /**
     * private/common HTTP GET for multiple/overloaded GETs
     *
     * @param url fully or partially qualified URL to get from
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     */
    public HttpURLConnection get(String url) throws IOException {
        // return common request
        return prequest("GET", url);
    }

    /**
     * perform an HTTP GET with un-encoded hashmap key/value pair args
     *
     * @param url  partial or fully qualified URL to GET from
     * @param args un-encoded hashmap key/value pair arguments to GET
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     */
    public HttpURLConnection get(String url,
                                 HashMap<String, Object> args) throws IOException {
        // return common request
        return prequest("GET", url, args);
    }

    /**
     * perform  HTTP DELETE
     *
     * @param url fully qualified URL to delete
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     */
    public HttpURLConnection delete(String url) throws IOException {
        // return common request
        return prequest("DELETE", url);
    }

    /**
     * perform an HTTP DELETE with un-encoded hashmap key/value pair args
     *
     * @param url  partial or fully qualified URL to GET from
     * @param args un-encoded hashmap key/value pair arguments to GET
     * @return XML string from splunkd server
     * @throws IOException percolates IOException from lower level HTTP access
     */
    public HttpURLConnection delete(String url,
                                    HashMap<String, Object> args) throws IOException {
        // return common request
        return prequest("DELETE", url, args);
    }

    // main methods login() and request(), and their overloads

    /**
     * request from splunk
     * @param operation        GET, DELETE or POST
     * @param url  partial or fully qualified URL to GET from
     * @throws IOException     percolates IOException from lower level HTTP access
     * @throws SplunkException Splunk contextual exceptions
     */
    public HttpURLConnection request(String operation,
                                     String url) throws IOException, SplunkException {

        if (operation == "GET" || operation == "DELETE") {
            return prequest(operation, url);
        } else if (operation.toUpperCase() == "POST") {
            throw new SplunkException("POST request requires at least the args parameter");
        } else {
            throw new SplunkException("Request operation must be GET, POST or DELETE");
        }
    }

    /**
     * request from splunk
     * @param operation        GET, DELETE or POST
     * @param url  partial or fully qualified URL to GET from
     * @param args  un-encoded HashMap key/value pair arguments to POST
     * @throws IOException     percolates IOException from lower level HTTP access
     * @throws SplunkException Splunk contextual exceptions
     */
    public HttpURLConnection request(String operation,
                                     String url,
                                     HashMap<String, Object> args) throws IOException, SplunkException {

        operation = operation.toUpperCase();

        if (operation == "POST" || operation == "DELETE" || operation == "POST") {
            return prequest(operation, url, args);
        } else {
            throw new SplunkException("Request operation must be GET, POST or DELETE");
        }
    }

    /**
     * request from splunk
     * @param operation        GET, DELETE or POST
     * @param url  partial or fully qualified URL to GET from
     * @param query standard query arguments encoded into URL
     * @param args  un-encoded HashMap key/value pair arguments to POST
     * @throws IOException     percolates IOException from lower level HTTP access
     * @throws SplunkException Splunk contextual exceptions
     */
    public HttpURLConnection request(String operation,
                                     String url,
                                     HashMap<String, Object> query,
                                     HashMap<String, Object> args) throws IOException, SplunkException {
        operation = operation.toUpperCase();

        if (operation == "POST") {
            return prequest(operation, url, query, args);
        } else {
            if (operation == "GET" || operation == "DELETE") {
                throw new SplunkException("GET or DELETE does not take a query parameter");
            } else {
                throw new SplunkException("Request operation must be GET, POST or DELETE");
            }
        }
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
            context.initSplunkContext();
        } catch (SplunkException e) {
            System.out.println("WARNING: could not initialize splunk splunkContext: " + e);
            throw e;
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
    public void login(String host,
                      String port,
                      String username,
                      String password,
                      String scheme) throws IOException, SplunkException {

        // seed Context with passed in values
        context.setContextValue("host", host);
        context.setContextValue("port", port);
        context.setContextValue("username", username);
        context.setContextValue("password", password);
        context.setContextValue("scheme", scheme);

        commonLogin();
    }
}