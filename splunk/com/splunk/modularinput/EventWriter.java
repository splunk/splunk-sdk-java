package com.splunk.modularinput;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.logging.Level;

/**
 * EventWriter encapsulates writing events and error messages to Splunk from a modular input.
 *
 * Its two important methods are writeEvent, which takes an Event object, and log, which takes a severity
 * and an error message.
 */
public class EventWriter {
    private XMLStreamWriter outputStreamWriter;
    private Writer errorStreamWriter;

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
        this.outputStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(outputWriter);
        this.errorStreamWriter = errorWriter;

        this.outputStreamWriter.writeStartElement("stream");
    }

    /**
     * Thread safe version of writeEvent.
     */
    public synchronized void synchronizedWriteEvent(Event event) throws MalformedDataException, XMLStreamException {
        writeEvent(event);
    }

    /**
     * Write an Event object to Splunk. This method is not thread safe. If you need a thread safe version, use
     * synchronizedWriteEvent.
     *
     * @param event The Event object to write.
     * @throws MalformedDataException
     * @throws XMLStreamException
     */
    public void writeEvent(Event event) throws MalformedDataException, XMLStreamException {
        try {
            event.writeTo(outputStreamWriter);
        } catch (MalformedDataException e) {
            try {
                log(Level.WARNING, e.toString());
            } catch (IOException ioe) { /* If we reach this, there's nothing good to do. */ }
            throw e;
        }
    }

    /**
     * Thread safe version of log.
     */
    public synchronized void synchronizedLog(Level severity, String errorMessage) throws IOException {
        log(severity, errorMessage);
    }

    /**
     * Log messages about the state of this modular input to Splunk. These messages will show up in Splunk's
     * internal logs.
     * @param severity The severity (e.g., Level.WARNING, Level.SEVERE) of this message.
     * @param errorMessage The message that should appear in the logs.
     * @throws IOException
     */
    public void log(Level severity, String errorMessage) throws IOException {
        errorStreamWriter.write(severity.toString() + " " + errorMessage + "\n");
        errorStreamWriter.flush();
    }

    /**
     * Write the closing </stream> tag to make this XML well formed.
     *
     * @throws XMLStreamException
     */
    public void close() throws XMLStreamException {
        this.outputStreamWriter.writeEndElement();
    }


}
