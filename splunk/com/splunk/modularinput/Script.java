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
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamException;
import java.io.*;

/**
 * The {@code Script} class is an abstract base class for implementing modular inputs. Subclasses
 * should override {@code getScheme} and {@code streamEvents}, and optional {@code configureValidator} if the modular
 * input is using external validation.
 */
public abstract class Script {
    /**
     * Encodes all the common behavior of modular inputs. You should have no reason
     * to override this method in most cases.
     *
     * @param args An array of command line arguments passed to this script.
     * @return An integer to be used as the exit value of this program.
     */
    public int run(String[] args) {
        EventWriter eventWriter;
        try {
            eventWriter = new EventWriter();
            return run(args, eventWriter, System.in);
        } catch (XMLStreamException e) {
            System.err.print(stackTraceToLogEntry(e));
            return 1;
        }
    }

    /**
     * Encodes all the common behavior of modular inputs. You should have no reason
     * to override this method in most cases.
     *
     * @param args An array of command line arguments passed to this script.
     * @param eventWriter An {@code EventWriter}.
     * @param in An {@code InputStream}.
     * @return An integer to be used as the exit value of this program.
     */
    public int run(String[] args, EventWriter eventWriter, InputStream in) {
        try {
            if (args.length == 0) {
                // This script is running as an input. Input definitions will be passed on stdin as XML, and
                // the script will write events on stdout and log entries on stderr.
                InputDefinition inputDefinition = InputDefinition.parseDefinition(in);
                streamEvents(inputDefinition, eventWriter);
                eventWriter.close();
                return 0;
            } else if (args[0].toLowerCase().equals("--scheme")) {
                // Splunk has requested XML specifying the scheme for this modular input. Return it and exit.
                Scheme scheme = getScheme();
                if (scheme == null) {
                    eventWriter.log("FATAL", "Modular input script returned a null scheme.");
                    return 1;
                } else {
                    eventWriter.writeXmlDocument(scheme.toXml());
                    return 0;
                }
            } else if (args[0].toLowerCase().equals("--validate-arguments")) {
                NonblockingInputStream stream = new NonblockingInputStream(in);
                ValidationDefinition validationDefinition = ValidationDefinition.parseDefinition(stream);

                try {
                    validateInput(validationDefinition);
                    return 0;
                } catch (Exception e) {
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    documentBuilderFactory.setIgnoringElementContentWhitespace(true);
                    DocumentBuilder documentBuilder;
                    documentBuilder = documentBuilderFactory.newDocumentBuilder();

                    Document document = documentBuilder.newDocument();

                    Node error = document.createElement("error");
                    document.appendChild(error);

                    Node message = document.createElement("message");
                    error.appendChild(message);

                    Node text = document.createTextNode(e.getLocalizedMessage());
                    message.appendChild(text);

                    eventWriter.writeXmlDocument(document);

                    return 1;
                }
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("ERROR Invalid arguments to modular input script:");
                for (String arg : args) {
                    sb.append(" ");
                    sb.append(arg);
                }
                eventWriter.log(EventWriter.ERROR, sb.toString());
                return 1;
            }
        } catch (Exception e) {
            eventWriter.log(EventWriter.ERROR, stackTraceToLogEntry(e));
            return 1;
        }
    }

    protected String stackTraceToLogEntry(Exception e) {
        // Concatenate all the lines of the exception's stack trace with \\ between them.
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement s : e.getStackTrace()) {
            sb.append(s.toString());
            sb.append("\\\\");
        }
        return sb.toString();
    }

    /**
     * Gets the scheme that defines the parameters understood by this modular input.
     *
     * @return A {@code Scheme} object representing the parameters for this modular input.
     */
    public abstract Scheme getScheme();

    /**
     * Handles external validation for modular input kinds. When Splunk
     * called a modular input script in validation mode, it will pass in an XML document
     * giving information about the Splunk instance (so you can call back into it if needed)
     * and the name and parameters of the proposed input.
     *
     * If this function does not throw an exception, the validation is assumed to succeed. Otherwise
     * any error thrown will be turned into a string and logged back to Splunk.
     *
     * The default implementation always passes.
     *
     * @param definition The parameters for the proposed input passed by splunkd.
     */
    public void validateInput(ValidationDefinition definition) throws Exception {}

    /**
     * Streams events into Splunk. It should do all of its output via
     * {@code EventWriter} rather than assuming that there is a console attached.
     *
     * @param ew An object with methods to write events and log messages to Splunk.
     */
    public abstract void streamEvents(InputDefinition inputs, EventWriter ew)
            throws MalformedDataException, XMLStreamException, IOException;
}
