/*
 * Copyright 2013 Splunk, Inc.
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

package com.splunk.modularinput;

import org.w3c.dom.Document;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.logging.Level;

/**
 * The {@code EventWriter} class encapsulates writing events and error messages to Splunk from a modular input.
 *
 * Its two important methods are {@code writeEvent}, which takes an {@code Event} object, and log, which takes a severity
 * and an error message.
 */
public class EventWriter {
    private XMLStreamWriter outputStreamWriter;
    private Writer rawOutputStreamWriter;
    private Writer errorStreamWriter;

    private boolean headerWritten = false;

    private boolean hadIOException = false;

    // The severities that Splunk understands for log messages from modular inputs.
    public static String DEBUG = "DEBUG";
    public static String INFO = "INFO";
    public static String WARN = "WARN";
    public static String ERROR = "ERROR";
    public static String FATAL = "FATAL";

    /**
     * Wrap a UTF-8 OutputStreamWriter around an OutputStream, chomping the error that the Java spec asserts
     * cannot actually happen.
     *
     * @param stream an OutputStream to wrap.
     * @return a Writer object wrapping the stream.
     */
    private static Writer wrapWriter(OutputStream stream) {
        try {
            return new OutputStreamWriter(stream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("Somehow your system doesn't support UTF-8");
        }
    }

    public EventWriter() throws XMLStreamException {
        this(wrapWriter(System.out), wrapWriter(System.err));
    }

    public EventWriter(Writer outputWriter, Writer errorWriter) throws XMLStreamException {
        this.rawOutputStreamWriter = outputWriter;
        this.outputStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(outputWriter);
        this.errorStreamWriter = errorWriter;
    }

    /**
     * Clears the error state on this {@code EventWriter}.
     *
     * {@code EventWriter} does not throw {@code IOException} errors, but does not ignore them entirely either. Instead it operates
     * the same way as {@code PrintStream} in the standard library. You can always check if an {@code IOException} has been thrown
     * by calling {@code checkError}.
     */
    protected void clearError() {
        hadIOException = false;
    }

    /**
     * Declares that this {@code EventWriter} had an {@code IOException}.
     *
     * {@code EventWriter} does not throw {@code IOException} errors, but does not ignore them entirely either. Instead it operates
     * the same way as {@code PrintStream} in the standard library. You can always check if an {@code IOException} has been thrown
     * by calling {@code checkError}.
     */
    protected void setError() {
        hadIOException = true;
    }

    /**
     * Returns whether there was an {@code IOException} on this EventWriter.
     *
     * {@code EventWriter} does not throw {@code IOException} errors, but does not ignore them entirely either. Instead it operates
     * the same way as {@code PrintStream} in the standard library. You can always check if an {@code IOException} has been thrown
     * by calling {@code checkError}.
     */
    public boolean checkError() {
        return hadIOException;
    }

    /**
     * Thread safe version of {@code writeEvent}.
     * @see #writeEvent
     */
    public synchronized void synchronizedWriteEvent(Event event) throws MalformedDataException {
        writeEvent(event);
    }

    /**
     * Writes an {@code Event} object to Splunk. This method is not thread safe. If you need a thread safe version, use
     * {@code synchronizedWriteEvent}.
     *
     * If you try to write an {@code Event} with null data, throws {@code MalformedDataException}.
     *
     * @see #synchronizedWriteEvent
     * @param event The {@code Event} object to write.
     * @throws MalformedDataException
     */
    public void writeEvent(Event event) throws MalformedDataException {
        try {
            if (!headerWritten) {
                outputStreamWriter.writeStartElement("stream");
                headerWritten = true;
            }
            event.writeTo(outputStreamWriter);
            outputStreamWriter.flush();
        } catch (MalformedDataException e) {
            log(WARN, e.toString());
            throw e;
        } catch (XMLStreamException e) {
            log(ERROR, e.toString());
        }
    }

    /**
     * Thread safe version of {@code log}.
     * @see #log
     */
    public synchronized void synchronizedLog(String severity, String errorMessage) {
        log(severity, errorMessage);
    }

    /**
     * Logs messages about the state of this modular input to Splunk. These messages will show up in Splunk's
     * internal logs.
     * 
     * @see #synchronizedLog
     * @param severity The severity (e.g., {@code EventWriter.WARN}, {@code EventWriter.FATAL}) of this message.
     * @param errorMessage The message that should appear in the logs.
     */
    public void log(String severity, String errorMessage) {
        try {
            errorStreamWriter.write(severity + " " + errorMessage + "\n");
            errorStreamWriter.flush();
        } catch (IOException e) {
            setError();
        }
    }

    /**
     * Writes the closing {@code </stream>} tag to make this XML well formed.
     *
     */
    public void close() {
        try {
            this.outputStreamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            log(ERROR, e.toString());
        }
    }

    /**
     * Writes an org.w3c.dom.Document object containing XML to the output stream.
     */
    void writeXmlDocument(Document document) throws TransformerException, XMLStreamException, IOException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        DOMSource source = new DOMSource(document);
        StringWriter buffer = new StringWriter();
        StreamResult result = new StreamResult(buffer);
        transformer.transform(source, result);
        rawOutputStreamWriter.write(result.getWriter().toString());
        rawOutputStreamWriter.flush();
    }

}
