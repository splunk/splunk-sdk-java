package com.splunk.modularinput;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

/**
 * EventWriter encapsulates writing events and error messages to Splunk from a modular input.
 *
 * Its two important methods are writeEvent, which takes an Event object, and log, which takes a severity
 * and an error message. Both methods are synchronized, so they can be used from parallel threads.
 */
public class EventWriter {
    private XMLStreamWriter outputStream;
    private OutputStreamWriter errorStream;

    public EventWriter() throws XMLStreamException {
        this(System.out, System.err);
    }

    public EventWriter(OutputStream outputStream, OutputStream errorStream) throws XMLStreamException {
        this.outputStream = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream);
        try {
            this.errorStream = new OutputStreamWriter(errorStream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("Somehow your system doesn't support UTF-8");
        }

        this.outputStream.writeStartElement("stream");
    }

    public synchronized void writeEvent(Event event) throws MalformedDataException, XMLStreamException {
        try {
            event.writeOn(outputStream);
        } catch (MalformedDataException e) {
            try {
                log(Level.WARNING, e.toString());
            } catch (IOException ioe) {}
            throw e;
        }
    }

    public synchronized void log(Level severity, String errorMessage) throws IOException {
        errorStream.write(severity.toString() + " " + errorMessage + "\n");
        errorStream.flush();
    }

    public void close() throws XMLStreamException {
        this.outputStream.writeEndElement();
    }


}
