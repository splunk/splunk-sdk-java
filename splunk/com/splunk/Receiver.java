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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

/**
 * The {@code Receiver} class represents a named index and unnamed index 
 * receivers.
 */
public class Receiver {

    Service service = null;

    /**
     * Class constructor.
     *
     * @param service The connected {@code Service} instance.
     */
    Receiver(Service service) {
        this.service = service;
    }

    /**
     * Creates a writable socket to this index.
     *
     * @return The socket.
     * @throws IOException
     */
    public Socket attach() throws IOException {
        return attach(null, null);
    }

    /**
     * Creates a writable socket to this index.
     *
     * @param indexName The index to write to.
     * @return The socket.
     * @throws IOException
     */
    public Socket attach(String indexName) throws IOException {
        return attach(indexName, null);
    }

    /**
     * Creates a writable socket to this index.
     *
     * @param args Optional arguments for this stream. Valid parameters are: 
     * "host", "host_regex", "source", and "sourcetype".
     * @return The socket.
     * @throws IOException
     */
    public Socket attach(Args args) throws IOException {
        return attach(null, args);
    }

    /**
     * Creates a writable socket to this index.
     *
     * @param indexName The index to write to.
     * @param args Optional arguments for this stream. Valid parameters are: 
     * "host", "host_regex", "source", and "sourcetype".
     * @return The socket.
     * @throws IOException
     */
    public Socket attach(String indexName, Args args) throws IOException {
        Socket socket = service.open();
        OutputStream ostream = socket.getOutputStream();
        Writer out = new OutputStreamWriter(ostream, "UTF-8");
        String postUrl = "POST /services/receivers/stream";
        if (indexName != null) {
            postUrl = postUrl + "?index=" + indexName;
        }
        if (args != null && args.size() > 0) {
            postUrl = postUrl +  ((indexName == null) ? "?" : "&");
            postUrl = postUrl + args.encode();
        }
        String header = String.format(
            "%s HTTP/1.1\r\n" +
            "Host: %s:%d\r\n" +
            "Accept-Encoding: identity\r\n" +
            "Authorization: %s\r\n" +
            "X-Splunk-Input-Mode: Streaming\r\n\r\n",
            postUrl,
            service.getHost(), service.getPort(),
            service.getToken());
        out.write(header);
        out.flush();
        return socket;
    }

    /**
     * Submits an event to this index through HTTP POST.
     *
     * @param data A string containing event data.
     */
    public void submit(String data) {
        submit(null, null, data);
    }

    /**
     * Submits an event to this index through HTTP POST.
     *
     * @param indexName The index to write to.
     * @param data A string containing event data.
     */
    public void submit(String indexName, String data) {
        submit(indexName, null, data);
    }

    /**
     * Submits an event to this index through HTTP POST.
     *
     * @param data A string containing event data.
     * @param args Optional arguments for this stream. Valid parameters are: 
     * "host", "host_regex", "source", and "sourcetype".
     */
    public void submit(Args args, String data) {
        submit(null, args, data);
    }

    /**
     * Logs an event to this index through HTTP POST.
     *
     * @param indexName The index to write to.
     * @param data A string containing event data.
     * @param args Optional arguments for this stream. Valid parameters are: 
     * "host", "host_regex", "source", and "sourcetype".
     */
    public void submit(String indexName, Args args, String data) {
        String sendString = "";
        RequestMessage request = new RequestMessage("POST");
        request.setContent(data);
        if (indexName !=null) {
            sendString = String.format("?index=%s", indexName);
        }
        if (args != null && args.size() > 0) {
            sendString = sendString +  ((indexName == null) ? "?" : "&");
            sendString = sendString + args.encode();
        }
        ResponseMessage response = service.send(service.simpleReceiverEndPoint
                + sendString, request);
        try {
            response.getContent().close();
        } catch (IOException e) {
            // noop
        }
    }

    /**
     * Submits an event to this index through HTTP POST. This method is an alias
     * for {@code submit()}.
     *
     * @param data A string containing event data.
     */
    public void log(String data) {
        submit(data);
    }

    /**
     * Submits an event to this index through HTTP POST. This method is an alias
     * for {@code submit()}.
     *
     * @param indexName The index to write to.
     * @param data A string containing event data.
     */
    public void log(String indexName, String data) {
        submit(indexName, data);
    }

    /**
     * Submits an event to this index through HTTP POST. This method is an alias
     * for {@code submit()}.
     *
     * @param args Optional arguments for this stream. Valid parameters are: 
     * "host", "host_regex", "source", and "sourcetype".
     * @param data A string containing event data.
     */
    public void log(Args args, String data) {
        submit(args, data);
    }

    /**
     * Logs an event to this index through HTTP POST. This method is an alias
     * for {@code submit()}.
     *
     * @param indexName The index to write to.
     * @param args Optional arguments for this stream. Valid parameters are: 
     * "host", "host_regex", "source", and "sourcetype".
     * @param data A string containing event data.
     */
    public void log(String indexName, Args args, String data) {
        submit(indexName, args, data);
    }
}