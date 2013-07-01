package com.splunk.modularinput;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Date;

/**
 * Event represents an event or fragment of an event to be written by this modular input to Splunk.
 *
 * To write an Event to an XML stream, call its writeTo method with an XMLStreamWriter object to write to.
 * The Event must have at least the data field set or writeTo will throw a MalformedDataException. All other
 * fields are optional. If you omit the time field, the writeTo method will fill in the current time when it is called.
 *
 * Typically, you will also want to call setStanza to specify which instance of the modular input kind this event
 * should go to, setTime to set the timestamp, and setSource, setHost, and setSourceType specify where this event
 * came from.
 */
public class Event {
    protected Date time = null;
    protected String data;
    protected String source = null;
    protected String sourceType = null;
    protected String index = null;
    protected String host = null;
    protected boolean done = true;
    protected boolean unbroken = true;
    protected String stanza = null;

    public Event() {}

    // Helper method to write a single field to an XMLStreamWriter object only if it is not null.
    protected void writeFieldTo(XMLStreamWriter out, String name, String value) throws XMLStreamException {
        if (value != null) {
            out.writeStartElement(name);
            out.writeCharacters(value);
            out.writeEndElement();
        }
    }

    /**
     * Write this event to the given XMLStreamWriter.
     *
     * @param out The XMLStreamWriter to append to.
     * @throws XMLStreamException if there is a problem in the XMLStreamWriter.
     * @throws MalformedDataException if you have not specified data for this event.
     */
    public void writeTo(XMLStreamWriter out) throws XMLStreamException, MalformedDataException {
        if (data == null) {
            throw new MalformedDataException("Events must have at least the data field set to be written to XML.");
        }

        out.writeStartElement("event");
        if (getStanza() != null) {
            out.writeAttribute("stanza", getStanza());
        }
        out.writeAttribute("unbroken", isUnbroken() ? "1" : "0");

        long epoch_time;
        if (this.time == null) {
            epoch_time = System.currentTimeMillis();
        } else {
            epoch_time = this.time.getTime();
        }

        writeFieldTo(out, "time", String.format("%.3f", epoch_time / 1000D));
        writeFieldTo(out, "source", getSource());
        writeFieldTo(out, "sourceType", getSourceType());
        writeFieldTo(out, "index", getIndex());
        writeFieldTo(out, "host", getHost());
        writeFieldTo(out, "data", getData());

        if (!isUnbroken() && isDone()) {
            out.writeStartElement("done");
            out.writeEndElement();
        }

        out.writeEndElement();
        out.flush();
    }

    /**
     * Time is a java.util.Date object giving the timestamp that should be sent with this event. If this field is null,
     * Splunk will assign the time at which the event is indexed as its timestamp.
     *
     * @return a java.util.Date object giving the time assigned to this Event, or null if Splunk should apply a default
     *         timestamp.
     */
    public Date getTime() {
        return this.time;
    }

    /**
     * Time is a java.util.Date object giving the timestamp that should be sent with this event. If this field is null,
     * Splunk will assign the time at which the event is indexed as its timestamp.
     *
     * @param time The java.util.Date which should be used as this event's timestamp, or null to have Splunk use a
     *             default timestamp.
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * Data is the text of the event that Splunk should index.
     *
     * @return a String containing the event text.
     */
    public String getData() {
        return this.data;
    }

    /**
     * Data is the text of the event that Splunk should index.
     *
     * @param data a String containing the event text.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Source is the file, service, or other producer that this Event comes from. For lines in log files, it is
     * typically the full path to the log file. If it is omitted, Splunk will guess a sensible name for the source.
     *
     * @return a String giving the source of this event, or null to have Splunk guess.
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Source is the file, service, or other producer that this Event comes from. For lines in log files, it is
     * typically the full path to the log file. If it is omitted, Splunk will guess a sensible name for the source.
     *
     * @param source a String to be used as the source of this event, or null to have Splunk guess.
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Source type is a classification of this event. For example, all different web server logs might be assigned
     * the same source type, or different source types can be assigned to distinct classes of events that all have
     * the same source. If it is omitted, Splunk will guess a sensible name for the source type.
     *
     * @return the source type currently set on this event, or null if Splunk is to guess a source.
     */
    public String getSourceType() {
        return this.sourceType;
    }

