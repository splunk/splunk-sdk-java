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

package com.splunk;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

/**
 * Representation of named index, and unnamed index receivers.
 */
public class Receiver extends Entity {

    /**
     * Class Constructor.
     *
     * @param service The connected service instance.
     */
    Receiver(Service service) {
        // we don't need a relative endpoint path, because all accesses
        // are targeting explicit endpoints in-line.
        super(service, "");
    }

    /**
     * Creates a writable socket to this index.
     *
     * @return The Socket.
     * @throws IOException
     */
    public Socket attach() throws IOException {
        Socket socket = service.open();
        OutputStream ostream = socket.getOutputStream();
        Writer out = new OutputStreamWriter(ostream, "UTF8");
        String header = String.format(
            "POST /services/receivers/stream HTTP/1.1\r\n" +
            "Host: %s:%d\r\n" +
            "Accept-Encoding: identity\r\n" +
            "Authorization: %s\r\n" +
            "X-Splunk-Input-Mode: Streaming\r\n\r\n",
            service.getHost(), service.getPort(),
            service.token);
        out.write(header);
        out.flush();
        return socket;
    }

    /**
     * Creates a writable socket to this index.
     *
     * @param indexName The index to write to.
     * @return The Socket.
     * @throws IOException
     */
    public Socket attach(String indexName) throws IOException {
        Socket socket = service.open();
        OutputStream ostream = socket.getOutputStream();
        Writer out = new OutputStreamWriter(ostream, "UTF8");
        String header = String.format(
            "POST /services/receivers/stream?index=%s HTTP/1.1\r\n" +
            "Host: %s:%d\r\n" +
            "Accept-Encoding: identity\r\n" +
            "Authorization: %s\r\n" +
            "X-Splunk-Input-Mode: Streaming\r\n\r\n",
            indexName,
            service.getHost(), service.getPort(),
            service.token);
        out.write(header);
        out.flush();
        return socket;
    }

    /**
     * Creates a writable socket to this index.
     *
     * @param args The optional arguments to the streaming endpoint.
     * @return The Socket.
     * @throws IOException
     */
    public Socket attach(Args args) throws IOException {
        Socket socket = service.open();
        OutputStream ostream = socket.getOutputStream();
        Writer out = new OutputStreamWriter(ostream, "UTF8");
        String header = String.format(
            "POST /services/receivers/stream?%s HTTP/1.1\r\n" +
            "Host: %s:%d\r\n" +
            "Accept-Encoding: identity\r\n" +
            "Authorization: %s\r\n" +
            "X-Splunk-Input-Mode: Streaming\r\n\r\n",
            args.encode(),
            service.getHost(), service.getPort(),
            service.token);
        out.write(header);
        out.flush();
        return socket;
    }

    /**
     * Creates a writable socket to this index.
     *
     * @param indexName The index to write to.
     * @param args The optional arguments to the streaming endpoint.
     * @return The Socket.
     * @throws IOException
     */
    public Socket attach(String indexName, Args args) throws IOException {
        Socket socket = service.open();
        OutputStream ostream = socket.getOutputStream();
        Writer out = new OutputStreamWriter(ostream, "UTF8");
        String header = String.format(
            "POST /services/receivers/stream?index=%s?%s HTTP/1.1\r\n" +
            "Host: %s:%d\r\n" +
            "Accept-Encoding: identity\r\n" +
            "Authorization: %s\r\n" +
            "X-Splunk-Input-Mode: Streaming\r\n\r\n",
            indexName,
            args.encode(),
            service.getHost(), service.getPort(),
            service.token);
        out.write(header);
        out.flush();
        return socket;
    }

    /**
     * Submits an event to this index through HTTP POST.
     *
     * @param data Event data posted.
     */
    public void submit(String data) {
        RequestMessage request = new RequestMessage("POST");
        request.setContent(data);
        service.send("receivers/simple?", request);
    }

    /**
     * Submits an event to this index through HTTP POST.
     *
     * @param indexName The index to write to.
     * @param data Event data posted.
     */
    public void submit(String indexName, String data) {
        RequestMessage request = new RequestMessage("POST");
        request.setContent(data);
        service.send("receivers/simple?index=" + indexName, request);
    }

    /**
     * Submits an event to this index through HTTP POST.
     *
     * @param data Event data posted.
     * @param args optional arguments for the simple receivers endpoint.
     */
    public void submit(String data, Args args) {
        RequestMessage request = new RequestMessage("POST");
        request.setContent(data);
        String argString = String.format("%s", args.encode());
        service.send("receivers/simple?" + argString, request);
    }

    /**
     * Submits an event to this index through HTTP POST.
     *
     * @param indexName The index to write to.
     * @param data Event data posted.
     * @param args optional arguments for the simple receivers endpoint.
     */
    public void submit(String indexName, String data, Args args) {
        RequestMessage request = new RequestMessage("POST");
        request.setContent(data);
        String argString = String.format(
             "index=%s&%s", indexName, args.encode());
        service.send(
             "receivers/simple?" + argString, request);
    }
}