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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Date;

/**
 * The {@code Event} class represents an event or fragment of an event to be written by this modular input to Splunk.
 *
 * To write an {@code Event} to an XML stream, call its {@code writeTo} method with an {@code XMLStreamWriter} object to write to.
 * The {@code Event} must have at least the data field set or {@code writeTo} will throw a {@code MalformedDataException}. All other
 * fields are optional. If you omit the time field, the {@code writeTo} method will fill in the current time when it is called.
 *
 * Typically, you will also want to call {@code setStanza} to specify which instance of the modular input kind this event
 * should go to, {@code setTime} to set the timestamp, and {@code setSource}, {@code setHost}, and {@code setSourceType}
 * specify where this event came from.
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
     * Writes this event to the given {@code XMLStreamWriter}.
     *
     * @param out The {@code XMLStreamWriter} to append to.
     * @throws XMLStreamException if there is a problem in the {@code XMLStreamWriter}.
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

        if (this.time != null) {
            writeFieldTo(out, "time", String.format("%.3f", time.getTime() / 1000D));
        }

        writeFieldTo(out, "source", getSource());
        writeFieldTo(out, "sourcetype", getSourceType());
        writeFieldTo(out, "index", getIndex());
        writeFieldTo(out, "host", getHost());
        writeFieldTo(out, "data", getData());

        if (isDone()) {
            out.writeStartElement("done");
            out.writeEndElement();
        }

        out.writeEndElement();
        out.writeCharacters("\r\n");
        out.flush();
    }

    /**
     * Gets a {@code java.util.Date} object giving the timestamp that should be sent with this event. If this field is null,
     * Splunk will assign the time at which the event is indexed as its timestamp.
     *
     * @return A {@code java.util.Date} object giving the time assigned to this Event, or null if Splunk should apply a default
     *         timestamp.
     */
    public Date getTime() {
        return this.time;
    }

    /**
     * Sets a {@code java.util.Date} object giving the timestamp that should be sent with this event. If this field is null,
     * Splunk will assign the time at which the event is indexed as its timestamp.
     *
     * @param time The {@code java.util.Date} which should be used as this event's timestamp, or null to have Splunk use a
     *             default timestamp.
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * Gets the text of the event that Splunk should index.
     *
     * @return A String containing the event text.
     */
    public String getData() {
        return this.data;
    }

    /**
     * Sets the text of the event that Splunk should index.
     *
     * @param data A String containing the event text.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets the file, service, or other producer that this {@code Event} comes from. For lines in log files, it is
     * typically the full path to the log file. If it is omitted, Splunk will guess a sensible name for the source.
     *
     * @return A String giving the source of this event, or null to have Splunk guess.
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Sets the file, service, or other producer that this {@code Event} comes from. For lines in log files, it is
     * typically the full path to the log file. If it is omitted, Splunk will guess a sensible name for the source.
     *
     * @param source A String to be used as the source of this event, or null to have Splunk guess.
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets a classification of this event. For example, all different web server logs might be assigned
     * the same source type, or different source types can be assigned to distinct classes of events that all have
     * the same source. If it is omitted, Splunk will guess a sensible name for the source type.
     *
     * @return The source type currently set on this event, or null if Splunk is to guess a source.
     */
    public String getSourceType() {
        return this.sourceType;
    }

    /**
     * Sets a classification of this event. For example, all different web server logs might be assigned
     * the same source type, or different source types can be assigned to distinct classes of events that all have
     * the same source. If this field is omitted, Splunk will make a guess as to the source type.
     *
     * @param sourceType A String to use as the source type for this event, or null to have Splunk guess.
     */
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * Gets an index field specifying which index Splunk should write this event to. If it is omitted, Splunk has a default
     * index where events will be written.
     *
     * @return The index this event is specified to write to, or null if it will be written to the default index.
     */
    public String getIndex() {
        return this.index;
    }

    /**
     * Sets an index field specifying which index Splunk should write this event to. If it is omitted, Splunk has a default
     * index where events will be written.
     *
     * @param index The name of the index to write to, or null to have Splunk write to the default index.
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * Gets a host specifying the name of the network host on which this event was produced. If it is omitted, Splunk will use
     * the host from which it directly received the event.
     *
     * @return A String giving the host name of the event source, or null to use the host Splunk receives the event from.
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Sets a host specifying the name of the network host on which this event was produced. If it is omitted, Splunk will use
     * the host from which it directly received the event.
     *
     * @param host A String giving the host name of the event source, or null to use the host Splunk receives
     *             the event from.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets a value indicating whether this is the last piece of an event broken across multiple {@code Event} objects.
     * 
     * Splunk allows events from modular inputs to be sent in pieces. For example, if lines of an event become available
     * one at a time, they can be sent (in events with {@code setUnbroken(false)} called on them) as hunks. At the end of the
     * event, you must manually tell Splunk to break after this hunk by setting done to true. Then the next event
     * received will be taken to be part of another event.
     *
     * By default, done is {@code true} and unbroken is {@code true}, so if you do not touch these fields, you will send one complete
     * event per {@code Event} object.
     *
     * @param done Is this the last hunk of an event broken across multiple {@code Event} objects?
     */
    public void setDone(boolean done) {
        this.done = done;
    }

    /**
     * Gets a value indicating whether this is the last piece of an event broken across multiple {@code Event} objects.
     * 
     * Splunk allows events from modular inputs to be sent in pieces. For example, if lines of an event become available
     * one at a time, they can be sent (in events with setUnbroken(false) called on them) as hunks. At the end of the
     * event, you must manually tell Splunk to break after this hunk by setting done to true. Then the next event
     * received will be taken to be part of another event.
     *
     * By default, done is {@code true} and unbroken is {@code true}, so if you do not touch these fields, you will send one complete
     * event per {@code Event} object.
     *
     * @return Is this the last hunk of an event broken across multiple {@code Event} objects?
     */
    public boolean isDone() {
        return this.done;
    }

    /**
     * Sets a value indicating whether this event is completely encapsulated in this {@code Event} object.
     * 
     * Splunk allows events from modular inputs to be sent in pieces. If unbroken is set to {@code true}, then this event is
     * assumed to be a complete event, not a piece of one. By default, unbroken is {@code true}. If you set unbroken to {@code false},
     * you need to call {@code setDone(true)} on the last hunk of the complete event for Splunk to know to terminate it.
     *
     * @param unbroken Is this event completely encapsulated in this {@code Event} object?
     */
    public void setUnbroken(boolean unbroken) {
        this.unbroken = unbroken;
    }

    /**
     * Gets a value indicating whether this event is completely encapsulated in this {@code Event} object.
     * 
     * Splunk allows events from modular inputs to be sent in pieces. If unbroken is set to {@code true}, then this event is
     * assumed to be a complete event, not a piece of one. By default, unbroken is {@code true}. If you set unbroken to {@code false},
     * you need to call {@code setDone(true)} on the last hunk of the complete event for Splunk to know to terminate it.
     *
     * @return Is this event completely encapsulated in this {@code Event} object?
     */
    public boolean isUnbroken() {
        return this.unbroken;
    }

    /**
     * Gets the name of the input this event should be sent to.
     * 
     * A single modular input script can feed events to multiple instances of the modular input. In this case, each
     * event must be marked with the name of the input it should be sent to. This is also the name of the configuration
     * stanza that describes that input.
     *
     * @return The name of the input this event should be sent to.
     */
    public String getStanza() {
        return this.stanza;
    }

    /**
     * Sets the name of the input this event should be sent to.
     * 
     * A single modular input script can feed events to multiple instances of the modular input. In this case, each
     * event must be marked with the name of the input it should be sent to. This is also the name of the configuration
     * stanza that describes that input.
     *
     * @param stanza The name of the input this event should be sent to.
     */
    public void setStanza(String stanza) {
        this.stanza = stanza;
    }


}