    /**
     * Source type is a classification of this event. For example, all different web server logs might be assigned
     * the same source type, or different source types can be assigned to distinct classes of events that all have
     * the same source. If this field is omitted, Splunk will make a guess as to the source type.
     *
     * @param sourceType a String to use as the source type for this event, or null to have Splunk guess.
     */
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * The index field specifies which index Splunk should write this event to. If it is omitted, Splunk has a default
     * index where events will be written.
     *
     * @return the index this event is specified to write to, or null if it will be written to the default index.
     */
    public String getIndex() {
        return this.index;
    }

    /**
     * The index field specifies which index Splunk should write this event to. If it is omitted, Splunk has a default
     * index where events will be written.
     *
     * @param index the name of the index to write to, or null to have Splunk write to the default index.
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * Host specifies the name of the network host on which this event was produced. If it is omitted, Splunk will use
     * the host from which it directly received the event.
     *
     * @return a String giving the host name of the event source, or null to use the host Splunk receives the event from.
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Host specifies the name of the network host on which this event was produced. If it is omitted, Splunk will use
     * the host from which it directly received the event.
     *
     * @param host a String giving the host name of the event source, or null to use the host Splunk receives
     *             the event from.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Splunk allows events from modular inputs to be sent in pieces. For example, if lines of an event become available
     * one at a time, they can be sent (in events with setUnbroken(false) called on them) as hunks. At the end of the
     * event, you must manually tell Splunk to break after this hunk by setting done to true. Then the next event
     * received will be taken to be part of another event.
     *
     * By default, done is true and unbroken is true, so if you do not touch these fields, you will send one complete
     * event per Event object.
     *
     * @param done Is this the last hunk of an event broken across multiple Event objects?
     */
    public void setDone(boolean done) {
        this.done = done;
    }

    /**
     * Splunk allows events from modular inputs to be sent in pieces. For example, if lines of an event become available
     * one at a time, they can be sent (in events with setUnbroken(false) called on them) as hunks. At the end of the
     * event, you must manually tell Splunk to break after this hunk by setting done to true. Then the next event
     * received will be taken to be part of another event.
     *
     * By default, done is true and unbroken is true, so if you do not touch these fields, you will send one complete
     * event per Event object.
     *
     * @return Is this the last hunk of an event broken across multiple Event objects?
     */
    public boolean isDone() {
        return this.done;
    }

    /**
     * Splunk allows events from modular inputs to be sent in pieces. If unbroken is set to true, then this event is
     * assumed to be a complete event, not a piece of one. By default, unbroken is true. If you set unbroken to false,
     * you need to call setDone(true) on the last hunk of the complete event for Splunk to know to terminate it.
     *
     * @param unbroken Is this event completely encapsulated in this Event object?
     */
    public void setUnbroken(boolean unbroken) {
        this.unbroken = unbroken;
    }

    /**
     * Splunk allows events from modular inputs to be sent in pieces. If unbroken is set to true, then this event is
     * assumed to be a complete event, not a piece of one. By default, unbroken is true. If you set unbroken to false,
     * you need to call setDone(true) on the last hunk of the complete event for Splunk to know to terminate it.
     *
     * @return Is this event completely encapsulated in this Event object?
     */
    public boolean isUnbroken() {
        return this.unbroken;
    }

    /**
     * A single modular input script can feed events to multiple instances of the modular input. In this case, each
     * event must be marked with the name of the input it should be sent to. This is also the name of the configuration
     * stanza that describes that input.
     *
     * @return the name of the input this event should be sent to.
     */
    public String getStanza() {
        return this.stanza;
    }

    /**
     * A single modular input script can feed events to multiple instances of the modular input. In this case, each
     * event must be marked with the name of the input it should be sent to. This is also the name of the configuration
     * stanza that describes that input.
     *
     * @param stanza the name of the input this event should be sent to.
     */
    public void setStanza(String stanza) {
        this.stanza = stanza;
    }


}
