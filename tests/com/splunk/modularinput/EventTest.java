package com.splunk.modularinput;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Level;

/**
 * Checks if Event and EventWriter behavior correctly.
 */
public class EventTest extends ModularInputTestCase {
    /**
     * An event without a data field should throw an error when asked to write itself onto a stream. Be sure
     * that it does.
     */
    @Test
    public void testEventWithoutEnoughFieldsFails() throws XMLStreamException {
        StringWriter sb = new StringWriter();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sb);

        Event event = new Event();

        try {
            event.writeTo(writer);
        } catch (MalformedDataException e) {
            Assert.assertTrue(true);
            return;
        }
        Assert.fail();
    }

    /**
     * Generate XML from an event object with a small number of fields, and see if it matches what we expect.
     */
    @Test
    public void testXmlOfEventWithMinimalConfiguration() throws XMLStreamException, MalformedDataException, TransformerException,
            ParserConfigurationException {
        StringWriter sb = new StringWriter();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sb);

        Event event = new Event();
        event.setTime(new Date(1372187084000L));
        event.setStanza("fubar");
        event.setData("This is a test of the emergency broadcast system.");
        event.writeTo(writer);
        Document found = stringToXmlDocument(sb.toString());

        Document expected = resourceToXmlDocument("modularinput/data/event_minimal.xml");

        assertXmlEqual(expected, found);
    }

    /**
     * Generate XML from an event with all its fields set, and see if it matches what we expect.
     */
    @Test
    public void testXmlOfEventWithMoreConfiguration() throws MalformedDataException, XMLStreamException, TransformerException,
            ParserConfigurationException {
        StringWriter sb = new StringWriter();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sb);

        Event event = new Event();
        event.setTime(new Date(1372274622493L));
        event.setStanza("fubar");
        event.setData("This is a test of the emergency broadcast system.");
        event.setHost("localhost");
        event.setIndex("main");
        event.setSource("hilda");
        event.setSourceType("misc");
        event.setDone(true);
        event.setUnbroken(true);
        event.writeTo(writer);
        Document found = stringToXmlDocument(sb.toString());

        Document expected = resourceToXmlDocument("modularinput/data/event_maximal.xml");

        assertXmlEqual(expected, found);
    }

    /**
     * Write a pair of events with an EventWriter, and ensure that they are being encoded immediately and correctly
     * onto the output stream.
     */
    @Test
    public void testWritingEventsOnEventWriter() throws XMLStreamException, TransformerException,
            ParserConfigurationException, MalformedDataException {
        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();

        EventWriter ew = new EventWriter(out, err);

        Event event = new Event();
        event.setTime(new Date(1372275124466L));
        event.setStanza("fubar");
        event.setData("This is a test of the emergency broadcast system.");
        event.setHost("localhost");
        event.setIndex("main");
        event.setSource("hilda");
        event.setSourceType("misc");
        event.setDone(true);
        event.setUnbroken(true);
        ew.writeEvent(event);

        Document found = stringToXmlDocument(out.toString() + "</stream>");
        Document expected = resourceToXmlDocument("modularinput/data/stream_with_one_event.xml");

        assertXmlEqual(expected, found);
        Assert.assertEquals("", err.toString());

        err.getBuffer().setLength(0);

        ew.writeEvent(event);
        ew.close();

        found = stringToXmlDocument(out.toString());
        expected = resourceToXmlDocument("modularinput/data/stream_with_two_events.xml");
        assertXmlEqual(expected, found);
    }

    /**
     * An event which cannot write itself onto an output stream (such as because it doesn't have a data field set)
     * should write an error to stderr. Check that it does so.
     */
    @Test
    public void testErrorInEventWriter() throws XMLStreamException {
        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();

        EventWriter ew = new EventWriter(out, err);

        Event event = new Event();
        try {
            ew.writeEvent(event);
        } catch (MalformedDataException e) {
            Assert.assertTrue(err.toString().startsWith("WARN"));
            return;
        }
        Assert.fail();
    }

    /**
     * Check that the log method on EventWriter produces the expected error message on the stderr stream.
     */
    @Test
    public void testLoggingErrorsWithEventWriter() throws XMLStreamException, IOException {
        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();

        EventWriter ew = new EventWriter(out, err);

        ew.log(EventWriter.ERROR, "Something happened!");

        Assert.assertEquals("ERROR Something happened!\n", err.toString());
    }

    /**
     * Check that EventWriter.writeXmlDocument writes sensible XML to the output stream.
     */
    @Test
    public void testWriteXmlDocumentIsSane() throws XMLStreamException, IOException, TransformerException, ParserConfigurationException {
        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();

        EventWriter ew = new EventWriter(out, err);

        Document expectedXml = resourceToXmlDocument("modularinput/data/event_maximal.xml");

        ew.writeXmlDocument(expectedXml);
        Document foundXml = stringToXmlDocument(out.toString());

        assertXmlEqual(expectedXml, foundXml);
    }

}
